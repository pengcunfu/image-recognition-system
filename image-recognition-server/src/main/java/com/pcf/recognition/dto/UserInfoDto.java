package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（用于返回给前端）
 */
@Data
@Builder
public class UserInfoDto {
    
    /** 用户ID */
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 邮箱 */
    private String email;
    
    /** 真实姓名 */
    private String name;
    
    /** 手机号 */
    private String phone;
    
    /** 头像URL */
    private String avatar;
    
    /** 个人简介 */
    private String bio;
    
    /** 用户角色 */
    private String role;
    
    /** 用户状态 */
    private String status;
    
    /** VIP等级 */
    private Integer vipLevel;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
