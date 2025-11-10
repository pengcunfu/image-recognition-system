<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select
          v-model:value="filterStatus"
          placeholder="筛选支付状态"
          style="width: 150px;"
          @change="handleFilterChange"
        >
          <a-select-option :value="undefined">全部状态</a-select-option>
          <a-select-option :value="0">待支付</a-select-option>
          <a-select-option :value="1">已支付</a-select-option>
          <a-select-option :value="2">已取消</a-select-option>
          <a-select-option :value="3">已退款</a-select-option>
        </a-select>
        
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索订单号"
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
        :columns="orderColumns" 
        :data-source="orders"
        :pagination="pagination"
        :loading="tableLoading"
        row-key="id"
        @change="handleTableChange"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'username'">
            <span>{{ record.username || '未知用户' }}</span>
          </template>
          <template v-else-if="column.key === 'planType'">
            <a-tag :color="getPlanColor(record.planType)">
              {{ getPlanText(record.planType) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'amount'">
            <span style="color: #f5222d; font-weight: bold;">¥{{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'paymentStatus'">
            <a-tag 
              :color="getStatusColor(record.paymentStatus)"
              class="status-tag"
            >
              {{ getStatusText(record.paymentStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'paymentTime'">
            <span>{{ record.paymentTime ? formatDateTime(record.paymentTime) : '-' }}</span>
          </template>
          <template v-else-if="column.key === 'createTime'">
            <span>{{ formatDateTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewOrderDetail(record)">
                查看详情
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu>
                    <a-menu-item 
                      v-if="record.paymentStatus === 0"
                      @click="updateStatus(record, 1)"
                    >
                      标记为已支付
                    </a-menu-item>
                    <a-menu-item 
                      v-if="record.paymentStatus === 0"
                      @click="updateStatus(record, 2)"
                    >
                      取消订单
                    </a-menu-item>
                    <a-menu-item 
                      v-if="record.paymentStatus === 1"
                      @click="updateStatus(record, 3)"
                    >
                      退款
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

    <!-- 订单详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="订单详情"
      :width="600"
      placement="right"
      :bodyStyle="{ padding: '24px' }"
    >
      <div v-if="selectedOrder" class="order-detail">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</a-descriptions-item>
          <a-descriptions-item label="用户">{{ selectedOrder.username || '未知用户' }}</a-descriptions-item>
          <a-descriptions-item label="用户ID">{{ selectedOrder.userId }}</a-descriptions-item>
          <a-descriptions-item label="VIP套餐">
            <a-tag :color="getPlanColor(selectedOrder.planType)">
              {{ getPlanText(selectedOrder.planType) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="订单金额">
            <span style="color: #f5222d; font-weight: bold; font-size: 16px;">¥{{ selectedOrder.amount }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="支付方式">
            {{ selectedOrder.paymentMethod || '未支付' }}
          </a-descriptions-item>
          <a-descriptions-item label="支付状态">
            <a-tag :color="getStatusColor(selectedOrder.paymentStatus)">
              {{ getStatusText(selectedOrder.paymentStatus) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="支付时间">
            {{ selectedOrder.paymentTime ? formatDateTime(selectedOrder.paymentTime) : '未支付' }}
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ formatDateTime(selectedOrder.createTime) }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { AdminAPI, type VipOrderInfo } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
const orders = ref<VipOrderInfo[]>([])
const selectedOrder = ref<VipOrderInfo | null>(null)
const drawerVisible = ref(false)
const filterStatus = ref<number | undefined>(undefined)
const searchKeyword = ref('')

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// 表格列定义
const orderColumns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 180 },
  { title: '用户', dataIndex: 'username', key: 'username', width: 120 },
  { title: 'VIP套餐', dataIndex: 'planType', key: 'planType', width: 120 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 100 },
  { title: '支付状态', dataIndex: 'paymentStatus', key: 'paymentStatus', width: 100 },
  { title: '支付时间', dataIndex: 'paymentTime', key: 'paymentTime', width: 160 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 150, fixed: 'right' }
]

// 初始化
onMounted(() => {
  loadOrders()
})

// 加载订单列表
async function loadOrders() {
  try {
    loading.value = true
    const response = await AdminAPI.getVipOrders({
      page: pagination.current,
      size: pagination.pageSize,
      paymentStatus: filterStatus.value,
      keyword: searchKeyword.value || undefined
    })
    
    orders.value = response.data || []
    pagination.total = response.total || 0
    pagination.current = response.page || 1
  } catch (error) {
    console.error('加载订单列表失败:', error)
    message.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
function handleFilterChange() {
  pagination.current = 1
  loadOrders()
}

// 搜索
function handleSearch() {
  pagination.current = 1
  loadOrders()
}

// 重置
function handleReset() {
  filterStatus.value = undefined
  searchKeyword.value = ''
  pagination.current = 1
  loadOrders()
}

// 表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadOrders()
}

// 查看订单详情
function viewOrderDetail(record: VipOrderInfo) {
  selectedOrder.value = record
  drawerVisible.value = true
}

// 更新订单状态
function updateStatus(record: VipOrderInfo, newStatus: number) {
  const statusText = getStatusText(newStatus)
  
  Modal.confirm({
    title: '确认操作',
    content: `确定要将订单状态更新为"${statusText}"吗？`,
    onOk: async () => {
      try {
        await AdminAPI.updateOrderStatus(record.id, newStatus)
        message.success('订单状态更新成功')
        loadOrders()
      } catch (error) {
        console.error('更新订单状态失败:', error)
        message.error('更新订单状态失败')
      }
    }
  })
}

// 获取VIP套餐文本
function getPlanText(planType: number): string {
  const plans: Record<number, string> = {
    1: '月度VIP',
    2: '季度VIP',
    3: '年度VIP'
  }
  return plans[planType] || '未知套餐'
}

// 获取VIP套餐颜色
function getPlanColor(planType: number): string {
  const colors: Record<number, string> = {
    1: 'blue',
    2: 'cyan',
    3: 'gold'
  }
  return colors[planType] || 'default'
}

// 获取状态文本
function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
function getStatusColor(status: number): string {
  const colorMap: Record<number, string> = {
    0: 'orange',
    1: 'success',
    2: 'default',
    3: 'red'
  }
  return colorMap[status] || 'default'
}

// 格式化日期时间
function formatDateTime(dateTime: string): string {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

