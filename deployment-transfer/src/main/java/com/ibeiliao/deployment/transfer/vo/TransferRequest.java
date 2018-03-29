package com.ibeiliao.deployment.transfer.vo;

import com.ibeiliao.deployment.common.enums.ModuleType;

import java.util.List;

/**
 * 详情 : 传输请求
 *
 * @author liangguanglong
 */
public class TransferRequest {

    /**
     * oss 要下载的文件 key
     */
    private String saveFileName;

    /**
     * 目标部署服务器ip
     */
    private List<String> targetServerIps;

    /**
     * 发布历史的id
     */
    private int historyId;

    /**
     * 回滚版本的发布历史id
     */
    private int rollBackDeployId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 环境
     */
    private String env;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 部署前执行的脚本
     */
    private String preDeployShell;

    /**
     * 部署启动执行的脚本 (当为独立进程的项目时 为启动的Main类路径)
     */
    private String restartShell;

    /**
     * 停止服务的脚本
     */
    private String stopShell;

    /**
     * 部署后执行的脚本
     */
    private String postDeployShell;

    /**
     * JAVA_OPT 参数,用于设置jvm参数 (java 独立进程的项目特有)
     */
    private String jvmArgs;

    /**
     * 模块类型，1代表web项目 2代表dubbo服务
     */
    private short moduleType = ModuleType.WEB_PROJECT.getValue();

    /**
     * 模块最终编译的名字，只针对 MAVEN 项目，moduleType = ModuleType.WEB_PROJECT 时有效
     */
    private String moduleFinalName;

    /**
     * Resin 配置，moduleType = ModuleType.WEB_PROJECT 时有效
     */
    private ResinConf resinConf;

    /**
     * 是否重新生成 resin 配置
     */
    private boolean createResinConf;

    /**
     * 编程语言
     */
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public short getModuleType() {
        return moduleType;
    }

    public void setModuleType(short moduleType) {
        this.moduleType = moduleType;
    }

    public String getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getPreDeployShell() {
        return preDeployShell;
    }

    public void setPreDeployShell(String preDeployShell) {
        this.preDeployShell = preDeployShell;
    }

    public String getRestartShell() {
        return restartShell;
    }

    public void setRestartShell(String restartShell) {
        this.restartShell = restartShell;
    }

    public String getPostDeployShell() {
        return postDeployShell;
    }

    public void setPostDeployShell(String postDeployShell) {
        this.postDeployShell = postDeployShell;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getStopShell() {
        return stopShell;
    }

    public void setStopShell(String stopShell) {
        this.stopShell = stopShell;
    }

    public List<String> getTargetServerIps() {
        return targetServerIps;
    }

    public void setTargetServerIps(List<String> targetServerIps) {
        this.targetServerIps = targetServerIps;
    }

    public String getModuleFinalName() {
        return moduleFinalName;
    }

    public void setModuleFinalName(String moduleFinalName) {
        this.moduleFinalName = moduleFinalName;
    }

    public ResinConf getResinConf() {
        return resinConf;
    }

    public void setResinConf(ResinConf resinConf) {
        this.resinConf = resinConf;
    }

    public boolean isCreateResinConf() {
        return createResinConf;
    }

    public void setCreateResinConf(boolean createResinConf) {
        this.createResinConf = createResinConf;
    }

    public int getRollBackDeployId() {
        return rollBackDeployId;
    }

    public void setRollBackDeployId(int rollBackDeployId) {
        this.rollBackDeployId = rollBackDeployId;
    }
}
