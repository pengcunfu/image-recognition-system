package com.pengcunfu.recognition.constant;

/**
 * Redis常量
 */
public class RedisConstants {

    /**
     * 键分隔符
     */
    public static final String KEY_SEPARATOR = ":";

    /**
     * 默认过期时间（秒）- 1小时
     */
    public static final long DEFAULT_EXPIRE_TIME = 3600L;

    /**
     * 短期过期时间（秒）- 5分钟
     */
    public static final long SHORT_EXPIRE_TIME = 300L;

    /**
     * 长期过期时间（秒）- 1天
     */
    public static final long LONG_EXPIRE_TIME = 86400L;

    /**
     * 超长期过期时间（秒）- 7天
     */
    public static final long EXTRA_LONG_EXPIRE_TIME = 604800L;

    // ==================== 缓存键前缀 ====================

    /**
     * 用户缓存键前缀
     */
    public static final String USER_KEY_PREFIX = "user";

    /**
     * 用户信息缓存键 - user:info:{userId}
     */
    public static final String USER_INFO_KEY = USER_KEY_PREFIX + KEY_SEPARATOR + "info" + KEY_SEPARATOR;

    /**
     * 用户Token缓存键 - user:token:{userId}
     */
    public static final String USER_TOKEN_KEY = USER_KEY_PREFIX + KEY_SEPARATOR + "token" + KEY_SEPARATOR;

    /**
     * 用户会话缓存键 - user:session:{userId}
     */
    public static final String USER_SESSION_KEY = USER_KEY_PREFIX + KEY_SEPARATOR + "session" + KEY_SEPARATOR;

    /**
     * 验证码缓存键前缀 - captcha:{type}:{target}
     */
    public static final String CAPTCHA_KEY_PREFIX = "captcha";

    /**
     * 邮箱验证码缓存键 - captcha:email:{email}
     */
    public static final String EMAIL_CODE_KEY = CAPTCHA_KEY_PREFIX + KEY_SEPARATOR + "email" + KEY_SEPARATOR;

    /**
     * 图形验证码缓存键 - captcha:image:{uuid}
     */
    public static final String IMAGE_CAPTCHA_KEY = CAPTCHA_KEY_PREFIX + KEY_SEPARATOR + "image" + KEY_SEPARATOR;

    /**
     * 识别结果缓存键 - recognition:result:{recognitionId}
     */
    public static final String RECOGNITION_RESULT_KEY = "recognition" + KEY_SEPARATOR + "result" + KEY_SEPARATOR;

    /**
     * 帖子缓存键 - post:info:{postId}
     */
    public static final String POST_INFO_KEY = "post" + KEY_SEPARATOR + "info" + KEY_SEPARATOR;

    /**
     * 帖子浏览次数缓存键 - post:view:{postId}
     */
    public static final String POST_VIEW_COUNT_KEY = "post" + KEY_SEPARATOR + "view" + KEY_SEPARATOR;

    /**
     * 知识缓存键 - knowledge:info:{knowledgeId}
     */
    public static final String KNOWLEDGE_INFO_KEY = "knowledge" + KEY_SEPARATOR + "info" + KEY_SEPARATOR;

    /**
     * 热门帖子缓存键 - hot:posts
     */
    public static final String HOT_POSTS_KEY = "hot" + KEY_SEPARATOR + "posts";

    /**
     * 热门知识缓存键 - hot:knowledge
     */
    public static final String HOT_KNOWLEDGE_KEY = "hot" + KEY_SEPARATOR + "knowledge";

    /**
     * 限流键前缀 - rate_limit:{type}:{identifier}
     */
    public static final String RATE_LIMIT_KEY_PREFIX = "rate_limit";

    /**
     * API限流键 - rate_limit:api:{userId}:{api}
     */
    public static final String API_RATE_LIMIT_KEY = RATE_LIMIT_KEY_PREFIX + KEY_SEPARATOR + "api" + KEY_SEPARATOR;

    /**
     * 识别限流键 - rate_limit:recognition:{userId}
     */
    public static final String RECOGNITION_RATE_LIMIT_KEY = RATE_LIMIT_KEY_PREFIX + KEY_SEPARATOR + "recognition" + KEY_SEPARATOR;

    /**
     * 登录失败次数键 - login:fail:{username}
     */
    public static final String LOGIN_FAIL_COUNT_KEY = "login" + KEY_SEPARATOR + "fail" + KEY_SEPARATOR;

    /**
     * 账号锁定键 - account:lock:{username}
     */
    public static final String ACCOUNT_LOCK_KEY = "account" + KEY_SEPARATOR + "lock" + KEY_SEPARATOR;

    /**
     * 在线用户键 - online:users
     */
    public static final String ONLINE_USERS_KEY = "online" + KEY_SEPARATOR + "users";

    /**
     * 统计数据缓存键 - stats:{type}
     */
    public static final String STATS_KEY_PREFIX = "stats";

    /**
     * 系统配置缓存键 - config:system
     */
    public static final String SYSTEM_CONFIG_KEY = "config" + KEY_SEPARATOR + "system";

    // ==================== 锁键前缀 ====================

    /**
     * 分布式锁键前缀
     */
    public static final String LOCK_KEY_PREFIX = "lock";

    /**
     * 订单锁键 - lock:order:{orderId}
     */
    public static final String ORDER_LOCK_KEY = LOCK_KEY_PREFIX + KEY_SEPARATOR + "order" + KEY_SEPARATOR;

    /**
     * 识别锁键 - lock:recognition:{userId}
     */
    public static final String RECOGNITION_LOCK_KEY = LOCK_KEY_PREFIX + KEY_SEPARATOR + "recognition" + KEY_SEPARATOR;

    private RedisConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

