package com.ibeiliao.deployment.transfer.conf;

import com.ibeiliao.deployment.transfer.vo.ModuleConf;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import org.junit.Test;

/**
 * 功能：ResinConfTemplate 测试
 * 详细：
 *
 * @author linyi, 2017/3/15.
 */
public class ResinConfTemplateTest {

    @Test
    public void testCreateConf() throws Exception {
        ResinConf resinConf = new ResinConf();
        resinConf.setDomain("pf.dev.ibeiliao.net");
        resinConf.setSocketTimeout(30);
        resinConf.setKeepaliveTimeout(15);
        resinConf.setHttpPort(8080);
        resinConf.setServerPort(6800);
        resinConf.setWatchdogPort(6600);

        ModuleConf moduleConf = new ModuleConf();
        moduleConf.setProjectNo("platform-parent");
        moduleConf.setShortModuleName("platform-admin");
        moduleConf.setModuleFinalName("platform-admin.war");
        moduleConf.setJvmArg("-Xms128m -Xmx160m -d64 -server");

        ResinConfTemplate template = new ResinConfTemplate(resinConf, moduleConf);
        template.createConfFiles("d://temp//resin");
    }
}