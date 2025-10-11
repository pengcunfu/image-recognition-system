package com.pcf.recognition.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论DTO
 */
@Data
public class CommentDto {

    private Long id;

    private Long postId;

    private Long authorId;

    private Long parentId;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
