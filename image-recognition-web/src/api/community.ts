import { get, post, put, del } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 帖子信息
 */
export interface PostInfo {
  id: number
  title: string
  content: string
  category: string
  tags?: string
  authorId: number
  authorName?: string
  authorAvatar?: string
  images?: string[]  // 改为数组类型
  viewCount: number
  likeCount: number
  commentCount: number
  shareCount: number
  isTop: number
  status: number
  createdAt: string
  updatedAt?: string
}

/**
 * 创建帖子请求
 */
export interface CreatePostRequest {
  title: string
  content: string
  category: string
  tags: string[]
  images?: string[]
  status?: number
}

/**
 * 分类信息
 */
export interface CategoryInfo {
  name: string
  count: number
  description?: string
}

/**
 * 标签信息
 */
export interface TagInfo {
  name: string
  count: number
}

/**
 * 获取帖子列表参数
 */
export interface GetPostsParams {
  page?: number
  size?: number
  category?: string
  tag?: string
  keyword?: string
  sort?: 'latest' | 'hot' | 'top'
}

/**
 * 社区API模块
 */
export class CommunityAPI {
  /**
   * 获取帖子列表
   */
  static getPosts(params: GetPostsParams) {
    return get<PageResponse<PostInfo>>('/api/community/posts', params)
  }

  /**
   * 获取帖子详情
   */
  static getPostDetail(id: number) {
    return get<PostInfo>(`/api/community/posts/${id}`)
  }

  /**
   * 创建帖子
   */
  static createPost(data: CreatePostRequest) {
    return post<PostInfo>('/api/community/posts', data)
  }

  /**
   * 更新帖子
   */
  static updatePost(id: number, data: Partial<CreatePostRequest>) {
    return put<void>(`/api/community/posts/${id}`, data)
  }

  /**
   * 删除帖子
   */
  static deletePost(id: number) {
    return del<void>(`/api/community/posts/${id}`)
  }

  /**
   * 点赞帖子
   */
  static likePost(postId: number) {
    return post<void>(`/api/community/posts/${postId}/like`)
  }

  /**
   * 取消点赞帖子
   */
  static unlikePost(postId: number) {
    return del<void>(`/api/community/posts/${postId}/like`)
  }

  /**
   * 收藏帖子
   */
  static collectPost(postId: number) {
    return post<void>(`/api/community/posts/${postId}/collect`)
  }

  /**
   * 取消收藏帖子
   */
  static uncollectPost(postId: number) {
    return del<void>(`/api/community/posts/${postId}/collect`)
  }

  /**
   * 获取帖子分类列表
   */
  static getCategories() {
    return get<CategoryInfo[]>('/api/community/categories')
  }

  /**
   * 获取帖子标签列表
   */
  static getTags() {
    return get<TagInfo[]>('/api/community/tags')
  }

  /**
   * 获取当前用户发布的帖子列表
   */
  static getMyPosts(params: { page?: number; size?: number }) {
    return get<PageResponse<PostInfo>>('/api/community/my-posts', params)
  }

  /**
   * 获取相关推荐帖子
   */
  static getRelatedPosts(id: number) {
    return get<PostInfo[]>(`/api/community/posts/${id}/related`)
  }

  /**
   * 获取热门帖子（按访问量排序）
   */
  static getHotPosts(limit: number = 5) {
    return get<PostInfo[]>('/api/community/hot-posts', { limit })
  }
}

// 导出默认实例
export default CommunityAPI
