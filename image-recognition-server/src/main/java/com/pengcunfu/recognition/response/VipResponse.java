package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP响应DTO
 */
public class VipResponse {

    /**
     * 订单信息（通用）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfo {
        private Long id;
        private Long userId;
        private String orderNo;
        private Integer planType;
        private String planName;
        private BigDecimal amount;
        private Integer paymentMethod;
        private Integer paymentStatus;
        private LocalDateTime paymentTime;
        private LocalDateTime startTime;
        private LocalDateTime expireTime;
        private LocalDateTime createdAt;
        private LocalDateTime createTime; // 兼容字段
    }

    /**
     * VIP状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipStatus {
        private Boolean isVip;
        private Integer level;
        private LocalDateTime expireTime;
        private Long remainingDays;
    }

    /**
     * VIP套餐
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipPlan {
        private Integer planType;
        private String planName;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer days;
        private String description;
        private String features;
    }

    /**
     * VIP订单详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetail {
        private Long id;
        private String orderNo;
        private Integer planType;
        private String planName;
        private BigDecimal amount;
        private Integer paymentMethod;
        private Integer paymentStatus;
        private LocalDateTime paymentTime;
        private LocalDateTime startTime;
        private LocalDateTime expireTime;
        private LocalDateTime createdAt;
    }
}
