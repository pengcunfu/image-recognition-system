package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NotificationType {
    
    /**
     * 系统通知
     */
    SYSTEM(0, "系统通知"),
    
    /**
     * 评论通知
     */
    COMMENT(1, "评论通知"),
    
    /**
     * 点赞通知
     */
    LIKE(2, "点赞通知"),
    
    /**
     * 收藏通知
     */
    COLLECT(3, "收藏通知"),
    
    /**
     * 审核通知
     */
    AUDIT(4, "审核通知"),
    
    /**
     * VIP通知
     */
    VIP(5, "VIP通知"),
    
    /**
     * 举报通知
     */
    REPORT(6, "举报通知");

    private final Integer code;
    private final String description;

    NotificationType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取值（用于数据库存储）
     */
    public Integer getValue() {
        return this.code;
    }

    /**
     * 根据code获取枚举
     */
    public static NotificationType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid notification type code: " + code);
    }
}

