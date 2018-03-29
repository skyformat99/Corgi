package com.ibeiliao.deployment.compile.vo;

/**
 * 详情 : 每次的编译请求参数
 *
 * @author liangguanglong
 */
public class CompileRequest {

    /**
     * 发布上线单的id
     */
    private int historyId;

    /**
     * 编译脚本
     */
    private String compileShell;

    /**
     * module名称
     */
    private String moduleName;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 分支名: 类似  tag/20170101_test  branch/20170202_test
     */
    private String tagName;

    /**
     * 该module的svn地址
     */
    private String svnAddr;

    /**
     * 所属环境
     */
    private String env;

    /**
     * svn 账户
     */
    private String svnUserName;

    /**
     * svn 密码
     */
    private String svnPassword;

    /**
     * 发布的版本号
     */
    private String version;

    /**
     * 模块id
     */
    private int moduleId;

    /**
     * 项目ID
     */
    private int projectId;

    /**
     * 仓库类型，svn -1 git-2
     *
     * @see com.ibeiliao.deployment.common.enums.ModuleRepoType
     */
    private short repoType;

    /**
     * 是否强制编译 (强制编译的情况,即使存在编译好的文件,也要进行重新编译的操作)
     */
    private short forceCompile;

    /**
     * 模块类型：dubbo服务、web 项目、静态
     * @see com.ibeiliao.deployment.common.enums.ModuleType
     */
    private short moduleType;

    /**
     * 编程语言，用来判断是否是静态项目
     */
    private String language;

    public short getRepoType() {
        return repoType;
    }

    public void setRepoType(short repoType) {
        this.repoType = repoType;
    }

    public short getForceCompile() {
        return forceCompile;
    }

    public void setForceCompile(short forceCompile) {
        this.forceCompile = forceCompile;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public String getSvnUserName() {
        return svnUserName;
    }

    public void setSvnUserName(String svnUserName) {
        this.svnUserName = svnUserName;
    }

    public String getSvnPassword() {
        return svnPassword;
    }

    public void setSvnPassword(String svnPassword) {
        this.svnPassword = svnPassword;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }


    public String getSvnAddr() {
        return svnAddr;
    }

    public void setSvnAddr(String svnAddr) {
        this.svnAddr = svnAddr;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getCompileShell() {
        return compileShell;
    }

    public void setCompileShell(String compileShell) {
        this.compileShell = compileShell;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public short getModuleType() {
        return moduleType;
    }

    public void setModuleType(short moduleType) {
        this.moduleType = moduleType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
