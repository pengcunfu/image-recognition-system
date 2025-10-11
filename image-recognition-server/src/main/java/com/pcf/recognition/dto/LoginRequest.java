package com.pcf.recognition.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求DTO
 */
@Data

public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    private boolean rememberMe = false;
}
