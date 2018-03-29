package com.ibeiliao.deployment.admin.po.account;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 管理员操作日志
 * 
 * <pre>
 *     自动生成代码: 表名 mz_admin_operation_log, 日期: 2015-08-03
 *     log_id <PK>             int(11)
 *     game_id           int(11)
 *     platform_id       int(11)
 *     server_id         int(11)
 *     server_code       varchar(30)
 *     uid               bigint(20)
 *     operation_type    varchar(30)
 *     arguments         varchar(512)
 *     result            varchar(255)
 *     create_time       datetime(19)
 *     ext1              varchar(64)
 *     ext2              varchar(64)
 * </pre>
 */
public class AdminOperationLogPO implements Serializable {

	private static final long serialVersionUID = -3074457344093415866L;

	/** logId */
	private int logId;

	/** 管理员ID */
	private long uid;

	/** 操作类型 */
	private String operationType;

	/** 参数 */
	private String arguments = "";

	/** 执行结果 */
	private String result = "";

	/** 创建时间 */
	private Date createTime;

	/** 扩展字段1 */
	private String ext1 = "";

	/** 扩展字段2 */
	private String ext2 = "";

	/**
	 * 操作者IP
	 */
	private String ip;

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getLogId() {
		return logId;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return uid;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getArguments() {
		return arguments;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public String getExt1() {
		return ext1;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public String getExt2() {
		return ext2;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
