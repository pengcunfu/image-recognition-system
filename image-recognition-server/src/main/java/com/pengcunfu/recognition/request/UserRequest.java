package com.pengcunfu.recognition.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户请求
 */
public class UserRequest {

    /**
     * 更新用户信息请求
     */
    @Data
    public static class UpdateProfileRequest {
        private String nickname;

        @Size(max = 20, message = "手机号长度不能超过20个字符")
        private String phone;

        private String avatar;

        @Size(max = 500, message = "简介长度不能超过500个字符")
        private String bio;
    }

    /**
     * 修改密码请求
     */
    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    /**
     * 用户查询请求（管理员功能）
     */
    @Data
    public static class QueryUserRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String keyword;
        private Integer role;
        private Integer status;
        private String startTime;
        private String endTime;
        private String sortBy;       // 排序字段: createTime, lastLoginTime, username
        private String sortOrder;    // 排序顺序: asc, desc
    }

    /**
     * 更新用户状态请求（管理员功能）
     */
    @Data
    public static class UpdateUserStatusRequest {
        private Integer status;
        private String reason;
    }

    /**
     * 更新用户角色请求（管理员功能）
     */
    @Data
    public static class UpdateUserRoleRequest {
        private Integer role;
    }

    /**
     * 创建用户请求（管理员功能）
     */
    @Data
    public static class CreateUserRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度在 3 到 20 个字符")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码长度至少 6 个字符")
        private String password;

        private String nickname;
        
        @Size(max = 20, message = "手机号长度不能超过20个字符")
        private String phone;
        
        private String avatar;
        
        private Integer role;  // 0-USER, 1-VIP, 不允许创建管理员(2)
        
        private Integer status; // 0-INACTIVE, 1-ACTIVE, 2-BANNED
    }

    /**
     * 更新用户信息请求（管理员功能）
     */
    @Data
    public static class UpdateUserRequest {
        @Email(message = "邮箱格式不正确")
        private String email;

        private String nickname;
        
        @Size(max = 20, message = "手机号长度不能超过20个字符")
        private String phone;
        
        private String avatar;
        
        private Integer role;  // 0-USER, 1-VIP, 不允许修改为管理员(2)
        
        private Integer status; // 0-INACTIVE, 1-ACTIVE, 2-BANNED
    }

    /**
     * 更新用户余额请求（管理员功能）
     */
    @Data
    public static class UpdateBalanceRequest {
        @NotBlank(message = "操作类型不能为空")
        private String type; // add-充值, deduct-扣除
        
        private java.math.BigDecimal amount;
        
        private String reason; // 操作原因
    }
}

