import { get, post, put, del, patch } from '@/utils/request'
import type {
  KnowledgeCategory,
  KnowledgeItem,
  KnowledgePage,
  KnowledgeSearchResult,
  KnowledgeStats,
  KnowledgeCreateResponse,
  KnowledgeOperationResult,
  PaginationParams,
  OperationResult,
  CreateKnowledgeCategoryRequest,
  UpdateKnowledgeCategoryRequest,
  GetKnowledgeItemsParams,
  CreateKnowledgeItemRequest,
  UpdateKnowledgeItemRequest,
  SearchKnowledgeItemsParams,
  AdvancedSearchKnowledgeParams,
  SubmitKnowledgeFeedbackRequest,
  KnowledgeItemCommentsResponse,
  LearningProgressResponse,
  LearningStatsResponse
} from './types'

/**
 * 知识库API模块
 */
export class KnowledgeAPI {
  /**
   * 获取知识分类列表
   */
  static getCategories(status?: number, keyword?: string) {
    return get<KnowledgeCategory[]>('/api/v1/knowledge/categories', {
      status,
      keyword
    })
  }

  /**
   * 创建知识分类
   */
  static createCategory(data: CreateKnowledgeCategoryRequest) {
    return post<KnowledgeCreateResponse>('/api/v1/knowledge/categories', data)
  }

  /**
   * 更新知识分类
   */
  static updateCategory(id: number, data: UpdateKnowledgeCategoryRequest) {
    return put<KnowledgeCreateResponse>(`/api/v1/knowledge/categories/${id}`, data)
  }

  /**
   * 删除知识分类
   */
  static deleteCategory(id: number) {
    return del<void>(`/api/v1/knowledge/categories/${id}`)
  }

  /**
   * 获取分类详情
   */
  static getCategoryDetail(id: number) {
    return get<KnowledgeCategory>(`/api/v1/knowledge/categories/${id}`)
  }

  /**
   * 获取知识条目列表
   */
  static getItems(params: GetKnowledgeItemsParams) {
    return get<KnowledgePage>('/api/v1/knowledge/items', params)
  }

  /**
   * 获取知识条目详情
   */
  static getItemDetail(id: string) {
    return get<KnowledgeItem>(`/api/v1/knowledge/items/${id}`)
  }

  /**
   * 创建知识条目
   */
  static createItem(data: CreateKnowledgeItemRequest) {
    return post<KnowledgeCreateResponse>('/api/v1/knowledge/items', data)
  }

  /**
   * 更新知识条目
   */
  static updateItem(id: number, data: UpdateKnowledgeItemRequest) {
    return put<KnowledgeCreateResponse>(`/api/v1/knowledge/items/${id}`, data)
  }

  /**
   * 删除知识条目
   */
  static deleteItem(id: number) {
    return del<void>(`/api/v1/knowledge/items/${id}`)
  }

  /**
   * 搜索知识条目
   */
  static searchItems(params: SearchKnowledgeItemsParams) {
    return get<KnowledgePage>('/api/v1/knowledge/search', params)
  }

  /**
   * 高级搜索知识条目
   */
  static advancedSearch(params: AdvancedSearchKnowledgeParams) {
    return get<KnowledgeSearchResult>('/api/v1/knowledge/advanced-search', params)
  }

  /**
   * 获取热门知识
   */
  static getPopularItems(limit: number = 10) {
    return get<KnowledgeItem[]>('/api/v1/knowledge/popular', { limit })
  }

  /**
   * 获取最新知�?
   */
  static getLatestItems(limit: number = 10) {
    return get<KnowledgeItem[]>('/api/v1/knowledge/latest', { limit })
  }

  /**
   * 获取知识统计信息
   */
  static getStats() {
    return get<KnowledgeStats>('/api/v1/knowledge/stats')
  }

  /**
   * 点赞知识条目
   */
  static likeItem(itemId: number) {
    return post<void>(`/api/v1/knowledge/${itemId}/like`)
  }

  /**
   * 取消点赞知识条目
   */
  static unlikeItem(itemId: number) {
    return del<void>(`/api/v1/knowledge/${itemId}/like`)
  }

  /**
   * 收藏知识条目 (暂未实现)
   */
  static collectItem(itemId: number) {
    return post<OperationResult>(`/api/v1/knowledge/${itemId}/collect`)
  }

  /**
   * 取消收藏知识条目 (暂未实现)
   */
  static uncollectItem(itemId: number) {
    return del<OperationResult>(`/api/v1/knowledge/${itemId}/collect`)
  }

  /**
   * 分享知识条目 (暂未实现)
   */
  static shareItem(itemId: number, platform?: string) {
    return post<OperationResult>(`/api/v1/knowledge/${itemId}/share`, { platform })
  }

  /**
   * 获取用户收藏的知识条�?
   */
  static getUserCollections(params: PaginationParams) {
    return get<KnowledgePage>('/api/v1/knowledge/collections', params)
  }

  /**
   * 获取用户浏览历史
   */
  static getUserHistory(params: PaginationParams) {
    return get<KnowledgePage>('/api/v1/knowledge/history', params)
  }

  /**
   * 清除浏览历史
   */
  static clearHistory() {
    return del<OperationResult>('/api/v1/knowledge/history')
  }

  /**
   * 获取推荐知识
   */
  static getRecommendations(limit: number = 10) {
    return get<KnowledgeItem[]>('/api/v1/knowledge/recommendations', { limit })
  }

  /**
   * 获取相关知识
   */
  static getRelatedItems(itemId: string, limit: number = 5) {
    return get<KnowledgeItem[]>(`/api/v1/knowledge/${itemId}/related`, { limit })
  }

  /**
   * 提交知识条目反馈
   */
  static submitFeedback(itemId: number, data: SubmitKnowledgeFeedbackRequest) {
    return post<OperationResult>(`/api/v1/knowledge/${itemId}/feedback`, data)
  }

  /**
   * 获取知识条目评论
   */
  static getItemComments(itemId: number, params: PaginationParams) {
    return get<KnowledgeItemCommentsResponse>(`/api/v1/knowledge/${itemId}/comments`, params)
  }

  /**
   * 添加知识条目评论
   */
  static addItemComment(itemId: number, content: string) {
    return post<OperationResult>(`/api/v1/knowledge/${itemId}/comments`, { content })
  }

  /**
   * 获取知识学习进度
   */
  static getLearningProgress() {
    return get<LearningProgressResponse>('/api/v1/knowledge/progress')
  }

  /**
   * 标记知识为已学习
   */
  static markAsLearned(itemId: number) {
    return post<OperationResult>(`/api/v1/knowledge/${itemId}/learned`)
  }

  /**
   * 取消已学习标�?
   */
  static unmarkAsLearned(itemId: number) {
    return del<OperationResult>(`/api/v1/knowledge/${itemId}/learned`)
  }

  /**
   * 获取学习统计
   */
  static getLearningStats() {
    return get<LearningStatsResponse>('/api/v1/knowledge/learning-stats')
  }
}

// 导出默认实例
export default KnowledgeAPI
