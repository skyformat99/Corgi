package com.ibeiliao.deployment.compile.vo;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.ModuleUserShellArgs;
import com.ibeiliao.deployment.common.ProgramLanguageType;
import com.ibeiliao.deployment.common.enums.ModuleRepoType;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.common.util.ShellTemplateFileUtil;
import com.ibeiliao.deployment.constant.tpl.CompileTplArgs;
import com.ibeiliao.deployment.storage.FileStorageUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static com.ibeiliao.deployment.constant.tpl.CompileTplArgs.MODULE_NAME;
import static com.ibeiliao.deployment.constant.tpl.CompileTplArgs.MVN_CP_SHELL;

/**
 * 详情 : 编译脚本的处理类
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/3/27
 */
public class CompileShellTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CompileShellTemplate.class);

    /**
     * 模块短名
     */
    private String shortModuleName;

    /**
     * 编译请求
     */
    private CompileRequest compileRequest;

    /**
     * 编译模板内容
     */
    private String compileTplContent;

    /**
     * 日志脚本名称
     */
    private String logScriptName;

    /**
     * 日志收集线程(monitor_shell_log_upload.py) 在 X 秒没有日志后退出
     */
    private static final int COLLECT_LOG_TIMEOUT = 120;

    public CompileShellTemplate(String shortModuleName, CompileRequest compileRequest, String logScriptName) {

        this.shortModuleName = shortModuleName;
        this.compileRequest = compileRequest;
        this.logScriptName = logScriptName;

        if (Objects.equals(compileRequest.getLanguage(), ProgramLanguageType.HTML)) {
            compileTplContent = ShellTemplateFileUtil.getStaticCompileShellTpl();
        } else {
            if (compileRequest.getModuleType() == ModuleType.STATIC.getValue()) {
                compileTplContent = ShellTemplateFileUtil.getStaticInJavaCompileShellTpl();
            } else {
                compileTplContent = ShellTemplateFileUtil.getJavaCompileShellTpl();
            }
        }
        Assert.hasText(compileTplContent, "编译模板没有内容");

    }

    /**
     * 生成编译脚本
     *
     * @return
     */
    public String generate() throws IOException {

        String tmpShellFilePath = FileUtils.getTempDirectoryPath() + "/compile_" + shortModuleName + ".sh";

        replaceCompileTplArgs();

        FileWriter writer = null;
        try {
            writer = new FileWriter(tmpShellFilePath);

            writer.write(compileTplContent);

        } catch (IOException e) {
            logger.error("生成执行编译的shell失败, {}", e);
            throw e;
        } finally {
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
        return tmpShellFilePath;
    }

    private void replaceCompileTplArgs() {
        String logFileDir = "/tmp/" + compileRequest.getEnv() + "_" + shortModuleName + "_shell.log";
        logger.info("log file: " + logFileDir);
        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.LOG_FILE_DIR, logFileDir);

        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.MODULE_ERR_LOG, getMvnTargetProjectDir() + shortModuleName + "_err.log");

        replaceNeedCompileArgs();

        replaceCheckoutArgs();

        replaceMvnArgs();

        replaceCompressArgs();

        replaceLocalStorageArgs();

    }

    private void replaceLocalStorageArgs() {
        String saveFileName = FileStorageUtil.getSaveFileName(compileRequest.getProjectId(), compileRequest.getModuleId(), compileRequest.getTagName(), compileRequest.getEnv(), compileRequest.getVersion());
        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.LOCAL_STORAGE_DIR, Configuration.getCompileStorageDir())
                .replaceAll(CompileTplArgs.SAVE_FILE_NAME, saveFileName);
    }

    private void replaceCheckoutArgs() {
        String tagName = compileRequest.getTagName();
        String checkoutDir = Configuration.getCompileServerCheckoutDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName()
                + "/" + tagName;

        String checkoutShell = buildCheckoutShell(tagName, checkoutDir);

        compileTplContent = compileTplContent
                .replaceAll(CompileTplArgs.BRANCH_DIR, checkoutDir)
                .replaceAll(CompileTplArgs.CHECKOUT_SHELL, checkoutShell);

    }

    private String buildCheckoutShell(String tagName, String checkoutDir) {
        String checkoutShell = " yes 2>/dev/null | ";

        String repoURL = compileRequest.getSvnAddr();
        if (compileRequest.getRepoType() == ModuleRepoType.SVN.getValue()) {
            checkoutShell = checkoutShell + "svn co " + repoURL + "/" + tagName + " " + checkoutDir
                    + " --no-auth-cache --username='" + compileRequest.getSvnUserName() + "' --password='" + compileRequest.getSvnPassword() + "'";
        } else {

            if (tagName.startsWith("/")) {
                tagName = tagName.substring(1, tagName.length());
            }
            if (tagName.endsWith("/")) {
                tagName = tagName.substring(0, tagName.length() - 1);
            }
            checkoutShell = "git clone ";
            String[] gitURLStartArr = {"http://", "https://", "git"};
            for (String startStr : gitURLStartArr) {
                if (repoURL.startsWith(startStr)) {
                    String replaceStr = startStr + compileRequest.getSvnUserName() + ":"
                            + compileRequest.getSvnPassword() + "@";
                    checkoutShell = checkoutShell + repoURL.replaceFirst(startStr, replaceStr) + " " + checkoutDir + "\n";
                    break;
                }
            }

            checkoutShell = checkoutShell + "cd " + checkoutDir + "\n" + "git pull\n";

            if (tagName.startsWith("branches/")) {
                checkoutShell = checkoutShell + "git checkout " + tagName.substring("branches/".length(), tagName.length());
            }

            if (tagName.startsWith("tags/")) {
                checkoutShell = checkoutShell + "git checkout " + tagName;
            }
        }

        return checkoutShell + " >/tmp/checkout_" + compileRequest.getEnv() + "_" + shortModuleName + ".log";
    }

    private void replaceCompressArgs() {
        compileTplContent = compileTplContent
                .replaceAll(CompileTplArgs.COMPILE_PROJECT_DIR, getMvnTargetProjectDir())
                .replaceAll(CompileTplArgs.MODULE_TAR_FILE, shortModuleName + ".tar.gz");
    }

    private void replaceNeedCompileArgs() {
        String projectDir = Configuration.getCompileServerCheckoutDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName() + "/";
        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.PROJECT_DIR, projectDir);

        // branch/tags_分支名_版本号.tar
        String tagName = compileRequest.getTagName();
        String uniqueName, shortBranchName, branchTypeName;
        if (tagName.startsWith("/")) {
            tagName = tagName.substring(1, tagName.length());
        }
        if (tagName.endsWith("/")) {
            tagName = tagName.substring(0, tagName.length() - 1);
        }
        if (tagName.contains("tags") || tagName.contains("branches") || tagName.contains("branch")) {
            uniqueName = tagName.replaceFirst("/", "_") + "_" + compileRequest.getVersion();
            branchTypeName = tagName.split("/")[0];
            shortBranchName = tagName.substring(branchTypeName.length() + 1, tagName.length());

        } else {
            uniqueName = tagName + "_" + compileRequest.getVersion();
            shortBranchName = tagName;
            branchTypeName = "";
        }

        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.BRANCH_TYPE_NAME, branchTypeName)
                .replaceAll(CompileTplArgs.BRANCH_TAR_FILE, uniqueName + ".tar")
                .replaceAll(CompileTplArgs.BRANCH_MD5_FILE, uniqueName + ".md5")
                .replaceAll(CompileTplArgs.SHORT_BRANCH_NAME, shortBranchName)
                .replaceAll(CompileTplArgs.REPO_TYPE, String.valueOf(compileRequest.getRepoType()))
                .replaceAll(CompileTplArgs.FORCE_COMPILE, String.valueOf(compileRequest.getForceCompile()));

    }

    private String getMvnTargetModuleDir() {
        return getMvnTargetProjectDir() + shortModuleName + "/";
    }

    private String getMvnTargetProjectDir() {
        return Configuration.getCompilePackagedir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName() + "/";
    }

    private void replaceMvnArgs() {

        compileTplContent = compileTplContent
                .replaceAll(MODULE_NAME, shortModuleName)
                .replaceAll(CompileTplArgs.COMPILED_FILE_DIR, getMvnTargetModuleDir())
                .replaceAll(CompileTplArgs.PROJECT_LOG_DIR, Configuration.getCompileLogDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName() + "/")
                .replaceAll(CompileTplArgs.PYTHON_COLLECT_LOG, buildCollectionLogShell());

        replaceMvnShell();
    }

    private void replaceMvnShell() {
        String checkoutDir = Configuration.getCompileServerCheckoutDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName()
                + "/" + compileRequest.getTagName();
        // 1. 替换参数
        String compileShell = compileRequest.getCompileShell();
        if (compileShell.contains("${" + ModuleUserShellArgs.MODULE_DIR + "}")) {
            compileShell = compileShell.replaceAll("\\$\\{" + ModuleUserShellArgs.MODULE_DIR + "}", checkoutDir + "/" + compileRequest.getModuleName());
        }
        String mvnTargetFileDir = getMvnTargetModuleDir();
        if (compileShell.contains("${" + ModuleUserShellArgs.TARGET_DIR + "}")) {
            compileShell = compileShell.replaceAll("\\$\\{" + ModuleUserShellArgs.TARGET_DIR + "}", mvnTargetFileDir);
        }
        compileShell = compileShell.replaceAll("\\$\\{" + ModuleUserShellArgs.ENV + "}", compileRequest.getEnv());
        // 2. 逐条处理
        String[] userCompileShellList = compileShell.split("\n");
        for (String shell : userCompileShellList) {
            if (StringUtils.isBlank(shell)) {
                continue;
            }
            shell = shell.trim();
            if (shell.contains("mvn ")) {
                compileTplContent = compileTplContent.replaceAll(CompileTplArgs.MVN_SHELL, shell);
            } else {
                // 判断是否是是复制, 改为 yes 2>/dev/null | cp 实现拷贝覆盖(2>/dev/null 是为了忽略yes的错误信息)
                if (shell.startsWith("cp ") && !shell.contains("/dev/null")) {
                    compileTplContent = compileTplContent.replaceAll(MVN_CP_SHELL, shell.replace("cp ", "yes 2>/dev/null | cp "));
                }
                if (shell.startsWith("cp ") && shell.contains("/dev/null")) {
                    compileTplContent = compileTplContent.replaceAll(MVN_CP_SHELL, shell);
                }
            }
        }
        // 3. mvn 日志
        String compileLogPath = Configuration.getCompileLogDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName()
                + "/" + shortModuleName + ".log";
        compileTplContent = compileTplContent.replaceAll(CompileTplArgs.COMPILE_LOG, compileLogPath);
    }

    private String buildCollectionLogShell() {
        String compileLogPath = Configuration.getCompileLogDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName()
                + "/" + shortModuleName + ".log";

        StringBuilder builder = new StringBuilder(128);

        builder.append(" python ").append(getScriptServerDir())
                .append(logScriptName).append(" ");
        /**
         * 传给python日志脚本的参数 :
         * [file_name/文件全路径]  [收集的超时时间] [type/日志类型:(shell|compile|deploy)]
         * [server_deploy_id/服务器发布id(当为type类型为compile/deploy时为 historyId)] [server_url/接口地址]
         * */
        builder.append(compileLogPath).append(" ").append(COLLECT_LOG_TIMEOUT).append(" compile ")
                .append(compileRequest.getHistoryId()).append(" ")
                .append(Configuration.getCollectLogUrl())
                .append(" & ");
        return builder.toString();
    }

    private String getScriptServerDir() {
        return Configuration.getCompileServerScriptDir() + compileRequest.getEnv() + "/" + compileRequest.getProjectName() + "/" + shortModuleName + "/";
    }
}
