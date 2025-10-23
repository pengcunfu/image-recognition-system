<template>
  <a-dropdown :trigger="['click']" placement="bottomRight">
    <a-badge :count="unreadCount" :overflow-count="99">
      <a-button type="text" :style="{ fontSize: '20px', padding: '4px 12px' }">
        <i class="fas fa-bell"></i>
      </a-button>
    </a-badge>

    <template #overlay>
      <div :style="{ 
        width: '380px', 
        maxHeight: '500px', 
        background: 'white', 
        borderRadius: '8px', 
        boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
        overflow: 'hidden'
      }">
        <!-- 标题栏 -->
        <div :style="{ 
          padding: '16px', 
          borderBottom: '1px solid #f0f0f0',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }">
          <span :style="{ fontSize: '16px', fontWeight: '600' }">通知中心</span>
          <div :style="{ display: 'flex', gap: '8px' }">
            <a-button 
              v-if="unreadCount > 0"
              type="link" 
              size="small"
              @click="handleMarkAllAsRead"
            >
              全部已读
            </a-button>
            <a-button 
              type="link" 
              size="small"
              @click="goToNotificationPage"
            >
              查看全部
            </a-button>
          </div>
        </div>

        <!-- 通知列表 -->
        <div :style="{ maxHeight: '400px', overflowY: 'auto' }">
          <a-spin :spinning="loading">
            <a-empty v-if="notifications.length === 0" description="暂无通知" />
            <div v-else>
              <div
                v-for="notification in notifications"
                :key="notification.id"
                :style="{
                  padding: '12px 16px',
                  borderBottom: '1px solid #f0f0f0',
                  cursor: 'pointer',
                  background: notification.isRead === 0 ? '#f6f9ff' : 'white',
                  transition: 'background 0.3s'
                }"
                @click="handleNotificationClick(notification)"
                @mouseenter="(e) => ((e.currentTarget as HTMLElement).style.background = '#f0f0f0')"
                @mouseleave="(e) => ((e.currentTarget as HTMLElement).style.background = notification.isRead === 0 ? '#f6f9ff' : 'white')"
              >
                <div :style="{ display: 'flex', gap: '12px' }">
                  <!-- 头像 -->
                  <a-avatar :size="40" :src="notification.senderAvatar">
                    <template v-if="!notification.senderAvatar">
                      <i class="fas fa-bell"></i>
                    </template>
                  </a-avatar>

                  <!-- 内容 -->
                  <div :style="{ flex: 1, minWidth: 0 }">
                    <div :style="{ 
                      display: 'flex', 
                      justifyContent: 'space-between',
                      marginBottom: '4px'
                    }">
                      <span :style="{ 
                        fontSize: '14px', 
                        fontWeight: '600',
                        color: '#262626'
                      }">{{ notification.title }}</span>
                      <a-badge 
                        v-if="notification.isRead === 0" 
                        status="processing" 
                      />
                    </div>
                    
                    <div :style="{ 
                      fontSize: '13px', 
                      color: '#595959',
                      marginBottom: '4px',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }">
                      {{ notification.content }}
                    </div>
                    
                    <div :style="{ fontSize: '12px', color: '#8c8c8c' }">
                      {{ formatTime(notification.createdAt) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-spin>
        </div>
      </div>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import NotificationAPI, { type NotificationInfo } from '@/api/notification'

const router = useRouter()

const unreadCount = ref(0)
const notifications = ref<NotificationInfo[]>([])
const loading = ref(false)
let eventSource: EventSource | null = null

// 加载通知列表
async function loadNotifications() {
  try {
    loading.value = true
    const response = await NotificationAPI.getNotifications({
      page: 1,
      size: 10
    })
    notifications.value = response.data
  } catch (error) {
    console.error('加载通知列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载未读数量
async function loadUnreadCount() {
  try {
    const count = await NotificationAPI.getUnreadCount()
    unreadCount.value = count
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

// 处理通知点击
async function handleNotificationClick(notification: NotificationInfo) {
  // 标记为已读
  if (notification.isRead === 0) {
    try {
      await NotificationAPI.markAsRead(notification.id)
      notification.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 跳转到相关页面
  if (notification.linkUrl) {
    router.push(notification.linkUrl)
  }
}

// 全部标记为已读
async function handleMarkAllAsRead() {
  try {
    await NotificationAPI.markAllAsRead()
    notifications.value.forEach(n => n.isRead = 1)
    unreadCount.value = 0
    message.success('已全部标记为已读')
  } catch (error) {
    message.error('操作失败')
  }
}

// 跳转到通知页面
function goToNotificationPage() {
  router.push('/user/notifications')
}

// 格式化时间
function formatTime(timeStr: string): string {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
  }
}

// 连接SSE
function connectSSE() {
  try {
    eventSource = NotificationAPI.createSseConnection(
      (notification) => {
        // 收到新通知
        console.log('收到新通知:', notification)
        notifications.value.unshift(notification)
        if (notifications.value.length > 10) {
          notifications.value.pop()
        }
        unreadCount.value++
        
        // 显示通知提示
        message.info(`新通知: ${notification.title}`)
      },
      (count) => {
        // 未读数更新
        unreadCount.value = count
      },
      (error) => {
        console.error('SSE错误:', error)
        // 5秒后重连
        setTimeout(() => {
          if (eventSource?.readyState === EventSource.CLOSED) {
            connectSSE()
          }
        }, 5000)
      }
    )
  } catch (error) {
    console.error('连接SSE失败:', error)
  }
}

onMounted(() => {
  loadUnreadCount()
  loadNotifications()
  connectSSE()
})

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

