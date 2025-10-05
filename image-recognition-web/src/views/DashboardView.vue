<template>
  <div class="page">
          <h1 class="page-title">仪表板</h1>
          
          <!-- Stats Grid -->
          <a-row :gutter="[24, 24]" class="stats-grid">
            <a-col :xs="24" :sm="12" :lg="6">
              <a-card class="stat-card">
                <a-statistic
                  title="总产品数"
                  :value="1234"
                  suffix="个"
                  :value-style="{ color: '#1890ff' }"
                />
                <div class="stat-trend trend-up">
                  <i class="fas fa-arrow-up"></i>
                  <span>+12.5%</span>
                </div>
              </a-card>
            </a-col>
            <a-col :xs="24" :sm="12" :lg="6">
              <a-card class="stat-card">
                <a-statistic
                  title="总用户数"
                  :value="5678"
                  suffix="人"
                  :value-style="{ color: '#52c41a' }"
                />
                <div class="stat-trend trend-up">
                  <i class="fas fa-arrow-up"></i>
                  <span>+8.3%</span>
                </div>
              </a-card>
            </a-col>
            <a-col :xs="24" :sm="12" :lg="6">
              <a-card class="stat-card">
                <a-statistic
                  title="今日订单"
                  :value="892"
                  suffix="单"
                  :value-style="{ color: '#faad14' }"
                />
                <div class="stat-trend trend-down">
                  <i class="fas fa-arrow-down"></i>
                  <span>-2.1%</span>
                </div>
              </a-card>
            </a-col>
            <a-col :xs="24" :sm="12" :lg="6">
              <a-card class="stat-card">
                <a-statistic
                  title="今日收入"
                  :value="156789"
                  prefix="¥"
                  :value-style="{ color: '#f5222d' }"
                />
                <div class="stat-trend trend-up">
                  <i class="fas fa-arrow-up"></i>
                  <span>+15.2%</span>
                </div>
              </a-card>
            </a-col>
          </a-row>
          
          <!-- Charts -->
          <a-card title="销售趋势" class="chart-card">
            <div class="chart-placeholder">
              <i class="fas fa-chart-line"></i>
              图表区域 - 销售趋势图
            </div>
          </a-card>
          
          <!-- Recent Orders -->
          <a-card title="最近订单" class="table-card">
            <a-table 
              :columns="orderColumns" 
              :data-source="recentOrders"
              :pagination="false"
              size="middle"
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
                  <a-button type="primary" size="small" @click="viewOrder(record)">
                    查看
                  </a-button>
                </template>
              </template>
            </a-table>
          </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

// 仪表板订单表格列定义
const orderColumns = [
  { title: '订单号', dataIndex: 'id', key: 'id' },
  { title: '客户', dataIndex: 'customer', key: 'customer' },
  { title: '产品', dataIndex: 'product', key: 'product' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' }
]

// 最近订单数据
const recentOrders = ref([
  { id: '#ORD001', customer: '张三', product: '智能图像识别设备', amount: '¥2,999', status: '已完成' },
  { id: '#ORD002', customer: '李四', product: 'AI视觉模块', amount: '¥5,999', status: '处理中' },
  { id: '#ORD003', customer: '王五', product: '图像处理服务', amount: '¥1,599', status: '已取消' }
])

function getStatusColor(status: string) {
  switch (status) {
    case '已完成': return 'success'
    case '处理中': return 'processing'
    case '已取消': return 'error'
    default: return 'default'
  }
}

// 查看订单详情
function viewOrder(record: any) {
  message.info(`查看订单：${record.id}`)
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

/* Stats Grid */
.stats-grid {
  margin-bottom: 24px;
}

.stat-card {
  border-left: 4px solid #1890ff;
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

.trend-down {
  color: #ff4d4f;
}

/* Charts */
.chart-card {
  margin-bottom: 24px;
}

.chart-placeholder {
  height: 300px;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 16px;
  gap: 8px;
}

/* Tables */
.table-card {
  margin-bottom: 24px;
}

.status-tag {
  font-size: 12px;
  font-weight: 500;
}
</style>
