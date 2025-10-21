import { get, post, baseURL } from '@/utils/request'
import type { UserInfo } from './types'

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string
  password: string
  captchaId: string
  captchaCode: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string
  user: UserInfo
}

/**
 * 注册请求
 */
export interface RegisterRequest {
  username: string
  email: string
  password: string
  emailCode: string
}

/**
 * 注册响应
 */
export interface RegisterResponse {
  userId: number
  username: string
  email: string
}

/**
 * 忘记密码请求
 */
export interface ForgotPasswordRequest {
  email: string
  emailCode: string
  newPassword: string
}

/**
 * 发送验证码请求
 */
export interface SendCodeRequest {
  email: string
  type: string
}

/**
 * 验证码响应
 */
export interface CaptchaResponse {
  captchaId: string
  captchaImage: string
}

/**
 * 认证API模块
 */
export class AuthAPI {
  /**
   * 用户登录
   */
  static login(data: LoginRequest) {
    return post<LoginResponse>('/api/auth/login', data)
  }

  /**
   * 用户注册
   */
  static register(data: RegisterRequest) {
    return post<RegisterResponse>('/api/auth/register', data)
  }

  /**
   * 退出登录
   */
  static logout() {
    return post<void>('/api/auth/logout')
  }

  /**
   * 刷新Token
   */
  static refreshToken() {
    return post<LoginResponse>('/api/auth/refresh')
  }

  /**
   * 发送验证码
   */
  static sendVerificationCode(data: SendCodeRequest) {
    return post<void>(`/api/auth/send-code?email=${encodeURIComponent(data.email)}&type=${data.type}`)
  }

  /**
   * 忘记密码
   */
  static forgotPassword(data: ForgotPasswordRequest) {
    return post<void>(`/api/auth/forgot-password?email=${encodeURIComponent(data.email)}&code=${data.emailCode}&newPassword=${encodeURIComponent(data.newPassword)}`)
  }

  /**
   * 重置密码（已登录用户）
   */
  static resetPassword(data: { oldPassword: string; newPassword: string }) {
    return post<void>('/api/auth/reset-password', data)
  }

  /**
   * 获取图形验证码
   */
  static getCaptcha() {
    return get<CaptchaResponse>('/api/auth/captcha')
  }
}

// 导出默认实例
export default AuthAPI
