package com.ibeiliao.deployment.admin.enums;

/**
 * 详情 : 服务器发布策略
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/5/10
 */
public enum  ServerStrategy {

    STOP_WITH_ERR((short) 1, "只要有失败就停止发布"),
    IGNORE_ERROR((short) 2, "不管是否有失败，都继续"),
    ;

    private short value;

    /**
     * 描述
     */
    private String name;

    ServerStrategy(short value, String name) {
        this.name = name;
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static ServerStrategy from(short value) {
        for (ServerStrategy strategy : ServerStrategy.values()) {
            if (strategy.getValue() == value) {
                return strategy;
            }
        }
        return null;
    }
}
