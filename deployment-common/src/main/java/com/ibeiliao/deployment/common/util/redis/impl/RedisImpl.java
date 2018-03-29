package com.ibeiliao.deployment.common.util.redis.impl;

import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.common.util.redis.invoker.RedisCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Pool;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 单个 redis 实例的实现，使用连接池，并处理一些警告信息。
 *
 * 如果是 Redis 3.x 集群直接使用 JedisCluster 即可，它已经使用了连接池。
 * 
 * @author linyi 2016/9/19
 *
 */
public class RedisImpl implements Redis {

	private static final Logger logger = LoggerFactory.getLogger(RedisImpl.class);

	private Pool<Jedis> jedisPool;

	public RedisImpl(JedisPool jedisPool) {
		this.jedisPool = jedisPool;

	}

	@Override
	public String set(final String key, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}

	@Override
	public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.set(key, value, nxxx, expx, time);
			}
		});
	}

	@Override
	public String set(final String key, final String value, final String nxxx) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.set(key, value, nxxx);
			}
		});
	}

	@Override
	public String get(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	@Override
	public Boolean exists(final String key) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.exists(key);
			}
		});
	}

	@Override
	public Long persist(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.persist(key);
			}
		});
	}

	@Override
	public String type(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.type(key);
			}
		});
	}

	@Override
	public Long expire(final String key, final int seconds) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	@Override
	public Long pexpire(final String key, final long milliseconds) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pexpire(key, milliseconds);
			}
		});
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		return null;
	}

	@Override
	public Long pexpireAt(final String key, final long millisecondsTimestamp) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pexpireAt(key, millisecondsTimestamp);
			}
		});
	}

	@Override
	public Long ttl(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.ttl(key);
			}
		});
	}

	@Override
	public Long pttl(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pttl(key);
			}
		});
	}

	@Override
	public Boolean setbit(final String key, final long offset, final boolean value) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.setbit(key, offset, value);
			}
		});
	}

	@Override
	public Boolean setbit(final String key, final long offset, final String value) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.setbit(key, offset, value);
			}
		});
	}

	@Override
	public Boolean getbit(final String key, final long offset) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.getbit(key, offset);
			}
		});
	}

	@Override
	public Long setrange(final String key, final long offset, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.setrange(key, offset, value);
			}
		});
	}

	@Override
	public String getrange(final String key, final long startOffset, final long endOffset) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.getrange(key, startOffset, endOffset);
			}
		});
	}

	@Override
	public String getSet(final String key, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.getSet(key, value);
			}
		});
	}

	@Override
	public Long setnx(final String key, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.setnx(key, value);
			}
		});
	}

	@Override
	public String setex(final String key, final int seconds, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.setex(key, seconds, value);
			}
		});
	}

	@Override
	public String psetex(final String key, final long milliseconds, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.psetex(key, milliseconds, value);
			}
		});
	}

	@Override
	public Long decrBy(final String key, final long integer) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.decrBy(key, integer);
			}
		});
	}

	@Override
	public Long decr(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.decr(key);
			}
		});
	}

	@Override
	public Long incrBy(final String key, final long integer) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.incrBy(key, integer);
			}
		});
	}

	@Override
	public Double incrByFloat(final String key, final double value) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.incrByFloat(key, value);
			}
		});
	}

	@Override
	public Long incr(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	@Override
	public Long append(final String key, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.append(key, value);
			}
		});
	}

	@Override
	public String substr(final String key, final int start, final int end) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.substr(key, start, end);
			}
		});
	}

	@Override
	public Long hset(final String key, final String field, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hset(key, field, value);
			}
		});
	}

	@Override
	public String hget(final String key, final String field) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.hget(key, field);
			}
		});
	}

	@Override
	public Long hsetnx(final String key, final String field, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hsetnx(key, field, value);
			}
		});
	}

	@Override
	public String hmset(final String key, final Map<String, String> hash) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.hmset(key, hash);
			}
		});
	}

	@Override
	public List<String> hmget(final String key, final String... fields) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.hmget(key, fields);
			}
		});
	}

	@Override
	public Long hincrBy(final String key, final String field, final long value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hincrBy(key, field, value);
			}
		});
	}

	@Override
	public Double hincrByFloat(final String key, final String field, final double value) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.hincrByFloat(key, field, value);
			}
		});
	}

	@Override
	public Boolean hexists(final String key, final String field) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.hexists(key, field);
			}
		});
	}

	@Override
	public Long hdel(final String key, final String... field) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hdel(key, field);
			}
		});
	}

	@Override
	public Long hlen(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hlen(key);
			}
		});
	}

	@Override
	public Set<String> hkeys(final String key) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.hkeys(key);
			}
		});
	}

	@Override
	public List<String> hvals(final String key) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.hvals(key);
			}
		});
	}

	@Override
	public Map<String, String> hgetAll(final String key) {
		return execute(new RedisCallback<Map<String, String>>() {
			@Override
			public Map<String, String> execute(Jedis jedis) {
				return jedis.hgetAll(key);
			}
		});
	}

	@Override
	public Long rpush(final String key, final String... string) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.rpush(key, string);
			}
		});
	}

	@Override
	public Long lpush(final String key, final String... string) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.lpush(key, string);
			}
		});
	}

	@Override
	public Long llen(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.llen(key);
			}
		});
	}

	@Override
	public List<String> lrange(final String key, final long start, final long end) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	@Override
	public String ltrim(final String key, final long start, final long end) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.ltrim(key, start, end);
			}
		});
	}

	@Override
	public String lindex(final String key, final long index) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.lindex(key, index);
			}
		});
	}

	@Override
	public String lset(final String key, final long index, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.lset(key, index, value);
			}
		});
	}

	@Override
	public Long lrem(final String key, final long count, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.lrem(key, count, value);
			}
		});
	}

	@Override
	public String lpop(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.lpop(key);
			}
		});
	}

	@Override
	public String rpop(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.rpop(key);
			}
		});
	}

	@Override
	public Long sadd(final String key, final String... member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sadd(key, member);
			}
		});
	}

	@Override
	public Set<String> smembers(final String key) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.smembers(key);
			}
		});
	}

	@Override
	public Long srem(final String key, final String... member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.srem(key, member);
			}
		});
	}

	@Override
	public String spop(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.spop(key);
			}
		});
	}

	@Override
	public Set<String> spop(final String key, final long count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.spop(key, count);
			}
		});
	}

	@Override
	public Long scard(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.scard(key);
			}
		});
	}

	@Override
	public Boolean sismember(final String key, final String member) {
		return execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.sismember(key, member);
			}
		});
	}

	@Override
	public String srandmember(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.srandmember(key);
			}
		});
	}

	@Override
	public List<String> srandmember(final String key, final int count) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.srandmember(key, count);
			}
		});
	}

	@Override
	public Long strlen(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.strlen(key);
			}
		});
	}

	@Override
	public Long zadd(final String key, final double score, final String member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zadd(key, score, member);
			}
		});
	}

	@Override
	public Long zadd(final String key, final double score, final String member, final ZAddParams params) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zadd(key, score, member, params);
			}
		});
	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zadd(key, scoreMembers);
			}
		});
	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers, final ZAddParams params) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zadd(key, scoreMembers, params);
			}
		});
	}

	@Override
	public Set<String> zrange(final String key, final long start, final long end) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrange(key, start, end);
			}
		});
	}

	@Override
	public Long zrem(final String key, final String... member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zrem(key, member);
			}
		});
	}

	@Override
	public Double zincrby(final String key, final double score, final String member) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.zincrby(key, score, member);
			}
		});
	}

	@Override
	public Double zincrby(final String key, final double score, final String member, final ZIncrByParams params) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.zincrby(key, score, member, params);
			}
		});
	}

	@Override
	public Long zrank(final String key, final String member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zrank(key, member);
			}
		});
	}

	@Override
	public Long zrevrank(final String key, final String member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zrevrank(key, member);
			}
		});
	}

	@Override
	public Set<String> zrevrange(final String key, final long start, final long end) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrange(key, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrangeWithScores(key, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrevrangeWithScores(key, start, end);
			}
		});
	}

	@Override
	public Long zcard(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zcard(key);
			}
		});
	}

	@Override
	public Double zscore(final String key, final String member) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	@Override
	public List<String> sort(final String key) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.sort(key);
			}
		});
	}

	@Override
	public List<String> sort(final String key, final SortingParams sortingParameters) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.sort(key, sortingParameters);
			}
		});
	}

	@Override
	public Long zcount(final String key, final double min, final double max) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	@Override
	public Long zcount(final String key, final String min, final String max) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByScore(key, max, min);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByScore(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByScore(key, max, min);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByScore(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByScore(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByScore(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset, final int count) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
		return execute(new RedisCallback<Set<Tuple>>() {
			@Override
			public Set<Tuple> execute(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Long zremrangeByRank(final String key, final long start, final long end) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zremrangeByRank(key, start, end);
			}
		});
	}

	@Override
	public Long zremrangeByScore(final String key, final double start, final double end) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zremrangeByScore(key, start, end);
			}
		});
	}

	@Override
	public Long zremrangeByScore(final String key, final String start, final String end) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zremrangeByScore(key, start, end);
			}
		});
	}

	@Override
	public Long zlexcount(final String key, final String min, final String max) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zlexcount(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByLex(final String key, final String min, final String max) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByLex(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrangeByLex(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByLex(final String key, final String max, final String min) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByLex(key, max, min);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByLex(final String key, final String max, final String min, final int offset, final int count) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.zrevrangeByLex(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Long zremrangeByLex(final String key, final String min, final String max) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zremrangeByLex(key, min, max);
			}
		});
	}

	@Override
	public Long linsert(final String key, final LIST_POSITION where, final String pivot, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.linsert(key, where, pivot, value);
			}
		});
	}

	@Override
	public Long lpushx(final String key, final String... string) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.lpushx(key, string);
			}
		});
	}

	@Override
	public Long rpushx(final String key, final String... string) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.rpushx(key, string);
			}
		});
	}

	@Override
	public List<String> blpop(final String arg) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.blpop(arg);
			}
		});
	}

	@Override
	public List<String> blpop(final int timeout, final String key) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.blpop(timeout, key);
			}
		});
	}

	@Override
	public List<String> brpop(final String arg) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.brpop(arg);
			}
		});
	}

	@Override
	public List<String> brpop(final int timeout, final String key) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.brpop(timeout, key);
			}
		});
	}

	@Override
	public Long del(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	@Override
	public String echo(final String string) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.echo(string);
			}
		});
	}

	@Override
	public Long move(final String key, final int dbIndex) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.move(key, dbIndex);
			}
		});
	}

	@Override
	public Long bitcount(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.bitcount(key);
			}
		});
	}

	@Override
	public Long bitcount(final String key, final long start, final long end) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.bitcount(key, start, end);
			}
		});
	}

	@Override
	public Long bitpos(final String key, final boolean value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.bitpos(key, value);
			}
		});
	}

	@Override
	public Long bitpos(final String key, final boolean value, final BitPosParams params) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.bitpos(key, value, params);
			}
		});
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final int cursor) {
		return execute(new RedisCallback<ScanResult<Entry<String, String>>>() {
			@Override
			public ScanResult<Entry<String, String>> execute(Jedis jedis) {
				return jedis.hscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<String> sscan(final String key, final int cursor) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.sscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final int cursor) {
		return execute(new RedisCallback<ScanResult<Tuple>>() {
			@Override
			public ScanResult<Tuple> execute(Jedis jedis) {
				return jedis.zscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor) {
		return execute(new RedisCallback<ScanResult<Entry<String, String>>>() {
			@Override
			public ScanResult<Entry<String, String>> execute(Jedis jedis) {
				return jedis.hscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor, final ScanParams params) {
		return execute(new RedisCallback<ScanResult<Entry<String, String>>>() {
			@Override
			public ScanResult<Entry<String, String>> execute(Jedis jedis) {
				return jedis.hscan(key, cursor, params);
			}
		});
	}

	@Override
	public ScanResult<String> sscan(final String key, final String cursor) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.sscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<String> sscan(final String key, final String cursor, final ScanParams params) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.sscan(key, cursor, params);
			}
		});
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final String cursor) {
		return execute(new RedisCallback<ScanResult<Tuple>>() {
			@Override
			public ScanResult<Tuple> execute(Jedis jedis) {
				return jedis.zscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params) {
		return execute(new RedisCallback<ScanResult<Tuple>>() {
			@Override
			public ScanResult<Tuple> execute(Jedis jedis) {
				return jedis.zscan(key, cursor, params);
			}
		});
	}

	@Override
	public Long pfadd(final String key, final String... elements) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pfadd(key, elements);
			}
		});
	}

	@Override
	public long pfcount(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pfcount(key);
			}
		});
	}

	@Override
	public Long geoadd(final String key, final double longitude, final double latitude, final String member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.geoadd(key, longitude, latitude, member);
			}
		});
	}

	@Override
	public Long geoadd(final String key, final Map<String, GeoCoordinate> memberCoordinateMap) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.geoadd(key, memberCoordinateMap);
			}
		});
	}

	@Override
	public Double geodist(final String key, final String member1, final String member2) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.geodist(key, member1, member2);
			}
		});
	}

	@Override
	public Double geodist(final String key, final String member1, final String member2, final GeoUnit unit) {
		return execute(new RedisCallback<Double>() {
			@Override
			public Double execute(Jedis jedis) {
				return jedis.geodist(key, member1, member2, unit);
			}
		});
	}

	@Override
	public List<String> geohash(final String key, final String... members) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.geohash(key, members);
			}
		});
	}

	@Override
	public List<GeoCoordinate> geopos(final String key, final String... members) {
		return execute(new RedisCallback<List<GeoCoordinate>>() {
			@Override
			public List<GeoCoordinate> execute(Jedis jedis) {
				return jedis.geopos(key, members);
			}
		});
	}

	@Override
	public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius, final GeoUnit unit) {
		return execute(new RedisCallback<List<GeoRadiusResponse>>() {
			@Override
			public List<GeoRadiusResponse> execute(Jedis jedis) {
				return jedis.georadius(key, longitude, latitude, radius, unit);
			}
		});
	}

	@Override
	public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius, final GeoUnit unit, final GeoRadiusParam param) {
		return execute(new RedisCallback<List<GeoRadiusResponse>>() {
			@Override
			public List<GeoRadiusResponse> execute(Jedis jedis) {
				return jedis.georadius(key, longitude, latitude, radius, unit, param);
			}
		});
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final String key, final String member, final double radius, final GeoUnit unit) {
		return execute(new RedisCallback<List<GeoRadiusResponse>>() {
			@Override
			public List<GeoRadiusResponse> execute(Jedis jedis) {
				return jedis.georadiusByMember(key, member, radius, unit);
			}
		});
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final String key, final String member, final double radius, final GeoUnit unit, final GeoRadiusParam param) {
		return execute(new RedisCallback<List<GeoRadiusResponse>>() {
			@Override
			public List<GeoRadiusResponse> execute(Jedis jedis) {
				return jedis.georadiusByMember(key, member, radius, unit, param);
			}
		});
	}

	@Override
	public List<Long> bitfield(final String key, final String... arguments) {
		return execute(new RedisCallback<List<Long>>() {
			@Override
			public List<Long> execute(Jedis jedis) {
				return jedis.bitfield(key, arguments);
			}
		});
	}

	@Override
	public Long del(final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.del(keys);
			}
		});
	}

	@Override
	public Long exists(final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.exists(keys);
			}
		});
	}

	@Override
	public List<String> blpop(final int timeout, final String... keys) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.blpop(timeout, keys);
			}
		});
	}

	@Override
	public List<String> brpop(final int timeout, final String... keys) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.brpop(timeout, keys);
			}
		});
	}

	@Override
	public List<String> blpop(final String... args) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.blpop(args);
			}
		});
	}

	@Override
	public List<String> brpop(final String... args) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.brpop(args);
			}
		});
	}

	@Override
	public Set<String> keys(final String pattern) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.keys(pattern);
			}
		});
	}

	@Override
	public List<String> mget(final String... keys) {
		return execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.mget(keys);
			}
		});
	}

	@Override
	public String mset(final String... keysvalues) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.mset(keysvalues);
			}
		});
	}

	@Override
	public Long msetnx(final String... keysvalues) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.msetnx(keysvalues);
			}
		});
	}

	@Override
	public String rename(final String oldkey, final String newkey) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.rename(oldkey, newkey);
			}
		});
	}

	@Override
	public Long renamenx(final String oldkey, final String newkey) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.renamenx(oldkey, newkey);
			}
		});
	}

	@Override
	public String rpoplpush(final String srckey, final String dstkey) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.rpoplpush(srckey, dstkey);
			}
		});
	}

	@Override
	public Set<String> sdiff(final String... keys) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.sdiff(keys);
			}
		});
	}

	@Override
	public Long sdiffstore(final String dstkey, final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sdiffstore(dstkey, keys);
			}
		});
	}

	@Override
	public Set<String> sinter(final String... keys) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.sinter(keys);
			}
		});
	}

	@Override
	public Long sinterstore(final String dstkey, final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sinterstore(dstkey, keys);
			}
		});
	}

	@Override
	public Long smove(final String srckey, final String dstkey, final String member) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.smove(srckey, dstkey, member);
			}
		});
	}

	@Override
	public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sort(key, sortingParameters, dstkey);
			}
		});
	}

	@Override
	public Long sort(final String key, final String dstkey) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sort(key, dstkey);
			}
		});
	}

	@Override
	public Set<String> sunion(final String... keys) {
		return execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.sunion(keys);
			}
		});
	}

	@Override
	public Long sunionstore(final String dstkey, final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.sunionstore(dstkey, keys);
			}
		});
	}

	@Override
	public String watch(final String... keys) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.watch(keys);
			}
		});
	}

	@Override
	public String unwatch() {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.unwatch();
			}
		});
	}

	@Override
	public Long zinterstore(final String dstkey, final String... sets) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zinterstore(dstkey, sets);
			}
		});
	}

	@Override
	public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zinterstore(dstkey, params, sets);
			}
		});
	}

	@Override
	public Long zunionstore(final String dstkey, final String... sets) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zunionstore(dstkey, sets);
			}
		});
	}

	@Override
	public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zunionstore(dstkey, params, sets);
			}
		});
	}

	@Override
	public String brpoplpush(final String source, final String destination, final int timeout) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.brpoplpush(source, destination, timeout);
			}
		});
	}

	@Override
	public Long publish(final String channel, final String message) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.publish(channel, message);
			}
		});
	}

	@Override
	public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
		execute(new RedisCallback<Object>() {

			@Override
			public Object execute(Jedis jedis) {
				jedis.subscribe(jedisPubSub, channels);
				return null;
			}
		});
	}

	@Override
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
		execute(new RedisCallback<Object>() {
			@Override
			public Object execute(Jedis jedis) {
				jedis.psubscribe(jedisPubSub, patterns);
				return null;
			}
		});
	}

	@Override
	public String randomKey() {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.randomKey();
			}
		});
	}

	@Override
	public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.bitop(op, destKey, srcKeys);
			}
		});
	}

	@Override
	public ScanResult<String> scan(final int cursor) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.scan(cursor);
			}
		});
	}

	@Override
	public ScanResult<String> scan(final String cursor) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.scan(cursor);
			}
		});
	}

	@Override
	public ScanResult<String> scan(final String cursor, final ScanParams params) {
		return execute(new RedisCallback<ScanResult<String>>() {
			@Override
			public ScanResult<String> execute(Jedis jedis) {
				return jedis.scan(cursor, params);
			}
		});
	}

	@Override
	public String pfmerge(final String destkey, final String... sourcekeys) {
		return execute(new RedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.pfmerge(destkey, sourcekeys);
			}
		});
	}

	@Override
	public long pfcount(final String... keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.pfcount(keys);
			}
		});
	}

// ------------- private methods

	private <T> T execute(String key, final RedisCallback<T> callback) {
		Jedis jedis = this.getResource();
		try {
			return execute(jedis, callback);
		} catch (Exception e) {
			throw new RuntimeException("读取redis出错", e);
		}
	}

	private <T> T execute(byte[] key, final RedisCallback<T> callback) {
		Jedis jedis = this.getResource();
		try {
			return execute(jedis, callback);
		} catch (Exception e) {
			throw new RuntimeException("读取redis出错", e);
		}
	}

	private <T> T execute(final RedisCallback<T> callback) {
		Jedis jedis = this.getResource();
		return execute(jedis, callback);
	}

	private <T> T execute(Jedis jedis, final RedisCallback<T> callback) {
		try {
			return callback.execute(jedis);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}


	/**
	 * 从连接池拿出来一个连接
	 * @return
	 */
	private Jedis getResource() {
		long startTime = System.nanoTime();
		try {
			return this.jedisPool.getResource();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("没有更多redis连接实例可以使用", e);
		} catch (JedisConnectionException e) {
			throw new JedisConnectionException("获取redis连接失败", e);
		} finally {
			long endTime = System.nanoTime();
			long time = (endTime - startTime) / 1000L / 1000L; // time 单位:毫秒
			if (time >= GET_RESOURCE_WARN_TIME) {// 超过警告阀值，才warn
				logger.warn("get resource from redis cost time: [" + time + "] ms");
			}
		}
	}

}
