package com.ibeiliao.deployment.admin.service.server.impl;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.vo.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 详情 :
 *
 * @author liangguanglong
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class ServerServiceTest extends
        AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ServerService serverService;

    @Test
    public void testByGroupId() {
        List<Server> servers = serverService.getByGroupIds(Lists.newArrayList(1, 2));
        assert servers.isEmpty();
    }

    @Test
    public void testByModuleId() {
        List<Server> servers = serverService.getByModuleId(1);
        assert servers.isEmpty();
    }
}
