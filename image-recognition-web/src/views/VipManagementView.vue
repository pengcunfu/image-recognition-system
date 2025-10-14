<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">VIP管理</h1>
      <a-space>
        <a-select
          v-model:value="filterLevel"
          placeholder="筛选等级"
          style="width: 120px"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部等级</a-select-option>
          <a-select-option value="1">VIP 1</a-select-option>
          <a-select-option value="2">VIP 2</a-select-option>
          <a-select-option value="3">VIP 3</a-select-option>
        </a-select>
        <a-select
          v-model:value="filterStatus"
          placeholder="筛选状态"
          style="width: 120px"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="active">生效中</a-select-option>
          <a-select-option value="expired">已过期</a-select-option>
          <a-select-option value="suspended">已暂停</a-select-option>
        </a-select>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索用户名或邮箱"
          style="width: 300px"
          @search="handleSearch"
        />
      </a-space>
    </div>
    
    <!-- VIP统计卡片 -->
    <a-row :gutter="[16, 16]" class="stats-row">
      <a-col :xs="12" :sm="6" :lg="6">
        <a-card class="stat-card compact-card vip-total">
          <a-statistic
            title="VIP总数"
            :value="vipStats.total"
            suffix="人"
            :value-style="{ color: '#faad14', fontSize: '20px' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6" :lg="6">
        <a-card class="stat-card compact-card vip-active">
          <a-statistic
            title="生效中"
            :value="vipStats.active"
            suffix="人"
            :value-style="{ color: '#52c41a', fontSize: '20px' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6" :lg="6">
        <a-card class="stat-card compact-card vip-expired">
          <a-statistic
            title="即将过期"
            :value="vipStats.expiring"
            suffix="人"
            :value-style="{ color: '#fa541c', fontSize: '20px' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6" :lg="6">
        <a-card class="stat-card compact-card vip-revenue">
          <a-statistic
            title="本月收入"
            :value="vipStats.monthlyRevenue"
            prefix="¥"
            :value-style="{ color: '#1890ff', fontSize: '20px' }"
          />
        </a-card>
      </a-col>
    </a-row>
    
    <a-card class="table-card">
      <a-table 
        :columns="vipColumns" 
        :data-source="filteredVips"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="user-info">
              <a-avatar :src="FileAPI.getImageUrl(record.avatar)" size="small">
                {{ record.username.charAt(0) }}
              </a-avatar>
              <div class="user-details">
                <div class="username">{{ record.username }}</div>
                <div class="email">{{ record.email }}</div>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'vipLevel'">
            <a-tag :color="getVipLevelColor(record.vipLevel)" class="vip-level-tag">
              <i class="fas fa-crown"></i>
              VIP {{ record.vipLevel }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="getStatusColor(record.status)"
              class="status-tag"
            >
              <i :class="getStatusIcon(record.status)"></i>
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'expireTime'">
            <div class="expire-time">
              <div>{{ record.expireTime }}</div>
              <div v-if="record.status === 'active'" class="days-left">
                <span :class="getDaysLeftClass(record.daysLeft)">
                  剩余{{ record.daysLeft }}天
                </span>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'usage'">
            <div class="usage-info">
              <div class="usage-item">
                <span class="label">识别:</span>
                <span class="value">{{ record.usage.recognitions }}/{{ record.limits.recognitions }}</span>
              </div>
              <div class="usage-item">
                <span class="label">批量:</span>
                <span class="value">{{ record.usage.batchTasks }}/{{ record.limits.batchTasks }}</span>
              </div>
              <a-progress 
                :percent="Math.round((record.usage.recognitions / record.limits.recognitions) * 100)" 
                size="small"
                :show-info="false"
              />
            </div>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="viewVipDetail(record)">
                详情
              </a-button>
              <a-dropdown>
                <a-button size="small">
                  操作 <i class="fas fa-chevron-down"></i>
                </a-button>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item key="extend">
                      <i class="fas fa-calendar-plus"></i> 延期
                    </a-menu-item>
                    <a-menu-item key="upgrade">
                      <i class="fas fa-arrow-up"></i> 升级
                    </a-menu-item>
                    <a-menu-item key="downgrade">
                      <i class="fas fa-arrow-down"></i> 降级
                    </a-menu-item>
                    <a-menu-item key="suspend" v-if="record.status === 'active'">
                      <i class="fas fa-pause"></i> 暂停
                    </a-menu-item>
                    <a-menu-item key="resume" v-if="record.status === 'suspended'">
                      <i class="fas fa-play"></i> 恢复
                    </a-menu-item>
                    <a-menu-item key="reset">
                      <i class="fas fa-refresh"></i> 重置用量
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="revoke" class="danger-item">
                      <i class="fas fa-ban"></i> 撤销VIP
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- VIP详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="VIP用户详情"
      width="60%"
      class="vip-drawer"
    >
      <div v-if="selectedVip" class="vip-detail">
        <div class="vip-header">
          <a-avatar :src="FileAPI.getImageUrl(selectedVip.avatar)" size="large">
            {{ selectedVip.username.charAt(0) }}
          </a-avatar>
          <div class="vip-info">
            <h2>{{ selectedVip.username }}</h2>
            <p>{{ selectedVip.email }}</p>
            <div class="vip-badges">
              <a-tag :color="getVipLevelColor(selectedVip.vipLevel)" class="vip-badge">
                <i class="fas fa-crown"></i>
                VIP {{ selectedVip.vipLevel }}
              </a-tag>
              <a-tag :color="getStatusColor(selectedVip.status)">
                {{ getStatusText(selectedVip.status) }}
              </a-tag>
            </div>
          </div>
        </div>
        
        <a-divider />
        
        <div class="vip-sections">
          <div class="section">
            <h3>VIP信息</h3>
            <a-descriptions :column="2" bordered>
              <a-descriptions-item label="VIP等级">
                VIP {{ selectedVip.vipLevel }}
              </a-descriptions-item>
              <a-descriptions-item label="开通时间">
                {{ selectedVip.startTime }}
              </a-descriptions-item>
              <a-descriptions-item label="到期时间">
                {{ selectedVip.expireTime }}
              </a-descriptions-item>
              <a-descriptions-item label="剩余天数">
                <span :class="getDaysLeftClass(selectedVip.daysLeft)">
                  {{ selectedVip.daysLeft }}天
                </span>
              </a-descriptions-item>
              <a-descriptions-item label="购买金额">
                ¥{{ selectedVip.purchaseAmount }}
              </a-descriptions-item>
              <a-descriptions-item label="续费次数">
                {{ selectedVip.renewalCount }}次
              </a-descriptions-item>
            </a-descriptions>
          </div>
          
          <div class="section">
            <h3>使用情况</h3>
            <a-row :gutter="16">
              <a-col :span="8">
                <a-card class="usage-card">
                  <a-statistic
                    title="图像识别"
                    :value="selectedVip.usage.recognitions"
                    :suffix="`/ ${selectedVip.limits.recognitions}`"
                  />
                  <a-progress 
                    :percent="Math.round((selectedVip.usage.recognitions / selectedVip.limits.recognitions) * 100)"
                    :stroke-color="getProgressColor((selectedVip.usage.recognitions / selectedVip.limits.recognitions) * 100)"
                  />
                </a-card>
              </a-col>
              <a-col :span="8">
                <a-card class="usage-card">
                  <a-statistic
                    title="批量任务"
                    :value="selectedVip.usage.batchTasks"
                    :suffix="`/ ${selectedVip.limits.batchTasks}`"
                  />
                  <a-progress 
                    :percent="Math.round((selectedVip.usage.batchTasks / selectedVip.limits.batchTasks) * 100)"
                    :stroke-color="getProgressColor((selectedVip.usage.batchTasks / selectedVip.limits.batchTasks) * 100)"
                  />
                </a-card>
              </a-col>
              <a-col :span="8">
                <a-card class="usage-card">
                  <a-statistic
                    title="API调用"
                    :value="selectedVip.usage.apiCalls"
                    :suffix="`/ ${selectedVip.limits.apiCalls}`"
                  />
                  <a-progress 
                    :percent="Math.round((selectedVip.usage.apiCalls / selectedVip.limits.apiCalls) * 100)"
                    :stroke-color="getProgressColor((selectedVip.usage.apiCalls / selectedVip.limits.apiCalls) * 100)"
                  />
                </a-card>
              </a-col>
            </a-row>
          </div>
          
          <div class="section">
            <h3>购买记录</h3>
            <a-table 
              :columns="purchaseColumns" 
              :data-source="selectedVip.purchaseHistory"
              :pagination="false"
              size="small"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'type'">
                  <a-tag :color="record.type === '新购' ? 'green' : 'blue'">
                    {{ record.type }}
                  </a-tag>
                </template>
                <template v-else-if="column.key === 'amount'">
                  ¥{{ record.amount }}
                </template>
              </template>
            </a-table>
          </div>
        </div>
        
        <div class="vip-actions">
          <a-space>
            <a-button type="primary" @click="extendVip(selectedVip)">
              延期VIP
            </a-button>
            <a-button @click="upgradeVip(selectedVip)">
              升级VIP
            </a-button>
            <a-button @click="resetUsage(selectedVip)">
              重置用量
            </a-button>
            <a-button danger @click="suspendVip(selectedVip)">
              {{ selectedVip.status === 'active' ? '暂停VIP' : '恢复VIP' }}
            </a-button>
          </a-space>
        </div>
      </div>
    </a-drawer>
    
    <!-- 延期VIP模态框 -->
    <a-modal
      v-model:open="extendModalVisible"
      title="延期VIP"
      @ok="handleExtend"
      @cancel="extendModalVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="延期天数">
          <a-input-number 
            v-model:value="extendDays" 
            :min="1" 
            :max="365"
            placeholder="请输入延期天数"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="延期原因">
          <a-textarea 
            v-model:value="extendReason" 
            placeholder="请输入延期原因"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { UserAPI } from '@/api/user'
import FileAPI from '@/api/file'
import type { User } from '@/api/types'

// 响应式数据
const loading = ref(false)
const drawerVisible = ref(false)
const extendModalVisible = ref(false)
const selectedVip = ref<any>(null)
const filterLevel = ref('')
const filterStatus = ref('')
const searchKeyword = ref('')
const extendDays = ref(30)
const extendReason = ref('')

// VIP统计数据
const vipStats = reactive({
  total: 1234,
  active: 1089,
  expiring: 45,
  monthlyRevenue: 156789
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// VIP表格列定义
const vipColumns = [
  { 
    title: '用户信息', 
    key: 'user',
    width: 200
  },
  { 
    title: 'VIP等级', 
    key: 'vipLevel',
    width: 120
  },
  { 
    title: '状态', 
    key: 'status',
    width: 120
  },
  { 
    title: '到期时间', 
    key: 'expireTime',
    width: 180
  },
  { 
    title: '使用情况', 
    key: 'usage',
    width: 200
  },
  { 
    title: '操作', 
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

// 购买记录表格列
const purchaseColumns = [
  { title: '时间', dataIndex: 'time', key: 'time' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '等级', dataIndex: 'level', key: 'level' },
  { title: '时长', dataIndex: 'duration', key: 'duration' },
  { title: '金额', dataIndex: 'amount', key: 'amount' }
]

// VIP用户数据
const vipUsers = ref<any[]>([])

// 加载VIP用户数据
async function loadVipUsers() {
  try {
    loading.value = true
    
    console.log('VIP用户查询参数:', {
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value,
      level: filterLevel.value,
      status: filterStatus.value
    })
    
    const response = await UserAPI.getVipUsers({
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
      level: filterLevel.value || undefined,
      status: filterStatus.value || undefined
    })
    
    console.log('VIP用户响应:', response)
    
    vipUsers.value = response.data.users.map(user => ({
      id: user.id,
      username: user.username,
      email: user.email,
      avatar: user.avatar,
      vipLevel: user.vipLevel || 0,
      status: getVipStatus(user),
      startTime: formatDateTime(user.createTime),
      expireTime: user.vipExpireTime ? formatDateTime(user.vipExpireTime) : '永久',
      daysLeft: calculateDaysLeft(user.vipExpireTime),
      purchaseAmount: 0, // 需要从订单系统获取
      renewalCount: 0, // 需要从订单系统获取
      usage: {
        recognitions: Math.floor(Math.random() * 1000), // 模拟数据
        batchTasks: Math.floor(Math.random() * 10),
        apiCalls: Math.floor(Math.random() * 500)
      },
      limits: getVipLimits(user.vipLevel || 0),
      purchaseHistory: [] // 需要从订单系统获取
    }))
    
    pagination.total = response.data.total
    pagination.current = response.data.page
    
    console.log('处理后的VIP用户数据:', vipUsers.value)
  } catch (error: any) {
    console.error('加载VIP用户失败:', error)
    message.error(error.message || '加载VIP用户失败')
  } finally {
    loading.value = false
  }
}

// 加载VIP统计数据
async function loadVipStats() {
  try {
    const response = await UserAPI.getVipStats()
    Object.assign(vipStats, response.data)
  } catch (error) {
    console.error('加载VIP统计失败:', error)
  }
}

// 获取VIP状态
function getVipStatus(user: User) {
  if (user.status === 'BANNED') return 'suspended'
  if (user.status === 'INACTIVE') return 'suspended'
  if (!user.vipExpireTime) return 'active'
  
  const expireDate = new Date(user.vipExpireTime)
  const now = new Date()
  
  if (expireDate < now) return 'expired'
  return 'active'
}

// 计算剩余天数
function calculateDaysLeft(expireTime?: string) {
  if (!expireTime) return 999999 // 永久VIP
  
  const expireDate = new Date(expireTime)
  const now = new Date()
  const diffTime = expireDate.getTime() - now.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  return diffDays
}

// 获取VIP等级限制
function getVipLimits(level: number) {
  switch (level) {
    case 1:
      return { recognitions: 2000, batchTasks: 10, apiCalls: 1000 }
    case 2:
      return { recognitions: 5000, batchTasks: 20, apiCalls: 2000 }
    case 3:
      return { recognitions: 10000, batchTasks: 50, apiCalls: 5000 }
    default:
      return { recognitions: 1000, batchTasks: 5, apiCalls: 500 }
  }
}

// 格式化日期时间
function formatDateTime(dateTime?: string) {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 过滤后的VIP用户列表（直接使用API返回的数据，筛选由后端处理）
const filteredVips = computed(() => {
  return vipUsers.value
})

// 获取VIP等级颜色
function getVipLevelColor(level: number) {
  switch (level) {
    case 1: return 'orange'
    case 2: return 'gold'
    case 3: return 'red'
    default: return 'default'
  }
}

// 获取状态颜色
function getStatusColor(status: string) {
  switch (status) {
    case 'active': return 'success'
    case 'expired': return 'error'
    case 'suspended': return 'warning'
    default: return 'default'
  }
}

// 获取状态图标
function getStatusIcon(status: string) {
  switch (status) {
    case 'active': return 'fas fa-check-circle'
    case 'expired': return 'fas fa-times-circle'
    case 'suspended': return 'fas fa-pause-circle'
    default: return 'fas fa-question-circle'
  }
}

// 获取状态文本
function getStatusText(status: string) {
  switch (status) {
    case 'active': return '生效中'
    case 'expired': return '已过期'
    case 'suspended': return '已暂停'
    default: return '未知'
  }
}

// 获取剩余天数样式类
function getDaysLeftClass(days: number) {
  if (days < 0) return 'expired'
  if (days <= 7) return 'warning'
  if (days <= 30) return 'attention'
  return 'normal'
}

// 获取进度条颜色
function getProgressColor(percent: number) {
  if (percent >= 90) return '#ff4d4f'
  if (percent >= 70) return '#faad14'
  return '#52c41a'
}


// 查看VIP详情
function viewVipDetail(vip: any) {
  selectedVip.value = vip
  drawerVisible.value = true
}

// 处理操作
function handleAction(action: string, vip: any) {
  switch (action) {
    case 'extend':
      extendVip(vip)
      break
    case 'upgrade':
      upgradeVip(vip)
      break
    case 'downgrade':
      downgradeVip(vip)
      break
    case 'suspend':
      suspendVip(vip)
      break
    case 'resume':
      resumeVip(vip)
      break
    case 'reset':
      resetUsage(vip)
      break
    case 'revoke':
      revokeVip(vip)
      break
  }
}

// 延期VIP
function extendVip(vip: any) {
  selectedVip.value = vip
  extendModalVisible.value = true
}

// 处理延期
async function handleExtend() {
  if (!extendDays.value || extendDays.value <= 0) {
    message.error('请输入有效的延期天数')
    return
  }
  
  try {
    await UserAPI.extendVip(selectedVip.value.id, {
      days: extendDays.value,
      reason: extendReason.value
    })
    message.success(`已为用户 ${selectedVip.value.username} 延期 ${extendDays.value} 天`)
    extendModalVisible.value = false
    extendDays.value = 30
    extendReason.value = ''
    loadVipUsers() // 重新加载数据
  } catch (error) {
    console.error('延期VIP失败:', error)
    message.error('延期VIP失败')
  }
}

// 升级VIP
async function upgradeVip(vip: any) {
  if (vip.vipLevel >= 3) {
    message.warning('已是最高等级VIP')
    return
  }
  
  Modal.confirm({
    title: '确认升级',
    content: `确定要将用户 ${vip.username} 的VIP等级从 VIP ${vip.vipLevel} 升级到 VIP ${vip.vipLevel + 1} 吗？`,
    async onOk() {
      try {
        await UserAPI.upgradeVip(vip.id, {
          level: vip.vipLevel + 1,
          reason: '管理员手动升级'
        })
        message.success('VIP等级升级成功')
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('升级VIP失败:', error)
        message.error('升级VIP失败')
      }
    }
  })
}

// 降级VIP
async function downgradeVip(vip: any) {
  if (vip.vipLevel <= 1) {
    message.warning('已是最低等级VIP')
    return
  }
  
  Modal.confirm({
    title: '确认降级',
    content: `确定要将用户 ${vip.username} 的VIP等级从 VIP ${vip.vipLevel} 降级到 VIP ${vip.vipLevel - 1} 吗？`,
    async onOk() {
      try {
        await UserAPI.downgradeVip(vip.id, {
          level: vip.vipLevel - 1,
          reason: '管理员手动降级'
        })
        message.success('VIP等级降级成功')
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('降级VIP失败:', error)
        message.error('降级VIP失败')
      }
    }
  })
}

// 暂停VIP
async function suspendVip(vip: any) {
  Modal.confirm({
    title: '确认暂停',
    content: `确定要暂停用户 ${vip.username} 的VIP服务吗？`,
    async onOk() {
      try {
        await UserAPI.toggleVipStatus(vip.id, {
          action: 'suspend',
          reason: '管理员手动暂停'
        })
        message.success('VIP服务已暂停')
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('暂停VIP失败:', error)
        message.error('暂停VIP失败')
      }
    }
  })
}

// 恢复VIP
async function resumeVip(vip: any) {
  Modal.confirm({
    title: '确认恢复',
    content: `确定要恢复用户 ${vip.username} 的VIP服务吗？`,
    async onOk() {
      try {
        await UserAPI.toggleVipStatus(vip.id, {
          action: 'resume',
          reason: '管理员手动恢复'
        })
        message.success('VIP服务已恢复')
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('恢复VIP失败:', error)
        message.error('恢复VIP失败')
      }
    }
  })
}

// 重置用量
async function resetUsage(vip: any) {
  Modal.confirm({
    title: '确认重置',
    content: `确定要重置用户 ${vip.username} 的使用量吗？`,
    async onOk() {
      try {
        await UserAPI.resetVipUsage(vip.id, {
          reason: '管理员手动重置'
        })
        message.success('使用量已重置')
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('重置用量失败:', error)
        message.error('重置用量失败')
      }
    }
  })
}

// 撤销VIP
async function revokeVip(vip: any) {
  Modal.confirm({
    title: '确认撤销',
    content: `确定要撤销用户 ${vip.username} 的VIP资格吗？此操作不可恢复！`,
    okType: 'danger',
    async onOk() {
      try {
        await UserAPI.revokeVip(vip.id, {
          reason: '管理员手动撤销'
        })
        message.success('VIP资格已撤销')
        drawerVisible.value = false
        loadVipUsers() // 重新加载数据
      } catch (error) {
        console.error('撤销VIP失败:', error)
        message.error('撤销VIP失败')
      }
    }
  })
}

// 处理筛选变化
function handleFilterChange() {
  pagination.current = 1
  loadVipUsers()
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
  loadVipUsers()
}

// 处理表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadVipUsers()
}

// 组件挂载
onMounted(() => {
  loadVipUsers()
  loadVipStats()
})
</script>

<style scoped>
.page {
  width: 100%;
}

.page-title {
  margin-bottom: 24px;
  color: #262626;
  font-size: 24px;
  font-weight: 600;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  transition: all 0.3s ease;
  border-radius: 8px;
}

.stat-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.compact-card {
  padding: 8px;
}

.compact-card :deep(.ant-card-body) {
  padding: 16px;
}

.compact-card :deep(.ant-statistic-title) {
  font-size: 12px;
  margin-bottom: 4px;
}

.compact-card :deep(.ant-statistic-content) {
  font-size: 18px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
}

.trend-up {
  color: #52c41a;
}

.trend-warning {
  color: #fa541c;
}

.table-card {
  margin-bottom: 24px;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 500;
  color: #262626;
}

.email {
  font-size: 12px;
  color: #666;
}

/* VIP等级标签 */
.vip-level-tag {
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.vip-level-tag i {
  font-size: 10px;
}

/* 状态标签 */
.status-tag {
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-tag i {
  font-size: 10px;
}

/* 到期时间 */
.expire-time {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.days-left {
  font-size: 12px;
}

.days-left .expired {
  color: #ff4d4f;
  font-weight: 600;
}

.days-left .warning {
  color: #fa541c;
  font-weight: 600;
}

.days-left .attention {
  color: #faad14;
}

.days-left .normal {
  color: #52c41a;
}

/* 使用情况 */
.usage-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.usage-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.usage-item .label {
  color: #666;
}

.usage-item .value {
  font-weight: 500;
}

/* 抽屉样式 */
.vip-drawer :deep(.ant-drawer-body) {
  padding: 24px;
}

.vip-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.vip-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.vip-info h2 {
  margin: 0 0 8px 0;
  color: #262626;
}

.vip-info p {
  margin: 0 0 12px 0;
  color: #666;
}

.vip-badges {
  display: flex;
  gap: 8px;
}

.vip-badge {
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.vip-badge i {
  font-size: 10px;
}

.vip-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.section h3 {
  margin-bottom: 16px;
  color: #262626;
  font-size: 18px;
}

.usage-card {
  text-align: center;
}

.vip-actions {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

/* 危险操作样式 */
:deep(.danger-item) {
  color: #ff4d4f !important;
}

:deep(.danger-item:hover) {
  background-color: #fff2f0 !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .stats-row .ant-col {
    margin-bottom: 16px;
  }
  
  .vip-drawer {
    width: 90% !important;
  }
  
  .vip-header {
    flex-direction: column;
    text-align: center;
  }
  
  .user-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>
