package com.ibeiliao.deployment.admin.dao.deploy;

import com.ibeiliao.deployment.admin.po.deploy.DeployHistoryPO;
import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatProjectResult;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import com.ibeiliao.deployment.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 功能：测试 DeployHistoryDao
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class DeployHistoryDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DeployHistoryDao deployHistoryDao;

    @Test
    public void testUpdateStatus() {
        DeployHistoryPO po = createDeployHistoryPO();
        deployHistoryDao.insert(po);
        assertTrue(po.getHistoryId() > 0);
        DeployHistoryPO tmp = deployHistoryDao.get(po.getHistoryId());
        assertEq(po, tmp);

        short newStatus = DeployStatus.DEPLOYED.getValue();
        int rows = deployHistoryDao.updateStatus(po.getHistoryId(), newStatus, po.getDeployStatus());
        assertTrue(rows > 0);
        tmp = deployHistoryDao.get(po.getHistoryId());
        assertTrue(tmp.getDeployStatus() == newStatus);
    }

    @Test
    public void testUpdateStatusAndResult() {
        DeployHistoryPO po = createDeployHistoryPO();
        deployHistoryDao.insert(po);
        assertTrue(po.getHistoryId() > 0);
        DeployHistoryPO tmp = deployHistoryDao.get(po.getHistoryId());
        assertEq(po, tmp);

        short newStatus = DeployStatus.DEPLOYED.getValue();
        short result = DeployResult.SUCCESS.getValue();
        int success = po.getDeployServers();
        int rows = deployHistoryDao.updateResultAndStatus(po.getHistoryId(), result, po.getDeployServers(), success, newStatus, po.getDeployStatus());
        assertTrue(rows > 0);
        tmp = deployHistoryDao.get(po.getHistoryId());
        assertTrue(tmp.getDeployStatus() == newStatus);
        assertTrue(tmp.getResult() == result);
        assertTrue(tmp.getSuccessCount() == success);
    }

    /**
     * 仅测试语法，不测试逻辑
     */
    @Test
    public void testStatProject() {
        Date startTime = DateUtil.currentStartDate();
        Date endTime = DateUtil.currentEndDate();

        List<StatProjectResult> list = deployHistoryDao.statProject(startTime, endTime, DeployStatus.DEPLOYED.getValue());
        assertNotNull(list);
    }

    /**
     * 仅测试语法，不测试逻辑
     */
    @Test
    public void testStatLowQualityModule() {
        Date startTime = DateUtil.currentStartDate();
        Date endTime = DateUtil.currentEndDate();
        int[] envId = {1};
        String envStr = StringUtils.join(envId, ",");
        List<LowQualityRank> ranks = deployHistoryDao.statLowQualityModule(startTime,
                endTime, envStr, DeployStatus.DEPLOYED.getValue(),
                DeployResult.SUCCESS.getValue(), Constants.LOW_QUALITY_DEPLOY_TIMES);
        assertNotNull(ranks);
    }

    private DeployHistoryPO createDeployHistoryPO() {
        DeployHistoryPO po = new DeployHistoryPO();
        po.setRealName("andy");
        po.setProjectId(1);
        po.setVersionNo("123");
        po.setEnvId(1);
        po.setAccountId(1);
        po.setConcurrentServerPercentage((short)50);
        po.setTitle("test-1");
        po.setTagName("/tags/20170201");
        po.setDeployStatus(DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        po.setResult(DeployResult.NONE.getValue());
        po.setModuleId(1);
        po.setModuleName("module-1");
        po.setDeployTimeInterval((short)20);
        po.setDeployServers(1);
        po.setSuccessCount(0);
        po.setCreateTime(new Date());
        return po;
    }

    private void assertEq(DeployHistoryPO po1, DeployHistoryPO po2) {
        assertNotNull(po1);
        assertNotNull(po2);
        assertTrue(po1.getHistoryId() == po2.getHistoryId());
        assertEquals(po1.getTitle(), po2.getTitle());
        assertEquals(po1.getRealName(), po2.getRealName());
        assertEquals(po1.getTagName(), po2.getTagName());
        assertEquals(po1.getModuleName(), po2.getModuleName());
        assertTrue(po1.getDeployStatus() == po2.getDeployStatus());
        assertTrue(po1.getResult() == po2.getResult());
        assertTrue(po1.getDeployServers() == po2.getDeployServers());
        assertTrue(po1.getSuccessCount() == po2.getSuccessCount());
    }
}