package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP订单表
 * VIP会员订单记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("vip_orders")
public class VipOrder {

    /**
     * 订单ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(关联users表)
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 套餐类型: 0-MONTHLY月度, 1-QUARTERLY季度, 2-YEARLY年度
     */
    private Integer planType;

    /**
     * 套餐名称
     */
    private String planName;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 支付方式: 0-ALIPAY支付宝, 1-WECHAT微信, 2-BALANCE余额
     */
    private Integer paymentMethod;

    /**
     * 支付状态: 0-UNPAID未支付, 1-PAID已支付, 2-REFUNDED已退款, 3-CANCELLED已取消
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 生效时间
     */
    private LocalDateTime startTime;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

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
}

