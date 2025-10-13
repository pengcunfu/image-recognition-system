import request, { baseURL } from '@/utils/request'
import type {
  FileUploadResult,
  FileInfo,
  FileUploadConfig,
  OperationResult
} from './types'

/**
 * 文件管理API模块
 */
export class FileAPI {
  /**
   * 单文件上传
   */
  static uploadFile(file: File, config?: FileUploadConfig) {
    // 验证文件大小
    if (config?.maxSize && file.size > config.maxSize) {
      return Promise.reject(new Error(`文件大小不能超过 ${(config.maxSize / 1024 / 1024).toFixed(2)}MB`))
    }

    // 验证文件类型
    if (config?.allowedTypes && config.allowedTypes.length > 0) {
      const fileType = file.type
      const fileName = file.name.toLowerCase()
      const isAllowed = config.allowedTypes.some(type => {
        if (type.startsWith('.')) {
          return fileName.endsWith(type)
        }
        return fileType.includes(type)
      })
      
      if (!isAllowed) {
        return Promise.reject(new Error(`不支持的文件类型，仅支持: ${config.allowedTypes.join(', ')}`))
      }
    }

    return request.upload<FileUploadResult>('/api/v1/files/upload', file)
  }

  /**
   * 批量文件上传（VIP功能）
   */
  static uploadFiles(files: File[], config?: FileUploadConfig) {
    // 验证每个文件
    for (const file of files) {
      if (config?.maxSize && file.size > config.maxSize) {
        return Promise.reject(new Error(`文件 ${file.name} 大小不能超过 ${(config.maxSize / 1024 / 1024).toFixed(2)}MB`))
      }

      if (config?.allowedTypes && config.allowedTypes.length > 0) {
        const fileType = file.type
        const fileName = file.name.toLowerCase()
        const isAllowed = config.allowedTypes.some(type => {
          if (type.startsWith('.')) {
            return fileName.endsWith(type)
          }
          return fileType.includes(type)
        })
        
        if (!isAllowed) {
          return Promise.reject(new Error(`文件 ${file.name} 类型不支持，仅支持: ${config.allowedTypes.join(', ')}`))
        }
      }
    }

    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })

    return request.upload<FileUploadResult[]>('/api/v1/files/upload/batch', formData)
  }

  /**
   * 获取完整的图片URL（支持相对路径和完整URL）
   */
  static getImageUrl(url: string | undefined | null): string {
    if (!url) {
      return ''
    }

    // 如果已经是完整的URL（http或https开头），直接返回
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }

    // 如果是以/开头的相对路径，添加baseURL前缀
    if (url.startsWith('/')) {
      return `${baseURL}${url}`
    }

    // 其他情况（可能是base64或data URL），直接返回
    return url
  }

  /**
   * 检查是否为有效的图片URL
   */
  static isValidImageUrl(url: string | undefined | null): boolean {
    if (!url) {
      return false
    }

    // 检查是否为base64
    if (url.startsWith('data:image/')) {
      return true
    }

    // 检查是否为有效的URL格式
    try {
      const fullUrl = this.getImageUrl(url)
      return fullUrl.length > 0
    } catch {
      return false
    }
  }

  /**
   * 获取文件（预览）
   */
  static getFileUrl(fileId: string): string {
    return `${baseURL}/api/v1/files/${fileId}`
  }

  /**
   * 获取文件预览URL
   */
  static getPreviewUrl(fileId: string): string {
    if (!fileId) {
      return ''
    }

    // 如果已经是完整的URL，直接返回
    if (fileId.startsWith('http://') || fileId.startsWith('https://')) {
      return fileId
    }

    // 如果是文件路径，转换为预览URL
    if (fileId.startsWith('/')) {
      return `${baseURL}${fileId}`
    }

    // 否则认为是文件ID，构建预览URL
    return `${baseURL}/api/v1/files/${fileId}/preview`
  }

  /**
   * 获取文件下载URL
   */
  static getDownloadUrl(fileId: string): string {
    if (!fileId) {
      return ''
    }

    // 如果已经是完整的URL，直接返回
    if (fileId.startsWith('http://') || fileId.startsWith('https://')) {
      return fileId
    }

    // 构建下载URL
    return `${baseURL}/api/v1/files/${fileId}/download`
  }

  /**
   * 下载文件
   */
  static downloadFile(fileId: string, filename?: string) {
    const url = this.getDownloadUrl(fileId)
    return request.download(url, filename)
  }

  /**
   * 删除文件
   */
  static deleteFile(fileId: string) {
    return request.delete<OperationResult>(`/api/v1/files/${fileId}`)
  }

  /**
   * 获取文件信息
   */
  static getFileInfo(fileId: string) {
    return request.get<FileInfo>(`/api/v1/files/${fileId}/info`)
  }

  /**
   * 预加载图片
   */
  static preloadImage(fileId: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(img)
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = this.getPreviewUrl(fileId)
    })
  }

  /**
   * 获取文件类型图标
   */
  static getFileTypeIcon(contentType: string): string {
    if (contentType.startsWith('image/')) {
      return 'file-image'
    } else if (contentType.startsWith('video/')) {
      return 'file-video'
    } else if (contentType.startsWith('audio/')) {
      return 'file-audio'
    } else if (contentType.includes('pdf')) {
      return 'file-pdf'
    } else if (contentType.includes('word') || contentType.includes('document')) {
      return 'file-word'
    } else if (contentType.includes('excel') || contentType.includes('spreadsheet')) {
      return 'file-excel'
    } else if (contentType.includes('powerpoint') || contentType.includes('presentation')) {
      return 'file-ppt'
    } else if (contentType.includes('zip') || contentType.includes('rar') || contentType.includes('7z')) {
      return 'file-zip'
    } else if (contentType.includes('text')) {
      return 'file-text'
    } else {
      return 'file'
    }
  }

  /**
   * 格式化文件大小
   */
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B'
    
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  /**
   * 验证图片文件
   */
  static validateImageFile(file: File): Promise<{
    width: number
    height: number
    size: number
  }> {
    return new Promise((resolve, reject) => {
      if (!file.type.startsWith('image/')) {
        reject(new Error('不是有效的图片文件'))
        return
      }

      const img = new Image()
      const url = URL.createObjectURL(file)

      img.onload = () => {
        URL.revokeObjectURL(url)
        resolve({
          width: img.naturalWidth,
          height: img.naturalHeight,
          size: file.size
        })
      }

      img.onerror = () => {
        URL.revokeObjectURL(url)
        reject(new Error('图片文件损坏或格式不支持'))
      }

      img.src = url
    })
  }

  /**
   * 压缩图片
   */
  static compressImage(
    file: File,
    options: {
      maxWidth?: number
      maxHeight?: number
      quality?: number
    } = {}
  ): Promise<File> {
    return new Promise((resolve, reject) => {
      const {
        maxWidth = 1920,
        maxHeight = 1080,
        quality = 0.8
      } = options

      if (!file.type.startsWith('image/')) {
        reject(new Error('不是有效的图片文件'))
        return
      }

      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      const img = new Image()

      img.onload = () => {
        // 计算压缩后的尺寸
        let { width, height } = img
        
        if (width > maxWidth) {
          height = (height * maxWidth) / width
          width = maxWidth
        }
        
        if (height > maxHeight) {
          width = (width * maxHeight) / height
          height = maxHeight
        }

        canvas.width = width
        canvas.height = height

        // 绘制压缩后的图片
        ctx?.drawImage(img, 0, 0, width, height)

        canvas.toBlob(
          (blob) => {
            if (blob) {
              const compressedFile = new File([blob], file.name, {
                type: file.type,
                lastModified: Date.now()
              })
              resolve(compressedFile)
            } else {
              reject(new Error('图片压缩失败'))
            }
          },
          file.type,
          quality
        )
      }

      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }

      img.src = URL.createObjectURL(file)
    })
  }

  /**
   * 创建文件预览缩略图
   */
  static createThumbnail(
    file: File,
    size: number = 200
  ): Promise<string> {
    return new Promise((resolve, reject) => {
      if (!file.type.startsWith('image/')) {
        reject(new Error('只支持图片文件生成缩略图'))
        return
      }

      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      const img = new Image()

      img.onload = () => {
        const { width, height } = img
        const scale = Math.min(size / width, size / height)
        
        canvas.width = width * scale
        canvas.height = height * scale

        ctx?.drawImage(img, 0, 0, canvas.width, canvas.height)
        
        const thumbnailUrl = canvas.toDataURL('image/jpeg', 0.8)
        resolve(thumbnailUrl)
      }

      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }

      img.src = URL.createObjectURL(file)
    })
  }
}

// 导出默认实例
export default FileAPI
