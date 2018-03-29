package com.ibeiliao.deployment.compile.service;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandUtil;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.compile.vo.CompileRequest;
import com.ibeiliao.deployment.log.PushLogService;

/**
 * 功能 :  java项目的编译(包含编译模块 传输到OSS)
 * <p>
 * 详细 : 步骤
 * 1. 创建相关目录
 * 2. 传输相关脚本
 * 3. 执行编译脚本
 * 4. 执行oss脚本进行传输
 *
 * @author liangguanglong
 */
public class JavaCompiler extends AbstractCompiler {

    public JavaCompiler(CompileRequest compileRequest, PushLogService pushLogService) {
        super(compileRequest,pushLogService);
    }

    @Override
    protected void doCompile() {
        executeCompileShell(getScriptServerDir() + "/compile_" + shortModuleName + ".sh");
    }

    private String getMvnTargetProjectDir() {
        return Configuration.getCompilePackagedir() + request.getEnv() + "/" + request.getProjectName() + "/";
    }

    private void executeCompileShell(String compileShellFilePath) {
        if (!result.isCompileSuccess()) {
            return;
        }
        logger.info("开始执行编译脚本");
        writeStep("开始编译项目 ... <a href=\"/admin/deploy/compileLog.xhtml?historyId=" + historyId + "\" target=\"_blank\">查看日志</a>");
        long startTime = System.currentTimeMillis();
        String[] args = {"-i", Configuration.getAnsibleHostFile(), "all", "-m", "shell", "-a", " sh " + compileShellFilePath};
        AnsibleCommandResult ansibleResult = CommandUtil.execAnsible(args);
        logger.info("编译返回的结果是 :" + ansibleResult.getMessage());
        boolean executeCompileSuccess = (ansibleResult.getSuccessType() == DeployResult.SUCCESS);
        result.setCompileSuccess(executeCompileSuccess);
        logger.info("执行完编译脚本,结果是 : {}", executeCompileSuccess);
        if (!executeCompileSuccess) {
            String tmpMessage = ansibleResult.getMessage();
            if (tmpMessage != null && tmpMessage.length() > 2048) {
                tmpMessage = tmpMessage.substring(0, 1900) + " ...... " + tmpMessage.substring(tmpMessage.length() - 100);
            }
            String message = "编译失败: " + tmpMessage;
            logger.error(message);
            writeStep(message);
        } else {
            String message = "编译成功，耗时 " + (System.currentTimeMillis() - startTime) + " ms";
            logger.info(message);
            writeStep(message);
        }

        if (result.isCompileSuccess()) {
            // 返回最终生成的tar.gz 文件 的路径
            result.setCompiledFileName(getMvnTargetProjectDir() + shortModuleName + ".tar.gz");
        }
    }
}
