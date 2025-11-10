package com.pengcunfu.recognition.constant;

/**
 * 错误码常量
 * 错误码格式：{模块代码}{错误类型}{具体错误}
 * 例如：10001 - 1(用户模块) 00(参数错误) 01(第1个错误)
 */
public class ErrorCode {

    // ==================== 通用错误码 (0xxxx) ====================
    
    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 系统错误
     */
    public static final int SYSTEM_ERROR = 500;

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;

    /**
     * 无效参数
     */
    public static final int INVALID_PARAM = 400;

    /**
     * 内部错误
     */
    public static final int INTERNAL_ERROR = 500;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 请求过于频繁
     */
    public static final int TOO_MANY_REQUESTS = 429;

    // ==================== 用户模块错误码 (1xxxx) ====================

    /**
     * 用户不存在
     */
    public static final int USER_NOT_FOUND = 10001;

    /**
     * 用户名已存在
     */
    public static final int USERNAME_EXISTS = 10002;

    /**
     * 邮箱已存在
     */
    public static final int EMAIL_EXISTS = 10003;

    /**
     * 密码错误
     */
    public static final int PASSWORD_ERROR = 10004;

    /**
     * 账号已被封禁
     */
    public static final int USER_BANNED = 10005;

    /**
     * 账号未激活
     */
    public static final int USER_INACTIVE = 10006;

    /**
     * Token无效
     */
    public static final int TOKEN_INVALID = 10007;

    /**
     * Token已过期
     */
    public static final int TOKEN_EXPIRED = 10008;

    /**
     * 权限不足
     */
    public static final int PERMISSION_DENIED = 10009;

    /**
     * 验证码错误
     */
    public static final int CAPTCHA_ERROR = 10010;

    /**
     * 验证码已过期
     */
    public static final int CAPTCHA_EXPIRED = 10011;

    // ==================== 识别模块错误码 (2xxxx) ====================

    /**
     * 识别失败
     */
    public static final int RECOGNITION_FAILED = 20001;

    /**
     * 图片格式不支持
     */
    public static final int IMAGE_FORMAT_ERROR = 20002;

    /**
     * 图片大小超限
     */
    public static final int IMAGE_SIZE_ERROR = 20003;

    /**
     * 识别次数已用完
     */
    public static final int RECOGNITION_LIMIT_EXCEEDED = 20004;

    /**
     * 识别结果不存在
     */
    public static final int RECOGNITION_NOT_FOUND = 20005;

    // ==================== 社区模块错误码 (3xxxx) ====================

    /**
     * 帖子不存在
     */
    public static final int POST_NOT_FOUND = 30001;

    /**
     * 评论不存在
     */
    public static final int COMMENT_NOT_FOUND = 30002;

    /**
     * 无权操作此帖子
     */
    public static final int POST_PERMISSION_DENIED = 30003;

    /**
     * 无权操作此评论
     */
    public static final int COMMENT_PERMISSION_DENIED = 30004;

    /**
     * 帖子审核中
     */
    public static final int POST_UNDER_REVIEW = 30005;

    /**
     * 帖子已被删除
     */
    public static final int POST_DELETED = 30006;

    // ==================== 知识库模块错误码 (4xxxx) ====================

    /**
     * 知识不存在
     */
    public static final int KNOWLEDGE_NOT_FOUND = 40001;

    /**
     * 无权操作此知识
     */
    public static final int KNOWLEDGE_PERMISSION_DENIED = 40002;

    /**
     * 知识审核中
     */
    public static final int KNOWLEDGE_UNDER_REVIEW = 40003;

    // ==================== VIP模块错误码 (5xxxx) ====================

    /**
     * VIP已过期
     */
    public static final int VIP_EXPIRED = 50001;

    /**
     * 非VIP用户
     */
    public static final int NOT_VIP_USER = 50002;

    /**
     * 订单不存在
     */
    public static final int ORDER_NOT_FOUND = 50003;

    /**
     * 订单已支付
     */
    public static final int ORDER_ALREADY_PAID = 50004;

    /**
     * 订单已取消
     */
    public static final int ORDER_CANCELLED = 50005;

    /**
     * 支付失败
     */
    public static final int PAYMENT_FAILED = 50006;

    // ==================== 文件模块错误码 (6xxxx) ====================

    /**
     * 文件上传失败
     */
    public static final int FILE_UPLOAD_FAILED = 60001;

    /**
     * 文件不存在
     */
    public static final int FILE_NOT_FOUND = 60002;

    /**
     * 文件类型不支持
     */
    public static final int FILE_TYPE_ERROR = 60003;

    /**
     * 文件大小超限
     */
    public static final int FILE_SIZE_ERROR = 60004;

    // ==================== 通知模块错误码 (7xxxx) ====================

    /**
     * 通知不存在
     */
    public static final int NOTIFICATION_NOT_FOUND = 70001;

    /**
     * 邮件发送失败
     */
    public static final int EMAIL_SEND_FAILED = 70002;

    // ==================== 限流错误码 (8xxxx) ====================

    /**
     * 操作过于频繁
     */
    public static final int OPERATION_TOO_FREQUENT = 80001;

    /**
     * API调用超限
     */
    public static final int API_LIMIT_EXCEEDED = 80002;

    /**
     * 登录失败次数过多
     */
    public static final int LOGIN_FAIL_LIMIT_EXCEEDED = 80003;

    /**
     * 账号已被锁定
     */
    public static final int ACCOUNT_LOCKED = 80004;

    private ErrorCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

