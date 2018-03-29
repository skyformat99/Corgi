package com.ibeiliao.deployment.admin.service.server;

import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.common.vo.ServerCollectLog;

import java.util.List;

/**
 * 功能: 发布日志服务
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
public interface DeployLogService {

    /**
     * 批量保存执行的shell 日志记录
     *
     * @param logs 服务器收集的shell日志记录
     * @return
     */
    void batchSaveServerDeployLog(List<ServerCollectLog> logs);

    /**
     * 读取某个服务器的发布日志，按发布时间顺序排列
     *
     * @param serverDeployHistoryId 服务器的发布记录ID
     * @return
     * @author linyi 2017/2/6
     */
    List<ServerDeployLog> getServerDeployLog(int serverDeployHistoryId);

    /**
     * redis发布服务器收集的shell 日志记录信息
     *
     * @param logs 服务器收集的shell日志记录
     */
    void publishSubscribeLogChangeMsg(List<ServerCollectLog> logs);

    /**
     *  根据serverDeployHistoryIds 获取每台服务器对应的发布日志
     * @param serverDeployIds 服务器发布的id
     */
    List<ServerDeployLog> getByServerDeployHistoryIds(List<Integer> serverDeployIds);
}
