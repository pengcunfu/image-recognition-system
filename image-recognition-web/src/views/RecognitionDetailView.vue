<template>
  <div :style="{ padding: '24px' }">
    <!-- 返回按钮 -->
    <div :style="{ marginBottom: '16px' }">
      <a-button @click="goBack">
        <i class="fas fa-arrow-left" :style="{ marginRight: '8px' }"></i>
        返回
      </a-button>
    </div>

    <!-- 加载状态 -->
    <a-spin :spinning="loading" tip="加载中...">
      <!-- 识别结果详情 -->
      <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-row :gutter="24">
        <!-- 图片区域 -->
        <a-col :xs="24" :lg="12">
          <div>
            <div :style="{ borderRadius: '8px', overflow: 'hidden', marginBottom: '12px' }">
              <img :src="recognition.originalImage" :alt="recognition.result" :style="{ width: '100%', height: 'auto' }" />
            </div>
            <div v-if="recognition.processedImage">
              <h4 :style="{ fontSize: '15px', fontWeight: '600', marginBottom: '8px' }">处理后图片</h4>
              <div :style="{ borderRadius: '8px', overflow: 'hidden' }">
                <img :src="recognition.processedImage" :alt="recognition.result" :style="{ width: '100%', height: 'auto' }" />
              </div>
            </div>
          </div>
        </a-col>

        <!-- 结果信息区域 -->
        <a-col :xs="24" :lg="12">
          <div>
            <div :style="{ marginBottom: '16px' }">
              <h1 :style="{ fontSize: '24px', fontWeight: '600', margin: '0 0 12px 0' }">{{ recognition.result }}</h1>
              <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
                <a-tag :color="getCategoryColor(recognition.category)">
                  {{ recognition.category }}
                </a-tag>
                <span :style="{ fontSize: '13px', opacity: 0.65 }">
                  <i class="fas fa-clock" :style="{ marginRight: '4px' }"></i>
                  {{ recognition.time }}
                </span>
              </div>
            </div>

            <!-- 置信度 -->
            <div :style="{ marginBottom: '16px' }">
              <h3 :style="{ fontSize: '16px', fontWeight: '600', marginBottom: '12px' }">识别置信度</h3>
              <div :style="{ marginBottom: '8px' }">
                <a-progress 
                  :percent="recognition.confidence" 
                  :stroke-color="getConfidenceColor(recognition.confidence)"
                  :show-info="false"
                />
                <span :style="{ fontSize: '20px', fontWeight: 'bold', color: getConfidenceColor(recognition.confidence) }">{{ recognition.confidence }}%</span>
              </div>
              <div>
                <span v-if="recognition.confidence >= 90" :style="{ fontSize: '13px', color: '#52c41a' }">
                  <i class="fas fa-check-circle" :style="{ marginRight: '4px' }"></i>
                  高置信度 - 识别结果非常可靠
                </span>
                <span v-else-if="recognition.confidence >= 70" :style="{ fontSize: '13px', color: '#faad14' }">
                  <i class="fas fa-exclamation-circle" :style="{ marginRight: '4px' }"></i>
                  中等置信度 - 识别结果较为可靠
                </span>
                <span v-else :style="{ fontSize: '13px', color: '#ff4d4f' }">
                  <i class="fas fa-question-circle" :style="{ marginRight: '4px' }"></i>
                  低置信度 - 识别结果仅供参考
                </span>
              </div>
            </div>

            <!-- 备选结果 -->
            <div v-if="recognition.alternatives && recognition.alternatives.length > 0" :style="{ marginBottom: '16px' }">
              <h3 :style="{ fontSize: '16px', fontWeight: '600', marginBottom: '12px' }">备选结果</h3>
              <div :style="{ display: 'flex', flexDirection: 'column', gap: '8px' }">
                <div 
                  v-for="(alt, index) in recognition.alternatives" 
                  :key="index"
                  :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px', background: '#fafafa', borderRadius: '6px' }"
                >
                  <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ alt.name }}</span>
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', minWidth: '120px' }">
                    <a-progress 
                      :percent="alt.confidence" 
                      size="small"
                      :show-info="false"
                      :style="{ flex: 1 }"
                    />
                    <span :style="{ fontSize: '13px', opacity: 0.65, minWidth: '40px', textAlign: 'right' }">{{ alt.confidence }}%</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 识别描述 -->
            <div v-if="recognition.description" :style="{ padding: '16px', borderRadius: '8px', background: '#f6ffed', border: '1px solid #b7eb8f', marginBottom: '16px' }">
              <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '500', color: '#52c41a' }">
                <i class="fas fa-lightbulb" :style="{ marginRight: '6px' }"></i>
                识别描述
              </h4>
              <p :style="{ margin: 0, fontSize: '14px', lineHeight: '1.6', color: '#262626' }">
                {{ recognition.description }}
              </p>
            </div>

            <!-- 操作按钮 -->
            <div :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }">
              <a-button type="primary" @click="viewKnowledge">
                <i class="fas fa-book" :style="{ marginRight: '4px' }"></i>
                查看知识库
              </a-button>
              
              <a-button @click="toggleFavorite" :type="isFavorited ? 'primary' : 'default'">
                <i class="fas fa-heart" :style="{ marginRight: '4px' }"></i>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </a-button>
              
              <a-button @click="shareResult">
                <i class="fas fa-share" :style="{ marginRight: '4px' }"></i>
                分享
              </a-button>
              
              <a-dropdown>
                <a-button>
                  <i class="fas fa-ellipsis-h"></i>
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="downloadImage">
                      <i class="fas fa-download" :style="{ marginRight: '8px' }"></i>
                      下载图片
                    </a-menu-item>
                    <a-menu-item @click="reRecognize">
                      <i class="fas fa-redo" :style="{ marginRight: '8px' }"></i>
                      重新识别
                    </a-menu-item>
                    <a-menu-item @click="deleteRecord" danger>
                      <i class="fas fa-trash" :style="{ marginRight: '8px' }"></i>
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
    <a-card title="技术详情" :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-descriptions bordered size="small">
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
    <a-card v-if="recognition.tags && recognition.tags.length > 0" title="相关标签" :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <div :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }">
        <a-tag 
          v-for="tag in recognition.tags" 
          :key="tag"
          color="blue"
          :style="{ cursor: 'pointer', fontSize: '13px' }"
          @click="searchByTag(tag)"
        >
          {{ tag }}
        </a-tag>
      </div>
    </a-card>

    <!-- 相关识别记录 -->
    <a-card title="相关识别记录" :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
        <div 
          v-for="related in relatedRecognitions" 
          :key="related.id"
          :style="{ 
            display: 'flex', 
            gap: '12px', 
            padding: '12px', 
            borderRadius: '8px', 
            cursor: 'pointer', 
            transition: 'all 0.3s',
            border: '1px solid #e8e8e8'
          }"
          @click="viewRelated(related)"
          @mouseenter="(e) => { const el = e.currentTarget as HTMLElement; if (el) { el.style.background = '#fafafa'; el.style.borderColor = '#1890ff'; } }"
          @mouseleave="(e) => { const el = e.currentTarget as HTMLElement; if (el) { el.style.background = 'white'; el.style.borderColor = '#e8e8e8'; } }"
        >
          <img :src="related.thumbnail" :alt="related.result" :style="{ width: '80px', height: '80px', objectFit: 'cover', borderRadius: '6px' }" />
          <div :style="{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '15px', fontWeight: '600' }">{{ related.result }}</h4>
            <div :style="{ display: 'flex', gap: '12px', fontSize: '13px', opacity: 0.65 }">
              <span>置信度: {{ related.confidence }}%</span>
              <span>{{ related.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-card>

      <!-- 识别历史 -->
      <a-card title="识别历史趋势" :style="{ borderRadius: '8px' }">
        <div :style="{ textAlign: 'center', padding: '40px 20px', background: '#fafafa', borderRadius: '8px' }">
          <i class="fas fa-chart-line" :style="{ fontSize: '48px', color: '#1890ff', marginBottom: '16px' }"></i>
          <p :style="{ fontSize: '16px', fontWeight: '500', margin: '0 0 8px 0' }">识别历史趋势图表</p>
          <p :style="{ fontSize: '13px', opacity: 0.65, margin: 0 }">显示最近30天的识别活动情况</p>
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { RecognitionAPI, type RecognitionInfo } from '@/api/recognition'
import { ImageUtils } from '@/utils/image'

const route = useRoute()
const router = useRouter()

// 识别详情数据
interface RecognitionDetail {
  id: number
  result: string
  category: string
  confidence: number
  time: string
  originalImage: string
  processedImage?: string
  model: string
  processTime: string
  imageSize: string
  fileSize: string
  format: string
  engine: string
  alternatives: Array<{ name: string; confidence: number }>
  tags: string[]
  description?: string
}

const recognition = ref<RecognitionDetail>({
  id: 0,
  result: '',
  category: '',
  confidence: 0,
  time: '',
  originalImage: '',
  processedImage: '',
  model: '豆包大模型',
  processTime: '0s',
  imageSize: '0x0',
  fileSize: '0MB',
  format: 'JPEG',
  engine: '豆包AI引擎',
  alternatives: [],
  tags: []
})

// 加载状态
const loading = ref(false)

// 相关识别记录
interface RelatedRecognition {
  id: number
  result: string
  confidence: number
  time: string
  thumbnail: string
}
const relatedRecognitions = ref<RelatedRecognition[]>([])

// 状态
const isFavorited = ref(false)

// 初始化
onMounted(() => {
  // 从路由参数获取识别记录ID
  const recognitionId = route.params.id
  if (recognitionId && typeof recognitionId === 'string') {
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

async function loadRecognitionDetail(recognitionId: string | number) {
  try {
    loading.value = true
    console.log('Loading recognition detail for ID:', recognitionId)
    
    const data = await RecognitionAPI.getDetail(Number(recognitionId))
    console.log('识别详情数据:', data)
    
    // 解析结果JSON
    let alternatives: Array<{ name: string; confidence: number }> = []
    let tags: string[] = []
    
    if (data.resultJson) {
      try {
        const resultData = JSON.parse(data.resultJson)
        // 从结果中提取备选项（如果有的话）
        if (resultData.alternatives && Array.isArray(resultData.alternatives)) {
          alternatives = resultData.alternatives.map((alt: any) => ({
            name: alt.name || alt.label || '未知',
            confidence: Math.round((alt.confidence || alt.score || 0) * 100)
          }))
        }
      } catch (e) {
        console.warn('解析结果JSON失败:', e)
      }
    }
    
    // 解析标签
    if (data.tags) {
      tags = data.tags.split(',').filter((t: string) => t.trim())
    }
    
    // 格式化文件大小
    const formatFileSize = (bytes: number): string => {
      if (!bytes) return '0 B'
      if (bytes < 1024) return bytes + ' B'
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
      return (bytes / 1024 / 1024).toFixed(2) + ' MB'
    }
    
    // 转换数据
    recognition.value = {
      id: data.id,
      result: data.objectName || data.category || '未知',
      category: data.category || '其他',
      confidence: Math.round(data.confidence * 100),
      time: formatTime(data.createdAt),
      originalImage: ImageUtils.getImageUrl(data.imageUrl),
      processedImage: undefined, // 暂时没有处理后的图片
      model: '豆包大模型',
      processTime: data.processingTime ? `${(data.processingTime / 1000).toFixed(3)}s` : '未知',
      imageSize: data.imageWidth && data.imageHeight ? `${data.imageWidth}x${data.imageHeight}` : '未知',
      fileSize: formatFileSize(data.imageSize || 0),
      format: data.imageName ? data.imageName.split('.').pop()?.toUpperCase() || 'JPEG' : 'JPEG',
      engine: '豆包AI引擎',
      alternatives,
      tags,
      description: data.description
    }

    // 加载相关识别记录
    await loadRelatedRecognitions(Number(recognitionId))
  } catch (error) {
    console.error('加载识别详情失败:', error)
    message.error('加载识别详情失败')
  } finally {
    loading.value = false
  }
}

// 加载相关识别记录
async function loadRelatedRecognitions(recognitionId: number) {
  try {
    const related = await RecognitionAPI.getRelated(recognitionId)
    console.log('相关识别记录:', related)

    // 转换数据格式
    relatedRecognitions.value = related.map((item: RecognitionInfo) => ({
      id: item.id,
      result: item.objectName || item.category || '未知',
      confidence: Math.round(item.confidence * 100),
      time: formatTime(item.createdAt),
      thumbnail: ImageUtils.getImageUrl(item.imageUrl)
    }))
  } catch (error) {
    console.error('加载相关识别记录失败:', error)
    // 静默失败，不显示错误提示
  }
}

// 格式化时间
function formatTime(timeStr: string): string {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
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

async function shareResult() {
  try {
    const knowledgeId = await RecognitionAPI.shareToKnowledge(recognition.value.id)
    message.success('已成功分享到知识库，等待审核')
    console.log('分享成功，知识ID:', knowledgeId)
  } catch (error: any) {
    console.error('分享失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '分享失败'
    message.error(errorMsg)
  }
}

function downloadImage() {
  message.info('下载功能开发中')
}

function reRecognize() {
  message.info('重新识别功能开发中')
}

async function deleteRecord() {
  try {
    await RecognitionAPI.deleteRecognition(recognition.value.id)
    message.success('删除成功')
    // 返回历史记录页面
    router.push('/user/history')
  } catch (error) {
    console.error('删除记录失败:', error)
    message.error('删除失败')
  }
}

function searchByTag(tag: string) {
  router.push(`/user/history?tag=${encodeURIComponent(tag)}`)
}

function viewRelated(related: any) {
  router.push(`/user/recognition/${related.id}`)
}
</script>

