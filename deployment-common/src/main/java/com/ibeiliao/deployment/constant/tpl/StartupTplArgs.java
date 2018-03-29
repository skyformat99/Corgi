package com.ibeiliao.deployment.constant.tpl;

/**
 * 详情 : dubbo服务自动生成的启动模板的参数
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/27
 */
public interface StartupTplArgs {

    /**
     * 模块日志目录
     */
    String MODULE_LOG_DIR = "\\$\\{MODULE_LOG_DIR\\}";

    /**
     * 项目目录
     */
    String PROJECT_DIR = "\\$\\{PROJECT_DIR\\}";
    /**
     * 启动类名
     */
    String MAIN_CLASS = "\\$\\{MAIN_CLASS\\}";
    /**
     * jvm参数
     */
    String JVM_ARGS = "\\$\\{JVM_ARGS\\}";
    /**
     * 模块名称
     */
    String MODULE_NAME = "\\$\\{MODULE_NAME\\}";
    /**
     * 模块pid文件目录
     */
    String MODULE_PID_FILE = "\\$\\{MODULE_PID_FILE\\}";

}
