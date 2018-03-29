package com.ibeiliao.deployment.admin.service.deploy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ibeiliao.deployment.admin.dao.deploy.DeployHistoryDao;
import com.ibeiliao.deployment.admin.dao.deploy.ServerDeployHistoryDao;
import com.ibeiliao.deployment.admin.enums.ServerStrategy;
import com.ibeiliao.deployment.admin.po.deploy.DeployHistoryPO;
import com.ibeiliao.deployment.admin.po.deploy.ServerDeployHistoryPO;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.deploy.ModuleStatusManagementService;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ModuleJvmService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.service.server.DeployLogService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.utils.RepoUtil;
import com.ibeiliao.deployment.admin.utils.ValidatorUtil;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.deploy.DeploymentOrder;
import com.ibeiliao.deployment.admin.vo.deploy.ServerDeployHistory;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.project.ModuleJvm;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatProjectResult;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.RepositoryConstants;
import com.ibeiliao.deployment.common.enums.*;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.compile.service.JavaCompiler;
import com.ibeiliao.deployment.compile.service.StaticFileCompiler;
import com.ibeiliao.deployment.compile.vo.CompileRequest;
import com.ibeiliao.deployment.compile.vo.CompileResult;
import com.ibeiliao.deployment.log.LogMsgUtil;
import com.ibeiliao.deployment.log.PushLogService;
import com.ibeiliao.deployment.storage.FileStorageUtil;
import com.ibeiliao.deployment.storage.ProjectFileStorage;
import com.ibeiliao.deployment.storage.ProjectFileStorageFactory;
import com.ibeiliao.deployment.transfer.service.JavaTransferService;
import com.ibeiliao.deployment.transfer.service.StaticTransferService;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import com.ibeiliao.deployment.transfer.vo.TransferResult;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 发布上线单的业务逻辑实现
 *
 * @author linyi 2017/1/17
 * @version 1.0
 */
@Service
public class DeployHistoryServiceImpl implements DeployHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(DeployHistoryServiceImpl.class);

    @Autowired
    private DeployHistoryDao deployHistoryDao;

    @Autowired
    private ServerDeployHistoryDao serverDeployHistoryDao;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ProjectAccountRelationService projectAccountRelationService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerGroupService serverGroupService;

    @Autowired
    private ModuleStatusManagementService moduleStatusManagementService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private PushLogService pushLogService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private ModuleJvmService moduleJvmService;

    @Autowired
    private DeployLogService deployLogService;

    @Override
    @Transactional
    public void createDeploymentOrder(DeploymentOrder order) {
        validateParameterAndModule(order);
        // 检查服务器是否存在 & 环境是否为 envId
        List<Server> servers = checkServerStatus(order);
        checkProjectAuthorization(order);
        checkIfDeployingModuleWithEnv(order);
        logger.info("创建上线单 | order: " + JSON.toJSONString(order));

        ProjectModule module = projectModuleService.getByModuleId(order.getModuleId());
        DeployHistoryPO deployHistory = new DeployHistoryPO();
        VOUtil.copy(order, deployHistory);
        deployHistory.setCreateTime(new Date());
        deployHistory.setProjectId(module.getProjectId());
        deployHistory.setModuleName(module.getModuleName());
        deployHistory.setResult(DeployResult.NONE.getValue());
        // 如果模块需要审核，进入审核，否则进入发布阶段
        if (needAudit(module, deployHistory)) {
            deployHistory.setDeployStatus(DeployStatus.WAITING_FOR_AUDIT.getValue());
        } else {
            deployHistory.setDeployStatus(DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        }

        deployHistory.setSuccessCount(0);
        deployHistory.setDeployServers(order.getServerId().length);
        if (!deployHistory.getTagName().startsWith("/")) {
            deployHistory.setTagName("/" + deployHistory.getTagName());
        }
        // 判断是不是回滚的请求
        if (deployHistory.getRollbackToDeployId() > 0) {
            deployHistory.setIsRollback(Constants.TRUE);
        }
        deployHistoryDao.insert(deployHistory);

        saveDeployServers(deployHistory, order, servers);
    }

    private boolean needAudit(ProjectModule module, DeployHistoryPO deployHistory) {
        // 1. 凡是生产环境的 都要审核
        if (projectEnvService.isOnline(deployHistory.getEnvId())) {
            return true;
        }
        // 2. 测试环境的，而且被设置了需要审核，那也要审核
        if (module.getNeedAudit() == Constants.TRUE) {
            ProjectEnv env = projectEnvService.getById(deployHistory.getEnvId());
            return "test".equalsIgnoreCase(env.getEnvName());
        }
        return false;
    }

    @Override
    public void createRollbackOrder(int historyId, long accountId, boolean isRollBack) {
        DeployHistory deployHistory = getByHistoryId(historyId);
        if (deployHistory == null) {
            throw new IllegalArgumentException("发布记录不存在");
        }
        AdminAccount account = adminAccountService.getById(accountId);
        DeploymentOrder order = new DeploymentOrder();
        VOUtil.copy(deployHistory, order);
        order.setAccountId(accountId);
        order.setRealName(account.getRealname());
        List<ServerDeployHistory> serverDeployHistories = deployHistory.getServerDeployHistories();
        int[] serverId = new int[serverDeployHistories.size()];
        for (int i = 0; i < serverId.length; i++) {
            serverId[i] = serverDeployHistories.get(i).getServerId();
        }
        order.setServerId(serverId);
        order.setRollbackToDeployId(historyId);
        if (isRollBack) {
            order.setTitle("回滚到" + historyId + "的版本");
        } else {
            order.setTitle("重发" + historyId + "的版本");
        }
        createDeploymentOrder(order);
    }

    @Override
    public DeployHistory createRestartHistory(long accountId, int serverId) {
        DeployHistoryPO po = createAndSaveRestart(accountId, "重启服务", serverId);
        return VOUtil.from(po, DeployHistory.class);
    }

    @Override
    public DeployHistory createStopHistory(long accountId, int serverId) {
        DeployHistoryPO po = createAndSaveRestart(accountId, "停止服务", serverId);
        return VOUtil.from(po, DeployHistory.class);
    }

    @Override
    @Transactional
    public void finishStopRestart(int historyId, DeployResult deployResult) {
        boolean success = (deployResult == DeployResult.SUCCESS);
        int successCount = (success ? 1 : 0);
//        deployHistoryDao.updateStatus(historyId, DeployStatus.DEPLOYED.getValue(), DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
//        deployHistoryDao.updateResult(historyId, deployResult.getValue(), 1, successCount);
        deployHistoryDao.updateResultAndStatus(historyId, deployResult.getValue(), 1, successCount, DeployStatus.DEPLOYED.getValue(), DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        serverDeployHistoryDao.updateAllStatus(historyId, (success ? ServerDeployResult.SUCCESS.getValue() : ServerDeployResult.FAILURE.getValue()), new Date());
        logger.info("完成stop/restart任务 | historyId: {}", historyId);
    }

    @Override
    public List<DeployHistory> queryDeployHistory(long accountId, int envId, int projectId,int moduleId, int page, int pageSize) {
        if (page < 0) page = 1;
        if (pageSize < 1 || pageSize > 1000) pageSize = 20;
        int startIndex = (page - 1) * pageSize;
        long t1 = System.currentTimeMillis();
        boolean isSuperAdmin = adminAccountService.isSuperAdmin(accountId);
        List<DeployHistoryPO> poList = null;
        if (isSuperAdmin) {
            poList = deployHistoryDao.query(envId, projectId, moduleId, startIndex, pageSize);
        } else {
            poList = deployHistoryDao.queryByAccountId(accountId, envId, projectId, moduleId, startIndex, pageSize);
        }
        List<DeployHistory> result = convert(poList, accountId, isSuperAdmin);

        long t2 = System.currentTimeMillis();
        logger.info("queryDeployHistory costTime: " + (t2 - t1) + ", size: " + result.size());
        return result;
    }

    @Override
    public List<DeployHistory> queryDeployHistory(int envId, int moduleId, int page, int pageSize) {
        if (page < 0) page = 1;
        if (pageSize < 1 || pageSize > 1000) pageSize = 20;
        int startIndex = (page - 1) * pageSize;
        List<DeployHistoryPO> poList = deployHistoryDao.queryByModuleId(envId, moduleId, startIndex, pageSize);
        return VOUtil.fromList(poList, DeployHistory.class);
    }

    @Override
    public DeployHistory getByHistoryId(int historyId) {
        DeployHistoryPO po = deployHistoryDao.get(historyId);
        if (po == null) {
            return null;
        }
        List<ServerDeployHistoryPO> servers = serverDeployHistoryDao.getByHistoryId(historyId);
        DeployHistory history = new DeployHistory();
        VOUtil.copy(po, history);

        List<ServerDeployHistory> deployHistoryList = VOUtil.fromList(servers, ServerDeployHistory.class);
        fillLogIntoServerDeploy(deployHistoryList);
        history.setServerDeployHistories(deployHistoryList);

        fillNames(history);
        return history;
    }

    private void fillLogIntoServerDeploy(List<ServerDeployHistory> deployHistoryList) {
        List<Integer> serverDeployIds = Lists.newArrayList();
        for (ServerDeployHistory history : deployHistoryList) {
            serverDeployIds.add(history.getId());
        }
        List<ServerDeployLog> deployLogs = deployLogService.getByServerDeployHistoryIds(serverDeployIds);
        ArrayListMultimap<Integer, ServerDeployLog> serverDeployId2DeployLogsMap = ArrayListMultimap.create();
        for (ServerDeployLog deployLog : deployLogs) {
            serverDeployId2DeployLogsMap.put(deployLog.getServerDeployId(), deployLog);
        }

        for (ServerDeployHistory deployHistory : deployHistoryList) {
            if (serverDeployId2DeployLogsMap.get(deployHistory.getId()) != null) {
                deployHistory.setServerDeployLogs(serverDeployId2DeployLogsMap.get(deployHistory.getId()));
            }
        }
    }

    @Override
    public void audit(long accountId, int historyId) {
        DeployHistoryPO po = readAndGetPermission(accountId, historyId);
        if (po.getDeployStatus() != DeployStatus.WAITING_FOR_AUDIT.getValue()) {
            throw new IllegalStateException("项目状态已改变，请先刷新页面");
        }
        int rows = deployHistoryDao.auditDeploy(historyId, DeployStatus.WAITING_FOR_DEPLOYMENT.getValue(),
                po.getDeployStatus(), accountId, new Date());
        logger.info("审核发布记录成功 | accountId: {}, historyId: {}, rows: {}", accountId, historyId, rows);
    }

    @Override
    public void cancel(long accountId, int historyId) {
        DeployHistoryPO po = deployHistoryDao.get(historyId);
        if (po == null) {
            throw new IllegalArgumentException("发布记录不存在，historyId=" + historyId);
        }
        if (accountId != po.getAccountId()) {
            throw new IllegalArgumentException("你没有权限取消发布");
        }
        if (po.getDeployStatus() != DeployStatus.WAITING_FOR_AUDIT.getValue() && po.getDeployStatus() != DeployStatus.WAITING_FOR_DEPLOYMENT.getValue()) {
            throw new IllegalStateException("项目状态已改变，请先刷新页面");
        }
        int rows = deployHistoryDao.auditDeploy(historyId, DeployStatus.CANCELLED.getValue(),
                po.getDeployStatus(), accountId, new Date());
        logger.info("取消发布记录成功 | accountId: {}, historyId: {}, rows: {}", accountId, historyId, rows);
    }

    @Override
    public void reject(long accountId, int historyId) {
        DeployHistoryPO po = readAndGetPermission(accountId, historyId);
        if (po.getDeployStatus() != DeployStatus.WAITING_FOR_AUDIT.getValue()) {
            throw new IllegalStateException("项目状态已改变，请先刷新页面");
        }
        int rows = deployHistoryDao.updateResultAndStatus(historyId,
                DeployResult.FAILURE.getValue(), 0, 0, DeployStatus.AUDIT_REJECTED.getValue(), po.getDeployStatus());

        logger.info("取消发布记录成功 | accountId: {}, historyId: {}, rows: {}", accountId, historyId, rows);

    }

    @Override
    public DeployHistory getByServerDeployHistoryId(int serverDeployHistoryId) {
        ServerDeployHistoryPO po = serverDeployHistoryDao.get(serverDeployHistoryId);
        DeployHistory history = null;
        if (po != null) {
            DeployHistoryPO deployPo = deployHistoryDao.get(po.getHistoryId());
            history = new DeployHistory();
            VOUtil.copy(deployPo, history);
        }
        return history;
    }

    @Override
    public void startDeploy(int historyId, long accountId) {
        DeployHistoryPO historyPO = deployHistoryDao.get(historyId);
        Assert.notNull(historyPO, "没有找到该发布记录");
        // 1. 判断状态
        if (historyPO.getDeployStatus() != DeployStatus.WAITING_FOR_DEPLOYMENT.getValue()) {
            throw new IllegalStateException("发布记录状态错误，请刷新界面");
        }
        // 2. 判断权限
        if (historyPO.getAccountId() != accountId) {
            throw new IllegalArgumentException("只有创建者可以发布这个上线单");
        }
        // 3. 进行发布
        if (moduleStatusManagementService.isModuleDeploying(historyPO.getModuleId(), historyPO.getEnvId())) {
            throw new IllegalStateException("该模块正在发布中，请等待上一次发布完成");
        }

        threadPoolTaskExecutor.execute(new DeployTask(historyPO));
        logger.info("开始发布 .... historyId: " + historyId);
    }

    @Override
    public List<Project> getProjectByServerDeployIds(List<Integer> serverDeployIdList) {
        if (!CollectionUtils.isEmpty(serverDeployIdList)) {
            Set<Integer> projectIdSet = new HashSet<>();

            for (Integer serverDeployId : serverDeployIdList) {
                ServerDeployHistoryPO po = serverDeployHistoryDao.get(serverDeployId);
                if (po != null) {
                    DeployHistoryPO historyPO = deployHistoryDao.get(po.getHistoryId());
                    projectIdSet.add(historyPO.getProjectId());
                }

            }
            if (!CollectionUtils.isEmpty(projectIdSet)) {
                List<Project> projects = new ArrayList<>();
                for (Integer id : projectIdSet) {
                    Project project = projectService.getProject(id);
                    projects.add(project);
                }
                return projects;
            }

        }
        return Collections.emptyList();
    }

    @Override
    public List<DeployHistory> queryUnfinished(Date startTime, Date endTime, int size) {
        Assert.notNull(startTime, "开始时间不能为null");
        Assert.notNull(endTime, "结束时间不能为null");
        Assert.isTrue(size > 0 && size <= 1000, "size在[1,1000]之间");
        List<DeployHistoryPO> list = deployHistoryDao.queryByTimeAndStatus(startTime, endTime, DeployStatus.DEPLOYING.getValue(), 0, size);
        return VOUtil.fromList(list, DeployHistory.class);
    }

    @Override
    public void systemCancel(int historyId) {
        DeployHistoryPO po = deployHistoryDao.get(historyId);
        Date now = new Date();
        if (po != null && po.getDeployStatus() == DeployStatus.DEPLOYING.getValue()
                && (now.getTime() - po.getCreateTime().getTime()) / 1000 > Constants.DEPLOY_TASK_TIMEOUT) {
            int rows = deployHistoryDao.auditDeploy(historyId, DeployStatus.CANCELLED.getValue(),
                    po.getDeployStatus(), Constants.SYSTEM_ACCOUNT_ID, now);
            logger.info("系统取消发布记录成功 | historyId: {}, rows: {}", historyId, rows);
            // 这里要 end ...
            moduleStatusManagementService.endModuleDeploy(po.getModuleId(), po.getEnvId());
        }
    }

    @Override
    public List<StatProjectResult> statProject(Date startTime, Date endTime) {
        return deployHistoryDao.statProject(startTime, endTime, DeployStatus.DEPLOYED.getValue());
    }

    @Override
    public List<LowQualityRank> statLowQualityModule(@Param("startTime") Date startTime, @Param("endTime") Date endTime) {
        List<ProjectEnv> envs = projectEnvService.findAllEnv();
        List<Integer> onlineEnvs = new ArrayList<>(envs.size());
        for (ProjectEnv env : envs) {
            if (env.getOnlineFlag() == Constants.TRUE) {
                onlineEnvs.add(env.getEnvId());
            }
        }
        if (onlineEnvs.isEmpty()) {
            return Collections.emptyList();
        }
        String env = StringUtils.join(onlineEnvs, ",");
        List<LowQualityRank> ranks = deployHistoryDao.statLowQualityModule(startTime,
                endTime, env, DeployStatus.DEPLOYED.getValue(),
                DeployResult.SUCCESS.getValue(), Constants.LOW_QUALITY_DEPLOY_TIMES);
        return ranks;
    }

    private DeployHistoryPO readAndGetPermission(long accountId, int historyId) {
        DeployHistoryPO po = deployHistoryDao.get(historyId);
        if (po == null) {
            throw new IllegalArgumentException("发布记录不存在，historyId=" + historyId);
        }
        if (!projectAccountRelationService.canModify(accountId, po.getProjectId())) {
            throw new IllegalArgumentException("你不是项目管理员或系统超级管理员，没有权限操作");
        }
        return po;
    }

    private DeployHistoryPO createAndSaveRestart(long accountId, String title, int serverId) {
        Server server = serverService.getById(serverId);
        AdminAccount account = adminAccountService.getById(accountId);
        Assert.notNull(server, "服务器不存在");
        Assert.notNull(account, "管理员不存在");
        ServerGroup serverGroup = serverGroupService.getById(server.getGroupId());
        ProjectModule module = projectModuleService.getByModuleId(serverGroup.getModuleId());
        DeployHistoryPO po = new DeployHistoryPO();
        po.setTagName("");
        po.setAccountId(accountId);
        po.setAuditorId(0);
        po.setTitle(title);
        po.setCreateTime(new Date());
        po.setDeployStatus(DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        po.setAuditTime(po.getCreateTime());
        po.setDeployServers(1);
        po.setProjectId(module.getProjectId());
        po.setModuleId(module.getModuleId());
        po.setModuleName(module.getModuleName());
        po.setVersionNo("");
        po.setIsRestart(Constants.TRUE);
        po.setEnvId(serverGroup.getEnvId());
        po.setRealName(account.getRealname());

        deployHistoryDao.insert(po);

        ServerDeployHistoryPO serverDeployHistoryPO = new ServerDeployHistoryPO();
        serverDeployHistoryPO.setHistoryId(po.getHistoryId());
        serverDeployHistoryPO.setServerId(serverId);
        serverDeployHistoryPO.setServerName(server.getServerName());
        serverDeployHistoryPO.setServerIp(server.getIp());
        serverDeployHistoryPO.setDeployStatus(ServerDeployResult.WAITING_FOR_DEPLOYMENT.getValue());
        serverDeployHistoryDao.insert(serverDeployHistoryPO);
        return deployHistoryDao.get(po.getHistoryId());
    }

    private List<DeployHistory> convert(List<DeployHistoryPO> poList, long accountId, boolean isSuperAdmin) {
        List<DeployHistory> result = VOUtil.fromList(poList, DeployHistory.class);
        for (DeployHistory history : result) {
            fillNames(history);
            if (history.getDeployStatus() == DeployStatus.WAITING_FOR_AUDIT.getValue()) {
                // 设置审核状态
                history.setCanAudit(projectAccountRelationService.canModify(accountId, history.getProjectId()));
            }
        }
        return result;
    }

    /**
     * 校验参数，成功返回当前发布的环境id
     *
     * @param order
     * @return envId
     */
    private void validateParameterAndModule(DeploymentOrder order) {
        // 校验参数
        String message = ValidatorUtil.validate(order);
        if (StringUtils.isNotEmpty(message)) {
            throw new IllegalArgumentException(message);
        }
        Assert.hasText(order.getRealName(), "发布者姓名不能为空");
        ProjectEnv env = projectEnvService.getById(order.getEnvId());
        Assert.notNull(env, "环境不存在, envId: " + order.getEnvId());
        if (env.getOnlineFlag() == Constants.TRUE && !order.getTagName().contains(RepositoryConstants.TAGS)) {
            throw new IllegalArgumentException("生产环境 " + env.getEnvName() + " 只能发布 tag，请选择一个 tag 创建上线单");
        }
        // 检查项目是否存在 & online
        checkProjectStatus(order.getProjectId());
        // 检查模块是否存在
        checkModuleStatus(order.getModuleId());

    }

    private void checkProjectStatus(int projectId) {
        Project project = projectService.getProject(projectId);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在，projectId=" + projectId);
        }
    }

    private void checkModuleStatus(int moduleId) {
        ProjectModule module = projectModuleService.getByModuleId(moduleId);
        if (module == null) {
            throw new IllegalArgumentException("模块不存在，moduleId=" + moduleId);
        }
        if (module.getModuleType() != ModuleType.STATIC.getValue()) {
            Assert.hasText(module.getRestartShell(), "重启脚本为空，请先完善模块配置");
        }
        Assert.hasText(module.getCompileShell(), "编译脚本为空，请先完善模块配置");
        Assert.hasText(module.getSvnAccount(), "svn/git帐号为空，请先完善模块配置");
        Assert.hasText(module.getSvnPassword(), "svn/git密码为空，请先完善模块配置");
    }

    private void checkProjectAuthorization(DeploymentOrder order) {
        // 检查用户对此项目是否有权限
        if (!projectAccountRelationService.hasRelation(order.getAccountId(), order.getProjectId())) {
            throw new IllegalStateException("你没有权限发布此项目");
        }
    }

    private void checkIfDeployingModuleWithEnv(DeploymentOrder order) {
        // 如果 module 正在 env 发布，不能再创建发布
        if (moduleStatusManagementService.isModuleDeploying(order.getModuleId(), order.getEnvId())) {
            ProjectModule module = projectModuleService.getByModuleId(order.getModuleId());
            ProjectEnv env = projectEnvService.getById(order.getEnvId());
            throw new IllegalStateException("模块 [" + module.getModuleName() + "] 正在 [" + env.getEnvName() + "] 环境发布，必须等待当前的发布结束才可以发布");
        }
    }

    private List<Server> checkServerStatus(DeploymentOrder order) {
        if (order.getServerId() == null || order.getServerId().length == 0) {
            throw new IllegalArgumentException("服务器列表不能为空");
        }
        Map<Integer, Integer> serverGroupEnvMap = new HashMap<>();
        List<Server> servers = serverService.getByIds(order.getServerId());
        if (servers.size() != order.getServerId().length) {
            throw new IllegalStateException("serverId不存在");
        }
        for (Server server : servers) {
            if (!serverGroupEnvMap.containsKey(server.getGroupId())) {
                ServerGroup serverGroup = serverGroupService.getById(server.getGroupId());
                if (serverGroup == null) {
                    throw new IllegalArgumentException("服务器组不存在，serverId=" + server.getServerId() + ", groupId=" + server.getGroupId());
                }
                if (serverGroup.getEnvId() != order.getEnvId()) {
                    throw new IllegalArgumentException("服务器组【" + serverGroup.getGroupName() + "】所在的环境不一致");
                }
                serverGroupEnvMap.put(serverGroup.getGroupId(), serverGroup.getEnvId());
            }
        }
        return servers;
    }

    private void saveDeployServers(DeployHistoryPO po, DeploymentOrder order, List<Server> servers) {
        Map<Integer, Server> serverMap = new HashMap<>(servers.size() * 4 / 3);
        for (Server server : servers) {
            serverMap.put(server.getServerId(), server);
        }
        for (int serverId : order.getServerId()) {
            ServerDeployHistoryPO serverDeployHistoryPO = new ServerDeployHistoryPO();
            serverDeployHistoryPO.setHistoryId(po.getHistoryId());
            serverDeployHistoryPO.setServerId(serverId);
            Server server = serverMap.get(serverId);
            serverDeployHistoryPO.setServerName(server.getServerName());
            serverDeployHistoryPO.setServerIp(server.getIp());
            serverDeployHistoryPO.setDeployStatus(ServerDeployResult.WAITING_FOR_DEPLOYMENT.getValue());
            serverDeployHistoryDao.insert(serverDeployHistoryPO);
        }
    }

    private void fillNames(DeployHistory history) {
        ProjectEnv env = projectEnvService.getById(history.getEnvId());
        if (env != null) {
            history.setEnvName(env.getEnvName());
        }
        Project project = projectService.getProject(history.getProjectId());
        if (project != null) {
            history.setProjectName(project.getProjectName());
        }
    }

    /**
     * 执行发布任务
     */
    public class DeployTask implements Runnable {

        private DeployHistoryPO deployHistory;

        /**
         * 编译过程产生的文件保存在 OSS 的位置
         */
        private String saveFileName = "";

        private String envName;

        private int total = 0;

        private int success = 0;

        public DeployTask(DeployHistoryPO deployHistory) {
            this.deployHistory = deployHistory;
        }

        @Override
        public void run() {
            int moduleId = deployHistory.getModuleId();
            logger.info("开始发布模块 ... moduleId: " + moduleId);
            envName = projectEnvService.getById(deployHistory.getEnvId()).getEnvName();

            long startTime = System.currentTimeMillis();
            writeStep("开始发布模块");
            pushLogService.clear(deployHistory.getHistoryId());
            boolean startSuccess = moduleStatusManagementService.startModuleDeploy(moduleId, deployHistory.getEnvId(), deployHistory.getHistoryId());
            if (!startSuccess) {
                logger.error("发布模块失败，已经有一个DeployTask在发布, historyId: {}, moduleId: {}, envId: {}", deployHistory.getHistoryId(), moduleId, deployHistory.getEnvId());
                return;
            }

            try {
                int rows = deployHistoryDao.updateStatus(deployHistory.getHistoryId(), DeployStatus.DEPLOYING.getValue(), deployHistory.getDeployStatus());
                if (rows > 0) {
                    ProjectModule module = projectModuleService.getByModuleId(deployHistory.getModuleId());
                    Project project = projectService.getProject(deployHistory.getProjectId());

                    boolean compileSuccess = true;

                    if (needCompile() && (!isRollBack(deployHistory))) {
                        compileSuccess = compileModule(module, project);
                    }
                    if (compileSuccess) {
                        transferAndDeploy(module, project);
                    }

                    short result = getDeployResult();
                    rows = deployHistoryDao.updateResultAndStatus(deployHistory.getHistoryId(), result, total, success, DeployStatus.DEPLOYED.getValue(), DeployStatus.DEPLOYING.getValue());

                    if (result == DeployResult.SUCCESS.getValue() && module.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
                        projectModuleService.setResinConfCreated(moduleId);
                    }
                    logger.info("发布结束, 修改发布记录状态和结果, rows: {}, total: {}, success: {}", rows, total, success);

                    writeStep("模块发布完成，总耗时 " + (System.currentTimeMillis() - startTime + " ms"));

                } else {
                    logger.error("修改发布记录状态失败, historyId: " + deployHistory.getHistoryId());
                    writeStep("修改发布记录状态失败，终止发布 ...");
                }

            } catch (Exception e) {
                logger.error("发布模块错误, moduleId: " + moduleId, e);
                int rows = deployHistoryDao.updateResultAndStatus(deployHistory.getHistoryId(), DeployResult.FAILURE.getValue(), total, success, DeployStatus.DEPLOYED.getValue(), DeployStatus.DEPLOYING.getValue());
                logger.info("historyId: {}, 修改deploy result为FAILURE: {}", deployHistory.getHistoryId(), rows);
                writeStep(LogMsgUtil.getFailMsg("发布异常: " + e.getMessage()));
            } finally {
                moduleStatusManagementService.endModuleDeploy(moduleId, deployHistory.getEnvId());
            }

            long costTime = System.currentTimeMillis() - startTime;
            logger.info("结束模块部署 ... moduleId: " + moduleId + ", costTime: " + costTime);
        }

        private boolean isRollBack(DeployHistoryPO deployHistory) {
            return deployHistory.getIsRollback() == 1;
        }

        private void transferAndDeploy(ProjectModule module, Project project) throws Exception {
            int deployedServers = 0;
            List<ServerDeployHistoryPO> serverDeployHistoryList = serverDeployHistoryDao.getByHistoryId(deployHistory.getHistoryId());
            total = serverDeployHistoryList.size();
            Map<String, Integer> ipToIdMap = Maps.newHashMapWithExpectedSize(total);
            writeStep("发布服务器总数量：" + total);
            success = 0;
            TransferRequest transferRequest = createTransferRequest(module, project);
            long sleepTime = deployHistory.getDeployTimeInterval() * 1000L;

            Set<String> successServerIps = Sets.newHashSet();
            Set<String> failServerIps = Sets.newHashSet();
            Set<String> notDeployServerIps = Sets.newHashSet();
            for (ServerDeployHistoryPO historyPO : serverDeployHistoryList) {
                notDeployServerIps.add(historyPO.getServerIp());
            }

            while (deployedServers < total) {
                int num = calcNum(total, deployedServers);
                if (num < 1) {
                    break;
                }

                List<String> serverIps = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    ServerDeployHistoryPO po = serverDeployHistoryList.get(deployedServers + i);
                    serverIps.add(po.getServerIp());
                    ipToIdMap.put(po.getServerIp(), po.getId());
                }

                String serverIpStr = StringUtils.join(serverIps, ",");
                logger.info("开始发布服务器: {}", serverIpStr);
                boolean sleep = true;

                try {
                    writeStep("开始发布服务器: " + serverIpStr);
                    long startTime = System.currentTimeMillis();
                    transferRequest.setTargetServerIps(serverIps);
                    TransferResult transferResult = doTransfer(transferRequest);

                    for (Map.Entry<String, Boolean> entry : transferResult.getIp2ResultMap().entrySet()) {
                        if (entry.getValue()) {
                            successServerIps.add(entry.getKey());
                        } else {
                            failServerIps.add(entry.getKey());
                        }
                        notDeployServerIps.remove(entry.getKey());
                    }

                    // 遇到错误立即 终止
                    if (deployHistory.getServerStrategy() == ServerStrategy.STOP_WITH_ERR.getValue()) {
                        if (transferResult.getSuccessType() != DeployResult.SUCCESS) {
                            terminateDeploy(transferResult);
                            break;
                        }
                    }

                    logger.info("发布服务器结果: {}, 耗时: {}", transferResult.getSuccessType(), System.currentTimeMillis() - startTime);
                    int tmpSuccess = dealTransferResult(ipToIdMap, transferResult, serverIps);
                    sleep = (tmpSuccess > 0);
                    success += tmpSuccess;
                } catch (Exception e) {
                    logger.error("发布服务器出错, servers: {}", serverIpStr, e);
                    writeStep("发布失败：" + e.getMessage());
                    sleep = false;
                }

                deployedServers += num;

                if (deployedServers < total && sleep) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        logger.warn("发布线程终止, historyId: {}, total: {}, deployedServers: {}, success: {}",
                                deployHistory.getHistoryId(), total, deployedServers, success, e);
                        break;
                    }
                }
            }
            logger.info("发布完成");

            pushFinalMsg2Client(successServerIps, failServerIps, notDeployServerIps);
            //pushResultToClient(total, deployedServers, success);
        }

        private void pushFinalMsg2Client(Set<String> successServerIps, Set<String> failServerIps, Set<String> notDeployServerIps) {
            String allInfo = "汇总：<br/>";
            String successInfo = "<span class='text-green'>发布成功的服务器, 数量：" + successServerIps.size() + "</span><br/>";
            allInfo += successInfo;

            if (CollectionUtils.isNotEmpty(failServerIps)) {
                String failInfo = "<span class='text-danger'>发布失败的服务器, 数量：" + failServerIps.size() + "</span><br/>";
                allInfo += failInfo;
            } else {
                String failInfo = "发布失败的服务器, 数量：" + failServerIps.size() + "<br/>";
                allInfo += failInfo;
            }

            String notDeployInfo = "还没发布的服务器, 数量：" + notDeployServerIps.size() + "<br/>";
            allInfo += notDeployInfo;

            writeStep(allInfo);

        }


        private void terminateDeploy(TransferResult transferResult) {
            // 发布通过的打印出来
            Map<String, Boolean> ip2ResultMap = transferResult.getIp2ResultMap();
            StringBuilder successInfo = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : ip2ResultMap.entrySet()) {
                if (entry.getValue()) {
                    successInfo.append("<span class='text-red'>" + entry.getKey() + "发布失败</span><br/>");
                }
            }

            Map<String, String> failLogMap = transferResult.getIp2FailLogMap();
            StringBuilder failInfo = new StringBuilder("<span class='text-danger'>");
            for (Map.Entry<String, String> entry : failLogMap.entrySet()) {
                failInfo.append("ip:" + entry.getKey()).append(" 信息：").append(entry.getValue()).append("<br/>");
            }
            failInfo.append("</span>");
            writeStep(successInfo.toString() + failInfo.toString() + "<br/>发布终止");
        }

        private TransferResult doTransfer(TransferRequest request) {
            if (request.getModuleType() == ModuleType.STATIC.getValue()) {
                StaticTransferService staticTransferService = new StaticTransferService(request);
                return staticTransferService.pushPackageToServer();
            } else {
                logger.info("开始回滚");
                JavaTransferService javaTransferService = new JavaTransferService(request);
                return javaTransferService.pushPackageToServer();
            }
        }

        /**
         * 处理发布服务器结果并更新 DB
         *
         * @param transferResult TransferResult
         * @return 返回成功的服务器数量
         */
        private int dealTransferResult(Map<String, Integer> ipToIdMap, TransferResult transferResult, List<String> serverIps) {
            // transferResult 包含每个服务器的结果，更新到DB
            int success = 0;

            Map<String, Boolean> ipResultMap = transferResult.getIp2ResultMap();
            Map<String, String> ip2ReasonMap = transferResult.getIp2FailLogMap();
            Date startupTime = new Date();
            for (Map.Entry<String, Boolean> entry : ipResultMap.entrySet()) {
                String ip = entry.getKey();
                short result = ServerDeployResult.SUCCESS.getValue();
                if (entry.getValue()) {
                    success++;
                    writeStep(LogMsgUtil.getSuccMsg(ip + "发布成功"));
                } else {
                    writeStep(LogMsgUtil.getFailMsg(ip + "发布失败：" + ip2ReasonMap.get(ip)));
                    result = ServerDeployResult.FAILURE.getValue();
                }
                Integer id = ipToIdMap.get(ip);
                if (id == null) {
                    String message = "命令执行结果解析错误，错误的IP: " + ip;
                    logger.error(message);
                    writeStep(message);
                } else {
                    serverDeployHistoryDao.updateStatus(id, result, startupTime);
                }
            }

            if (ipResultMap.size() != serverIps.size()) {
                for (String ip : serverIps) {
                    if (!ipResultMap.containsKey(ip)) {
                        String message = ip + "的发布结果没有返回!";
                        logger.error(message);
                        writeStep(LogMsgUtil.getFailMsg(message));
                    }
                }
            }

            if (transferResult.getSuccessType() == DeployResult.FAILURE) {
                logger.error("发布失败!");
            }
            return success;
        }

        /**
         * 计算一个批次可以发布的服务器数量，最少是 1 台
         *
         * @param total
         * @param deployedServers
         * @return
         */
        private int calcNum(int total, int deployedServers) {
            int num = Math.round((float) total * deployHistory.getConcurrentServerPercentage() / 100);
            if (num <= 0) {
                num = 1;
            }
            if (deployedServers + num > total) {
                num = total - deployedServers;
            }
            return num;
        }

        private void writeStep(String message) {
            pushLogService.writeStep(deployHistory.getHistoryId(), message);
        }

        /**
         * 推送结果到客户端
         *
         * @param deployedServers
         * @param success
         */
        private void pushResultToClient(int total, int deployedServers, int success) {
            String message = "发布服务器数量<b> " + deployedServers + "</b>，成功数量<b> " + success + "</b>，失败数量 <b>" + (deployedServers - success) + "</b>";
            writeStep(message);
            if (success == 0) {
                writeStep(LogMsgUtil.getFatalMsg("发布失败"));
            }
        }

        private short getDeployResult() {
            short result;
            if (success == 0) {
                result = DeployResult.FAILURE.getValue();
            } else if (success == total) {
                result = DeployResult.SUCCESS.getValue();
            } else {
                result = DeployResult.PARTIAL_SUCCESS.getValue();
            }
            return result;
        }

        private boolean needCompile() {
            boolean doCompile = (deployHistory.getForceCompile() == Constants.TRUE);
            if (!doCompile) {
                // 检查OSS文件是否还存在，如果不在要重新编译
                ProjectFileStorage projectFileStorage = ProjectFileStorageFactory.getInstance();
                String filename = getSaveFileName();
                doCompile = !projectFileStorage.exists(filename);
                logger.info("检测文件{} 是否存在, doCompile: {}", filename, doCompile);
            }
            if (!doCompile) {
                writeStep("编译文件已经存在或不需要编译，跳过编译");
            }
            return doCompile;
        }

        private String getSaveFileName() {
            if (StringUtils.isEmpty(saveFileName)) {
                saveFileName = FileStorageUtil.getSaveFileName(deployHistory.getProjectId(), deployHistory.getModuleId(),
                        deployHistory.getTagName(), envName, deployHistory.getVersionNo());
            }
            return saveFileName;
        }

        private boolean compileModule(ProjectModule module, Project project) {
            long startTime = System.currentTimeMillis();
            CompileRequest compileRequest = createCompileRequest(module, project);
            CompileResult compileResult = doCompile(compileRequest, module);
            logger.info("编译结果: {}, 耗时: {}", compileResult.isCompileSuccess(), System.currentTimeMillis() - startTime);
            saveFileName = compileResult.getSaveFileName();
            return compileResult.isCompileSuccess();
        }

        private CompileResult doCompile(CompileRequest compileRequest, ProjectModule module) {
            if (module.getModuleType() == ModuleType.WEB_PROJECT.getValue() || module.getModuleType() == ModuleType.SERVICE.getValue()) {
                JavaCompiler javaCompileService = new JavaCompiler(compileRequest, pushLogService);
                return javaCompileService.compileModule();
            } else {
                StaticFileCompiler staticCompileService = new StaticFileCompiler(compileRequest, pushLogService);
                return staticCompileService.compileModule();
            }
        }

        private CompileRequest createCompileRequest(ProjectModule module, Project project) {
            CompileRequest compileRequest = new CompileRequest();
            compileRequest.setHistoryId(deployHistory.getHistoryId());

            compileRequest.setCompileShell(module.getCompileShell());
            compileRequest.setModuleName(module.getModuleName());
            compileRequest.setProjectName(project.getProjectNo());
            compileRequest.setEnv(envName);
            compileRequest.setTagName(deployHistory.getTagName());
            compileRequest.setSvnAddr(module.getRepoUrl());

            compileRequest.setSvnUserName(module.getSvnAccount());
            compileRequest.setSvnPassword(module.getSvnPassword());
            compileRequest.setModuleId(module.getModuleId());
            compileRequest.setProjectId(module.getProjectId());
            compileRequest.setVersion(deployHistory.getVersionNo());
            compileRequest.setRepoType(module.getRepoType());

            compileRequest.setForceCompile(deployHistory.getForceCompile());
            compileRequest.setModuleType(module.getModuleType());

            compileRequest.setLanguage(project.getProgramLanguage());
            return compileRequest;
        }

        private TransferRequest createTransferRequest(ProjectModule module, Project project) throws Exception {
            TransferRequest request = new TransferRequest();
            request.setHistoryId(deployHistory.getHistoryId());
            request.setSaveFileName(getSaveFileName());
            request.setModuleName(deployHistory.getModuleName());
            request.setEnv(envName);
            request.setProjectName(project.getProjectNo());
            request.setRestartShell(module.getRestartShell());
            request.setJvmArgs(getModuleJvmArgs(module));
            request.setPreDeployShell(module.getPreShell());
            request.setPostDeployShell(module.getPostShell());
            request.setModuleType(module.getModuleType());
            request.setStopShell(module.getStopShell());
            request.setRollBackDeployId(deployHistory.getRollbackToDeployId());

            request.setLanguage(project.getProgramLanguage());

            if (module.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
                ResinConf resinConf = module.getResinConf();
                // 设置 [当前环境] 的域名
                resinConf.setDomain(ModuleUtil.getDomainForEnv(resinConf.getDomain(), envName));
                resinConf.setAliasDomain(ModuleUtil.getAliasDomainForEnv(resinConf.getAliasDomain(), envName));

                request.setModuleFinalName(readFinalName(module));
                request.setResinConf(resinConf);

                if (!projectModuleService.isResinConfCreated(deployHistory.getModuleId())
                        || resinConf.isCreateEveryTime()) {
                    request.setCreateResinConf(true);
                }
            }
            return request;
        }

        private String readFinalName(ProjectModule module) throws Exception {
            if (module.getRepoType() == ModuleRepoType.SVN.getValue()) {
                String pomUrl = RepoUtil.getPomRepoUrl(module.getRepoUrl(), deployHistory.getTagName(), module.getModuleName());
                return RepoUtil.getFinalNameForSvn(pomUrl, module.getSvnAccount(), module.getSvnPassword());
            }
            return RepoUtil.getFinalNameForGit(module.getModuleName(), module.getRepoUrl(), module.getSvnAccount(), module.getSvnPassword(), deployHistory.getTagName());
        }

        private String getModuleJvmArgs(ProjectModule module) {
            List<ModuleJvm> moduleJvms = moduleJvmService.queryByModuleId(module.getModuleId());
            if (CollectionUtils.isEmpty(moduleJvms)) {
                return "";
            }
            for (ModuleJvm moduleJvm : moduleJvms) {
                if (moduleJvm.getEnvId() == deployHistory.getEnvId()) {
                    return moduleJvm.getJvmArgs();
                }
            }
            return "";
        }
    }
}