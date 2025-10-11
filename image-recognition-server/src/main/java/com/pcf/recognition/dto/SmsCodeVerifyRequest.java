package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 短信验证码验证请求DTO
 */
@Data
@Builder
public class SmsCodeVerifyRequest {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;
}