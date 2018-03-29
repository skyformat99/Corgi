package com.ibeiliao.deployment.admin.service.server.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.dao.server.ServerDao;
import com.ibeiliao.deployment.admin.po.server.ServerPO;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;


@Service
public class ServerServiceImpl implements ServerService {

    private static final Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private ServerGroupService serverGroupService;

    @Override
    public void batchInsertServer(List<Server> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return;
        }
        List<ServerPO> serverPOs = VOUtil.fromList(servers, ServerPO.class);
        serverDao.batchInsert(serverPOs);
    }

    @Override
    public Server getById(int serverId) {
        Assert.isTrue(serverId > 0, "服务器id 不大于0 !");
        ServerPO serverPO = serverDao.get(serverId);
        return VOUtil.from(serverPO, Server.class);
    }

    @Override
    public List<Server> getByIds(int... serverIds) {
        if (serverIds == null || serverIds.length == 0) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(serverDao.getByIds(serverIds), Server.class);
    }

    @Override
    public void deleteByServerGroupIdsAndServerIds(List<Integer> groupIds, List<Integer> serverIds) {
        if (CollectionUtils.isEmpty(groupIds) || CollectionUtils.isEmpty(serverIds)) {
            return;
        }

        serverDao.deleteByServerGroupIdsAndServerIds(groupIds, serverIds);
        logger.info("删除以下服务器组id {} 的 且服务器id不在 {} 里面的服务器", StringUtils.join(groupIds, ","), StringUtils.join(serverIds, ","));
    }

    @Override
    public void updateServer(Server server) {
        Assert.isTrue(server != null && server.getServerId() > 0, "服务器更新参数不合法");

        serverDao.update(VOUtil.from(server, ServerPO.class));
    }

    @Override
    public List<Server> getByModuleId(int moduleId) {
        Assert.isTrue(moduleId > 0, "moduleId数值非法");

        List<ServerGroup> serverGroups = serverGroupService.getByModuleIds(Lists.newArrayList(moduleId), false);
        if (CollectionUtils.isEmpty(serverGroups)) {
            return Collections.emptyList();
        }
        List<Integer> groupIds = Lists.newArrayList();
        for (ServerGroup group : serverGroups) {
            groupIds.add(group.getGroupId());
        }

        List<ServerPO> serverPOs = serverDao.getByGroupIds(groupIds);
        if (CollectionUtils.isEmpty(serverPOs)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(serverPOs, Server.class);
    }

    @Override
    public List<Server> getByGroupIds(List<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return Collections.emptyList();
        }
        List<ServerPO> serverPOs = serverDao.getByGroupIds(groupIds);
        if (CollectionUtils.isEmpty(serverPOs)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(serverPOs, Server.class);
    }

    @Override
    public void deleteByGroupIds(List<Integer> groupIds) {
        serverDao.deleteByServerGroupIds(groupIds);
    }
}