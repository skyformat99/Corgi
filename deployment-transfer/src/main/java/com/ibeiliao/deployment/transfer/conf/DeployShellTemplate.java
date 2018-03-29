package com.ibeiliao.deployment.transfer.conf;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.ModuleUserShellArgs;
import com.ibeiliao.deployment.common.ProgramLanguageType;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.common.util.ShellTemplateFileUtil;
import com.ibeiliao.deployment.constant.tpl.DeployTplArgs;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 详情 : 发布脚本模板处理类
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/3/28
 */
public class DeployShellTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DeployShellTemplate.class);

    /**
     * module 短名
     */
    private String shortModuleName;

    /**
     * 传输请求
     */
    private TransferRequest request;

    /**
     * 是否是stop
     */
    private boolean isStop;

    /**
     * 发布脚本内容
     */
    private String deployTplContents;

    /**
     * 发布类型
     */
    private String deployType;

    private static final String LOG_SCRIPT_NAME = "monitor_shell_log_upload.py";

    /**
     * 日志收集线程(monitor_shell_log_upload.py) 在 X 秒没有日志后退出
     */
    private static final int COLLECT_LOG_TIMEOUT = 120;

    /**
     * 模块重启的脚本
     */
    private String moduleDeployShell;

    /**
     * 模块stop脚本
     */
    private String moduleStopShell;

    public DeployShellTemplate(String shortModuleName, TransferRequest request, boolean isStop, String deployType) {
        this.shortModuleName = shortModuleName;
        this.request = request;
        this.isStop = isStop;
        this.deployType = deployType;

        moduleDeployShell = request.getRestartShell();

        moduleStopShell = request.getStopShell();

        if (Objects.equals(request.getLanguage(), ProgramLanguageType.HTML)) {
            deployTplContents = ShellTemplateFileUtil.getStaticDeployShellTpl();
        } else {
            if (request.getModuleType() == ModuleType.STATIC.getValue()) {
                deployTplContents = ShellTemplateFileUtil.getStaticInJavaDeployShellTpl();
            } else {
                deployTplContents = ShellTemplateFileUtil.getJavaDeployShellTpl();
            }
        }
        Assert.hasText(deployTplContents, "发布脚本模板没有信息");
    }

    private String buildRestartShell(TransferRequest request) {
        if (request.getModuleType() != ModuleType.WEB_PROJECT.getValue()) {
            return request.getRestartShell();
        }
        // web项目检测resin的目录是否有权限
        String defaultResin = Configuration.getWebContainerShell().split(" ")[0];
        StringBuilder builder = new StringBuilder();
        builder.append("if [ ! -x ").append(defaultResin).append(" ]; then\n");
        builder.append("  echo 'resin脚本 ").append(defaultResin).append("没有执行权限' >&2\n");
        builder.append("  exit 1\n");
        builder.append("fi\n");
        return builder.toString() + request.getRestartShell();
    }

    public String generateDeployShellFile() throws IOException {

        String tmpShellFilePath = FileUtils.getTempDirectoryPath() + "/setup_" + shortModuleName + ".sh";
        FileWriter writer = null;

        replaceDeployArgs();

        try {
            writer = new FileWriter(tmpShellFilePath);

            writer.write(deployTplContents);

        } catch (IOException e) {
            logger.error("生成执行的shell失败, {}", e);
            throw e;
        } finally {
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
        return tmpShellFilePath;
    }

    private void replaceDeployArgs() {

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.HISTORY_ID, String.valueOf(request.getHistoryId()));

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.ROLL_BACK_ID, String.valueOf(request.getRollBackDeployId()));

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.DEPLOY_TYPE, deployType);

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.GC_LOG_DIR, Configuration.getGcLogDir() + request.getProjectName() + "/" + shortModuleName + "/");

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.RESIN_ACCESS_LOG_DIR, Configuration.getAccessLogDir() + request.getProjectName() + "/" + request.getModuleName() + "/");

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.WEB_PROJECT_FLAG, String.valueOf(request.getModuleType()));

        replaceWebModuleArgs();

        replaceCollectLogArgs();

        replaceBackupArgs();

        replaceDecompressArgs();

        replaceStartupArgs();

    }

    private void replaceDecompressArgs() {
        deployTplContents = deployTplContents
                .replaceAll(DeployTplArgs.PID_FILE, shortModuleName + ".pid")
                .replaceAll(DeployTplArgs.MODULE_TAR_FILE, shortModuleName + ".tar.gz");
    }

    private void replaceWebModuleArgs() {
        if (request.getModuleType() != ModuleType.WEB_PROJECT.getValue()) {
            return;
        }
        // resin配置文件 ，resin.xml 改为域名.xml
        String resinXMlName = getResinXmlName();
        ArrayList<String> configFiles = Lists.newArrayList(ResinConfTemplate.ADMIN_USERS, ResinConfTemplate.APP_DEFAULT,
                ResinConfTemplate.CLUSTER_DEFAULT, ResinConfTemplate.RESIN_PROPERTIES, resinXMlName);
        String configFilesStr = StringUtils.join(configFiles, " ");
        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.MODULE_OSS_CONF_TAR, getOssResinTarFile())
                .replaceAll(DeployTplArgs.RESIN_CONF_DIR, ModuleUtil.getModuleConfDir(request.getProjectName(), shortModuleName))
                .replaceAll(DeployTplArgs.CONFIG_FILE_LIST, configFilesStr);
    }

    private String getOssResinTarFile() {
        return "oss_resin_" + shortModuleName + "_" + request.getEnv() + ".tar";
    }

    private void replaceCollectLogArgs() {

        String deployLogPath = "/tmp/deploy_" + shortModuleName + ".log";

        String moduleErrLogPath = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/" + shortModuleName + "_err.log";

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.MODULE_NAME, shortModuleName);

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.ENV, request.getEnv());

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.BASE_PROJECT_DIR, Configuration.getAnsibleUploadFileDir());

        if (request.getResinConf() != null) {
            // 域名为空的，统一采用resin.xml
            if (StringUtils.isBlank(request.getResinConf().getDomain())) {
                deployTplContents = deployTplContents.replaceAll(DeployTplArgs.RESIN_XML_FILE, "resin.xml");
            } else {
                deployTplContents = deployTplContents.replaceAll(DeployTplArgs.RESIN_XML_FILE, request.getResinConf().getDomain() + ".xml");
            }
        }

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.PROJECT_NAME, request.getProjectName());

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.MODULE_ERR_LOG, moduleErrLogPath);

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.LOG_FILE_DIR, deployLogPath);

        deployTplContents = deployTplContents.replaceAll(DeployTplArgs.PYTHON_COLLECT_LOG, buildCollectLogShell(deployLogPath));
    }

    private void replaceStartupArgs() {
        // 根据stop restart 确定 restartShell
        rebuildDeployShell();

        int hasPreShell = StringUtils.isNotBlank(request.getPreDeployShell()) ? 1 : 0;
        int hasPostShell = StringUtils.isNotBlank(request.getPostDeployShell()) ? 1 : 0;

        if (moduleDeployShell.contains("{" + ModuleUserShellArgs.CONF + "}")) {
            moduleDeployShell = moduleDeployShell.replaceAll("\\$\\{" + ModuleUserShellArgs.CONF + "}", ModuleUtil.getModuleConfDir(request.getProjectName(), shortModuleName) + getResinXmlName());
        }
        if (moduleStopShell.contains("{" + ModuleUserShellArgs.CONF + "}")) {
            moduleStopShell = moduleStopShell.replaceAll("\\$\\{" + ModuleUserShellArgs.CONF + "}", ModuleUtil.getModuleConfDir(request.getProjectName(), shortModuleName) + getResinXmlName());
        }

        deployTplContents = deployTplContents
                .replaceAll(DeployTplArgs.PRE_SHELL, StringUtils.defaultString(request.getPreDeployShell(), ""))
                .replaceAll(DeployTplArgs.HAS_PRESHELL, String.valueOf(hasPreShell))
                .replaceAll(DeployTplArgs.RESTART_SHELL, moduleDeployShell)
                .replaceAll(DeployTplArgs.STOP_SHELL, moduleStopShell)
                .replaceAll(DeployTplArgs.POST_SHELL, StringUtils.defaultString(request.getPostDeployShell(), ""))
                .replaceAll(DeployTplArgs.HAS_POSTSHELL, String.valueOf(hasPostShell));
    }

    private void replaceBackupArgs() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        String absoluteBackupPath = Configuration.getServerBackupFileDir() + request.getProjectName() + "/" + shortModuleName + "/";
        //项目备份目录 针对静态项目
        String projectBackupPath = Configuration.getServerBackupFileDir() + request.getProjectName() + "/";

        String needBackupModuleDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/" + shortModuleName;
        String needBackupProjectDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() ;

        deployTplContents = deployTplContents
                .replaceAll(DeployTplArgs.BACKUP_DIR, absoluteBackupPath)
                .replaceAll(DeployTplArgs.PRO_BACKUP_DIR, projectBackupPath)
                .replaceAll(DeployTplArgs.MODULE_DIR, needBackupModuleDir)
                .replaceAll(DeployTplArgs.PROJECT_DIR, needBackupProjectDir);
    }


    /**
     * 处理 ModuleType.SERVICE 的重启脚本，
     * 其他类型的 stop 脚本。
     */
    private void rebuildDeployShell() {
        // 模块类型 为 独立进程的
        if (request.getModuleType() == ModuleType.SERVICE.getValue()) {
            //本地生成的脚本 dubboStartupShellPath，dubbo重启脚本地址为/data/project/shell/项目名/模块名/restart_module名.sh
            moduleDeployShell = "sh " + getScriptServerDir() + "/restart_" + shortModuleName + ".sh" + " restart";
            moduleStopShell = "sh " + getScriptServerDir() + "/restart_" + shortModuleName + ".sh" + " stop";
        }
    }


    private String getResinXmlName() {
        //潜规则：如果域名为空，host默认设置为""，文件名统一用resin.xml
        String domain = request.getResinConf().getDomain();
        return (StringUtils.isBlank(domain) ? "resin" : domain) + ".xml";
    }

    private String getScriptServerDir() {
        return Configuration.getAnsibleUploadShellDir() + request.getProjectName() + "/" + shortModuleName + "/";
    }

    private String buildCollectLogShell(String deployLogPath) {
        StringBuilder builder = new StringBuilder(128);

        builder.append(" python ").append(getScriptServerDir()).append(LOG_SCRIPT_NAME).append(" ");
        /**
         * 传给python日志脚本的参数 :
         * [file_name/文件全路径]  [收集的超时时间] [type/日志类型:(shell|compile|deploy)]
         * [server_deploy_id/服务器发布id(当为type类型为compile/deploy时为 historyId)] [server_url/接口地址]
         * $deployId 是指发布的id
         **/
        builder.append(deployLogPath).append(" ").append(COLLECT_LOG_TIMEOUT).append(" deploy ")
                .append(request.getHistoryId()).append(" ")
                .append(Configuration.getCollectLogUrl())
                .append(" & ");
        return builder.toString();
    }
}
