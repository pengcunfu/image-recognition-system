import request, { baseURL } from '@/utils/request'
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  ForgotPasswordRequest,
  TokenValidationResponse,
  SmsCodeRequest,
  SmsCodeResponse,
  SmsCodeVerifyRequest,
  OperationResult
} from './types'

/**
 * 认证API模块
 */
export class AuthAPI {
  /**
   * 用户登录
   */
  static login(data: LoginRequest) {
    return request.post<LoginResponse>('/api/v1/auth/login', data)
  }

  /**
   * 用户注册
   */
  static register(data: RegisterRequest) {
    return request.post<RegisterResponse>('/api/v1/auth/register', data)
  }

  /**
   * 发送忘记密码验证码
   */
  static sendForgotPasswordCode(data: { email: string }) {
    return request.post<string>('/api/v1/auth/email-code', {
      email: data.email,
      type: 'reset_password'
    })
  }

  /**
   * 忘记密码（重置密码）
   */
  static forgotPassword(data: ForgotPasswordRequest) {
    return request.post<string>('/api/v1/auth/forgot-password', data)
  }

  /**
   * 获取验证码图片URL
   */
  static getCaptchaUrl() {
    const timestamp = new Date().getTime()
    return `${baseURL}/api/v1/auth/captcha?t=${timestamp}`
  }

  /**
   * 发送邮箱验证码
   */
  static sendEmailCode(data: { email: string; type: string }) {
    return request.post('/api/v1/auth/email-code', data)
  }




  /**
   * 用户登出
   */
  static logout() {
    return request.post<string>('/api/v1/auth/logout')
  }

  /**
   * 验证Token
   */
  static validateToken() {
    return request.get<TokenValidationResponse>('/api/v1/auth/validate')
  }

  /**
   * 发送短信验证码
   */
  static sendSmsCode(data: SmsCodeRequest) {
    return request.post<SmsCodeResponse>('/api/v1/auth/sms-code', data)
  }

  /**
   * 验证短信验证码
   */
  static verifySmsCode(data: SmsCodeVerifyRequest) {
    return request.post<boolean>('/api/v1/auth/sms-code/verify', data)
  }

  /**
   * 刷新Token
   */
  static refreshToken() {
    return request.post<LoginResponse>('/api/v1/auth/refresh')
  }

  /**
   * 修改密码
   */
  static changePassword(data: {
    oldPassword: string
    newPassword: string
    confirmPassword: string
  }) {
    return request.post<OperationResult>('/api/v1/auth/change-password', data)
  }

  /**
   * 绑定手机号
   */
  static bindPhone(data: {
    phone: string
    smsCode: string
  }) {
    return request.post<OperationResult>('/api/v1/auth/bind-phone', data)
  }

  /**
   * 绑定邮箱
   */
  static bindEmail(data: {
    email: string
    verifyCode: string
  }) {
    return request.post<OperationResult>('/api/v1/auth/bind-email', data)
  }

  /**
   * GitHub OAuth登录
   */
  static githubLogin(code: string) {
    return request.post<LoginResponse>('/api/v1/auth/oauth/github', { code })
  }

  /**
   * Gitee OAuth登录
   */
  static giteeLogin(code: string) {
    return request.post<LoginResponse>('/api/v1/auth/oauth/gitee', { code })
  }

  /**
   * 获取OAuth登录URL
   */
  static getOAuthUrl(provider: 'github' | 'gitee') {
    return request.get<{ url: string }>(`/api/v1/auth/oauth/${provider}/url`)
  }

  /**
   * 解绑第三方账号
   */
  static unbindOAuth(provider: 'github' | 'gitee') {
    return request.delete<OperationResult>(`/api/v1/auth/oauth/${provider}/unbind`)
  }

  /**
   * 获取用户绑定的第三方账号
   */
  static getOAuthBindings() {
    return request.get<Array<{
      provider: string
      username: string
      email: string
      bindTime: string
      status: 'active' | 'inactive'
    }>>('/api/v1/auth/oauth/bindings')
  }
}

// 导出默认实例
export default AuthAPI
