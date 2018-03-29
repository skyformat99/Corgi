package com.ibeiliao.deployment.admin.service.project.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ibeiliao.deployment.admin.dao.project.ModuleConfDao;
import com.ibeiliao.deployment.admin.dao.project.ProjectAccountRelationDao;
import com.ibeiliao.deployment.admin.dao.project.ProjectDao;
import com.ibeiliao.deployment.admin.dao.project.ProjectModuleDao;
import com.ibeiliao.deployment.admin.po.project.ModuleConfPO;
import com.ibeiliao.deployment.admin.po.project.ProjectAccountRelationPO;
import com.ibeiliao.deployment.admin.po.project.ProjectModulePO;
import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ModuleJvmService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.project.ModuleJvm;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.common.util.StaticKeyHelper;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 详情 : 项目模块服务
 *
 * @author liangguanglong
 */
@Service
public class ProjectModuleServiceImpl implements ProjectModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectModuleServiceImpl.class);

    @Autowired
    private ProjectModuleDao projectModuleDao;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerGroupService serverGroupService;

    @Autowired
    private ProjectAccountRelationService projectAccountRelationService;

    @Autowired
    private ModuleJvmService moduleJvmService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ModuleConfDao moduleConfDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectAccountRelationDao projectAccountRelationDao;

    @Override
    @Transactional
    public void saveProjectModule(long accountId, ProjectModule projectModule) {
        validateModule(projectModule);

        ProjectModulePO projectModulePO = VOUtil.from(projectModule, ProjectModulePO.class);
        encodeAccountAndPassword(projectModulePO);
        initEmptyValue(projectModulePO);

        if (projectModule.getModuleId() > 0) {
            ProjectModulePO current = projectModuleDao.get(projectModule.getModuleId());
            Assert.notNull(current, "模块不存在");

//            if (projectAccountRelationService.canModify(accountId, projectModule.getProjectId())) {
                projectModuleDao.update(projectModulePO);
//            } else {
//                logger.error("没有权限修改模块 | moduleId: {}, accountId: {}", projectModule.getModuleId(), accountId);
//                throw new IllegalStateException("你没有权限修改此模块，请联系项目管理员或超级管理员");
//            }
        } else {
            projectModulePO.setCreateTime(new Date());
            projectModuleDao.insert(projectModulePO);
            projectModule.setModuleId(projectModulePO.getModuleId());
        }
        saveServerGroupAndServer(projectModule.getServerGroups(), projectModule.getModuleId());
        saveModuleJvms(projectModule, projectModule.getModuleId());
        saveResinConf(projectModule);

        logger.info("修改/新增模块完成 | moduleId: {}, accountId: {}", projectModule.getModuleId(), accountId);
    }

    private void initEmptyValue(ProjectModulePO projectModulePO) {
        projectModulePO.setPreShell(StringUtils.trimToEmpty(projectModulePO.getPreShell()));
        projectModulePO.setPostShell(StringUtils.trimToEmpty(projectModulePO.getPostShell()));
        projectModulePO.setRestartShell(StringUtils.trimToEmpty(projectModulePO.getRestartShell()));
        projectModulePO.setStopShell(StringUtils.trimToEmpty(projectModulePO.getStopShell()));
        projectModulePO.setPreShell(StringUtils.trimToEmpty(projectModulePO.getPreShell()));
        projectModulePO.setSrcPath("");
    }

    private void validateModule(ProjectModule projectModule) {
        Assert.notNull(projectModule, "参数不能为null");
        Assert.isTrue(StringUtils.isNotBlank(projectModule.getModuleNameZh()), "模块名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(projectModule.getModuleName()), "模块名称不能为空");
        if (projectModule.getModuleType() != ModuleType.STATIC.getValue()) {
            Assert.hasText(projectModule.getRestartShell(), "请输入restart shell");
        }
        Assert.hasText(projectModule.getCompileShell(), "请输入编译脚本");

        validateJvmArgs(projectModule);

        if (projectModule.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
            Assert.notNull(projectModule.getResinConf(), "Resin配置不能为null");
            ResinConf resinConf = projectModule.getResinConf();
            Assert.isTrue(resinConf.getHttpPort() > 0, "http端口错误");
            Assert.isTrue(resinConf.getServerPort() > 0, "server端口错误");
            Assert.isTrue(resinConf.getWatchdogPort() > 0, "watchdog端口错误");
            //Assert.hasText(resinConf.getDomain(), "域名配置错误");

            String domain = resinConf.getDomain().trim().toLowerCase();
            if (domain.startsWith("http://")) {
                domain = domain.substring("http://".length());
            } else if (domain.startsWith("https://")) {
                domain = domain.substring("https://".length());
            }
            if (StringUtils.isNotBlank(domain)) {
                Assert.isTrue(isLegalDomain(domain), "域名格式错误: " + domain);
                resinConf.setDomain(domain);
            } else {
                resinConf.setDomain("");
            }
        }

    }

    private void validateJvmArgs(ProjectModule projectModule) {
        if (projectModule.getModuleType() == ModuleType.WEB_PROJECT.getValue()
                || ModuleUtil.isMainClass(projectModule.getRestartShell())) {
            List<ModuleJvm> moduleJvms = projectModule.getModuleJvms();
            Assert.notNull(moduleJvms, "jvm list不能为null");
            boolean found = false;
            for (ModuleJvm jvm : moduleJvms) {
                String arg = StringUtils.trimToEmpty(jvm.getJvmArgs());
                found |= StringUtils.isNotEmpty(arg);
                jvm.setJvmArgs(arg);
            }
            Assert.isTrue(found, "至少要配置一个环境的JVM参数");
        }
    }

    private boolean isLegalDomain(String domain) {
        return domain.matches(Constants.DOMAIN_REGX);
    }

    /**
     * 保存 resin 配置
     *
     * @param projectModule
     * @author linyi 2017/3/14
     */
    private void saveResinConf(ProjectModule projectModule) {
        if (projectModule.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
            ResinConf resinConf = projectModule.getResinConf();
            resinConf.setConfType(Constants.CONF_TYPE_RESIN);
            resinConf.setModuleId(projectModule.getModuleId());
            ModuleConfPO current = moduleConfDao.get(resinConf.getConfType(), projectModule.getModuleId());

            if (resinConf.getKeepaliveTimeout() <= 0) {
                resinConf.setKeepaliveTimeout(Constants.DEFAULT_KEEPALIVE_TIMEOUT);
            }
            if (resinConf.getSocketTimeout() <= 0) {
                resinConf.setSocketTimeout(Constants.DEFAULT_SOCKET_TIMEOUT);
            }
            if (current == null) {
                ModuleConfPO po = new ModuleConfPO();
                po.setConfType(resinConf.getConfType());
                po.setModuleId(projectModule.getModuleId());
                po.setCreateTime(new Date());
                po.setUpdateTime(po.getCreateTime());
                po.setConfValue(JSON.toJSONString(resinConf));
                moduleConfDao.insert(po);
            } else {
                current.setUpdateTime(new Date());
                current.setConfValue(JSON.toJSONString(resinConf));
                moduleConfDao.update(current);
            }

        }
    }

    private void saveModuleJvms(ProjectModule projectModule, int projectModuleId) {
        List<ModuleJvm> moduleJvms = projectModule.getModuleJvms();
        ArrayList<ModuleJvm> newModuleJvms = Lists.newArrayList();
        ArrayList<ModuleJvm> oldModuleJvms = Lists.newArrayList();
        for (ModuleJvm moduleJvm : moduleJvms) {
            moduleJvm.setModuleId(projectModuleId);
            if (moduleJvm.getModuleJvmId() > 0) {
                oldModuleJvms.add(moduleJvm);
            } else {
                newModuleJvms.add(moduleJvm);
            }
        }

        moduleJvmService.saveModuleJvm(newModuleJvms);
        moduleJvmService.updateModuleJvm(oldModuleJvms);
    }

    private void encodeAccountAndPassword(ProjectModulePO projectModulePO) {
        if (StringUtils.isNotBlank(projectModulePO.getSvnAccount())) {
            projectModulePO.setSvnAccount(StaticKeyHelper.encryptKey(projectModulePO.getSvnAccount().trim()));
        } else {
            projectModulePO.setSvnAccount("");
        }
        if (StringUtils.isNotBlank(projectModulePO.getSvnPassword())) {
            projectModulePO.setSvnPassword(StaticKeyHelper.encryptKey(projectModulePO.getSvnPassword().trim()));
        } else {
            projectModulePO.setSvnPassword("");
        }
    }

    private void saveServerGroupAndServer(List<ServerGroup> groups, int moduleId) {
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }

        List<ServerGroup> needUpdateGroups = Lists.newArrayList();
        List<Server> needUpdateServers = Lists.newArrayList();
        List<Integer> oldServerGroupIds = Lists.newArrayList();
        List<Integer> oldServerIds = Lists.newArrayList();
        for (ServerGroup group : groups) {
            group.setModuleId(moduleId);
            if (group.getGroupId() <= 0) {
                continue;
            }
            needUpdateGroups.add(group);
            oldServerGroupIds.add(group.getGroupId());
            if (CollectionUtils.isNotEmpty(group.getServers())) {
                for (Server server : group.getServers()) {
                    if (server.getServerId() > 0) {
                        oldServerIds.add(server.getServerId());
                        needUpdateServers.add(server);
                    }
                    server.setIp(StringUtils.trimToEmpty(server.getIp()));
                }
            }
        }

        // 删除被删除的服务器组
        serverGroupService.deleteByOldGroupIds(oldServerGroupIds, moduleId);
        // 先删除提交后不存在的服务器
        serverService.deleteByServerGroupIdsAndServerIds(oldServerGroupIds, oldServerIds);
        // 更新服务器组和服务器
        for (ServerGroup group : needUpdateGroups) {
            serverGroupService.updateGroup(group);
        }
        for (Server server : needUpdateServers) {
            serverService.updateServer(server);
        }
        // 插入新增的服务器组合服务器
        insertServerGroupAndServer(groups, moduleId);
    }

    private void insertServerGroupAndServer(List<ServerGroup> groups, int moduleId) {
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }
        List<ServerGroup> needInsertGroups = Lists.newArrayList();
        List<ServerGroup> oldGroups = Lists.newArrayList();
        for (ServerGroup group : groups) {
            group.setModuleId(moduleId);
            if (group.getGroupId() == 0) {
                needInsertGroups.add(group);
            } else {
                oldGroups.add(group);
            }
        }
        List<ServerGroup> serverGroups = serverGroupService.batchInsertGroups(needInsertGroups);

        List<Server> servers = Lists.newArrayList();
        // 新增的group的server
        for (ServerGroup group : serverGroups) {
            List<Server> serverList = group.getServers();
            if (CollectionUtils.isNotEmpty(serverList)) {
                for (Server server : serverList) {
                    server.setGroupId(group.getGroupId());
                    servers.add(server);
                }
            }
        }
        // 旧的group新增的server
        for (ServerGroup oldGroup : oldGroups) {
            if (CollectionUtils.isNotEmpty(oldGroup.getServers())) {
                for (Server server : oldGroup.getServers()) {
                    server.setGroupId(oldGroup.getGroupId());
                    if (server.getServerId() == 0) {
                        servers.add(server);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(servers)) {
            serverService.batchInsertServer(servers);
        }
    }

    @Override
    public List<ProjectModule> getByProjectId(int projectId) {
        Assert.isTrue(projectId > 0, "projectId 数值小于1");

        List<ProjectModulePO> projectModulePOs = projectModuleDao.getByProjectId(projectId);
        if (CollectionUtils.isEmpty(projectModulePOs)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(projectModulePOs, ProjectModule.class);
    }

    @Override
    public ProjectModule getByModuleId(int moduleId) {
        Assert.isTrue(moduleId > 0, "moduleId 数值小于1");

        ProjectModulePO projectModulePO = projectModuleDao.get(moduleId);
        if (projectModulePO != null) {
            ProjectModule projectModule = VOUtil.from(projectModulePO, ProjectModule.class);

            decodeAccountAndPassword(projectModule);

            List<ModuleJvm> moduleJvmArgses = buildModuleJvms(moduleId);

            projectModule.setModuleJvms(moduleJvmArgses);

            loadModuleConf(projectModule);
            return projectModule;
        }
        return null;
    }

    @Override
    public boolean isResinConfCreated(int moduleId) {
        return moduleConfDao.get(Constants.CONF_TYPE_RESIN_GEN_FLAG, moduleId) != null;
    }

    @Override
    public void setResinConfCreated(int moduleId) {
        ModuleConfPO moduleConf = moduleConfDao.get(Constants.CONF_TYPE_RESIN_GEN_FLAG, moduleId);
        if (moduleConf == null) {
            moduleConf = new ModuleConfPO();
            moduleConf.setConfValue("1");
            moduleConf.setModuleId(moduleId);
            moduleConf.setConfType(Constants.CONF_TYPE_RESIN_GEN_FLAG);
            moduleConf.setCreateTime(new Date());
            moduleConf.setUpdateTime(moduleConf.getCreateTime());
            moduleConfDao.insert(moduleConf);
        }
    }

    @Override
    public Map<String, String> checkResinPortOccupy(ProjectModule projectModule) {
        if (projectModule.getModuleType() == ModuleType.SERVICE.getValue() || projectModule.getResinConf() == null) {
            return Collections.emptyMap();
        }

        List<ServerGroup> serverGroups = projectModule.getServerGroups();
        if (CollectionUtils.isEmpty(serverGroups)) {
            return Collections.emptyMap();
        }

        List<Server> allServers = Lists.newArrayList();
        for (ServerGroup group : serverGroups) {
            if (CollectionUtils.isNotEmpty(group.getServers())) {
                allServers.addAll(group.getServers());
            }
        }
        if (CollectionUtils.isEmpty(allServers)) {
            return Collections.emptyMap();
        }

        return buildOccupyInfoMap(projectModule, allServers);
    }

    @Override
    public List<ProjectModule> getProjectModuleByAccountId(long accountId) {
        Assert.isTrue(accountId > 0);
        List<ProjectAccountRelationPO> relationPOS = projectAccountRelationDao.getByAccountId(accountId);
        if (CollectionUtils.isEmpty(relationPOS)) {
            return Collections.emptyList();
        }

        ArrayList<Integer> projectIds = Lists.newArrayList();
        for (ProjectAccountRelationPO relationPO : relationPOS) {
            projectIds.add(relationPO.getProjectId());
        }
        List<ProjectModulePO> modulePOS = projectModuleDao.getByProjectIds(projectIds);
        if (CollectionUtils.isEmpty(modulePOS)) {
            return Collections.emptyList();
        }

        return rebuildModuleName(projectIds, modulePOS);
    }

    @Override
    public List<ProjectModule> getSimpleInfoByProjectIds(List<Integer> projectIds) {
        List<ProjectModulePO> modulePOS = projectModuleDao.getByProjectIds(projectIds);
        if (CollectionUtils.isEmpty(modulePOS)) {
            return Collections.emptyList();
        }
        List<ProjectModule> modules = Lists.newArrayList();
        for (ProjectModulePO modulePO : modulePOS) {
            ProjectModule module = new ProjectModule();
            module.setModuleId(modulePO.getModuleId());
            module.setProjectId(modulePO.getProjectId());
            module.setModuleName(modulePO.getModuleName());
            module.setModuleNameZh(modulePO.getModuleNameZh());
            modules.add(module);
        }
        return modules;
    }

    private List<ProjectModule> rebuildModuleName(ArrayList<Integer> projectIds, List<ProjectModulePO> modulePOS) {
        List<ProjectModule> modules = VOUtil.fromList(modulePOS, ProjectModule.class);
        List<ProjectPO> projectPOS = projectDao.getByProjectIds(projectIds);
        HashMap<Integer, ProjectPO> projectId2ProjectMap = Maps.newHashMap();
        for (ProjectPO projectPO : projectPOS) {
            projectId2ProjectMap.put(projectPO.getProjectId(), projectPO);
        }
        for (ProjectModule module : modules) {
            if (projectId2ProjectMap.get(module.getProjectId()) != null) {
                String projectName = projectId2ProjectMap.get(module.getProjectId()).getProjectName();
                module.setModuleName(module.getModuleName() + "(" + projectName +  ")");
            }
        }
        return  modules;
    }

    private Map<String, String> buildOccupyInfoMap(ProjectModule projectModule, List<Server> allServers) {
        List<ModuleConfPO> matchModuleConfList = getModuleConf(projectModule);
        if (CollectionUtils.isEmpty(matchModuleConfList)) {
            return Collections.emptyMap();
        }

        List<Integer> moduleIds = Lists.newArrayList();
        for (ModuleConfPO po : matchModuleConfList) {
            if (projectModule.getModuleId() > 0 && po.getModuleId() == projectModule.getModuleId()) {
                continue;
            }
            moduleIds.add(po.getModuleId());
        }

        List<ServerGroup> serverGroupList = serverGroupService.getByModuleIds(moduleIds, true);
        if (CollectionUtils.isEmpty(serverGroupList)) {
            return Collections.emptyMap();
        }

        List<ProjectModulePO> modulePOs = projectModuleDao.getByModuleIds(moduleIds);
        HashMap<Integer, ProjectModulePO> moduleId2ModuleMap = Maps.newHashMap();
        for (ProjectModulePO modulePO : modulePOs) {
            moduleId2ModuleMap.put(modulePO.getModuleId(), modulePO);
        }

        HashMap<Integer, ModuleConfPO> moduleId2ConfMap = Maps.newHashMap();
        for (ModuleConfPO confPO : matchModuleConfList) {
            moduleId2ConfMap.put(confPO.getModuleId(), confPO);
        }

        Set<String> moduleServerIps = Sets.newHashSet();
        for (Server server : allServers) {
            moduleServerIps.add(server.getIp());
        }

        HashMap<String, String> port2OccupyInfoMap = Maps.newHashMap();

        // 找到有相同的服务器，再看看是哪个端口有冲突
        for (ServerGroup group : serverGroupList) {
            List<Server> servers = group.getServers();
            if (CollectionUtils.isEmpty(servers)) {
                continue;
            }
            for (Server server : servers) {
                if (moduleServerIps.contains(server.getIp())
                        && moduleId2ModuleMap.get(group.getModuleId()) != null
                        && moduleId2ConfMap.get(group.getModuleId()) != null) {

                    ProjectModulePO modulePO = moduleId2ModuleMap.get(group.getModuleId());
                    ModuleConfPO confPO = moduleId2ConfMap.get(group.getModuleId());

                    ResinConf resinConf = projectModule.getResinConf();
                    String occupyInfo = " 已被项目id为 " + modulePO.getProjectId() + " 的" + modulePO.getModuleNameZh() + " 模块所占用，相关服务器IP为 " + server.getIp();

                    if (confPO.getConfValue().contains("\"httpPort\":" + resinConf.getHttpPort())) {
                        port2OccupyInfoMap.put("http端口" + resinConf.getHttpPort(), occupyInfo);
                    }

                    if (confPO.getConfValue().contains("\"serverPort\":" + resinConf.getServerPort())) {
                        port2OccupyInfoMap.put("服务器端口" + resinConf.getServerPort(), occupyInfo);
                    }

                    if (confPO.getConfValue().contains("\"watchdogPort\":" + resinConf.getWatchdogPort())) {
                        port2OccupyInfoMap.put("watchdog端口" + resinConf.getWatchdogPort(), occupyInfo);
                    }
                }
            }
        }

        return port2OccupyInfoMap;
    }

    private List<ModuleConfPO> getModuleConf(ProjectModule projectModule) {
        ResinConf resinConf = projectModule.getResinConf();
        String httpPort = "%\"httpPort\":" + resinConf.getHttpPort() + "%";
        String serverPort = "%\"serverPort\":" + resinConf.getServerPort() + "%";
        String watchDogPort = "%\"watchdogPort\":" + resinConf.getWatchdogPort() + "%";

        List<ModuleConfPO> matchModuleConfList = moduleConfDao.getByTypeAndValue(Constants.CONF_TYPE_RESIN, httpPort, serverPort, watchDogPort);
        if (CollectionUtils.isEmpty(matchModuleConfList)) {
            return Collections.emptyList();
        }
        if (projectModule.getModuleId() <= 0) {
            return matchModuleConfList;
        }
        List<ModuleConfPO> moduleConfPOS = Lists.newArrayList();
        for (ModuleConfPO confPO : matchModuleConfList) {
            if (confPO.getModuleId() != projectModule.getModuleId()) {
                moduleConfPOS.add(confPO);
            }
        }

        return moduleConfPOS;
    }

    /**
     * 加载模块配置，比如 web 配置
     *
     * @param projectModule
     * @author linyi 2017/3/14
     */
    private void loadModuleConf(ProjectModule projectModule) {
        ModuleConfPO moduleConf = moduleConfDao.get(Constants.CONF_TYPE_RESIN, projectModule.getModuleId());
        if (moduleConf != null && StringUtils.isNotEmpty(moduleConf.getConfValue())) {
            ResinConf resinConf = JSON.parseObject(moduleConf.getConfValue(), ResinConf.class);
            resinConf.setModuleId(projectModule.getModuleId());
            projectModule.setResinConf(resinConf);
        }
    }

    private List<ModuleJvm> buildModuleJvms(int moduleId) {
        List<ModuleJvm> moduleJvmArgs = moduleJvmService.queryByModuleId(moduleId);
        List<ProjectEnv> allEnv = projectEnvService.findAllEnv();
        HashMap<Integer, ModuleJvm> envId2ModuleJvmMap = Maps.newHashMap();
        for (ModuleJvm moduleJvm : moduleJvmArgs) {
            envId2ModuleJvmMap.put(moduleJvm.getEnvId(), moduleJvm);
        }
        for (ProjectEnv env : allEnv) {
            if (envId2ModuleJvmMap.get(env.getEnvId()) == null) {
                ModuleJvm newModuleJvm = new ModuleJvm();
                newModuleJvm.setEnvId(env.getEnvId());
                newModuleJvm.setEnvName(env.getEnvName());
                envId2ModuleJvmMap.put(env.getEnvId(), newModuleJvm);
            }
        }
        return Lists.newArrayList(envId2ModuleJvmMap.values());
    }

    @Override
    @Transactional
    public void deleteByModuleId(int moduleId) {
        projectModuleDao.deleteModule(moduleId);
        // 服务器组和服务器
        serverGroupService.deleteByModuleId(moduleId);
        // jvm 参数
        moduleJvmService.deleteByModuleId(moduleId);
    }

    private void decodeAccountAndPassword(ProjectModule projectModule) {
        if (projectModule != null) {
            if (StringUtils.isNotEmpty(projectModule.getSvnAccount())) {
                projectModule.setSvnAccount(StaticKeyHelper.descryptKey(projectModule.getSvnAccount()));
            }
            if (StringUtils.isNotEmpty(projectModule.getSvnPassword())) {
                projectModule.setSvnPassword(StaticKeyHelper.descryptKey(projectModule.getSvnPassword()));
            }
        }
    }
}
