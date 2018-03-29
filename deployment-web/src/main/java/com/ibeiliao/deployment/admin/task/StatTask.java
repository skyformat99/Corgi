package com.ibeiliao.deployment.admin.task;

import com.ibeiliao.deployment.admin.service.stat.StatService;
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

/**
 * 功能：统计任务
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@Component
@EnableScheduling
public class StatTask {

    private static final Logger logger = LoggerFactory.getLogger(StatTask.class);

    @Autowired
    private StatService statService;

    @Autowired
    private Redis redis;

    /**
     * 每日统计
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyStat() {
        RedisLock redisLock = new RedisLock(redis, "task_daily_stat", 120);
        if (redisLock.lock()) {
            try {
                Date yesterday = DateUtils.addDays(new Date(), -1);
                statService.statDate(yesterday);
            } finally {
                redisLock.unlock();
            }
        }
        else {
            logger.info("抢锁失败，跳过 ......");
        }
    }
}
