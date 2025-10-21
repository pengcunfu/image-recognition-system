import { get, post, del } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 通知信息
 */
export interface NotificationInfo {
  id: number
  userId: number
  type: string
  title: string
  content: string
  data?: any
  isRead: number
  createTime: string
}

/**
 * 通知查询参数
 */
export interface QueryNotificationParams {
  page?: number
  size?: number
  unreadOnly?: boolean
}

/**
 * 通知API模块
 */
export class NotificationAPI {
  /**
   * 获取通知列表
   */
  static getNotifications(params: QueryNotificationParams) {
    return get<PageResponse<NotificationInfo>>('/api/notifications', params)
  }

  /**
   * 获取未读通知数量
   */
  static getUnreadCount() {
    return get<number>('/api/notifications/unread-count')
  }

  /**
   * 标记通知为已读
   */
  static markAsRead(id: number) {
    return post<void>(`/api/notifications/${id}/read`)
  }

  /**
   * 标记所有通知为已读
   */
  static markAllAsRead() {
    return post<void>('/api/notifications/read-all')
  }

  /**
   * 删除通知
   */
  static deleteNotification(id: number) {
    return del<void>(`/api/notifications/${id}`)
  }
}

// 导出默认实例
export default NotificationAPI
