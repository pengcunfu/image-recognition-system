import { get, put, del } from '@/utils/request'
import type {
  NotificationQueryParams,
  NotificationResponse,
  OperationResult
} from './types'

/**
 * 通知API模块
 */
export class NotificationAPI {
  /**
   * 获取消息通知
   */
  static getNotifications(params: NotificationQueryParams) {
    return get<NotificationResponse>('/api/v1/community/notifications', params)
  }

  /**
   * 标记通知为已读
   */
  static markNotificationAsRead(notificationId: number) {
    return put<OperationResult>(`/api/v1/community/notifications/${notificationId}/read`)
  }

  /**
   * 标记所有通知为已读
   */
  static markAllNotificationsAsRead() {
    return put<OperationResult>('/api/v1/community/notifications/read-all')
  }

  /**
   * 删除通知
   */
  static deleteNotification(notificationId: number) {
    return del<OperationResult>(`/api/v1/community/notifications/${notificationId}`)
  }
}

// 导出默认实例
export default NotificationAPI


