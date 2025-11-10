package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PaymentMethod {
    
    /**
     * 支付宝
     */
    ALIPAY(0, "支付宝", "alipay"),
    
    /**
     * 微信支付
     */
    WECHAT(1, "微信支付", "wechat"),
    
    /**
     * 余额支付
     */
    BALANCE(2, "余额支付", "balance");

    private final Integer code;
    private final String description;
    private final String channel;

    PaymentMethod(Integer code, String description, String channel) {
        this.code = code;
        this.description = description;
        this.channel = channel;
    }

    /**
     * 根据code获取枚举
     */
    public static PaymentMethod fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid payment method code: " + code);
    }
}

