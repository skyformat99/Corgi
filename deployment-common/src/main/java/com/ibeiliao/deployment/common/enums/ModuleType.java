package com.ibeiliao.deployment.common.enums;

/**
 * 详情 : 模块类型的常量
 *
 * @author liangguanglong
 */
public enum ModuleType {
    /**
     * web项目
     */
    WEB_PROJECT((short) 1, "Web服务"),

    /**
     * 命令行启动的服务
     */
    SERVICE((short) 2, "独立进程"),

    /**
     * 静态项目（不需要编译的，比如前端、php的）
     */
    STATIC((short) 3, "静态项目");

    private short value;

    private String name;

    ModuleType(short value, String name) {
        this.value = value;
        this.name = name;
    }

    public short getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
