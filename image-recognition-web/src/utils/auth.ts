import type { Router } from 'vue-router'

/**
 * 认证工具类
 */
export class AuthUtils {
  private static router: Router | null = null

  /**
   * 设置路由实例（在应用初始化时调用）
   */
  static setRouter(routerInstance: Router) {
    this.router = routerInstance
  }

  /**
   * 获取存储的 token
   */
  static getToken(): string | null {
    return localStorage.getItem('userToken') || sessionStorage.getItem('userToken')
  }

  /**
   * 设置 token
   */
  static setToken(token: string, remember: boolean = false) {
    if (remember) {
      localStorage.setItem('userToken', token)
    } else {
      sessionStorage.setItem('userToken', token)
    }
  }

  /**
   * 清除 token
   */
  static clearToken() {
    localStorage.removeItem('userToken')
    sessionStorage.removeItem('userToken')
  }

  /**
   * 处理未授权情况（清除token并跳转登录页）
   */
  static handleUnauthorized() {
    // 清除token
    this.clearToken()

    // 使用路由跳转到登录页
    if (this.router) {
      const currentPath = this.router.currentRoute.value.fullPath
      if (currentPath !== '/login') {
        this.router.push({
          path: '/login',
          query: { redirect: currentPath }
        })
      }
    } else {
      // 如果路由未初始化，使用 location 跳转
      console.warn('[Auth] Router not initialized, using location redirect')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
  }

  /**
   * 检查是否已登录
   */
  static isAuthenticated(): boolean {
    return !!this.getToken()
  }

  /**
   * 获取用户信息（从 localStorage）
   */
  static getUserInfo(): any {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  }

  /**
   * 设置用户信息
   */
  static setUserInfo(userInfo: any) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  }

  /**
   * 清除用户信息
   */
  static clearUserInfo() {
    localStorage.removeItem('userInfo')
  }

  /**
   * 退出登录
   */
  static logout() {
    this.clearToken()
    this.clearUserInfo()
    
    if (this.router) {
      this.router.push('/login')
    } else {
      window.location.href = '/login'
    }
  }
}

export default AuthUtils

