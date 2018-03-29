package com.ibeiliao.deployment.admin.po.project;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能：模块配置
 * 详细：
 *
 * @author linyi, 2017/3/14.
 */
public class ModuleConfPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
    private int moduleId;

    /**
     * 配置类型, 见 Constants.CONF_TYPE_xxxx
     */
    private int confType;

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

    public int getConfType() {
        return confType;
    }

    public void setConfType(int confType) {
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
