<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ textAlign: 'center' }">
        <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">图像识别</h1>
        <p :style="{ margin: 0, fontSize: '14px', opacity: 0.65 }">上传图片，AI 为您智能识别图像内容</p>
      </div>
    </a-card>

    <!-- 上传区域 -->
    <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="image"
        :multiple="false"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        @drop="handleDrop"
        accept="image/*"
        :style="{ padding: '48px 24px' }"
      >
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '64px', marginBottom: '24px', opacity: '0.3' }">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <h3 :style="{ margin: '0 0 12px 0', fontSize: '18px', fontWeight: '500' }">点击上传或拖拽图片到此区域</h3>
          <p :style="{ margin: '0 0 24px 0', fontSize: '14px', opacity: '0.65' }">支持 JPG、PNG、GIF 格式，文件大小不超过 10MB</p>
          <a-button type="primary" size="large">
            <template #icon>
              <i class="fas fa-upload"></i>
            </template>
            选择图片
          </a-button>
        </div>
      </a-upload-dragger>
    </a-card>

    <!-- 识别结果 -->
    <div v-if="recognitionResult" :style="{ marginBottom: '16px' }">
      <a-row :gutter="16">
        <!-- 图片预览 -->
        <a-col :xs="24" :lg="12">
          <a-card title="上传的图片" :style="{ borderRadius: '8px', marginBottom: '16px', height: '100%' }">
            <div :style="{ textAlign: 'center', padding: '24px' }">
              <img :src="previewUrl" alt="上传的图片" :style="{ maxWidth: '100%', maxHeight: '400px', borderRadius: '8px' }" />
            </div>
          </a-card>
        </a-col>

        <!-- 识别结果 -->
        <a-col :xs="24" :lg="12">
          <a-card title="识别结果" :style="{ borderRadius: '8px', marginBottom: '16px', height: '100%' }">
            <div>
              <!-- 主要结果 -->
              <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '24px', borderRadius: '8px', background: '#fafafa', marginBottom: '24px' }">
                <div :style="{ width: '64px', height: '64px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '32px' }">
                  <i :class="recognitionResult.icon"></i>
                </div>
                <div :style="{ flex: 1 }">
                  <h2 :style="{ margin: '0 0 12px 0', fontSize: '24px', fontWeight: '600' }">{{ recognitionResult.name }}</h2>
                  <div>
                    <span :style="{ fontSize: '14px', marginBottom: '8px', display: 'block' }">置信度：{{ recognitionResult.confidence }}%</span>
                    <a-progress 
                      :percent="recognitionResult.confidence" 
                      :show-info="false"
                      stroke-color="#52c41a"
                    />
                  </div>
                </div>
              </div>

              <!-- 其他可能结果 -->
              <div v-if="recognitionResult.alternatives?.length" :style="{ marginBottom: '24px' }">
                <h4 :style="{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: '500' }">其他可能结果：</h4>
                <div :style="{ display: 'flex', flexDirection: 'column', gap: '8px' }">
                  <div 
                    v-for="alt in recognitionResult.alternatives" 
                    :key="alt.name"
                    :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', borderRadius: '6px', background: '#fafafa' }"
                  >
                    <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ alt.name }}</span>
                    <span :style="{ fontSize: '14px', opacity: '0.65' }">{{ alt.confidence }}%</span>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div :style="{ display: 'flex', gap: '12px', flexWrap: 'wrap' }">
                <a-button type="primary" @click="viewKnowledge">
                  <template #icon>
                    <i class="fas fa-book"></i>
                  </template>
                  查看百科
                </a-button>
                <a-button @click="addToFavorites">
                  <template #icon>
                    <i class="fas fa-heart"></i>
                  </template>
                  收藏
                </a-button>
                <a-button @click="shareResult">
                  <template #icon>
                    <i class="fas fa-share"></i>
                  </template>
                  分享
                </a-button>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 详细信息 -->
      <a-card title="详细信息" :style="{ borderRadius: '8px', marginBottom: '16px' }">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="识别类别">
            {{ recognitionResult.category }}
          </a-descriptions-item>
          <a-descriptions-item label="置信度">
            {{ recognitionResult.confidence }}%
          </a-descriptions-item>
          <a-descriptions-item label="识别时间">
            {{ recognitionResult.timestamp }}
          </a-descriptions-item>
          <a-descriptions-item label="图片尺寸">
            {{ recognitionResult.imageSize }}
          </a-descriptions-item>
          <a-descriptions-item label="文件大小">
            {{ recognitionResult.fileSize }}
          </a-descriptions-item>
          <a-descriptions-item label="处理时间">
            {{ recognitionResult.processTime }}ms
          </a-descriptions-item>
        </a-descriptions>

        <!-- 标签 -->
        <div v-if="recognitionResult.tags?.length" :style="{ marginTop: '24px', paddingTop: '24px', borderTop: '1px solid #f0f0f0' }">
          <h4 :style="{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: '500' }">相关标签：</h4>
          <div :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }">
            <a-tag 
              v-for="tag in recognitionResult.tags" 
              :key="tag"
              color="blue"
            >
              {{ tag }}
            </a-tag>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 历史记录 -->
    <a-card title="最近识别" :style="{ borderRadius: '8px' }">
      <template #extra>
        <a-button type="link" @click="viewAllHistory">查看全部</a-button>
      </template>
      <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
        <div 
          v-for="item in recentHistory" 
          :key="item.id"
          :style="{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease' }"
          @click="loadHistoryResult(item)"
        >
          <div :style="{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', flexShrink: 0 }">
            <img :src="item.thumbnail" :alt="item.result" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
          </div>
          <div :style="{ flex: 1, minWidth: 0 }">
            <div :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '6px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }">{{ item.result }}</div>
            <div :style="{ fontSize: '13px', opacity: '0.65' }">{{ item.time }}</div>
          </div>
          <div :style="{ fontSize: '14px', fontWeight: '500' }">{{ item.confidence }}%</div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'
import { RecognitionAPI } from '@/api/recognition'

const router = useRouter()
const fileList = ref<UploadFile[]>([])
const previewUrl = ref('')
const recognitionResult = ref<any>(null)
const recognizing = ref(false)

// 最近历史记录
const recentHistory = ref<any[]>([])

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
  
  return false // 阻止自动上传
}

// 文件变化处理
function handleFileChange(info: any) {
  const { file } = info
  if (file) {
    // 创建预览URL
    const reader = new FileReader()
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string
      recognizeImage(file)
    }
    reader.readAsDataURL(file)
  }
}

// 拖拽处理
function handleDrop(e: DragEvent) {
  console.log('File dropped:', e)
}

// 图像识别
async function recognizeImage(file: any) {
  recognizing.value = true
  const loadingMsg = message.loading('正在识别中...', 0)
  
  try {
    // 获取实际的文件对象
    const actualFile = file.originFileObj || file
    
    // 调用后端API进行识别
    console.log('开始识别:', { fileName: actualFile.name, size: actualFile.size, type: actualFile.type })
    const result = await RecognitionAPI.recognize(actualFile, 0)
    console.log('识别结果:', result)
    
    if (result) {
      // 解析识别结果
      let resultData: any = {}
      
      // 如果后端返回的是JSON字符串，解析它
      if (result.resultJson) {
        try {
          resultData = JSON.parse(result.resultJson)
          console.log('解析后的结果数据:', resultData)
        } catch (e) {
          console.error('解析resultJson失败:', e)
          resultData = {}
        }
      }
      
      // 计算置信度百分比
      const confidencePercent = result.confidence 
        ? Math.round(Number(result.confidence) * 100) 
        : 0
      
      // 格式化识别结果供界面显示
      recognitionResult.value = {
        id: result.id,
        name: result.mainCategory || result.category || '未知',
        category: result.mainCategory || result.category || '未分类',
        confidence: confidencePercent,
        icon: getIconByCategory(result.mainCategory || result.category || ''),
        alternatives: resultData.alternatives || [],
        tags: result.tags ? result.tags.split(',') : [],
        timestamp: formatDateTime(result.createdAt || result.createTime),
        imageSize: result.imageWidth && result.imageHeight 
          ? `${result.imageWidth} × ${result.imageHeight}` 
          : '-',
        fileSize: formatFileSize(actualFile.size),
        processTime: result.processingTime || 0,
        description: result.description || ''
      }
      
      // 刷新历史记录
      await loadRecentHistory()
      
      loadingMsg()
      message.success('识别完成!')
    }
  } catch (error: any) {
    console.error('识别失败:', error)
    loadingMsg()
    
    // 显示友好的错误提示
    const errorMsg = error.message || error.response?.data?.message || '识别失败，请重试'
    message.error(errorMsg)
    
    // 清空识别结果
    recognitionResult.value = null
  } finally {
    recognizing.value = false
  }
}

// 根据分类获取图标
function getIconByCategory(category: string): string {
  if (!category) return 'fas fa-question-circle'
  
  const categoryLower = category.toLowerCase()
  if (categoryLower.includes('动物') || categoryLower.includes('犬') || categoryLower.includes('猫')) {
    return 'fas fa-paw'
  } else if (categoryLower.includes('植物') || categoryLower.includes('花')) {
    return 'fas fa-leaf'
  } else if (categoryLower.includes('食物') || categoryLower.includes('水果')) {
    return 'fas fa-apple-alt'
  } else if (categoryLower.includes('建筑') || categoryLower.includes('房')) {
    return 'fas fa-building'
  } else if (categoryLower.includes('交通') || categoryLower.includes('车')) {
    return 'fas fa-car'
  } else {
    return 'fas fa-image'
  }
}

// 格式化日期时间
function formatDateTime(dateTime: any): string {
  if (!dateTime) return new Date().toLocaleString()
  
  if (typeof dateTime === 'string') {
    return new Date(dateTime).toLocaleString()
  }
  
  return dateTime.toLocaleString ? dateTime.toLocaleString() : String(dateTime)
}

// 格式化文件大小
function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

// 加载最近历史记录
async function loadRecentHistory() {
  try {
    const response = await RecognitionAPI.getHistory({ page: 1, size: 3 })
    
          if (response && response.data) {
            recentHistory.value = response.data.map((item: any) => ({
        id: item.id,
        result: item.mainCategory || item.category || '未知',
        confidence: item.confidence 
          ? Math.round(Number(item.confidence) * 100) 
          : 0,
        time: formatRelativeTime(item.createdAt || item.createTime),
        thumbnail: item.imageUrl || '/api/placeholder/60/60'
      }))
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
  }
}

// 格式化相对时间
function formatRelativeTime(dateTime: any): string {
  if (!dateTime) return '刚刚'
  
  const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < 7 * day) {
    return Math.floor(diff / day) + '天前'
  } else {
    return date.toLocaleDateString()
  }
}

// 查看知识库
function viewKnowledge() {
  if (recognitionResult.value?.name) {
    // 跳转到知识库搜索页面，带上识别结果作为搜索关键词
    router.push(`/user/knowledge?search=${encodeURIComponent(recognitionResult.value.name)}`)
  } else {
    router.push('/user/knowledge')
  }
}

// 添加到收藏
function addToFavorites() {
  // TODO: 实现收藏功能
  message.success('已添加到收藏')
}

// 分享结果
function shareResult() {
  if (recognitionResult.value?.id) {
    // 复制分享链接到剪贴板
    const shareUrl = `${window.location.origin}/user/recognition/${recognitionResult.value.id}`
    
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(shareUrl)
        .then(() => {
          message.success('分享链接已复制到剪贴板')
        })
        .catch(() => {
          message.info('分享链接: ' + shareUrl)
        })
    } else {
      message.info('分享链接: ' + shareUrl)
    }
  } else {
    message.warning('请先完成图像识别')
  }
}

// 查看全部历史
function viewAllHistory() {
  router.push('/user/history')
}

// 加载历史结果
function loadHistoryResult(item: any) {
  router.push(`/user/recognition/${item.id}`)
}

// 页面加载时获取最近历史
onMounted(() => {
  loadRecentHistory()
})
</script>

