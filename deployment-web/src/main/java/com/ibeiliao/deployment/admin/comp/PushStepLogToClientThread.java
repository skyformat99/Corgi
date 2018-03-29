package com.ibeiliao.deployment.admin.comp;

import com.alibaba.fastjson.JSON;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.deploy.ModuleStatusManagementService;
import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.deploy.ServerDeployHistory;
import com.ibeiliao.deployment.admin.websocket.context.LogIdToSessionHolder;
import com.ibeiliao.deployment.admin.websocket.request.ShellLogResponseMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import com.ibeiliao.deployment.common.enums.LogType;
import com.ibeiliao.deployment.common.util.RedisLogKey;
import com.ibeiliao.deployment.common.util.redis.Redis;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * 功能：推送步骤日志到客户端
 * 详细：
 *
 * @author linyi, 2017/2/17.
 */
public class PushStepLogToClientThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PushStepLogToClientThread.class);

    private boolean running = false;

    private static final long DELAY_TIME = 10 * 1000;

    private Redis redis;

    private ModuleStatusManagementService moduleStatusManagementService;

    private DeployHistoryService deployHistoryService;

    public PushStepLogToClientThread(ApplicationContext context) {
        super("PushStepLogToClientThread");
        this.redis = context.getBean(Redis.class);
        this.moduleStatusManagementService = context.getBean(ModuleStatusManagementService.class);
        this.deployHistoryService = context.getBean(DeployHistoryService.class);
    }

    public void stopThread() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        logger.info("推送步骤日志线程已启动 ...");
        Map<Integer, Long> historyId2StepLogCountMap = new LRUMap();
        Map<Integer, Long> historyId2ServerLogCountMap = new LRUMap();
        List<ClearLogReq> clearLogReqs = new ArrayList<>();
        running = true;
        while (running) {
            try {
                Map<String, String> allModules = moduleStatusManagementService.getAllDeployingModules();
                for (Map.Entry<String, String> entry : allModules.entrySet()) {
                    int historyId = moduleStatusManagementService.getHistoryIdFromFieldValue(entry.getValue());
                    if (historyId <= 0) {
                        continue;
                    }

                    DeployHistory deployHistory = deployHistoryService.getByHistoryId(historyId);
                    if (deployHistory == null) {
                        continue;
                    }

                    String stepLogKey = RedisKey.getProjectHistoryLogKey(historyId);
                    List<String> stepLogs = buildLogList(historyId2StepLogCountMap, stepLogKey, historyId);
                    String serverLogKey = RedisLogKey.getDeployServerLogKey(historyId);
                    List<String> serverDeployLogs = buildLogList(historyId2ServerLogCountMap, serverLogKey, historyId);

                    String logMessage = buildClientLogMessage(stepLogs, serverDeployLogs);
                    if (CollectionUtils.isNotEmpty(stepLogs) || CollectionUtils.isNotEmpty(serverDeployLogs)) {
                        sendLogToClient(deployHistory, logMessage);
                    } else {
                        logger.info("没有日志推送, historyId: " + historyId);
                    }

                    if (deployHistory.getDeployStatus() == DeployStatus.DEPLOYED.getValue()) {
                        clearLogReqs.add(new ClearLogReq(historyId, deployHistory.getModuleId(), deployHistory.getEnvId()));
                    }
                }

                clearModuleDeployLog(clearLogReqs);

            } catch (Exception e) {
                logger.error("日志推送异常", e);
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                logger.warn("线程 Interrupted");
                break;
            }
        }
        logger.info("推送步骤日志线程退出 ...");
    }

    private String buildClientLogMessage(List<String> stepLogs, List<String> serverDeployLogs) {
        ShellLogResponseMessage responseMessage = new ShellLogResponseMessage();
        responseMessage.setStepLogs(stepLogs);
        responseMessage.setServerDeployLogs(serverDeployLogs);
        responseMessage.setType(WebSocketRequestType.DEPLOY_STEP_LOG.getName());
        responseMessage.setCode(LogType.MAIN_STEP_LOG.getType());
        return JSON.toJSONString(responseMessage);
    }

    private List<String> buildLogList(Map<Integer, Long> historyId2StepLogCountMap, String stepLogKey, int historyId) {
        Long logCount = redis.llen(stepLogKey);

        List<String> messages = Collections.emptyList();
        if (logCount != null && logCount > 0) {
            Long oldValue = historyId2StepLogCountMap.get(historyId);
            if (oldValue == null || oldValue.longValue() != logCount.longValue()) {
                historyId2StepLogCountMap.put(historyId, logCount);
                messages = redis.lrange(stepLogKey, 0, logCount);
            }
        }
        return messages;
    }


    private void sendLogToClient(DeployHistory deployHistory, String message) {
        logger.info("websocket信息:" + message);

        for (ServerDeployHistory sdh : deployHistory.getServerDeployHistories()) {
            Set<WebSocketSession> sessions = LogIdToSessionHolder.getInstance().get(sdh.getId());
            if (CollectionUtils.isNotEmpty(sessions)) {
                logger.info("将推送客户端数量: " + sessions.size() + ", historyId: " + deployHistory.getHistoryId());
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(message));
                        } catch (Exception e) {
                            logger.error("推送step日志失败", e);
                        }
                    } else {
                        logger.warn("session 已关闭: " + session.getRemoteAddress());
                    }
                }
            } else {
                logger.warn("没有客户端可以推送, historyId: " + deployHistory.getHistoryId());
            }
        } // end for
    }

    /**
     * 延迟清除日志，因为客户端要展示日志
     * @param clearLogReqs
     */
    private void clearModuleDeployLog(List<ClearLogReq> clearLogReqs) {
        int size = clearLogReqs.size();
        for (int i=0; i < size; ) {
            ClearLogReq req = clearLogReqs.get(i);
            if (req.isTimeToClear()) {
                logger.info("清除发布日志, historyId: " + req.getHistoryId());
                moduleStatusManagementService.clearModuleDeployLog(req.getModuleId(), req.getEnvId());
                clearLogReqs.remove(i);
                size = clearLogReqs.size();
            } else {
                i++;
            }
        }
    }

    static class ClearLogReq {
        private int historyId;
        private int moduleId;
        private int envId;

        private long createTime;

        public ClearLogReq(int historyId, int moduleId, int envId) {
            this.historyId = historyId;
            this.moduleId = moduleId;
            this.envId = envId;
            this.createTime = System.currentTimeMillis();
        }

        public int getHistoryId() {
            return historyId;
        }

        public int getModuleId() {
            return moduleId;
        }

        public int getEnvId() {
            return envId;
        }

        public boolean isTimeToClear() {
            return (System.currentTimeMillis() - createTime > DELAY_TIME);
        }
    }
}
