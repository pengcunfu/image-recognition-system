import type { Router } from 'vue-router'

/**
 * 认证工具类
 * 注意：这个类主要用于与 Pinia store 解耦的场景（如请求拦截器）
 * token 统一存储在 localStorage 的 'token' 键中
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
   * 注意：token 键名为 'token'，与 user store 保持一致
   */
  static getToken(): string | null {
    return localStorage.getItem('token')
  }

  /**
   * 设置 token
   * 注意：不推荐直接使用此方法，应该使用 user store 的 setLoginInfo
   * @deprecated 请使用 useUserStore().setLoginInfo() 替代
   */
  static setToken(token: string) {
    localStorage.setItem('token', token)
  }

  /**
   * 清除 token
   * 注意：不推荐直接使用此方法，应该使用 user store 的 clearUserInfo
   * @deprecated 请使用 useUserStore().clearUserInfo() 替代
   */
  static clearToken() {
    localStorage.removeItem('token')
  }

  /**
   * 处理未授权情况（清除token并跳转登录页）
   */
  static handleUnauthorized() {
    // 清除token - 只清除 localStorage，store 会在重新加载时同步
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
}

export default AuthUtils

