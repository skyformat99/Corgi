package com.ibeiliao.deployment.admin.vo.deploy;

import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.common.enums.ServerDeployResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 发布历史
 */
public class ServerDeployHistory implements Serializable {

	private static final long serialVersionUID = -3074457344035130983L;

	/** id */
	private int id;

	/** historyId */
	private int historyId;

	/** 服务器ID */
	private int serverId;

	/**
	 * 服务器名称
	 */
	private String serverName;

	/**
	 * 服务器IP
	 */
	private String serverIp;

	/** {@link ServerDeployResult} */
	private short deployStatus;

	/**
	 * 服务启动时间
	 */
	private Date startupTime;

	/**
	 * 发布日志
	 */
	private List<ServerDeployLog> serverDeployLogs;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setDeployStatus(short deployStatus) {
		this.deployStatus = deployStatus;
	}

	public short getDeployStatus() {
		return deployStatus;
	}
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Date getStartupTime() {
		return startupTime;
	}

	public void setStartupTime(Date startupTime) {
		this.startupTime = startupTime;
	}

	public List<ServerDeployLog> getServerDeployLogs() {
		return serverDeployLogs;
	}

	public void setServerDeployLogs(List<ServerDeployLog> serverDeployLogs) {
		this.serverDeployLogs = serverDeployLogs;
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
