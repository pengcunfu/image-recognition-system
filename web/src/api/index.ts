// 统一导出所有API模块
export { default as AuthAPI } from './auth'
export { default as UserAPI } from './user'
export { default as CommunityAPI } from './community'
export { default as CommentAPI } from './comments'
export { default as NotificationAPI } from './notification'
export { default as KnowledgeAPI } from './knowledge'
export { default as RecognitionAPI } from './recognition'
export { default as VipAPI } from './vip'
export { default as FileAPI } from './file'
export { default as AdminAPI } from './admin'

// 导出通用类型
export type * from './types'

// 导出各模块特定类型
export type * from './auth'
export type * from './user'
export type * from './community'
export type * from './comments'
export type * from './notification'
export type * from './knowledge'
export type * from './recognition'
export type * from './vip'
export type * from './admin'
