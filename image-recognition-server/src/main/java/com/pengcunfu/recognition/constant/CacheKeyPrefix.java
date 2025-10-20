package com.pengcunfu.recognition.constant;

/**
 * 缓存键前缀枚举
 * 用于Redis缓存键的统一管理
 */
public enum CacheKeyPrefix {
    
    /**
     * 用户信息缓存
     */
    USER_INFO("user:info:", 3600L),
    
    /**
     * 用户Token缓存
     */
    USER_TOKEN("user:token:", 604800L),
    
    /**
     * 邮箱验证码缓存
     */
    EMAIL_CODE("captcha:email:", 300L),
    
    /**
     * 图形验证码缓存
     */
    IMAGE_CAPTCHA("captcha:image:", 300L),
    
    /**
     * 识别结果缓存
     */
    RECOGNITION_RESULT("recognition:result:", 3600L),
    
    /**
     * 帖子信息缓存
     */
    POST_INFO("post:info:", 1800L),
    
    /**
     * 知识信息缓存
     */
    KNOWLEDGE_INFO("knowledge:info:", 3600L),
    
    /**
     * 热门帖子缓存
     */
    HOT_POSTS("hot:posts", 600L),
    
    /**
     * 热门知识缓存
     */
    HOT_KNOWLEDGE("hot:knowledge", 600L),
    
    /**
     * API限流缓存
     */
    API_RATE_LIMIT("rate_limit:api:", 60L),
    
    /**
     * 识别限流缓存
     */
    RECOGNITION_RATE_LIMIT("rate_limit:recognition:", 60L),
    
    /**
     * 登录失败次数缓存
     */
    LOGIN_FAIL_COUNT("login:fail:", 1800L),
    
    /**
     * 账号锁定缓存
     */
    ACCOUNT_LOCK("account:lock:", 1800L);

    /**
     * 键前缀
     */
    private final String prefix;
    
    /**
     * 过期时间（秒）
     */
    private final Long expireTime;

    CacheKeyPrefix(String prefix, Long expireTime) {
        this.prefix = prefix;
        this.expireTime = expireTime;
    }

    public String getPrefix() {
        return prefix;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    /**
     * 生成完整的缓存键
     */
    public String getKey(String suffix) {
        return prefix + suffix;
    }
}

