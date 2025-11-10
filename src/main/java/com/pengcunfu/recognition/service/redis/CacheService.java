package com.pengcunfu.recognition.service.redis;

import com.pengcunfu.recognition.constant.CacheKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 * 提供业务层面的缓存操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisService redisService;

    /**
     * 设置缓存（使用CacheKeyPrefix）
     */
    public void set(CacheKeyPrefix prefix, String suffix, Object value) {
        String key = prefix.getKey(suffix);
        redisService.set(key, value, prefix.getExpireTime(), TimeUnit.SECONDS);
        log.debug("设置缓存: key={}, expire={}s", key, prefix.getExpireTime());
    }

    /**
     * 获取缓存
     */
    public Object get(CacheKeyPrefix prefix, String suffix) {
        String key = prefix.getKey(suffix);
        Object value = redisService.get(key);
        log.debug("获取缓存: key={}, hit={}", key, value != null);
        return value;
    }

    /**
     * 删除缓存
     */
    public void delete(CacheKeyPrefix prefix, String suffix) {
        String key = prefix.getKey(suffix);
        redisService.delete(key);
        log.debug("删除缓存: key={}", key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean exists(CacheKeyPrefix prefix, String suffix) {
        String key = prefix.getKey(suffix);
        Boolean exists = redisService.hasKey(key);
        return exists != null && exists;
    }

    /**
     * 设置缓存（自定义过期时间）
     */
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisService.set(key, value, timeout, unit);
        log.debug("设置缓存: key={}, expire={}{}", key, timeout, unit);
    }

    /**
     * 刷新缓存过期时间
     */
    public void refreshExpire(CacheKeyPrefix prefix, String suffix) {
        String key = prefix.getKey(suffix);
        redisService.expire(key, prefix.getExpireTime(), TimeUnit.SECONDS);
        log.debug("刷新缓存过期时间: key={}, expire={}s", key, prefix.getExpireTime());
    }

    /**
     * 批量删除缓存（根据模式）
     */
    public void deleteByPattern(String pattern) {
        var keys = redisService.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisService.delete(keys);
            log.info("批量删除缓存: pattern={}, count={}", pattern, keys.size());
        }
    }
}

