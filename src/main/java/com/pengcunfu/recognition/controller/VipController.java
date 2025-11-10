package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.VipRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.VipResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.VipOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * VIP订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/vip")
@RequiredArgsConstructor
@Role("USER")
public class VipController {

    private final VipOrderService vipOrderService;

    /**
     * 创建VIP订单
     */
    @PostMapping("/orders")
    public ApiResponse<VipResponse.OrderInfo> createOrder(
            @Valid @RequestBody VipRequest.CreateOrderRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("创建VIP订单: userId={}, planType={}", userId, request.getPlanType());
        VipResponse.OrderInfo order = vipOrderService.createOrder(userId, request);
        return ApiResponse.success(order);
    }

    /**
     * 支付订单
     */
    @PostMapping("/orders/{orderNo}/pay")
    public ApiResponse<Void> payOrder(@PathVariable String orderNo) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("支付VIP订单: userId={}, orderNo={}", userId, orderNo);
        vipOrderService.payOrder(userId, orderNo);
        return ApiResponse.success();
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/orders")
    public ApiResponse<PageResponse<VipResponse.OrderInfo>> getOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取订单列表: userId={}, page={}, size={}", userId, page, size);
        PageResponse<VipResponse.OrderInfo> response = vipOrderService.getUserOrders(userId, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/orders/{orderNo}")
    public ApiResponse<VipResponse.OrderInfo> getOrderDetail(@PathVariable String orderNo) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取订单详情: userId={}, orderNo={}", userId, orderNo);
        VipResponse.OrderInfo order = vipOrderService.getOrderDetail(userId, orderNo);
        return ApiResponse.success(order);
    }

    /**
     * 取消订单
     */
    @PostMapping("/orders/{orderNo}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable String orderNo) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消订单: userId={}, orderNo={}", userId, orderNo);
        vipOrderService.cancelOrder(userId, orderNo);
        return ApiResponse.success();
    }
}
