package com.ibeiliao.deployment.log.test;

import com.ibeiliao.deployment.common.enums.LogType;
import com.ibeiliao.deployment.log.service.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/9
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")

public class SyncTest {


    @Autowired
    LogService logService;


    @Test
    public void SyncTest() throws Exception{
        logService.collectDeployLog(1, LogType.SERVER_SHELL_LOG.getType(), "测试111");
        logService.collectDeployLog(1, LogType.SERVER_SHELL_LOG.getType(), "测试111");
        logService.collectDeployLog(1, LogType.SERVER_SHELL_LOG.getType(), "测试111");
        logService.collectDeployLog(1, LogType.SERVER_SHELL_LOG.getType(), "测试111");
        logService.collectDeployLog(1, LogType.SERVER_SHELL_LOG.getType(), "测试111");
        System.out.println("=====================");

        System.out.println("睡眠30秒");
        Thread.sleep(30 * 1000);

    }

}
