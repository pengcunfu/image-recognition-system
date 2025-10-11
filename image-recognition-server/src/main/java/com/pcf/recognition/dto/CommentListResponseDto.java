package com.pcf.recognition.dto;

import lombok.Data;

import java.util.List;

/**
 * 评论列表响应DTO
 */
@Data
public class CommentListResponseDto {

    private List<CommentDto> data;

    private Long total;

    private Long pages;

    private Long current;

    private Long size;
}
