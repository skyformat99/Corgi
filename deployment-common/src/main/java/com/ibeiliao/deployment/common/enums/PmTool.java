package com.ibeiliao.deployment.common.enums;

/**
 * 功能：项目管理工具定义
 * 详细：
 *
 * @author linyi, 2017/3/15.
 */
public enum PmTool {
    /**
     * 无
     */
    NONE((short)0),
    /**
     * apache maven
     */
    MAVEN((short)1);

    private short value;

    private PmTool(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public static PmTool from(short value) {
        if (value == MAVEN.getValue()) {
            return MAVEN;
        }
        return NONE;
    }
}
