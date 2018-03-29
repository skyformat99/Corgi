package com.ibeiliao.deployment.admin.task;

import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.common.util.redis.RedisLock;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 功能：处理超时的发布任务，比如申请上线，没有发布，或发布失败但状态没有更新的记录（可能因为bug）
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@Component
@EnableScheduling
public class ProcessTimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(ProcessTimeoutTask.class);

    @Autowired
    private Redis redis;

    @Autowired
    private DeployHistoryService deployHistoryService;

    /**
     * 每15分钟处理一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void process() {
        // lock 时间要超过5分钟，防止上一个任务没处理完，新的任务又来了
        // 1800 秒最多可以占 1800/5 次定时任务
        RedisLock redisLock = new RedisLock(redis, "deployment_timeout_task_check", 1800);
        if (redisLock.lock()) {
            logger.info("准备处理超时任务 ...");
            try {
                Date now = new Date();
                Date startTime = DateUtils.addSeconds(now, -Constants.DEPLOY_TASK_TIMEOUT * 3);
                Date endTime = DateUtils.addSeconds(now, -Constants.DEPLOY_TASK_TIMEOUT);
                final int MAX = 1000;
                logger.info("startTime: {}, endTime: {}", startTime, endTime);
                List<DeployHistory> list = deployHistoryService.queryUnfinished(startTime, endTime, MAX);
                if (list.size() > 0) {
                    for (DeployHistory history : list) {
                        deployHistoryService.systemCancel(history.getHistoryId());
                    }
                    logger.info("处理超时任务数量: " + list.size());
                }

            } catch (Exception e) {
                logger.error("处理超时任务出错", e);
            } finally {
                redisLock.unlock();
            }
        } else {
            logger.info("抢锁失败，跳过 ......");
        }
    }
}
