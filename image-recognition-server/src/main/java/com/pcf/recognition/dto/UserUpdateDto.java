package com.pcf.recognition.dto;

import lombok.Data;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UserUpdateDto {

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 头像URL
     */
    private String avatar;
}
