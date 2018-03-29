package com.ibeiliao.deployment.admin.po.project;

import java.io.Serializable;
import java.util.Date;


/**
 * 项目
 * 
 * <pre>
 *     自动生成代码: 表名 t_project, 日期: 2017-01-12
 *     project_id <PK>        int(11)
 *     project_name     varchar(60)
 *     manager_id       int(11)
 *     manager_name     varchar(40)
 *     manager_email    varchar(60)
 *     manager_phone    varchar(20)
 *     create_time      datetime(19)
 *     project_no       varchar(50)
 *     language         varchar(20)
 * </pre>
 */
public class ProjectPO implements Serializable {

	private static final long serialVersionUID = -3074457345128689750L;

	/** projectId */
	private int projectId;

	/** 项目名称，全局唯一，不能重名 */
	private String projectName;

	/** 项目管理员ID */
	private long managerId;

	/** 管理员名称 */
	private String managerName = "";

	/** 管理员email */
	private String managerEmail = "";

	/** 管理员电话 */
	private String managerPhone = "";

	/** 创建时间 */
	private Date createTime;

	/** 项目编号 */
	private String projectNo;

	/** 项目的后台编程语言 */
	private String programLanguage;

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}

	public long getManagerId() {
		return managerId;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerPhone(String managerPhone) {
		this.managerPhone = managerPhone;
	}

	public String getManagerPhone() {
		return managerPhone;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}

	public String getProjectNo() {
		return projectNo;
	}

	public void setProgramLanguage(String programLanguage) {
		this.programLanguage = programLanguage;
	}

	public String getProgramLanguage() {
		return programLanguage;
	}

}
