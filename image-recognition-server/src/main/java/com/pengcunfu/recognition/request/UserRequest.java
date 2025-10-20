package com.pengcunfu.recognition.request;

import lombok.Data;

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
}

