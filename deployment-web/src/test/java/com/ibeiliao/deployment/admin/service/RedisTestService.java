package com.ibeiliao.deployment.admin.service;

import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.common.util.redis.Redis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPubSub;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"/*, "classpath:web-servlet.xml"*/})
public class RedisTestService{

    @Autowired
    private Redis redis;

    @Test
    public void testSubscribe(){
        System.out.println("准备订阅......");
        redis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
            }
        }, "publish-channel-1");
        System.out.println("订阅中......");

    }

    @Test
    public void testPublish() throws Exception{
        redis.publish(RedisKey.getDeploySubscribeChannelKey(), "test");
        System.out.println("睡眠100秒");
        Thread.sleep(100 * 1000);
    }

}
