package com.ibeiliao.deployment.transfer.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.util.FileCompressUtil;
import com.ibeiliao.deployment.transfer.conf.DeployShellTemplate;
import com.ibeiliao.deployment.transfer.enums.DeployType;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 功能 : 下载oss文件到本地然后批量传输到目标服务器
 * <p>
 * 详细 :
 *
 * @author liangguanglong
 */
public class StaticTransferService extends AbstractTransferService {

    private static final Logger logger = LoggerFactory.getLogger(StaticTransferService.class);

    public StaticTransferService(TransferRequest transferRequest) {
        super(transferRequest, DeployType.DEPLOY);
    }

    @Override
    protected void doPush2Server() {

        transferAllScript2Server();

        pushPackageToServers();
    }

    @Override
    protected void afterPush2Server() {

    }

    private void transferAllScript2Server() {
        logger.info("开始传输脚本到目标服务器");
        //1.日志脚本
        String logFilePath = StaticTransferService.class.getResource("/").getPath() + "shell/" + LOG_SCRIPT_NAME;
        //2.发布脚本
        String shellFilePath = generateShellFile();

        if (checkIsAllFail()) {
            return;
        }

        List<String> allScriptLocalPath = Lists.newArrayList(logFilePath, shellFilePath);

        String tarFilePath = FileUtils.getTempDirectoryPath() + "/transfer_" + shortModuleName + ".tar";
        boolean tarResult = FileCompressUtil.archive(allScriptLocalPath, tarFilePath);

        unTarShell2Server(tarFilePath, tarResult);

        result.setSetupShellPath(getScriptServerDir() + "setup_" + shortModuleName + ".sh");

        FileUtils.deleteQuietly(new File(tarFilePath));
        FileUtils.deleteQuietly(new File(shellFilePath));
    }

    private void pushPackageToServers() {
        if (checkIsAllFail()) {
            return;
        }
        logger.info("开始推送文件&shell到目标服务器");

        // ansible copy 模块批量传输 resin文件和下载的包 到目标服务器
        transPackageToServer();

        // 执行shell : 依次包括备份 解压 启动项目
        executeShell();

    }

    private String generateShellFile() {

        String deployShellFilePth = null;

        DeployShellTemplate deployShellTemplate = new DeployShellTemplate(shortModuleName, request, false, deployType.getName());
        try {
            deployShellFilePth = deployShellTemplate.generateDeployShellFile();
        } catch (IOException e) {
            result.setSuccessType(DeployResult.FAILURE);
        }
        return deployShellFilePth;
    }

    private void transPackageToServer() {
        if (checkIsAllFail() || !isNewDeploy()) {
            return;
        }
        logger.info("开始传输OSS文件到目标服务器");
        String serverUploadDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/";

        String hostFilePath = generateHostFile();

        String[] args = new String[]{"ansible", "-i", hostFilePath, "all", "-m", "copy", "-a", "src=" + result.getDownloadFileName() + " dest=" + serverUploadDir};
        logger.info("传输OSS文件的ansible:" + StringUtils.join(args, " "));

        execAnsibleCommand(args);

        FileUtils.deleteQuietly(new File(hostFilePath));
        FileUtils.deleteQuietly(new File(result.getDownloadFileName()));
    }

}
