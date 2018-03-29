package com.ibeiliao.deployment.admin.vo.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 服务器组
 */
public class ServerGroup implements Serializable {

    private static final long serialVersionUID = -3074457345486784402L;

    /**
     * 组ID
     */
    private int groupId;

    /**
     * 组名
     */
    private String groupName = "";

    /**
     * 环境id
     */
    private int envId;

    /**
     * 所属环境的名字
     */
    private String envName;

    /**
     * 所属模块id
     */
    private int moduleId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 服务器组下的服务器
     */
    private List<Server> servers;

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public int getEnvId() {
        return envId;
    }

    public void setEnvId(int envId) {
        this.envId = envId;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
