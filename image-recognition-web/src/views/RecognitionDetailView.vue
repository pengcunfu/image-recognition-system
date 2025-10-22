<template>
  <div :style="{ padding: '24px' }">
    <!-- 返回按钮 -->
    <div :style="{ marginBottom: '16px' }">
      <a-button @click="goBack">
        <i class="fas fa-arrow-left" :style="{ marginRight: '8px' }"></i>
        返回
      </a-button>
    </div>

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

