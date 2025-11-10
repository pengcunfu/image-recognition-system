package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NotificationType {
    SYSTEM(0, "系统通知"),
    COMMENT(1, "评论通知"),
    LIKE(2, "点赞通知"),
    COLLECT(3, "收藏通知"),
    AUDIT(4, "审核通知"),
    VIP(5, "VIP通知"),
    REPORT(6, "举报通知"),
    RECOGNITION(7, "识别完成"),
    REPLY(8, "回复通知");

    private final Integer value;
    private final String description;

    NotificationType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public static NotificationType fromValue(Integer value) {
        for (NotificationType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
