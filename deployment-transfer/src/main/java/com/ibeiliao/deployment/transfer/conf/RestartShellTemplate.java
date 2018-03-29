package com.ibeiliao.deployment.transfer.conf;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.common.util.ShellTemplateFileUtil;
import com.ibeiliao.deployment.constant.tpl.StartupTplArgs;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 详情 : 重启脚本模板类
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/3/28
 */
public class RestartShellTemplate {

    private static final Logger logger = LoggerFactory.getLogger(RestartShellTemplate.class);

    private String shortModuleName;

    private TransferRequest request;

    private String startupTplContents;

    private boolean isStop;

    public RestartShellTemplate(String shortModuleName, TransferRequest request, boolean isStop) {

        this.shortModuleName = shortModuleName;
        this.request = request;
        this.isStop = isStop;

        startupTplContents = ShellTemplateFileUtil.getStartupShellTpl();
        Assert.hasText(startupTplContents, "启动脚本模板没有信息");
    }

    /**
     * 生成restart脚本的文件
     */
    public String generateRestartShellFile() throws IOException {
        String restartShellFilePath = FileUtils.getTempDirectoryPath() + "/restart_" + shortModuleName + ".sh";
        FileWriter writer = null;

        replaceStartupTplArgs();

        try {
            writer = new FileWriter(restartShellFilePath);
            // 1. 初始化基础参数
            String restartShell = StringUtils.trimToEmpty(request.getRestartShell());
            if (ModuleUtil.isMainClass(restartShell)) {

                writer.write(startupTplContents);

            } else {
                writer.write("#!/bin/bash\n");
                writer.write("source /etc/profile\n");
                if (isStop) {
                    writer.write(StringUtils.trimToEmpty(request.getStopShell()));
                } else {
                    writer.write(restartShell);
                }
            }
        } catch (IOException e) {
            logger.error("生成模块 {} 的重启脚本失败, {}", shortModuleName, e);
            throw e;
        } finally {
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
        return restartShellFilePath;
    }

    private void replaceStartupTplArgs() {
        String projectDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/";
        String modulePidFile = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/"
                + shortModuleName + "/" + shortModuleName + ".pid";
        startupTplContents = startupTplContents
                .replaceAll(StartupTplArgs.PROJECT_DIR, projectDir)
                .replaceAll(StartupTplArgs.MAIN_CLASS, request.getRestartShell())
                .replaceAll(StartupTplArgs.JVM_ARGS, request.getJvmArgs())
                .replaceAll(StartupTplArgs.MODULE_NAME, shortModuleName)
                .replaceAll(StartupTplArgs.MODULE_PID_FILE, modulePidFile);
    }
}
