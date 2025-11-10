import { get, post, del } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 评论信息
 */
export interface CommentInfo {
  id: number
  content: string
  targetType: number
  targetId: number
  parentId?: number
  userId: number
  username: string
  avatar?: string
  likeCount: number
  isLiked: boolean
  createdAt: string
  updatedAt?: string
  replies?: CommentInfo[]
}

/**
 * 评论查询参数
 */
export interface QueryCommentParams {
  targetType: number
  targetId: number
  page?: number
  size?: number
}

/**
 * 创建评论请求
 */
export interface CreateCommentRequest {
  targetType: number
  targetId: number
  content: string
  parentId?: number
}

/**
 * 评论API模块
 */
export class CommentAPI {
  /**
   * 获取评论列表
   */
  static getComments(params: QueryCommentParams) {
    return get<PageResponse<CommentInfo>>('/api/comments', params)
  }

  /**
   * 创建评论
   */
  static createComment(data: CreateCommentRequest) {
    return post<CommentInfo>('/api/comments', data)
  }

  /**
   * 删除评论
   */
  static deleteComment(id: number) {
    return del<void>(`/api/comments/${id}`)
  }

  /**
   * 点赞评论
   */
  static likeComment(id: number) {
    return post<void>(`/api/comments/${id}/like`)
  }

  /**
   * 取消点赞评论
   */
  static unlikeComment(id: number) {
    return del<void>(`/api/comments/${id}/like`)
  }
}

// 导出默认实例
export default CommentAPI
