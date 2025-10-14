import request from '@/utils/request'
import type {
  Post,
  PostListResponse,
  PostDetailResponse,
  CreatePostRequest,
  PostCreateResponse,
  Comment,
  CommentListResponse,
  AddCommentRequest,
  CommentCreateResponse,
  PaginationParams,
  OperationResult
} from './types'

/**
 * 社区API模块
 */
export class CommunityAPI {
  /**
   * 获取帖子列表
   */
  static getPosts(params: {
    page?: number
    size?: number
    category?: string
    sort?: 'latest' | 'hot' | 'top'
  }) {
    return request.get<PostListResponse>('/api/v1/community/posts', params)
  }

  /**
   * 获取帖子详情
   */
  static getPostDetail(id: number) {
    return request.get<PostDetailResponse>(`/api/v1/community/posts/${id}`)
  }

  /**
   * 创建帖子
   */
  static createPost(data: CreatePostRequest) {
    return request.post<PostCreateResponse>('/api/v1/community/posts', data)
  }

  /**
   * 编辑帖子
   */
  static updatePost(id: number, data: Partial<CreatePostRequest>) {
    return request.put<OperationResult>(`/api/v1/community/posts/${id}`, data)
  }

  /**
   * 删除帖子
   */
  static deletePost(id: number) {
    return request.delete<OperationResult>(`/api/v1/community/posts/${id}`)
  }

  /**
   * 获取帖子评论
   */
  static getPostComments(postId: number, params: {
    page?: number
    size?: number
  }) {
    return request.get<CommentListResponse>(`/api/v1/community/posts/${postId}/comments`, params)
  }

  /**
   * 添加评论
   */
  static addComment(postId: number, data: AddCommentRequest) {
    return request.post<CommentCreateResponse>(`/api/v1/community/posts/${postId}/comments`, data)
  }

  /**
   * 编辑评论
   */
  static updateComment(commentId: number, content: string) {
    return request.put<OperationResult>(`/api/v1/community/comments/${commentId}`, { content })
  }

  /**
   * 删除评论
   */
  static deleteComment(commentId: number) {
    return request.delete<OperationResult>(`/api/v1/community/comments/${commentId}`)
  }

  /**
   * 点赞帖子
   */
  static likePost(postId: number) {
    return request.post<OperationResult>(`/api/v1/community/posts/${postId}/like`)
  }

  /**
   * 取消点赞帖子
   */
  static unlikePost(postId: number) {
    return request.delete<OperationResult>(`/api/v1/community/posts/${postId}/like`)
  }

  /**
   * 点赞评论
   */
  static likeComment(commentId: number) {
    return request.post<OperationResult>(`/api/v1/community/comments/${commentId}/like`)
  }

  /**
   * 取消点赞评论
   */
  static unlikeComment(commentId: number) {
    return request.delete<OperationResult>(`/api/v1/community/comments/${commentId}/like`)
  }

  /**
   * 收藏帖子
   */
  static collectPost(postId: number) {
    return request.post<OperationResult>(`/api/v1/community/posts/${postId}/collect`)
  }

  /**
   * 取消收藏帖子
   */
  static uncollectPost(postId: number) {
    return request.delete<OperationResult>(`/api/v1/community/posts/${postId}/collect`)
  }

  /**
   * 分享帖子
   */
  static sharePost(postId: number, platform?: string) {
    return request.post<OperationResult>(`/api/v1/community/posts/${postId}/share`, { platform })
  }

  /**
   * 举报帖子
   */
  static reportPost(postId: number, data: {
    type: 'spam' | 'abuse' | 'inappropriate' | 'copyright' | 'other'
    description: string
  }) {
    return request.post<OperationResult>(`/api/v1/community/posts/${postId}/report`, data)
  }

  /**
   * 获取相关推荐帖子
   */
  static getRelatedPosts(postId: number, limit: number = 3) {
    return request.get<Post[]>(`/api/v1/community/posts/${postId}/related`, { limit })
  }

  /**
   * 举报评论
   */
  static reportComment(commentId: number, data: {
    type: 'spam' | 'inappropriate' | 'other'
    description: string
  }) {
    return request.post<OperationResult>(`/api/v1/community/comments/${commentId}/report`, data)
  }

  /**
   * 关注用户
   */
  static followUser(userId: number) {
    return request.post<OperationResult>(`/api/v1/community/users/${userId}/follow`)
  }

  /**
   * 取消关注用户
   */
  static unfollowUser(userId: number) {
    return request.delete<OperationResult>(`/api/v1/community/users/${userId}/follow`)
  }

  /**
   * 获取用户的帖子
   */
  static getUserPosts(userId: number, params: PaginationParams) {
    return request.get<PostListResponse>(`/api/v1/community/users/${userId}/posts`, params)
  }

  /**
   * 获取用户的评论
   */
  static getUserComments(userId: number, params: PaginationParams) {
    return request.get<CommentListResponse>(`/api/v1/community/users/${userId}/comments`, params)
  }

  /**
   * 获取用户收藏的帖子
   */
  static getUserCollections(params: PaginationParams) {
    return request.get<PostListResponse>('/api/v1/community/collections', params)
  }

  /**
   * 获取用户点赞的帖子
   */
  static getUserLikes(params: PaginationParams) {
    return request.get<PostListResponse>('/api/v1/community/likes', params)
  }

  /**
   * 搜索帖子
   */
  static searchPosts(params: {
    keyword: string
    page?: number
    size?: number
    category?: string
    author?: string
    dateRange?: [string, string]
  }) {
    return request.get<PostListResponse>('/api/v1/community/search', params)
  }

  /**
   * 获取热门话题
   */
  static getHotTopics(limit: number = 10) {
    return request.get<Array<{
      tag: string
      count: number
      trend: 'up' | 'down' | 'stable'
    }>>('/api/v1/community/hot-topics', { limit })
  }

  /**
   * 获取推荐帖子
   */
  static getRecommendedPosts(limit: number = 10) {
    return request.get<Post[]>('/api/v1/community/recommendations', { limit })
  }

  /**
   * 获取社区统计
   */
  static getCommunityStats() {
    return request.get<{
      totalPosts: number
      totalComments: number
      totalUsers: number
      activeUsers: number
      todayPosts: number
      todayComments: number
      hotCategories: Array<{
        category: string
        count: number
        percentage: number
      }>
    }>('/api/v1/community/stats')
  }

  /**
   * 获取用户社区统计
   */
  static getUserCommunityStats() {
    return request.get<{
      postsCount: number
      commentsCount: number
      likesReceived: number
      followersCount: number
      followingCount: number
      reputation: number
      level: number
      badges: Array<{
        id: string
        name: string
        description: string
        icon: string
        earnedAt: string
      }>
    }>('/api/v1/community/user-stats')
  }

  /**
   * 获取社区分类
   */
  static getCategories() {
    return request.get<Array<{
      id: string
      name: string
      description: string
      icon?: string
      postCount: number
      color?: string
    }>>('/api/v1/community/categories')
  }

  /**
   * 获取消息通知
   */
  static getNotifications(params: {
    page?: number
    size?: number
    type?: 'like' | 'comment' | 'follow' | 'mention' | 'system'
    unreadOnly?: boolean
  }) {
    return request.get<{
      notifications: Array<{
        id: number
        type: string
        title: string
        content: string
        data?: any
        read: boolean
        createTime: string
      }>
      total: number
      unreadCount: number
      page: number
      size: number
    }>('/api/v1/community/notifications', params)
  }

  /**
   * 标记通知为已读
   */
  static markNotificationAsRead(notificationId: number) {
    return request.put<OperationResult>(`/api/v1/community/notifications/${notificationId}/read`)
  }

  /**
   * 标记所有通知为已读
   */
  static markAllNotificationsAsRead() {
    return request.put<OperationResult>('/api/v1/community/notifications/read-all')
  }

  /**
   * 删除通知
   */
  static deleteNotification(notificationId: number) {
    return request.delete<OperationResult>(`/api/v1/community/notifications/${notificationId}`)
  }

  /**
   * 获取@提及建议
   */
  static getMentionSuggestions(query: string) {
    return request.get<Array<{
      id: number
      username: string
      name?: string
      avatar?: string
    }>>('/api/v1/community/mention-suggestions', { query })
  }

  /**
   * 上传图片
   */
  static uploadImage(file: File) {
    return request.upload<{ url: string }>('/api/v1/community/upload/image', file)
  }

  /**
   * 上传多张图片
   */
  static uploadImages(files: File[]) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    return request.upload<{ urls: string[] }>('/api/v1/community/upload/images', formData)
  }

  // ==================== 管理员接口 ====================

  /**
   * 管理员获取所有帖子（包括所有状态）
   */
  static getAdminPosts(
    page?: number,
    size?: number,
    category?: string,
    status?: number,
    keyword?: string,
    sort?: number
  ) {
    return request.get<PostListResponse>('/api/v1/community/admin/posts', 
      { page, size, category, status, keyword, sort }
    )
  }

  /**
   * 审核通过帖子
   */
  static approvePost(postId: number) {
    return request.post<OperationResult>(`/api/v1/community/admin/posts/${postId}/approve`)
  }

  /**
   * 拒绝帖子发布
   */
  static rejectPost(postId: number, reason?: string) {
    return request.post<OperationResult>(`/api/v1/community/admin/posts/${postId}/reject`, { reason })
  }

  /**
   * 置顶/取消置顶帖子
   */
  static toggleTopPost(postId: number, isTop: boolean) {
    return request.put<OperationResult>(`/api/v1/community/admin/posts/${postId}/top`, { isTop })
  }

  /**
   * 隐藏/显示帖子
   */
  static togglePostVisibility(postId: number, isHidden: boolean) {
    return request.put<OperationResult>(`/api/v1/community/admin/posts/${postId}/visibility`, { isHidden })
  }

  /**
   * 删除帖子（管理员）
   */
  static deletePostByAdmin(postId: number) {
    return request.delete<OperationResult>(`/api/v1/community/admin/posts/${postId}`)
  }
}

// 导出默认实例
export default CommunityAPI
