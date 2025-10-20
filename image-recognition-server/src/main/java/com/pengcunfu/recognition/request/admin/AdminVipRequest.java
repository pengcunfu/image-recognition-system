package com.pengcunfu.recognition.request.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理后台 - VIP管理请求
 */
public class AdminVipRequest {

    /**
     * VIP订单查询请求
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
     * 延长VIP时间请求
     */
    @Data
    public static class ExtendVipRequest {
        @NotNull(message = "延长天数不能为空")
        private Integer days;
        private String reason;
    }

    /**
     * 更新VIP状态请求
     */
    @Data
    public static class UpdateVipStatusRequest {
        @NotNull(message = "状态不能为空")
        private Integer status; // 0-取消VIP, 1-激活VIP
        private String reason;
    }
}
