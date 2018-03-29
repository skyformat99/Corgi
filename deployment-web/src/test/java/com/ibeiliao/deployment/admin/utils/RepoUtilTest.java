package com.ibeiliao.deployment.admin.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * 功能：测试 RepoUtil
 * 详细：
 *
 * @author linyi, 2017/3/15.
 */
public class RepoUtilTest {

    @Test
    public void testReadPomFinalName() throws Exception {
        String path = RepoUtilTest.class.getResource("/").getPath();
        path = path.replaceAll("target/test-classes/", "") + "pom.xml";
        String xml = FileUtils.readFileToString(new File(path), "utf-8");
        assertTrue(StringUtils.isNotBlank(xml));
        String finalName = RepoUtil.readPomFinalName(xml);
        assertEquals("deployment-web.war", finalName);
    }

    @Test
    public void test() throws Exception {
        String xml = FileUtils.readFileToString(new File("E:\\work\\2017\\2017年4月\\pom.xml"), "utf-8");
        assertTrue(StringUtils.isNotBlank(xml));
        String finalName = RepoUtil.readPomFinalName(xml);
        assertEquals("support-api-server.war", finalName);
    }

    @Test
    public void testGetNewModuleFinalName() throws Exception {
        String finalNameForGit = RepoUtil.getFinalNameForGit("platform-admin", "http://gits.ibeiliao.net/platform/platform-parent.git",
                "liangguanglong", "kevin711", "master");
        Assert.assertTrue(StringUtils.isNotBlank(finalNameForGit));
    }
}