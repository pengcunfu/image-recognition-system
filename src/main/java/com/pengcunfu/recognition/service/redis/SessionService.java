package com.pengcunfu.recognition.service.redis;

import com.pengcunfu.recognition.constant.CacheKeyPrefix;
import com.pengcunfu.recognition.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 会话服务
 * 管理用户登录会话
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisService redisService;

    /**
     * 保存用户Token
     */
    public void saveToken(Long userId, String token, long expireSeconds) {
        String key = CacheKeyPrefix.USER_TOKEN.getKey(userId.toString());
        redisService.set(key, token, expireSeconds, TimeUnit.SECONDS);
        log.info("保存用户Token: userId={}, expire={}s", userId, expireSeconds);
    }

    /**
     * 获取用户Token
     */
    public String getToken(Long userId) {
        String key = CacheKeyPrefix.USER_TOKEN.getKey(userId.toString());
        Object token = redisService.get(key);
        return token != null ? token.toString() : null;
    }

    /**
     * 删除用户Token（登出）
     */
    public void removeToken(Long userId) {
        String key = CacheKeyPrefix.USER_TOKEN.getKey(userId.toString());
        redisService.delete(key);
        log.info("删除用户Token: userId={}", userId);
    }

    /**
     * 缓存用户信息
     */
    public void cacheUserInfo(Long userId, UserPrincipal userPrincipal) {
        String key = CacheKeyPrefix.USER_INFO.getKey(userId.toString());
        redisService.set(key, userPrincipal, CacheKeyPrefix.USER_INFO.getExpireTime(), TimeUnit.SECONDS);
        log.debug("缓存用户信息: userId={}", userId);
    }

    /**
     * 获取缓存的用户信息
     */
    public UserPrincipal getCachedUserInfo(Long userId) {
        String key = CacheKeyPrefix.USER_INFO.getKey(userId.toString());
        Object obj = redisService.get(key);
        return obj instanceof UserPrincipal ? (UserPrincipal) obj : null;
    }

    /**
     * 删除缓存的用户信息
     */
    public void removeCachedUserInfo(Long userId) {
        String key = CacheKeyPrefix.USER_INFO.getKey(userId.toString());
        redisService.delete(key);
        log.debug("删除缓存用户信息: userId={}", userId);
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        String key = CacheKeyPrefix.USER_TOKEN.getKey(userId.toString());
        Boolean exists = redisService.hasKey(key);
        return exists != null && exists;
    }

    /**
     * 刷新Token过期时间
     */
    public void refreshTokenExpire(Long userId, long expireSeconds) {
        String key = CacheKeyPrefix.USER_TOKEN.getKey(userId.toString());
        redisService.expire(key, expireSeconds, TimeUnit.SECONDS);
        log.debug("刷新Token过期时间: userId={}, expire={}s", userId, expireSeconds);
    }
}

