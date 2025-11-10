<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
        <div>
          <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">
            VIP数据分析
          </h1>
          <p :style="{ margin: '0', fontSize: '14px', opacity: 0.65 }">深度洞察您的识别数据和使用模式</p>
        </div>
        <a-space>
          <a-range-picker v-model:value="dateRange" @change="updateAnalytics" />
          <a-button type="primary" @click="exportReport">
            <i class="fas fa-file-export"></i>
            导出报告
          </a-button>
        </a-space>
      </div>
    </a-card>

    <!-- 核心指标 -->
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '16px' }">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }" :loading="loading">
          <a-statistic
            title="总识别次数"
            :value="analytics.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: analytics.growthRate >= 0 ? '#52c41a' : '#f5222d' }">
            <i :class="analytics.growthRate >= 0 ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
            <span :style="{ marginLeft: '4px' }">{{ analytics.growthRate >= 0 ? '+' : '' }}{{ analytics.growthRate.toFixed(1) }}%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }" :loading="loading">
          <a-statistic
            title="平均精度"
            :value="analytics.averageAccuracy"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#52c41a' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-up"></i>
            <span :style="{ marginLeft: '4px' }">+{{ analytics.accuracyImprovement.toFixed(1) }}%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }" :loading="loading">
          <a-statistic
            title="处理速度"
            :value="analytics.avgProcessTime"
            suffix="ms"
            :value-style="{ color: '#faad14' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-down"></i>
            <span :style="{ marginLeft: '4px' }">-{{ analytics.speedImprovement.toFixed(1) }}%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }" :loading="loading">
          <a-statistic
            title="节省成本"
            :value="analytics.costSaved"
            prefix="¥"
            :precision="2"
            :value-style="{ color: '#f5222d' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-up"></i>
            <span :style="{ marginLeft: '4px' }">基于{{ analytics.daysAnalyzed }}天数据</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表分析 -->
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '16px' }">
      <a-col :xs="24" :lg="12">
        <a-card title="识别趋势分析" :style="{ borderRadius: '8px' }" :loading="loading">
          <v-chart 
            :option="trendChartOption" 
            :style="{ height: '300px' }"
            autoresize
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="准确率分布" :style="{ borderRadius: '8px' }" :loading="loading">
          <v-chart 
            :option="accuracyChartOption" 
            :style="{ height: '300px' }"
            autoresize
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 分类统计图表 -->
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '16px' }">
      <a-col :xs="24">
        <a-card title="识别分类统计" :style="{ borderRadius: '8px' }" :loading="loading">
          <template #extra>
            <a-space>
              <a-tag color="blue">最多: {{ categoryAnalysis.topCategory }}</a-tag>
              <a-tag color="green">最准: {{ categoryAnalysis.mostAccurateCategory }}</a-tag>
              <a-tag color="orange">最快: {{ categoryAnalysis.fastestCategory }}</a-tag>
            </a-space>
          </template>
          <v-chart 
            :option="categoryChartOption" 
            :style="{ height: '400px' }"
            autoresize
          />
    </a-card>
      </a-col>
    </a-row>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { StatsAPI, type VipAnalytics, type VipTrends, type VipCategoryAnalysis } from '@/api/stats'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const dateRange = ref<[Dayjs, Dayjs] | null>(null)
const loading = ref(false)

// 统计数据
const analytics = ref<VipAnalytics>({
  totalRecognitions: 0,
  averageAccuracy: 0,
  avgProcessTime: 0,
  costSaved: 0,
  growthRate: 0,
  accuracyImprovement: 0,
  speedImprovement: 0,
  daysAnalyzed: 30
})

const trends = ref<VipTrends>({
  dailyTrends: [],
  dates: [],
  recognitionCounts: [],
  accuracyTrends: [],
  timeTrends: []
})

const categoryAnalysis = ref<VipCategoryAnalysis>({
  categories: [],
  topCategory: '',
  mostAccurateCategory: '',
  fastestCategory: ''
})


// 计算分析天数
const analysisDays = computed(() => {
  if (dateRange.value) {
    const [start, end] = dateRange.value
    return end.diff(start, 'day') + 1
  }
  return 30
})

// 识别趋势图表配置
const trendChartOption = computed(() => ({
  title: {
    text: '识别次数趋势',
    left: 'center',
    textStyle: {
      fontSize: 16,
      fontWeight: 'normal'
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: {
    data: ['识别次数', '平均准确率'],
    bottom: 10
  },
  xAxis: {
    type: 'category',
    data: trends.value.dates,
    axisLabel: {
      formatter: (value: string) => {
        return dayjs(value).format('MM-DD')
      }
    }
  },
  yAxis: [
    {
      type: 'value',
      name: '识别次数',
      position: 'left'
    },
    {
      type: 'value',
      name: '准确率(%)',
      position: 'right',
      min: 90,
      max: 100
    }
  ],
  series: [
    {
      name: '识别次数',
      type: 'line',
      data: trends.value.recognitionCounts,
      smooth: true,
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
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
          ]
        }
      }
    },
    {
      name: '平均准确率',
      type: 'line',
      yAxisIndex: 1,
      data: trends.value.accuracyTrends,
      smooth: true,
      itemStyle: {
        color: '#52c41a'
      }
    }
  ],
  grid: {
    top: 60,
    bottom: 60,
    left: 60,
    right: 60
  }
}))

// 准确率分布图表配置
const accuracyChartOption = computed(() => {
  const accuracyRanges = [
    { name: '95-100%', value: 0 },
    { name: '90-95%', value: 0 },
    { name: '85-90%', value: 0 },
    { name: '80-85%', value: 0 },
    { name: '<80%', value: 0 }
  ]

  // 根据实际数据计算分布
  trends.value.accuracyTrends.forEach(accuracy => {
    if (accuracy >= 95) accuracyRanges[0].value++
    else if (accuracy >= 90) accuracyRanges[1].value++
    else if (accuracy >= 85) accuracyRanges[2].value++
    else if (accuracy >= 80) accuracyRanges[3].value++
    else accuracyRanges[4].value++
  })

  return {
    title: {
      text: '准确率分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: accuracyRanges.map(item => item.name)
    },
    series: [
      {
        name: '准确率分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: accuracyRanges,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        }
      }
    ]
  }
})

// 分类统计图表配置
const categoryChartOption = computed(() => ({
  title: {
    text: '识别分类统计',
    left: 'center',
    textStyle: {
      fontSize: 16,
      fontWeight: 'normal'
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: categoryAnalysis.value.categories.slice(0, 10).map(item => item.category),
    axisLabel: {
      interval: 0,
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    name: '识别次数'
  },
  series: [
    {
      name: '识别次数',
      type: 'bar',
      data: categoryAnalysis.value.categories.slice(0, 10).map(item => item.count),
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: '#1890ff' },
            { offset: 1, color: '#69c0ff' }
          ]
        },
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: '#096dd9'
        }
      }
    }
  ],
  grid: {
    top: 60,
    bottom: 100,
    left: 60,
    right: 40
  }
}))

// 加载数据
async function loadAnalyticsData() {
  loading.value = true
  
  try {
    const days = analysisDays.value
    
    // 并行加载所有数据
    const [analyticsData, trendsData, categoryData] = await Promise.all([
      StatsAPI.getVipAnalytics(days),
      StatsAPI.getVipTrends(days),
      StatsAPI.getVipCategoryAnalysis(days)
    ])
    
    analytics.value = analyticsData
    trends.value = trendsData
    categoryAnalysis.value = categoryData
    
    message.success('数据加载完成')
  } catch (error: any) {
    console.error('加载VIP分析数据失败:', error)
    message.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// 更新分析数据
function updateAnalytics() {
  loadAnalyticsData()
}

// 导出报告
function exportReport() {
  const reportData = {
    analytics: analytics.value,
    trends: trends.value,
    categories: categoryAnalysis.value,
    exportTime: new Date().toISOString()
  }
  
  const blob = new Blob([JSON.stringify(reportData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `vip-analytics-report-${dayjs().format('YYYY-MM-DD')}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  message.success('分析报告已导出')
}


// 页面加载时获取数据
onMounted(() => {
  loadAnalyticsData()
})
</script>
