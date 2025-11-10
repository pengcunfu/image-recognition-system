<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select
          v-model:value="filterLevel"
          placeholder="筛选等级"
          style="width: 120px;"
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
          style="width: 120px;"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="active">生效中</a-select-option>
          <a-select-option value="expired">已过期</a-select-option>
          <a-select-option value="suspended">已暂停</a-select-option>
        </a-select>
        
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索用户名或邮箱"
          style="width: 300px;"
          @pressEnter="handleSearch"
        />
        
        <a-button type="primary" @click="handleSearch">
          <template #icon>
            <i class="fas fa-search"></i>
          </template>
          搜索
        </a-button>
        
        <a-button @click="handleReset">
          <template #icon>
            <i class="fas fa-redo"></i>
          </template>
          重置
        </a-button>
      </div>
    </a-card>
    
    <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '24px' }">
      <a-table 
        :columns="vipColumns" 
        :data-source="filteredVips"
        :pagination="pagination"
        :loading="tableLoading"
        row-key="id"
        @change="handleTableChange"
        :scroll="{ x: 1200 }"
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
              <a-button type="link" size="small" @click="viewVipDetail(record)">
                查看
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item key="extend">
                      延期
                    </a-menu-item>
                    <a-menu-item key="upgrade">
                      升级
                    </a-menu-item>
                    <a-menu-item key="downgrade">
                      降级
                    </a-menu-item>
                    <a-menu-item key="suspend" v-if="record.status === 'active'">
                      暂停
                    </a-menu-item>
                    <a-menu-item key="resume" v-if="record.status === 'suspended'">
                      恢复
                    </a-menu-item>
                    <a-menu-item key="reset">
                      重置用量
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger key="revoke">
                      撤销VIP
                    </a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  更多 <i class="fas fa-chevron-down"></i>
                </a-button>
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
      :width="600"
      placement="right"
      :bodyStyle="{ padding: '24px' }"
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
      :width="500"
      @ok="handleExtend"
      @cancel="extendModalVisible = false"
      :bodyStyle="{ padding: '24px' }"
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
import { AdminAPI } from '@/api/admin'
import FileAPI from '@/api/file'
import type { UserInfo } from '@/api/types'

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
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

// 所有VIP用户数据(用于前端筛选)
const allVipUsers = ref<any[]>([])

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
    
    // 注意: status 筛选在前端进行,因为 VIP 状态是计算得出的
    // 当有状态筛选时,获取大量数据以便前端筛选
    const pageSize = filterStatus.value ? 1000 : pagination.pageSize
    
    const response = await AdminAPI.getVipUsers({
      page: 1,
      size: pageSize,
      keyword: searchKeyword.value || undefined,
      level: filterLevel.value ? Number(filterLevel.value) : undefined
      // 不传递 status 参数,在前端通过 filteredVips 计算属性筛选
    })
    
    console.log('VIP用户响应:', response)
    
    allVipUsers.value = response.users.map((user: UserInfo) => ({
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
    
    // 如果没有状态筛选,使用后端分页
    if (!filterStatus.value) {
      vipUsers.value = allVipUsers.value
      pagination.total = response.total
      pagination.current = response.page
    }
    
    console.log('处理后的VIP用户数据:', allVipUsers.value)
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
    const response = await AdminAPI.getVipStats()
    Object.assign(vipStats, response)
  } catch (error) {
    console.error('加载VIP统计失败:', error)
  }
}

// 获取VIP状态
function getVipStatus(user: UserInfo) {
  if (user.status === 2) return 'suspended' // BANNED
  if (user.status === 0) return 'suspended' // INACTIVE
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

// 过滤后的VIP用户列表（等级筛选由后端处理，状态筛选在前端进行）
const filteredVips = computed(() => {
  let result = allVipUsers.value
  
  // 前端筛选 VIP 状态（因为状态是根据多个字段计算出来的）
  if (filterStatus.value) {
    result = result.filter(vip => vip.status === filterStatus.value)
    
    // 更新分页总数
    pagination.total = result.length
    
    // 前端分页
    const start = (pagination.current - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    return result.slice(start, end)
  }
  
  // 没有状态筛选时,直接使用后端分页的数据
  return result
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
    await AdminAPI.extendVip(selectedVip.value.id, {
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
        await AdminAPI.upgradeVip(vip.id, {
          newLevel: vip.vipLevel + 1,
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
        await AdminAPI.downgradeVip(vip.id, {
          newLevel: vip.vipLevel - 1,
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
        await AdminAPI.toggleVipStatus(vip.id, {
          status: 'suspend',
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
        await AdminAPI.toggleVipStatus(vip.id, {
          status: 'resume',
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
        await AdminAPI.resetVipUsage(vip.id, {
          resetType: 'all',
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
        await AdminAPI.revokeVip(vip.id, {
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

// 处理重置
function handleReset() {
  filterLevel.value = ''
  filterStatus.value = ''
  searchKeyword.value = ''
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

