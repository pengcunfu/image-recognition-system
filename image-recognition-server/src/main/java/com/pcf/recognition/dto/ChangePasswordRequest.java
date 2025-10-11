package com.pcf.recognition.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求DTO
 */
@Data

public class ChangePasswordRequest {


    @NotBlank(message = "原密码不能为空")
    private String oldPassword;


    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String newPassword;
}
