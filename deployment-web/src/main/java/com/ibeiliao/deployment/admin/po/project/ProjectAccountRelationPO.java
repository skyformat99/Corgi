package com.ibeiliao.deployment.admin.po.project;

import java.io.Serializable;


/**
 * 项目和成员的关系表
 * 
 * <pre>
 *     自动生成代码: 表名 t_project_account_relation, 日期: 2017-01-12
 *     project_id <PK>     int(11)
 *     account_id <PK>     int(11)
 *     is_admin      tinyint(4)
 * </pre>
 */
public class ProjectAccountRelationPO implements Serializable {

	private static final long serialVersionUID = -3074457346800986927L;

	/** 项目ID */
	private int projectId;

	/** 用户ID */
	private long accountId;

	/** 是否为管理员，0不是，1是 ，一个项目至少有一个管理员 */
	private short isAdmin = 0;

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setIsAdmin(short isAdmin) {
		this.isAdmin = isAdmin;
	}

	public short getIsAdmin() {
		return isAdmin;
	}

}
