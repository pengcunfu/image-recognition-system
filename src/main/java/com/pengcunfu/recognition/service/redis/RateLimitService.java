package com.pengcunfu.recognition.service.redis;

import com.pengcunfu.recognition.constant.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 限流服务
 * 基于Redis实现分布式限流
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisService redisService;

    /**
     * 检查是否被限流
     *
     * @param key 限流键
     * @param maxCount 最大次数
     * @param timeWindow 时间窗口（秒）
     * @return true-允许访问，false-被限流
     */
    public boolean isAllowed(String key, int maxCount, int timeWindow) {
        Object countObj = redisService.get(key);
        
        if (countObj == null) {
            // 第一次访问，设置计数为1
            redisService.set(key, 1, timeWindow, TimeUnit.SECONDS);
            return true;
        }

        int count = (Integer) countObj;
        if (count < maxCount) {
            // 未达到限流阈值，增加计数
            redisService.increment(key);
            return true;
        }

        // 达到限流阈值
        log.warn("触发限流: key={}, count={}/{}, timeWindow={}s", key, count, maxCount, timeWindow);
        return false;
    }

    /**
     * API限流
     */
    public boolean isApiAllowed(Long userId, String api, int maxCount, int timeWindow) {
        String key = RedisConstants.API_RATE_LIMIT_KEY + userId + ":" + api;
        return isAllowed(key, maxCount, timeWindow);
    }

    /**
     * 识别功能限流
     */
    public boolean isRecognitionAllowed(Long userId, int maxCount, int timeWindow) {
        String key = RedisConstants.RECOGNITION_RATE_LIMIT_KEY + userId;
        return isAllowed(key, maxCount, timeWindow);
    }

    /**
     * 登录失败次数限制
     */
    public boolean checkLoginFailCount(String username, int maxFailCount) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        Object countObj = redisService.get(key);
        
        if (countObj == null) {
            return true; // 未失败过
        }

        int count = (Integer) countObj;
        return count < maxFailCount;
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFail(String username, int lockTime) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        Long count = redisService.increment(key);
        
        if (count == 1) {
            // 第一次失败，设置过期时间
            redisService.expire(key, lockTime, TimeUnit.SECONDS);
        }
        
        log.warn("登录失败计数: username={}, count={}", username, count);
    }

    /**
     * 清除登录失败记录
     */
    public void clearLoginFailCount(String username) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        redisService.delete(key);
        log.debug("清除登录失败记录: username={}", username);
    }

    /**
     * 锁定账号
     */
    public void lockAccount(String username, int lockTime) {
        String key = RedisConstants.ACCOUNT_LOCK_KEY + username;
        redisService.set(key, "locked", lockTime, TimeUnit.SECONDS);
        log.warn("账号已锁定: username={}, lockTime={}s", username, lockTime);
    }

    /**
     * 检查账号是否被锁定
     */
    public boolean isAccountLocked(String username) {
        String key = RedisConstants.ACCOUNT_LOCK_KEY + username;
        Boolean locked = redisService.hasKey(key);
        return locked != null && locked;
    }

    /**
     * 解锁账号
     */
    public void unlockAccount(String username) {
        String key = RedisConstants.ACCOUNT_LOCK_KEY + username;
        redisService.delete(key);
        log.info("账号已解锁: username={}", username);
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public Long getAccountLockTTL(String username) {
        String key = RedisConstants.ACCOUNT_LOCK_KEY + username;
        return redisService.getExpire(key, TimeUnit.SECONDS);
    }
}

