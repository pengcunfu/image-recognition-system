package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 注册响应DTO
 */
@Data
@Builder
public class RegisterResponseDto {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 新用户ID
     */
    private Long userId;
}
