import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

// 导出baseURL供其他模块使用
export const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// API响应接口
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message: string
  code?: number
}

// 请求配置接口
export interface RequestConfig extends AxiosRequestConfig {
  showError?: boolean // 是否显示错误消息
  showLoading?: boolean // 是否显示加载状态
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
        if ((config as any).showLoading !== false) {
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
        console.log(`[Response] ${response.config.url}`, data)

        // 检查业务状态码
        if (data.success === false) {
          const config = response.config as RequestConfig
          if (config.showError !== false) {
            message.error(data.message || '请求失败')
          }
          return Promise.reject(new Error(data.message || '请求失败'))
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
          let errorMessage = '请求失败'

          switch (status) {
            case 400:
              errorMessage = data?.message || '请求参数错误'
              break
            case 401:
              errorMessage = '未授权，请重新登录'
              this.handleUnauthorized()
              break
            case 403:
              errorMessage = '权限不足'
              break
            case 404:
              errorMessage = '请求的资源不存在'
              break
            case 500:
              errorMessage = '服务器内部错误'
              break
            case 502:
              errorMessage = '网关错误'
              break
            case 503:
              errorMessage = '服务不可用'
              break
            default:
              errorMessage = data?.message || `请求失败 (${status})`
          }

          if (requestConfig.showError !== false) {
            message.error(errorMessage)
          }
        } else if (error.code === 'ECONNABORTED') {
          if (requestConfig.showError !== false) {
            message.error('请求超时，请稍后重试')
          }
        } else {
          if (requestConfig.showError !== false) {
            message.error('网络连接失败，请检查网络设置')
          }
        }

        return Promise.reject(error)
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
   * GET请求
   */
  get<T = any>(url: string, params?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance.get(url, { ...config, params }).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * POST请求
   */
  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance.post(url, data, config).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * PUT请求
   */
  put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance.put(url, data, config).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * DELETE请求
   */
  delete<T = any>(url: string, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance.delete(url, config).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * PATCH请求
   */
  patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.instance.patch(url, data, config).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 上传文件
   */
  upload<T = any>(url: string, file: File | FormData, config?: RequestConfig): Promise<ApiResponse<T>> {
    const formData = file instanceof FormData ? file : new FormData()
    if (file instanceof File) {
      formData.append('file', file)
    }

    return this.instance.post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers
      }
    }).then((res: AxiosResponse<ApiResponse<T>>) => res.data)
  }

  /**
   * 下载文件
   */
  download(url: string, filename?: string, config?: RequestConfig): Promise<void> {
    return this.instance.get(url, {
      ...config,
      responseType: 'blob'
    }).then((response: AxiosResponse<Blob>) => {
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
