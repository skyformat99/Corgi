package com.ibeiliao.deployment.admin.po.deploy;

import com.ibeiliao.deployment.common.enums.DeployResult;

import java.io.Serializable;
import java.util.Date;


/**
 * 发布历史
 * 
 * <pre>
 *     自动生成代码: 表名 t_deploy_history, 日期: 2017-01-12
 *     history_id <PK>                       int(11)
 *     title                           varchar(80)
 *     create_time                     datetime(19)
 *     account_id                      int(11)
 *     deploy_time                     datetime(19)
 *     result                          tinyint(4)
 *     audit_time                      datetime(19)
 *     auditor_id                      int(11)
 *     tag_name                        varchar(60)
 *     version_no                      varchar(30)
 *     is_restart                      tinyint(4)
 *     is_rollback                     tinyint(4)
 *     rollback_to_deploy_id           int(11)
 *     module_id                       int(11)
 *     module_name                     varchar(40)
 *     env_id                          int(11)
 *     project_id                      int(11)
 *     deploy_servers                  int(11)
 *     success_count                   int(11)
 *     concurrent_server_percentage    tinyint(4)
 *     deploy_time_interval            smallint(11)
 * </pre>
 */
public class DeployHistoryPO implements Serializable {

	private static final long serialVersionUID = -3074457345711004188L;

	/** 版本发布历史ID */
	private int historyId;

	/** 发布版本的标题 */
	private String title;

	/** 创建时间 */
	private Date createTime;

	/** 管理员ID */
	private long accountId;

	/** 发布时间 */
	private Date deployTime;

	/**
	 * 1 是全部成功 2是部分成功 3是全部失败
	 *
	 * @see DeployResult
	 */
	private short result;

	/** 审核时间 */
	private Date auditTime;

	/** 审核人ID */
	private long auditorId;

	/** 发布的tag/分支，相对地址，例如 20161025，和 t_project_module.repo_url组合起来就是完整地址。如果发布trunk，这里可以为"" */
	private String tagName = "";

	/** 发布的版本号 */
	private String versionNo = "";

	/** 是否是重启，0不是，1是 */
	private short isRestart = 0;

	/** 本次发布是否是回滚操作 0不是  1是 */
	private short isRollback = 0;

	/** 回滚到的版本发布历史ID */
	private int rollbackToDeployId = 0;

	/** 哪个模块部署的 */
	private int moduleId;

	/** 模块名称 */
	private String moduleName;

	/** 环境 */
	private int envId;

	/** 项目ID */
	private int projectId;

	/** 发布的服务器数量 */
	private int deployServers;

	/** 发布成功的服务器数量 */
	private int successCount;

	/** 并发发布服务器百分比 */
	private short concurrentServerPercentage = 0;

	/** 发布时间间隔单位是秒 */
	private short deployTimeInterval;

	/**
	 * 发布者真实姓名
	 */
	private String realName;

	/**
	 * 是否强制编译
	 */
	private short forceCompile;

	/**
	 * 状态
	 * @see com.ibeiliao.deployment.common.enums.DeployStatus
	 */
	private short deployStatus;

	/**
     * 服务器发布策略
     * @see com.ibeiliao.deployment.admin.enums.ServerStrategy
     */
    private short serverStrategy;

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setDeployTime(Date deployTime) {
		this.deployTime = deployTime;
	}

	public Date getDeployTime() {
		return deployTime;
	}

	public void setResult(short result) {
		this.result = result;
	}

	public short getResult() {
		return result;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditorId(long auditorId) {
		this.auditorId = auditorId;
	}

	public long getAuditorId() {
		return auditorId;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setIsRestart(short isRestart) {
		this.isRestart = isRestart;
	}

	public short getIsRestart() {
		return isRestart;
	}

	public void setIsRollback(short isRollback) {
		this.isRollback = isRollback;
	}

	public short getIsRollback() {
		return isRollback;
	}

	public void setRollbackToDeployId(int rollbackToDeployId) {
		this.rollbackToDeployId = rollbackToDeployId;
	}

	public int getRollbackToDeployId() {
		return rollbackToDeployId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getModuleId() {
		return moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setDeployServers(int deployServers) {
		this.deployServers = deployServers;
	}

	public int getDeployServers() {
		return deployServers;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setConcurrentServerPercentage(short concurrentServerPercentage) {
		this.concurrentServerPercentage = concurrentServerPercentage;
	}

	public short getConcurrentServerPercentage() {
		return concurrentServerPercentage;
	}

	public void setDeployTimeInterval(short deployTimeInterval) {
		this.deployTimeInterval = deployTimeInterval;
	}

	public short getDeployTimeInterval() {
		return deployTimeInterval;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public short getForceCompile() {
		return forceCompile;
	}

	public void setForceCompile(short forceCompile) {
		this.forceCompile = forceCompile;
	}

	public short getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(short deployStatus) {
		this.deployStatus = deployStatus;
	}

	public short getServerStrategy() {
		return serverStrategy;
	}

	public void setServerStrategy(short serverStrategy) {
		this.serverStrategy = serverStrategy;
	}
}
