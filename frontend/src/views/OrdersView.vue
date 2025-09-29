<template>
  <div class="page">
    <h1 class="page-title">订单管理</h1>
    
    <a-card class="table-card">
      <a-table 
        :columns="orderColumns" 
        :data-source="orders"
        :pagination="{ pageSize: 10 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag 
              :color="getStatusColor(record.status)"
              class="status-tag"
            >
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="viewOrderDetail(record)">
                查看详情
              </a-button>
              <a-button 
                v-if="record.status === '待处理'"
                type="primary" 
                size="small" 
                @click="processOrder(record)"
              >
                处理订单
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

// 表格列定义
const orderColumns = [
  { title: '订单号', dataIndex: 'id', key: 'id' },
  { title: '客户', dataIndex: 'customer', key: 'customer' },
  { title: '产品', dataIndex: 'product', key: 'product' },
  { title: '数量', dataIndex: 'quantity', key: 'quantity' },
  { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount' },
  { title: '订单日期', dataIndex: 'orderDate', key: 'orderDate' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' }
]

// 订单数据
const orders = ref([
  { 
    id: '#ORD001', 
    customer: '张三', 
    product: '智能图像识别摄像头', 
    quantity: 1, 
    totalAmount: '¥8,999', 
    orderDate: '2024-03-15', 
    status: '已完成' 
  },
  { 
    id: '#ORD002', 
    customer: '李四', 
    product: 'AI视觉分析模块', 
    quantity: 1, 
    totalAmount: '¥12,999', 
    orderDate: '2024-03-16', 
    status: '处理中' 
  },
  { 
    id: '#ORD003', 
    customer: '王五', 
    product: '图像处理服务包', 
    quantity: 2, 
    totalAmount: '¥9,198', 
    orderDate: '2024-03-17', 
    status: '已取消' 
  },
  { 
    id: '#ORD004', 
    customer: '赵六', 
    product: '深度学习训练平台', 
    quantity: 1, 
    totalAmount: '¥15,999', 
    orderDate: '2024-03-18', 
    status: '待处理' 
  },
  { 
    id: '#ORD005', 
    customer: '钱七', 
    product: '边缘计算设备', 
    quantity: 3, 
    totalAmount: '¥20,664', 
    orderDate: '2024-03-19', 
    status: '已发货' 
  }
])

// 方法
function getStatusColor(status: string) {
  switch (status) {
    case '已完成': return 'success'
    case '处理中': return 'processing'
    case '已发货': return 'blue'
    case '待处理': return 'orange'
    case '已取消': return 'error'
    default: return 'default'
  }
}

function viewOrderDetail(record: any) {
  message.info(`查看订单详情：${record.id}`)
}

function processOrder(record: any) {
  record.status = '处理中'
  message.success(`订单${record.id}已开始处理`)
}
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

.table-card {
  margin-bottom: 24px;
}

.status-tag {
  font-size: 12px;
  font-weight: 500;
}
</style>
