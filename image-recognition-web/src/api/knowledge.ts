import request from '@/utils/request'
import type {
  KnowledgeCategory,
  KnowledgeItem,
  KnowledgePage,
  KnowledgeSearchResult,
  KnowledgeStats,
  KnowledgeCreateResponse,
  KnowledgeOperationResult,
  PaginationParams,
  OperationResult
} from './types'

/**
 * 知识库API模块
 */
export class KnowledgeAPI {
  /**
   * 获取知识分类列表
   */
  static getCategories() {
    return request.get<KnowledgeCategory[]>('/api/v1/knowledge/categories')
  }

  /**
   * 获取知识条目列表
   */
  static getItems(params: {
    category?: string
    page?: number
    size?: number
    keyword?: string
  }) {
    return request.get<KnowledgePage>('/api/v1/knowledge/items', params)
  }

  /**
   * 获取知识条目详情
   */
  static getItemDetail(id: string) {
    return request.get<KnowledgeItem>(`/api/v1/knowledge/items/${id}`)
  }

  /**
   * 搜索知识条目
   */
  static searchItems(params: {
    keyword: string
    page?: number
    size?: number
  }) {
    return request.get<KnowledgePage>('/api/v1/knowledge/search', params)
  }

  /**
   * 高级搜索知识条目
   */
  static advancedSearch(params: {
    keyword: string
    page?: number
    size?: number
    category?: string
  }) {
    return request.get<KnowledgeSearchResult>('/api/v1/knowledge/advanced-search', params)
  }

  /**
   * 获取热门知识
   */
  static getPopularItems(limit: number = 10) {
    return request.get<KnowledgeItem[]>('/api/v1/knowledge/popular', { limit })
  }

  /**
   * 获取最新知识
   */
  static getLatestItems(limit: number = 10) {
    return request.get<KnowledgeItem[]>('/api/v1/knowledge/latest', { limit })
  }

  /**
   * 获取知识统计信息
   */
  static getStats() {
    return request.get<KnowledgeStats>('/api/v1/knowledge/stats')
  }

  /**
   * 点赞知识条目
   */
  static likeItem(itemId: number) {
    return request.post<void>(`/api/v1/knowledge/${itemId}/like`)
  }

  /**
   * 取消点赞知识条目
   */
  static unlikeItem(itemId: number) {
    return request.delete<void>(`/api/v1/knowledge/${itemId}/like`)
  }

  /**
   * 收藏知识条目 (暂未实现)
   */
  static collectItem(itemId: number) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/collect`)
  }

  /**
   * 取消收藏知识条目 (暂未实现)
   */
  static uncollectItem(itemId: number) {
    return request.delete<OperationResult>(`/api/v1/knowledge/${itemId}/collect`)
  }

  /**
   * 分享知识条目 (暂未实现)
   */
  static shareItem(itemId: number, platform?: string) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/share`, { platform })
  }

  /**
   * 获取用户收藏的知识条目
   */
  static getUserCollections(params: PaginationParams) {
    return request.get<KnowledgePage>('/api/v1/knowledge/collections', params)
  }

  /**
   * 获取用户浏览历史
   */
  static getUserHistory(params: PaginationParams) {
    return request.get<KnowledgePage>('/api/v1/knowledge/history', params)
  }

  /**
   * 清除浏览历史
   */
  static clearHistory() {
    return request.delete<OperationResult>('/api/v1/knowledge/history')
  }

  /**
   * 获取推荐知识
   */
  static getRecommendations(limit: number = 10) {
    return request.get<KnowledgeItem[]>('/api/v1/knowledge/recommendations', { limit })
  }

  /**
   * 获取相关知识
   */
  static getRelatedItems(itemId: string, limit: number = 5) {
    return request.get<KnowledgeItem[]>(`/api/v1/knowledge/${itemId}/related`, { limit })
  }

  /**
   * 报告知识条目问题
   */
  static reportItem(itemId: number, data: {
    type: 'content_error' | 'copyright' | 'inappropriate' | 'other'
    description: string
  }) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/report`, data)
  }

  /**
   * 提交知识条目反馈
   */
  static submitFeedback(itemId: number, data: {
    rating: number
    comment?: string
    helpful: boolean
  }) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/feedback`, data)
  }

  /**
   * 获取知识条目评论
   */
  static getItemComments(itemId: number, params: PaginationParams) {
    return request.get<{
      comments: Array<{
        id: number
        content: string
        author: {
          id: number
          username: string
          avatar?: string
        }
        createTime: string
        likes: number
        isLiked: boolean
      }>
      total: number
      page: number
      size: number
    }>(`/api/v1/knowledge/${itemId}/comments`, params)
  }

  /**
   * 添加知识条目评论
   */
  static addItemComment(itemId: number, content: string) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/comments`, { content })
  }

  /**
   * 获取知识学习进度
   */
  static getLearningProgress() {
    return request.get<{
      totalItems: number
      learnedItems: number
      progress: number
      categories: Array<{
        category: string
        total: number
        learned: number
        progress: number
      }>
    }>('/api/v1/knowledge/progress')
  }

  /**
   * 标记知识为已学习
   */
  static markAsLearned(itemId: number) {
    return request.post<OperationResult>(`/api/v1/knowledge/${itemId}/learned`)
  }

  /**
   * 取消已学习标记
   */
  static unmarkAsLearned(itemId: number) {
    return request.delete<OperationResult>(`/api/v1/knowledge/${itemId}/learned`)
  }

  /**
   * 获取学习统计
   */
  static getLearningStats() {
    return request.get<{
      totalTime: number // 总学习时间（分钟）
      todayTime: number // 今日学习时间
      weekTime: number // 本周学习时间
      monthTime: number // 本月学习时间
      streak: number // 连续学习天数
      achievements: Array<{
        id: string
        name: string
        description: string
        icon: string
        unlockedAt?: string
      }>
    }>('/api/v1/knowledge/learning-stats')
  }
}

// 导出默认实例
export default KnowledgeAPI
