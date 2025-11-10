<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '16px', borderRadius: '8px', textAlign: 'center' }">
      <h1 :style="{ fontSize: '24px', fontWeight: '600', margin: '0 0 8px 0' }">批量识别</h1>
      <p :style="{ fontSize: '14px', margin: 0, opacity: 0.65 }">一次上传多张图片，批量进行AI智能识别</p>
    </a-card>

    <!-- 上传区域 -->
    <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="images"
        :multiple="true"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        accept="image/*"
      >
        <div>
          <div :style="{ fontSize: '48px', color: '#1890ff', marginBottom: '16px' }">
            <i class="fas fa-images"></i>
          </div>
          <h3 :style="{ fontSize: '18px', fontWeight: '600', marginBottom: '8px' }">批量上传图片</h3>
          <p :style="{ fontSize: '14px', opacity: 0.65, marginBottom: '12px' }">支持同时上传多张图片，JPG、PNG、GIF 格式，单个文件不超过 10MB</p>
          <div :style="{ fontSize: '13px', opacity: 0.65 }">
            <span>已选择 {{ fileList.length }} 张图片</span>
            <span v-if="uploadedCount > 0" :style="{ marginLeft: '12px' }">已处理 {{ uploadedCount }} 张</span>
          </div>
        </div>
      </a-upload-dragger>

      <!-- 批量操作 -->
      <div v-if="fileList.length > 0" :style="{ marginTop: '16px' }">
        <div :style="{ display: 'flex', gap: '8px', marginBottom: '16px', flexWrap: 'wrap' }">
          <a-button 
            type="primary"
            :loading="processing"
            @click="startBatchRecognition"
            :disabled="fileList.length === 0"
          >
            <template #icon>
              <i class="fas fa-play"></i>
            </template>
            开始批量识别
          </a-button>
          <a-button @click="clearAll" :disabled="processing">
            <template #icon>
              <i class="fas fa-trash"></i>
            </template>
            清空列表
          </a-button>
          <a-button v-if="results.length > 0" @click="exportResults">
            <template #icon>
              <i class="fas fa-download"></i>
            </template>
            导出结果
          </a-button>
        </div>
        
        <!-- 进度条 -->
        <div v-if="processing" :style="{ padding: '12px', background: '#fafafa', borderRadius: '8px' }">
          <div :style="{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', fontSize: '13px' }">
            <span>处理进度：{{ uploadedCount }} / {{ fileList.length }}</span>
            <span :style="{ fontWeight: '600', color: '#1890ff' }">{{ Math.round((uploadedCount / fileList.length) * 100) }}%</span>
          </div>
          <a-progress 
            :percent="Math.round((uploadedCount / fileList.length) * 100)"
            :show-info="false"
            stroke-color="#1890ff"
          />
        </div>
      </div>
    </a-card>

    <!-- 图片列表 -->
    <a-card v-if="fileList.length > 0" :style="{ borderRadius: '8px', marginTop: '16px' }">
      <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '16px' }">
        <div 
          v-for="(file, index) in fileList" 
          :key="file.uid"
          :style="{ 
            borderRadius: '8px', 
            overflow: 'hidden', 
            border: '1px solid #e8e8e8',
            background: isCompleted(index) ? '#f6ffed' : isProcessing(index) ? '#e6f7ff' : 'white'
          }"
        >
          <div :style="{ position: 'relative', paddingBottom: '56.25%', background: '#f5f5f5' }">
            <img :src="getPreviewUrl(file)" :alt="file.name" :style="{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }" />
            <div :style="{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', opacity: isProcessing(index) || isCompleted(index) ? 1 : 0, transition: 'opacity 0.3s' }">
              <div v-if="isProcessing(index)" :style="{ textAlign: 'center', color: 'white' }">
                <a-spin size="large" />
                <div :style="{ marginTop: '8px' }">识别中...</div>
              </div>
              <div v-else-if="isCompleted(index)" :style="{ textAlign: 'center', color: 'white', fontSize: '24px' }">
                <i class="fas fa-check-circle" :style="{ color: '#52c41a' }"></i>
                <div :style="{ marginTop: '8px', fontSize: '14px' }">已完成</div>
              </div>
              <div v-else :style="{ textAlign: 'center', color: 'white' }">
                <i class="fas fa-clock"></i>
                <div :style="{ marginTop: '8px', fontSize: '14px' }">待处理</div>
              </div>
            </div>
          </div>
          
          <div :style="{ padding: '12px' }">
            <div :style="{ fontSize: '13px', fontWeight: '500', marginBottom: '4px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }">{{ file.name }}</div>
            <div :style="{ fontSize: '12px', opacity: 0.65 }">{{ formatFileSize(file.size) }}</div>
          </div>

          <!-- 识别结果 -->
          <div v-if="results[index]" :style="{ padding: '12px', borderTop: '1px solid #e8e8e8', background: '#fafafa' }">
            <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }">
              <span :style="{ fontSize: '14px', fontWeight: '600' }">{{ results[index].name }}</span>
              <span :style="{ fontSize: '13px', color: '#1890ff', fontWeight: '600' }">{{ results[index].confidence }}%</span>
            </div>
            <div :style="{ display: 'flex', gap: '8px' }">
              <a-button type="link" size="small" @click="viewDetail(index)" :style="{ padding: 0 }">
                详情
              </a-button>
              <a-button type="link" size="small" @click="addToFavorites(index)" :style="{ padding: 0 }">
                收藏
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 批量结果统计 -->
    <a-card v-if="results.length > 0" title="识别结果统计" :style="{ borderRadius: '8px', marginTop: '16px' }">
        <a-row :gutter="[24, 24]">
          <a-col :xs="24" :sm="8">
            <a-statistic
              title="总图片数"
              :value="fileList.length"
              suffix="张"
            />
          </a-col>
          <a-col :xs="24" :sm="8">
            <a-statistic
              title="识别成功"
              :value="successCount"
              suffix="张"
              value-style="color: #52c41a"
            />
          </a-col>
          <a-col :xs="24" :sm="8">
            <a-statistic
              title="平均置信度"
              :value="averageConfidence"
              suffix="%"
              :precision="1"
              value-style="color: #1890ff"
            />
          </a-col>
        </a-row>

        <!-- 类别分布 -->
        <div :style="{ marginTop: '24px' }">
          <h4 :style="{ fontSize: '15px', fontWeight: '600', marginBottom: '12px' }">识别类别分布：</h4>
          <div :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }">
            <a-tag 
              v-for="(count, category) in categoryStats" 
              :key="category"
              color="blue"
              :style="{ fontSize: '13px', padding: '4px 12px' }"
            >
              {{ category }}: {{ count }}张
            </a-tag>
          </div>
        </div>
      </a-card>

    <!-- 历史记录 -->
    <a-card title="批量识别历史" :style="{ borderRadius: '8px', marginTop: '16px' }">
      <a-table 
        :columns="historyColumns" 
        :data-source="batchHistory"
        :pagination="{ pageSize: 5 }"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="loadHistoryBatch(record)">
              查看详情
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'
import { RecognitionAPI } from '@/api/recognition'

const fileList = ref<UploadFile[]>([])
const results = ref<any[]>([])
const processing = ref(false)
const uploadedCount = ref(0)
const currentProcessingIndex = ref(-1)

// 历史记录表格列
const historyColumns = [
  { title: '批次时间', dataIndex: 'time', key: 'time' },
  { title: '图片数量', dataIndex: 'count', key: 'count' },
  { title: '成功率', dataIndex: 'successRate', key: 'successRate' },
  { title: '操作', key: 'action' }
]

// 批量历史记录
const batchHistory = ref<any[]>([])

// 计算属性
const successCount = computed(() => results.value.length)

const averageConfidence = computed(() => {
  if (results.value.length === 0) return 0
  const total = results.value.reduce((sum, result) => sum + result.confidence, 0)
  return total / results.value.length
})

const categoryStats = computed(() => {
  const stats: Record<string, number> = {}
  results.value.forEach(result => {
    stats[result.category] = (stats[result.category] || 0) + 1
  })
  return stats
})

// 文件上传前验证
function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('图片大小不能超过 10MB!')
    return false
  }
  
  if (fileList.value.length >= 50) {
    message.error('最多只能上传50张图片!')
    return false
  }
  
  return false // 阻止自动上传
}

// 文件变化处理
function handleFileChange(info: any) {
  // fileList 会自动更新
}

// 获取预览URL
function getPreviewUrl(file: UploadFile) {
  if (file.originFileObj) {
    return URL.createObjectURL(file.originFileObj)
  }
  return ''
}

// 格式化文件大小
function formatFileSize(bytes?: number) {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

// 检查是否正在处理
function isProcessing(index: number) {
  return processing.value && currentProcessingIndex.value === index
}

// 检查是否已完成
function isCompleted(index: number) {
  return results.value[index] !== undefined
}

// 开始批量识别
async function startBatchRecognition() {
  if (fileList.value.length === 0) {
    message.warning('请先上传图片')
    return
  }

  if (fileList.value.length > 10) {
    message.warning('批量识别最多支持10张图片')
    return
  }

  processing.value = true
  uploadedCount.value = 0
  results.value = []
  currentProcessingIndex.value = 0

  try {
    // 准备文件数组
    const files: File[] = fileList.value.map(f => f.originFileObj as File).filter(f => f)
    
    if (files.length === 0) {
      message.error('没有有效的文件')
      return
    }

    // 调用批量识别API
    const batchResults = await RecognitionAPI.batchRecognize(files, 0)
    
    if (batchResults && Array.isArray(batchResults)) {
      // 处理每个结果
      results.value = batchResults.map((result: any) => {
        const confidencePercent = result.confidence 
          ? Math.round(Number(result.confidence) * 100) 
          : 0
        
        return {
          id: result.id,
          name: result.mainCategory || result.category || '未知',
          category: result.mainCategory || result.category || '未分类',
          confidence: confidencePercent,
          status: result.status,
          errorMessage: result.errorMessage
        }
      })
      
      uploadedCount.value = results.value.length
      
      const successCount = results.value.filter(r => r.status === 1).length
      message.success(`批量识别完成！成功识别 ${successCount}/${files.length} 张图片`)
      
      // 刷新历史记录
      await loadBatchHistory()
    }
  } catch (error: any) {
    console.error('批量识别失败:', error)
    message.error(error.message || '批量识别过程中出现错误')
  } finally {
    processing.value = false
    currentProcessingIndex.value = -1
  }
}

// 加载批量历史记录
async function loadBatchHistory() {
  try {
    const response = await RecognitionAPI.getHistory({ page: 1, size: 10 })
    
          if (response && response.data) {
            // 按时间分组统计批次
            const batchMap = new Map<string, any>()

            response.data.forEach((item: any) => {
        const date = new Date(item.createdAt || item.createTime)
        const timeKey = date.toISOString().slice(0, 16) // 精确到分钟
        
        if (!batchMap.has(timeKey)) {
          batchMap.set(timeKey, {
            id: timeKey,
            time: date.toLocaleString(),
            items: [],
            successCount: 0
          })
        }
        
        const batch = batchMap.get(timeKey)
        batch.items.push(item)
        if (item.status === 1) {
          batch.successCount++
        }
      })
      
      // 转换为历史记录列表
      batchHistory.value = Array.from(batchMap.values()).map(batch => ({
        id: batch.id,
        time: batch.time,
        count: batch.items.length,
        successRate: batch.items.length > 0 
          ? `${Math.round((batch.successCount / batch.items.length) * 100)}%`
          : '0%'
      }))
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
  }
}

// 清空列表
function clearAll() {
  fileList.value = []
  results.value = []
  uploadedCount.value = 0
}

// 导出结果
function exportResults() {
  if (results.value.length === 0) {
    message.warning('没有可导出的结果')
    return
  }

  // 生成CSV内容
  const headers = ['序号', '识别结果', '分类', '置信度(%)', '状态']
  const rows = results.value.map((result, index) => [
    index + 1,
    result.name,
    result.category,
    result.confidence,
    result.status === 1 ? '成功' : '失败'
  ])
  
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n')
  
  // 下载CSV
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', `batch_recognition_${new Date().getTime()}.csv`)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  message.success('导出成功')
}

// 查看详情
function viewDetail(index: number) {
  const result = results.value[index]
  if (result && result.id) {
    // 跳转到识别详情页
    window.open(`/user/recognition/${result.id}`, '_blank')
  } else {
    message.warning('无法查看详情')
  }
}

// 添加到收藏
function addToFavorites(index: number) {
  // TODO: 实现收藏功能
  message.success('已添加到收藏')
}

// 加载历史批次
function loadHistoryBatch(record: any) {
  message.info(`加载批次：${record.time}`)
}

// 页面加载时获取历史记录
onMounted(() => {
  loadBatchHistory()
})
</script>

