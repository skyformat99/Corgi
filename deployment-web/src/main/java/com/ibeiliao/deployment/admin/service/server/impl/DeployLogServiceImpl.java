package com.ibeiliao.deployment.admin.service.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibeiliao.deployment.admin.dao.deploy.DeployHistoryDao;
import com.ibeiliao.deployment.admin.dao.deploy.ServerDeployHistoryDao;
import com.ibeiliao.deployment.admin.dao.deploy.ServerDeployLogDao;
import com.ibeiliao.deployment.admin.po.deploy.ServerDeployHistoryPO;
import com.ibeiliao.deployment.admin.po.deploy.ServerDeployLogPO;
import com.ibeiliao.deployment.admin.service.server.DeployLogService;
import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.common.vo.ServerCollectLog;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能: 发布日志服务
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
@Service
public class DeployLogServiceImpl implements DeployLogService {

    private static final Logger logger = LoggerFactory.getLogger(DeployLogServiceImpl.class);

    @Autowired
    private ServerDeployLogDao serverDeployLogDao;

    @Autowired
    private Redis redis;

    @Autowired
    private DeployHistoryDao deployHistoryDao;

    @Autowired
    private ServerDeployHistoryDao serverDeployHistoryDao;


    @Override
    public void batchSaveServerDeployLog(List<ServerCollectLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        rebuildServerDeployId(logs);
        List<ServerDeployLogPO> poList = new ArrayList<>();
        Date now = new Date();
        for (ServerCollectLog log : logs) {
            ServerDeployLogPO po = new ServerDeployLogPO();
            po.setServerDeployId(log.getId());
            po.setShellLog(log.getContent());
            po.setCreateTime(now);
            poList.add(po);
        }
        int rows = serverDeployLogDao.batchInsert(poList);
        logger.info("成功保存{}条shell日志记录", rows);
    }

    @Override
    public List<ServerDeployLog> getServerDeployLog(int serverDeployHistoryId) {
        List<ServerDeployLogPO> logs = serverDeployLogDao.getByServerDeployId(serverDeployHistoryId);
        return VOUtil.fromList(logs, ServerDeployLog.class);
    }


    @Override
    public void publishSubscribeLogChangeMsg(List<ServerCollectLog> logs) {
        if (CollectionUtils.isNotEmpty(logs)) {
            logger.info("发布日志信息:" + JSONObject.toJSONString(logs));
            redis.publish(RedisKey.getDeploySubscribeChannelKey(), JSONObject.toJSONString(logs));
        }
    }

    @Override
    public List<ServerDeployLog> getByServerDeployHistoryIds(List<Integer> serverDeployIds) {
        if (CollectionUtils.isEmpty(serverDeployIds)) {
            return Collections.emptyList();
        }

        List<ServerDeployLogPO> deployLogPOs = serverDeployLogDao.getByServerDeployIds(serverDeployIds);
        if (CollectionUtils.isEmpty(deployLogPOs)) {
            return Collections.emptyList();
        }

        return VOUtil.fromList(deployLogPOs, ServerDeployLog.class);
    }

    private void rebuildServerDeployId(List<ServerCollectLog> logs) {
        List<String> ips = Lists.newArrayList();
        for (ServerCollectLog log : logs) {
            if (StringUtils.isNotBlank(log.getServerIp())) {
                ips.add(log.getServerIp().trim());
            }
        }
        List<Integer> historyIds = Lists.newArrayList();
        for (ServerCollectLog log : logs) {
            if (log.getId() >=0 ) {
                historyIds.add(log.getId());
            }
        }

        if (CollectionUtils.isEmpty(ips) || CollectionUtils.isEmpty(historyIds)) {
            return;
        }

        List<ServerDeployHistoryPO> deployHistoryPOs = serverDeployHistoryDao.getByHistoryIdsAndIps(historyIds, ips);
        if (CollectionUtils.isEmpty(deployHistoryPOs)) {
            return;
        }
        HashMap<String, Integer> historyIdIp2ServerDeployIdMap = Maps.newHashMap();
        for (ServerDeployHistoryPO po : deployHistoryPOs) {
            historyIdIp2ServerDeployIdMap.put(po.getHistoryId() + "_" + po.getServerIp(), po.getId());
        }

        for (ServerCollectLog log : logs) {
            Integer serverDeployId = historyIdIp2ServerDeployIdMap.get(log.getId() + "_" + log.getServerIp());
            if (serverDeployId != null) {
                log.setId(serverDeployId);
            }
        }
    }

}
