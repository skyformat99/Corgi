package com.ibeiliao.deployment.compile.service;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandUtil;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.compile.vo.CompileRequest;
import com.ibeiliao.deployment.log.PushLogService;

/**
 * 功能 :  静态项目的打包编译
 * <p>
 * 详细 : 步骤
 *
 * @author liangguanglong
 */
public class StaticFileCompiler extends AbstractCompiler {

    public StaticFileCompiler(CompileRequest compileRequest, PushLogService pushLogService) {
        super(compileRequest, pushLogService);
    }

    @Override
    protected void doCompile() {
        executePackageShell(getScriptServerDir() + "/compile_" + shortModuleName + ".sh");
    }

    private String getMvnTargetProjectDir() {
        return Configuration.getCompilePackagedir() + request.getEnv() + "/" + request.getProjectName() + "/";
    }

    private void executePackageShell(String compileShellFilePath) {
        if (!result.isCompileSuccess()) {
            return;
        }
        logger.info("开始进行打包");
        long startTime = System.currentTimeMillis();
        String[] args = {"-i", Configuration.getAnsibleHostFile(), "all", "-m", "shell", "-a", " sh " + compileShellFilePath};
        AnsibleCommandResult ansibleResult = CommandUtil.execAnsible(args);
        logger.info("打包返回的结果是 :" + ansibleResult.getMessage());
        boolean executeCompileSuccess = (ansibleResult.getSuccessType() == DeployResult.SUCCESS);
        result.setCompileSuccess(executeCompileSuccess);
        if (!executeCompileSuccess) {
            String tmpMessage = ansibleResult.getMessage();
            if (tmpMessage != null && tmpMessage.length() > 2048) {
                tmpMessage = tmpMessage.substring(0, 1900) + " ...... " + tmpMessage.substring(tmpMessage.length() - 100);
            }
            String message = "打包失败: " + tmpMessage;
            writeStep(message);
        } else {
            String message = "打包成功，耗时 " + (System.currentTimeMillis() - startTime) + " ms";
            writeStep(message);
        }

        if (result.isCompileSuccess()) {
            // 返回最终生成的tar.gz 文件 的路径
            result.setCompiledFileName(getMvnTargetProjectDir() + shortModuleName + ".tar.gz");
        }
    }

}
