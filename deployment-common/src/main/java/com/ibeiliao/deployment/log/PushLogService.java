package com.ibeiliao.deployment.log;

/**
 * 功能：推送日志给客户端
 * 详细：
 *
 * @author linyi, 2017/2/17.
 */
public interface PushLogService {

    /**
     * 模块发布的时候写步骤日志。
     * 注：不能同时有两个任务发布同一个模块
     * @param historyId 发布记录ID
     * @param message   日志信息
     */
    void writeStep(int historyId, String message);

    /**
     * 清除 log
     * @param historyId
     */
    void clear(int historyId);
}
