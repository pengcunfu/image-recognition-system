import { get, post, put, del } from '@/utils/request'
import type { PageResponse, UserInfo } from './types'
import type { PostInfo } from './community'
import type { KnowledgeInfo } from './knowledge'

// 重新导出类型供外部使用
export type { PostInfo, KnowledgeInfo }

/**
 * 系统概览
 */
export interface SystemOverview {
  totalUsers: number
  activeUsers: number
  totalRecognitions: number
  todayRecognitions: number
  totalPosts: number
  totalKnowledge: number
  totalOrders: number
  totalRevenue: number
  userGrowth: number
  recognitionGrowth: number
  postGrowth: number
  revenueGrowth: number
}

/**
 * 趋势数据
 */
export interface TrendData {
  dates: string[]
  values?: number[]
  series?: {
    [key: string]: number[]
  }
}

/**
 * 分类统计项
 */
export interface CategoryStatsItem {
  category: string
  count: number
  percentage: number
}

/**
 * 分类统计
 */
export interface CategoryStats {
  recognitionCategories: CategoryStatsItem[]
  postCategories: CategoryStatsItem[]
  knowledgeCategories: CategoryStatsItem[]
}

/**
 * 系统日志信息
 */
export interface SystemLogInfo {
  id: number
  module: string
  action: string
  userId?: number
  username?: string
  ip: string
  userAgent?: string
  requestUrl?: string
  requestMethod?: string
  requestParams?: string
  responseStatus?: number
  errorMessage?: string
  executionTime?: number
  createTime: string
}

/**
 * VIP订单信息
 */
export interface VipOrderInfo {
  id: number
  userId: number
  username?: string
  orderNo: string
  planType: number
  amount: number
  paymentMethod?: string
  paymentStatus: number
  paymentTime?: string
  createTime: string
}

/**
 * 识别记录信息
 */
export interface RecognitionRecordInfo {
  id: number
  userId: number
  username?: string
  imageUrl: string
  mainCategory?: string
  confidence?: number
  recognitionType: number
  status: number
  processingTime?: number
  createTime: string
}

/**
 * 管理后台API模块
 */
export class AdminAPI {
  // ==================== 统计数据 ====================

  /**
   * 获取系统概览
   */
  static getSystemOverview() {
    return get<SystemOverview>('/api/admin/stats/overview')
  }

  /**
   * 获取趋势数据
   */
  static getTrendData(days: number = 7) {
    return get<TrendData>('/api/admin/stats/trends', { days })
  }

  /**
   * 获取分类统计
   */
  static getCategoryStats() {
    return get<CategoryStats>('/api/admin/stats/categories')
  }

  /**
   * 获取系统日志
   */
  static getSystemLogs(params: {
    page?: number
    size?: number
    module?: string
    userId?: number
    startTime?: string
    endTime?: string
  }) {
    return get<PageResponse<SystemLogInfo>>('/api/admin/stats/logs', params)
  }

  // ==================== 用户管理 ====================

  /**
   * 获取用户列表
   */
  static getUsers(params: {
    page?: number
    size?: number
    status?: number
    role?: number
    keyword?: string
    sortBy?: string
    sortOrder?: string
  }) {
    return get<PageResponse<UserInfo>>('/api/admin/users', params)
  }

  /**
   * 创建用户
   */
  static createUser(data: {
    username: string
    email: string
    password: string
    nickname?: string
    phone?: string
    avatar?: string
    role?: number
    status?: number
  }) {
    return post<number>('/api/admin/users', data)
  }

  /**
   * 更新用户信息
   */
  static updateUser(userId: number, data: {
    email?: string
    nickname?: string
    phone?: string
    avatar?: string
    role?: number
    status?: number
  }) {
    return put<void>(`/api/admin/users/${userId}`, data)
  }

  /**
   * 更新用户状态
   */
  static updateUserStatus(userId: number, status: number) {
    return put<void>(`/api/admin/users/${userId}/status?status=${status}`)
  }

  /**
   * 删除用户
   */
  static deleteUser(userId: number) {
    return del<void>(`/api/admin/users/${userId}`)
  }

  /**
   * 重置用户密码
   */
  static resetUserPassword(userId: number, newPassword: string) {
    return post<void>(`/api/admin/users/${userId}/reset-password?newPassword=${encodeURIComponent(newPassword)}`)
  }

  // ==================== 社区管理 ====================

  /**
   * 获取帖子列表（管理员）
   */
  static getPosts(params: {
    page?: number
    size?: number
    status?: number
    keyword?: string
  }) {
    return get<PageResponse<PostInfo>>('/api/admin/community/posts', params)
  }

  /**
   * 获取帖子详情（管理员）
   */
  static getPostDetail(postId: number) {
    return get<PostInfo>(`/api/admin/community/posts/${postId}`)
  }

  /**
   * 更新帖子状态
   */
  static updatePostStatus(postId: number, status: number) {
    return put<void>(`/api/admin/community/posts/${postId}/status?status=${status}`)
  }

  /**
   * 创建帖子（管理员）
   */
  static createPost(data: {
    title: string
    content: string
    category: string
    tags?: string
    images?: string
    status?: number
  }) {
    return post<number>('/api/admin/community/posts', data)
  }

  /**
   * 更新帖子（管理员）
   */
  static updatePost(postId: number, data: {
    title?: string
    content?: string
    category?: string
    tags?: string
    images?: string
    status?: number
  }) {
    return put<void>(`/api/admin/community/posts/${postId}`, data)
  }

  /**
   * 删除帖子（管理员）
   */
  static deletePost(postId: number) {
    return del<void>(`/api/admin/community/posts/${postId}`)
  }

  /**
   * 置顶/取消置顶帖子
   */
  static togglePostTop(postId: number, isTop: number) {
    return put<void>(`/api/admin/community/posts/${postId}/top?isTop=${isTop}`)
  }

  // ==================== 知识库管理 ====================

  /**
   * 获取知识列表（管理员）
   */
  static getKnowledge(params: {
    page?: number
    size?: number
    status?: number
    category?: string
    tag?: string
    keyword?: string
  }) {
    return get<PageResponse<KnowledgeInfo>>('/api/admin/knowledge', params)
  }

  /**
   * 获取所有知识分类
   */
  static getKnowledgeCategories() {
    return get<string[]>('/api/admin/knowledge/categories')
  }

  /**
   * 获取所有知识标签
   */
  static getKnowledgeTags() {
    return get<string[]>('/api/admin/knowledge/tags')
  }

  /**
   * 创建知识条目
   */
  static createKnowledge(data: {
    category: string
    title: string
    content: string
    coverImage?: string
    images?: string
    tags?: string
  }) {
    return post<number>('/api/admin/knowledge', data)
  }

  /**
   * 更新知识条目
   */
  static updateKnowledge(
    knowledgeId: number,
    data: {
      category?: string
      title?: string
      content?: string
      coverImage?: string
      images?: string
      tags?: string
    }
  ) {
    return put<void>(`/api/admin/knowledge/${knowledgeId}`, data)
  }

  /**
   * 删除知识条目
   */
  static deleteKnowledge(knowledgeId: number) {
    return del<void>(`/api/admin/knowledge/${knowledgeId}`)
  }

  // ==================== VIP订单管理 ====================

  /**
   * 获取VIP订单列表
   */
  static getVipOrders(params: {
    page?: number
    size?: number
    paymentStatus?: number
    keyword?: string
  }) {
    return get<PageResponse<VipOrderInfo>>('/api/admin/vip/orders', params)
  }

  /**
   * 更新订单状态
   */
  static updateOrderStatus(orderId: number, status: number) {
    return put<void>(`/api/admin/vip/orders/${orderId}/status?status=${status}`)
  }

  // ==================== VIP用户管理 ====================

  /**
   * 获取VIP用户列表
   */
  static getVipUsers(params: {
    page?: number
    size?: number
    level?: number
    status?: number
    keyword?: string
  }) {
    return get<{
      users: UserInfo[]
      total: number
      page: number
      size: number
    }>('/api/admin/vip/users', params)
  }

  /**
   * 获取VIP统计数据
   */
  static getVipStats() {
    return get<{
      totalVips: number
      activeVips: number
      expiredVips: number
      totalRevenue: number
      monthlyRevenue: number
      newVipsThisMonth: number
      renewalRate: number
      avgLifetime: number
    }>('/api/admin/vip/stats')
  }

  /**
   * 延长VIP时长
   */
  static extendVip(userId: number, data: {
    days: number
    reason?: string
  }) {
    return post<void>(`/api/admin/vip/users/${userId}/extend`, data)
  }

  /**
   * 升级VIP等级
   */
  static upgradeVip(userId: number, data: {
    newLevel: number
    reason?: string
  }) {
    return post<void>(`/api/admin/vip/users/${userId}/upgrade`, data)
  }

  /**
   * 降级VIP等级
   */
  static downgradeVip(userId: number, data: {
    newLevel: number
    reason?: string
  }) {
    return post<void>(`/api/admin/vip/users/${userId}/downgrade`, data)
  }

  /**
   * 切换VIP状态
   */
  static toggleVipStatus(userId: number, data: {
    status: string
    reason?: string
  }) {
    return put<void>(`/api/admin/vip/users/${userId}/status`, data)
  }

  /**
   * 重置VIP使用量
   */
  static resetVipUsage(userId: number, data: {
    resetType: string
    reason?: string
  }) {
    return post<void>(`/api/admin/vip/users/${userId}/reset-usage`, data)
  }

  /**
   * 撤销VIP权限
   */
  static revokeVip(userId: number, data: {
    reason?: string
  }) {
    return post<void>(`/api/admin/vip/users/${userId}/revoke`, data)
  }

  // ==================== 识别记录管理 ====================

  /**
   * 获取识别记录列表
   */
  static getRecognitionRecords(params: {
    page?: number
    size?: number
    status?: number
    userId?: number
    keyword?: string
  }) {
    return get<PageResponse<RecognitionRecordInfo>>('/api/admin/recognition/records', params)
  }

  /**
   * 删除识别记录
   */
  static deleteRecognitionRecord(recordId: number) {
    return del<void>(`/api/admin/recognition/records/${recordId}`)
  }

  /**
   * 批量删除识别记录
   */
  static batchDeleteRecognitionRecords(recordIds: number[]) {
    return post<void>('/api/admin/recognition/records/batch-delete', { ids: recordIds })
  }

  // ==================== 管理员个人资料 ====================

  /**
   * 获取当前管理员信息
   */
  static getAdminProfile() {
    return get<UserInfo>('/api/admin/profile')
  }

  /**
   * 更新管理员个人资料
   */
  static updateAdminProfile(data: {
    nickname?: string
    phone?: string
    avatar?: string
    bio?: string
  }) {
    return put<void>('/api/admin/profile', data)
  }

  /**
   * 更新管理员头像
   */
  static updateAdminAvatar(avatarUrl: string) {
    return put<string>(`/api/admin/profile/avatar?avatarUrl=${encodeURIComponent(avatarUrl)}`)
  }

  /**
   * 修改管理员密码
   */
  static changeAdminPassword(data: {
    oldPassword: string
    newPassword: string
  }) {
    return put<void>('/api/admin/profile/password', data)
  }
}

// 导出默认实例
export default AdminAPI
