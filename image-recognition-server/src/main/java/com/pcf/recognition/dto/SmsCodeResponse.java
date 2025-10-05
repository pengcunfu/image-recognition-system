package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短信验证码响应
 */
@Data
@Schema(description = "短信验证码响应")
public class SmsCodeResponse {
    
    @Schema(description = "脱敏后的手机号", example = "138****5678")
    private String phone;
    
    @Schema(description = "验证码过期时间(秒)", example = "300")
    private Integer codeExpiry;
    
    @Schema(description = "发送时间戳", example = "1640995200000")
    private Long sendTime;
    
    @Schema(description = "发送状态", example = "success")
    private String status = "success";
}
