package com.pcf.recognition.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis客户端工具类
 * 封装RedisTemplate的常用操作，提供更简洁的API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisClient {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 设置键值对
     * 
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            log.debug("Redis SET: key={}, value={}", key, value);
        } catch (Exception e) {
            log.error("Redis SET操作失败: key={}, value={}", key, value, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置键值对并指定过期时间
     * 
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void setWithExpire(String key, String value, long timeout, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            log.debug("Redis SET with expire: key={}, value={}, timeout={} {}", key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Redis SET with expire操作失败: key={}, value={}, timeout={} {}", key, value, timeout, timeUnit, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取值
     * 
     * @param key 键
     * @return 值，如果不存在返回null
     */
    public String get(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            log.debug("Redis GET: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Redis GET操作失败: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 删除键
     * 
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            Boolean result = stringRedisTemplate.delete(key);
            boolean success = result != null && result;
            log.debug("Redis DELETE: key={}, success={}", key, success);
            return success;
        } catch (Exception e) {
            log.error("Redis DELETE操作失败: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 检查键是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            Boolean result = stringRedisTemplate.hasKey(key);
            boolean exists = result != null && result;
            log.debug("Redis EXISTS: key={}, exists={}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("Redis EXISTS操作失败: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置键的过期时间
     * 
     * @param key      键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = stringRedisTemplate.expire(key, timeout, timeUnit);
            boolean success = result != null && result;
            log.debug("Redis EXPIRE: key={}, timeout={} {}, success={}", key, timeout, timeUnit, success);
            return success;
        } catch (Exception e) {
            log.error("Redis EXPIRE操作失败: key={}, timeout={} {}", key, timeout, timeUnit, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取键的剩余过期时间（秒）
     * 
     * @param key 键
     * @return 剩余过期时间，-1表示永不过期，-2表示键不存在
     */
    public long getExpire(String key) {
        try {
            Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            long result = expire != null ? expire : -2;
            log.debug("Redis GET EXPIRE: key={}, expire={}s", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis GET EXPIRE操作失败: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 构建带前缀的Redis键
     * 
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 完整的键
     */
    public static String buildKey(String prefix, String suffix) {
        return prefix + suffix;
    }

    /**
     * 构建带前缀和类型的Redis键
     * 
     * @param prefix 前缀
     * @param type   类型
     * @param suffix 后缀
     * @return 完整的键
     */
    public static String buildKey(String prefix, String type, String suffix) {
        return prefix + type + ":" + suffix;
    }
}
