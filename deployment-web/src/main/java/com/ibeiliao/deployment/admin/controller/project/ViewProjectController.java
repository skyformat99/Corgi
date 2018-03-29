package com.ibeiliao.deployment.admin.controller.project;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.global.GlobalSettingService;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ModuleJvmService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.utils.RepoUtil;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.global.GlobalSetting;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.project.*;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.RepositoryConstants;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.ModuleRepoType;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.transfer.enums.DeployType;
import com.ibeiliao.deployment.transfer.service.JavaTransferService;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import com.ibeiliao.deployment.transfer.vo.TransferResult;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情 : 项目详情
 *
 * @author liangguanglong
 */
@Menu(name = "项目详情", parent = "项目管理", sequence = 500038)
@Controller
@RequestMapping("admin/project")
public class ViewProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ViewProjectController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private GlobalSettingService globalSettingService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ProjectAccountRelationService projectAccountRelationService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ServerGroupService serverGroupService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private ModuleJvmService moduleJvmService;

    /**
     * 查看项目
     */
    @RequestMapping("viewProject.xhtml")
    @MenuResource("查看项目")
    public String viewProject() {
        return "/project/view_project";
    }

    /**
     * 获取项目
     */
    @RequestMapping("getProject")
    @ResponseBody
    @MenuResource("获取项目")
    public RestResult getProject(int projectId) {
        Project project = new Project();
        if (projectId > 0) {
            project = projectService.getProject(projectId);
        }

        List<AdminAccount> adminAccounts = adminAccountService.listAll();

        GlobalSetting globalSetting = globalSettingService.getGlobalSetting();

        ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo();
        projectDetailInfo.setProject(project);
        projectDetailInfo.setAllAccounts(adminAccounts);
        projectDetailInfo.setGlobalSetting(globalSetting);

        List<ProjectAccountRelation> accountRelations = projectAccountRelationService.getByProjectId(projectId);
        projectDetailInfo.setProjectAccountRelations(accountRelations);

        return new RestResult<>(projectDetailInfo);
    }

    /**
     * 查看项目详情
     */
    @RequestMapping("projectDetail")
    @ResponseBody
    @MenuResource("项目详情")
    public RestResult projectDetail(int projectId) {
        Project project = projectService.getProject(projectId);

        GlobalSetting globalSetting = globalSettingService.getGlobalSetting();

        ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo();
        projectDetailInfo.setProject(project);
        projectDetailInfo.setGlobalSetting(globalSetting);

        List<ProjectEnv> allEnv = projectEnvService.findAllEnv();
        projectDetailInfo.setProjectEnvs(allEnv);

        List<ProjectModule> modules = projectModuleService.getByProjectId(projectId);
        fillServerGroupIntoModule(modules);
        fillJvmArgsIntoModule(modules);
        projectDetailInfo.setModules(modules);

        return new RestResult<>(projectDetailInfo);
    }

    /**
     * 删除模块
     */
    @RequestMapping("deleteModule.do")
    @ResponseBody
    @MenuResource("删除模块")
    public RestResult<Object> deleteModule(int moduleId) {
        Assert.isTrue(moduleId > 0, "moduleId 要大于0");

        projectModuleService.deleteByModuleId(moduleId);
        return new RestResult<>(ApiCode.SUCCESS);
    }

    /**
     * 重启服务
     */
    @RequestMapping("restartServer.do")
    @ResponseBody
    @MenuResource("重启服务")
    public RestResult<Object> restartServer(int serverId) {
        TransferRequest request;
        try {
            request = createTransferRequest(serverId, true);
            Assert.isTrue(request.getModuleType() != ModuleType.STATIC.getValue(), "静态项目不能执行重启");
            JavaTransferService javaTransferService = new JavaTransferService(request, DeployType.RESTART);
            return transfer(request.getHistoryId(), javaTransferService);
        } catch (Exception e) {
            logger.info("重启失败， {}", e);
            return new RestResult<>(ApiCode.FAILURE, "重启失败");
        }
    }

    /**
     * 停止服务
     */
    @RequestMapping("stopServer.do")
    @ResponseBody
    @MenuResource("停止服务")
    public RestResult<Object> stopServer(int serverId) {
        TransferRequest request;
        try {
            request = createTransferRequest(serverId, false);
            Assert.isTrue(request.getModuleType() != ModuleType.STATIC.getValue(), "静态项目不能stop");
            JavaTransferService javaTransferService = new JavaTransferService(request, DeployType.STOP);
            return transfer(request.getHistoryId(), javaTransferService);
        } catch (Exception e) {
            logger.info("停止失败，{}", e);
            return new RestResult<>(ApiCode.FAILURE, "停止失败");
        }
    }

    private RestResult<Object> transfer(int historyId, JavaTransferService javaTransferService) {
        TransferResult transferResult = javaTransferService.pushPackageToServer();
        int code = (transferResult.getSuccessType() == DeployResult.SUCCESS ? ApiCode.SUCCESS : ApiCode.FAILURE);
        String message = transferResult.getFailLog();
        if (StringUtils.isEmpty(message)) {
            message = transferResult.getSuccessType().toString();
        }
        deployHistoryService.finishStopRestart(historyId, transferResult.getSuccessType());
        logger.info("对服务器操作结果: {}, code: {}, message: {}", transferResult.getSuccessType(), code, message);
        return new RestResult<>(code, message);
    }

    private TransferRequest createTransferRequest(int serverId, boolean restart) throws Exception {
        Server server = serverService.getById(serverId);
        Assert.notNull(server, "server不存在");
        ServerGroup serverGroup = serverGroupService.getById(server.getGroupId());
        Assert.notNull(serverGroup, "serverGroup不存在");
        ProjectModule module = projectModuleService.getByModuleId(serverGroup.getModuleId());
        Project project = projectService.getProject(module.getProjectId());
        ProjectEnv env = projectEnvService.getById(serverGroup.getEnvId());
        long accountId = AdminContext.getAccountId();
//        if (projectAccountRelationService.canModify(accountId, project.getProjectId())) {

            DeployHistory deployHistory = null;
            if (restart) {
                deployHistory = deployHistoryService.createRestartHistory(accountId, serverId);
            } else {
                deployHistory = deployHistoryService.createStopHistory(accountId, serverId);
            }
            TransferRequest request = new TransferRequest();
            request.setHistoryId(deployHistory.getHistoryId());
            request.setSaveFileName(null);
            request.setModuleName(module.getModuleName());
            request.setEnv(env.getEnvName());
            request.setProjectName(project.getProjectNo());
            request.setRestartShell(module.getRestartShell());
            request.setJvmArgs(getModuleJvmArgs(module.getModuleId(), serverGroup.getEnvId()));
            request.setPreDeployShell(module.getPreShell());
            request.setPostDeployShell(module.getPostShell());
            request.setStopShell(module.getStopShell());
            request.setModuleType(module.getModuleType());
            List<String> serverIps = new ArrayList<>();
            serverIps.add(server.getIp());
            request.setTargetServerIps(serverIps);

            if (module.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
                ResinConf resinConf = module.getResinConf();
                // 设置 [当前环境] 的域名
                resinConf.setDomain(ModuleUtil.getDomainForEnv(resinConf.getDomain(), env.getEnvName()));
                resinConf.setAliasDomain(ModuleUtil.getAliasDomainForEnv(resinConf.getAliasDomain(), env.getEnvName()));

                request.setModuleFinalName(readFinalName(module, deployHistory.getTagName()));
                request.setResinConf(resinConf);

                if (!projectModuleService.isResinConfCreated(deployHistory.getModuleId())
                        || resinConf.isCreateEveryTime()) {
                    request.setCreateResinConf(true);
                }
            }

            return request;
//        }
//        throw new IllegalStateException("你没有权限操作，请联系项目负责人或超级管理员");
    }

    private String readFinalName(ProjectModule module, String branchName) throws Exception {
        // 读取final name默认采用 trunk 读取
        if (module.getRepoType() == ModuleRepoType.SVN.getValue()) {
            if (StringUtils.isBlank(branchName)) {
                branchName = RepositoryConstants.TRUNK.substring(1);
            }
            String pomUrl = RepoUtil.getPomRepoUrl(module.getRepoUrl(), branchName, module.getModuleName());
            return RepoUtil.getFinalNameForSvn(pomUrl, module.getSvnAccount(), module.getSvnPassword());
        }
        return RepoUtil.getFinalNameForGit(module.getModuleName(), module.getRepoUrl(), module.getSvnAccount(), module.getSvnPassword(), branchName);
    }

    private String getModuleJvmArgs(int moduleId, int envId) {
        List<ModuleJvm> moduleJvms = moduleJvmService.queryByModuleId(moduleId);
        if (CollectionUtils.isEmpty(moduleJvms)) {
            return "";
        }
        for (ModuleJvm moduleJvm : moduleJvms) {
            if (moduleJvm.getEnvId() == envId) {
                return moduleJvm.getJvmArgs();
            }
        }
        return "";
    }

    private void fillJvmArgsIntoModule(List<ProjectModule> modules) {
        if (CollectionUtils.isEmpty(modules)) {
            return;
        }
        List<Integer> moduleIds = Lists.newArrayList();
        for (ProjectModule module : modules) {
            moduleIds.add(module.getModuleId());
        }
        List<ModuleJvm> moduleJvms = moduleJvmService.queryByModuleIds(moduleIds);
        ArrayListMultimap<Integer, ModuleJvm> moduleId2ModuleJvmsMap = ArrayListMultimap.create();
        for (ModuleJvm moduleJvm : moduleJvms) {
            moduleId2ModuleJvmsMap.put(moduleJvm.getModuleId(), moduleJvm);
        }
        for (ProjectModule module : modules) {
            if (moduleId2ModuleJvmsMap.get(module.getModuleId()) != null) {
                module.setModuleJvms(moduleId2ModuleJvmsMap.get(module.getModuleId()));
            }
        }
    }

    private void fillServerGroupIntoModule(List<ProjectModule> modules) {
        if (CollectionUtils.isEmpty(modules)) {
            return;
        }
        List<Integer> moduleIds = Lists.newArrayList();
        for (ProjectModule module : modules) {
            moduleIds.add(module.getModuleId());
        }
        List<ServerGroup> serverGroups = serverGroupService.getByModuleIds(moduleIds, true);
        ArrayListMultimap<Integer, ServerGroup> moduleId2ServerGroupsMap = ArrayListMultimap.create();
        for (ServerGroup group : serverGroups) {
            moduleId2ServerGroupsMap.put(group.getModuleId(), group);
        }

        for (ProjectModule module : modules) {
            if (moduleId2ServerGroupsMap.get(module.getModuleId()) != null) {
                module.setServerGroups(moduleId2ServerGroupsMap.get(module.getModuleId()));
            }
        }
    }
}
