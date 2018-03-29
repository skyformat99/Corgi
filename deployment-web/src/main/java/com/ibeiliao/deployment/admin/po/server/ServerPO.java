package com.ibeiliao.deployment.admin.po.server;

import java.io.Serializable;
import java.util.Date;


/**
 * 服务器表
 * 
 * <pre>
 *     自动生成代码: 表名 t_server, 日期: 2017-01-12
 *     server_id <PK>       int(11)
 *     server_name    varchar(100)
 *     ip             varchar(40)
 *     status         tinyint(4)
 *     create_time    datetime(19)
 *     module_id      int(11)
 *     group_id       int(11)
 * </pre>
 */
public class ServerPO implements Serializable {

	private static final long serialVersionUID = -3074457346091342789L;

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

}
