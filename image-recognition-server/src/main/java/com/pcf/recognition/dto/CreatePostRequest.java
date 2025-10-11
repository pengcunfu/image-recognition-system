package com.pcf.recognition.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建帖子请求DTO
 */
@Data

public class CreatePostRequest {


    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;


    @NotBlank(message = "内容不能为空")
    private String content;


    private String category;


    private String tags;
}
