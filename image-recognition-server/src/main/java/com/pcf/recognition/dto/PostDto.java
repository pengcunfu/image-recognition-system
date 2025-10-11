package com.pcf.recognition.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子DTO
 */
@Data
public class PostDto {

    private Long id;

    private String title;

    private String content;

    private Long authorId;

    private String category;

    private String tags;

    private String images;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer shareCount;

    private Boolean isTop;

    private Boolean isFeatured;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
