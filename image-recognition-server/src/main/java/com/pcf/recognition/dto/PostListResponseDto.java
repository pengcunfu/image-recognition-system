package com.pcf.recognition.dto;

import lombok.Data;

import java.util.List;

/**
 * 帖子列表响应DTO
 */
@Data
public class PostListResponseDto {
    
    private List<PostDto> data;
    
    private Long total;
    
    private Long pages;
    
    private Long current;
    
    private Long size;
}
