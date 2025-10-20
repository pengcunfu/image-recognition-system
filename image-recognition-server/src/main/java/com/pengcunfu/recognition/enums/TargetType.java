package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 目标类型枚举
 * 用于评论、点赞、收藏等操作的目标对象类型
 */
@Getter
public enum TargetType {
    
    /**
     * 帖子
     */
    POST(0, "帖子"),
    
    /**
     * 评论
     */
    COMMENT(1, "评论"),
    
    /**
     * 知识
     */
    KNOWLEDGE(2, "知识");

    private final Integer code;
    private final String description;

    TargetType(Integer code, String description) {
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
    public static TargetType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TargetType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid target type code: " + code);
    }
}

