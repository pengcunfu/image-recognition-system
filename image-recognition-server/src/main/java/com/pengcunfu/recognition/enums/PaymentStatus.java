package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatus {
    
    /**
     * 未支付
     */
    UNPAID(0, "未支付"),
    
    /**
     * 已支付
     */
    PAID(1, "已支付"),
    
    /**
     * 已退款
     */
    REFUNDED(2, "已退款"),
    
    /**
     * 已取消
     */
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    PaymentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取值（用于数据库存储）
     */
    public Integer getValue() {
        return this.code;
    }

    /**
     * 根据code获取枚举
     */
    public static PaymentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status code: " + code);
    }

    /**
     * 判断是否为已支付状态
     */
    public boolean isPaid() {
        return this == PAID;
    }

    /**
     * 判断是否为待支付状态
     */
    public boolean isUnpaid() {
        return this == UNPAID;
    }
}

