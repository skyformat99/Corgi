package com.ibeiliao.deployment.admin.comp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.admin.websocket.context.LogIdToSessionHolder;
import com.ibeiliao.deployment.admin.websocket.request.ShellLogResponseMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.common.vo.ServerCollectLog;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import redis.clients.jedis.JedisPubSub;

import java.util.*;

/**
 * 功能：订阅日志并发送给服务端的线程
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public class SubscribeLogThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeLogThread.class);

    private DeployHistoryService deployHistoryService;

    private Redis redis;

    private JedisPubSub jedisPubSub;

    public SubscribeLogThread(ApplicationContext context) {
        super("logSubscriberThread");
        this.redis = context.getBean(Redis.class);
        this.deployHistoryService = context.getBean(DeployHistoryService.class);
    }

    public void stopThread() {
        interrupt();
        if (jedisPubSub != null) {
            jedisPubSub.unsubscribe();
        }
    }

    @Override
    public void run() {
        jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
              logger.info("接收到通道:{} 发布的信息, message: {}", channel, message);
                try {

                    //向用户推送监控的发布服务器信息
                    List<ServerCollectLog> logList = JSONArray.parseArray(message, ServerCollectLog.class);

                    //发布服务器对应的模块id  map
                    Map<Integer, Integer> serverDeployId2ModuleIdMap = new HashMap<>();

                    for (ServerCollectLog log : logList) {

//                        Set<WebSocketSession> sessions = serverDeployRelates.get(log.getId());
                        Set<WebSocketSession> sessions = LogIdToSessionHolder.getInstance().get(log.getId());

                        if (CollectionUtils.isNotEmpty(sessions)) {

                            //推送信息
                            ShellLogResponseMessage responseMessage = new ShellLogResponseMessage();

//                            responseMessage.setStepLogs(readStepLogs(serverDeployId2ModuleIdMap, log));

                            ShellLogResponseMessage.ServerShellLog shellLog = new ShellLogResponseMessage.ServerShellLog();

                            shellLog.setServerDeployId(log.getId());
                            shellLog.setLog(log.getContent());

                            List<ShellLogResponseMessage.ServerShellLog> shellLogList = new ArrayList<>();
                            shellLogList.add(shellLog);
                            responseMessage.setServerLogs(shellLogList);
                            responseMessage.setCode(ApiCode.SUCCESS);
                            responseMessage.setType(WebSocketRequestType.DEPLOY_SHELL_LOG.getName());

                            String messageStr = JSONObject.toJSONString(responseMessage);

                            //向监听用户发送消息
                            for (WebSocketSession session : sessions) {
                                if (session.isOpen()) {
                                    try {
                                        session.sendMessage(new TextMessage(messageStr));
                                    } catch (Exception e) {
                                        logger.error("发送推送日志消息失败 | msg:{}", e.getMessage(), e);
                                    }
                                }

                            }

                        }
                    } // end for
                } catch (Exception e) {
                    logger.error("处理订阅的日志更改信息失败 | msg:{}", e.getMessage(), e);
                }

            }
        };
        logger.info("订阅开始……");
        redis.subscribe(jedisPubSub, RedisKey.getDeploySubscribeChannelKey());
        logger.info("订阅结束 ...");
    }

}
