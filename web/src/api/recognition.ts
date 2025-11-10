import { get, del, upload,post } from '@/utils/request'
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
 * VIP识别统计数据
 */
export interface VipRecognitionStats {
  totalRecognitions: number
  advancedRecognitions: number
  averageConfidence: number
  categoryCount: number
  tagCount: number
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
   * 获取VIP识别统计数据
   */
  static getVipStats() {
    return get<VipRecognitionStats>('/api/recognition/vip-stats')
  }

  /**
   * 获取相关识别记录（同分类）
   */
  static getRelated(id: number) {
    return get<RecognitionInfo[]>(`/api/recognition/${id}/related`)
  }

  /**
   * 分享识别结果到知识库
   */
  static shareToKnowledge(id: number) {
    return post<number>(`/api/recognition/${id}/share-to-knowledge`)
  }

  /**
   * 高级图像识别（VIP功能）
   */
  static advancedRecognize(file: File, settings: AdvancedRecognitionSettings) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('recognitionType', '1') // 高级识别类型
    formData.append('settings', JSON.stringify(settings))
    return upload<RecognitionInfo>('/api/recognition/advanced-recognize', formData)
  }

  /**
   * 批量高级识别（VIP功能）
   */
  static batchAdvancedRecognize(files: File[], settings: AdvancedRecognitionSettings) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('recognitionType', '1') // 高级识别类型
    formData.append('settings', JSON.stringify(settings))
    return upload<RecognitionInfo[]>('/api/recognition/batch-advanced-recognize', formData)
  }
}

/**
 * 高级识别设置
 */
export interface AdvancedRecognitionSettings {
  mode: 'precision' | 'multi' | 'scene'
  precision: number
  threshold: number
  depth: 'basic' | 'advanced' | 'expert'
  outputs: string[]
  features: string[]
}

// 导出默认实例
export default RecognitionAPI
