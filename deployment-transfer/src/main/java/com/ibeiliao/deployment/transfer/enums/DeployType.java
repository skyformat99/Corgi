package com.ibeiliao.deployment.transfer.enums;

/**
 * 详情 :  发布类型
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/5/10
 */
public enum DeployType {

    DEPLOY("deploy"), // 发布

    RESTART("restart"), // 重启

    ROLLBACK("rollBack"), // 回滚

    STOP("stop");  // 停止

    /**
     * 发布类型名称
     */
    private String name;

    DeployType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
