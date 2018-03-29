package com.ibeiliao.deployment.transfer.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.ModuleType;
import com.ibeiliao.deployment.common.util.FileCompressUtil;
import com.ibeiliao.deployment.common.util.JvmArgUtil;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.transfer.conf.DeployShellTemplate;
import com.ibeiliao.deployment.transfer.conf.ResinConfTemplate;
import com.ibeiliao.deployment.transfer.conf.RestartShellTemplate;
import com.ibeiliao.deployment.transfer.enums.DeployType;
import com.ibeiliao.deployment.transfer.vo.ModuleConf;
import com.ibeiliao.deployment.transfer.vo.TransferRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能 : 下载oss文件到本地然后批量传输到目标服务器
 * <p>
 * 详细 :
 *
 * @author liangguanglong
 */
public class JavaTransferService extends AbstractTransferService {

    private static final Logger logger = LoggerFactory.getLogger(JavaTransferService.class);

    public JavaTransferService(TransferRequest transferRequest) {
        this(transferRequest, DeployType.DEPLOY);
    }

    public JavaTransferService(TransferRequest transferRequest, DeployType deployType) {
        super(transferRequest, deployType);

        if (transferRequest.getRollBackDeployId() > 0) {
            deployType = DeployType.ROLLBACK;
        }
        if (deployType == DeployType.STOP) {
            if (!ModuleUtil.isMainClass(request.getRestartShell())) {
                Assert.hasText(request.getStopShell(), "stop脚本没有设置");
            }
        } else if (deployType == DeployType.STOP) {
            Assert.hasText(request.getRestartShell(), "restart脚本没有设置");
        }

        enhanceJvmArg();
    }

    @Override
    protected void doPush2Server() {


        transferAllScript2Server();

        if (request.getRollBackDeployId() > 0) {
            logger.info("回滚" + request.getRollBackDeployId() + "版本");
        }

        if (!isRollBack()) {
            pushPackageToServers();
        }

        // 执行shell : 依次包括备份 解压 启动项目
        executeShell();
    }

    @Override
    protected void afterPush2Server() {

    }

    private void transferAllScript2Server() {
        logger.info("开始传输脚本到目标服务器");
        //1.日志脚本
        String logFilePath = JavaTransferService.class.getResource("/").getPath() + "shell/" + LOG_SCRIPT_NAME;
        //2.dubbo 启动脚本
        String dubboStartupShellPath = generateModuleRestartShell();
        //3.发布脚本
        String shellFilePath = generateShellFile();

        if (checkIsAllFail()) {
            return;
        }

        List<String> allScriptLocalPath = Lists.newArrayList(logFilePath, shellFilePath);
        //3.dubbo 启动脚本
        if (StringUtils.isNotBlank(dubboStartupShellPath)) {
            allScriptLocalPath.add(dubboStartupShellPath);
        }

        String taredFilePath = FileUtils.getTempDirectoryPath() + "/transfer_" + shortModuleName + ".tar";
        boolean localTarResult = FileCompressUtil.archive(allScriptLocalPath, taredFilePath);

        unTarShell2Server(taredFilePath, localTarResult);

        result.setSetupShellPath(getScriptServerDir() + "setup_" + shortModuleName + ".sh");

        FileUtils.deleteQuietly(new File(taredFilePath));
        FileUtils.deleteQuietly(new File(shellFilePath));
        if (StringUtils.isNotBlank(dubboStartupShellPath)) {
            FileUtils.deleteQuietly(new File(dubboStartupShellPath));
        }
    }

    private void enhanceJvmArg() {
        // 增加默认的 JVM 参数
        Map<String, String> params = new HashMap<>();
        params.put(JvmArgUtil.GC_LOG, JvmArgUtil.GC_LOG + ModuleUtil.getGcLogFile(request.getProjectName(), shortModuleName));

        String jvmArgs = JvmArgUtil.enhanceArg(request.getJvmArgs(), params);

        request.setJvmArgs(JvmArgUtil.addProfileArgs(jvmArgs, request.getEnv()));
    }

    private void pushPackageToServers() {
        if (checkIsAllFail()) {
            return;
        }
        logger.info("开始推送文件&shell到目标服务器");

        // 生成resin配置文件
        List<String> resinConfFiles = generateResinConf();

        // ansible copy 模块批量传输 resin文件和下载的包 到目标服务器
        transPackageToServer(resinConfFiles);

    }

    private List<String> generateResinConf() {
        if (request.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
            ModuleConf moduleConf = new ModuleConf();
            moduleConf.setProjectNo(request.getProjectName());
            moduleConf.setJvmArg(request.getJvmArgs());
            moduleConf.setShortModuleName(shortModuleName);
            moduleConf.setModuleFinalName(request.getModuleFinalName());

            ResinConfTemplate template = new ResinConfTemplate(request.getResinConf(), moduleConf);
            try {
                return template.createConfFiles(FileUtils.getTempDirectoryPath());
            } catch (IOException e) {
                logger.info("生成resin配置文件失败, {}", e);
                result.setSuccessType(DeployResult.FAILURE);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private String generateShellFile() {

        String deployShellFilePth = null;

        DeployShellTemplate deployShellTemplate = new DeployShellTemplate(shortModuleName, request, isStop(), deployType.getName());
        try {
            deployShellFilePth = deployShellTemplate.generateDeployShellFile();
        } catch (IOException e) {
            result.setSuccessType(DeployResult.FAILURE);
        }
        return deployShellFilePth;
    }

    private void transPackageToServer(List<String> resinConfFiles) {
        if (checkIsAllFail() || !isNewDeploy()) {
            return;
        }
        if (request.getModuleType() == ModuleType.WEB_PROJECT.getValue() && CollectionUtils.isEmpty(resinConfFiles)) {
            return;
        }
        logger.info("开始传输OSS文件到目标服务器");
        String serverUploadDir = Configuration.getAnsibleUploadFileDir() + request.getProjectName() + "/";

        String hostFilePath = generateHostFile();

        String[] args;
        String tarFileLocalPath = null;
        if (request.getModuleType() == ModuleType.WEB_PROJECT.getValue()) {
            // web服务的将resin文件 + OSS文件打包发送
            tarFileLocalPath = tarResinAndOss(resinConfFiles);
            if (checkIsAllFail()) {
                return;
            }
            logger.info("将resin配置文件和oss文件打包发送 ： " + tarFileLocalPath);
            args = new String[]{"ansible", "-i", hostFilePath, "all", "-m", "unarchive", "-a", "src=" + tarFileLocalPath + " dest=" + serverUploadDir + shortModuleName + "/"};
        } else {
            logger.info("发送dubbo文件");
            // dubbo服务的直接上传oss文件 (解压模块比传输模块多耗3S,所以dubbo服务不做解压)
            args = new String[]{"ansible", "-i", hostFilePath, "all", "-m", "copy", "-a", "src=" + result.getDownloadFileName() + " dest=" + serverUploadDir};
            logger.info("传输OSS文件的ansible:" + StringUtils.join(args, " "));
        }

        logger.info("resin和oss压缩过后的文件是：" + tarFileLocalPath);

        execAnsibleCommand(args);

        FileUtils.deleteQuietly(new File(hostFilePath));
        FileUtils.deleteQuietly(new File(result.getDownloadFileName()));
        if (StringUtils.isNotBlank(tarFileLocalPath)) {
            FileUtils.deleteQuietly(new File(tarFileLocalPath));
        }
    }

    private String tarResinAndOss(List<String> resinConfFiles) {
        resinConfFiles.add(result.getDownloadFileName());
        String tarFilePath = FileUtils.getTempDirectoryPath() + "/" + getOssResinTarFile();
        boolean tarResult = FileCompressUtil.archive(resinConfFiles, tarFilePath);
        if (tarResult) {
            return tarFilePath;
        } else {
            result.setSuccessType(DeployResult.FAILURE);
            return null;
        }
    }

    private String getOssResinTarFile() {
        return "oss_resin_" + shortModuleName + "_" + request.getEnv() + ".tar";
    }

    /**
     * 针对dubbo服务生成的启动脚本
     */
    private String generateModuleRestartShell() {

        if (request.getModuleType() != ModuleType.SERVICE.getValue()) {
            return null;
        }

        RestartShellTemplate shellTemplate = new RestartShellTemplate(shortModuleName, request, isStop());

        String restartShellFilePath = null;
        try {
            restartShellFilePath = shellTemplate.generateRestartShellFile();
        } catch (IOException e) {
            result.setSuccessType(DeployResult.FAILURE);
        }

        return restartShellFilePath;
    }

}
