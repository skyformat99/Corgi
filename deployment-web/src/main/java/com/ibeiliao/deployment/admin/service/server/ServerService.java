package com.ibeiliao.deployment.admin.service.server;

import com.ibeiliao.deployment.admin.vo.server.Server;

import java.util.List;

/**
 * 详情 :  服务器service
 *
 * @author liangguanglong
 */
public interface ServerService {

    /**
     * 批量insert
     * @param servers
     */
    void batchInsertServer(List<Server> servers);

    /**
     * 根据id查询
     * @param serverId
     * @return    */
    Server getById(int serverId);

    /**
     * 批量读取服务器
     * @param serverIds
     * @return
     */
    List<Server> getByIds(int ... serverIds);

    /**
     * 根据所属的服务器组和服务器id 来删除被客户端手动删除的服务器
     * @param groupIds 服务器组ids (in)
     * @param serverIds 服务器id  (not in)
     */
    void  deleteByServerGroupIdsAndServerIds(List<Integer> groupIds, List<Integer> serverIds);

    /**
     * 更新server
     * @param server
     */
    void updateServer(Server server);

    /**
     * 根据module
     * @param moduleId
     * @return
     */
    List<Server> getByModuleId(int moduleId);

    /**
     * 根据groupIds 查找
     * @param groupIds
     * @return
     */
    List<Server> getByGroupIds(List<Integer> groupIds);

    void deleteByGroupIds(List<Integer> groupId);
}


