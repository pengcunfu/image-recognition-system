package com.pcf.recognition.dto;

import lombok.Data;

/**
 * 健康检查响应DTO
 */
@Data
public class HealthCheckResponseDto {

    private String status;

    private String service;

    private Long timestamp;

    private String version;
}
