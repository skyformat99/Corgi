package com.ibeiliao.deployment.admin.service.server;

import com.ibeiliao.deployment.admin.vo.server.ServerGroup;

import java.util.List;

/**
 * 详情 :  项目组service
 *
 * @author liangguanglong
 */
public interface ServerGroupService {

    /**
     * 根据groupId获取
     *
     * @param groupId
     * @return
     */
    ServerGroup getById(int groupId);

    /**
     * 批量插入server group
     * @param groups
     * @return
     */
    List<ServerGroup> batchInsertGroups(List<ServerGroup> groups);

    /**
     * 更新
     * @param serverGroup
     */
    void updateGroup(ServerGroup serverGroup);

    /**
     * 根据模块ids获取
     * @param moduleIds
     * @param needServer 是否需要填充服务器, true是要 false 是否
     * @return
     */
    List<ServerGroup> getByModuleIds(List<Integer> moduleIds, boolean needServer);

    /**
     * 根据模块、环境获取
     * @param moduleId 模块ID
     * @param envId 环境ID，大于0
     * @return
     */
    List<ServerGroup> getByModuleAndEnv(int moduleId, int envId);

    /**
     * 删除模块下的所有服务器和服务器组
     * @param moduleId 模块id
     */
    void deleteByModuleId(int moduleId);

    /**
     * 根据旧的groupId 删除被用户删除的组
     * @param groupIds
     * @param moduleId
     */
    void deleteByOldGroupIds(List<Integer> groupIds, int moduleId);
}


