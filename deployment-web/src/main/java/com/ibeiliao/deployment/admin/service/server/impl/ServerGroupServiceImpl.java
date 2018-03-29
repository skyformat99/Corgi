package com.ibeiliao.deployment.admin.service.server.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.dao.server.ServerGroupDao;
import com.ibeiliao.deployment.admin.po.server.ServerGroupPO;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 服务器组的service实现
 */
@Service
public class ServerGroupServiceImpl implements ServerGroupService {

	private static final Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

	@Autowired
	private ServerGroupDao serverGroupDao;

	@Autowired
	private ServerService serverService;

    @Override
    public ServerGroup getById(int groupId) {
		ServerGroupPO serverGroupPO = serverGroupDao.get(groupId);
		return VOUtil.from(serverGroupPO, ServerGroup.class);
    }

	@Override
	public List<ServerGroup> batchInsertGroups(List<ServerGroup> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyList();
		}
		List<ServerGroupPO> serverGroupPOs = VOUtil.fromList(groups, ServerGroupPO.class);
		for (ServerGroupPO groupPO : serverGroupPOs) {
			serverGroupDao.insert(groupPO);
		}
		return VOUtil.fromList(serverGroupPOs, ServerGroup.class);
	}

	@Override
	public void updateGroup(ServerGroup serverGroup) {
		ServerGroupPO serverGroupPO = VOUtil.from(serverGroup, ServerGroupPO.class);
		serverGroupDao.update(serverGroupPO);
	}

	@Override
	public List<ServerGroup> getByModuleIds(List<Integer> moduleIds, boolean needServer) {
		Assert.isTrue(CollectionUtils.isNotEmpty(moduleIds), "moduleId 为空");

		List<ServerGroupPO> serverGroupPOs = serverGroupDao.getByModuleIds(moduleIds);
		if (CollectionUtils.isEmpty(serverGroupPOs)) {
			return Collections.emptyList();
		}
		List<ServerGroup> serverGroups = VOUtil.fromList(serverGroupPOs, ServerGroup.class);
		fillInServer(serverGroups);
		return serverGroups;
	}

	@Override
	public List<ServerGroup> getByModuleAndEnv(int moduleId, int envId) {
		Assert.isTrue(moduleId > 0, "moduleId 数值小于1");
		Assert.isTrue(envId > 0, "envId 错误，不能小于1");

		List<ServerGroupPO> serverGroupPOs = serverGroupDao.getByModuleAndEnv(moduleId, envId);
		if (CollectionUtils.isEmpty(serverGroupPOs)) {
			return Collections.emptyList();
		}
		return VOUtil.fromList(serverGroupPOs, ServerGroup.class);
	}

	@Override
	public void deleteByModuleId(int moduleId) {
		List<ServerGroupPO> groupPOs = serverGroupDao.getByModuleId(moduleId);
		ArrayList<Integer> groupIds = Lists.newArrayList();
		for (ServerGroupPO groupPO : groupPOs) {
			groupIds.add(groupPO.getGroupId());
		}
		serverGroupDao.deleteByModuleId(moduleId);
		if (CollectionUtils.isNotEmpty(groupIds)) {
			serverService.deleteByGroupIds(groupIds);
		}
	}

	@Override
	public void deleteByOldGroupIds(List<Integer> groupIds, int moduleId) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return;
		}
		serverGroupDao.deleteByModuleIdAndOldGroupIds(groupIds, moduleId);
	}

	private void fillInServer(List<ServerGroup> groups) {
		List<Integer> groupIds = Lists.newArrayList();
		for (ServerGroup group : groups) {
			groupIds.add(group.getGroupId());
		}
		List<Server> servers = serverService.getByGroupIds(groupIds);
		if (CollectionUtils.isEmpty(servers)) {
			return;
		}
		ArrayListMultimap<Integer, Server> groupId2ServersMap = ArrayListMultimap.create();
		for (Server server : servers) {
			groupId2ServersMap.put(server.getGroupId(), server);
		}
		for (ServerGroup group : groups) {
			if (groupId2ServersMap.get(group.getGroupId()) != null) {
				group.setServers(groupId2ServersMap.get(group.getGroupId()));
			}
		}
	}
}