package com.ibeiliao.deployment.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 功能：测试 ModuleUtil
 * 详细：
 *
 * @author linyi, 2017/3/2.
 */
public class ModuleUtilTest {

    @Test
    public void testMainClass() {
        assertTrue(ModuleUtil.isMainClass("com.alibaba.dubbo.container.Main"));
        assertTrue(ModuleUtil.isMainClass("Main"));
        assertFalse(ModuleUtil.isMainClass("echo \"hello\""));
        assertTrue(ModuleUtil.isMainClass("com.alibaba.dubbo_$container.Main"));
    }

    @Test
    public void testGetDomainForEnv() {
        String domain = "pf.ibeiliao.net";
        String devDomain = ModuleUtil.getDomainForEnv(domain, "dev");
        String testDomain = ModuleUtil.getDomainForEnv(domain, "test");
        String preDomain = ModuleUtil.getDomainForEnv(domain, "pre");
        String onlineDomain = ModuleUtil.getDomainForEnv(domain, "online");
        assertEquals("pf.dev.ibeiliao.net", devDomain);
        assertEquals("pf.test.ibeiliao.net", testDomain);
        assertEquals("pf-pre.ibeiliao.net", preDomain);
        assertEquals("pf.ibeiliao.net", onlineDomain);

        domain = "api3.ibeiliao.com";
        devDomain = ModuleUtil.getDomainForEnv(domain, "dev");
        testDomain = ModuleUtil.getDomainForEnv(domain, "test");
        preDomain = ModuleUtil.getDomainForEnv(domain, "pre");
        onlineDomain = ModuleUtil.getDomainForEnv(domain, "online");
        assertEquals("api3.dev.ibeiliao.com", devDomain);
        assertEquals("api3.test.ibeiliao.com", testDomain);
        assertEquals("api3-pre.ibeiliao.com", preDomain);
        assertEquals("api3.ibeiliao.com", onlineDomain);
    }
}