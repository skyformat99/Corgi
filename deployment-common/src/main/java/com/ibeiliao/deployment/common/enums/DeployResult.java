package com.ibeiliao.deployment.common.enums;

/**
 * 功能：
 * 详细：
 *
 * @author linyi, 2017/1/23.
 */
public enum DeployResult {
    NONE((short)0),

    /**
     * 全部成功
     */
    SUCCESS((short)1),
    /**
     * 部分成功
     */
    PARTIAL_SUCCESS((short)2),

    /**
     * 全部失败
     */
    FAILURE((short)3);

    private short value;

    DeployResult(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
