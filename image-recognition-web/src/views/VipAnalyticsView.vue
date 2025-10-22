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
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="总识别次数"
            :value="metrics.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-up"></i>
            <span :style="{ marginLeft: '4px' }">+23.5%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="平均精度"
            :value="metrics.averageAccuracy"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#52c41a' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-up"></i>
            <span :style="{ marginLeft: '4px' }">+2.1%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="处理速度"
            :value="metrics.avgProcessTime"
            suffix="ms"
            :value-style="{ color: '#faad14' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-down"></i>
            <span :style="{ marginLeft: '4px' }">-15.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="节省成本"
            :value="metrics.costSaved"
            prefix="¥"
            :value-style="{ color: '#f5222d' }"
          />
          <div :style="{ marginTop: '8px', fontSize: '12px', color: '#52c41a' }">
            <i class="fas fa-arrow-up"></i>
            <span :style="{ marginLeft: '4px' }">+45.8%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表分析 -->
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '16px' }">
      <a-col :xs="24" :lg="12">
        <a-card title="识别趋势分析" :style="{ borderRadius: '8px' }">
          <div :style="{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', background: '#fafafa', borderRadius: '8px' }">
            <i class="fas fa-chart-area" :style="{ fontSize: '48px', color: '#1890ff', marginBottom: '16px' }"></i>
            <div :style="{ fontSize: '16px', fontWeight: '500', marginBottom: '8px' }">识别次数趋势图</div>
            <div :style="{ fontSize: '13px', opacity: 0.65 }">显示过去30天的识别活动趋势</div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="准确率分布" :style="{ borderRadius: '8px' }">
          <div :style="{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', background: '#fafafa', borderRadius: '8px' }">
            <i class="fas fa-chart-pie" :style="{ fontSize: '48px', color: '#52c41a', marginBottom: '16px' }"></i>
            <div :style="{ fontSize: '16px', fontWeight: '500', marginBottom: '8px' }">准确率分布图</div>
            <div :style="{ fontSize: '13px', opacity: 0.65 }">按准确率区间统计识别结果</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 使用建议 -->
    <a-card title="智能优化建议" :style="{ borderRadius: '8px' }">
      <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
        <div 
          v-for="suggestion in suggestions" 
          :key="suggestion.id"
          :style="{ 
            display: 'flex',
            alignItems: 'center',
            padding: '16px',
            borderRadius: '8px',
            border: '1px solid #d9d9d9',
            background: '#fafafa',
            gap: '16px'
          }"
        >
          <div :style="{ 
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '20px',
            color: '#fff',
            flexShrink: 0
          }">
            <i :class="suggestion.icon"></i>
          </div>
          <div :style="{ flex: 1 }">
            <h4 :style="{ margin: '0 0 6px 0', fontSize: '15px', fontWeight: '600' }">{{ suggestion.title }}</h4>
            <p :style="{ margin: '0 0 6px 0', fontSize: '13px', opacity: 0.75 }">{{ suggestion.description }}</p>
            <div :style="{ fontSize: '12px', color: '#52c41a', fontWeight: '500' }">
              <i class="fas fa-chart-line" :style="{ marginRight: '4px' }"></i>
              预期改善: {{ suggestion.impact }}
            </div>
          </div>
          <a-button type="primary" size="small" @click="applySuggestion(suggestion)">
            应用建议
          </a-button>
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
