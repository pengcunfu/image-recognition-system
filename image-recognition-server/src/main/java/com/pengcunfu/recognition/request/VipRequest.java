package com.pengcunfu.recognition.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * VIP请求
 */
public class VipRequest {

    /**
     * 创建VIP订单请求
     */
    @Data
    public static class CreateOrderRequest {
        @NotNull(message = "套餐类型不能为空")
        private Integer planType; // 0-MONTHLY, 1-QUARTERLY, 2-YEARLY
    }

    /**
     * 支付订单请求
     */
    @Data
    public static class PayOrderRequest {
        @NotNull(message = "支付方式不能为空")
        private Integer paymentMethod; // 0-ALIPAY, 1-WECHAT, 2-BALANCE
    }

    /**
     * 取消订单请求
     */
    @Data
    public static class CancelOrderRequest {
        private String reason;
    }

    /**
     * 申请退款请求
     */
    @Data
    public static class RefundRequest {
        private String reason;
    }
}
