package com.ibeiliao.deployment.admin.service;

import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.log.PushLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能：PushLogService的实现，写日志到队列，线程推送给客户端
 * 详细：
 *
 * @author linyi, 2017/2/17.
 */
@Service
public class PushLogServiceImpl implements PushLogService {

    @Autowired
    private Redis redis;

    private static final int MAX_LEN = 256;

    @Override
    public void writeStep(int historyId, String message) {
        if (StringUtils.isNotEmpty(message)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            String key = RedisKey.getProjectHistoryLogKey(historyId);
            if (message.length() > MAX_LEN) {
                message = message.substring(0, MAX_LEN) + " ...";
            }
            redis.rpush(key, sdf.format(new Date()) + " " + message);
        }
    }

    @Override
    public void clear(int historyId) {
        String key = RedisKey.getProjectHistoryLogKey(historyId);
        redis.del(key);
    }
}
