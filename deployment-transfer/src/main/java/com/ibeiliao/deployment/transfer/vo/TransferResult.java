package com.ibeiliao.deployment.transfer.vo;

import com.ibeiliao.deployment.common.enums.DeployResult;

import java.util.Map;

/**
 * 详情 : 传输文件结果
 *
 * @author liangguanglong
 */
public class TransferResult {

    /**
     * 成功类型 : 1-全部成功 2-部分成功 3-全部失败
     *
     * @see com.ibeiliao.deployment.common.enums.DeployResult
     */
    private DeployResult successType = DeployResult.SUCCESS;

    /**
     * 下载到本地的文件名
     */
    private String downloadFileName;

    /**
     * 失败日志记录
     */
    private String failLog;

    /**
     * 启动脚本在远程服务器完整路径,内容包括(备份 解压 部署启动)
     */
    private String setupShellPath;

    /**
     * 执行ansible的结果,key:IP value: 是否成功
     */
    private Map<String,Boolean> ip2ResultMap;

    /**
     * 执行失败的ip以及对应的失败日志
     */
    private Map<String,String> ip2FailLogMap;


     public Map<String, Boolean> getIp2ResultMap() {
        return ip2ResultMap;
    }

    public void setIp2ResultMap(Map<String, Boolean> ip2ResultMap) {
        this.ip2ResultMap = ip2ResultMap;
    }

    public Map<String, String> getIp2FailLogMap() {
        return ip2FailLogMap;
    }

    public void setIp2FailLogMap(Map<String, String> ip2FailLogMap) {
        this.ip2FailLogMap = ip2FailLogMap;
    }

    public DeployResult getSuccessType() {
        return successType;
    }

    public void setSuccessType(DeployResult successType) {
        this.successType = successType;
    }

    public String getSetupShellPath() {
        return setupShellPath;
    }

    public void setSetupShellPath(String setupShellPath) {
        this.setupShellPath = setupShellPath;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public String getFailLog() {
        return failLog;
    }

    public void setFailLog(String failLog) {
        this.failLog = failLog;
    }
}
