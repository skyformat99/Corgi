package com.ibeiliao.deployment.cmd;

/**
 * 功能：执行命令的结果
 * 详细：
 *
 * @author linyi, 2017/2/15.
 */
public class CommandResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 成功日志
     */
    private String message;

    private int exitValue;

    private String host;

    /**
     * 错误日志
     */
    private String errorMessage;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
