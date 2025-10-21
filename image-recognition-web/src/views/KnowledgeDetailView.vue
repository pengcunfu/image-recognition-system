<template>
  <div :style="{ padding: '24px' }">
    <!-- 返回按钮 -->
    <div :style="{ marginBottom: '24px' }">
      <a-button @click="goBack" size="large">
        <i class="fas fa-arrow-left"></i>
        <span :style="{ color: '#000' }">返回知识库</span>
      </a-button>
    </div>

    <!-- 加载状态 -->
    <a-spin :spinning="loading" size="large" tip="加载中...">
      <div v-if="!loading && !knowledge.id" :style="{ textAlign: 'center', padding: '100px 0' }">
        <a-empty description="知识内容不存在" />
      </div>

      <div v-else>
    <!-- 知识详情卡片 -->
    <a-card :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
      <!-- 知识头部 -->
      <div :style="{ display: 'flex', justifyContent: 'space-between', gap: '24px', marginBottom: '24px' }">
        <div :style="{ display: 'flex', gap: '24px', flex: 1 }">
          <div :style="{ width: '400px', flexShrink: 0 }">
            <img :src="knowledge.image || '/api/placeholder/400/300'" :alt="knowledge.title" :style="{ width: '100%', height: 'auto', borderRadius: '8px' }" />
          </div>
          <div :style="{ flex: 1 }">
            <h1 :style="{ margin: '0 0 12px 0', fontSize: '28px', fontWeight: '600' }">{{ knowledge.title }}</h1>
            <p :style="{ margin: '0 0 16px 0', fontSize: '14px', opacity: '0.65', lineHeight: '1.6' }">{{ knowledge.description }}</p>
            <div :style="{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'center' }">
              <a-tag :color="getCategoryColor(knowledge.category)">
                {{ knowledge.category }}
              </a-tag>
              <a-tag v-if="knowledge.difficulty" :color="getDifficultyColor(knowledge.difficulty)">
                难度：{{ knowledge.difficulty }}
              </a-tag>
              <span :style="{ fontSize: '14px', opacity: '0.65' }">
                <i class="fas fa-eye"></i>
                {{ knowledge.views }} 浏览
              </span>
              <span :style="{ fontSize: '14px', opacity: '0.65' }">
                <i class="fas fa-clock"></i>
                更新于 {{ knowledge.updateTime }}
              </span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px', flexShrink: 0 }">
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
        <a-card title="详细介绍" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ lineHeight: '1.8', fontSize: '15px' }" v-html="knowledge.content"></div>
        </a-card>

        <!-- 特征分析 -->
        <a-card v-if="knowledge.features" title="特征分析" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }">
            <div 
              v-for="feature in knowledge.features" 
              :key="feature.id"
              :style="{ display: 'flex', gap: '16px', padding: '16px', borderRadius: '8px', background: '#fafafa' }"
            >
              <div :style="{ width: '100px', height: '100px', flexShrink: 0, borderRadius: '8px', overflow: 'hidden' }">
                <img :src="feature.image" :alt="feature.name" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
              </div>
              <div :style="{ flex: 1 }">
                <h4 :style="{ margin: '0 0 8px 0', fontSize: '16px', fontWeight: '500' }">{{ feature.name }}</h4>
                <p :style="{ margin: 0, fontSize: '14px', opacity: '0.65', lineHeight: '1.6' }">{{ feature.description }}</p>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 识别技巧 -->
        <a-card v-if="knowledge.tips" title="识别技巧" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '20px' }">
            <div 
              v-for="(tip, index) in knowledge.tips" 
              :key="index"
              :style="{ display: 'flex', gap: '16px', padding: '20px', borderRadius: '8px', background: '#fafafa' }"
            >
              <div :style="{ width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', background: '#1890ff', color: 'white', fontSize: '18px', fontWeight: '600', flexShrink: 0 }">{{ index + 1 }}</div>
              <div :style="{ flex: 1 }">
                <h4 :style="{ margin: '0 0 8px 0', fontSize: '16px', fontWeight: '500' }">{{ tip.title }}</h4>
                <p :style="{ margin: 0, fontSize: '14px', opacity: '0.65', lineHeight: '1.6' }">{{ tip.description }}</p>
                <div v-if="tip.examples" :style="{ marginTop: '16px' }">
                  <h5 :style="{ margin: '0 0 12px 0', fontSize: '14px', fontWeight: '500' }">示例：</h5>
                  <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '12px' }">
                    <div 
                      v-for="(example, exIndex) in tip.examples" 
                      :key="exIndex"
                      :style="{ cursor: 'pointer', borderRadius: '8px', overflow: 'hidden', transition: 'all 0.3s ease' }"
                      @click="previewImage(example.image, example.description)"
                    >
                      <img :src="example.image" :alt="example.description" :style="{ width: '100%', height: '120px', objectFit: 'cover' }" />
                      <span :style="{ display: 'block', padding: '8px', fontSize: '13px', textAlign: 'center', background: '#fff' }">{{ example.description }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 相关图片 -->
        <a-card v-if="knowledge.gallery" title="相关图片" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px' }">
            <div 
              v-for="(image, index) in knowledge.gallery" 
              :key="index"
              :style="{ position: 'relative', cursor: 'pointer', borderRadius: '8px', overflow: 'hidden', transition: 'all 0.3s ease' }"
              @click="previewImage(image.url, image.description)"
            >
              <img :src="image.url" :alt="image.description" :style="{ width: '100%', height: '160px', objectFit: 'cover' }" />
              <div :style="{ position: 'absolute', bottom: 0, left: 0, right: 0, padding: '12px', background: 'rgba(0,0,0,0.6)', color: 'white' }">
                <span :style="{ fontSize: '13px' }">{{ image.description }}</span>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 相关知识测试 -->
        <a-card v-if="knowledge.quiz" title="知识测试" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
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
        <a-card title="快速信息" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
            <div :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">分类</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.category }}</span>
            </div>
            <div :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">识别难度</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.difficulty || '一般' }}</span>
            </div>
            <div v-if="knowledge.scientificName" :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">学名</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.scientificName }}</span>
            </div>
            <div v-if="knowledge.commonNames" :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">别名</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.commonNames.join('、') }}</span>
            </div>
            <div v-if="knowledge.habitat" :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">栖息地</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.habitat }}</span>
            </div>
            <div v-if="knowledge.size" :style="{ display: 'flex', justifyContent: 'space-between', padding: '12px', borderRadius: '6px', background: '#fafafa' }">
              <span :style="{ fontSize: '14px', opacity: '0.65' }">大小</span>
              <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ knowledge.size }}</span>
            </div>
          </div>
        </a-card>

        <!-- 识别要点 -->
        <a-card v-if="knowledge.keyPoints" title="识别要点" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
            <div 
              v-for="(point, index) in knowledge.keyPoints" 
              :key="index"
              :style="{ display: 'flex', gap: '12px', padding: '12px', borderRadius: '6px', background: '#fafafa' }"
            >
              <div :style="{ width: '32px', height: '32px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', background: '#52c41a', color: 'white', flexShrink: 0 }">
                <i :class="point.icon || 'fas fa-check'"></i>
              </div>
              <div :style="{ flex: 1 }">
                <strong :style="{ display: 'block', marginBottom: '4px', fontSize: '14px' }">{{ point.title }}</strong>
                <p :style="{ margin: 0, fontSize: '13px', opacity: '0.65', lineHeight: '1.6' }">{{ point.description }}</p>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 统计信息 -->
        <a-card title="统计信息" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px' }">
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px' }">
              <i class="fas fa-eye" :style="{ width: '20px' }"></i>
              <span>浏览量：{{ knowledge.views }}</span>
            </div>
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px' }">
              <i class="fas fa-bookmark" :style="{ width: '20px' }"></i>
              <span>收藏数：{{ knowledge.bookmarks || 0 }}</span>
            </div>
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px' }">
              <i class="fas fa-thumbs-up" :style="{ width: '20px' }"></i>
              <span>点赞数：{{ knowledge.likes || 0 }}</span>
            </div>
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px' }">
              <i class="fas fa-calendar" :style="{ width: '20px' }"></i>
              <span>创建时间：{{ knowledge.createTime }}</span>
            </div>
          </div>
        </a-card>

        <!-- 相关知识 -->
        <a-card title="相关知识" :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }">
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
            <div 
              v-for="related in relatedKnowledge" 
              :key="related.id"
              :style="{ display: 'flex', gap: '12px', padding: '12px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease', border: '1px solid #f0f0f0' }"
              @click="viewRelated(related)"
            >
              <img :src="related.image || '/api/placeholder/80/60'" :alt="related.title" :style="{ width: '80px', height: '60px', borderRadius: '6px', objectFit: 'cover', flexShrink: 0 }" />
              <div :style="{ flex: 1, minWidth: 0 }">
                <h4 :style="{ margin: '0 0 6px 0', fontSize: '14px', fontWeight: '500', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }">{{ related.title }}</h4>
                <p :style="{ margin: '0 0 8px 0', fontSize: '13px', opacity: '0.65', lineHeight: '1.4', overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }">{{ related.description }}</p>
                <div>
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
    
    const item = await KnowledgeAPI.getKnowledgeDetail(parseInt(knowledgeId))
    
    console.log('知识详情数据:', item)
    
    // 解析第一张图片
    let firstImage = ''
    if (item.images) {
      try {
        const images = JSON.parse(item.images)
        if (Array.isArray(images) && images.length > 0) {
          firstImage = FileAPI.getImageUrl(images[0])
        }
      } catch {
        firstImage = FileAPI.getImageUrl(item.images)
      }
    } else if (item.coverImage) {
      firstImage = FileAPI.getImageUrl(item.coverImage)
    } else if (item.imageUrl) {
      firstImage = FileAPI.getImageUrl(item.imageUrl)
    }
    
    // 转换数据格式
    knowledge.value = {
      id: item.id,
      title: item.title || item.name || '',
      description: item.description || '',
      category: '', // 需要从分类ID获取分类名称
      difficulty: '一般',
      image: firstImage,
      views: item.viewCount || 0,
      bookmarks: item.collectCount || 0,
      likes: item.likeCount || 0,
      createTime: item.createdAt?.split(' ')[0] || item.createdAt || '',
      updateTime: item.updatedAt?.split(' ')[0] || item.updatedAt || '',
      scientificName: '',
      commonNames: [], // 可以从tags解析
      habitat: '',
      size: '',
      content: item.content || item.description || item.detail || '',
      features: null,
      tips: null,
      keyPoints: null,
      gallery: parseGallery(item.images),
      quiz: null // 测试功能暂未实现
    }
    
    // 加载分类名称
    if (item.category) {
      loadCategoryName(item.category)
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

// 分类映射（本地数据）
const categoryMap: Record<string, string> = {
  'animals': '动物',
  'plants': '植物',
  'food': '食物',
  'vehicles': '交通工具',
  'buildings': '建筑',
  'objects': '物品',
  'nature': '自然',
  'tools': '工具'
}

// 加载分类名称
function loadCategoryName(categoryKey: string) {
  knowledge.value.category = categoryMap[categoryKey] || '未分类'
}

// 加载相关知识
async function loadRelatedKnowledge(itemId: string) {
  try {
    // 加载同分类的其他知识
    const response = await KnowledgeAPI.getKnowledgeList({
      category: knowledge.value.category,
      page: 1,
      size: 4
    })
    relatedKnowledge.value = response.data
      .filter((item: any) => item.id !== parseInt(itemId))
      .slice(0, 3)
      .map((item: any) => {
        // 解析图片
        let image = ''
        if (item.coverImage) {
          image = FileAPI.getImageUrl(item.coverImage)
        } else if (item.imageUrl) {
          image = FileAPI.getImageUrl(item.imageUrl)
        } else if (item.images) {
          try {
            const images = JSON.parse(item.images)
            if (Array.isArray(images) && images.length > 0) {
              image = FileAPI.getImageUrl(images[0])
            }
          } catch {
            image = FileAPI.getImageUrl(item.images)
          }
        }
        
        return {
          id: item.id,
          title: item.title || item.name,
          description: item.description || item.content?.substring(0, 100) || '',
          image: image || '/api/placeholder/80/60',
          category: categoryMap[item.category] || '未分类'
        }
      })
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
      await KnowledgeAPI.uncollectKnowledge(knowledge.value.id)
      isBookmarked.value = false
      knowledge.value.bookmarks--
      message.success('取消收藏')
    } else {
      // 收藏
      await KnowledgeAPI.collectKnowledge(knowledge.value.id)
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

function reportKnowledge() {
  // 举报功能暂未实现，显示提示信息
  message.info('举报功能开发中，敬请期待')
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

