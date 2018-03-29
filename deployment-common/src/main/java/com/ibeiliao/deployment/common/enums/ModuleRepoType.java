package com.ibeiliao.deployment.common.enums;

/**
 * 详情 : 版本管理类型的常量
 *
 * @author liangguanglong
 */
public enum ModuleRepoType {
    /**
     * svn
     */
    SVN((short) 1),

    /**
     * git
     */
    GIT((short) 2);

    private short value;

    ModuleRepoType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
