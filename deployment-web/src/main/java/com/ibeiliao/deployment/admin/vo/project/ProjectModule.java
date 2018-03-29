package com.ibeiliao.deployment.admin.vo.project;

import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.enums.ModuleRepoType;
import com.ibeiliao.deployment.transfer.vo.ResinConf;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 模块详情
 *
 */
public class ProjectModule implements Serializable {

	private static final long serialVersionUID = -3074457347327928024L;

	/** moduleId */
	private int moduleId;

	/** 模块中文名称 */
	private String moduleNameZh;

	/** 模块名称 */
	private String moduleName;

	/** 模块类型，0代表web项目 1代表dubbo服务 */
	private short moduleType = 0;

	/** SVN上的目录，比如 service-impl/target/*.jar */
	private String srcPath;

	/** 发布前执行的shell */
	private String preShell = "";

	/** 发布后执行的shell */
	private String postShell = "";

	/** 日志名称，读取日志并返回 */
	private String logName = "";

	/**
     *  版本管理类型 1-svn 2-git
     *  @see com.ibeiliao.deployment.common.enums.ModuleRepoType
     */
	private short repoType = ModuleRepoType.SVN.getValue();

	/** 版本管理地址，比如：svn://a.b.com/project/tags */
	private String repoUrl;

	/** 创建时间 */
	private Date createTime;

	/** 编译脚本 */
	private String compileShell;

	/** 停止服务脚本 */
	private String stopShell = "";

	/** 重启服务脚本 */
	private String restartShell;

	/** 项目id */
	private int projectId;

	/** 是否需要审核，0不需要，1需要 */
	private short needAudit = 0;

	/** svnAccount */
	private String svnAccount;

	/** svnPassword */
	private String svnPassword;

	/**
	 * 服务器组
	 */
	private List<ServerGroup> serverGroups;

	/**
	 * 对应的各个环境的jvm参数
	 */
	private List<ModuleJvm> moduleJvms;

	/**
	 * resin模块配置
	 */
	private ResinConf resinConf;

	public List<ModuleJvm> getModuleJvms() {
		return moduleJvms;
	}

	public void setModuleJvms(List<ModuleJvm> moduleJvms) {
		this.moduleJvms = moduleJvms;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleNameZh(String moduleNameZh) {
		this.moduleNameZh = moduleNameZh;
	}

	public String getModuleNameZh() {
		return moduleNameZh;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleType(short moduleType) {
		this.moduleType = moduleType;
	}

	public short getModuleType() {
		return moduleType;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setPreShell(String preShell) {
		this.preShell = preShell;
	}

	public String getPreShell() {
		return preShell;
	}

	public void setPostShell(String postShell) {
		this.postShell = postShell;
	}

	public String getPostShell() {
		return postShell;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getLogName() {
		return logName;
	}

	public void setRepoType(short repoType) {
		this.repoType = repoType;
	}

	public short getRepoType() {
		return repoType;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCompileShell(String compileShell) {
		this.compileShell = compileShell;
	}

	public String getCompileShell() {
		return compileShell;
	}

	public void setStopShell(String stopShell) {
		this.stopShell = stopShell;
	}

	public String getStopShell() {
		return stopShell;
	}

	public void setRestartShell(String restartShell) {
		this.restartShell = restartShell;
	}

	public String getRestartShell() {
		return restartShell;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setNeedAudit(short needAudit) {
		this.needAudit = needAudit;
	}

	public short getNeedAudit() {
		return needAudit;
	}

	public void setSvnAccount(String svnAccount) {
		this.svnAccount = svnAccount;
	}

	public String getSvnAccount() {
		return svnAccount;
	}

	public void setSvnPassword(String svnPassword) {
		this.svnPassword = svnPassword;
	}

	public String getSvnPassword() {
		return svnPassword;
	}

	public List<ServerGroup> getServerGroups() {
		return serverGroups;
	}

	public void setServerGroups(List<ServerGroup> serverGroups) {
		this.serverGroups = serverGroups;
	}

	public ResinConf getResinConf() {
		return resinConf;
	}

	public void setResinConf(ResinConf resinConf) {
		this.resinConf = resinConf;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
