package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.VipRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.response.VipResponse;
import com.pengcunfu.recognition.service.UserService;
import com.pengcunfu.recognition.service.VipOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台 - VIP管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/vip")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminVipController {

    private final VipOrderService vipOrderService;
    private final UserService userService;

    /**
     * 获取VIP订单列表（管理员）
     */
    @GetMapping("/orders")
    public ApiResponse<PageResponse<VipResponse.VipOrderInfo>> getVipOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "paymentStatus", required = false) Integer paymentStatus,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("管理员获取VIP订单列表: page={}, size={}, paymentStatus={}, keyword={}",
                page, size, paymentStatus, keyword);
        PageResponse<VipResponse.VipOrderInfo> response =
            vipOrderService.getOrdersAdmin(page, size, paymentStatus, keyword);
        return ApiResponse.success(response);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/orders/{orderId}/status")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status) {
        log.info("更新订单状态: orderId={}, status={}", orderId, status);
        vipOrderService.updateOrderStatus(orderId, status);
        return ApiResponse.success();
    }

    // ==================== VIP用户管理 ====================

    /**
     * 获取VIP用户列表
     */
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getVipUsers(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("管理员获取VIP用户列表: page={}, size={}, level={}, status={}, keyword={}",
                page, size, level, status, keyword);
        
        PageResponse<UserResponse.UserInfo> response = userService.getVipUsers(page, size, level, status, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", response.getData());
        result.put("total", response.getTotal());
        result.put("page", response.getPage());
        result.put("size", response.getSize());
        
        return ApiResponse.success(result);
    }

    /**
     * 获取VIP统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<VipResponse.VipStats> getVipStats() {
        log.info("管理员获取VIP统计数据");
        VipResponse.VipStats stats = vipOrderService.getVipStats();
        return ApiResponse.success(stats);
    }

    /**
     * 延长VIP时长
     */
    @PostMapping("/users/{userId}/extend")
    public ApiResponse<Void> extendVip(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.ExtendVipRequest request) {
        log.info("管理员延长VIP时长: userId={}, days={}, reason={}", 
                userId, request.getDays(), request.getReason());
        userService.extendVip(userId, request.getDays(), request.getReason());
        return ApiResponse.success();
    }

    /**
     * 升级VIP等级
     */
    @PostMapping("/users/{userId}/upgrade")
    public ApiResponse<Void> upgradeVip(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.UpdateVipLevelRequest request) {
        log.info("管理员升级VIP等级: userId={}, newLevel={}, reason={}", 
                userId, request.getNewLevel(), request.getReason());
        userService.upgradeVip(userId, request.getNewLevel(), request.getReason());
        return ApiResponse.success();
    }

    /**
     * 降级VIP等级
     */
    @PostMapping("/users/{userId}/downgrade")
    public ApiResponse<Void> downgradeVip(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.UpdateVipLevelRequest request) {
        log.info("管理员降级VIP等级: userId={}, newLevel={}, reason={}", 
                userId, request.getNewLevel(), request.getReason());
        userService.downgradeVip(userId, request.getNewLevel(), request.getReason());
        return ApiResponse.success();
    }

    /**
     * 切换VIP状态
     */
    @PutMapping("/users/{userId}/status")
    public ApiResponse<Void> toggleVipStatus(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.ToggleVipStatusRequest request) {
        log.info("管理员切换VIP状态: userId={}, status={}, reason={}", 
                userId, request.getStatus(), request.getReason());
        userService.toggleVipStatus(userId, request.getStatus(), request.getReason());
        return ApiResponse.success();
    }

    /**
     * 重置VIP使用量
     */
    @PostMapping("/users/{userId}/reset-usage")
    public ApiResponse<Void> resetVipUsage(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.ResetVipUsageRequest request) {
        log.info("管理员重置VIP使用量: userId={}, resetType={}, reason={}", 
                userId, request.getResetType(), request.getReason());
        userService.resetVipUsage(userId, request.getResetType(), request.getReason());
        return ApiResponse.success();
    }

    /**
     * 撤销VIP权限
     */
    @PostMapping("/users/{userId}/revoke")
    public ApiResponse<Void> revokeVip(
            @PathVariable Long userId,
            @Valid @RequestBody VipRequest.RevokeVipRequest request) {
        log.info("管理员撤销VIP权限: userId={}, reason={}", userId, request.getReason());
        userService.revokeVip(userId, request.getReason());
        return ApiResponse.success();
    }
}
