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

// 管理员仪表盘统计数据
export interface AdminDashboardStats {
  totalRecognitions: number
  totalUsers: number
  vipUsers: number
  totalPosts: number
  todayRecognitions: number
  knowledgeItems: number
  pendingPosts: number
  systemActivity: number
}

// 用户概览响应
export interface UsersOverviewResponse {
  totalUsers: number
  activeUsers: number
  newUsersToday: number
  newUsersThisMonth: number
  usersByRole: Record<string, number>
  usersByStatus: Record<string, number>
  recentRegistrations: User[]
}

// 用户登录历史参数
export interface UserLoginHistoryParams {
  page?: number
  size?: number
}

// 用户登录历史响应
export interface UserLoginHistoryResponse {
  records: Array<{
    id: number
    loginTime: string
    loginIp: string
    userAgent: string
    location?: string
    success: boolean
  }>
  total: number
  page: number
  size: number
}

// VIP用户列表参数
export interface VipUsersParams {
  page?: number
  size?: number
  keyword?: string
  level?: string
  status?: string
}

// VIP统计响应
export interface VipStatsResponse {
  total: number
  active: number
  expiring: number
  monthlyRevenue: number
}

// 延期VIP请求
export interface ExtendVipRequest {
  days: number
  reason?: string
}

// 升级VIP请求
export interface UpgradeVipRequest {
  level: number
  reason?: string
}

// 降级VIP请求
export interface DowngradeVipRequest {
  level: number
  reason?: string
}

// 切换VIP状态请求
export interface ToggleVipStatusRequest {
  action: 'suspend' | 'resume'
  reason?: string
}

// 撤销VIP请求
export interface RevokeVipRequest {
  reason?: string
}

// 重置VIP用量请求
export interface ResetVipUsageRequest {
  reason?: string
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

// 发送邮箱验证码请求
export interface SendEmailCodeRequest {
  email: string
  type: string
}

// 修改密码请求（认证模块）
export interface AuthChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

// 绑定手机号请求
export interface BindPhoneRequest {
  phone: string
  smsCode: string
}

// 绑定邮箱请求
export interface BindEmailRequest {
  email: string
  verifyCode: string
}

// OAuth URL响应
export interface OAuthUrlResponse {
  url: string
}

// OAuth绑定信息
export interface OAuthBinding {
  provider: string
  username: string
  email: string
  bindTime: string
  status: 'active' | 'inactive'
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

// 创建知识分类请求
export interface CreateKnowledgeCategoryRequest {
  name: string
  key: string
  description?: string
  image?: string
  sortOrder?: number
}

// 更新知识分类请求
export interface UpdateKnowledgeCategoryRequest {
  name?: string
  description?: string
  image?: string
  sortOrder?: number
  status?: number
}

// 获取知识条目列表参数
export interface GetKnowledgeItemsParams {
  category?: string
  page?: number
  size?: number
  keyword?: string
}

// 创建知识条目请求
export interface CreateKnowledgeItemRequest {
  name: string
  categoryId: number
  scientificName?: string
  description: string
  content: string
  images?: string
  characteristics?: string
  habitat?: string
  lifespan?: string
  relatedItems?: string
  tags?: string
  difficulty?: number
  sortOrder?: number
  status?: string
}

// 更新知识条目请求
export interface UpdateKnowledgeItemRequest {
  name?: string
  categoryId?: number
  scientificName?: string
  description?: string
  content?: string
  images?: string
  characteristics?: string
  habitat?: string
  lifespan?: string
  relatedItems?: string
  tags?: string
  difficulty?: number
  sortOrder?: number
  status?: string
}

// 搜索知识条目参数
export interface SearchKnowledgeItemsParams {
  keyword: string
  page?: number
  size?: number
}

// 高级搜索知识条目参数
export interface AdvancedSearchKnowledgeParams {
  keyword: string
  page?: number
  size?: number
  category?: string
}

// 提交知识条目反馈请求
export interface SubmitKnowledgeFeedbackRequest {
  rating: number
  comment?: string
  helpful: boolean
}

// 知识条目评论响应
export interface KnowledgeItemCommentsResponse {
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
}

// 学习进度响应
export interface LearningProgressResponse {
  totalItems: number
  learnedItems: number
  progress: number
  categories: Array<{
    category: string
    total: number
    learned: number
    progress: number
  }>
}

// 学习统计响应
export interface LearningStatsResponse {
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

// 获取帖子列表参数
export interface GetPostsParams {
  page?: number
  size?: number
  category?: string
  sort?: 'latest' | 'hot' | 'top'
}

// 获取帖子评论参数
export interface GetPostCommentsParams {
  page?: number
  size?: number
}

// 搜索帖子参数
export interface SearchPostsParams {
  keyword: string
  page?: number
  size?: number
  category?: string
  author?: string
  dateRange?: [string, string]
}

// 热门话题项
export interface HotTopic {
  tag: string
  count: number
  trend: 'up' | 'down' | 'stable'
}

// 社区统计响应
export interface CommunityStatsResponse {
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
}

// 用户社区统计响应
export interface UserCommunityStatsResponse {
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
}

// 社区分类
export interface CommunityCategory {
  id: string
  name: string
  description: string
  icon?: string
  postCount: number
  color?: string
}

// 通知查询参数
export interface NotificationQueryParams {
  page?: number
  size?: number
  type?: 'like' | 'comment' | 'follow' | 'mention' | 'system'
  unreadOnly?: boolean
}

// 通知响应
export interface NotificationResponse {
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
}

// @提及建议
export interface MentionSuggestion {
  id: number
  username: string
  name?: string
  avatar?: string
}

// 图片上传响应
export interface ImageUploadResponse {
  url: string
}

// 多图片上传响应
export interface MultiImageUploadResponse {
  urls: string[]
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
