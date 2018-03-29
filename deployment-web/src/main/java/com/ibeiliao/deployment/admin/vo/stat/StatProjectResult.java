package com.ibeiliao.deployment.admin.vo.stat;

import java.io.Serializable;

/**
 * 功能：project统计的中间结果
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
public class StatProjectResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private int projectId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * 发布结果，见 {@link com.ibeiliao.deployment.common.enums.DeployResult}
     */
    private short result;

    /**
     * 数量
     */
    private int num;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getEnvId() {
        return envId;
    }

    public void setEnvId(int envId) {
        this.envId = envId;
    }

    public short getResult() {
        return result;
    }

    public void setResult(short result) {
        this.result = result;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
