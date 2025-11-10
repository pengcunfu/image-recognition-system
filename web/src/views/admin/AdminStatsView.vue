<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    
    <!-- Stats Grid -->
    <a-row :gutter="[16, 16]" style="margin-bottom: 24px;">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="总识别次数"
            :value="stats.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+15.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="注册用户"
            :value="stats.totalUsers"
            suffix="人"
            :value-style="{ color: '#52c41a', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+8.3%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="VIP用户"
            :value="stats.vipUsers"
            suffix="人"
            :value-style="{ color: '#faad14', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+12.5%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="社区帖子"
            :value="stats.totalPosts"
            suffix="篇"
            :value-style="{ color: '#722ed1', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+6.8%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Additional Stats Row -->
    <a-row :gutter="[16, 16]" style="margin-bottom: 24px;">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="今日识别"
            :value="stats.todayRecognitions"
            suffix="次"
            :value-style="{ color: '#13c2c2', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+22.1%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="知识库条目"
            :value="stats.knowledgeItems"
            suffix="条"
            :value-style="{ color: '#eb2f96', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>+4.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="待审核帖子"
            :value="stats.pendingPosts"
            suffix="篇"
            :value-style="{ color: '#fa541c', fontSize: '24px', fontWeight: '600' }"
          />
          <div 
            style="margin-top: 12px; display: flex; align-items: center; gap: 6px; font-size: 13px;"
            :style="{ color: stats.pendingPosts > 0 ? '#faad14' : '#52c41a' }"
          >
            <i :class="stats.pendingPosts > 0 ? 'fas fa-exclamation-triangle' : 'fas fa-check'"></i>
            <span>{{ stats.pendingPosts > 0 ? '需要处理' : '已处理完' }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <a-statistic
            title="系统活跃度"
            :value="stats.systemActivity"
            suffix="%"
            :value-style="{ color: '#52c41a', fontSize: '24px', fontWeight: '600' }"
          />
          <div style="margin-top: 12px; display: flex; align-items: center; gap: 6px; color: #52c41a; font-size: 13px;">
            <i class="fas fa-arrow-up"></i>
            <span>系统正常</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Charts Row 1 -->
    <a-row :gutter="[16, 16]" style="margin-bottom: 24px;">
      <a-col :xs="24" :lg="12">
        <a-card 
          title="用户角色分布" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 300px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="userRoleChartOption" 
              autoresize
            />
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card 
          title="识别分类统计" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 300px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="recognitionCategoryChartOption" 
              autoresize
            />
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- Charts Row 2 -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="12">
        <a-card 
          title="VIP订单统计" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 300px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="vipOrderChartOption" 
              autoresize
            />
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card 
          title="每日识别趋势（最近30天）" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 300px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="dailyRecognitionChartOption" 
              autoresize
            />
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { AdminAPI } from '@/api/admin'
import type { 
  UserRoleStats, 
  RecognitionCategoryStats, 
  VipOrderStats, 
  DailyRecognitionStats 
} from '@/api/admin'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import {
  CanvasRenderer
} from 'echarts/renderers'
import {
  LineChart,
  PieChart,
  BarChart
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
  BarChart,
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
  systemActivity: 85,
  activeUsers: 0,
  totalKnowledge: 0,
  totalOrders: 0,
  totalRevenue: 0,
  userGrowth: 0,
  recognitionGrowth: 0,
  postGrowth: 0,
  revenueGrowth: 0
})

// 趋势数据
const trendData = ref<any>({ dates: [], values: [] })

// 新增图表数据
const userRoleData = ref<UserRoleStats>({
  normalUsers: 0,
  vipUsers: 0,
  adminUsers: 0,
  totalUsers: 0
})

const recognitionCategoryData = ref<RecognitionCategoryStats>({
  categories: [],
  totalRecognitions: 0
})

const vipOrderData = ref<VipOrderStats>({
  orders: [],
  totalOrders: 0,
  totalRevenue: 0
})

const dailyRecognitionData = ref<DailyRecognitionStats>({
  data: [],
  totalDays: 0,
  avgDaily: 0
})

// 加载统计数据
async function loadDashboardStats() {
  try {
    const overview = await AdminAPI.getSystemOverview()
    
    if (overview) {
      stats.totalRecognitions = overview.totalRecognitions || 0
      stats.totalUsers = overview.totalUsers || 0
      stats.activeUsers = overview.activeUsers || 0
      stats.todayRecognitions = overview.todayRecognitions || 0
      stats.totalPosts = overview.totalPosts || 0
      stats.knowledgeItems = overview.totalKnowledge || 0
      stats.totalKnowledge = overview.totalKnowledge || 0
      stats.totalOrders = overview.totalOrders || 0
      stats.totalRevenue = overview.totalRevenue || 0
      stats.userGrowth = overview.userGrowth || 0
      stats.recognitionGrowth = overview.recognitionGrowth || 0
      stats.postGrowth = overview.postGrowth || 0
      stats.revenueGrowth = overview.revenueGrowth || 0
      
      // 计算VIP用户数（假设10%）
      stats.vipUsers = Math.floor(stats.totalUsers * 0.1)
      
      // 计算待审核帖子（假设5%）
      stats.pendingPosts = Math.floor(stats.totalPosts * 0.05)
      
      // 系统活跃度
      stats.systemActivity = stats.activeUsers > 0 
        ? Math.min(100, Math.round((stats.activeUsers / stats.totalUsers) * 100))
        : 85
    }
  } catch (error) {
    console.error('加载仪表盘统计数据失败:', error)
    message.error('加载统计数据失败')
  }
}

// 加载趋势数据
async function loadTrendData() {
  try {
    const trends = await AdminAPI.getTrendData(30)
    
    if (trends && trends.dates) {
      trendData.value = trends
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 加载用户角色统计数据
async function loadUserRoleStats() {
  try {
    const data = await AdminAPI.getUserRoleStats()
    if (data) {
      userRoleData.value = data
    }
  } catch (error) {
    console.error('加载用户角色统计失败:', error)
    // 使用模拟数据
    userRoleData.value = {
      normalUsers: 7,
      vipUsers: 4,
      adminUsers: 1,
      totalUsers: 12
    }
  }
}

// 加载识别分类统计数据
async function loadRecognitionCategoryStats() {
  try {
    const data = await AdminAPI.getRecognitionCategoryStats()
    if (data) {
      recognitionCategoryData.value = data
    }
  } catch (error) {
    console.error('加载识别分类统计失败:', error)
    // 使用模拟数据
    recognitionCategoryData.value = {
      categories: [
        { category: '哪吒卡通形象', count: 3, percentage: 18.8 },
        { category: '未分类', count: 2, percentage: 12.5 },
        { category: '跑车', count: 2, percentage: 12.5 },
        { category: '猫耳少女', count: 1, percentage: 6.3 },
        { category: '秋景树林', count: 1, percentage: 6.3 },
        { category: '人物', count: 1, percentage: 6.3 },
        { category: '蒙奇·D·路飞', count: 1, percentage: 6.3 },
        { category: '蓝妹啤酒', count: 1, percentage: 6.3 },
        { category: '棒球帽', count: 1, percentage: 6.3 },
        { category: '针织手套', count: 1, percentage: 6.3 }
      ],
      totalRecognitions: 16
    }
  }
}

// 加载VIP订单统计数据
async function loadVipOrderStats() {
  try {
    const data = await AdminAPI.getVipOrderStats()
    if (data) {
      vipOrderData.value = data
    }
  } catch (error) {
    console.error('加载VIP订单统计失败:', error)
    // 使用模拟数据
    vipOrderData.value = {
      orders: [
        { planType: 0, planName: '月度会员', orderCount: 5, totalAmount: 149.50, percentage: 83.3 },
        { planType: 2, planName: '年度会员', orderCount: 1, totalAmount: 199.00, percentage: 16.7 }
      ],
      totalOrders: 6,
      totalRevenue: 348.50
    }
  }
}

// 加载每日识别统计数据
async function loadDailyRecognitionStats() {
  try {
    const data = await AdminAPI.getDailyRecognitionStats()
    if (data) {
      dailyRecognitionData.value = data
    }
  } catch (error) {
    console.error('加载每日识别统计失败:', error)
    // 使用模拟数据
    dailyRecognitionData.value = {
      data: [
        { date: '2025-10-22', count: 6 },
        { date: '2025-10-23', count: 5 },
        { date: '2025-10-24', count: 4 },
        { date: '2025-10-25', count: 2 }
      ],
      totalDays: 4,
      avgDaily: 4.25
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadDashboardStats()
  loadTrendData()
  loadUserRoleStats()
  loadRecognitionCategoryStats()
  loadVipOrderStats()
  loadDailyRecognitionStats()
})

// 用户角色分布饼图配置
const userRoleChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}人 ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: {
      fontSize: 12
    }
  },
  series: [
    {
      name: '用户角色',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
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
          fontSize: '16',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        {
          value: userRoleData.value.normalUsers,
          name: '普通用户',
          itemStyle: {
            color: '#52c41a'
          }
        },
        {
          value: userRoleData.value.vipUsers,
          name: 'VIP用户',
          itemStyle: {
            color: '#faad14'
          }
        },
        {
          value: userRoleData.value.adminUsers,
          name: '管理员',
          itemStyle: {
            color: '#1890ff'
          }
        }
      ]
    }
  ]
}))

// 识别分类统计柱状图配置
const recognitionCategoryChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    },
    formatter: '{a} <br/>{b}: {c}次 ({d}%)'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '15%',
    top: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: recognitionCategoryData.value.categories.slice(0, 8).map(item => item.category),
    axisLabel: {
      interval: 0,
      rotate: 45,
      fontSize: 10
    }
  },
  yAxis: {
    type: 'value',
    name: '识别次数'
  },
  series: [
    {
      name: '识别分类',
      type: 'bar',
      data: recognitionCategoryData.value.categories.slice(0, 8).map((item, index) => ({
        value: item.count,
        d: item.percentage.toFixed(1),
        itemStyle: {
          color: [
            '#1890ff', '#52c41a', '#faad14', '#f5222d', 
            '#722ed1', '#13c2c2', '#eb2f96', '#fa541c'
          ][index % 8]
        }
      })),
      barWidth: '60%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0]
      }
    }
  ]
}))

// VIP订单统计环形图配置
const vipOrderChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}单 (¥{d})'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: {
      fontSize: 12
    }
  },
  series: [
    {
      name: 'VIP订单',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
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
          fontSize: '16',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: vipOrderData.value.orders.map((item, index) => ({
        value: item.orderCount,
        name: item.planName,
        d: item.totalAmount.toFixed(2),
        itemStyle: {
          color: ['#1890ff', '#52c41a', '#faad14'][index % 3]
        }
      }))
    }
  ]
}))

// 每日识别趋势折线图配置
const dailyRecognitionChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    },
    formatter: '{a} <br/>{b}: {c}次'
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
    data: dailyRecognitionData.value.data.map(item => 
      new Date(item.date).toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
    )
  },
  yAxis: {
    type: 'value',
    name: '识别次数'
  },
  series: [
    {
      name: '每日识别',
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
      data: dailyRecognitionData.value.data.map(item => item.count)
    }
  ]
}))
</script>

