package com.ibeiliao.deployment.compile.service;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandUtil;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.util.FileCompressUtil;
import com.ibeiliao.deployment.common.util.ModuleUtil;
import com.ibeiliao.deployment.compile.vo.CompileRequest;
import com.ibeiliao.deployment.compile.vo.CompileResult;
import com.ibeiliao.deployment.compile.vo.CompileShellTemplate;
import com.ibeiliao.deployment.log.PushLogService;
import com.ibeiliao.deployment.storage.FileStorageUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 详情 : 编译处理的
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/5/9
 */
public abstract class AbstractCompiler {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractCompiler.class);


    protected static final String LOG_SCRIPT_NAME = "monitor_shell_log_upload.py";

    protected static final String OSS_SCRIPT_NAME = "oss_upload.py";


    protected CompileRequest request;

    protected CompileResult result = new CompileResult();

    protected PushLogService pushLogService;

    protected int historyId;

    /**
     * 缩短后的模块名，比如 services/xxx-impl 变成 xxx-impl
     */
    protected String shortModuleName;

    /**
     * oss file key
     */
    protected String saveFileName;

    public AbstractCompiler(CompileRequest compileRequest, PushLogService pushLogService) {
        Assert.notNull(compileRequest);
        Assert.isTrue(compileRequest.getProjectId() > 0, "projectId没有设置");
        Assert.isTrue(compileRequest.getModuleId() > 0, "mobuleId没有设置");
        Assert.notNull(pushLogService, "pushLogService没有设置");
        Assert.hasText(compileRequest.getModuleName(), "moduleName 没有设置");
        Assert.hasText(compileRequest.getEnv(), "env 没有设置");
        Assert.hasText(compileRequest.getTagName(), "tagname 没有设置");
        Assert.hasText(compileRequest.getSvnUserName(), "svn/git帐号 没有设置");
        Assert.hasText(compileRequest.getSvnPassword(), "svn/git密码 没有设置");
        Assert.hasText(compileRequest.getSvnAddr(), "svn地址 没有设置");
        Assert.hasText(compileRequest.getVersion(), "版本号没有设置");

        this.request = compileRequest;
        this.historyId = this.request.getHistoryId();
        this.pushLogService = pushLogService;
        this.shortModuleName = ModuleUtil.getShortModuleName(request.getModuleName());
        this.saveFileName = FileStorageUtil.getSaveFileName(request.getProjectId(), request.getModuleId(),
                request.getTagName(), request.getEnv(), request.getVersion());
    }

    /**
     * 执行编译打包的操作
     */
    public CompileResult compileModule() {

        beforeCompile();

        doCompile();

        afterCompile();

        return result;
    }

    protected void beforeCompile() {
        writeStep("开始编译打包");

        preparedDir();

        transferScriptToServer();
    }

    protected abstract void doCompile();

    protected void afterCompile() {
        if (!result.isCompileSuccess()) {
            return;
        }
        //executeOssScript();

        //writeStep("编译打包步骤完成，success: " + result.isCompileSuccess());
    }

    /**
     * 在服务器上建立相应的目录
     */
    protected void preparedDir() {
        writeStep("创建相关目录");

        String allScriptDir = getScriptServerDir();

        List<String> needBuildDirs = Lists.newArrayList("mkdir -p " + allScriptDir);

        String[] args = {"-i", Configuration.getAnsibleHostFile(), "all", "-m", "shell", "-a", StringUtils.join(needBuildDirs, " && ")};

        CommandUtil.execAnsible(args);
    }

    protected void transferScriptToServer() {
        if (!result.isCompileSuccess()) {
            return;
        }
        long beginTime = System.currentTimeMillis();

        writeStep("传输编译相关脚本到服务器");
        //1.日志收集脚本
        String logFilePath = JavaCompiler.class.getResource("/").getPath() + "shell/" + LOG_SCRIPT_NAME;
        //2.编译脚本
        String compileShellFilePath = generateCompileShell();
        //3.oss脚本
        String ossScriptFilePath = JavaCompiler.class.getResource("/").getPath() + "shell/" + OSS_SCRIPT_NAME;

        ArrayList<String> allScriptFilePath = Lists.newArrayList(logFilePath, compileShellFilePath, ossScriptFilePath);
        String tarFilePath = FileUtils.getTempDirectoryPath() + "/" + request.getEnv() + "_" + shortModuleName + ".tar";

        boolean tarResult = FileCompressUtil.archive(allScriptFilePath, tarFilePath);
        result.setCompileSuccess(tarResult);

        if (tarResult) {
            String[] args = {"-i", Configuration.getAnsibleHostFile(), "all", "-m", "unarchive", "-a", "src=" + tarFilePath + " dest=" + getScriptServerDir() + " mode=755"};
            CommandUtil.execAnsible(args);
        } else {
            writeStep("传输编译相关脚本到服务器失败");
        }

        logger.info("传输解压耗时 :" + String.valueOf(System.currentTimeMillis() - beginTime));
        FileUtils.deleteQuietly(new File(tarFilePath));
        FileUtils.deleteQuietly(new File(compileShellFilePath));
    }

    protected String generateCompileShell() {
        if (!result.isCompileSuccess()) {
            return "";
        }
        logger.info("开始生成编译脚本文件");

        CompileShellTemplate compileTemplateCreator = new CompileShellTemplate(shortModuleName, request, LOG_SCRIPT_NAME);
        String compileShellFile = null;
        try {
            compileShellFile = compileTemplateCreator.generate();
            writeStep("生成执行编译的shell成功");
        } catch (IOException e) {
            result.setCompileSuccess(false);
            writeStep("生成执行编译的shell失败: " + e.getMessage());
        }

        return compileShellFile;
    }

    protected void writeStep(String message) {
        pushLogService.writeStep(historyId, message);
    }

    private void executeOssScript() {
        // 传递 OSS file key (发布的historyId) 给 python脚本
        // python脚本接收的参数 :
        //[file_path/文件全路径]  [save_name/保存至oss的key名称] [access_key_id] [access_key_secret] [endpoint] [bucket]


        logger.info("compileSaveFileName : " + saveFileName);
        String ossShell = "python " + getScriptServerDir() + OSS_SCRIPT_NAME + " " +
                result.getCompiledFileName() + " " +
                saveFileName + " " +
                Configuration.getOssAccessKeyId() + " " +
                Configuration.getOssAccessSecret() + " " +
                Configuration.getOssEndpoint() + " " +
                Configuration.getOssBucket() + " ";
        // 添加删除文件
        ossShell = ossShell + " && rm -rf " + result.getCompiledFileName();
        String[] args = {"-i", Configuration.getAnsibleHostFile(), "all", "-m", "shell", "-a",
                ossShell};
        AnsibleCommandResult ansibleResult = CommandUtil.execAnsible(args);
        boolean execOssScriptSuccess = (ansibleResult.getSuccessType() == DeployResult.SUCCESS);

        result.setCompileSuccess(execOssScriptSuccess);
        if (!execOssScriptSuccess) {
            logger.error("执行oss脚本失败, {}", ansibleResult.getErrorMessage());
            writeStep("上传文件到OSS失败！");
        } else {
            result.setSaveFileName(saveFileName);
            writeStep("上传文件到OSS成功");
        }
    }

    protected String getScriptServerDir() {
        return Configuration.getCompileServerScriptDir() + request.getEnv() + "/" + request.getProjectName() + "/" + shortModuleName + "/";
    }
}
