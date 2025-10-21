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

    /**
     * VIP订单查询请求（管理员功能）
     */
    @Data
    public static class QueryOrderRequest {
        private Integer page = 1;
        private Integer size = 10;
        private Long userId;
        private Integer planType;
        private Integer paymentStatus;
        private String startTime;
        private String endTime;
    }

    /**
     * 延长VIP时间请求（管理员功能）
     */
    @Data
    public static class ExtendVipRequest {
        @NotNull(message = "延长天数不能为空")
        private Integer days;
        private String reason;
    }

    /**
     * 更新VIP状态请求（管理员功能）
     */
    @Data
    public static class UpdateVipStatusRequest {
        @NotNull(message = "状态不能为空")
        private Integer status; // 0-取消VIP, 1-激活VIP
        private String reason;
    }

    /**
     * 更新VIP等级请求（管理员功能）
     */
    @Data
    public static class UpdateVipLevelRequest {
        @NotNull(message = "VIP等级不能为空")
        private Integer newLevel;
        private String reason;
    }

    /**
     * 切换VIP状态请求（管理员功能）
     */
    @Data
    public static class ToggleVipStatusRequest {
        @NotNull(message = "状态不能为空")
        private String status; // suspend-暂停, resume-恢复
        private String reason;
    }

    /**
     * 重置VIP使用量请求（管理员功能）
     */
    @Data
    public static class ResetVipUsageRequest {
        @NotNull(message = "重置类型不能为空")
        private String resetType; // all-全部, recognitions-识别次数, batch-批量任务, api-API调用
        private String reason;
    }

    /**
     * 撤销VIP权限请求（管理员功能）
     */
    @Data
    public static class RevokeVipRequest {
        private String reason;
    }
}
