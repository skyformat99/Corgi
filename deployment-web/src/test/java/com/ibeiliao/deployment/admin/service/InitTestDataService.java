package com.ibeiliao.deployment.admin.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.account.RoleService;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.enums.ModuleRepoType;
import com.ibeiliao.deployment.common.enums.ModuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 功能：初始化测试数据
 * 详细：
 *
 * @author linyi, 2017/1/18.
 */
@Component
public class InitTestDataService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectAccountRelationService projectAccountRelationService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ServerGroupService serverGroupService;

    @Autowired
    private ServerService serverService;

    /**
     * 创建初始化数据
     */
    public InitData init() {
        // 1. 创建 account
        // 2. 创建 project
        // 3. 创建 module
        // 4. 初始化 env
        // 5. 创建 server group & server
        List<ProjectEnv> envs = createProjectEnv();
        AdminAccount account = createAdminAccount();
        Project project = createProject(account);
        ProjectModule module = createModule(account.getUid(), project, envs);
        return new InitData(account, project, module, envs);
    }

    private AdminAccount createAdminAccount() {
        AdminAccount account = new AdminAccount();
        long accountId = Integer.MAX_VALUE / 10;
        account.setAccount(accountId + "@abc.com");
        account.setDefaultData(Constants.TRUE);
//        account.setLastModify(new Date());
//        account.setCreateTime(new Date());
        account.setMobileNo("13000001111");
        account.setRealname("张" + accountId);
        account.setAccountStatus(AdminAccount.NOMAL);
        account.setUid(accountId);
        Set<Integer> roleIds = new HashSet<>();
        roleIds.add(1);
        adminAccountService.addOrUpdate(account, roleIds);
        Assert.notNull(adminAccountService.getById(accountId));
        return account;
    }

    private Project createProject(AdminAccount account) {
        Project project = new Project();
        project.setJoinerNames(account.getRealname());
        project.setProjectName("测试项目");
        project.setManagerId((int)account.getUid());
        project.setProgramLanguage("java");
        project.setManagerEmail(account.getAccount());
        project.setManagerPhone(account.getMobileNo());
        project.setProjectNo("test_project");
        project.setManagerName(account.getRealname());

        ProjectAccountRelation relation = new ProjectAccountRelation();
        relation.setAccountId(account.getUid());
        relation.setIsAdmin(Constants.TRUE);
//        relation.setProjectId(newProject.getProjectId());

        relation.setRealName(account.getRealname());
        List<ProjectAccountRelation> relations = new ArrayList<>();
        relations.add(relation);

        project.setProjectAccountRelations(relations);
        Project newProject = projectService.saveProject(account.getUid(), project);
        Assert.notNull(projectService.getProject(newProject.getProjectId()));

        List<ProjectAccountRelation> tmpList = projectAccountRelationService.getByProjectId(newProject.getProjectId());
        Assert.notEmpty(tmpList);
        Assert.isTrue(tmpList.size() == 1);
        return newProject;
    }

    private ProjectModule createModule(long accountId, Project project, List<ProjectEnv> envs) {
        ProjectModule module = new ProjectModule();
        module.setProjectId(project.getProjectId());
        module.setCompileShell("mvn -P=dev");
        module.setNeedAudit(Constants.TRUE);
        module.setModuleName("test-module");
        module.setModuleNameZh("测试模块");
        module.setModuleType(ModuleType.SERVICE.getValue());
        module.setLogName("");
        module.setPreShell("");
        module.setPostShell("");
        module.setRestartShell("/usr/local/resinpro/bin/resin.sh restart");
        module.setRepoUrl("https://a.b.com/test-project");
        module.setRepoType(ModuleRepoType.SVN.getValue());
        module.setSrcPath("");
        module.setStopShell("");
        module.setSvnAccount("test-account");
        module.setSvnPassword("test-password");
        module.setNeedAudit(Constants.TRUE);

        ProjectEnv env = envs.get(0);
        List<ServerGroup> serverGroups = new ArrayList<>();
        ServerGroup group1 = new ServerGroup();
        group1.setEnvId(env.getEnvId());
        group1.setGroupName("dev test");
        serverGroups.add(group1);
        projectModuleService.saveProjectModule(accountId, module);

        List<Server> servers = new ArrayList<>();
        final int MAX_SERVERS = 8;
        for (int i=0; i < MAX_SERVERS; i++) {
            servers.add(createServer(i));
        }
        group1.setServers(servers);
        module.setServerGroups(serverGroups);

        projectModuleService.saveProjectModule(accountId, module);

        ProjectModule tmpModule = projectModuleService.getByModuleId(module.getModuleId());
        Assert.notNull(tmpModule);
        Assert.isTrue(tmpModule.getNeedAudit() == Constants.TRUE);

        // 要重新读一次数据
        List<ServerGroup> newServerGroups = serverGroupService.getByModuleIds(Lists.newArrayList(module.getModuleId()),false);
        module.setServerGroups(newServerGroups);
        for (ServerGroup tmpGroup : newServerGroups) {
            List<Integer> groupIds = new LinkedList<>();
            groupIds.add(tmpGroup.getGroupId());
            List<Server> newServers = serverService.getByGroupIds(groupIds);
            tmpGroup.setServers(newServers);
        }
        return module;
    }

    private Server createServer(int i) {
        i = i + 1;
        Server server = new Server();
        server.setIp("12.34.56." + (10 + i));
        server.setServerName("test-server-" + i);
        return server;
    }

    private List<ProjectEnv> createProjectEnv() {
        ProjectEnv test = new ProjectEnv();
        test.setEnvId(10000001);
        test.setEnvName("test");
        projectEnvService.saveEnv(test);
        ProjectEnv online = new ProjectEnv();
        online.setEnvName("online");
        online.setEnvId(10000002);
        projectEnvService.saveEnv(online);
        List<ProjectEnv> envs = new ArrayList<>();
        envs.add(test);
        envs.add(online);
        return envs;
    }

    public static class InitData {
        public AdminAccount account;
        public Project project;
        public ProjectModule module;
        public ServerGroup serverGroup;
        public List<ProjectEnv> envs;

        public InitData(AdminAccount account, Project project, ProjectModule module, List<ProjectEnv> envs) {
            this.account = account;
            this.project = project;
            this.module = module;
            this.envs = envs;
        }
    }
}
