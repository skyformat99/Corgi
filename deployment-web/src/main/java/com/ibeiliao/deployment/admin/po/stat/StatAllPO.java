package com.ibeiliao.deployment.admin.po.stat;

import java.io.Serializable;
import java.util.Date;


/**
 * 总的发布次数，根据deploy_time统计，未开始发布和result/stop的不算
 * 
 * <pre>
 *     自动生成代码: 表名 t_stat_all, 日期: 2017-02-27
 *     stat_id <PK>          int(11)
 *     stat_date       date(10)
 *     env_id          int(11)
 *     deploy_times    int(11)
 *     success         int(11)
 *     failure         int(11)
 * </pre>
 */
public class StatAllPO implements Serializable {

	private static final long serialVersionUID = -3074457347348194842L;

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

}
