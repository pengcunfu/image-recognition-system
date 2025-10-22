import { get, post, put } from '@/utils/request'
import type { UserInfo } from './types'

/**
 * 用户统计信息
 */
export interface UserStats {
  userId: number
  recognitionCount: number
  postCount: number
  commentCount: number
  collectCount: number
  likeCount: number
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
 * 收藏项
 */
export interface CollectionItem {
  id: number
  type: string // recognition, post, knowledge
  title: string
  description?: string
  imageUrl?: string
  confidence?: number
  likeCount?: number
  viewCount?: number
  createdAt: string
}

/**
 * 用户收藏列表
 */
export interface UserCollections {
  recognitions: CollectionItem[]
  posts: CollectionItem[]
  knowledge: CollectionItem[]
}

/**
 * 点赞项
 */
export interface LikeItem {
  id: number
  type: string // post, knowledge, comment
  title: string
  content: string
  author: string
  likeCount: number
  createdAt: string
}

/**
 * 用户点赞列表
 */
export interface UserLikes {
  posts: LikeItem[]
  knowledge: LikeItem[]
  comments: LikeItem[]
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

  /**
   * 获取用户收藏列表
   */
  static getCollections(params: { page?: number; size?: number }) {
    return get<UserCollections>('/api/user/collects', params)
  }

  /**
   * 获取用户点赞列表
   */
  static getLikes(params: { page?: number; size?: number }) {
    return get<UserLikes>('/api/user/likes', params)
  }
}

// 导出默认实例
export default UserAPI
