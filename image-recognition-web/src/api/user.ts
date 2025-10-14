import request from '@/utils/request'
import type {
  User,
  UserListResponse,
  UserStats,
  UserSettings,
  UserActivity,
  UserUpdateRequest,
  ChangePasswordRequest,
  AdminUserCreateRequest,
  AdminUserUpdateRequest,
  UserQueryParams,
  OperationResult
} from './types'

/**
 * 用户API模块
 */
export class UserAPI {
  // ==================== 用户个人接口 ====================
  
  /**
   * 获取当前用户信息
   */
  static getProfile() {
    return request.get<User>('/api/v1/user/profile')
  }

  /**
   * 更新用户信息
   */
  static updateProfile(data: UserUpdateRequest) {
    return request.put<void>('/api/v1/user/profile', data)
  }

  /**
   * 获取用户统计信息
   */
  static getStats() {
    return request.get<UserStats>('/api/v1/user/stats')
  }

  /**
   * 修改密码
   */
  static changePassword(data: ChangePasswordRequest) {
    return request.put<void>('/api/v1/user/password', data)
  }

  /**
   * 获取用户设置
   */
  static getSettings() {
    return request.get<UserSettings>('/api/v1/user/settings')
  }

  /**
   * 更新用户设置
   */
  static updateSettings(data: UserSettings) {
    return request.put<void>('/api/v1/user/settings', data)
  }

  /**
   * 获取用户活动记录
   */
  static getActivities(limit: number = 10) {
    return request.get<UserActivity[]>('/api/v1/user/activities', { limit })
  }

  // ==================== 管理员用户管理接口 ====================

  /**
   * 获取用户列表（管理员）
   */
  static getUsers(params: UserQueryParams = {}) {
    return request.get<UserListResponse>('/api/v1/user/list', params)
  }

  /**
   * 获取用户详情（管理员）
   */
  static getUserDetail(id: number) {
    return request.get<User>(`/api/v1/user/${id}`)
  }

  /**
   * 创建用户（管理员）
   */
  static createUser(data: AdminUserCreateRequest) {
    return request.post<{ id: number }>('/api/v1/user/create', data)
  }

  /**
   * 更新用户（管理员）
   */
  static updateUser(id: number, data: AdminUserUpdateRequest) {
    return request.put<void>(`/api/v1/user/${id}`, data)
  }

  /**
   * 删除用户（管理员）
   */
  static deleteUser(id: number) {
    return request.delete<void>(`/api/v1/user/${id}`)
  }

  /**
   * 启用/禁用用户（管理员）
   */
  static toggleUserStatus(id: number, status: 'ACTIVE' | 'INACTIVE' | 'BANNED') {
    return request.put<void>(`/api/v1/user/${id}/status`, { status })
  }

  /**
   * 重置用户密码（管理员）
   */
  static resetPassword(id: number, newPassword: string) {
    return request.put<void>(`/api/v1/user/${id}/password`, { newPassword })
  }

  /**
   * 批量操作用户（管理员）
   */
  static batchUpdateUsers(userIds: number[], action: 'activate' | 'deactivate' | 'ban' | 'delete') {
    return request.post<void>('/api/v1/user/batch', { userIds, action })
  }

  /**
   * 获取用户统计概览（管理员）
   */
  static getUsersOverview() {
    return request.get<{
      totalUsers: number
      activeUsers: number
      newUsersToday: number
      newUsersThisMonth: number
      usersByRole: Record<string, number>
      usersByStatus: Record<string, number>
      recentRegistrations: User[]
    }>('/api/v1/user/overview')
  }

  /**
   * 导出用户数据（管理员）
   */
  static exportUsers(params: UserQueryParams = {}) {
    return request.get('/api/v1/user/export', params, { 
      responseType: 'blob',
      showLoading: true 
    })
  }

  /**
   * 搜索用户
   */
  static searchUsers(keyword: string, params: Partial<UserQueryParams> = {}) {
    return request.get<UserListResponse>('/api/v1/user/search', {
      keyword,
      ...params
    })
  }

  /**
   * 获取用户登录历史
   */
  static getUserLoginHistory(id: number, params: { page?: number; size?: number } = {}) {
    return request.get<{
      records: Array<{
        id: number
        loginTime: string
        loginIp: string
        userAgent: string
        location?: string
        success: boolean
      }>
      total: number
      page: number
      size: number
    }>(`/api/v1/user/${id}/login-history`, params)
  }

  // ==================== VIP管理接口 ====================

  /**
   * 获取VIP用户列表
   */
  static getVipUsers(params: {
    page?: number
    size?: number
    keyword?: string
    level?: string
    status?: string
  } = {}) {
    return request.get<UserListResponse>('/api/v1/user/vip/list', params)
  }

  /**
   * 获取VIP统计数据
   */
  static getVipStats() {
    return request.get<{
      total: number
      active: number
      expiring: number
      monthlyRevenue: number
    }>('/api/v1/user/vip/stats')
  }

  /**
   * 延期VIP
   */
  static extendVip(id: number, data: { days: number; reason?: string }) {
    return request.put<void>(`/api/v1/user/vip/${id}/extend`, data)
  }

  /**
   * 升级VIP等级
   */
  static upgradeVip(id: number, data: { level: number; reason?: string }) {
    return request.put<void>(`/api/v1/user/vip/${id}/upgrade`, data)
  }

  /**
   * 降级VIP等级
   */
  static downgradeVip(id: number, data: { level: number; reason?: string }) {
    return request.put<void>(`/api/v1/user/vip/${id}/downgrade`, data)
  }

  /**
   * 暂停/恢复VIP
   */
  static toggleVipStatus(id: number, data: { action: 'suspend' | 'resume'; reason?: string }) {
    return request.put<void>(`/api/v1/user/vip/${id}/toggle`, data)
  }

  /**
   * 撤销VIP
   */
  static revokeVip(id: number, data: { reason?: string }) {
    return request.delete<void>(`/api/v1/user/vip/${id}/revoke`, { data })
  }

  /**
   * 重置VIP用量
   */
  static resetVipUsage(id: number, data: { reason?: string }) {
    return request.put<void>(`/api/v1/user/vip/${id}/reset-usage`, data)
  }
}

// 导出默认实例
export default UserAPI
