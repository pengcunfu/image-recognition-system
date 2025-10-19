import request, { baseURL } from '@/utils/request'
import { ImageUtils } from '@/utils/image'
import { formatFileSize, getFileTypeIcon } from '@/utils/helpers'
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
   * @deprecated 请使用 ImageUtils.getImageUrl
   */
  static getImageUrl(url: string | undefined | null): string {
    return ImageUtils.getImageUrl(url)
  }

  /**
   * 检查是否为有效的图片URL
   * @deprecated 请使用 ImageUtils.isValidImageUrl
   */
  static isValidImageUrl(url: string | undefined | null): boolean {
    return ImageUtils.isValidImageUrl(url)
  }

  /**
   * 从JSON数组中获取第一张图片URL
   * @deprecated 请使用 ImageUtils.getFirstImage
   */
  static getFirstImage(imagesJson: string | null | undefined): string {
    return ImageUtils.getFirstImage(imagesJson)
  }

  /**
   * 获取文件（预览）
   */
  static getFileUrl(fileId: string): string {
    return `${baseURL}/api/v1/files/${fileId}`
  }

  /**
   * 获取文件预览URL
   * @deprecated 请使用 ImageUtils.getPreviewUrl
   */
  static getPreviewUrl(fileId: string): string {
    return ImageUtils.getPreviewUrl(fileId)
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
   * @deprecated 请使用 ImageUtils.preloadImage
   */
  static preloadImage(fileId: string): Promise<HTMLImageElement> {
    return ImageUtils.preloadImage(fileId)
  }

  /**
   * 获取文件类型图标
   * @deprecated 请使用 getFileTypeIcon
   */
  static getFileTypeIcon(contentType: string): string {
    return getFileTypeIcon(contentType)
  }

  /**
   * 格式化文件大小
   * @deprecated 请使用 formatFileSize
   */
  static formatFileSize(bytes: number): string {
    return formatFileSize(bytes)
  }

  /**
   * 验证图片文件
   * @deprecated 请使用 ImageUtils.validateImageFile
   */
  static validateImageFile(file: File): Promise<{
    width: number
    height: number
    size: number
  }> {
    return ImageUtils.validateImageFile(file)
  }

  /**
   * 压缩图片
   * @deprecated 请使用 ImageUtils.compressImage
   */
  static compressImage(
    file: File,
    options: {
      maxWidth?: number
      maxHeight?: number
      quality?: number
    } = {}
  ): Promise<File> {
    return ImageUtils.compressImage(file, options)
  }

  /**
   * 创建文件预览缩略图
   * @deprecated 请使用 ImageUtils.createThumbnail
   */
  static createThumbnail(
    file: File,
    size: number = 200
  ): Promise<string> {
    return ImageUtils.createThumbnail(file, size)
  }
}

// 导出默认实例
export default FileAPI
