import { post, del, upload, baseURL } from '@/utils/request'

/**
 * 文件API模块
 */
export class FileAPI {
  /**
   * 上传图片
   */
  static uploadImage(file: File) {
    return upload<string>('/api/files/upload/image', file)
  }

  /**
   * 批量上传图片
   */
  static uploadImages(files: File[]) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    return upload<string[]>('/api/files/upload/images', formData)
  }

  /**
   * 上传文件
   */
  static uploadFile(file: File) {
    return upload<string>('/api/files/upload/file', file)
  }

  /**
   * 删除文件
   */
  static deleteFile(url: string) {
    return del<void>('/api/files', { params: { url } })
  }

  /**
   * 获取图片完整URL
   */
  static getImageUrl(url?: string) {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    return `${baseURL}${url}`
  }

  /**
   * 下载文件
   */
  static downloadFile(url: string, filename?: string) {
    const link = document.createElement('a')
    link.href = `${baseURL}/api/files/download?url=${encodeURIComponent(url)}`
    if (filename) {
      link.download = filename
    }
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

// 导出默认实例
export default FileAPI
