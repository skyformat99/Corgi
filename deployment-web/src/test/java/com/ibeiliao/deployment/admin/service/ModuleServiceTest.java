package com.ibeiliao.deployment.admin.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.enums.ModuleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 详情 :
 *
 * @author liangguanglong
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class ModuleServiceTest extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    private ProjectModuleService moduleService;

    @Autowired
    private ServerGroupService serverGroupService;

    @Test
    public void testSave() {
        ProjectModule module = new ProjectModule();
        buildModuleBaseInfo(module);
        fillServerGroupInfo(module);

        moduleService.saveProjectModule(1L, module);
    }

    private void fillServerGroupInfo(ProjectModule module) {
        ServerGroup group = new ServerGroup();
        group.setGroupId(129);
        group.setModuleId(43);
        group.setCreateTime(new Date());
        group.setEnvId(1);
        group.setGroupName("online01111");

        Server server1 = new Server();
        server1.setServerId(22);
        server1.setGroupId(129);
        server1.setCreateTime(new Date());
        server1.setServerName("server00008888");
        server1.setIp("192.168.0.1");

        Server server2 = new Server();
        server2.setGroupId(129);
        server2.setCreateTime(new Date());
        server2.setServerName("server000099");
        server2.setIp("192.168.0.4");

        group.setServers(Lists.newArrayList(server1,server2));

        module.setServerGroups(Lists.newArrayList(group));
    }
    private void buildModuleBaseInfo(ProjectModule module) {
        module.setModuleId(43);

        module.setCreateTime(new Date());
        module.setCompileShell("1.sh");
        module.setLogName("logName");
        module.setModuleName("admin3");
        module.setModuleNameZh("管理3");
        module.setModuleType(ModuleType.WEB_PROJECT.getValue());
        module.setNeedAudit(Constants.TRUE);
        module.setCompileShell("2.sh");
        module.setPostShell("3.sh");
        module.setPreShell("4.sh");
        module.setRestartShell("5.sh");
        module.setStopShell("6.sh");
        module.setProjectId(1);
        module.setRepoUrl("http://svn");
        module.setSvnAccount("1234");
        module.setSvnPassword("345");
        module.setSrcPath("svn://11");

    }


    @Test
    public void testCheckResinPort() {
        ProjectModule module = moduleService.getByModuleId(6);
        List<ServerGroup> serverGroups = serverGroupService.getByModuleIds(Lists.newArrayList(6), true);

        module.setServerGroups(serverGroups);
        module.setModuleId(0);
        module.getResinConf().setHttpPort(8087);
        module.getResinConf().setServerPort(6807);
        module.getResinConf().setWatchdogPort(6608);
        Map<String, String> resinPort2OccupyInfoMap = moduleService.checkResinPortOccupy(module);

        assert !resinPort2OccupyInfoMap.isEmpty();
    }
}
