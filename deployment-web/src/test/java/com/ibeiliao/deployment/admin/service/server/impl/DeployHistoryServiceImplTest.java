package com.ibeiliao.deployment.admin.service.server.impl;

import com.ibeiliao.deployment.admin.service.InitTestDataService;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.deploy.DeploymentOrder;
import com.ibeiliao.deployment.admin.vo.deploy.ServerDeployHistory;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import com.ibeiliao.deployment.common.enums.ServerDeployResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 功能：测试 DeployHistoryServiceImpl
 * 详细：
 *
 * @author linyi, 2017/1/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class DeployHistoryServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private InitTestDataService initTestDataService;

    /**
     * 测试创建上线单
     * 期望：
     * （1）创建成功
     * （2）校验参数成功
     * （3）审核成功
     */
    @Test
    public void testCreateDeploymentOrder() {
        // 前提：
        // 1. 创建 account
        // 2. 创建 project
        // 3. 创建 module
        // 4. 创建 server group & server
        // 5. 初始化 env
        InitTestDataService.InitData data = initTestDataService.init();
        ProjectEnv env = data.envs.get(0);

        DeploymentOrder order = new DeploymentOrder();
        order.setAccountId((int)data.account.getUid());
        order.setRealName(data.account.getRealname());
        order.setConcurrentServerPercentage((short)10);
        order.setDeployTimeInterval((short)30);
        order.setModuleId(data.module.getModuleId());
        order.setProjectId(data.project.getProjectId());
        order.setTagName("/tags/" + System.currentTimeMillis());
        order.setTitle("测试创建上线单");
        order.setServerId(getServerIds(data));
        order.setVersionNo("12345");
        order.setEnvId(env.getEnvId());
        deployHistoryService.createDeploymentOrder(order);

        List<DeployHistory> deployHistories = deployHistoryService.queryDeployHistory(order.getAccountId(), 0,0, 0,1, 10);
        assertNotNull(deployHistories);
        assertTrue(deployHistories.size() >= 1);

        DeployHistory history = deployHistories.get(0);
        // 判断状态
        assertTrue(history.getHistoryId() > 0);
        assertEquals(order.getTagName(), history.getTagName());
        assertEquals(order.getTitle(), history.getTitle());
        assertTrue(history.getDeployStatus() == DeployStatus.WAITING_FOR_AUDIT.getValue());
        assertTrue(history.getResult() == 0);
        assertTrue(order.getServerId().length == history.getDeployServers());
        assertTrue(order.getConcurrentServerPercentage() == history.getConcurrentServerPercentage());
        assertTrue(order.getAccountId() == history.getAccountId());
        assertTrue(order.getModuleId() == history.getModuleId());
        assertTrue(order.getProjectId() == history.getProjectId());
        assertEquals(data.project.getProjectName(), history.getProjectName());
        assertEquals(data.module.getModuleName(), history.getModuleName());

        testGetDeployHistory(history.getHistoryId(), order);

        testAudit(data.account.getUid(), history.getHistoryId());
    }

    /**
     * 测试审核发布
     * @param accountId
     * @param historyId
     */
    private void testAudit(long accountId, int historyId) {
        DeployHistory history = deployHistoryService.getByHistoryId(historyId);
        assertTrue(history.getDeployStatus() == DeployStatus.WAITING_FOR_AUDIT.getValue());
        deployHistoryService.audit(accountId, historyId);
        history = deployHistoryService.getByHistoryId(historyId);
        assertTrue(history.getDeployStatus() == DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
    }

    private int[] getServerIds(InitTestDataService.InitData data) {
        List<Server> servers = data.module.getServerGroups().get(0).getServers();
        int[] serverIds = new int[servers.size()];
        int count = 0;
        for (Server server : servers) {
            serverIds[count++] = server.getServerId();
        }
        return serverIds;
    }

    private void testGetDeployHistory(int historyId, DeploymentOrder order) {
        DeployHistory history = deployHistoryService.getByHistoryId(historyId);
        assertNotNull(history);
        assertNotNull(history.getServerDeployHistories());
        assertTrue(history.getServerDeployHistories().size() == order.getServerId().length);

        Set<Integer> serverSet = new HashSet<>();
        for (int serverId : order.getServerId()) {
            serverSet.add(serverId);
        }
        for (ServerDeployHistory sdh : history.getServerDeployHistories()) {
            assertTrue(sdh.getHistoryId() == historyId);
            assertTrue(StringUtils.isNotEmpty(sdh.getServerIp()));
            assertTrue(StringUtils.isNotEmpty(sdh.getServerName()));
            assertTrue(sdh.getDeployStatus() == ServerDeployResult.WAITING_FOR_DEPLOYMENT.getValue());
            assertTrue(serverSet.contains(sdh.getServerId()));
        }
    }

    /**
     * 仅测试SQL语法
     * 期望：测试通过
     */
    @Test
    public void testQueryDeployHistory() {
        long accountId = 100;
        List<DeployHistory> list = deployHistoryService.queryDeployHistory(accountId, 0,0, 0,1, 20);
        assertNotNull(list);
    }

    /**
     * 测试 restart
     * 期望：测试通过
     */
    @Test
    public void testCreateRestartHistory() {
        InitTestDataService.InitData data = initTestDataService.init();
        long accountId = data.account.getUid();
        int serverId = data.module.getServerGroups().get(0).getServers().get(0).getServerId();
        DeployHistory deployHistory = deployHistoryService.createRestartHistory(accountId, serverId);
        validateStopRestart(data, deployHistory.getHistoryId(), serverId);
    }

    /**
     * 测试 stop
     * 期望：测试通过
     */
    @Test
    public void testCreateStopHistory() {
        InitTestDataService.InitData data = initTestDataService.init();
        long accountId = data.account.getUid();
        int serverId = data.module.getServerGroups().get(0).getServers().get(0).getServerId();
        DeployHistory deployHistory = deployHistoryService.createStopHistory(accountId, serverId);
        validateStopRestart(data, deployHistory.getHistoryId(), serverId);
    }

    /**
     * 测试取消发布的功能
     * 期望：测试通过
     */
    @Test
    public void testCancel() {
        DeployHistory history = createDeployHistory();
        deployHistoryService.cancel(history.getAccountId(), history.getHistoryId());
        DeployHistory newHistory = deployHistoryService.getByHistoryId(history.getHistoryId());
        assertTrue(newHistory.getDeployStatus() == DeployStatus.CANCELLED.getValue());
    }

    /**
     * 测试拒绝审核
     * 期望：测试通过
     */
    @Test
    public void testReject() {
        DeployHistory history = createDeployHistory();
        deployHistoryService.reject(history.getAccountId(), history.getHistoryId());
        DeployHistory newHistory = deployHistoryService.getByHistoryId(history.getHistoryId());
        assertTrue(newHistory.getDeployStatus() == DeployStatus.AUDIT_REJECTED.getValue());
    }

    /**
     * 仅测试SQL语法
     * 期望：测试通过
     */
    @Test
    public void testQueryUnfinished() {
        Date now = new Date();
        Date endTime = DateUtils.addSeconds(now, Constants.DEPLOY_TASK_TIMEOUT);
        Date startTime = DateUtils.addSeconds(now, Constants.DEPLOY_TASK_TIMEOUT * 12);
        final int MAX = 1000;
        List<DeployHistory> list = deployHistoryService.queryUnfinished(startTime, endTime, MAX);
        assertNotNull(list);
    }

    private DeployHistory createDeployHistory() {
        InitTestDataService.InitData data = initTestDataService.init();
        ProjectEnv env = data.envs.get(0);

        DeploymentOrder order = new DeploymentOrder();
        order.setAccountId((int)data.account.getUid());
        order.setRealName(data.account.getRealname());
        order.setConcurrentServerPercentage((short)10);
        order.setDeployTimeInterval((short)30);
        order.setModuleId(data.module.getModuleId());
        order.setProjectId(data.project.getProjectId());
        order.setTagName("tags/" + System.currentTimeMillis());
        order.setTitle("测试创建上线单");
        order.setServerId(getServerIds(data));
        order.setVersionNo("12345");
        order.setEnvId(env.getEnvId());
        deployHistoryService.createDeploymentOrder(order);

        List<DeployHistory> deployHistories = deployHistoryService.queryDeployHistory(order.getAccountId(), 0,0, 0,1, 10);
        assertNotNull(deployHistories);
        assertTrue(deployHistories.size() >= 1);

        DeployHistory history = deployHistories.get(0);
        assertTrue(history.getDeployStatus() == DeployStatus.WAITING_FOR_AUDIT.getValue());
        return history;
    }

    private void validateStopRestart(InitTestDataService.InitData data, int historyId, int serverId) {
        DeployHistory tmp = deployHistoryService.getByHistoryId(historyId);
        assertTrue(tmp.getDeployStatus() == DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        assertTrue(tmp.getIsRestart() == Constants.TRUE);
        assertTrue(tmp.getServerDeployHistories().size() == 1);
        assertTrue(tmp.getServerDeployHistories().get(0).getServerId() == serverId);
        assertTrue(tmp.getProjectId() == data.project.getProjectId());
        assertTrue(tmp.getModuleId() == data.module.getModuleId());
        assertTrue(tmp.getAccountId() == data.account.getUid());

        deployHistoryService.finishStopRestart(historyId, DeployResult.SUCCESS);
        DeployHistory tmp2 = deployHistoryService.getByHistoryId(historyId);
        assertTrue(tmp2.getDeployTime() != null);
        assertTrue(tmp2.getDeployStatus() == DeployStatus.DEPLOYED.getValue());
        assertTrue(tmp2.getResult() == DeployResult.SUCCESS.getValue());
        assertTrue(tmp2.getServerDeployHistories().get(0).getDeployStatus() == ServerDeployResult.SUCCESS.getValue());
    }
}