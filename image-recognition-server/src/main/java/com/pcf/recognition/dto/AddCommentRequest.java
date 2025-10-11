package com.pcf.recognition.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 添加评论请求DTO
 */
@Data

public class AddCommentRequest {


    @NotBlank(message = "评论内容不能为空")
    private String content;


    private Long parentId;
}
