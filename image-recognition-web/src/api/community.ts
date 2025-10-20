import { get, post, put, del, patch, upload } from '@/utils/request'
import type {
  Post,
  PostListResponse,
  PostDetailResponse,
  CreatePostRequest,
  PostCreateResponse,
  PaginationParams,
  OperationResult,
  GetPostsParams,
  SearchPostsParams,
  HotTopic,
  CommunityStatsResponse,
  UserCommunityStatsResponse,
  CommunityCategory,
  MentionSuggestion,
  ImageUploadResponse,
  MultiImageUploadResponse
} from './types'

/**
 * 社区API模块
 */
export class CommunityAPI {
  /**
   * 获取帖子列表
   */
  static getPosts(params: GetPostsParams) {
    return get<PostListResponse>('/api/v1/community/posts', params)
  }

  /**
   * 获取帖子详情
   */
  static getPostDetail(id: number) {
    return get<PostDetailResponse>(`/api/v1/community/posts/${id}`)
  }

  /**
   * 创建帖子
   */
  static createPost(data: CreatePostRequest) {
    return post<PostCreateResponse>('/api/v1/community/posts', data)
  }

  /**
   * 编辑帖子
   */
  static updatePost(id: number, data: Partial<CreatePostRequest>) {
    return put<OperationResult>(`/api/v1/community/posts/${id}`, data)
  }

  /**
   * 删除帖子
   */
  static deletePost(id: number) {
    return del<OperationResult>(`/api/v1/community/posts/${id}`)
  }

  /**
   * 点赞帖子
   */
  static likePost(postId: number) {
    return post<OperationResult>(`/api/v1/community/posts/${postId}/like`)
  }

  /**
   * 取消点赞帖子
   */
  static unlikePost(postId: number) {
    return del<OperationResult>(`/api/v1/community/posts/${postId}/like`)
  }

  /**
   * 收藏帖子
   */
  static collectPost(postId: number) {
    return post<OperationResult>(`/api/v1/community/posts/${postId}/collect`)
  }

  /**
   * 取消收藏帖子
   */
  static uncollectPost(postId: number) {
    return del<OperationResult>(`/api/v1/community/posts/${postId}/collect`)
  }

  /**
   * 分享帖子
   */
  static sharePost(postId: number, platform?: string) {
    return post<OperationResult>(`/api/v1/community/posts/${postId}/share`, { platform })
  }

  /**
   * 获取相关推荐帖子
   */
  static getRelatedPosts(postId: number, limit: number = 3) {
    return get<Post[]>(`/api/v1/community/posts/${postId}/related`, { limit })
  }

  /**
   * 关注用户
   */
  static followUser(userId: number) {
    return post<OperationResult>(`/api/v1/community/users/${userId}/follow`)
  }

  /**
   * 取消关注用户
   */
  static unfollowUser(userId: number) {
    return del<OperationResult>(`/api/v1/community/users/${userId}/follow`)
  }

  /**
   * 获取用户的帖子
   */
  static getUserPosts(userId: number, params: PaginationParams) {
    return get<PostListResponse>(`/api/v1/community/users/${userId}/posts`, params)
  }

  /**
   * 获取用户收藏的帖子
   */
  static getUserCollections(params: PaginationParams) {
    return get<PostListResponse>('/api/v1/community/collections', params)
  }

  /**
   * 获取用户点赞的帖子
   */
  static getUserLikes(params: PaginationParams) {
    return get<PostListResponse>('/api/v1/community/likes', params)
  }

  /**
   * 搜索帖子
   */
  static searchPosts(params: SearchPostsParams) {
    return get<PostListResponse>('/api/v1/community/search', params)
  }

  /**
   * 获取热门话题
   */
  static getHotTopics(limit: number = 10) {
    return get<HotTopic[]>('/api/v1/community/hot-topics', { limit })
  }

  /**
   * 获取推荐帖子
   */
  static getRecommendedPosts(limit: number = 10) {
    return get<Post[]>('/api/v1/community/recommendations', { limit })
  }

  /**
   * 获取社区统计
   */
  static getCommunityStats() {
    return get<CommunityStatsResponse>('/api/v1/community/stats')
  }

  /**
   * 获取用户社区统计
   */
  static getUserCommunityStats() {
    return get<UserCommunityStatsResponse>('/api/v1/community/user-stats')
  }

  /**
   * 获取社区分类
   */
  static getCategories() {
    return get<CommunityCategory[]>('/api/v1/community/categories')
  }

  /**
   * 获取@提及建议
   */
  static getMentionSuggestions(query: string) {
    return get<MentionSuggestion[]>('/api/v1/community/mention-suggestions', { query })
  }

  /**
   * 上传图片
   */
  static uploadImage(file: File) {
    return upload<ImageUploadResponse>('/api/v1/community/upload/image', file)
  }

  /**
   * 上传多张图片
   */
  static uploadImages(files: File[]) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    return upload<MultiImageUploadResponse>('/api/v1/community/upload/images', formData)
  }

  // ==================== 管理员接�?====================

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
    return get<PostListResponse>('/api/v1/community/admin/posts', 
      { page, size, category, status, keyword, sort }
    )
  }

  /**
   * 审核通过帖子
   */
  static approvePost(postId: number) {
    return post<OperationResult>(`/api/v1/community/admin/posts/${postId}/approve`)
  }

  /**
   * 拒绝帖子发布
   */
  static rejectPost(postId: number, reason?: string) {
    return post<OperationResult>(`/api/v1/community/admin/posts/${postId}/reject`, { reason })
  }

  /**
   * 置顶/取消置顶帖子
   */
  static toggleTopPost(postId: number, isTop: boolean) {
    return put<OperationResult>(`/api/v1/community/admin/posts/${postId}/top`, { isTop })
  }

  /**
   * 隐藏/显示帖子
   */
  static togglePostVisibility(postId: number, isHidden: boolean) {
    return put<OperationResult>(`/api/v1/community/admin/posts/${postId}/visibility`, { isHidden })
  }

  /**
   * 删除帖子（管理员）
   */
  static deletePostByAdmin(postId: number) {
    return del<OperationResult>(`/api/v1/community/admin/posts/${postId}`)
  }
}

// 导出默认实例
export default CommunityAPI
