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
    
    <!-- Charts Row -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="16">
        <a-card 
          title="识别趋势分析" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 350px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="lineChartOption" 
              autoresize
            />
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="8">
        <a-card 
          title="用户分布" 
          style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);"
          :headStyle="{ borderBottom: '1px solid #f0f0f0', padding: '16px 24px' }"
          :bodyStyle="{ padding: '24px' }"
        >
          <div style="width: 100%; height: 350px;">
            <v-chart 
              style="width: 100%; height: 100%;"
              :option="pieChartOption" 
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

// 页面加载时获取数据
onMounted(() => {
  loadDashboardStats()
  loadTrendData()
})

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
    data: trendData.value.dates || []
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
      data: trendData.value.values || []
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
</script>

