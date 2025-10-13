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
        USER("普通用户"),
        VIP("VIP用户"),
        ADMIN("管理员");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

    }

    // 用户状态枚举
    @Getter
    public enum UserStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        BANNED("封禁"),
        DELETED("已删除");

        private final String description;

        UserStatus(String description) {
            this.description = description;
        }
    }
}
