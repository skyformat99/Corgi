package com.ibeiliao.deployment.admin.vo.project;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能：模块配置
 * 详细：
 *
 * @author linyi, 2017/3/14.
 */
public class ModuleConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
    private int moduleId;

    /**
     * 配置类型,1-resin, 2-tomcat, 3-其他
     */
    private short confType;

    /**
     * 配置，json格式
     */
    private String confValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public short getConfType() {
        return confType;
    }

    public void setConfType(short confType) {
        this.confType = confType;
    }

    public String getConfValue() {
        return confValue;
    }

    public void setConfValue(String confValue) {
        this.confValue = confValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
