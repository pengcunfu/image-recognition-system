package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.entity.VipOrder;
import com.pengcunfu.recognition.enums.PaymentStatus;
import com.pengcunfu.recognition.enums.VipPlanType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.repository.VipOrderRepository;
import com.pengcunfu.recognition.request.VipRequest;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.VipResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * VIP订单服务
 * 处理VIP会员购买、续费等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VipOrderService {

    private final VipOrderRepository vipOrderRepository;
    private final UserRepository userRepository;

    /**
     * 创建VIP订单（使用 Builder 模式）
     */
    @Transactional
    public VipResponse.OrderInfo createOrder(Long userId, VipRequest.CreateOrderRequest request) {
        log.info("创建VIP订单: userId={}, planType={}", userId, request.getPlanType());

        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 获取套餐价格
        BigDecimal amount = calculateAmount(request.getPlanType());

        // 创建订单（使用 Builder 模式）
        VipOrder order = VipOrder.builder()
                .userId(userId)
                .orderNo(generateOrderNo())
                .planType(request.getPlanType())
                .amount(amount)
                .paymentMethod(0) // 默认支付宝，实际支付时再设置
                .paymentStatus(PaymentStatus.UNPAID.getValue())
                .build();

        vipOrderRepository.insert(order);

        log.info("VIP订单创建成功: userId={}, orderId={}, orderNo={}", 
                userId, order.getId(), order.getOrderNo());

        return convertToOrderInfo(order);
    }

    /**
     * 支付订单（使用 SQL 查询）
     */
    @Transactional
    public void payOrder(Long userId, String orderNo) {
        log.info("支付VIP订单: userId={}, orderNo={}", userId, orderNo);

        VipOrder order = vipOrderRepository.findByOrderNo(orderNo);

        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该订单");
        }

        if (!order.getPaymentStatus().equals(PaymentStatus.UNPAID.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "订单状态异常");
        }

        // TODO: 调用第三方支付接口
        // 这里模拟支付成功

        // 更新订单状态
        order.setPaymentStatus(PaymentStatus.PAID.getValue());
        order.setPaymentTime(LocalDateTime.now());
        vipOrderRepository.updateById(order);

        // 更新用户VIP信息
        updateUserVipStatus(userId, order.getPlanType());

        log.info("VIP订单支付成功: userId={}, orderNo={}", userId, orderNo);
    }

    /**
     * 获取用户订单列表（使用 SQL 查询）
     */
    public PageResponse<VipResponse.OrderInfo> getUserOrders(Long userId, Integer page, Integer size) {
        log.info("获取用户订单列表: userId={}, page={}, size={}", userId, page, size);

        Page<VipOrder> pageRequest = new Page<>(page, size);
        Page<VipOrder> pageResult = vipOrderRepository.findByUserId(pageRequest, userId);

        return PageResponse.<VipResponse.OrderInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToOrderInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 获取订单详情（使用 SQL 查询）
     */
    public VipResponse.OrderInfo getOrderDetail(Long userId, String orderNo) {
        log.info("获取订单详情: userId={}, orderNo={}", userId, orderNo);

        VipOrder order = vipOrderRepository.findByOrderNo(orderNo);

        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该订单");
        }

        return convertToOrderInfo(order);
    }

    /**
     * 取消订单（使用 SQL 查询）
     */
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        log.info("取消VIP订单: userId={}, orderNo={}", userId, orderNo);

        VipOrder order = vipOrderRepository.findByOrderNo(orderNo);

        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该订单");
        }

        if (!order.getPaymentStatus().equals(PaymentStatus.UNPAID.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "订单状态异常，无法取消");
        }

        // 更新订单状态
        order.setPaymentStatus(PaymentStatus.CANCELLED.getValue());
        vipOrderRepository.updateById(order);

        log.info("VIP订单取消成功: userId={}, orderNo={}", userId, orderNo);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "VIP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 计算套餐价格
     */
    private BigDecimal calculateAmount(Integer planType) {
        VipPlanType plan = VipPlanType.fromValue(planType);
        switch (plan) {
            case MONTHLY:
                return new BigDecimal("29.90");
            case QUARTERLY:
                return new BigDecimal("79.90");
            case YEARLY:
                return new BigDecimal("299.90");
            default:
                throw new BusinessException(ErrorCode.INVALID_PARAM, "无效的套餐类型");
        }
    }

    /**
     * 更新用户VIP状态
     */
    private void updateUserVipStatus(Long userId, Integer planType) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            return;
        }

        VipPlanType plan = VipPlanType.fromValue(planType);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime;

        // 如果已有VIP，从过期时间开始计算，否则从当前时间开始
        LocalDateTime baseTime = user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(now)
                ? user.getVipExpireTime()
                : now;

        switch (plan) {
            case MONTHLY:
                expireTime = baseTime.plusMonths(1);
                break;
            case QUARTERLY:
                expireTime = baseTime.plusMonths(3);
                break;
            case YEARLY:
                expireTime = baseTime.plusYears(1);
                break;
            default:
                return;
        }

        user.setVipExpireTime(expireTime);
        userRepository.updateById(user);

        log.info("用户VIP状态更新成功: userId={}, expireTime={}", userId, expireTime);
    }

    /**
     * 转换为订单信息 DTO
     */
    private VipResponse.OrderInfo convertToOrderInfo(VipOrder order) {
        return VipResponse.OrderInfo.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderNo(order.getOrderNo())
                .planType(order.getPlanType())
                .amount(order.getAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentTime(order.getPaymentTime())
                .createdAt(order.getCreatedAt())
                .build();
    }

    // ==================== 管理员方法 ====================

    /**
     * 获取VIP订单列表（管理员）
     */
    public PageResponse<VipResponse.VipOrderInfo> getOrdersAdmin(
            Integer page, Integer size, Integer paymentStatus, String keyword) {
        log.info("管理员获取VIP订单列表: page={}, size={}, paymentStatus={}, keyword={}",
                page, size, paymentStatus, keyword);

        Page<VipOrder> orderPage = new Page<>(page, size);

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VipOrder> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (paymentStatus != null) {
            queryWrapper.eq(VipOrder::getPaymentStatus, paymentStatus);
        }

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like(VipOrder::getOrderNo, keyword)
            );
        }

        queryWrapper.orderByDesc(VipOrder::getCreatedAt);

        Page<VipOrder> result = vipOrderRepository.selectPage(orderPage, queryWrapper);

        return PageResponse.<VipResponse.VipOrderInfo>builder()
                .data(result.getRecords().stream()
                        .map(this::convertToVipOrderInfo)
                        .collect(Collectors.toList()))
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .size((int) result.getSize())
                .build();
    }

    /**
     * 更新订单状态（管理员）
     */
    @Transactional
    public void updateOrderStatus(Long orderId, Integer status) {
        log.info("管理员更新订单状态: orderId={}, status={}", orderId, status);

        VipOrder order = vipOrderRepository.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "订单不存在");
        }

        order = VipOrder.builder()
                .id(orderId)
                .paymentStatus(status)
                .build();

        vipOrderRepository.updateById(order);

        log.info("订单状态更新成功: orderId={}, status={}", orderId, status);
    }

    /**
     * 转换为VIP订单信息（管理员）
     */
    private VipResponse.VipOrderInfo convertToVipOrderInfo(VipOrder order) {
        User user = userRepository.selectById(order.getUserId());

        return VipResponse.VipOrderInfo.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .username(user != null ? user.getUsername() : "未知用户")
                .orderNo(order.getOrderNo())
                .planType(order.getPlanType())
                .amount(order.getAmount())
                .paymentStatus(order.getPaymentStatus())
                .paymentTime(order.getPaymentTime())
                .build();
    }

    /**
     * 获取VIP统计数据（管理员）
     */
    public VipResponse.VipStats getVipStats() {
        log.info("获取VIP统计数据");
        // TODO: 实现VIP统计数据查询逻辑
        return VipResponse.VipStats.builder()
                .totalVips(0L)
                .activeVips(0L)
                .expiredVips(0L)
                .totalRevenue(java.math.BigDecimal.ZERO)
                .monthlyRevenue(java.math.BigDecimal.ZERO)
                .newVipsThisMonth(0L)
                .renewalRate(0.0)
                .avgLifetime(0.0)
                .build();
    }
}
