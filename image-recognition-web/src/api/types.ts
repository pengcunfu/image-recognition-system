/**
 * API 通用类型定义
 */

/**
 * 通用分页响应
 */
export interface PageResponse<T> {
  data: T[]
  total: number
  page: number
  size: number
  pages?: number
}

/**
 * 用户基本信息
 */
export interface UserInfo {
  id: number
  username: string
  nickname?: string
  name?: string
  email?: string
  phone?: string
  avatar?: string
  bio?: string
  role: number  // 0=普通用户, 1=VIP, 2=管理员
  status?: number
  vipLevel?: number
  vipExpireTime?: string
  createTime?: string
  createdAt?: string
  lastLoginTime?: string
  lastLoginIp?: string
}

/**
 * 操作结果
 */
export interface OperationResult {
  success: boolean
  message: string
}

