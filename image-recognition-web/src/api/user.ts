import { get, post, put } from '@/utils/request'
import type { UserInfo } from './types'

/**
 * 用户统计信息
 */
export interface UserStats {
  totalRecognitions: number
  totalUploads: number
  totalFavorites: number
  totalShares: number
  joinDays: number
  lastActiveTime: string
  monthlyUsage: {
    recognitions: number
    uploads: number
  }
  weeklyActivity: Array<{
    date: string
    count: number
  }>
}

/**
 * 用户更新请求
 */
export interface UserUpdateRequest {
  nickname?: string
  phone?: string
  avatar?: string
  bio?: string
}

/**
 * 修改密码请求
 */
export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
  confirmPassword?: string
}

/**
 * 用户API模块
 */
export class UserAPI {
  /**
   * 获取当前用户信息
   */
  static getProfile() {
    return get<UserInfo>('/api/user/profile')
  }

  /**
   * 更新用户信息
   */
  static updateProfile(data: UserUpdateRequest) {
    return put<void>('/api/user/profile', data)
  }

  /**
   * 修改密码
   */
  static changePassword(data: ChangePasswordRequest) {
    return post<void>('/api/user/change-password', data)
  }

  /**
   * 获取用户统计信息
   */
  static getStats() {
    return get<UserStats>('/api/user/stats')
  }
}

// 导出默认实例
export default UserAPI
