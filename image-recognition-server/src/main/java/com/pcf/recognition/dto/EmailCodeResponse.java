package com.pcf.recognition.dto;

import lombok.Data;

/**
 * 邮箱验证码响应DTO
 */
@Data
public class EmailCodeResponse {

    private String email;
    private String maskedEmail; // 脱敏后的邮箱
    private Integer codeExpiry; // 验证码过期时间（秒）
    private Long sendTime; // 发送时间戳

}
