package com.ibeiliao.deployment.constant.tpl;

/**
 * 详情 : 发布启动模板的参数
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/27
 */
public interface DeployTplArgs {

    /**
     * 基础项目目录
     * @see "ansible.upload.file.dir"
     */
    String BASE_PROJECT_DIR = "\\$\\{BASE_PROJECT_DIR\\}";

    /**
     * resin文件名
     */
    String RESIN_XML_FILE = "\\$\\{RESIN_XML_FILE\\}";

    /**
     * 模块名字
     */
    String MODULE_NAME = "\\$\\{MODULE_NAME\\}";


    /**
     * 发布错误日志文件路径
     */
    String PROJECT_NAME = "\\$\\{PROJECT_NAME\\}";

    /**
     * 发布错误日志文件路径
     */
    String MODULE_ERR_LOG = "\\$\\{MODULE_ERR_LOG\\}";


    /**
     * 日志收集脚本
     */
    String LOG_FILE_DIR = "\\$\\{LOG_FILE_DIR\\}";
    /**
     * 日志收集脚本
     */
    String PYTHON_COLLECT_LOG = "\\$\\{PYTHON_COLLECT_LOG\\}";

    /**
     * 模块发布类型
     */
    String DEPLOY_TYPE = "\\$\\{DEPLOY_TYPE\\}";

    /**
     * 发布历史的id
     */
    String HISTORY_ID = "\\$\\{HISTORY_ID\\}";

    /**
     * 回滚版本的发布历史id
     */
    String ROLL_BACK_ID = "\\$\\{ROLL_BACK_ID\\}";

    /**
     * 模块备份目录
     */
    String BACKUP_DIR = "\\$\\{BACKUP_DIR\\}";

    /**
     * 项目备份目录
     */
    String PRO_BACKUP_DIR = "\\$\\{PRO_BACKUP_DIR\\}";
    /**
     * 模块目录
     */
    String MODULE_DIR = "\\$\\{MODULE_DIR\\}";

    /**
     * 项目目录 （针对静态项目）
     */
    String PROJECT_DIR = "\\$\\{PROJECT_DIR\\}";
    /**
     * pid文件路径
     */
    String PID_FILE = "\\$\\{PID_FILE\\}";
    /**
     * 模块tar.gz文件
     */
    String MODULE_TAR_FILE = "\\$\\{MODULE_TAR_FILE\\}";
    /**
     * 启动前shell
     */
    String PRE_SHELL = "\\$\\{PRE_SHELL\\}";
    /**
     * 启动前shell
     */
    String HAS_PRESHELL = "\\$\\{HAS_PRESHELL\\}";
    /**
     * 启动shell
     */
    String RESTART_SHELL = "\\$\\{RESTART_SHELL\\}";
    /**
     * stop shell
     */
    String STOP_SHELL = "\\$\\{STOP_SHELL\\}";
    /**
     * 启动后shell
     */
    String POST_SHELL = "\\$\\{POST_SHELL\\}";
    /**
     * 启动后shell
     */
    String HAS_POSTSHELL = "\\$\\{HAS_POSTSHELL\\}";

    /**
     * module 是web项目的标识 1代表是
     */
    String WEB_PROJECT_FLAG = "\\$\\{WEB_PROJECT_FLAG\\}";

    /**
     * web模块 tar了oss文件 + web配置文件(比如resin)后的tar包文件路径
     */
    String MODULE_OSS_CONF_TAR = "\\$\\{MODULE_OSS_CONF_TAR\\}";

    /**
     * web 模块的resin配置文件路径
     */
    String RESIN_CONF_DIR = "\\$\\{RESIN_CONF_DIR\\}";

    /**
     * web 模块生成的配置文件 ( f1\|f2\|f3)
     */
    String CONFIG_FILE_LIST = "\\$\\{CONFIG_FILE_LIST\\}";

    /**
     * gc 日志的目录
     */
    String GC_LOG_DIR = "\\$\\{GC_LOG_DIR\\}";

    /**
     * resin access log 目录
     */
    String RESIN_ACCESS_LOG_DIR = "\\$\\{RESIN_ACCESS_LOG_DIR\\}";

    /**
     * 环境
     */
    String ENV = "\\$\\{ENV\\}";

}
