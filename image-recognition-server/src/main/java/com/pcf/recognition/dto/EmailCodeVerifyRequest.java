package com.pcf.recognition.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 邮箱验证码验证请求DTO
 */
@Data
public class EmailCodeVerifyRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须为6位数字")
    private String code;

    @NotBlank(message = "验证码类型不能为空")
    private String type; // register, forgot_password, change_email

}
