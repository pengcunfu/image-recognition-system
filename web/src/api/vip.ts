import { get, post } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * VIP订单信息
 */
export interface VipOrderInfo {
  id: number
  orderNo: string
  userId: number
  planType: string
  amount: number
  status: number
  paymentMethod?: string
  paymentTime?: string
  createTime: string
  updateTime: string
}

/**
 * 创建订单请求
 */
export interface CreateOrderRequest {
  planType: number
  paymentMethod?: string
}

/**
 * VIP API模块
 */
export class VipAPI {
  /**
   * 创建VIP订单
   */
  static createOrder(data: CreateOrderRequest) {
    return post<VipOrderInfo>('/api/vip/orders', data)
  }

  /**
   * 支付订单
   */
  static payOrder(orderNo: string) {
    return post<void>(`/api/vip/orders/${orderNo}/pay`)
  }

  /**
   * 获取订单列表
   */
  static getOrders(page: number = 1, size: number = 10) {
    return get<PageResponse<VipOrderInfo>>('/api/vip/orders', { page, size })
  }

  /**
   * 获取订单详情
   */
  static getOrderDetail(orderNo: string) {
    return get<VipOrderInfo>(`/api/vip/orders/${orderNo}`)
  }

  /**
   * 取消订单
   */
  static cancelOrder(orderNo: string) {
    return post<void>(`/api/vip/orders/${orderNo}/cancel`)
  }
}

// 导出默认实例
export default VipAPI
