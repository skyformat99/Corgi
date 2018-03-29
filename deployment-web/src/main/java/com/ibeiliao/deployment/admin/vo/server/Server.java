package com.ibeiliao.deployment.admin.vo.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务器表
 * 
 */
public class Server implements Serializable {

	private static final long serialVersionUID = -3074457347398955213L;

	/** 服务器ID */
	private int serverId;

	/** serverName */
	private String serverName;

	/** ip地址 */
	private String ip;

	/** 创建时间 */
	private Date createTime;

	/** 所在的服务器组 */
	private int groupId;

	/**
	 * 负载信息
	 */
	private String loadInfo;

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getLoadInfo() {
		return loadInfo;
	}

	public void setLoadInfo(String loadInfo) {
		this.loadInfo = loadInfo;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
