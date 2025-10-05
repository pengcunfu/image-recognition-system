package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建帖子请求DTO
 */
@Data
@Schema(description = "创建帖子请求")
public class CreatePostRequest {
    
    @Schema(description = "帖子标题", required = true, example = "这是一个测试帖子")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
    
    @Schema(description = "帖子内容", required = true, example = "这是帖子的详细内容...")
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @Schema(description = "分类", example = "技术讨论")
    private String category;
    
    @Schema(description = "标签（JSON格式）", example = "[\"Java\",\"Spring\"]")
    private String tags;
}
