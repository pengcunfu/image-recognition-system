import { get, del, upload } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 识别结果信息
 */
export interface RecognitionInfo {
  id: number
  userId: number
  imageUrl: string
  imageName?: string
  imageSize?: number
  imageWidth?: number
  imageHeight?: number
  recognitionType: number
  resultJson?: string
  mainCategory?: string
  category?: string
  objectName?: string
  confidence: number
  tags?: string
  attributes?: string
  description?: string
  processingTime?: number
  status: number
  errorMessage?: string
  createdAt: string
  createTime?: string
  updateTime?: string
}

/**
 * 识别历史查询参数
 */
export interface HistoryQueryParams {
  page?: number
  size?: number
}

/**
 * 识别统计数据
 */
export interface RecognitionStats {
  total: number
  thisMonth: number
  averageConfidence: number
  favorites: number
}

/**
 * 图像识别API模块
 */
export class RecognitionAPI {
  /**
   * 图像识别（上传文件）
   */
  static recognize(file: File, recognitionType: number = 0) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('recognitionType', String(recognitionType))
    return upload<RecognitionInfo>('/api/recognition/recognize', formData)
  }

  /**
   * 批量图像识别（上传多个文件）
   */
  static batchRecognize(files: File[], recognitionType: number = 0) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('recognitionType', String(recognitionType))
    return upload<RecognitionInfo[]>('/api/recognition/batch-recognize', formData)
  }

  /**
   * 获取识别历史列表
   */
  static getHistory(params: HistoryQueryParams) {
    return get<PageResponse<RecognitionInfo>>('/api/recognition/history', params)
  }

  /**
   * 获取识别结果详情
   */
  static getDetail(id: number) {
    return get<RecognitionInfo>(`/api/recognition/${id}`)
  }

  /**
   * 删除识别记录
   */
  static deleteRecognition(id: number) {
    return del<void>(`/api/recognition/${id}`)
  }

  /**
   * 获取识别统计数据
   */
  static getStats() {
    return get<RecognitionStats>('/api/recognition/stats')
  }

  /**
   * 获取相关识别记录（同分类）
   */
  static getRelated(id: number) {
    return get<RecognitionInfo[]>(`/api/recognition/${id}/related`)
  }
}

// 导出默认实例
export default RecognitionAPI
