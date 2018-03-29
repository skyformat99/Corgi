package com.ibeiliao.deployment.admin.po.deploy;

import java.io.Serializable;
import java.util.Date;


/**
 * 服务器发布shell日志记录
 * 
 * <pre>
 *     自动生成代码: 表名 t_server_deploy_log, 日期: 2017-01-25
 *     log_id <PK>               int(11)
 *     server_deploy_id    int(11)
 *     shell_log           varchar(1000)
 *     create_time         datetime(19)
 * </pre>
 */
public class ServerDeployLogPO implements Serializable {

	private static final long serialVersionUID = -3074457346500987008L;

	/** logId */
	private int logId;

	/** 服务器部署记录的id，对应的是t_server_deploy_history的主键 */
	private int serverDeployId;

	/** shell 日志 */
	private String shellLog;

	/** 插入时间 */
	private Date createTime;

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getLogId() {
		return logId;
	}

	public void setServerDeployId(int serverDeployId) {
		this.serverDeployId = serverDeployId;
	}

	public int getServerDeployId() {
		return serverDeployId;
	}

	public void setShellLog(String shellLog) {
		this.shellLog = shellLog;
	}

	public String getShellLog() {
		return shellLog;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

}
