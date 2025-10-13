import { baseURL } from './request'

/**
 * 获取完整的图片URL
 * @param url 图片URL（可能是相对路径或完整URL）
 * @returns 完整的图片URL
 */
export function getImageUrl(url: string | undefined | null): string {
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
 * 获取图片预览URL
 * @param fileId 文件ID
 * @returns 预览URL
 */
export function getPreviewUrl(fileId: string): string {
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
 * @param fileId 文件ID
 * @returns 下载URL
 */
export function getDownloadUrl(fileId: string): string {
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
 * 检查是否为有效的图片URL
 * @param url 图片URL
 * @returns 是否有效
 */
export function isValidImageUrl(url: string | undefined | null): boolean {
  if (!url) {
    return false
  }

  // 检查是否为base64
  if (url.startsWith('data:image/')) {
    return true
  }

  // 检查是否为有效的URL格式
  try {
    const fullUrl = getImageUrl(url)
    return fullUrl.length > 0
  } catch {
    return false
  }
}

