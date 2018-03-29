package com.ibeiliao.deployment.admin.po.stat;

import java.io.Serializable;
import java.util.Date;


/**
 * 项目的发布次数统计，根据deploy_time统计，未开始发布和result/stop的不算
 * 
 * <pre>
 *     自动生成代码: 表名 t_stat_project, 日期: 2017-02-27
 *     stat_id <PK>          int(11)
 *     stat_date       date(10)
 *     env_id          int(11)
 *     project_id      int(11)
 *     deploy_times    int(11)
 *     success         int(11)
 *     failure         int(11)
 * </pre>
 */
public class StatProjectPO implements Serializable {

	private static final long serialVersionUID = -3074457346836685382L;

	/** statId */
	private int statId;

	/** 统计日期 */
	private Date statDate;

	/** 环境ID */
	private int envId;

	/** 项目ID */
	private int projectId;

	/** 发布次数=success+failure */
	private int deployTimes;

	/** 成功次数 */
	private int success;

	/** 失败次数 */
	private int failure;

	/**
	 * 仅用于显示项目名称，不入库
	 */
	private String projectName;

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

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
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

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
