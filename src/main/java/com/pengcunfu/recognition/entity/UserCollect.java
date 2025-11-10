package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户收藏表
 * 记录用户收藏行为(帖子、知识)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("user_collects")
public class UserCollect {

    /**
     * 记录ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(关联users表)
     */
    private Long userId;

    /**
     * 收藏对象类型: 0-POST帖子, 1-KNOWLEDGE知识
     */
    private Integer targetType;

    /**
     * 收藏对象ID
     */
    private Long targetId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

