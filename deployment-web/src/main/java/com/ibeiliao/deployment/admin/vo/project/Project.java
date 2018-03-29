package com.ibeiliao.deployment.admin.vo.project;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 项目
 */
public class Project implements Serializable {

    private static final long serialVersionUID = -3074457347621482076L;

    /**
     * projectId
     */
    private int projectId;

    /**
     * 项目名称，全局唯一，不能重名
     */
    @NotBlank(message = "项目名称不能为空")
    @Length(min = 1, max = 30, message = "项目名称的长度是1~30个字符")
    private String projectName;

    /**
     * 项目管理员ID
     */
    private long managerId;

    /**
     * 管理员名称
     */
    private String managerName = "";

    /**
     * 管理员email
     */
    private String managerEmail = "";

    /**
     * 管理员电话
     */
    private String managerPhone = "";

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 项目编号
     */
    @NotBlank(message = "项目代号不能为空")
    @Length(min = 1, max = 30, message = "项目代号的长度是1~30个字符")
    private String projectNo;

    /**
     * 项目的后台编程语言
     */
    @NotBlank(message = "项目语言不能为空")
    private String programLanguage;

    /**
     * 参与者名称,用','隔开
     */
    private String joinerNames;

    /**
     * 项目账号关联关系，仅用于保存数据
     */
    private List<ProjectAccountRelation> projectAccountRelations;

    /**
     * 项目模块
     */
    private List<ProjectModule> projectModules;

    /**
     * 负责人，多个用逗号隔开
     */
    private String managers;

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

    public String getJoinerNames() {
        return joinerNames;
    }

    public void setJoinerNames(String joinerNames) {
        this.joinerNames = joinerNames;
    }

    public List<ProjectAccountRelation> getProjectAccountRelations() {
        return projectAccountRelations;
    }

    public void setProjectAccountRelations(List<ProjectAccountRelation> projectAccountRelations) {
        this.projectAccountRelations = projectAccountRelations;
    }

    public List<ProjectModule> getProjectModules() {
        return projectModules;
    }

    public void setProjectModules(List<ProjectModule> projectModules) {
        this.projectModules = projectModules;
    }

    public String getManagers() {
        return managers;
    }

    public void setManagers(String managers) {
        this.managers = managers;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
