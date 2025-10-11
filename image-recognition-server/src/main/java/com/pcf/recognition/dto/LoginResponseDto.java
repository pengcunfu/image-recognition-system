package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 登录响应DTO
 */
@Data
@Builder
public class LoginResponseDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 响应消息 */
    private String message;
    
    /** 访问令牌 */
    private String token;
    
    /** 用户信息 */
    private UserInfoDto user;
}
