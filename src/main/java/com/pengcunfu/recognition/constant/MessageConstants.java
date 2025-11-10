package com.pengcunfu.recognition.constant;

/**
 * 消息常量
 * 统一管理系统中的提示消息
 */
public class MessageConstants {

    // ==================== 通用消息 ====================
    
    public static final String SUCCESS = "操作成功";
    public static final String FAILED = "操作失败";
    public static final String SYSTEM_ERROR = "系统错误，请稍后重试";
    public static final String PARAM_ERROR = "参数错误";
    public static final String NOT_FOUND = "资源不存在";

    // ==================== 用户模块消息 ====================
    
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String USERNAME_EXISTS = "用户名已存在";
    public static final String EMAIL_EXISTS = "邮箱已被注册";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String USER_BANNED = "账号已被封禁";
    public static final String USER_INACTIVE = "账号未激活，请先激活";
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String LOGOUT_SUCCESS = "退出登录成功";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String UPDATE_PROFILE_SUCCESS = "更新资料成功";
    public static final String CHANGE_PASSWORD_SUCCESS = "修改密码成功";
    public static final String RESET_PASSWORD_SUCCESS = "重置密码成功";

    // ==================== 认证模块消息 ====================
    
    public static final String TOKEN_INVALID = "Token无效，请重新登录";
    public static final String TOKEN_EXPIRED = "Token已过期，请重新登录";
    public static final String UNAUTHORIZED = "未授权，请先登录";
    public static final String PERMISSION_DENIED = "权限不足";
    public static final String CAPTCHA_ERROR = "验证码错误";
    public static final String CAPTCHA_EXPIRED = "验证码已过期，请重新获取";
    public static final String CAPTCHA_SEND_SUCCESS = "验证码已发送";

    // ==================== 识别模块消息 ====================
    
    public static final String RECOGNITION_SUCCESS = "识别成功";
    public static final String RECOGNITION_FAILED = "识别失败";
    public static final String RECOGNITION_PROCESSING = "识别处理中";
    public static final String IMAGE_FORMAT_ERROR = "图片格式不支持";
    public static final String IMAGE_SIZE_ERROR = "图片大小超出限制";
    public static final String RECOGNITION_LIMIT_EXCEEDED = "识别次数已用完，请升级VIP";
    public static final String RECOGNITION_NOT_FOUND = "识别记录不存在";

    // ==================== 社区模块消息 ====================
    
    public static final String POST_CREATE_SUCCESS = "发布帖子成功";
    public static final String POST_UPDATE_SUCCESS = "更新帖子成功";
    public static final String POST_DELETE_SUCCESS = "删除帖子成功";
    public static final String POST_NOT_FOUND = "帖子不存在";
    public static final String POST_PERMISSION_DENIED = "无权操作此帖子";
    public static final String POST_UNDER_REVIEW = "帖子审核中";
    public static final String POST_DELETED = "帖子已被删除";
    
    public static final String COMMENT_CREATE_SUCCESS = "发表评论成功";
    public static final String COMMENT_UPDATE_SUCCESS = "更新评论成功";
    public static final String COMMENT_DELETE_SUCCESS = "删除评论成功";
    public static final String COMMENT_NOT_FOUND = "评论不存在";
    public static final String COMMENT_PERMISSION_DENIED = "无权操作此评论";
    
    public static final String LIKE_SUCCESS = "点赞成功";
    public static final String UNLIKE_SUCCESS = "取消点赞成功";
    public static final String COLLECT_SUCCESS = "收藏成功";
    public static final String UNCOLLECT_SUCCESS = "取消收藏成功";

    // ==================== 知识库模块消息 ====================
    
    public static final String KNOWLEDGE_CREATE_SUCCESS = "创建知识成功";
    public static final String KNOWLEDGE_UPDATE_SUCCESS = "更新知识成功";
    public static final String KNOWLEDGE_DELETE_SUCCESS = "删除知识成功";
    public static final String KNOWLEDGE_NOT_FOUND = "知识不存在";
    public static final String KNOWLEDGE_PERMISSION_DENIED = "无权操作此知识";
    public static final String KNOWLEDGE_UNDER_REVIEW = "知识审核中";

    // ==================== VIP模块消息 ====================
    
    public static final String VIP_EXPIRED = "VIP已过期，请续费";
    public static final String NOT_VIP_USER = "非VIP用户，请先开通VIP";
    public static final String ORDER_CREATE_SUCCESS = "订单创建成功";
    public static final String ORDER_NOT_FOUND = "订单不存在";
    public static final String ORDER_ALREADY_PAID = "订单已支付";
    public static final String ORDER_CANCELLED = "订单已取消";
    public static final String PAYMENT_SUCCESS = "支付成功";
    public static final String PAYMENT_FAILED = "支付失败";

    // ==================== 文件模块消息 ====================
    
    public static final String FILE_UPLOAD_SUCCESS = "文件上传成功";
    public static final String FILE_UPLOAD_FAILED = "文件上传失败";
    public static final String FILE_NOT_FOUND = "文件不存在";
    public static final String FILE_TYPE_ERROR = "文件类型不支持";
    public static final String FILE_SIZE_ERROR = "文件大小超出限制";
    public static final String FILE_DELETE_SUCCESS = "文件删除成功";

    // ==================== 通知模块消息 ====================
    
    public static final String NOTIFICATION_NOT_FOUND = "通知不存在";
    public static final String NOTIFICATION_READ_SUCCESS = "标记已读成功";
    public static final String NOTIFICATION_DELETE_SUCCESS = "删除通知成功";
    public static final String EMAIL_SEND_SUCCESS = "邮件发送成功";
    public static final String EMAIL_SEND_FAILED = "邮件发送失败";

    // ==================== 限流消息 ====================
    
    public static final String OPERATION_TOO_FREQUENT = "操作过于频繁，请稍后再试";
    public static final String API_LIMIT_EXCEEDED = "API调用次数超限";
    public static final String LOGIN_FAIL_LIMIT_EXCEEDED = "登录失败次数过多，账号已被锁定";
    public static final String ACCOUNT_LOCKED = "账号已被锁定，请稍后再试";

    // ==================== 管理员模块消息 ====================
    
    public static final String AUDIT_SUCCESS = "审核成功";
    public static final String AUDIT_REJECT = "审核拒绝";
    public static final String BAN_USER_SUCCESS = "封禁用户成功";
    public static final String UNBAN_USER_SUCCESS = "解封用户成功";
    public static final String TOP_POST_SUCCESS = "置顶帖子成功";
    public static final String UNTOP_POST_SUCCESS = "取消置顶成功";
    public static final String FEATURE_POST_SUCCESS = "精选帖子成功";
    public static final String UNFEATURE_POST_SUCCESS = "取消精选成功";

    private MessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

