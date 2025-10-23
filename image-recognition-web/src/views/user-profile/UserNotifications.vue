<template>
  <div>
    <a-card title="我的通知">
      <template #extra>
        <a-space>
          <a-select
            v-model:value="filterType"
            :style="{ width: '120px' }"
            @change="loadNotifications"
          >
            <a-select-option :value="null">全部通知</a-select-option>
            <a-select-option :value="0">未读</a-select-option>
            <a-select-option :value="1">已读</a-select-option>
          </a-select>
          <a-button @click="handleMarkAllAsRead" :disabled="unreadCount === 0">
            全部标记为已读
          </a-button>
        </a-space>
      </template>

      <a-spin :spinning="loading">
        <a-empty v-if="notifications.length === 0" description="暂无通知" />
        
        <a-list v-else :data-source="notifications" item-layout="horizontal">
          <template #renderItem="{ item }">
            <a-list-item
              :style="{
                padding: '16px',
                background: item.isRead === 0 ? '#f6f9ff' : 'white',
                marginBottom: '8px',
                borderRadius: '8px',
                cursor: 'pointer',
                transition: 'all 0.3s'
              }"
              @click="handleNotificationClick(item)"
            >
              <template #actions>
                <a-button
                  v-if="item.isRead === 0"
                  type="link"
                  size="small"
                  @click.stop="markAsRead(item)"
                >
                  标记已读
                </a-button>
                <a-popconfirm
                  title="确定要删除这条通知吗？"
                  @confirm="deleteNotification(item.id)"
                >
                  <a-button type="link" size="small" danger @click.stop>
                    删除
                  </a-button>
                </a-popconfirm>
              </template>

              <a-list-item-meta>
                <template #avatar>
                  <a-avatar :size="48" :src="item.senderAvatar">
                    <template v-if="!item.senderAvatar">
                      <i class="fas fa-bell"></i>
                    </template>
                  </a-avatar>
                </template>

                <template #title>
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
                    <span :style="{ fontWeight: '600' }">{{ item.title }}</span>
                    <a-tag :color="getTypeColor(item.type)" size="small">
                      {{ item.typeName }}
                    </a-tag>
                    <a-badge v-if="item.isRead === 0" status="processing" text="未读" />
                  </div>
                </template>

                <template #description>
                  <div :style="{ marginTop: '8px' }">
                    <div :style="{ color: '#595959', marginBottom: '8px' }">
                      {{ item.content }}
                    </div>
                    <div :style="{ fontSize: '12px', color: '#8c8c8c' }">
                      <i class="fas fa-clock"></i>
                      {{ formatTime(item.createdAt) }}
                      <span v-if="item.senderName" :style="{ marginLeft: '16px' }">
                        <i class="fas fa-user"></i>
                        {{ item.senderName }}
                      </span>
                    </div>
                  </div>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>

        <!-- 分页 -->
        <div v-if="total > 0" :style="{ marginTop: '24px', textAlign: 'center' }">
          <a-pagination
            v-model:current="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :show-size-changer="true"
            :show-total="(t: number) => `共 ${t} 条`"
            @change="loadNotifications"
          />
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import NotificationAPI, { type NotificationInfo } from '@/api/notification'

const router = useRouter()

const loading = ref(false)
const notifications = ref<NotificationInfo[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterType = ref<number | null>(null)

const unreadCount = computed(() => {
  return notifications.value.filter(n => n.isRead === 0).length
})

// 加载通知列表
async function loadNotifications() {
  try {
    loading.value = true
    const response = await NotificationAPI.getNotifications({
      page: currentPage.value,
      size: pageSize.value,
      isRead: filterType.value !== null ? filterType.value : undefined
    })
    
    notifications.value = response.data
    total.value = response.total
  } catch (error) {
    console.error('加载通知列表失败:', error)
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 处理通知点击
async function handleNotificationClick(notification: NotificationInfo) {
  // 标记为已读
  if (notification.isRead === 0) {
    await markAsRead(notification)
  }

  // 跳转到相关页面
  if (notification.linkUrl) {
    router.push(notification.linkUrl)
  }
}

// 标记为已读
async function markAsRead(notification: NotificationInfo) {
  try {
    await NotificationAPI.markAsRead(notification.id)
    notification.isRead = 1
    message.success('已标记为已读')
  } catch (error) {
    message.error('操作失败')
  }
}

// 全部标记为已读
async function handleMarkAllAsRead() {
  try {
    await NotificationAPI.markAllAsRead()
    notifications.value.forEach(n => n.isRead = 1)
    message.success('已全部标记为已读')
  } catch (error) {
    message.error('操作失败')
  }
}

// 删除通知
async function deleteNotification(notificationId: number) {
  try {
    await NotificationAPI.deleteNotification(notificationId)
    notifications.value = notifications.value.filter(n => n.id !== notificationId)
    total.value--
    message.success('删除成功')
  } catch (error) {
    message.error('删除失败')
  }
}

// 获取类型颜色
function getTypeColor(type: number): string {
  const colors: Record<number, string> = {
    0: 'blue',     // 系统
    1: 'green',    // 评论
    2: 'red',      // 点赞
    3: 'orange',   // 收藏
    4: 'purple',   // 审核
    5: 'gold',     // VIP
    6: 'volcano',  // 举报
    7: 'cyan',     // 识别
    8: 'magenta'   // 回复
  }
  return colors[type] || 'default'
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
    return time.toLocaleDateString() + ' ' + time.toLocaleTimeString()
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

