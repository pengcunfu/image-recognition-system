package com.pengcunfu.recognition.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * VIP套餐类型枚举
 */
@Getter
public enum VipPlanType {
    
    /**
     * 月度套餐
     */
    MONTHLY(0, "月度套餐", 30, new BigDecimal("30.00")),
    
    /**
     * 季度套餐
     */
    QUARTERLY(1, "季度套餐", 90, new BigDecimal("80.00")),
    
    /**
     * 年度套餐
     */
    YEARLY(2, "年度套餐", 365, new BigDecimal("300.00"));

    private final Integer code;
    private final String description;
    private final Integer days;
    private final BigDecimal price;

    VipPlanType(Integer code, String description, Integer days, BigDecimal price) {
        this.code = code;
        this.description = description;
        this.days = days;
        this.price = price;
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
    public static VipPlanType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (VipPlanType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VIP plan type code: " + code);
    }

    /**
     * 根据value获取枚举（兼容方法）
     */
    public static VipPlanType fromValue(Integer value) {
        return fromCode(value);
    }
}

