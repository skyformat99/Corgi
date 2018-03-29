package com.ibeiliao.deployment.common.util.redis.impl;

import com.ibeiliao.deployment.common.util.redis.Redis;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Set;

/**
 * 功能：redis集群的实现，建议直接使用 redis.clients.jedis.JedisCluster
 *
 * 详细：其实直接使用 JedisCluster就可以了，这里重新包装主要是为了统一接口。<br/>
 *       MultiKeyCommands 包含了 MultiKeyJedisClusterCommands，并多出了一些接口。
 *
 * @author linyi, 2016/9/20.
 */
public class RedisClusterImpl extends JedisCluster implements Redis {

    public RedisClusterImpl(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts, String password, GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
    }

    @Override
    public List<String> blpop(String... args) {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public List<String> brpop(String... args) {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public Set<String> keys(String pattern) {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public String watch(String... keys) {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public String unwatch() {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public String randomKey() {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public ScanResult<String> scan(int cursor) {
        throw new IllegalStateException("Not supported in cluster mode");
    }

    @Override
    public ScanResult<String> scan(String cursor) {
        throw new IllegalStateException("Not supported in cluster mode");
    }
}
