package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * Token验证响应DTO
 */
@Data
@Builder
public class TokenValidationResponseDto {
    
    /** Token是否有效 */
    private Boolean valid;
    
    /** 用户信息 */
    private UserInfoDto userInfo;
}
