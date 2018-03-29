package com.ibeiliao.deployment.transfer.conf;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.transfer.vo.ModuleConf;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.util.List;

/**
 * 功能：Resin配置生成
 * 详细：
 *
 * @author linyi, 2017/3/14.
 */
public class ResinConfTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ResinConfTemplate.class);


    /**
     * resin配置
     */
    private ResinConf resinConf;

    private ModuleConf moduleConf;

    public static final String ADMIN_USERS = "admin-users.xml";

    public static final String APP_DEFAULT = "app-default.xml";

    public static final String CLUSTER_DEFAULT = "cluster-default.xml";

    public static final String RESIN_PROPERTIES = "resin.properties";

    private String lineSeparator = System.getProperty("line.separator");

    /**
     * 最终生成的配置文件名
     */
    private String confFileName;

    /**
     * 构造函数
     * @param resinConf   Resin配置信息
     * @param moduleConf  模块的基本信息
     */
    public ResinConfTemplate(ResinConf resinConf, ModuleConf moduleConf) {
        Assert.notNull(resinConf);
        Assert.notNull(moduleConf);
        this.resinConf = resinConf;
        this.moduleConf = moduleConf;
    }

    /**
     * 创建 resin 配置文件并输出到指定目录
     * @param outputPath 输出到目录，比如 /tmp/conf
     * @throws IOException
     */
    public List<String> createConfFiles(String outputPath) throws IOException {
        Assert.notNull(outputPath);
        String serverId = moduleConf.getShortModuleName();
        String hostName = resinConf.getDomain();
        String documentDir = Configuration.getWebRunDir() + resinConf.getDomain() + "/" + moduleConf.getShortModuleName();
        String archiveDir = ModuleUtil.getModuleDir(moduleConf.getProjectNo(), moduleConf.getShortModuleName()) + moduleConf.getModuleFinalName();
        //潜规则：如果域名为空，host默认设置为""，文件名统一用resin.xml
        confFileName = (StringUtils.isBlank(hostName) ? "resin" : hostName) + ".xml";

        String resinXml = readFile("/resin/resin.xml");
        String adminUsersXml = readFile("/resin/" + ADMIN_USERS);
        String appDefaultXml = readFile("/resin/" + APP_DEFAULT);
        String clusterDefaultXml = readFile("/resin/" + CLUSTER_DEFAULT);
        String resinProperties = readFile("/resin/" + RESIN_PROPERTIES);
        resinXml = resinXml.replaceAll("\\$\\{SERVER_ID}", serverId)
                .replaceAll("\\$\\{SERVER_PORT}", resinConf.getServerPort() + "")
                .replaceAll("\\$\\{WATCHDOG_PORT}", resinConf.getWatchdogPort() + "")
                .replaceAll("\\$\\{HTTP_PORT}", resinConf.getHttpPort() + "")
                .replaceAll("\\$\\{HOST_NAME}", hostName)
                .replaceAll("\\$\\{HOST_ALIAS}", buildAliasHost())
                .replaceAll("\\$\\{DOCUMENT_DIR}", documentDir)
                .replaceAll("\\$\\{ARCHIVE_PATH}", archiveDir)
                .replaceAll("\\$\\{RESIN_LOG}", getLogXml());

        resinXml = resinXml.replaceAll("\\$\\{JVM_ARGS}", buildJvmArgs());

        String accessLogFile = ModuleUtil.getAccessLogFile(moduleConf.getProjectNo(), moduleConf.getShortModuleName());
        resinXml = resinXml.replaceAll("\\$\\{ACCESS_LOG_FILE}", accessLogFile);

        String resinFile = outputPath + "/" + confFileName;
        FileUtils.writeStringToFile(new File(resinFile), resinXml, "utf-8");

        String appDefaultFile = outputPath + "/" + APP_DEFAULT;
        FileUtils.writeStringToFile(new File(appDefaultFile), appDefaultXml, "utf-8");

        String clusterFile = outputPath + "/" + CLUSTER_DEFAULT;
        FileUtils.writeStringToFile(new File(clusterFile), clusterDefaultXml, "utf-8");

        String adminUserFile = outputPath + "/" + ADMIN_USERS;
        FileUtils.writeStringToFile(new File(adminUserFile), adminUsersXml, "utf-8");

        String resinPropertiesFile = outputPath + "/" + RESIN_PROPERTIES;
        FileUtils.writeStringToFile(new File(resinPropertiesFile), resinProperties, "utf-8");

        return Lists.newArrayList(resinFile, appDefaultFile, clusterFile, adminUserFile, resinPropertiesFile);
    }

    /**
     * 返回 resin 的配置文件名
     * @return
     */
    public String getConfFileName() {
        return confFileName;
    }

    private String readFile(String file) throws IOException {
        InputStream is = ResinConfTemplate.class.getResourceAsStream(file);
        StringBuilder buf = new StringBuilder(1024);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            buf.append(line).append(lineSeparator);
        }
        IOUtils.closeQuietly(is);
        return buf.toString();
    }

    private String buildJvmArgs() {
        StringBuilder buf = new StringBuilder(64);
        if (resinConf.getThreadMax() > 0) {
            buf.append(buildXmlElement("thread-max", resinConf.getThreadMax())).append(lineSeparator);
        }
        if (resinConf.getKeepaliveMax() > 0) {
            buf.append(buildXmlElement("keepalive-max", resinConf.getKeepaliveMax())).append(lineSeparator);
        }
        if (resinConf.getKeepaliveTimeout() > 0) {
            buf.append(buildXmlElement("keepalive-timeout", resinConf.getKeepaliveTimeout())).append(lineSeparator);
        }
        if (resinConf.getSocketTimeout() > 0) {
            buf.append(buildXmlElement("socket-timeout", resinConf.getSocketTimeout())).append(lineSeparator);
        }
        if (StringUtils.isNoneBlank(moduleConf.getJvmArg())) {
            String jvmArg = moduleConf.getJvmArg();
            for (String arg : jvmArg.split(" ")) {
                if (StringUtils.isNotBlank(arg)) {
                    buf.append(buildXmlElement("jvm-arg", arg)).append(lineSeparator);
                }
            }
        }
        return buf.toString();
    }

    private String buildXmlElement(String name, Object value) {
        return "<" + name + ">" + value + "</" + name + ">";
    }

    private String getLogXml() {
        String logBase = Configuration.getResinLogDir() + moduleConf.getProjectNo() + "/"
                + moduleConf.getShortModuleName();
        String str = "<stdout-log path=\"" + logBase + "/stdout.log\" archive-format=\"stdout.log.%Y-%m-%d.gz\"\n" +
                "                  rollover-period=\"1D\" rollover-count=\"3\"/>\n" +
                "    <stderr-log path=\"" + logBase + "/stderr.err\" archive-format=\"stderr.err.%Y-%m-%d.gz\"\n" +
                "                  rollover-period=\"1D\" rollover-count=\"3\"/>";

        return str;
    }

    private String buildAliasHost() {
        String aliasDomain = resinConf.getAliasDomain();
        if (StringUtils.isBlank(aliasDomain)) {
            return "";
        }
        String[] domains = aliasDomain.split("\\s{1,}");
        StringBuilder builder = new StringBuilder();
        for (String domain : domains) {
            builder.append("<host-alias>").append(domain).append("</host-alias>\n");
        }
        return builder.toString();
    }
}
