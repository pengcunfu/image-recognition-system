package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 帖子状态枚举
 */
@Getter
public enum PostStatus {
    
    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    
    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),
    
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),
    
    /**
     * 已删除
     */
    DELETED(3, "已删除");

    private final Integer code;
    private final String description;

    PostStatus(Integer code, String description) {
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
    public static PostStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PostStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid post status code: " + code);
    }

    /**
     * 判断是否为已发布状态
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }
}

