package com.ibeiliao.deployment.transfer.vo;

import java.io.Serializable;

/**
 * 功能：模块配置
 * 详细：
 *
 * @author linyi, 2017/3/15.
 */
public class ModuleConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目代号
     */
    private String projectNo;

    /**
     * 模块名
     */
    private String shortModuleName;

    /**
     * jvm参数
     */
    private String jvmArg;

    /**
     * 在 pom.xml 里的 finalName
     */
    private String moduleFinalName;

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getShortModuleName() {
        return shortModuleName;
    }

    public void setShortModuleName(String shortModuleName) {
        this.shortModuleName = shortModuleName;
    }

    public String getJvmArg() {
        return jvmArg;
    }

    public void setJvmArg(String jvmArg) {
        this.jvmArg = jvmArg;
    }

    public String getModuleFinalName() {
        return moduleFinalName;
    }

    public void setModuleFinalName(String moduleFinalName) {
        this.moduleFinalName = moduleFinalName;
    }
}
