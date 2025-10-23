package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 通知表
 * 存储用户通知消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("notifications")
public class Notification {

    /**
     * 通知ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(关联users表)
     */
    private Long userId;

    /**
     * 通知类型: 0-SYSTEM系统, 1-COMMENT评论, 2-LIKE点赞, 3-COLLECT收藏, 4-AUDIT审核, 5-VIP会员, 6-REPORT举报
     */
    private Integer type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 附加数据(JSON格式)
     */
    private String data;

    /**
     * 跳转链接
     */
    private String linkUrl;

    /**
     * 发送者ID(关联users表,系统通知为NULL)
     */
    private Long senderId;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

