package com.ibeiliao.deployment.admin.vo.stat;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 总的发布次数，根据deploy_time统计，未开始发布和result/stop的不算
 * 
 */
public class StatAll implements Serializable {

	private static final long serialVersionUID = -3074457347706663192L;

	/** statId */
	private int statId;

	/** 统计日期 */
	private Date statDate;

	/** 环境ID */
	private int envId;

	/** 发布次数=success+failure */
	private int deployTimes;

	/** 成功次数 */
	private int success;

	/** 失败次数 */
	private int failure;

	public void setStatId(int statId) {
		this.statId = statId;
	}

	public int getStatId() {
		return statId;
	}

	public void setStatDate(Date statDate) {
		this.statDate = statDate;
	}

	public Date getStatDate() {
		return statDate;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setDeployTimes(int deployTimes) {
		this.deployTimes = deployTimes;
	}

	public int getDeployTimes() {
		return deployTimes;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getSuccess() {
		return success;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public int getFailure() {
		return failure;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
