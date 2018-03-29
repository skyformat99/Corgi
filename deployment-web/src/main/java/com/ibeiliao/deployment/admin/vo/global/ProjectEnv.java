package com.ibeiliao.deployment.admin.vo.global;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 环境配置表
 * 
 */
public class ProjectEnv {

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

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
