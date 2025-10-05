package com.pcf.recognition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新用户资料请求DTO
 */
@Data
@Schema(description = "更新用户资料请求")
public class UpdateProfileRequest {
    
    @Schema(description = "姓名", required = true, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "个人简介", example = "这是我的个人简介")
    private String bio;
}
