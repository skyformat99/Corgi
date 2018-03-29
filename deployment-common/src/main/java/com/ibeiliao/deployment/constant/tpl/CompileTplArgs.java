package com.ibeiliao.deployment.constant.tpl;

/**
 * 详情 : 编译模板的参数
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/27
 */
public interface CompileTplArgs {

    /**
     * 编译错误日志文件路径
     */
    String MODULE_ERR_LOG = "\\$\\{MODULE_ERR_LOG\\}";

    /**
     * 日志文件路径
     */
    String LOG_FILE_DIR = "\\$\\{LOG_FILE_DIR\\}";

    /**
     * 分支路径(svn checkout的目录)
     */
    String BRANCH_DIR = "\\$\\{BRANCH_DIR\\}";

    /**
     * svn/git checkout 的命令
     */
    String CHECKOUT_SHELL = "\\$\\{CHECKOUT_SHELL\\}";

    /**
     * svn日志文件目录
     */
    String SVN_LOG_DIR = "\\$\\{SVN_LOG_DIR\\}";

    /**
     * 模块名称
     */
    String MODULE_NAME = "\\$\\{MODULE_NAME\\}";

    /**
     * 编译服务器 编译文件存放的目录
     */
    String COMPILED_FILE_DIR = "\\$\\{COMPILED_FILE_DIR\\}";

    /**
     * 项目日志文件目录
     */
    String PROJECT_LOG_DIR = "\\$\\{PROJECT_LOG_DIR\\}";

    /**
     * 编译日志路径
     */
    String COMPILE_LOG = "\\$\\{COMPILE_LOG\\}";

    /**
     * python收集日志的shell
     */
    String PYTHON_COLLECT_LOG = "\\$\\{PYTHON_COLLECT_LOG\\}";

    /**
     * 编译脚本
     */
    String MVN_SHELL = "\\$\\{MVN_SHELL\\}";

    /**
     * mvn 拷贝的shell
     */
    String MVN_CP_SHELL = "\\$\\{MVN_CP_SHELL\\}";

    /**
     * 编译项目目录
     */
    String COMPILE_PROJECT_DIR = "\\$\\{COMPILE_PROJECT_DIR\\}";

    /**
     * 要压缩成的tar.gz的文件名
     */
    String MODULE_TAR_FILE = "\\$\\{MODULE_TAR_FILE\\}";
    /**
     * 项目目录
     */
    String PROJECT_DIR = "\\$\\{PROJECT_DIR\\}";
    /**
     * 分支归档的tar文件
     */
    String BRANCH_TAR_FILE = "\\$\\{BRANCH_TAR_FILE\\}";
    /**
     *  tar文件的mod5文件
     */
    String BRANCH_MD5_FILE = "\\$\\{BRANCH_MD5_FILE\\}";
    /**
     * 分支的短名 (除开branches/ tags/)
     */
    String SHORT_BRANCH_NAME = "\\$\\{SHORT_BRANCH_NAME\\}";

    /**
     * 分支类型: branches tags ,trunk分支的则为 ""
     */
    String BRANCH_TYPE_NAME = "\\$\\{BRANCH_TYPE_NAME\\}";

    /**
     *  是否强制编译 (1 是, 0 不是),  强制编译的情况下 即使存在编译好的tar文件依旧要执行编译
     */
    String FORCE_COMPILE = "\\$\\{FORCE_COMPILE\\}";

    /**
     *  repo类型 svn-1 git-2
     */
    String REPO_TYPE = "\\$\\{REPO_TYPE\\}";

    /**
     * 编译服务器存储的编译好的文件地址
     */
    String LOCAL_STORAGE_DIR = "\\$\\{LOCAL_STORAGE_DIR\\}";

    /**
     * 保存在编译服务器本地的编译好的文件名称（跟OSS文件名字一致）
     */
    String SAVE_FILE_NAME = "\\$\\{SAVE_FILE_NAME\\}";

}
