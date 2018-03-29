package com.ibeiliao.deployment.common.util.redis;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyCommands;

/**
 * 包装 redis 接口，目的是支持连接池和处理警告。
 * 仅支持 redis 3.x。
 * @author hemeng
 *
 */
public interface Redis extends JedisCommands, MultiKeyCommands {
	/** 获取资源（getResource()）方法，警告的阀值（毫秒） */
	long GET_RESOURCE_WARN_TIME = 30;

}
