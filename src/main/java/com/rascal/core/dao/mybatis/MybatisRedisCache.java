package com.rascal.core.dao.mybatis;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MybatisRedisCache implements Cache {

    private static Logger logger = LoggerFactory.getLogger(MybatisRedisCache.class);

    private static RedisTemplate<Object, Object> redisTemplate;

    //用于标识缓存全局版本，一般在应用更新版本时自动更新此值，更新前后应用使用各自版本缓存数据避免类型转换异常
    private static String cacheVersion;

    private static RedisSerializer<Object> defaultSerializer = new JdkSerializationRedisSerializer();

    /**
     * The ReadWriteLock.
     */

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private String id;

    public MybatisRedisCache() {

    }

    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        logger.debug("MybatisRedisCache: id=" + id);
        this.id = id + "_" + cacheVersion;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        Long size = redisTemplate.execute((RedisConnection connection) -> {
            Long dbSize = connection.dbSize();
            logger.debug("Mybatis redis getSize:" + dbSize);
            return dbSize;
        });
        return size == null ? 0 : size.intValue();
    }

    @Override
    public void putObject(final Object key, final Object value) {
        redisTemplate.execute((RedisConnection connection) -> {
            logger.debug("Mybatis redis putObject: " + key + "=" + value);
            connection.set(defaultSerializer.serialize(key), defaultSerializer.serialize(value));
            return null;
        });
    }

    @Override
    public Object getObject(final Object key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            byte[] keyBytes = defaultSerializer.serialize(key);
            if (connection.exists(keyBytes)) {
                Object v = defaultSerializer.deserialize(connection.get(keyBytes));
                logger.debug("Mybatis redis getObject: " + key + "=" + v);
                return v;
            }
            return null;
        });
    }

    @Override
    public Object removeObject(final Object key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            logger.debug("Mybatis redis removeObject: " + key);
            return connection.expire(defaultSerializer.serialize(key), 0);
        });
    }

    @Override
    public void clear() {
        redisTemplate.execute((RedisConnection connection) -> {
            logger.debug("Mybatis redis flushDB.");
            connection.flushDb();
            return null;
        });
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public static void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        MybatisRedisCache.redisTemplate = redisTemplate;
    }

    public static String getCacheVersion() {
        return cacheVersion;
    }

    public static void setCacheVersion(String cacheVersion) {
        MybatisRedisCache.cacheVersion = cacheVersion;
    }

}
