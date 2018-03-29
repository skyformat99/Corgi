package com.ibeiliao.deployment.admin.vo.project;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 项目和成员的关系表
 * 
 */
public class ProjectAccountRelation implements Serializable {

	private static final long serialVersionUID = -3074457343628119715L;

	/** 项目ID */
	private int projectId;

	/** 用户ID */
	private long accountId;

	/** 是否为管理员，0不是，1是 ，一个项目至少有一个管理员 */
	private short isAdmin = 0;

	/**
	 * 用户真实名称
	 */
	private String realName;

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

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

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
