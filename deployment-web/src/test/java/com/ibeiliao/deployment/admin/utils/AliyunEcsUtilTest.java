package com.ibeiliao.deployment.admin.utils;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.vo.project.AliyunEcs;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 功能：测试 AliyunEcsUtil
 * 详细：
 *
 * @author linyi, 2017/5/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class AliyunEcsUtilTest {

    @Test
    public void testLoad() {
        List<AliyunEcs> list = AliyunEcsUtil.load();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testUtcTimeFormat() throws Exception {
        String[] range = AliyunEcsUtil.getUtcRecentTimeRange();
        assertNotNull(range);
        assertTrue(range.length == 2);
        System.out.println(StringUtils.join(range, ","));
    }

//    @Test
//    public void testLoadMonitor() {
//        List<AliyunEcs> list = AliyunEcsUtil.load();
//        assertNotNull(list);
//        assertTrue(list.size() > 0);
//        AliyunEcs ecs = list.get(0);
//
//        System.out.println("instanceId: " + ecs.getInstanceId() + " -  " + ecs.getInstanceName());
//        AliyunEcsUtil.EcsMonitor monitor = AliyunEcsUtil.getMonitor("i-bp1g1qtjq2ptyaf7dfjr");
//        assertNotNull(monitor);
//    }

    @Test
    public void testGetXmxFromJvmArg() {
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xmx128m") == 128 * 1024);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xmx256mb") == 256 * 1024);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Dh=3") == AliyunEcsUtil.DEFAULT_XMX);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xmx2g") == 2048 * 1024);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xmx1gb") == 1024 * 1024);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xms128m") == AliyunEcsUtil.DEFAULT_XMX);
        assertTrue(AliyunEcsUtil.getHeapSizeFromJvmArg("-Xms1024m") == 1024 * 1024);

    }

    @Test
    public void testGetMonitor() {
        // i-bp1ejsc14psyaxusz46k elk1
        // i-bp1g1qtjq2ptyaf7dfjr api3-1
        // i-bp11t4fzcyfxavksn0z7 passport-1
//        AliyunEcsUtil.getRecentMonitor("i-bp1ejsc14psyaxusz46k");
        RestResult<Object> result = AliyunEcsUtil.isEnoughRes("i-bp11t4fzcyfxavksn0z7", "-Xmx256m");
        assertTrue(result.isSuccess());
    }
}
