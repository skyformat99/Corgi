package com.ibeiliao.deployment.cmd;

import com.ibeiliao.deployment.common.enums.DeployResult;

import java.util.Map;

/**
 * 详情 : ansible执行的返回结果
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/17
 */
public class AnsibleCommandResult extends CommandResult {

    /**
     * 成功类型
     *
     * @see com.ibeiliao.deployment.common.enums.DeployResult
     */
    private DeployResult successType;

    /**
     * 执行ansible的结果,key:IP value: 是否成功
     */
    private Map<String,Boolean> ip2ResultMap;

    /**
     * 执行失败的ip以及对应的失败日志
     */
    private Map<String,String> ip2FailLogMap;

    public DeployResult getSuccessType() {
        return successType;
    }

    public void setSuccessType(DeployResult successType) {
        this.successType = successType;
    }

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
}
