package com.pengcunfu.recognition.request.admin;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 管理后台 - 用户管理请求
 */
public class AdminUserRequest {

    /**
     * 用户查询请求
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
    }

    /**
     * 更新用户状态请求
     */
    @Data
    public static class UpdateUserStatusRequest {
        private Integer status;
        private String reason;
    }

    /**
     * 更新用户角色请求
     */
    @Data
    public static class UpdateUserRoleRequest {
        private Integer role;
    }

    /**
     * 创建用户请求
     */
    @Data
    public static class CreateUserRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        private String password;

        private String nickname;
        private Integer role;
    }
}
