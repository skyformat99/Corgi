package com.ibeiliao.deployment.admin.po.deploy;

import com.ibeiliao.deployment.common.enums.ServerDeployResult;

import java.io.Serializable;
import java.util.Date;


/**
 * t_server_deploy_history
 * 
 * <pre>
 *     自动生成代码: 表名 t_server_deploy_history, 日期: 2017-01-12
 *     id <PK>                  int(11)
 *     history_id         int(11)
 *     server_id          int(11)
 *     last_history_id    int(11)
 *     status             tinyint(4)
 * </pre>
 */
public class ServerDeployHistoryPO implements Serializable {

	private static final long serialVersionUID = -3074457347091210987L;

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

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Date getStartupTime() {
		return startupTime;
	}

	public void setStartupTime(Date startupTime) {
		this.startupTime = startupTime;
	}
}
