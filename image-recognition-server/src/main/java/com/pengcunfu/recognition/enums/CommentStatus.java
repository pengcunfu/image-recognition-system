package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 评论状态枚举
 */
@Getter
public enum CommentStatus {
    
    /**
     * 已发布
     */
    PUBLISHED(0, "已发布"),
    
    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final Integer code;
    private final String description;

    CommentStatus(Integer code, String description) {
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
    public static CommentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CommentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid comment status code: " + code);
    }
}

