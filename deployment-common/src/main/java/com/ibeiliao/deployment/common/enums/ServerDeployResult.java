package com.ibeiliao.deployment.common.enums;

/**
 * 功能：服务器发布状态
 * 详细：
 *
 * @author linyi, 2017/1/22.
 */
public enum ServerDeployResult {
    SUCCESS((short)1),
    FAILURE((short)2),
    /**
     * 等待部署
     */
    WAITING_FOR_DEPLOYMENT((short)3),
    ;

    private short value;

    ServerDeployResult(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

}
