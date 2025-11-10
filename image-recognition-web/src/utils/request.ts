import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'
import { loadingManager } from '@/directives/loading'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 导出baseURL供其他模块使用
export const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// API响应接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
}

// 请求配置接口
export interface RequestConfig extends AxiosRequestConfig {
  showError?: boolean // 是否显示错误消息，默认true
  showLoading?: boolean // 是否显示加载状态，默认true
  headers?: Record<string, string> // 请求头
}

// 创建axios实例
const instance: AxiosInstance = axios.create({
  baseURL: baseURL,
  timeout: 600000,
  withCredentials: true, // 支持跨域携带Cookie
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设置请求拦截器
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token（从 Pinia store 获取）
    const userStore = useUserStore()
    const token = userStore.token
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 显示加载状态
    const requestConfig = config as RequestConfig
    if (requestConfig.showLoading !== false) {
      loadingManager.show()
    }

    console.log(`[Request] ${config.method?.toUpperCase()} ${config.url}`, config.data || config.params)
    return config
  },
  (error: any) => {
    loadingManager.hide()
    return Promise.reject(error)
  }
)

// 设置响应拦截器
instance.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    loadingManager.hide()

    const { data } = response
    const config = response.config as RequestConfig

    console.log(`[Response] ${response.config.url}`, data)

    // 检查业务状态码
    if (data.code !== 200) {
      const errorMessage = data.message || '请求失败'

      // 显示错误消息
      if (config.showError !== false) {
        message.error(errorMessage)
      }

      return Promise.reject(new Error(errorMessage))
    }

    return response
  },
  (error: any) => {
    loadingManager.hide()

    console.error('[Request Error]', error)

    const { response, config } = error
    const requestConfig = config as RequestConfig

    // 处理HTTP状态码错误
    if (response) {
      const { status, data } = response
      // 直接使用服务端返回的错误消息
      const errorMessage = data?.message || `请求失败 (HTTP ${status})`

      // 如果是401未授权，清除登录状态并跳转登录页
      if (status === 401) {
        const userStore = useUserStore()
        userStore.clearUserInfo()
        
        const currentPath = router.currentRoute.value.fullPath
        if (currentPath !== '/login') {
          router.push({
            path: '/login',
            query: { redirect: currentPath }
          })
        }
      }

      // 显示错误消息
      if (requestConfig?.showError !== false) {
        message.error(errorMessage)
      }

      // 返回包含错误信息的错误对象
      return Promise.reject(new Error(errorMessage))
    } else if (error.code === 'ECONNABORTED') {
      // 请求超时
      const errorMessage = error.message || '请求超时'
      if (requestConfig?.showError !== false) {
        message.error(errorMessage)
      }
      return Promise.reject(new Error(errorMessage))
    } else {
      // 网络错误或其他错误
      const errorMessage = error.message || '网络连接失败'
      if (requestConfig?.showError !== false) {
        message.error(errorMessage)
      }
      return Promise.reject(new Error(errorMessage))
    }
  }
)

/**
 * 原始请求方法 - 返回完整的ApiResponse
 * 用于需要访问完整响应对象（包括 code、message、timestamp）的场景
 */
export function rawRequest<T = any>(config: RequestConfig): Promise<ApiResponse<T>> {
  return instance
    .request(config)
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
}

/**
 * GET请求 - 直接返回data字段
 */
export function get<T = any>(url: string, params?: any, config?: RequestConfig): Promise<T> {
  return instance
    .get(url, { ...config, params })
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * POST请求 - 直接返回data字段
 */
export function post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
  return instance
    .post(url, data, config)
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * PUT请求 - 直接返回data字段
 */
export function put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
  return instance
    .put(url, data, config)
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * DELETE请求 - 直接返回data字段
 */
export function del<T = any>(url: string, config?: RequestConfig): Promise<T> {
  return instance
    .delete(url, config)
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * PATCH请求 - 直接返回data字段
 */
export function patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
  return instance
    .patch(url, data, config)
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * 上传文件 - 直接返回data字段
 */
export function upload<T = any>(url: string, file: File | FormData, config?: RequestConfig): Promise<T> {
  const formData = file instanceof FormData ? file : new FormData()
  if (file instanceof File) {
    formData.append('file', file)
  }

  return instance
    .post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers
      }
    })
    .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
}

/**
 * 下载文件
 */
export function download(url: string, filename?: string, config?: RequestConfig): Promise<void> {
  return instance
    .get(url, {
      ...config,
      responseType: 'blob'
    })
    .then((response: AxiosResponse<Blob>) => {
      const blob = new Blob([response.data])
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = filename || 'download'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(downloadUrl)
    })
}

// 默认导出一个包含所有方法的对象（兼容旧代码）
export default {
  get,
  post,
  put,
  delete: del,
  patch,
  upload,
  download,
  rawRequest
}
