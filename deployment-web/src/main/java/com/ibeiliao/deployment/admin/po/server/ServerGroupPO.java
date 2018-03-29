package com.ibeiliao.deployment.admin.po.server;

import com.ibeiliao.deployment.admin.vo.server.Server;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 服务器组
 * 
 * <pre>
 *     自动生成代码: 表名 t_server_group, 日期: 2017-01-12
 *     group_id <PK>        int(11)
 *     group_name     varchar(50)
 *     status         tinyint(2)
 *     create_time    datetime(19)
 * </pre>
 */
public class ServerGroupPO implements Serializable {

	private static final long serialVersionUID = -3074457346445301856L;

	/** 组ID */
	private int groupId;

	/** 组名 */
	private String groupName = "";

	/**
	 * 环境id
	 */
	private int envId;

	/**
	 * 所在模块id
	 */
	private int moduleId;

	/** 创建时间 */
	private Date createTime;

	/**
	 * 服务器组下的服务器
	 */
	private List<Server> servers;

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}
}
