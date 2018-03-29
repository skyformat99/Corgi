package com.ibeiliao.deployment.compile.constant;

/**
 * 详情 : 编译环节报错的类型
 *
 * @author liangguanglong
 */
public enum CompileFailType {

    /**
     * 传输日志收集脚本出错
     */
    TRANS_LOG_SCRIPT((short) 1),

    /**
     * 创建编译shell脚本出错
     */
    CREATE_COMPILE_SHELL((short) 2),

    /**
     * 传输编译的shell脚本出错
     */
    TRANS_COMPILE_SHELL((short) 3),

    /**
     * 编译脚本执行出错
     */
    EXC_COMPILE_SHELL((short) 4),

    /**
     * 传输到OSS 出错
     */
    TRAN_TO_OSS((short) 5),

    /**
     * 传输oss shell脚本到目标服务器出错
     */
    TRANS_OSS_SHELL((short) 3),
    ;

    private short value;

    CompileFailType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
