package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 知识创建响应DTO
 */
@Data
@Builder
public class KnowledgeCreateResponseDto {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 创建的ID
     */
    private Long id;
}
