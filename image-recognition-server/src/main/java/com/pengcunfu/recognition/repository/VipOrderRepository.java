package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.VipOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * VIP订单Repository
 */
@Mapper
public interface VipOrderRepository extends BaseMapper<VipOrder> {

    /**
     * 统计总订单数
     */
    @Select("SELECT COUNT(*) FROM vip_orders")
    long countTotal();

    /**
     * 统计今日订单数
     */
    @Select("""
            SELECT COUNT(*) FROM vip_orders
            WHERE created_at >= #{startTime}
            """)
    long countByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);

    /**
     * 根据订单号查询订单
     */
    @Select("""
            SELECT * FROM vip_orders
            WHERE order_no = #{orderNo}
            LIMIT 1
            """)
    VipOrder findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 分页查询用户订单列表
     */
    @Select("""
            SELECT * FROM vip_orders
            WHERE user_id = #{userId}
            ORDER BY created_at DESC
            """)
    Page<VipOrder> findByUserId(
            Page<VipOrder> page,
            @Param("userId") Long userId
    );

    /**
     * 按日期统计订单趋势
     */
    @Select("""
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM vip_orders
            WHERE created_at >= #{startDate}
            GROUP BY DATE(created_at)
            ORDER BY date
            """)
    java.util.List<java.util.Map<String, Object>> countOrderTrendByDate(@Param("startDate") LocalDateTime startDate);

    /**
     * 按套餐类型统计订单数量和金额
     */
    @Select("""
            SELECT 
                plan_type as planType,
                COUNT(*) as orderCount,
                SUM(amount) as totalAmount
            FROM vip_orders
            WHERE payment_status = 1
            GROUP BY plan_type
            ORDER BY plan_type
            """)
    java.util.List<java.util.Map<String, Object>> countByPlanType();
}

