package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名或邮箱", required = true, example = "user123")
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "密码", required = true, example = "password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;
    
    @Schema(description = "验证码", example = "A1B2")
    private String captcha;
    
    @Schema(description = "记住我", example = "false")
    private boolean rememberMe = false;
}
