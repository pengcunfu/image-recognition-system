package com.pcf.recognition.dto;

import lombok.Data;

/**
 * Doubao连接测试响应DTO
 */
@Data
public class DoubaoConnectionTestResponseDto {

    private Boolean connected;

    private String message;

    private Long timestamp;

    private String error;
}
