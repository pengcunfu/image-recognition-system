<template>
  <div class="knowledge-detail-page">
    <!-- 返回按钮 -->
    <div class="back-section">
      <a-button @click="goBack" size="large">
        <i class="fas fa-arrow-left"></i>
        返回知识库
      </a-button>
    </div>

    <!-- 加载状态 -->
    <a-spin :spinning="loading" size="large" tip="加载中...">
      <div v-if="!loading && !knowledge.id" style="text-align: center; padding: 100px 0;">
        <a-empty description="知识内容不存在" />
      </div>

      <div v-else>
    <!-- 知识详情卡片 -->
    <a-card class="knowledge-card">
      <!-- 知识头部 -->
      <div class="knowledge-header">
        <div class="knowledge-main-info">
          <div class="knowledge-image">
            <img :src="knowledge.image || '/api/placeholder/400/300'" :alt="knowledge.title" />
          </div>
          <div class="knowledge-info">
            <h1 class="knowledge-title">{{ knowledge.title }}</h1>
            <p class="knowledge-description">{{ knowledge.description }}</p>
            <div class="knowledge-meta">
              <a-tag :color="getCategoryColor(knowledge.category)">
                {{ knowledge.category }}
              </a-tag>
              <a-tag v-if="knowledge.difficulty" :color="getDifficultyColor(knowledge.difficulty)">
                难度：{{ knowledge.difficulty }}
              </a-tag>
              <span class="meta-item">
                <i class="fas fa-eye"></i>
                {{ knowledge.views }} 浏览
              </span>
              <span class="meta-item">
                <i class="fas fa-clock"></i>
                更新于 {{ knowledge.updateTime }}
              </span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="knowledge-actions">
          <a-button @click="toggleBookmark" :type="isBookmarked ? 'primary' : 'default'">
            <i class="fas fa-bookmark"></i>
            {{ isBookmarked ? '已收藏' : '收藏' }}
          </a-button>
          
          <a-button @click="shareKnowledge">
            <i class="fas fa-share"></i>
            分享
          </a-button>
          
          <a-dropdown>
            <a-button>
              <i class="fas fa-ellipsis-h"></i>
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="printKnowledge">
                  <i class="fas fa-print"></i>
                  打印
                </a-menu-item>
                <a-menu-item @click="reportKnowledge">
                  <i class="fas fa-flag"></i>
                  举报
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </a-card>

    <!-- 知识内容区域 -->
    <a-row :gutter="24">
      <!-- 主要内容 -->
      <a-col :xs="24" :lg="16">
        <!-- 详细内容 -->
        <a-card title="详细介绍" class="content-card">
          <div class="knowledge-content" v-html="knowledge.content"></div>
        </a-card>

        <!-- 特征分析 -->
        <a-card v-if="knowledge.features" title="特征分析" class="features-card">
          <div class="features-grid">
            <div 
              v-for="feature in knowledge.features" 
              :key="feature.id"
              class="feature-item"
            >
              <div class="feature-image">
                <img :src="feature.image" :alt="feature.name" />
              </div>
              <div class="feature-content">
                <h4>{{ feature.name }}</h4>
                <p>{{ feature.description }}</p>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 识别技巧 -->
        <a-card v-if="knowledge.tips" title="识别技巧" class="tips-card">
          <div class="tips-list">
            <div 
              v-for="(tip, index) in knowledge.tips" 
              :key="index"
              class="tip-item"
            >
              <div class="tip-number">{{ index + 1 }}</div>
              <div class="tip-content">
                <h4>{{ tip.title }}</h4>
                <p>{{ tip.description }}</p>
                <div v-if="tip.examples" class="tip-examples">
                  <h5>示例：</h5>
                  <div class="examples-grid">
                    <div 
                      v-for="(example, exIndex) in tip.examples" 
                      :key="exIndex"
                      class="example-item"
                      @click="previewImage(example.image, example.description)"
                    >
                      <img :src="example.image" :alt="example.description" />
                      <span class="example-label">{{ example.description }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 相关图片 -->
        <a-card v-if="knowledge.gallery" title="相关图片" class="gallery-card">
          <div class="image-gallery">
            <div 
              v-for="(image, index) in knowledge.gallery" 
              :key="index"
              class="gallery-item"
              @click="previewImage(image.url, image.description)"
            >
              <img :src="image.url" :alt="image.description" />
              <div class="image-overlay">
                <span class="image-description">{{ image.description }}</span>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 相关知识测试 -->
        <a-card v-if="knowledge.quiz" title="知识测试" class="quiz-card">
          <div class="quiz-section">
            <div v-if="!showQuizResult" class="quiz-question">
              <h3>第 {{ currentQuestionIndex + 1 }} 题 / {{ knowledge.quiz.length }}</h3>
              <div class="question-content">
                <p>{{ currentQuestion.question }}</p>
                <div v-if="currentQuestion.image" class="question-image">
                  <img :src="currentQuestion.image" :alt="currentQuestion.question" />
                </div>
                <div class="question-options">
                  <a-radio-group v-model:value="selectedAnswer" @change="handleAnswerChange">
                    <div 
                      v-for="(option, index) in currentQuestion.options" 
                      :key="index"
                      class="option-item"
                    >
                      <a-radio :value="index">{{ option }}</a-radio>
                    </div>
                  </a-radio-group>
                </div>
                <div class="quiz-actions">
                  <a-button 
                    type="primary" 
                    @click="submitAnswer"
                    :disabled="selectedAnswer === null"
                  >
                    {{ currentQuestionIndex === knowledge.quiz.length - 1 ? '完成测试' : '下一题' }}
                  </a-button>
                </div>
              </div>
            </div>

            <div v-else class="quiz-result">
              <div class="result-header">
                <i class="fas fa-trophy"></i>
                <h3>测试完成！</h3>
              </div>
              <div class="result-stats">
                <div class="stat-item">
                  <span class="stat-label">正确率</span>
                  <span class="stat-value">{{ Math.round(correctAnswers / knowledge.quiz.length * 100) }}%</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">正确题数</span>
                  <span class="stat-value">{{ correctAnswers }} / {{ knowledge.quiz.length }}</span>
                </div>
              </div>
              <div class="result-actions">
                <a-button type="primary" @click="restartQuiz">
                  重新测试
                </a-button>
                <a-button @click="shareResult">
                  分享成绩
                </a-button>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 侧边栏 -->
      <a-col :xs="24" :lg="8">
        <!-- 快速信息 -->
        <a-card title="快速信息" class="info-card">
          <div class="quick-info">
            <div class="info-item">
              <span class="info-label">分类</span>
              <span class="info-value">{{ knowledge.category }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">识别难度</span>
              <span class="info-value">{{ knowledge.difficulty || '一般' }}</span>
            </div>
            <div v-if="knowledge.scientificName" class="info-item">
              <span class="info-label">学名</span>
              <span class="info-value">{{ knowledge.scientificName }}</span>
            </div>
            <div v-if="knowledge.commonNames" class="info-item">
              <span class="info-label">别名</span>
              <span class="info-value">{{ knowledge.commonNames.join('、') }}</span>
            </div>
            <div v-if="knowledge.habitat" class="info-item">
              <span class="info-label">栖息地</span>
              <span class="info-value">{{ knowledge.habitat }}</span>
            </div>
            <div v-if="knowledge.size" class="info-item">
              <span class="info-label">大小</span>
              <span class="info-value">{{ knowledge.size }}</span>
            </div>
          </div>
        </a-card>

        <!-- 识别要点 -->
        <a-card v-if="knowledge.keyPoints" title="识别要点" class="key-points-card">
          <div class="key-points">
            <div 
              v-for="(point, index) in knowledge.keyPoints" 
              :key="index"
              class="key-point"
            >
              <div class="point-icon">
                <i :class="point.icon || 'fas fa-check'"></i>
              </div>
              <div class="point-content">
                <strong>{{ point.title }}</strong>
                <p>{{ point.description }}</p>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 统计信息 -->
        <a-card title="统计信息" class="stats-card">
          <div class="knowledge-stats">
            <div class="stat-row">
              <i class="fas fa-eye"></i>
              <span>浏览量：{{ knowledge.views }}</span>
            </div>
            <div class="stat-row">
              <i class="fas fa-bookmark"></i>
              <span>收藏数：{{ knowledge.bookmarks || 0 }}</span>
            </div>
            <div class="stat-row">
              <i class="fas fa-thumbs-up"></i>
              <span>点赞数：{{ knowledge.likes || 0 }}</span>
            </div>
            <div class="stat-row">
              <i class="fas fa-calendar"></i>
              <span>创建时间：{{ knowledge.createTime }}</span>
            </div>
          </div>
        </a-card>

        <!-- 相关知识 -->
        <a-card title="相关知识" class="related-card">
          <div class="related-list">
            <div 
              v-for="related in relatedKnowledge" 
              :key="related.id"
              class="related-item"
              @click="viewRelated(related)"
            >
              <img :src="related.image || '/api/placeholder/80/60'" :alt="related.title" />
              <div class="related-content">
                <h4>{{ related.title }}</h4>
                <p>{{ related.description }}</p>
                <div class="related-meta">
                  <a-tag :color="getCategoryColor(related.category)" size="small">
                    {{ related.category }}
                  </a-tag>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图片预览模态框 -->
    <a-modal
      v-model:open="imagePreviewVisible"
      :footer="null"
      width="80%"
      centered
      class="image-preview-modal"
    >
      <div class="image-preview-container">
        <img :src="previewImageUrl" :alt="previewImageDescription" />
        <p v-if="previewImageDescription" class="preview-description">
          {{ previewImageDescription }}
        </p>
      </div>
    </a-modal>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { KnowledgeAPI } from '@/api/knowledge'
import { FileAPI } from '@/api/file'
import type { KnowledgeItem } from '@/api/types'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(true)

// 知识详情数据
const knowledge = ref<any>({
  id: 0,
  title: '',
  description: '',
  category: '',
  difficulty: '',
  image: '',
  views: 0,
  bookmarks: 0,
  likes: 0,
  createTime: '',
  updateTime: '',
  scientificName: '',
  commonNames: [],
  habitat: '',
  size: '',
  content: '',
  features: null,
  tips: null,
  keyPoints: null,
  gallery: null,
  quiz: null
})

// 相关知识
const relatedKnowledge = ref<any[]>([])

// 状态管理
const isBookmarked = ref(false)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')
const previewImageDescription = ref('')

// 测试相关状态
const currentQuestionIndex = ref(0)
const selectedAnswer = ref(null)
const correctAnswers = ref(0)
const showQuizResult = ref(false)
const userAnswers = ref([])

// 计算属性
const currentQuestion = computed(() => {
  return knowledge.value.quiz?.[currentQuestionIndex.value]
})

// 初始化
onMounted(() => {
  // 从路由参数获取知识ID
  const knowledgeId = route.params.id
  if (knowledgeId) {
    loadKnowledgeDetail(knowledgeId as string)
  }
})

// 方法
function goBack() {
  router.push('/user/knowledge')
}

async function loadKnowledgeDetail(knowledgeId: string) {
  try {
    loading.value = true
    console.log('加载知识详情, ID:', knowledgeId)
    
    const response = await KnowledgeAPI.getItemDetail(knowledgeId)
    const item = response.data
    
    console.log('知识详情数据:', item)
    
    // 转换数据格式
    knowledge.value = {
      id: item.id,
      title: item.name,
      description: item.description,
      category: '', // 需要从分类ID获取分类名称
      difficulty: getDifficultyText(item.difficulty),
      image: FileAPI.getFirstImage(item.images),
      views: item.viewCount || 0,
      bookmarks: item.favoriteCount || 0,
      likes: item.likeCount || 0,
      createTime: item.createTime?.split(' ')[0] || '',
      updateTime: item.updateTime?.split(' ')[0] || '',
      scientificName: item.scientificName || '',
      commonNames: [], // 可以从tags解析
      habitat: item.habitat || '',
      size: '', // 可以从characteristics解析
      content: item.content || item.description,
      features: parseFeatures(item.characteristics),
      tips: null, // 可以从content解析
      keyPoints: parseKeyPoints(item.characteristics),
      gallery: parseGallery(item.images),
      quiz: null // 测试功能暂未实现
    }
    
    // 加载分类名称
    if (item.categoryId) {
      await loadCategoryName(item.categoryId)
    }
    
    // 加载相关知识
    await loadRelatedKnowledge(knowledgeId)
    
  } catch (error) {
    console.error('加载知识详情失败:', error)
    message.error('加载知识详情失败')
  } finally {
    loading.value = false
  }
}

// 加载分类名称
async function loadCategoryName(categoryId: number) {
  try {
    const response = await KnowledgeAPI.getCategories()
    const category = response.data.find(cat => cat.id === categoryId)
    if (category) {
      knowledge.value.category = category.name
    }
  } catch (error) {
    console.error('加载分类名称失败:', error)
  }
}

// 加载相关知识
async function loadRelatedKnowledge(itemId: string) {
  try {
    const response = await KnowledgeAPI.getRelatedItems(itemId, 3)
    relatedKnowledge.value = response.data.map(item => ({
      id: item.id,
      title: item.name,
      description: item.description,
      image: FileAPI.getFirstImage(item.images),
      category: '' // 可以后续加载
    }))
    console.log('相关知识加载成功:', relatedKnowledge.value)
  } catch (error) {
    console.error('加载相关知识失败:', error)
    // 如果相关知识接口失败，不影响主流程
  }
}

// 解析特征数据
function parseFeatures(characteristics: string | undefined): any[] | null {
  if (!characteristics) return null
  
  try {
    // 尝试解析JSON格式的特征数据
    const parsed = JSON.parse(characteristics)
    if (Array.isArray(parsed)) {
      return parsed.map((item, index) => ({
        id: index + 1,
        name: item.name || item.title || '',
        description: item.description || item.desc || '',
        image: item.image || '/api/placeholder/200/150'
      }))
    }
  } catch {
    // 如果不是JSON，按文本处理
    const lines = characteristics.split('\n').filter(line => line.trim())
    if (lines.length > 0) {
      return lines.map((line, index) => ({
        id: index + 1,
        name: `特征 ${index + 1}`,
        description: line.trim(),
        image: '/api/placeholder/200/150'
      }))
    }
  }
  
  return null
}

// 解析识别要点
function parseKeyPoints(characteristics: string | undefined): any[] | null {
  if (!characteristics) return null
  
  try {
    const parsed = JSON.parse(characteristics)
    if (Array.isArray(parsed)) {
      return parsed.map(item => ({
        title: item.name || item.title || '',
        description: item.description || item.desc || '',
        icon: item.icon || 'fas fa-check'
      }))
    }
  } catch {
    // 如果不是JSON，返回null
  }
  
  return null
}

// 解析图片画廊
function parseGallery(imagesJson: string | undefined): any[] | null {
  if (!imagesJson) return null
  
  try {
    const images = JSON.parse(imagesJson)
    if (Array.isArray(images) && images.length > 0) {
      return images.map((url, index) => ({
        url: FileAPI.getImageUrl(url),
        description: `图片 ${index + 1}`
      }))
    }
  } catch {
    // 如果不是JSON，尝试作为单个URL处理
    return [{
      url: FileAPI.getImageUrl(imagesJson),
      description: '图片'
    }]
  }
  
  return null
}

// 获取难度文本
function getDifficultyText(difficulty: number | undefined): string {
  if (!difficulty) return '一般'
  if (difficulty <= 3) return '简单'
  if (difficulty <= 6) return '一般'
  return '困难'
}

function getCategoryColor(category: string) {
  const colors: Record<string, string> = {
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange',
    '物品': 'purple',
    '风景': 'cyan'
  }
  return colors[category] || 'default'
}

function getDifficultyColor(difficulty: string) {
  const colors: Record<string, string> = {
    '简单': 'green',
    '一般': 'orange',
    '困难': 'red'
  }
  return colors[difficulty] || 'default'
}

async function toggleBookmark() {
  try {
    if (isBookmarked.value) {
      // 取消收藏
      await KnowledgeAPI.uncollectItem(knowledge.value.id)
      isBookmarked.value = false
      knowledge.value.bookmarks--
      message.success('取消收藏')
    } else {
      // 收藏
      await KnowledgeAPI.collectItem(knowledge.value.id)
      isBookmarked.value = true
      knowledge.value.bookmarks++
      message.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    message.error('操作失败，请重试')
  }
}

function shareKnowledge() {
  // 复制分享链接到剪贴板
  const shareUrl = `${window.location.origin}/user/knowledge/${knowledge.value.id}`
  navigator.clipboard.writeText(shareUrl).then(() => {
    message.success('分享链接已复制到剪贴板')
  }).catch(() => {
    message.info(`分享链接：${shareUrl}`)
  })
}

function printKnowledge() {
  window.print()
}

async function reportKnowledge() {
  try {
    // 简单的举报提示，实际应该有一个表单
    const result = await new Promise<string>((resolve) => {
      // 这里应该弹出一个举报表单，暂时用简单提示代替
      message.info('举报功能：请选择举报类型')
      resolve('inappropriate')
    })
    
    await KnowledgeAPI.reportItem(knowledge.value.id, {
      type: 'inappropriate',
      description: '用户举报'
    })
    
    message.success('举报已提交，我们会尽快处理')
  } catch (error) {
    console.error('举报失败:', error)
  }
}

function previewImage(imageUrl: string, description: string = '') {
  previewImageUrl.value = imageUrl
  previewImageDescription.value = description
  imagePreviewVisible.value = true
}

function viewRelated(relatedItem: any) {
  router.push(`/user/knowledge/${relatedItem.id}`)
}

// 测试相关方法
function handleAnswerChange(e: any) {
  selectedAnswer.value = e.target.value
}

function submitAnswer() {
  if (selectedAnswer.value === null) return
  
  userAnswers.value.push(selectedAnswer.value)
  
  if (selectedAnswer.value === currentQuestion.value.correct) {
    correctAnswers.value++
  }
  
  if (currentQuestionIndex.value < knowledge.value.quiz.length - 1) {
    currentQuestionIndex.value++
    selectedAnswer.value = null
  } else {
    showQuizResult.value = true
  }
}

function restartQuiz() {
  currentQuestionIndex.value = 0
  selectedAnswer.value = null
  correctAnswers.value = 0
  showQuizResult.value = false
  userAnswers.value = []
}

function shareResult() {
  const score = Math.round(correctAnswers.value / knowledge.value.quiz.length * 100)
  message.success(`我在《${knowledge.value.title}》测试中得了${score}分！`)
}
</script>

