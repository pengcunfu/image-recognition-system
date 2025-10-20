package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    
    /**
     * 普通用户
     */
    USER(0, "普通用户", "ROLE_USER"),
    
    /**
     * VIP会员
     */
    VIP(1, "VIP会员", "ROLE_VIP"),
    
    /**
     * 管理员
     */
    ADMIN(2, "管理员", "ROLE_ADMIN");

    private final Integer code;
    private final String description;
    private final String authority;

    UserRole(Integer code, String description, String authority) {
        this.code = code;
        this.description = description;
        this.authority = authority;
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
    public static UserRole fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid user role code: " + code);
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * 判断是否为VIP
     */
    public boolean isVip() {
        return this == VIP || this == ADMIN;
    }
}

