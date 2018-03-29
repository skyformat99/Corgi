package com.ibeiliao.deployment.admin.vo.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务器发布shell日志记录
 * 
 */
public class ServerDeployLog implements Serializable {

	private static final long serialVersionUID = -3074457347213024114L;

	/** logId */
	private int logId;

	/** 服务器部署记录的id，对应的是t_server_deploy_history的主键 */
	private int serverDeployId;

	/** shell 日志 */
	private String shellLog;

	/** 插入时间 */
	private Date createTime;

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getLogId() {
		return logId;
	}

	public void setServerDeployId(int serverDeployId) {
		this.serverDeployId = serverDeployId;
	}

	public int getServerDeployId() {
		return serverDeployId;
	}

	public void setShellLog(String shellLog) {
		this.shellLog = shellLog;
	}

	public String getShellLog() {
		return shellLog;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
