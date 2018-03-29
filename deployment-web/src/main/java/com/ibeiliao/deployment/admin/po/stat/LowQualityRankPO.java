package com.ibeiliao.deployment.admin.po.stat;

import java.io.Serializable;
import java.util.Date;


/**
 * 最差发布质量排行，只统计生产环境
 * 
 * <pre>
 *     自动生成代码: 表名 t_low_quality_rank, 日期: 2017-02-27
 *     rank_id <PK>          int(11)
 *     stat_date       date(10)
 *     module_id       int(11)
 *     deploy_times    int(11)
 * </pre>
 */
public class LowQualityRankPO implements Serializable {

	private static final long serialVersionUID = -3074457347031179381L;

	/** rankId */
	private int rankId;

	/** 统计日期 */
	private Date statDate;

	/** 模块ID */
	private int moduleId;

	/** 成功发布次数，失败的不计算在内 */
	private int deployTimes;

	/**
	 * 模块名称，仅用于显示，不入库
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
}
