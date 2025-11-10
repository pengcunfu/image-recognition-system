package com.pengcunfu.recognition.constant;

/**
 * JWT常量
 */
public class JwtConstants {

    /**
     * JWT令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT令牌头部
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * JWT Claims - 用户ID
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT Claims - 用户名
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT Claims - 用户角色
     */
    public static final String CLAIM_ROLE = "role";

    /**
     * JWT Claims - 颁发时间
     */
    public static final String CLAIM_ISSUED_AT = "iat";

    /**
     * JWT Claims - 过期时间
     */
    public static final String CLAIM_EXPIRATION = "exp";

    /**
     * JWT签名算法
     */
    public static final String ALGORITHM = "HS256";

    /**
     * Token类型
     */
    public static final String TOKEN_TYPE = "JWT";

    /**
     * Token过期时间（毫秒）- 7天
     */
    public static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    private JwtConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

