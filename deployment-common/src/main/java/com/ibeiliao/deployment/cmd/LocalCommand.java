package com.ibeiliao.deployment.cmd;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * 功能：执行本地的命令
 * 详细：
 *
 * @author linyi, 2017/2/15.
 */
public class LocalCommand implements Command {

    public static final int SUCCESS_CODE = 0;

    /**
     * 执行命令的最大timeout时间，秒
     */
    public static final int MAX_TIMEOUT = 500;

    protected static final Logger logger = LoggerFactory.getLogger(LocalCommand.class);

    private Process process;


    @Override
    public CommandResult exec(String[] cmdArray) {
        CommandResult result = new CommandResult();
        try {
            process = Runtime.getRuntime().exec(cmdArray);


            readSuccessMessage(result);
            readErrorMessage(result);
            process.waitFor(MAX_TIMEOUT, TimeUnit.SECONDS);

            if (process.isAlive()) {
                process.destroy();
            }
            result.setSuccess(getProcessExitCode() == SUCCESS_CODE);

            String message = result.getMessage();
            if (message != null && message.length() > 2048) {
                message = message.substring(0, 1900) + " ...... " + message.substring(message.length() - 100);
            }

            logger.info("执行local脚本结果, success: {}, message: {}", result.isSuccess(), message);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            logger.error("执行local脚本失败", e);
        }
        return result;
    }

    private int getProcessExitCode() throws InterruptedException {
        return process.exitValue();
    }

    protected void readSuccessMessage(CommandResult result) throws Exception {
        result.setMessage(readMessage(process.getInputStream()));
    }

    protected void readErrorMessage(CommandResult result) throws Exception {
        result.setErrorMessage(readMessage(process.getErrorStream()));
    }

    protected String readMessage(InputStream inputStream) throws IOException, InterruptedException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String s1;
            while ((s1 = reader.readLine()) != null) {
                builder.append(s1);
                System.out.println(s1);
            }
            return builder.toString();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
