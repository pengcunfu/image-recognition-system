package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 操作结果响应DTO
 */
@Data
@Builder
public class OperationResultDto {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;
}
