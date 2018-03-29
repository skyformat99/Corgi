package com.ibeiliao.deployment.common.util.redis;

import com.ibeiliao.deployment.common.util.redis.impl.RedisClusterImpl;
import com.ibeiliao.deployment.common.util.redis.impl.RedisImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 根据配置进行Redis的实例化 工厂类 <br>
 *
 * @author linyi 2016/9/20
 */
public class RedisFactory extends JedisPoolConfig {

	private static final Logger logger = LoggerFactory.getLogger(RedisFactory.class);
	private static final int DEFAULT_MAX_TOTAL = 128;
	private static final int DEFAULT_TIMEOUT = 2000;
	private static final int DEFAULT_WEIGHT = 1;// 权重，只针对多个实例的redis有效

	private int soTimeout;
	private int connectionTimeout;
	private int maxAttempts = 5;  // 见 BinaryJedisCluster.DEFAULT_MAX_REDIRECTIONS

	public RedisFactory() {
		setMaxTotal(DEFAULT_MAX_TOTAL);
		setMaxIdle(DEFAULT_MAX_TOTAL);// 最大空闲连接数（默认与最大连接数一致）
		setSoTimeout(DEFAULT_TIMEOUT);// 超时时间,默认3秒;
		setConnectionTimeout(DEFAULT_TIMEOUT);// 连接超时时间,默认3秒;
	}

	/**
	 * 初始化 com.ibeiliao.platform.commons.cache.redis.Redis 并得到一个实例
	 * 
	 * @param server redis服务器配置；<br>
	 *            配置格式如下：[password@]host[:port],host2[:port2],host3,host4 ...，密码可选、端口不填则默认6379<br>
	 *            如果有多个server，以英文逗号分隔: server1:port1,server2:port2<br/>
	 * @return 返回Redis实例
	 */
	public Redis initRedis(String server) {
		return initRedis(server, 0);
	}

	/**
	 * 初始化 com.ibeiliao.platform.commons.cache.redis.Redis 并得到一个实例
	 * 
	 * @param server redis服务器配置；<br>
	 *            配置格式如下：[password@]host[:port],host2[:port2],host3,host4 ...，密码可选、端口不填则默认6379<br>
	 *            如果有多个server，以英文逗号分隔: server1:port1,server2:port2<br/>
	 * @param initPoolSize 初始化的连接数
	 * @return 返回Redis实例
	 */
	public Redis initRedis(String server, int initPoolSize) {
		// 解密配置
		LinkedList<JedisShardInfo> servers = splitServerConfig(server);
		// 得到redis实例
		// int max = getMaxActive();
		int max = getMaxTotal();
		if (servers.size() == 1) {
			return initSingleInstance(servers, max, initPoolSize);
		}
		return initRedisCluster(server);
	}

	/**
	 * 初始化redis.clients.jedis.JedisCluster并得到一个实例
	 *
	 * @param server redis服务器配置；<br>
	 *            配置格式如下：[password@]host[:port],host2[:port2],host3,host4 ...，密码可选、端口不填则默认6379<br>
	 *            如果有多个server，以英文逗号分隔: server1:port1,server2:port2<br/>
	 * @return 返回 JedisCluster 实例
	 */
	public JedisCluster initJedisCluster(String server) {
		LinkedList<JedisShardInfo> servers = splitServerConfig(server);
		Set<HostAndPort> hosts = new HashSet<>();
		for (JedisShardInfo info : servers) {
			HostAndPort hostAndPort = new HostAndPort(info.getHost(), info.getPort());
			hosts.add(hostAndPort);
		}
		JedisCluster jc = new JedisCluster(hosts, connectionTimeout, soTimeout, maxAttempts,
				servers.get(0).getPassword(), this);
		return jc;
	}

	/**
	 * 初始化 com.ibeiliao.platform.commons.cache.redis.Redis 并得到一个实例
	 *
	 * @param server redis服务器配置；<br>
	 *            配置格式如下：[password@]host[:port],host2[:port2],host3,host4 ...，密码可选、端口不填则默认6379<br>
	 *            如果有多个server，以英文逗号分隔: server1:port1,server2:port2<br/>
	 * @return 返回 Redis 实例
	 */
	public Redis initRedisCluster(String server) {
		LinkedList<JedisShardInfo> servers = splitServerConfig(server);
		Set<HostAndPort> hosts = new HashSet<>();
		for (JedisShardInfo info : servers) {
			HostAndPort hostAndPort = new HostAndPort(info.getHost(), info.getPort());
			hosts.add(hostAndPort);
		}
		logger.info("初始化Redis cluster ... " + servers.size());
		Redis redis = new RedisClusterImpl(hosts, connectionTimeout, soTimeout, maxAttempts,
				servers.get(0).getPassword(), this);
		return redis;
	}


	/**
	 * 根据配置获取一个连接池<br>
	 * 并返回单机的redis实例
	 */
	private Redis initSingleInstance(LinkedList<JedisShardInfo> servers, int maxActive, int initPoolSize) {
		if(servers == null || servers.size() == 0){
			throw new IllegalArgumentException("'server'");
		}

		// 单机
		JedisShardInfo server = servers.get(0);
		int soTimeout = server.getSoTimeout();
		int connectionTimeout = server.getConnectionTimeout();
		logger.info("单机redis | connectionTimeout: {}, maxActive: {}, initPoolSize: {}",
				connectionTimeout, maxActive, initPoolSize);
		JedisPool jedisPool = new JedisPool(this, server.getHost(), server.getPort(), connectionTimeout, soTimeout,
				server.getPassword(), Protocol.DEFAULT_DATABASE, null, false, null, null, null);
		return new RedisImpl(jedisPool);

	}

	/**
	 * 解读server配置<br>
	 * 
	 * 配置格式如下：[password@]host[:port],host2[:port],host3,host4 ---- 密码可选、端口不填则默认6379<br>
	 * 如果有多个server，以英文逗号分隔: server1,server2
	 */
	private LinkedList<JedisShardInfo> splitServerConfig(String server) {
		LinkedList<JedisShardInfo> serverList = new LinkedList<>();
		// 分解配置：密码@servers
		String[] authAndServer = StringUtils.split(server, '@');
		String auth = null;
		if (authAndServer.length > 2)
			throw new IllegalArgumentException("redis服务器配置不正确 | server: {}" + server);

		String[] servers = null;
		if (authAndServer.length == 2) {
			auth = authAndServer[0].trim();
			servers = StringUtils.split(authAndServer[1].trim(), ',');
		}
		else {
			servers = StringUtils.split(authAndServer[0].trim(), ',');
		}

		int index = 1;
		for (String tmp : servers) {
			String serverConf = StringUtils.trimToNull(tmp);
			if (null == serverConf)
				continue;
			JedisShardInfo info = readServer(serverConf);
			if (auth != null) {
				info.setPassword(auth);
			}
			logger.info("cluster server{} | {}", index, info.getHost() + ":" + info.getPort());
			serverList.add(info);
			index++;
		}

		return serverList;
	}

	/**
	 * 读取单个服务的配置，并构建JedisShardInfo对象<br>
	 * 
	 * @param config 单个服务器的配置（一定不为null）格式为：host[:port] >> 端口不填则默认6379
	 */
	private JedisShardInfo readServer(String config) {
		// 分解host配置：host[:port]
		String[] hostInfo = StringUtils.split(config, ':');
		int hLength = hostInfo.length;
		if (2 < hLength)
			throw new IllegalArgumentException("redis服务器配置不正确（host[:port]）：" + config);
		String host = hostInfo[0];

		// 端口配置，有可能为null，为null时使用默认6379端口
		String portStr = (2 == hLength ? hostInfo[1] : null);
		if (null != portStr && !NumberUtils.isDigits(portStr))
			throw new IllegalArgumentException("redis端口配置不正确（int）：" + portStr);

		int port = (null == portStr ? Protocol.DEFAULT_PORT : Integer.parseInt(portStr));

		JedisShardInfo info = new JedisShardInfo(host, port, connectionTimeout, soTimeout, DEFAULT_WEIGHT);

		return info;
	}

	// ####
	// ## set func
	// ####

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

}
