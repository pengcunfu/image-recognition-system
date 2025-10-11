package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 验证码响应DTO
 */
@Data
@Builder
public class CaptchaResponseDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 验证码文本（用于测试，生产环境不应返回） */
    private String captcha;
    
    /** 验证码图片（Base64编码） */
    private String captchaImage;
    
    /** 验证码ID */
    private String captchaId;
}
