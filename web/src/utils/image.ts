import { baseURL } from './request'

/**
 * 图像处理工具类
 */
export class ImageUtils {
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
   * 从JSON数组中获取第一张图片URL
   * 用于处理数据库中存储为JSON数组格式的图片字段
   */
  static getFirstImage(imagesJson: string | null | undefined): string {
    if (!imagesJson) return ''
    
    try {
      const images = JSON.parse(imagesJson)
      if (Array.isArray(images) && images.length > 0) {
        // 返回第一张图片的完整URL
        return this.getImageUrl(images[0])
      }
      return ''
    } catch {
      // 如果不是JSON格式，直接当作单个URL处理（兼容旧数据）
      return this.getImageUrl(imagesJson)
    }
  }

  /**
   * 从JSON数组中获取所有图片URL
   */
  static getAllImages(imagesJson: string | null | undefined): string[] {
    if (!imagesJson) return []
    
    try {
      const images = JSON.parse(imagesJson)
      if (Array.isArray(images)) {
        return images.map(img => this.getImageUrl(img)).filter(url => url.length > 0)
      }
      return []
    } catch {
      // 如果不是JSON格式，直接当作单个URL处理（兼容旧数据）
      const url = this.getImageUrl(imagesJson)
      return url ? [url] : []
    }
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
   * 预加载图片
   */
  static preloadImage(url: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(img)
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = this.getImageUrl(url)
    })
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

  /**
   * 图片转Base64
   */
  static imageToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => {
        if (typeof reader.result === 'string') {
          resolve(reader.result)
        } else {
          reject(new Error('转换失败'))
        }
      }
      reader.onerror = () => reject(new Error('读取文件失败'))
      reader.readAsDataURL(file)
    })
  }

  /**
   * Base64转Blob
   */
  static base64ToBlob(base64: string, contentType: string = 'image/png'): Blob {
    const byteString = atob(base64.split(',')[1])
    const ab = new ArrayBuffer(byteString.length)
    const ia = new Uint8Array(ab)
    
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i)
    }
    
    return new Blob([ab], { type: contentType })
  }

  /**
   * 获取图片尺寸
   */
  static getImageDimensions(url: string): Promise<{ width: number; height: number }> {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => {
        resolve({
          width: img.naturalWidth,
          height: img.naturalHeight
        })
      }
      img.onerror = () => reject(new Error('加载图片失败'))
      img.src = url
    })
  }

  /**
   * 裁剪图片
   */
  static cropImage(
    file: File,
    cropData: {
      x: number
      y: number
      width: number
      height: number
    }
  ): Promise<File> {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      const img = new Image()

      img.onload = () => {
        canvas.width = cropData.width
        canvas.height = cropData.height

        ctx?.drawImage(
          img,
          cropData.x,
          cropData.y,
          cropData.width,
          cropData.height,
          0,
          0,
          cropData.width,
          cropData.height
        )

        canvas.toBlob(
          (blob) => {
            if (blob) {
              const croppedFile = new File([blob], file.name, {
                type: file.type,
                lastModified: Date.now()
              })
              resolve(croppedFile)
            } else {
              reject(new Error('裁剪失败'))
            }
          },
          file.type
        )
      }

      img.onerror = () => reject(new Error('加载图片失败'))
      img.src = URL.createObjectURL(file)
    })
  }
}

// 默认导出
export default ImageUtils

