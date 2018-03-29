package com.ibeiliao.deployment.admin.vo.deploy;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 功能：创建上线单的内容
 * 详细：
 *
 * @author linyi, 2017/1/17.
 */
public class DeploymentOrder implements Serializable {

    /** 发布版本的标题 */
    @NotBlank(message = "标题不能为空")
    @Length(min = 1, max = 80, message = "标题的长度在1~80个字符之间")
    private String title;

    /** 管理员ID */
    private long accountId;

    /**
     * 发布者真实姓名
     */
    private String realName;

    /** 发布的tag/分支，相对地址，例如 tags/20161025，和 t_project_config.repo_url组合起来就是完整地址。如果发布trunk，这里可以为"" */
    @NotBlank(message = "分支/tag不能为空")
    @Length(min = 1, max = 60, message = "分支的长度在1~60个字符之间")
    private String tagName = "";

    /** 发布的版本号 */
    private String versionNo = "";

    /** 哪个模块部署的 */
    @Min(value = 1, message = "请选择一个模块发布")
    private int moduleId;

    /** 项目ID */
    @Min(value = 1, message = "请选择一个项目发布")
    private int projectId;

    /** 并发发布服务器百分比 */
    @Min(value = 1, message = "并发发布服务器百分比不能小于1%")
    @Max(value = 100, message = "并发发布服务器百分比不能超过100%")
    private short concurrentServerPercentage;

    /** 发布时间间隔单位是秒 */
    @Min(value = 1, message = "发布时间间隔不能小于1秒")
    @Max(value = 120, message = "发布时间间隔不能大于120秒")
    private short deployTimeInterval;

    /**
     * 服务器ID列表
     */
    @NotNull(message = "请选择服务器")
    private int[] serverId;

    /**
     * 环境ID
     */
    @Min(value = 1, message = "请选择环境")
    private int envId;

    /**
     * 是否强制编译
     */
    private short forceCompile;

    /**
     * 服务器发布策略
     * @see com.ibeiliao.deployment.admin.enums.ServerStrategy
     */
    private short serverStrategy;

    /** 回滚到的版本发布历史ID，回滚的时候设置 */
    private int rollbackToDeployId = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public short getConcurrentServerPercentage() {
        return concurrentServerPercentage;
    }

    public void setConcurrentServerPercentage(short concurrentServerPercentage) {
        this.concurrentServerPercentage = concurrentServerPercentage;
    }

    public short getDeployTimeInterval() {
        return deployTimeInterval;
    }

    public void setDeployTimeInterval(short deployTimeInterval) {
        this.deployTimeInterval = deployTimeInterval;
    }

    public int[] getServerId() {
        return serverId;
    }

    public void setServerId(int[] serverId) {
        this.serverId = serverId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getEnvId() {
        return envId;
    }

    public void setEnvId(int envId) {
        this.envId = envId;
    }

    public short getForceCompile() {
        return forceCompile;
    }

    public void setForceCompile(short forceCompile) {
        this.forceCompile = forceCompile;
    }

    public int getRollbackToDeployId() {
        return rollbackToDeployId;
    }

    public void setRollbackToDeployId(int rollbackToDeployId) {
        this.rollbackToDeployId = rollbackToDeployId;
    }

    public short getServerStrategy() {
        return serverStrategy;
    }

    public void setServerStrategy(short serverStrategy) {
        this.serverStrategy = serverStrategy;
    }
}
