package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String name;

    private String phone;

    private String avatar;

    private String bio;

    private UserRole role = UserRole.USER;

    private UserStatus status = UserStatus.ACTIVE;

    private Integer vipLevel = 0;

    private LocalDateTime vipExpireTime;

    private LocalDateTime lastLoginTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // 用户角色枚举
    @Getter
    public enum UserRole {
        USER(1, "普通用户"),
        VIP(2, "VIP用户"),
        ADMIN(3, "管理员");

        private final int value;
        private final String description;

        UserRole(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public static UserRole fromValue(int value) {
            for (UserRole role : values()) {
                if (role.value == value) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Invalid role value: " + value);
        }
    }

    // 用户状态枚举
    @Getter
    public enum UserStatus {
        ACTIVE(1, "激活"),
        INACTIVE(2, "未激活"),
        BANNED(3, "封禁"),
        DELETED(4, "已删除");

        private final int value;
        private final String description;

        UserStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public static UserStatus fromValue(int value) {
            for (UserStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status value: " + value);
        }
    }
}
