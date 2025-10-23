import { get, put, del } from '@/utils/request'
import type { PageResponse } from './types'

/**
 * 通知信息
 */
export interface NotificationInfo {
  id: number
  userId: number
  type: number
  typeName: string
  title: string
  content: string
  data?: string
  linkUrl?: string
  senderId?: number
  senderName?: string
  senderAvatar?: string
  isRead: number
  readTime?: string
  createdAt: string
}

/**
 * 通知API模块
 */
export class NotificationAPI {
  /**
   * 获取未读通知数量
   */
  static getUnreadCount() {
    return get<number>('/api/notifications/unread-count')
  }

  /**
   * 获取通知列表
   */
  static getNotifications(params: {
    page?: number
    size?: number
    isRead?: number
  }) {
    return get<PageResponse<NotificationInfo>>('/api/notifications', params)
  }

  /**
   * 标记为已读
   */
  static markAsRead(notificationId: number) {
    return put<void>(`/api/notifications/${notificationId}/read`)
  }

  /**
   * 全部标记为已读
   */
  static markAllAsRead() {
    return put<void>('/api/notifications/read-all')
  }

  /**
   * 删除通知
   */
  static deleteNotification(notificationId: number) {
    return del<void>(`/api/notifications/${notificationId}`)
  }

  /**
   * 创建SSE连接
   */
  static createSseConnection(
    onMessage: (notification: NotificationInfo) => void,
    onUnreadCount: (count: number) => void,
    onError?: (error: Event) => void
  ): EventSource {
    const token = localStorage.getItem('token')
    const url = `/api/notifications/stream?token=${encodeURIComponent(token || '')}`
    
    const eventSource = new EventSource(url)

    // 连接成功
    eventSource.addEventListener('connected', (e) => {
      console.log('SSE连接成功:', e.data)
    })

    // 接收通知
    eventSource.addEventListener('notification', (e) => {
      try {
        const notification = JSON.parse(e.data) as NotificationInfo
        onMessage(notification)
      } catch (error) {
        console.error('解析通知数据失败:', error)
      }
    })

    // 接收未读数更新
    eventSource.addEventListener('unread_count', (e) => {
      try {
        const count = parseInt(e.data)
        onUnreadCount(count)
      } catch (error) {
        console.error('解析未读数失败:', error)
      }
    })

    // 错误处理
    eventSource.onerror = (error) => {
      console.error('SSE连接错误:', error)
      if (onError) {
        onError(error)
      }
    }

    return eventSource
  }
}

// 导出默认实例
export default NotificationAPI
