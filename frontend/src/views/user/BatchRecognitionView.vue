<template>
  <div class="batch-recognition-page">
    <div class="page-header">
      <h1>批量识别</h1>
      <p>一次上传多张图片，批量进行AI智能识别</p>
    </div>

    <!-- 上传区域 -->
    <a-card class="upload-card">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="images"
        :multiple="true"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        class="upload-dragger"
        accept="image/*"
      >
        <div class="upload-content">
          <div class="upload-icon">
            <i class="fas fa-images"></i>
          </div>
          <h3>批量上传图片</h3>
          <p>支持同时上传多张图片，JPG、PNG、GIF 格式，单个文件不超过 10MB</p>
          <div class="upload-stats">
            <span>已选择 {{ fileList.length }} 张图片</span>
            <span v-if="uploadedCount > 0">已处理 {{ uploadedCount }} 张</span>
          </div>
        </div>
      </a-upload-dragger>

      <!-- 批量操作 -->
      <div v-if="fileList.length > 0" class="batch-actions">
        <div class="action-buttons">
          <a-button 
            type="primary" 
            size="large"
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
        <div v-if="processing" class="progress-section">
          <div class="progress-info">
            <span>处理进度：{{ uploadedCount }} / {{ fileList.length }}</span>
            <span>{{ Math.round((uploadedCount / fileList.length) * 100) }}%</span>
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
    <div v-if="fileList.length > 0" class="images-grid">
      <div 
        v-for="(file, index) in fileList" 
        :key="file.uid"
        class="image-item"
        :class="{ 'processing': isProcessing(index), 'completed': isCompleted(index) }"
      >
        <div class="image-preview">
          <img :src="getPreviewUrl(file)" :alt="file.name" />
          <div class="image-overlay">
            <div v-if="isProcessing(index)" class="processing-indicator">
              <a-spin size="large" />
              <span>识别中...</span>
            </div>
            <div v-else-if="isCompleted(index)" class="completed-indicator">
              <i class="fas fa-check-circle"></i>
              <span>已完成</span>
            </div>
            <div v-else class="pending-indicator">
              <i class="fas fa-clock"></i>
              <span>待处理</span>
            </div>
          </div>
        </div>
        
        <div class="image-info">
          <div class="file-name">{{ file.name }}</div>
          <div class="file-size">{{ formatFileSize(file.size) }}</div>
        </div>

        <!-- 识别结果 -->
        <div v-if="results[index]" class="recognition-result">
          <div class="result-main">
            <span class="result-name">{{ results[index].name }}</span>
            <span class="result-confidence">{{ results[index].confidence }}%</span>
          </div>
          <div class="result-actions">
            <a-button type="link" size="small" @click="viewDetail(index)">
              详情
            </a-button>
            <a-button type="link" size="small" @click="addToFavorites(index)">
              收藏
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 批量结果统计 -->
    <div v-if="results.length > 0" class="results-summary">
      <a-card title="识别结果统计" class="summary-card">
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
        <div class="category-distribution">
          <h4>识别类别分布：</h4>
          <div class="category-list">
            <a-tag 
              v-for="(count, category) in categoryStats" 
              :key="category"
              color="blue"
              class="category-tag"
            >
              {{ category }}: {{ count }}张
            </a-tag>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 历史记录 -->
    <a-card title="批量识别历史" class="history-card">
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
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'

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
const batchHistory = ref([
  { 
    id: 1, 
    time: '2025-03-20 14:30', 
    count: 15, 
    successRate: '93.3%' 
  },
  { 
    id: 2, 
    time: '2025-03-19 10:15', 
    count: 8, 
    successRate: '100%' 
  }
])

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

  processing.value = true
  uploadedCount.value = 0
  results.value = []
  currentProcessingIndex.value = 0

  const mockResults = [
    { name: '金毛犬', confidence: 95, category: '动物' },
    { name: '玫瑰花', confidence: 88, category: '植物' },
    { name: '苹果', confidence: 92, category: '食物' },
    { name: '汽车', confidence: 89, category: '交通工具' },
    { name: '建筑', confidence: 85, category: '建筑' }
  ]

  try {
    for (let i = 0; i < fileList.value.length; i++) {
      currentProcessingIndex.value = i
      
      // 模拟识别过程
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      // 随机选择一个结果
      const result = mockResults[Math.floor(Math.random() * mockResults.length)]
      results.value[i] = {
        ...result,
        confidence: Math.floor(Math.random() * 20) + 80 // 80-100%
      }
      
      uploadedCount.value++
    }
    
    message.success(`批量识别完成！成功识别 ${results.value.length} 张图片`)
  } catch (error) {
    message.error('批量识别过程中出现错误')
  } finally {
    processing.value = false
    currentProcessingIndex.value = -1
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
  message.info('导出功能开发中')
}

// 查看详情
function viewDetail(index: number) {
  message.info(`查看第 ${index + 1} 张图片的详细结果`)
}

// 添加到收藏
function addToFavorites(index: number) {
  message.success('已添加到收藏')
}

// 加载历史批次
function loadHistoryBatch(record: any) {
  message.info(`加载批次：${record.time}`)
}
</script>

<style scoped>
.batch-recognition-page {
  padding: 0;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 16px;
  color: #666;
}

/* 上传区域 */
.upload-card {
  border-radius: 12px;
  margin-bottom: 32px;
}

.upload-dragger {
  border-radius: 12px !important;
  border: 2px dashed #d9d9d9 !important;
  background: #fafafa !important;
}

.upload-dragger:hover {
  border-color: #1890ff !important;
}

.upload-content {
  padding: 40px;
  text-align: center;
}

.upload-icon {
  font-size: 64px;
  color: #1890ff;
  margin-bottom: 16px;
}

.upload-content h3 {
  font-size: 18px;
  color: #262626;
  margin-bottom: 8px;
}

.upload-content p {
  color: #666;
  margin-bottom: 16px;
}

.upload-stats {
  display: flex;
  justify-content: center;
  gap: 24px;
  font-size: 14px;
  color: #1890ff;
}

/* 批量操作 */
.batch-actions {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.progress-section {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

/* 图片网格 */
.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.image-item {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.image-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.image-item.processing {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.image-item.completed {
  border-color: #52c41a;
}

.image-preview {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item.processing .image-overlay,
.image-item.completed .image-overlay {
  opacity: 1;
}

.processing-indicator,
.completed-indicator,
.pending-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.completed-indicator i {
  font-size: 32px;
  color: #52c41a;
}

.pending-indicator i {
  font-size: 24px;
  color: #faad14;
}

.image-info {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.file-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #999;
}

.recognition-result {
  padding: 12px;
}

.result-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.result-name {
  font-weight: 500;
  color: #262626;
}

.result-confidence {
  color: #52c41a;
  font-weight: 500;
}

.result-actions {
  display: flex;
  gap: 8px;
}

/* 结果统计 */
.results-summary {
  margin-bottom: 32px;
}

.summary-card {
  border-radius: 12px;
}

.category-distribution {
  margin-top: 24px;
}

.category-distribution h4 {
  color: #262626;
  margin-bottom: 12px;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  margin: 0;
}

/* 历史记录 */
.history-card {
  border-radius: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .images-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .upload-content {
    padding: 24px;
  }
  
  .upload-icon {
    font-size: 48px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .upload-stats {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 576px) {
  .images-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
}
</style>
