<template>
  <div class="recognition-detail-page">
    <!-- 返回按钮 -->
    <div class="back-section">
      <a-button @click="goBack" size="large">
        <i class="fas fa-arrow-left"></i>
        返回
      </a-button>
    </div>

    <!-- 识别结果详情 -->
    <a-card class="result-card">
      <a-row :gutter="24">
        <!-- 图片区域 -->
        <a-col :xs="24" :lg="12">
          <div class="image-section">
            <div class="main-image">
              <img :src="recognition.originalImage" :alt="recognition.result" />
            </div>
            <div v-if="recognition.processedImage" class="processed-image">
              <h4>处理后图片</h4>
              <img :src="recognition.processedImage" :alt="recognition.result" />
            </div>
          </div>
        </a-col>

        <!-- 结果信息区域 -->
        <a-col :xs="24" :lg="12">
          <div class="result-info">
            <div class="result-header">
              <h1 class="result-title">{{ recognition.result }}</h1>
              <div class="result-meta">
                <a-tag :color="getCategoryColor(recognition.category)">
                  {{ recognition.category }}
                </a-tag>
                <span class="recognition-time">
                  <i class="fas fa-clock"></i>
                  {{ recognition.time }}
                </span>
              </div>
            </div>

            <!-- 置信度 -->
            <div class="confidence-section">
              <h3>识别置信度</h3>
              <div class="confidence-bar">
                <a-progress 
                  :percent="recognition.confidence" 
                  :stroke-color="getConfidenceColor(recognition.confidence)"
                  :show-info="false"
                />
                <span class="confidence-value">{{ recognition.confidence }}%</span>
              </div>
              <div class="confidence-description">
                <span v-if="recognition.confidence >= 90" class="high-confidence">
                  <i class="fas fa-check-circle"></i>
                  高置信度 - 识别结果非常可靠
                </span>
                <span v-else-if="recognition.confidence >= 70" class="medium-confidence">
                  <i class="fas fa-exclamation-circle"></i>
                  中等置信度 - 识别结果较为可靠
                </span>
                <span v-else class="low-confidence">
                  <i class="fas fa-question-circle"></i>
                  低置信度 - 识别结果仅供参考
                </span>
              </div>
            </div>

            <!-- 备选结果 -->
            <div v-if="recognition.alternatives && recognition.alternatives.length > 0" class="alternatives-section">
              <h3>备选结果</h3>
              <div class="alternatives-list">
                <div 
                  v-for="(alt, index) in recognition.alternatives" 
                  :key="index"
                  class="alternative-item"
                >
                  <span class="alt-name">{{ alt.name }}</span>
                  <div class="alt-confidence">
                    <a-progress 
                      :percent="alt.confidence" 
                      size="small"
                      :show-info="false"
                    />
                    <span class="alt-value">{{ alt.confidence }}%</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <a-button type="primary" @click="viewKnowledge">
                <i class="fas fa-book"></i>
                查看知识库
              </a-button>
              
              <a-button @click="toggleFavorite" :type="isFavorited ? 'primary' : 'default'">
                <i class="fas fa-heart"></i>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </a-button>
              
              <a-button @click="shareResult">
                <i class="fas fa-share"></i>
                分享
              </a-button>
              
              <a-dropdown>
                <a-button>
                  <i class="fas fa-ellipsis-h"></i>
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="downloadImage">
                      <i class="fas fa-download"></i>
                      下载图片
                    </a-menu-item>
                    <a-menu-item @click="reRecognize">
                      <i class="fas fa-redo"></i>
                      重新识别
                    </a-menu-item>
                    <a-menu-item @click="deleteRecord" class="danger-item">
                      <i class="fas fa-trash"></i>
                      删除记录
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 技术详情 -->
    <a-card title="技术详情" class="tech-details-card">
      <a-descriptions bordered>
        <a-descriptions-item label="识别模型">
          {{ recognition.model || 'ResNet-50' }}
        </a-descriptions-item>
        <a-descriptions-item label="处理时间">
          {{ recognition.processTime || '2.5s' }}
        </a-descriptions-item>
        <a-descriptions-item label="图片尺寸">
          {{ recognition.imageSize || '1920x1080' }}
        </a-descriptions-item>
        <a-descriptions-item label="文件大小">
          {{ recognition.fileSize || '2.5MB' }}
        </a-descriptions-item>
        <a-descriptions-item label="文件格式">
          {{ recognition.format || 'JPEG' }}
        </a-descriptions-item>
        <a-descriptions-item label="识别引擎">
          {{ recognition.engine || 'TensorFlow 2.0' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 相关标签 -->
    <a-card v-if="recognition.tags && recognition.tags.length > 0" title="相关标签" class="tags-card">
      <div class="tags-list">
        <a-tag 
          v-for="tag in recognition.tags" 
          :key="tag"
          color="blue"
          class="result-tag"
          @click="searchByTag(tag)"
        >
          {{ tag }}
        </a-tag>
      </div>
    </a-card>

    <!-- 相关识别记录 -->
    <a-card title="相关识别记录" class="related-card">
      <div class="related-list">
        <div 
          v-for="related in relatedRecognitions" 
          :key="related.id"
          class="related-item"
          @click="viewRelated(related)"
        >
          <img :src="related.thumbnail" :alt="related.result" />
          <div class="related-content">
            <h4>{{ related.result }}</h4>
            <div class="related-meta">
              <span class="related-confidence">{{ related.confidence }}%</span>
              <span class="related-time">{{ related.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 识别历史 -->
    <a-card title="识别历史趋势" class="history-chart-card">
      <div class="chart-placeholder">
        <i class="fas fa-chart-line"></i>
        <p>识别历史趋势图表</p>
        <p class="chart-desc">显示最近30天的识别活动情况</p>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()

// 识别详情数据
const recognition = ref({
  id: 1,
  result: '金毛寻回犬',
  category: '动物·犬类',
  confidence: 95,
  time: '2小时前',
  originalImage: '/api/placeholder/400/300',
  processedImage: '/api/placeholder/400/300',
  model: 'ResNet-50 深度神经网络',
  processTime: '2.847s',
  imageSize: '1920x1080',
  fileSize: '2.5MB',
  format: 'JPEG',
  engine: 'TensorFlow 2.0',
  alternatives: [
    { name: '拉布拉多犬', confidence: 78 },
    { name: '萨摩耶犬', confidence: 65 },
    { name: '边境牧羊犬', confidence: 52 }
  ],
  tags: ['宠物', '犬类', '金毛', '温顺', '大型犬']
})

// 相关识别记录
const relatedRecognitions = ref([
  {
    id: 2,
    result: '拉布拉多犬',
    confidence: 92,
    time: '1天前',
    thumbnail: '/api/placeholder/100/100'
  },
  {
    id: 3,
    result: '边境牧羊犬',
    confidence: 88,
    time: '2天前',
    thumbnail: '/api/placeholder/100/100'
  },
  {
    id: 4,
    result: '萨摩耶犬',
    confidence: 85,
    time: '3天前',
    thumbnail: '/api/placeholder/100/100'
  }
])

// 状态
const isFavorited = ref(false)

// 初始化
onMounted(() => {
  // 从路由参数获取识别记录ID
  const recognitionId = route.params.id
  if (recognitionId) {
    loadRecognitionDetail(recognitionId)
  }
})

// 方法
function goBack() {
  // 根据来源页面决定返回位置
  const from = route.query.from as string
  if (from === 'dashboard') {
    router.push('/user/dashboard')
  } else if (from === 'recognition') {
    router.push('/user/recognition')
  } else {
    router.push('/user/history')
  }
}

function loadRecognitionDetail(recognitionId: string | number) {
  // 这里应该调用API获取识别详情
  console.log('Loading recognition detail for ID:', recognitionId)
  
  // 模拟根据ID加载不同的数据
  const mockData = {
    1: {
      result: '金毛寻回犬',
      category: '动物·犬类',
      confidence: 95
    },
    2: {
      result: '玫瑰花',
      category: '植物·花卉',
      confidence: 88
    },
    3: {
      result: '苹果',
      category: '食物·水果',
      confidence: 92
    }
  }
  
  const data = mockData[recognitionId as keyof typeof mockData]
  if (data) {
    Object.assign(recognition.value, data)
  }
}

function getCategoryColor(category: string) {
  const colors: Record<string, string> = {
    '动物·犬类': 'green',
    '植物·花卉': 'blue',
    '食物·水果': 'orange',
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange'
  }
  return colors[category] || 'default'
}

function getConfidenceColor(confidence: number) {
  if (confidence >= 90) return '#52c41a'
  if (confidence >= 70) return '#faad14'
  return '#ff4d4f'
}

function viewKnowledge() {
  router.push('/user/knowledge')
}

function toggleFavorite() {
  isFavorited.value = !isFavorited.value
  message.success(isFavorited.value ? '已添加到收藏' : '已从收藏移除')
}

function shareResult() {
  message.info('分享功能开发中')
}

function downloadImage() {
  message.info('下载功能开发中')
}

function reRecognize() {
  message.info('重新识别功能开发中')
}

function deleteRecord() {
  message.info('删除记录功能开发中')
}

function searchByTag(tag: string) {
  router.push(`/user/history?tag=${encodeURIComponent(tag)}`)
}

function viewRelated(related: any) {
  router.push(`/user/recognition/${related.id}`)
}
</script>

<style scoped>
.recognition-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 返回按钮 */
.back-section {
  margin-bottom: 16px;
}

/* 结果卡片 */
.result-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 图片区域 */
.image-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-image,
.processed-image {
  text-align: center;
}

.main-image img,
.processed-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.processed-image h4 {
  color: #262626;
  margin-bottom: 12px;
  font-size: 16px;
}

/* 结果信息 */
.result-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.result-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-title {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin: 0;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.recognition-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.recognition-time i {
  color: #1890ff;
}

/* 置信度区域 */
.confidence-section h3 {
  color: #262626;
  margin-bottom: 16px;
  font-size: 18px;
}

.confidence-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.confidence-bar .ant-progress {
  flex: 1;
}

.confidence-value {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  min-width: 50px;
}

.confidence-description {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.high-confidence {
  color: #52c41a;
}

.medium-confidence {
  color: #faad14;
}

.low-confidence {
  color: #ff4d4f;
}

.confidence-description i {
  font-size: 16px;
}

/* 备选结果 */
.alternatives-section h3 {
  color: #262626;
  margin-bottom: 16px;
  font-size: 18px;
}

.alternatives-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alternative-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.alt-name {
  min-width: 100px;
  color: #262626;
  font-weight: 500;
}

.alt-confidence {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.alt-value {
  min-width: 40px;
  font-size: 12px;
  color: #666;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 技术详情卡片 */
.tech-details-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 标签卡片 */
.tags-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.result-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.result-tag:hover {
  transform: translateY(-1px);
}

/* 相关记录卡片 */
.related-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.related-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.related-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  background: #fafafa;
  transform: translateY(-1px);
}

.related-item img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.related-content {
  flex: 1;
}

.related-content h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 14px;
}

.related-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.related-confidence {
  color: #1890ff;
  font-weight: 500;
  font-size: 12px;
}

.related-time {
  color: #999;
  font-size: 12px;
}

/* 历史图表卡片 */
.history-chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.chart-placeholder {
  height: 200px;
  background: #f5f5f5;
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
  color: #1890ff;
}

.chart-placeholder p {
  margin: 0;
  font-size: 16px;
}

.chart-desc {
  font-size: 12px !important;
  color: #999 !important;
}

/* 危险操作 */
.danger-item {
  color: #ff4d4f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .recognition-detail-page {
    padding: 16px;
  }
  
  .result-title {
    font-size: 24px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .ant-btn {
    width: 100%;
  }
  
  .related-list {
    grid-template-columns: 1fr;
  }
  
  .alternative-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .alt-confidence {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .recognition-detail-page {
    padding: 12px;
  }
  
  .result-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .confidence-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .confidence-value {
    text-align: center;
  }
}
</style>
