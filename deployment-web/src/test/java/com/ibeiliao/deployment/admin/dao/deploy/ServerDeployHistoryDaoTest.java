package com.ibeiliao.deployment.admin.dao.deploy;

import com.ibeiliao.deployment.admin.po.deploy.ServerDeployHistoryPO;
import com.ibeiliao.deployment.admin.po.deploy.ServerDeployLogPO;
import com.ibeiliao.deployment.common.enums.DeployStatus;
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
 * 功能：测试 ServerDeployHistoryDao
 * 详细：
 *
 * @author linyi, 2017/3/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class ServerDeployHistoryDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ServerDeployHistoryDao serverDeployHistoryDao;

    @Test
    public void testInsertUpdate() {
        int historyId = (int) (System.currentTimeMillis() / 1000);
        ServerDeployHistoryPO po = new ServerDeployHistoryPO();
        po.setHistoryId(historyId);
        po.setServerId(1);
        po.setServerIp("127.0.0.1");
        po.setServerName("localhost");
        po.setDeployStatus(DeployStatus.DEPLOYING.getValue());
        po.setStartupTime(null);
        serverDeployHistoryDao.insert(po);

        List<ServerDeployHistoryPO> list = serverDeployHistoryDao.getByHistoryId(historyId);
        assertNotNull(list);
        assertTrue(list.size() == 1);

        int rows = serverDeployHistoryDao.updateStatus(po.getId(), DeployStatus.DEPLOYED.getValue(), new Date());
        assertTrue(rows == 1);

        rows = serverDeployHistoryDao.updateAllStatus(historyId, DeployStatus.CANCELLED.getValue(), new Date());
        assertTrue(rows == 1);
    }
}