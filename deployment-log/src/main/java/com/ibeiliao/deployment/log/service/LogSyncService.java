package com.ibeiliao.deployment.log.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibeiliao.deployment.common.enums.LogType;
import com.ibeiliao.deployment.common.util.HttpUtils;
import com.ibeiliao.deployment.common.util.RedisLogKey;
import com.ibeiliao.deployment.log.utils.LogSyncConfig;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.common.util.redis.RedisLock;
import com.ibeiliao.deployment.common.vo.ServerCollectLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 功能: 日志同步服务
 * <p>
 * 详细: 同步收集到的服务器日志
 *
 * @author jingyesi   17/1/24
 */
@Service
public class LogSyncService {

    private static final Logger logger = LoggerFactory.getLogger(LogSyncService.class);

    @Autowired
    private Redis redis;

    /**
     * 编译日志失效时间
     */
    private static final int COMPILE_LOG_EXISTS_TIME = 2 * 3600; //2小时


    /**
     *
     */
    private AtomicBoolean threadIsRunning = new AtomicBoolean(true);

    /**
     * 发送线程
     */
    private List<Thread> syncThreads = new ArrayList<>();

    /**
     * 取出日志并
     */
    private RedisLock popLogLock;

    /**
     * 启动同步shell日志线程
     */
    public void startupSyncShellLog() {

        popLogLock = new RedisLock(redis, "deployment:popShellLogLock", 2);


        for (int i = 0; i < LogSyncConfig.getInstance().getSyncShellLogThreadCount(); i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    int exCount = 0;
                    while (threadIsRunning.get()) {
                        try {
                            syncShellLog();

                            exCount = 0;
                        } catch (InterruptedException e) {
                            threadIsRunning.set(false);
                            break;
                        } catch (Exception e) {
                            logger.error("同步shell日志失败 | msg:{}", e.getMessage(), e);
                            // 防止 redis 挂掉的时候，磁盘瞬间满
                            exCount = (exCount + 1) % 20;
                            try {
                                Thread.sleep((exCount * 1000L) % 19000L + 1000L);
                            } catch (InterruptedException e1) {
                                break;
                            }
                        }

                    }
                    logger.info("同步shell日志线程结束");
                }
            });
            thread.setName("同步shell日志线程-" + i);
            syncThreads.add(thread);
            thread.start();

        }


    }

    /**
     * 关闭容器时,同时关闭后台同步线程
     */
    @PreDestroy
    public void shutdownSyncThreads() {
        threadIsRunning.set(false);
        if (CollectionUtils.isNotEmpty(syncThreads)) {
            for (Thread thread : syncThreads) {
                thread.interrupt();
            }
        }
    }


    /**
     * 同步shell脚本日志记录, 没有数据时, 休眠一段时间
     */

    private void syncShellLog() throws Exception {

        List<ServerCollectLog> logList = new ArrayList<>();
        int syncSize = 100;
        int count = 0;

        //避免多线程获取同一服务器同一次发布日志的错乱顺序问题, 取出记录时加锁
        try {
            boolean obtainLock = false;
            while (!obtainLock) {
                obtainLock = popLogLock.lock();
                if (obtainLock) {
                    while (count < syncSize) {
                        String str = redis.lpop(RedisLogKey.getServerDeploymentKey());

                        if (StringUtils.isNotEmpty(str)) {
                            logger.info("获取redis 日志内容:" + str);
                            ServerCollectLog log = JSONObject.parseObject(str, ServerCollectLog.class);

                            //处理shell 日志,
                            if (log.getLogType() == LogType.SERVER_SHELL_LOG.getType()) {
                                logList.add(log);
                            } else if (log.getLogType() == LogType.SERVER_DEPLOY_LOG.getType()) {
                                logList.addAll(buildServerDeployLogs(log));
                            }
                            //处理编译日志, 重新分类放进redis 里面, 并设置保存时间
                            else if (log.getLogType() == LogType.COMPILE_LOG.getType()) {

                                String moduleCompileKey = RedisLogKey.getModuleCompileKey(log.getId());
                                redis.rpush(moduleCompileKey, log.getContent());
                                redis.expire(moduleCompileKey, COMPILE_LOG_EXISTS_TIME);
                            }

                        } else {
                            break;
                        }
                        count++;
                    }
                } else {
                    //休眠
                    Thread.sleep(100);
                }
            }

        } finally {
            popLogLock.unlock();
        }

        //获取到数据后调用同步接口
        if (CollectionUtils.isNotEmpty(logList)) {
            logger.info("发送同步内容...");
            Map<String, String> params = new HashMap<>();
            params.put("data", JSONObject.toJSONString(logList));
            //成功失败均不重试
            HttpUtils.post(LogSyncConfig.getInstance().getSyncShellLogUrl(), params);


            //没有数据时休息一段时间等待下次调用
        } else {
            Thread.sleep(LogSyncConfig.getInstance().getMaxSyncInteval());
        }


    }

    private List<ServerCollectLog> buildServerDeployLogs(ServerCollectLog log) {
        List<ServerCollectLog> allDeployLogs = Lists.newArrayList();
        String content = log.getContent();
        String[] allLogs = content.split("\n");
        for (String deployLog : allLogs) {
            if (StringUtils.isBlank(deployLog)) {
                continue;
            }
            String[] split = deployLog.split(" ");
            String ip = split[0];
            String logContent = deployLog.substring(ip.length(), deployLog.length());
            ServerCollectLog serverDeployLog = new ServerCollectLog(log.getId(), log.getLogType(), logContent);
            serverDeployLog.setServerIp(ip);
            allDeployLogs.add(serverDeployLog);
        }

        return allDeployLogs;
    }


}
