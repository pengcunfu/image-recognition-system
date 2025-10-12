// 通用类型定义

// 分页参数
export interface PaginationParams {
  page?: number
  size?: number
}

// 分页响应
export interface PaginationResponse<T> {
  items: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 操作结果
export interface OperationResult {
  success: boolean
  message: string
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  name?: string
  email?: string
  avatar?: string
  role: string
  vipLevel?: number
  vipExpireTime?: string
}

// ==================== 认证相关类型 ====================

// 登录请求
export interface LoginRequest {
  username: string
  password: string
  captcha?: string
  rememberMe?: boolean
}

// 登录响应
export interface LoginResponse {
  success: boolean
  message: string
  token?: string
  user?: UserInfo
}

// 注册请求
export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword?: string
  captcha?: string
  emailCode: string
  acceptTerms?: boolean
}

// 注册响应
export interface RegisterResponse {
  success: boolean
  message: string
  user?: UserInfo
}

// 忘记密码请求
export interface ForgotPasswordRequest {
  email: string
  captcha?: string
}

// 验证码响应
export interface CaptchaResponse {
  captchaId: string
  captchaImage: string
  expiry: number
}

// Token验证响应
export interface TokenValidationResponse {
  valid: boolean
  userInfo?: UserInfo
}

// 短信验证码请求
export interface SmsCodeRequest {
  phone: string
  type?: 'register' | 'login' | 'reset'
}

// 短信验证码响应
export interface SmsCodeResponse {
  phone: string
  codeExpiry: number
  sendTime: number
}

// 短信验证码验证请求
export interface SmsCodeVerifyRequest {
  phone: string
  code: string
}

// ==================== 知识库相关类型 ====================

// 知识分类
export interface KnowledgeCategory {
  id: string
  name: string
  description?: string
  icon?: string
  count: number
}

// 知识条目
export interface KnowledgeItem {
  id: string
  title: string
  description: string
  content?: string
  category: string
  tags: string[]
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  author: string
  authorAvatar?: string
  coverImage?: string
  images?: string[]
  views: number
  likes: number
  collections: number
  shares: number
  createTime: string
  updateTime: string
  status: 'published' | 'draft'
}

// 知识库分页响应
export interface KnowledgePage {
  items: KnowledgeItem[]
  total: number
  page: number
  size: number
  totalPages: number
  categories: KnowledgeCategory[]
}

// 知识库搜索结果
export interface KnowledgeSearchResult {
  items: KnowledgeItem[]
  total: number
  page: number
  size: number
  totalPages: number
  searchTime: number
  suggestions?: string[]
}

// 知识库统计
export interface KnowledgeStats {
  totalItems: number
  totalCategories: number
  totalViews: number
  totalLikes: number
  recentItems: KnowledgeItem[]
  popularItems: KnowledgeItem[]
  categoryStats: Array<{
    category: string
    count: number
    percentage: number
  }>
}

// ==================== 社区相关类型 ====================

// 帖子
export interface Post {
  id: number
  title: string
  content: string
  summary?: string
  category: string
  tags: string[]
  author: {
    id: number
    username: string
    name?: string
    avatar?: string
    vipLevel?: number
  }
  images?: string[]
  views: number
  likes: number
  comments: number
  shares: number
  isTop: boolean
  isHot: boolean
  status: 'published' | 'pending' | 'rejected' | 'hidden'
  createTime: string
  updateTime: string
}

// 帖子列表响应
export interface PostListResponse {
  posts: Post[]
  total: number
  page: number
  size: number
  totalPages: number
  categories: string[]
  hotTags: string[]
}

// 帖子详情响应
export interface PostDetailResponse {
  post: Post
  comments: Comment[]
  relatedPosts: Post[]
  isLiked: boolean
  isCollected: boolean
}

// 创建帖子请求
export interface CreatePostRequest {
  title: string
  content: string
  category: string
  tags: string[]
  images?: string[]
}

// 创建帖子响应
export interface PostCreateResponse {
  success: boolean
  message: string
  postId?: number
  post?: Post
}

// 评论
export interface Comment {
  id: number
  content: string
  author: {
    id: number
    username: string
    name?: string
    avatar?: string
    vipLevel?: number
  }
  parentId?: number
  replies?: Comment[]
  likes: number
  isLiked: boolean
  createTime: string
  updateTime: string
}

// 评论列表响应
export interface CommentListResponse {
  comments: Comment[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 添加评论请求
export interface AddCommentRequest {
  content: string
  parentId?: number
}

// 创建评论响应
export interface CommentCreateResponse {
  success: boolean
  message: string
  commentId?: number
  comment?: Comment
}

// ==================== 图像识别相关类型 ====================

// 识别请求
export interface RecognitionRequest {
  image: File | string
  type?: 'single' | 'batch'
  options?: {
    confidence?: number
    maxResults?: number
    includeDetails?: boolean
  }
}

// 识别结果
export interface RecognitionResult {
  id: string
  image: string
  results: Array<{
    label: string
    confidence: number
    bbox?: {
      x: number
      y: number
      width: number
      height: number
    }
  }>
  processTime: number
  status: 'success' | 'failed' | 'processing'
  createTime: string
}

// 批量识别任务
export interface BatchRecognitionTask {
  id: string
  name: string
  description?: string
  images: string[]
  results: RecognitionResult[]
  progress: number
  status: 'pending' | 'processing' | 'completed' | 'failed'
  createTime: string
  updateTime: string
  completedTime?: string
}

// ==================== VIP相关类型 ====================

// VIP套餐
export interface VipPackage {
  id: string
  name: string
  level: number
  price: number
  duration: number // 天数
  features: string[]
  limits: {
    dailyRecognitions: number
    batchTasks: number
    apiCalls: number
    storage: number // GB
  }
  popular?: boolean
}

// VIP订单
export interface VipOrder {
  id: string
  packageId: string
  packageName: string
  price: number
  duration: number
  status: 'pending' | 'paid' | 'active' | 'expired' | 'cancelled'
  createTime: string
  payTime?: string
  activeTime?: string
  expireTime?: string
}

// ==================== 文件上传相关类型 ====================

// 文件上传结果
export interface FileUploadResult {
  id: string
  originalName: string
  fileName: string
  url: string
  size: number
  contentType: string
  uploadTime: string
}

// 文件信息
export interface FileInfo {
  id: string
  name: string
  size: number
  contentType: string
  lastModified: number
}

// 文件上传配置
export interface FileUploadConfig {
  maxSize?: number // 最大文件大小（字节）
  allowedTypes?: string[] // 允许的文件类型
  multiple?: boolean // 是否允许多文件上传
}

// ==================== 系统设置相关类型 ====================

// 系统设置
export interface SystemSettings {
  name: string
  description: string
  version: string
  email: string
  logo?: string
  favicon?: string
}

// OAuth设置
export interface OAuthSettings {
  github: {
    enabled: boolean
    clientId: string
    clientSecret: string
    callbackUrl: string
  }
  gitee: {
    enabled: boolean
    clientId: string
    clientSecret: string
    callbackUrl: string
  }
}

// 安全设置
export interface SecuritySettings {
  loginTimeout: number
  passwordPolicy: 'low' | 'medium' | 'high'
  twoFactorAuth: boolean
  captchaEnabled: boolean
  ipWhitelist: boolean
}

// 数据设置
export interface DataSettings {
  backupFrequency: 'daily' | 'weekly' | 'monthly'
  logRetentionDays: number
  backupRetention: number
  logLevel: 'error' | 'warn' | 'info' | 'debug'
}
