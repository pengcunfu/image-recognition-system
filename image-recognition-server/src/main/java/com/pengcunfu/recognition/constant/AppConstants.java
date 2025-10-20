package com.pengcunfu.recognition.constant;

/**
 * 应用常量
 */
public class AppConstants {

    /**
     * 应用名称
     */
    public static final String APP_NAME = "image-recognition-system";

    /**
     * 应用版本
     */
    public static final String APP_VERSION = "1.0.0";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 字符编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 日期时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区
     */
    public static final String TIMEZONE = "Asia/Shanghai";

    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png";

    /**
     * 系统用户ID（用于系统操作）
     */
    public static final Long SYSTEM_USER_ID = 0L;

    /**
     * 管理员用户名
     */
    public static final String ADMIN_USERNAME = "admin";

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

