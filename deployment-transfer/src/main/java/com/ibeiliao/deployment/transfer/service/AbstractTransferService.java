package com.ibeiliao.deployment.transfer.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.cmd.AnsibleCommand;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandResult;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.storage.FileStorageUtil;
import com.ibeiliao.deployment.storage.ProjectFileStorage;
import com.ibeiliao.deployment.storage.ProjectFileStorageFactory;
import com.ibeiliao.deployment.transfer.enums.DeployType;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import com.ibeiliao.deployment.transfer.vo.TransferResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 详情 : 传输服务的抽象类
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/5/9
 */
public abstract class AbstractTransferService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTransferService.class);


    protected static ProjectFileStorage projectFileStorage;

    protected static final String LOG_SCRIPT_NAME = "monitor_shell_log_upload.py";

    protected TransferRequest request;

    protected TransferResult result = new TransferResult();

    /**
     * 缩短后的模块名，比如 services/xxx-impl 变成 xxx-impl
     */
    protected String shortModuleName;

    protected DeployType deployType;

    static {
        projectFileStorage = ProjectFileStorageFactory.getInstance();
    }

    public AbstractTransferService(TransferRequest transferRequest, DeployType deployType) {
        if (transferRequest.getRollBackDeployId() > 0) {
            this.deployType = DeployType.ROLLBACK;
        } else {
            this.deployType = deployType;
        }
        request = transferRequest;
        Assert.notNull(request);
        Assert.notNull(deployType);

        Assert.hasText(request.getModuleName(), "模块名称不能为空");
        shortModuleName = ModuleUtil.getShortModuleName(transferRequest.getModuleName());
    }

    /**
     * 处理推送
     */
    public TransferResult pushPackageToServer() {
        if (!isRollBack()) {
            beforePush();
        }

        doPush2Server();

        afterPush2Server();

        return result;
    }

    private void beforePush() {

        downloadCompileFile();

        prepareServerDirs();
    }

    protected abstract void doPush2Server();

    protected abstract void afterPush2Server();

    private void downloadCompileFile() {
        if (isNewDeploy()) {
            try {
                File file = buildDownloadFile();
                logger.info("下载到本地文件: " + file.getAbsolutePath());

                projectFileStorage.download(request.getSaveFileName(), file);
                result.setDownloadFileName(file.getAbsolutePath());

            } catch (Exception e) {
                result.setFailLog("下载OSS文件报错, 文件名是 :" + request.getSaveFileName());
                result.setSuccessType(DeployResult.FAILURE);
                logger.error("下载OSS文件报错 : {}", request.getSaveFileName(), e);
            }
        }
    }

    private File buildDownloadFile() {
        String downloadDir = Configuration.getOssFileDownloadDir() + request.getEnv() + "/";
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullPathFileName = downloadDir + shortModuleName + FileStorageUtil.EXT;
        File file = new File(fullPathFileName);
        if (file.exists()) {
            file.delete();
            return new File(fullPathFileName);
        }
        return file;
    }

    private void prepareServerDirs() {
        if (checkIsAllFail()) {
            return;
        }

        String shellUploadDir = Configuration.getAnsibleUploadShellDir() + request.getProjectName() + "/" + shortModuleName + "/";
        String serverUploadDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/" + shortModuleName + "/";

        List<String> needBuildDirs = Lists.newArrayList("mkdir -p " + shellUploadDir, "mkdir -p " + serverUploadDir);

        String hostFile = generateHostFile();
        String[] args = {"-i", hostFile, "all", "-m", "shell", "-a", StringUtils.join(needBuildDirs, " && ")};

        execAnsibleCommand(args);

        FileUtils.deleteQuietly(new File(hostFile));
    }

    protected void execAnsibleCommand(String[] args) {
        logger.info("执行的ansible命令 " + StringUtils.join(args, " "));

        AnsibleCommand ansibleCommand = new AnsibleCommand();
        CommandResult commandResult = ansibleCommand.exec(args);
        AnsibleCommandResult ansibleResult = AnsibleCommand.parse(commandResult.getMessage(), request.getTargetServerIps());

        if (result.getSuccessType() == DeployResult.SUCCESS) {
            result.setSuccessType(ansibleResult.getSuccessType());
        }
        if (result.getSuccessType() == DeployResult.PARTIAL_SUCCESS) {
            if (ansibleResult.getSuccessType() != DeployResult.SUCCESS) {
                result.setSuccessType(ansibleResult.getSuccessType());
            }
        }

        if (result.getIp2FailLogMap() == null) {
            result.setIp2ResultMap(ansibleResult.getIp2ResultMap());
        } else {
            if (ansibleResult.getIp2ResultMap() != null) {
                result.getIp2ResultMap().putAll(ansibleResult.getIp2ResultMap());
            }
        }

        Map<String, String> ip2FailLogMap = ansibleResult.getIp2FailLogMap();
        if (result.getIp2FailLogMap() == null) {
            result.setIp2FailLogMap(ip2FailLogMap);
        } else {
            if (ip2FailLogMap != null) {
                result.getIp2FailLogMap().putAll(ip2FailLogMap);
            }
        }
    }

    /**
     * 在部署服务器的某个目录 根据ip创建对应服务器的私钥
     * 到遇到部署1个或多个服务器的时候,生成ansible的host文件,:
     */
    public String generateHostFile() {
        List<String> successIps = getSuccessIps();

        String hostFileName = "";
        FileWriter writer = null;
        try {
            File tmpFile = File.createTempFile("host_", request.getEnv() + "_" + request.getHistoryId());
            writer = new FileWriter(tmpFile);
            writer.write("[all]\n");
            // host文件只有ip地址.( 不同服务器统一采用web用户进行登录处理,私钥公用同一个,所以不需要配置)
            writer.write(StringUtils.join(successIps, "\n"));
            writer.flush();

            return tmpFile.getAbsolutePath();
        } catch (IOException e) {
            result.setSuccessType(DeployResult.FAILURE);
            logger.error("生成 ansible host文件 失败 : {}", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return hostFileName;
    }

    private List<String> getSuccessIps() {
        if (result.getIp2FailLogMap() == null) {
            return request.getTargetServerIps();
        }
        Set<String> failServerIps = result.getIp2FailLogMap().keySet();
        List<String> serverIps = request.getTargetServerIps();
        if (CollectionUtils.isEmpty(failServerIps)) {
            return serverIps;
        }
        List<String> successIps = Lists.newArrayList();
        for (String ip : serverIps) {
            if (!failServerIps.contains(ip)) {
                successIps.add(ip);
            }
        }
        return successIps;
    }

    protected void executeShell() {
        if (checkIsAllFail()) {
            return;
        }
        logger.info("开始执行启动脚本");
        String hostFilePath = generateHostFile();
        String[] args = {"ansible", "-i", hostFilePath, "all", "-m", "shell", "-a", "nohup sh " + result.getSetupShellPath()};
        logger.info("执行启动脚本的ansible" + StringUtils.join(args, " "));

        execAnsibleCommand(args);

        FileUtils.deleteQuietly(new File(hostFilePath));
    }

    protected boolean checkIsAllFail() {
        return result.getSuccessType() == DeployResult.FAILURE;
    }

    /**
     * 解压文件到远程服务器
     * @param taredFilePath 本地压缩好的文件 .tar 的路径
     * @param localTarResult 本地压缩的结果
     */
    protected void unTarShell2Server(String taredFilePath, boolean localTarResult) {
        if (localTarResult) {
            String hostFile = generateHostFile();
            String[] args = {"-i", hostFile, "all", "-m", "unarchive", "-a", "src=" + taredFilePath + " dest=" + getScriptServerDir() + " mode=755"};
            execAnsibleCommand(args);
            FileUtils.deleteQuietly(new File(hostFile));
        } else {
            result.setSuccessType(DeployResult.FAILURE);
        }
    }

    protected   String getScriptServerDir() {
        return Configuration.getAnsibleUploadShellDir() + request.getProjectName() + "/" + shortModuleName + "/";
    }

    public boolean isNewDeploy() {
        return deployType == DeployType.DEPLOY;
    }

    public boolean isRestart() {
        return deployType == DeployType.RESTART;
    }

    public boolean isStop() {
        return deployType == DeployType.STOP;
    }

    public boolean isRollBack() {
        return request.getRollBackDeployId() > 0;
    }
}
