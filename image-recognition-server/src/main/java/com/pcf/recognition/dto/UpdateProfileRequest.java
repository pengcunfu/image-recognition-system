package com.pcf.recognition.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新用户资料请求DTO
 */
@Data

public class UpdateProfileRequest {


    @NotBlank(message = "姓名不能为空")
    private String name;


    private String phone;


    private String bio;
}
