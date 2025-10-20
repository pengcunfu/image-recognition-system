import { get, post, put, del } from '@/utils/request'
import type {
  Comment,
  CommentListResponse,
  AddCommentRequest,
  CommentCreateResponse,
  GetPostCommentsParams,
  PaginationParams,
  OperationResult
} from './types'

/**
 * 评论API模块
 */
export class CommentAPI {
  /**
   * 获取帖子评论
   */
  static getPostComments(postId: number, params: GetPostCommentsParams) {
    return get<CommentListResponse>(`/api/v1/community/posts/${postId}/comments`, params)
  }

  /**
   * 添加评论
   */
  static addComment(postId: number, data: AddCommentRequest) {
    return post<CommentCreateResponse>(`/api/v1/community/posts/${postId}/comments`, data)
  }

  /**
   * 编辑评论
   */
  static updateComment(commentId: number, content: string) {
    return put<OperationResult>(`/api/v1/community/comments/${commentId}`, { content })
  }

  /**
   * 删除评论
   */
  static deleteComment(commentId: number) {
    return del<OperationResult>(`/api/v1/community/comments/${commentId}`)
  }

  /**
   * 点赞评论
   */
  static likeComment(commentId: number) {
    return post<OperationResult>(`/api/v1/community/comments/${commentId}/like`)
  }

  /**
   * 取消点赞评论
   */
  static unlikeComment(commentId: number) {
    return del<OperationResult>(`/api/v1/community/comments/${commentId}/like`)
  }

  /**
   * 获取用户的评论
   */
  static getUserComments(userId: number, params: PaginationParams) {
    return get<CommentListResponse>(`/api/v1/community/users/${userId}/comments`, params)
  }
}

// 导出默认实例
export default CommentAPI


