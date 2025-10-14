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

// ==================== 用户管理相关类型 ====================

// 用户角色枚举
export enum UserRole {
  USER = 1,    // 普通用户
  VIP = 2,     // VIP用户
  ADMIN = 3    // 管理员
}

// 用户状态枚举
export enum UserStatus {
  ACTIVE = 1,      // 激活
  INACTIVE = 2,    // 未激活
  BANNED = 3,      // 封禁
  DELETED = 4      // 已删除
}

// 用户基本信息
export interface User {
  id: number
  username: string
  email: string
  phone?: string
  name?: string
  avatar?: string
  role: 'USER' | 'VIP' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  vipLevel?: number
  vipExpireTime?: string
  createTime: string
  updateTime: string
  lastLoginTime?: string
  lastLoginIp?: string
}

// 用户列表响应
export interface UserListResponse {
  users: User[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 用户统计信息
export interface UserStats {
  totalRecognitions: number
  totalUploads: number
  totalFavorites: number
  totalShares: number
  joinDays: number
  lastActiveTime: string
  monthlyUsage: {
    recognitions: number
    uploads: number
  }
  weeklyActivity: Array<{
    date: string
    count: number
  }>
}

// 用户设置
export interface UserSettings {
  emailNotifications: boolean
  smsNotifications: boolean
  theme: 'light' | 'dark' | 'auto'
  language: 'zh-CN' | 'en-US'
  timezone: string
  privacy: {
    showEmail: boolean
    showPhone: boolean
    allowSearch: boolean
  }
}

// 用户活动记录
export interface UserActivity {
  id: number
  type: 'LOGIN' | 'RECOGNITION' | 'UPLOAD' | 'FAVORITE' | 'SHARE' | 'COMMENT'
  description: string
  metadata?: Record<string, any>
  createTime: string
  ip?: string
  userAgent?: string
}

// 用户更新请求
export interface UserUpdateRequest {
  name?: string
  email?: string
  phone?: string
  avatar?: string
}

// 修改密码请求
export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
  confirmPassword?: string
}

// 管理员用户创建请求
export interface AdminUserCreateRequest {
  username: string
  email: string
  password: string
  name?: string
  phone?: string
  role: 'USER' | 'VIP' | 'ADMIN'
  status?: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  vipLevel?: number
}

// 管理员用户更新请求
export interface AdminUserUpdateRequest {
  username?: string
  email?: string
  name?: string
  phone?: string
  role?: 'USER' | 'VIP' | 'ADMIN'
  status?: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  vipLevel?: number
  vipExpireTime?: string
}

// 用户查询参数
export interface UserQueryParams {
  page?: number
  size?: number
  keyword?: string
  role?: number  // 使用枚举值：1=USER, 2=VIP, 3=ADMIN
  status?: number  // 使用枚举值：1=ACTIVE, 2=INACTIVE, 3=BANNED, 4=DELETED
  sortBy?: 'createTime' | 'lastLoginTime' | 'username'
  sortOrder?: 'asc' | 'desc'
}

// 批量用户操作请求
export interface BatchUserOperationRequest {
  userIds: number[]
  action: 'delete' | 'activate' | 'deactivate' | 'ban'
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
  emailCode: string
  newPassword: string
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

// 分类状态枚举
export enum CategoryStatus {
  ACTIVE = 1,      // 活跃
  INACTIVE = 2,    // 不活跃
  HIDDEN = 3       // 隐藏
}

// 知识分类
export interface KnowledgeCategory {
  id: number
  name: string
  key: string
  description?: string
  image?: string
  itemCount: number
  sortOrder: number
  status: number  // 使用CategoryStatus枚举值
  createTime: string
  updateTime: string
}

// 知识条目
export interface KnowledgeItem {
  id: number
  categoryId: number
  name: string
  key: string
  scientificName?: string
  description: string
  content?: string
  images?: string
  characteristics?: string
  habitat?: string
  lifespan?: string
  relatedItems?: string
  tags?: string
  viewCount: number
  likeCount: number
  favoriteCount: number
  shareCount: number
  difficulty: number
  sortOrder: number
  status: string
  authorId: number
  reviewerId?: number
  reviewTime?: string
  createTime: string
  updateTime: string
  
  // 前端显示用的计算属性
  title?: string
  category?: string
  author?: string
  views?: number
  likes?: number
  collections?: number
  shares?: number
}

// 知识库分页响应
export interface KnowledgePage {
  items: KnowledgeItem[]
  total: number
  pages: number
  current: number
  size: number
  keyword?: string
  category?: string
}

// 知识库搜索结果
export interface KnowledgeSearchResult {
  items: KnowledgeItem[]
  total: number
  page: number
  size: number
  pages: number
  keyword: string
  category?: string
}

// 知识库统计
export interface KnowledgeStats {
  totalCategories: number
  totalItems: number
  totalViews: number
  totalLikes: number
  totalFavorites: number
  monthlyGrowth: number
  averageDifficulty: number
}

// 知识创建响应
export interface KnowledgeCreateResponse {
  success: boolean
  message: string
  id?: number
}

// 知识操作结果
export interface KnowledgeOperationResult {
  itemId?: number
  success: boolean
  message: string
  operation?: string
}

// ==================== 社区相关类型 ====================

// 帖子状态枚举
export enum PostStatus {
  DRAFT = 1,        // 草稿
  PENDING = 2,      // 待审核
  PUBLISHED = 3,    // 已发布
  REJECTED = 4,     // 已拒绝
  HIDDEN = 5,       // 已隐藏
  DELETED = 6       // 已删除
}

// 排序方式枚举
export enum SortType {
  LATEST = 0,       // 最新
  HOT = 1,          // 最热
  TOP_FIRST = 2     // 置顶优先
}

// 帖子
export interface Post {
  id: number
  title: string
  content: string
  summary?: string
  category: string
  tags?: string  // 后端返回的是JSON字符串
  authorId: number
  authorName?: string
  authorAvatar?: string
  images?: string  // 后端返回的是JSON字符串
  viewCount: number
  likeCount: number
  commentCount: number
  shareCount: number
  isTop: boolean
  isFeatured: boolean
  status: string  // 'PUBLISHED' | 'PENDING' | 'REJECTED' | 'HIDDEN' | 'DRAFT' | 'DELETED'
  createTime: string
  updateTime: string
}

// 帖子列表响应
export interface PostListResponse {
  data: Post[]  // 后端返回的字段名是 data
  total: number
  current: number  // 后端使用 current 而不是 page
  size: number
  pages: number  // 后端使用 pages 而不是 totalPages
  category?: string
  sort?: string
}

// 帖子详情响应
export interface PostDetailResponse {
  post: Post
  recentComments?: Comment[]
  relatedPosts?: Post[]
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
