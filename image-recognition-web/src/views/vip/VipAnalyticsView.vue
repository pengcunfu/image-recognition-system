<template>
  <div class="vip-analytics-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <i class="fas fa-chart-line vip-icon"></i>
            VIP数据分析
          </h1>
          <p>深度洞察您的识别数据和使用模式</p>
        </div>
        <div class="header-actions">
          <a-range-picker v-model:value="dateRange" @change="updateAnalytics" />
          <a-button type="primary" @click="exportReport">
            <i class="fas fa-file-export"></i>
            导出报告
          </a-button>
        </div>
      </div>
    </div>

    <!-- 核心指标 -->
    <a-row :gutter="[24, 24]" class="metrics-section">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="metric-card">
          <a-statistic
            title="总识别次数"
            :value="metrics.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
          <div class="metric-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+23.5%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="metric-card">
          <a-statistic
            title="平均精度"
            :value="metrics.averageAccuracy"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#52c41a' }"
          />
          <div class="metric-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+2.1%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="metric-card">
          <a-statistic
            title="处理速度"
            :value="metrics.avgProcessTime"
            suffix="ms"
            :value-style="{ color: '#faad14' }"
          />
          <div class="metric-trend trend-down">
            <i class="fas fa-arrow-down"></i>
            <span>-15.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="metric-card">
          <a-statistic
            title="节省成本"
            :value="metrics.costSaved"
            prefix="¥"
            :value-style="{ color: '#f5222d' }"
          />
          <div class="metric-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+45.8%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表分析 -->
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="12">
        <a-card title="识别趋势分析" class="chart-card">
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="fas fa-chart-area"></i>
              <span>识别次数趋势图</span>
              <div class="chart-description">
                显示过去30天的识别活动趋势
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="准确率分布" class="chart-card">
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="fas fa-chart-pie"></i>
              <span>准确率分布图</span>
              <div class="chart-description">
                按准确率区间统计识别结果
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 使用建议 -->
    <a-card title="智能优化建议" class="suggestions-card">
      <div class="suggestions-list">
        <div 
          v-for="suggestion in suggestions" 
          :key="suggestion.id"
          class="suggestion-item"
        >
          <div class="suggestion-icon">
            <i :class="suggestion.icon"></i>
          </div>
          <div class="suggestion-content">
            <h4>{{ suggestion.title }}</h4>
            <p>{{ suggestion.description }}</p>
            <div class="suggestion-impact">
              预期改善: {{ suggestion.impact }}
            </div>
          </div>
          <div class="suggestion-action">
            <a-button type="primary" size="small" @click="applySuggestion(suggestion)">
              应用建议
            </a-button>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'

const dateRange = ref<[Dayjs, Dayjs] | null>(null)

const metrics = reactive({
  totalRecognitions: 3456,
  averageAccuracy: 97.2,
  avgProcessTime: 847,
  costSaved: 2856
})

const suggestions = ref([
  {
    id: 1,
    icon: 'fas fa-bolt',
    title: '优化图片预处理',
    description: '建议在上传前对图片进行压缩和格式优化，可提升处理速度25%',
    impact: '速度提升25%',
    action: 'enable_preprocessing'
  },
  {
    id: 2,
    icon: 'fas fa-clock',
    title: '错峰使用',
    description: '建议在10-12点和15-17点使用，可获得更快的响应速度',
    impact: '响应时间减少30%',
    action: 'schedule_optimization'
  },
  {
    id: 3,
    icon: 'fas fa-layer-group',
    title: '批量处理',
    description: '建议使用批量识别功能，可降低单次识别成本40%',
    impact: '成本降低40%',
    action: 'batch_processing'
  }
])

function updateAnalytics() {
  message.info('正在更新分析数据...')
}

function exportReport() {
  message.success('分析报告导出中，请稍候...')
}

function applySuggestion(suggestion: any) {
  message.success(`正在应用建议: ${suggestion.title}`)
}
</script>

<style scoped>
.vip-analytics-page {
  padding: 0;
}

.page-header {
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.vip-icon {
  color: #ffd700;
  font-size: 32px;
}

.title-section p {
  font-size: 16px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.metrics-section {
  margin-bottom: 24px;
}

.metric-card {
  border-radius: 12px;
  border-left: 4px solid #1890ff;
}

.metric-trend {
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

.chart-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.chart-container {
  height: 300px;
}

.chart-placeholder {
  height: 100%;
  background: linear-gradient(135deg, #f0f2f5 0%, #fafafa 100%);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  gap: 8px;
}

.chart-placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

.chart-placeholder span {
  font-size: 16px;
  font-weight: 500;
}

.chart-description {
  font-size: 12px;
  opacity: 0.7;
}

.suggestions-card {
  border-radius: 12px;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #ffd700;
}

.suggestion-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #ffd700, #ffb300);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.suggestion-content {
  flex: 1;
}

.suggestion-content h4 {
  font-size: 16px;
  color: #262626;
  margin-bottom: 4px;
}

.suggestion-content p {
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.suggestion-impact {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .suggestion-item {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 576px) {
  .page-header {
    padding: 20px;
  }
  
  .title-section h1 {
    font-size: 24px;
  }
}
</style>
