import { get, post, del } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 知识库信息
 */
export interface KnowledgeInfo {
  id: number
  name?: string
  title: string
  category: string
  description?: string
  content: string
  detail?: string
  imageUrl?: string
  coverImage?: string
  images?: string
  tags?: string
  authorId: number
  authorName?: string
  viewCount: number
  likeCount: number
  collectCount?: number
  commentCount: number
  isLiked?: boolean
  isCollected?: boolean
  status: number
  createdAt: string
  updatedAt: string
}

/**
 * 知识查询参数
 */
export interface QueryKnowledgeParams {
  page?: number
  size?: number
  category?: string
  keyword?: string
}

/**
 * 知识库API模块
 */
export class KnowledgeAPI {
  /**
   * 获取知识列表
   */
  static getKnowledgeList(params: QueryKnowledgeParams) {
    return get<PageResponse<KnowledgeInfo>>('/api/knowledge', params)
  }

  /**
   * 获取知识详情
   */
  static getKnowledgeDetail(id: number) {
    return get<KnowledgeInfo>(`/api/knowledge/${id}`)
  }

  /**
   * 点赞知识
   */
  static likeKnowledge(id: number) {
    return post<void>(`/api/knowledge/${id}/like`)
  }

  /**
   * 取消点赞知识
   */
  static unlikeKnowledge(id: number) {
    return post<void>(`/api/knowledge/${id}/unlike`)
  }

  /**
   * 收藏知识
   */
  static collectKnowledge(id: number) {
    return post<void>(`/api/knowledge/${id}/collect`)
  }

  /**
   * 取消收藏知识
   */
  static uncollectKnowledge(id: number) {
    return post<void>(`/api/knowledge/${id}/uncollect`)
  }

  /**
   * 获取所有分类
   */
  static getCategories() {
    return get<string[]>('/api/knowledge/categories')
  }

  /**
   * 获取所有标签
   */
  static getTags() {
    return get<string[]>('/api/knowledge/tags')
  }
}

// 导出默认实例
export default KnowledgeAPI
