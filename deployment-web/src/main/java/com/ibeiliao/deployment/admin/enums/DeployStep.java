package com.ibeiliao.deployment.admin.enums;

/**
 * 功能: 发布步骤说明
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/7
 */
public enum DeployStep {

    COMPILING("编译中..."), COMPILE_COMPLETE("编译完成"),
    PACKAGING("正在打包..."),
    SAVING_TO_OSS("保存到OSS中..."), SAVE_TO_OSS_COMPLETE("保存到OSS成功"),
    DOWNLOADING_FROM_OSS("从OSS下载发布包..."), DOWNLOAD_FROM_OSS_COMPLETE("从OSS下载发布成功"),
    SENDING_TO_TARGET_SERVER("发送至目标机器中..."), SEND_TO_TARGET_SERVER_COMPLETE("发送至目标机器完成"),
    BANKUPS_ORIGINAL_PACKAGE_COMPLETE("备份原始发布包成功"),
    EXECUTE_PRE_SHELL("执行发布前脚本"),
    EXECUTE_STOP_SHELL("停止服务"),
    EXECUTE_RESTART_SHELL("重启服务"),
    EXECUTE_POST_SHELL("执行发布后脚本"),
    DEPLOY_COMPLETE("发布完成"),

    DEPLOY_FAILURE("发布失败")


    ;


    /**
     * 发布步骤名称
     */
    private String name;

    private DeployStep(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
