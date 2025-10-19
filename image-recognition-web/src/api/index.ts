// API模块统一导出

// 导出请求工具
export { default as request } from '@/utils/request'

// 导出类型定义
export * from './types'

// 导入API模块
import AuthAPI from './auth'
import KnowledgeAPI from './knowledge'
import CommunityAPI from './community'
import FileAPI from './file'

// 导出API模块
export { default as AuthAPI } from './auth'
export { default as KnowledgeAPI } from './knowledge'
export { default as CommunityAPI } from './community'
export { default as FileAPI } from './file'

// 创建API实例对象，方便使用
const API = {
  auth: AuthAPI,
  knowledge: KnowledgeAPI,
  community: CommunityAPI,
  file: FileAPI
}

export default API

// 导出常用的API方法，方便直接调用
export const {
  // 认证相关
  login: authLogin,
  register: authRegister,
  logout: authLogout,
  validateToken,
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

export const {
  // 文件相关
  uploadFile: uploadSingleFile,
  uploadFiles: uploadMultipleFiles,
  deleteFile: deleteUploadedFile,
  getFileInfo: getUploadedFileInfo,
  downloadFile: downloadUploadedFile,
  getFileUrl: getUploadedFileUrl,
  getPreviewUrl: getFilePreviewUrl,
  getDownloadUrl: getFileDownloadUrl
} = FileAPI
