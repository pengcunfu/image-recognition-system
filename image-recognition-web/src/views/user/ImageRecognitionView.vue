<template>
  <div class="recognition-page">
    <div class="page-header">
      <h1>图像识别</h1>
      <p>上传图片，AI 为您智能识别图像内容</p>
    </div>

    <!-- 上传区域 -->
    <a-card class="upload-card">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="image"
        :multiple="false"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        @drop="handleDrop"
        class="upload-dragger"
        accept="image/*"
      >
        <div class="upload-content">
          <div class="upload-icon">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <h3>点击上传或拖拽图片到此区域</h3>
          <p>支持 JPG、PNG、GIF 格式，文件大小不超过 10MB</p>
          <a-button type="primary" size="large" class="upload-btn">
            <template #icon>
              <i class="fas fa-upload"></i>
            </template>
            选择图片
          </a-button>
        </div>
      </a-upload-dragger>
    </a-card>

    <!-- 识别结果 -->
    <div v-if="recognitionResult" class="result-section">
      <a-row :gutter="24">
        <!-- 图片预览 -->
        <a-col :xs="24" :lg="12">
          <a-card title="上传的图片" class="preview-card">
            <div class="image-preview">
              <img :src="previewUrl" alt="上传的图片" />
            </div>
          </a-card>
        </a-col>

        <!-- 识别结果 -->
        <a-col :xs="24" :lg="12">
          <a-card title="识别结果" class="result-card">
            <div class="result-content">
              <!-- 主要结果 -->
              <div class="main-result">
                <div class="result-icon">
                  <i :class="recognitionResult.icon"></i>
                </div>
                <div class="result-info">
                  <h2>{{ recognitionResult.name }}</h2>
                  <div class="confidence">
                    <span>置信度：{{ recognitionResult.confidence }}%</span>
                    <a-progress 
                      :percent="recognitionResult.confidence" 
                      :show-info="false"
                      stroke-color="#52c41a"
                    />
                  </div>
                </div>
              </div>

              <!-- 其他可能结果 -->
              <div v-if="recognitionResult.alternatives?.length" class="alternatives">
                <h4>其他可能结果：</h4>
                <div class="alt-list">
                  <div 
                    v-for="alt in recognitionResult.alternatives" 
                    :key="alt.name"
                    class="alt-item"
                  >
                    <span class="alt-name">{{ alt.name }}</span>
                    <span class="alt-confidence">{{ alt.confidence }}%</span>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="result-actions">
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
      <a-card title="详细信息" class="detail-card">
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
        <div v-if="recognitionResult.tags?.length" class="tags-section">
          <h4>相关标签：</h4>
          <a-tag 
            v-for="tag in recognitionResult.tags" 
            :key="tag"
            color="blue"
            class="result-tag"
          >
            {{ tag }}
          </a-tag>
        </div>
      </a-card>
    </div>

    <!-- 历史记录 -->
    <a-card title="最近识别" class="history-card">
      <template #extra>
        <a-button type="link" @click="viewAllHistory">查看全部</a-button>
      </template>
      <div class="history-list">
        <div 
          v-for="item in recentHistory" 
          :key="item.id"
          class="history-item"
          @click="loadHistoryResult(item)"
        >
          <div class="history-image">
            <img :src="item.thumbnail" :alt="item.result" />
          </div>
          <div class="history-info">
            <div class="history-name">{{ item.result }}</div>
            <div class="history-time">{{ item.time }}</div>
          </div>
          <div class="history-confidence">{{ item.confidence }}%</div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'

const router = useRouter()
const fileList = ref<UploadFile[]>([])
const previewUrl = ref('')
const recognitionResult = ref<any>(null)
const recognizing = ref(false)

// 最近历史记录
const recentHistory = ref([
  {
    id: 1,
    result: '金毛犬',
    confidence: 95,
    time: '2小时前',
    thumbnail: '/api/placeholder/60/60'
  },
  {
    id: 2,
    result: '玫瑰花',
    confidence: 88,
    time: '1天前',
    thumbnail: '/api/placeholder/60/60'
  },
  {
    id: 3,
    result: '苹果',
    confidence: 92,
    time: '2天前',
    thumbnail: '/api/placeholder/60/60'
  }
])

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
async function recognizeImage(file: File) {
  recognizing.value = true
  message.loading('正在识别中...', 0)
  
  try {
    // 模拟AI识别过程
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    // 模拟识别结果
    const mockResults = [
      {
        name: '金毛寻回犬',
        category: '动物·犬类',
        confidence: 95,
        icon: 'fas fa-dog',
        alternatives: [
          { name: '拉布拉多', confidence: 78 },
          { name: '萨摩耶', confidence: 65 }
        ],
        tags: ['宠物', '犬类', '金毛', '温顺'],
        timestamp: new Date().toLocaleString(),
        imageSize: '1920x1080',
        fileSize: '2.5MB',
        processTime: 2847
      },
      {
        name: '玫瑰花',
        category: '植物·花卉',
        confidence: 92,
        icon: 'fas fa-rose',
        alternatives: [
          { name: '月季花', confidence: 73 },
          { name: '牡丹花', confidence: 58 }
        ],
        tags: ['花卉', '玫瑰', '红色', '浪漫'],
        timestamp: new Date().toLocaleString(),
        imageSize: '1080x1080',
        fileSize: '1.8MB',
        processTime: 2156
      }
    ]
    
    recognitionResult.value = mockResults[Math.floor(Math.random() * mockResults.length)]
    
    message.destroy()
    message.success('识别完成!')
  } catch (error) {
    message.destroy()
    message.error('识别失败，请重试')
  } finally {
    recognizing.value = false
  }
}

// 查看知识库
function viewKnowledge() {
  message.info('跳转到知识库页面')
  router.push('/user/knowledge')
}

// 添加到收藏
function addToFavorites() {
  message.success('已添加到收藏')
}

// 分享结果
function shareResult() {
  message.info('分享功能开发中')
}

// 查看全部历史
function viewAllHistory() {
  router.push('/user/history')
}

// 加载历史结果
function loadHistoryResult(item: any) {
  router.push(`/user/recognition/${item.id}?from=recognition`)
}
</script>

