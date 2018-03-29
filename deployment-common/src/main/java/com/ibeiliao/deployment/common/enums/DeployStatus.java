package com.ibeiliao.deployment.common.enums;

/**
 * 功能：
 * 详细：
 *
 * @author linyi, 2017/1/18.
 */
public enum DeployStatus {

    /**
     * 等待审核
     */
    WAITING_FOR_AUDIT((short)1),

    /**
     * 等待部署
     */
    WAITING_FOR_DEPLOYMENT((short)2),

    /**
     * 审核拒绝
     */
    AUDIT_REJECTED((short)3),
    /**
     * 发布取消
     */
    CANCELLED((short)4),

    /**
     * 发布中
     */
    DEPLOYING((short)5),

    /**
     * 已经发布
     * 结果看 @see {@link DeployResult}
     */
    DEPLOYED((short)10);

    private short value;

    DeployStatus(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
