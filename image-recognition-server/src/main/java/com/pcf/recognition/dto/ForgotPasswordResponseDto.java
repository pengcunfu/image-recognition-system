package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 忘记密码响应DTO
 */
@Data
@Builder
public class ForgotPasswordResponseDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 响应消息 */
    private String message;
    
    /** 重置令牌（仅用于测试） */
    private String resetToken;
}
