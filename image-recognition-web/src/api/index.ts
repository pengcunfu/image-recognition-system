// API模块统一导出

// 导出请求工具
export { default as request } from '@/utils/request'

// 导出类型定义
export * from './types'

// 导出API模块
export { default as AuthAPI } from './auth'
export { default as KnowledgeAPI } from './knowledge'
export { default as CommunityAPI } from './community'

// 导出具名API类
export { AuthAPI, KnowledgeAPI, CommunityAPI } from './auth'
export { AuthAPI, KnowledgeAPI, CommunityAPI } from './knowledge'
export { AuthAPI, KnowledgeAPI, CommunityAPI } from './community'

// 创建API实例对象，方便使用
const API = {
  auth: AuthAPI,
  knowledge: KnowledgeAPI,
  community: CommunityAPI
}

export default API

// 导出常用的API方法，方便直接调用
export const {
  // 认证相关
  login: authLogin,
  register: authRegister,
  logout: authLogout,
  validateToken,
  getCaptcha,
  forgotPassword,
  sendSmsCode,
  verifySmsCode
} = AuthAPI

export const {
  // 知识库相关
  getCategories: getKnowledgeCategories,
  getItems: getKnowledgeItems,
  getItemDetail: getKnowledgeDetail,
  searchItems: searchKnowledge,
  getPopularItems: getPopularKnowledge,
  getLatestItems: getLatestKnowledge,
  getStats: getKnowledgeStats
} = KnowledgeAPI

export const {
  // 社区相关
  getPosts: getCommunityPosts,
  getPostDetail: getCommunityPostDetail,
  createPost: createCommunityPost,
  getPostComments: getCommunityPostComments,
  addComment: addCommunityComment,
  likePost: likeCommunityPost,
  sharePost: shareCommunityPost
} = CommunityAPI
