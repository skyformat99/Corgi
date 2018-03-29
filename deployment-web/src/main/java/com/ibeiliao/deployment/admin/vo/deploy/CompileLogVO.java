package com.ibeiliao.deployment.admin.vo.deploy;

import java.util.List;

/**
 * 详情 : 编译日志信息
 * <p>
 * 详细 :
 *
 * @author liangguanglong 2017/8/16
 */
public class CompileLogVO {

    /**
     * 日志信息
     */
    private List<String> logs;

    /**
     * 偏移量
     */
    private int offset;

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
