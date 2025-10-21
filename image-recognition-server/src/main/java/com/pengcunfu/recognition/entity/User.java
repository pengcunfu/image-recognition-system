package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户表
 * 存储用户基本信息和账号数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 用户ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码(BCrypt加密存储)
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 用户角色: 0-USER普通用户, 1-VIP会员, 2-ADMIN管理员
     */
    private Integer role;

    /**
     * 账号状态: 0-ACTIVE激活, 1-BANNED封禁, 2-INACTIVE未激活
     */
    private Integer status;

    /**
     * VIP到期时间
     */
    private LocalDateTime vipExpireTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
