package com.ibeiliao.deployment.common.util.redis.invoker;

import redis.clients.jedis.Jedis;

/**
 * 执行 redis 指令
 * @param <T>
 */
public interface RedisCallback<T> {
	T execute(Jedis jedis);
}

