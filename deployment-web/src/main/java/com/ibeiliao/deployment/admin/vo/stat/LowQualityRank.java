package com.ibeiliao.deployment.admin.vo.stat;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 最差发布质量排行，只统计生产环境
 * 
 */
public class LowQualityRank implements Serializable {

	private static final long serialVersionUID = -3074457346469118493L;

	/** rankId */
	private int rankId;

	/** 统计日期 */
	private Date statDate;

	/** 模块ID */
	private int moduleId;

	/** 成功发布次数，失败的不计算在内 */
	private int deployTimes;

	/**
	 * 模块名称，仅用于显示
	 */
	private String moduleName;

	public void setRankId(int rankId) {
		this.rankId = rankId;
	}

	public int getRankId() {
		return rankId;
	}

	public void setStatDate(Date statDate) {
		this.statDate = statDate;
	}

	public Date getStatDate() {
		return statDate;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setDeployTimes(int deployTimes) {
		this.deployTimes = deployTimes;
	}

	public int getDeployTimes() {
		return deployTimes;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
