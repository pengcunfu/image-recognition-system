package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求DTO
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    
    @Schema(description = "原密码", required = true, example = "oldpassword123")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @Schema(description = "新密码", required = true, example = "newpassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String newPassword;
}
