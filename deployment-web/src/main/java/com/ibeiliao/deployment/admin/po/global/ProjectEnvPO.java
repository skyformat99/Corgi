package com.ibeiliao.deployment.admin.po.global;

import java.io.Serializable;


/**
 * 环境配置表
 * 
 * <pre>
 *     自动生成代码: 表名 t_project_env, 日期: 2017-01-13
 *     env_id <PK>       int(11)
 *     env_name    varchar(40)
 * </pre>
 */
public class ProjectEnvPO implements Serializable {

	private static final long serialVersionUID = -3074457347656971803L;

	/** envId */
	private int envId;

	/** 环境名称，dev test pre online */
	private String envName;

	/**
	 * 线上环境标识 0 不是  1是
	 */
	private int onlineFlag;


	public int getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(int onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getEnvName() {
		return envName;
	}

}
