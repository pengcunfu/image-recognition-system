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
  message.info(`加载历史结果：${item.result}`)
}
</script>

<style scoped>
.recognition-page {
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
  margin-bottom: 24px;
}

.upload-btn {
  border-radius: 8px;
}

/* 结果区域 */
.result-section {
  margin-bottom: 32px;
}

.preview-card,
.result-card,
.detail-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.image-preview {
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.result-content {
  padding: 0;
}

.main-result {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f6ffed 0%, #f0f9ff 100%);
  border-radius: 12px;
  border: 1px solid #b7eb8f;
}

.result-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #52c41a, #73d13d);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.result-info h2 {
  font-size: 24px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.confidence {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.confidence span {
  font-size: 14px;
  color: #666;
}

.alternatives {
  margin-bottom: 24px;
}

.alternatives h4 {
  color: #262626;
  margin-bottom: 12px;
}

.alt-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.alt-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 6px;
}

.alt-name {
  color: #262626;
}

.alt-confidence {
  color: #666;
  font-size: 14px;
}

.result-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 详细信息 */
.tags-section {
  margin-top: 24px;
}

.tags-section h4 {
  color: #262626;
  margin-bottom: 12px;
}

.result-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

/* 历史记录 */
.history-card {
  border-radius: 12px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  background: #fafafa;
  border-color: #1890ff;
}

.history-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.history-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.history-info {
  flex: 1;
}

.history-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.history-time {
  font-size: 12px;
  color: #999;
}

.history-confidence {
  color: #52c41a;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-content {
    padding: 24px;
  }
  
  .upload-icon {
    font-size: 48px;
  }
  
  .main-result {
    flex-direction: column;
    text-align: center;
  }
  
  .result-actions {
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .result-actions {
    flex-direction: column;
  }
}
</style>
