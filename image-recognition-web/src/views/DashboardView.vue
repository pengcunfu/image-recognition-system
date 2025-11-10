<template>
  <div class="page">
    <h1 class="page-title">仪表板</h1>
    
    <!-- Stats Grid -->
    <a-row :gutter="[24, 24]" class="stats-grid">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card recognition-card">
          <a-statistic
            title="总识别次数"
            :value="stats.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+15.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card users-card">
          <a-statistic
            title="注册用户"
            :value="stats.totalUsers"
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
        <a-card class="stat-card vip-card">
          <a-statistic
            title="VIP用户"
            :value="stats.vipUsers"
            suffix="人"
            :value-style="{ color: '#faad14' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+12.5%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card posts-card">
          <a-statistic
            title="社区帖子"
            :value="stats.totalPosts"
            suffix="篇"
            :value-style="{ color: '#722ed1' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+6.8%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Additional Stats Row -->
    <a-row :gutter="[24, 24]" class="stats-grid">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="今日识别"
            :value="stats.todayRecognitions"
            suffix="次"
            :value-style="{ color: '#13c2c2' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+22.1%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="知识库条目"
            :value="stats.knowledgeItems"
            suffix="条"
            :value-style="{ color: '#eb2f96' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+4.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="待审核帖子"
            :value="stats.pendingPosts"
            suffix="篇"
            :value-style="{ color: '#fa541c' }"
          />
          <div class="stat-trend" :class="stats.pendingPosts > 0 ? 'trend-warning' : 'trend-success'">
            <i :class="stats.pendingPosts > 0 ? 'fas fa-exclamation-triangle' : 'fas fa-check'"></i>
            <span>{{ stats.pendingPosts > 0 ? '需要处理' : '已处理完' }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="系统活跃度"
            :value="stats.systemActivity"
            suffix="%"
            :value-style="{ color: '#52c41a' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>系统正常</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Charts Row -->
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="16">
        <a-card title="识别趋势分析" class="chart-card">
          <v-chart 
            class="chart" 
            :option="lineChartOption" 
            autoresize
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="8">
        <a-card title="用户分布" class="chart-card">
          <v-chart 
            class="chart pie-chart" 
            :option="pieChartOption" 
            autoresize
          />
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Quick Actions -->
    <a-card title="快捷操作" class="quick-actions-card">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6">
          <a-button type="primary" block @click="goToPostsManagement">
            <i class="fas fa-clipboard-list"></i>
            帖子管理
          </a-button>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-button type="primary" block @click="goToKnowledgeManagement">
            <i class="fas fa-book"></i>
            知识库管理
          </a-button>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-button type="primary" block @click="goToVipManagement">
            <i class="fas fa-crown"></i>
            VIP管理
          </a-button>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-button type="primary" block @click="goToSystemSettings">
            <i class="fas fa-cog"></i>
            系统设置
          </a-button>
        </a-col>
      </a-row>
    </a-card>
    
    <!-- Recent Activities -->
    <a-card title="最近活动" class="table-card">
      <a-table 
        :columns="activityColumns" 
        :data-source="recentActivities"
        :pagination="false"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag 
              :color="getActivityColor(record.type)"
              class="activity-tag"
            >
              <i :class="getActivityIcon(record.type)"></i>
              {{ record.type }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="viewActivity(record)">
              查看详情
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { UserAPI } from '@/api/user'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import {
  CanvasRenderer
} from 'echarts/renderers'
import {
  LineChart,
  PieChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const router = useRouter()

// 统计数据
const stats = reactive({
  totalRecognitions: 0,
  totalUsers: 0,
  vipUsers: 0,
  totalPosts: 0,
  todayRecognitions: 0,
  knowledgeItems: 0,
  pendingPosts: 0,
  systemActivity: 0
})

// 加载统计数据
async function loadDashboardStats() {
  try {
    const response = await UserAPI.getAdminDashboardStats()
    Object.assign(stats, response.data)
  } catch (error) {
    console.error('加载仪表盘统计数据失败:', error)
    message.error('加载统计数据失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadDashboardStats()
})

// 生成最近30天的识别数据
const generateRecognitionData = () => {
  const data = []
  const categories = []
  const today = new Date()
  
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
    categories.push(dateStr)
    
    // 生成模拟数据，有一定的波动
    const baseValue = 4000
    const randomFactor = Math.random() * 2000 - 1000
    const weekendFactor = date.getDay() === 0 || date.getDay() === 6 ? -500 : 0
    data.push(Math.max(1000, baseValue + randomFactor + weekendFactor))
  }
  
  return { data, categories }
}

const recognitionData = generateRecognitionData()

// 折线图配置
const lineChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: recognitionData.categories
  },
  yAxis: {
    type: 'value',
    name: '识别次数'
  },
  series: [
    {
      name: '图像识别',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        color: '#1890ff',
        width: 3
      },
      itemStyle: {
        color: '#1890ff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: 'rgba(24, 144, 255, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(24, 144, 255, 0.05)'
            }
          ]
        }
      },
      data: recognitionData.data
    }
  ]
}))

// 饼图配置
const pieChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '用户类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        {
          value: stats.totalUsers - stats.vipUsers,
          name: '普通用户',
          itemStyle: {
            color: '#52c41a'
          }
        },
        {
          value: stats.vipUsers,
          name: 'VIP用户',
          itemStyle: {
            color: '#faad14'
          }
        }
      ]
    }
  ]
}))

// 活动表格列定义
const activityColumns = [
  { title: '时间', dataIndex: 'time', key: 'time', width: 180 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
  { title: '用户', dataIndex: 'user', key: 'user', width: 120 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'action', width: 100 }
]

// 最近活动数据
const recentActivities = ref([
  { 
    id: 1,
    time: '2025-01-15 14:30',
    type: '图像识别',
    user: '张三',
    description: '识别了一张动物图片（猫）'
  },
  { 
    id: 2,
    time: '2025-01-15 14:25',
    type: '发布帖子',
    user: '李四',
    description: '在社区发布了新帖子："如何提高识别准确率"'
  },
  { 
    id: 3,
    time: '2025-01-15 14:20',
    type: '用户注册',
    user: '王五',
    description: '新用户注册并完成邮箱验证'
  },
  { 
    id: 4,
    time: '2025-01-15 14:15',
    type: 'VIP升级',
    user: '赵六',
    description: '用户升级为VIP会员'
  },
  { 
    id: 5,
    time: '2025-01-15 14:10',
    type: '批量识别',
    user: '钱七',
    description: '开始批量识别任务，包含50张图片'
  }
])

// 获取活动类型颜色
function getActivityColor(type: string) {
  switch (type) {
    case '图像识别': return 'blue'
    case '发布帖子': return 'green'
    case '用户注册': return 'cyan'
    case 'VIP升级': return 'gold'
    case '批量识别': return 'purple'
    default: return 'default'
  }
}

// 获取活动类型图标
function getActivityIcon(type: string) {
  switch (type) {
    case '图像识别': return 'fas fa-camera'
    case '发布帖子': return 'fas fa-edit'
    case '用户注册': return 'fas fa-user-plus'
    case 'VIP升级': return 'fas fa-crown'
    case '批量识别': return 'fas fa-images'
    default: return 'fas fa-info-circle'
  }
}

// 快捷操作导航
function goToPostsManagement() {
  router.push('/posts-management')
}

function goToKnowledgeManagement() {
  router.push('/knowledge-management')
}

function goToVipManagement() {
  router.push('/vip-management')
}

function goToSystemSettings() {
  router.push('/settings')
}

// 查看活动详情
function viewActivity(record: any) {
  message.info(`查看活动详情：${record.description}`)
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
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
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

.trend-warning {
  color: #fa541c;
}

.trend-success {
  color: #52c41a;
}

/* Charts */
.chart-card {
  margin-bottom: 24px;
}

.chart {
  height: 300px;
  width: 100%;
}

.pie-chart {
  height: 300px;
}

/* Quick Actions */
.quick-actions-card {
  margin-bottom: 24px;
}

.quick-actions-card .ant-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.quick-actions-card .ant-btn i {
  font-size: 16px;
}

/* Tables */
.table-card {
  margin-bottom: 24px;
}

.activity-tag {
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-tag i {
  font-size: 10px;
}

/* Responsive */
@media (max-width: 1200px) {
  .chart {
    height: 250px;
  }
}

@media (max-width: 768px) {
  .stats-grid .ant-col {
    margin-bottom: 16px;
  }
  
  .chart {
    height: 200px;
  }
  
  .quick-actions-card .ant-btn {
    height: 40px;
    font-size: 13px;
  }
  
  .quick-actions-card .ant-btn i {
    font-size: 14px;
  }
}

@media (max-width: 576px) {
  .page-title {
    font-size: 20px;
    margin-bottom: 16px;
  }
  
  .chart {
    height: 180px;
  }
  
  .activity-tag {
    font-size: 11px;
  }
}

/* 自定义统计卡片动画 */
.stat-card .ant-statistic-content {
  transition: all 0.3s ease;
}

.stat-card:hover .ant-statistic-content {
  transform: scale(1.02);
}

/* ECharts 图表容器样式 */
:deep(.echarts) {
  background: transparent !important;
}

/* 图表加载状态 */
.chart-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
}

/* 快捷操作按钮悬停效果 */
.quick-actions-card .ant-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}
</style>
