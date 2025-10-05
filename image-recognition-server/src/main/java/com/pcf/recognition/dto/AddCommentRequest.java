package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 添加评论请求DTO
 */
@Data
@Schema(description = "添加评论请求")
public class AddCommentRequest {
    
    @Schema(description = "评论内容", required = true, example = "这是一条评论")
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    @Schema(description = "父评论ID（回复时使用）", example = "123")
    private Long parentId;
}
