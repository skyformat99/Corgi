package com.ibeiliao.deployment.transfer.constant;

/**
 * 详情 : 传输环节失败的类型
 *
 * @author liangguanglong
 */
public enum TransferFailType {
    /**
     * 下载OSS文件报错
     */
    DOWNLOAD_OSS((short) 1),

    /**
     * 生成host文件出错
     */
    GENERATE_HOST_FILE((short) 2),

    /**
     * 传输到SERVER 出错
     */
    TRAN_TO_SERVER((short) 3),

    /**
     * 生成shell文件出错
     */
    GENERATE_SHELL_FILE((short) 4),
    /**
     * 执行shell文件出错
     */
    EXEC_SHELL_FILE((short) 5);

    private short value;

    TransferFailType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
