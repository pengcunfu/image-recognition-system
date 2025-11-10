package com.pengcunfu.recognition.constant;

/**
 * 文件常量
 */
public class FileConstants {

    /**
     * 最大文件大小（字节）- 10MB
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /**
     * 最大图片大小（字节）- 5MB
     */
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;

    /**
     * 最大头像大小（字节）- 2MB
     */
    public static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024L;

    /**
     * 允许的图片格式
     */
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    };

    /**
     * 允许的图片MIME类型
     */
    public static final String[] ALLOWED_IMAGE_MIME_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    };

    /**
     * 默认上传目录
     */
    public static final String DEFAULT_UPLOAD_DIR = "uploads";

    /**
     * 图片上传目录
     */
    public static final String IMAGE_UPLOAD_DIR = "images";

    /**
     * 头像上传目录
     */
    public static final String AVATAR_UPLOAD_DIR = "avatars";

    /**
     * 临时文件目录
     */
    public static final String TEMP_UPLOAD_DIR = "temp";

    /**
     * 文件路径分隔符
     */
    public static final String FILE_SEPARATOR = "/";

    /**
     * 日期格式（用于文件路径）- yyyy/MM/dd
     */
    public static final String DATE_PATH_FORMAT = "yyyy/MM/dd";

    /**
     * 文件名最大长度
     */
    public static final int MAX_FILENAME_LENGTH = 100;

    /**
     * URL有效期（秒）- 1小时
     */
    public static final int URL_EXPIRATION = 3600;

    private FileConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

