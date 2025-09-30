package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 短信验证码请求
 */
@Data
@Schema(description = "短信验证码请求")
public class SmsCodeRequest {
    
    @Schema(description = "手机号", example = "13812345678", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "验证码类型", example = "register", allowableValues = {"register", "login", "forgot"})
    private String type;
}
