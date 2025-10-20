package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatus {
    
    /**
     * 激活
     */
    ACTIVE(0, "激活"),
    
    /**
     * 封禁
     */
    BANNED(1, "封禁"),
    
    /**
     * 未激活
     */
    INACTIVE(2, "未激活");

    private final Integer code;
    private final String description;

    UserStatus(Integer code, String description) {
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
    public static UserStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid user status code: " + code);
    }

    /**
     * 判断是否为激活状态
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}

