import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

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
  returnRaw?: boolean // 是否返回原始响应数据（不经过业务状态码检查），默认false
  headers?: Record<string, string> // 请求头
}

class RequestService {
  private instance: AxiosInstance
  private loadingCount = 0

  constructor() {
    // 创建axios实例
    this.instance = axios.create({
      baseURL: baseURL,
      timeout: 30000,
      withCredentials: true, // 支持跨域携带Cookie
      headers: {
        'Content-Type': 'application/json'
      }
    })

    // 设置请求拦截器
    this.setupRequestInterceptor()
    // 设置响应拦截器
    this.setupResponseInterceptor()
  }

  /**
   * 设置请求拦截器
   */
  private setupRequestInterceptor() {
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        // 添加认证token
        const token = this.getToken()
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`
        }

        // 显示加载状态
        const requestConfig = config as RequestConfig
        if (requestConfig.showLoading !== false) {
          this.showLoading()
        }

        console.log(`[Request] ${config.method?.toUpperCase()} ${config.url}`, config.data || config.params)
        return config
      },
      (error: any) => {
        this.hideLoading()
        return Promise.reject(error)
      }
    )
  }

  /**
   * 设置响应拦截器
   */
  private setupResponseInterceptor() {
    this.instance.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        this.hideLoading()
        
        const { data } = response
        const config = response.config as RequestConfig
        
        console.log(`[Response] ${response.config.url}`, data)

        // 如果是原始请求，直接返回完整响应
        if (config.returnRaw) {
          return response
        }

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
        this.hideLoading()
        
        console.error('[Request Error]', error)
        
        const { response, config } = error
        const requestConfig = config as RequestConfig
        
        // 处理HTTP状态码错误
        if (response) {
          const { status, data } = response
          // 直接使用服务端返回的错误消息
          const errorMessage = data?.message || `请求失败 (HTTP ${status})`

          // 如果是401未授权，执行跳转登录逻辑
          if (status === 401) {
            this.handleUnauthorized()
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
  }

  /**
   * 获取存储的token
   */
  private getToken(): string | null {
    return localStorage.getItem('userToken') || sessionStorage.getItem('userToken')
  }

  /**
   * 处理未授权情况
   */
  private handleUnauthorized() {
    // 清除token
    localStorage.removeItem('userToken')
    sessionStorage.removeItem('userToken')
    
    // 跳转到登录页
    if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
  }

  /**
   * 显示加载状态
   */
  private showLoading() {
    this.loadingCount++
    if (this.loadingCount === 1) {
      // 这里可以显示全局loading
      console.log('[Loading] Start')
    }
  }

  /**
   * 隐藏加载状态
   */
  private hideLoading() {
    this.loadingCount = Math.max(0, this.loadingCount - 1)
    if (this.loadingCount === 0) {
      // 这里可以隐藏全局loading
      console.log('[Loading] End')
    }
  }

  /**
   * GET请求 - 直接返回data字段
   */
  get<T = any>(url: string, params?: any, config?: RequestConfig): Promise<T> {
    return this.instance
      .get(url, { ...config, params })
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
  }

  /**
   * POST请求 - 直接返回data字段
   */
  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.instance
      .post(url, data, config)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
  }

  /**
   * PUT请求 - 直接返回data字段
   */
  put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.instance
      .put(url, data, config)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
  }

  /**
   * DELETE请求 - 直接返回data字段
   */
  delete<T = any>(url: string, config?: RequestConfig): Promise<T> {
    return this.instance
      .delete(url, config)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
  }

  /**
   * PATCH请求 - 直接返回data字段
   */
  patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.instance
      .patch(url, data, config)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data.data)
  }

  /**
   * 上传文件 - 直接返回data字段
   */
  upload<T = any>(url: string, file: File | FormData, config?: RequestConfig): Promise<T> {
    const formData = file instanceof FormData ? file : new FormData()
    if (file instanceof File) {
      formData.append('file', file)
    }

    return this.instance
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
  download(url: string, filename?: string, config?: RequestConfig): Promise<void> {
    return this.instance
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

  /**
   * 原始GET请求 - 返回完整的ApiResponse
   */
  getRaw<T = any>(url: string, params?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance
      .get(url, { ...config, params, returnRaw: true } as RequestConfig)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 原始POST请求 - 返回完整的ApiResponse
   */
  postRaw<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance
      .post(url, data, { ...config, returnRaw: true } as RequestConfig)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 原始PUT请求 - 返回完整的ApiResponse
   */
  putRaw<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance
      .put(url, data, { ...config, returnRaw: true } as RequestConfig)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 原始DELETE请求 - 返回完整的ApiResponse
   */
  deleteRaw<T = any>(url: string, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance
      .delete(url, { ...config, returnRaw: true } as RequestConfig)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 原始PATCH请求 - 返回完整的ApiResponse
   */
  patchRaw<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance
      .patch(url, data, { ...config, returnRaw: true } as RequestConfig)
      .then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 取消请求
   */
  cancelRequest(message?: string) {
    // 这里可以实现请求取消逻辑
    console.log('[Request] Cancelled:', message)
  }
}

// 创建请求实例
const request = new RequestService()

export default request
