<template>
  <div class="knowledge-detail-page">
    <!-- 返回按钮 -->
    <div class="back-section">
      <a-button @click="goBack" size="large">
        <i class="fas fa-arrow-left"></i>
        返回知识库
      </a-button>
    </div>

    <!-- 知识详情卡片 -->
    <a-card class="knowledge-card">
      <!-- 知识头部 -->
      <div class="knowledge-header">
        <div class="knowledge-main-info">
          <div class="knowledge-image">
            <img :src="knowledge.image" :alt="knowledge.title" />
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
              <img :src="related.image" :alt="related.title" />
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
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()

// 知识详情数据
const knowledge = ref({
  id: 1,
  title: '金毛犬识别指南',
  description: '金毛寻回犬是一种大型犬种，以其友善的性格和金色的毛发而闻名',
  category: '动物',
  difficulty: '简单',
  image: '/api/placeholder/400/300',
  views: 1234,
  bookmarks: 89,
  likes: 156,
  createTime: '2024-01-15',
  updateTime: '2025-01-01',
  scientificName: 'Canis lupus familiaris',
  commonNames: ['金毛', '金毛寻回犬', 'Golden Retriever'],
  habitat: '家庭环境，适应性强',
  size: '体重：25-34kg，身高：51-61cm',
  content: `
    <h3>基本介绍</h3>
    <p>金毛寻回犬（Golden Retriever）是一种大型犬，原产于苏格兰，最初培育用于狩猎水鸟。它们以其友善、聪明和忠诚的性格而闻名于世。</p>
    
    <h3>外观特征</h3>
    <ul>
      <li><strong>毛色：</strong>金黄色到深金色，毛发浓密且有光泽</li>
      <li><strong>体型：</strong>中大型犬，雄性通常比雌性更大</li>
      <li><strong>头部：</strong>宽阔的头骨，友善的表情</li>
      <li><strong>耳朵：</strong>中等大小，下垂</li>
      <li><strong>尾巴：</strong>毛发浓密，呈羽毛状</li>
    </ul>
    
    <h3>性格特点</h3>
    <ul>
      <li>友善温和，对人类和其他动物都很友好</li>
      <li>聪明易训，学习能力强</li>
      <li>忠诚可靠，是优秀的家庭伴侣</li>
      <li>活泼好动，需要充足的运动</li>
      <li>适合与儿童相处</li>
    </ul>
    
    <h3>护理要点</h3>
    <ul>
      <li>定期梳理毛发，保持清洁</li>
      <li>提供充足的运动和活动空间</li>
      <li>均衡饮食，控制体重</li>
      <li>定期健康检查</li>
    </ul>
  `,
  features: [
    {
      id: 1,
      name: '金黄色毛发',
      description: '最显著的特征，毛色从浅金色到深金色不等',
      image: '/api/placeholder/200/150'
    },
    {
      id: 2,
      name: '友善表情',
      description: '眼神温和，表情友善，给人亲近感',
      image: '/api/placeholder/200/150'
    },
    {
      id: 3,
      name: '中大型体型',
      description: '体型匀称，肌肉发达，运动能力强',
      image: '/api/placeholder/200/150'
    }
  ],
  tips: [
    {
      title: '观察毛色',
      description: '金毛犬的毛色是最明显的识别特征，通常呈现金黄色到深金色',
      examples: [
        {
          image: '/api/placeholder/150/150',
          description: '浅金色毛发'
        },
        {
          image: '/api/placeholder/150/150',
          description: '深金色毛发'
        }
      ]
    },
    {
      title: '注意体型特征',
      description: '金毛犬属于中大型犬，体型匀称，肌肉发达',
      examples: [
        {
          image: '/api/placeholder/150/150',
          description: '侧面体型'
        },
        {
          image: '/api/placeholder/150/150',
          description: '正面体型'
        }
      ]
    },
    {
      title: '观察面部表情',
      description: '金毛犬通常表情友善，眼神温和',
      examples: [
        {
          image: '/api/placeholder/150/150',
          description: '友善表情'
        }
      ]
    }
  ],
  keyPoints: [
    {
      title: '毛色识别',
      description: '金黄色是主要特征，从浅金到深金都有',
      icon: 'fas fa-paint-brush'
    },
    {
      title: '体型大小',
      description: '中大型犬，体重通常在25-34kg之间',
      icon: 'fas fa-weight'
    },
    {
      title: '耳朵形状',
      description: '中等大小的下垂耳，毛发浓密',
      icon: 'fas fa-leaf'
    },
    {
      title: '性格表现',
      description: '友善温和，对人类表现出亲近感',
      icon: 'fas fa-heart'
    }
  ],
  gallery: [
    {
      url: '/api/placeholder/300/200',
      description: '成年金毛犬全身照'
    },
    {
      url: '/api/placeholder/300/200',
      description: '金毛犬头部特写'
    },
    {
      url: '/api/placeholder/300/200',
      description: '金毛犬幼犬'
    },
    {
      url: '/api/placeholder/300/200',
      description: '金毛犬运动姿态'
    }
  ],
  quiz: [
    {
      question: '金毛犬最显著的特征是什么？',
      image: '/api/placeholder/300/200',
      options: ['黑色毛发', '金黄色毛发', '白色毛发', '棕色毛发'],
      correct: 1
    },
    {
      question: '金毛犬属于什么体型？',
      options: ['小型犬', '中型犬', '中大型犬', '超大型犬'],
      correct: 2
    },
    {
      question: '金毛犬的性格特点是什么？',
      options: ['凶猛好斗', '友善温和', '胆小怯懦', '冷漠疏离'],
      correct: 1
    }
  ]
})

// 相关知识
const relatedKnowledge = ref([
  {
    id: 2,
    title: '拉布拉多犬识别',
    description: '另一种常见的大型犬种，与金毛犬有相似之处',
    image: '/api/placeholder/80/60',
    category: '动物'
  },
  {
    id: 3,
    title: '边境牧羊犬识别',
    description: '聪明的中型犬种，善于牧羊',
    image: '/api/placeholder/80/60',
    category: '动物'
  },
  {
    id: 4,
    title: '犬类毛色识别指南',
    description: '不同犬种的毛色特征对比',
    image: '/api/placeholder/80/60',
    category: '动物'
  }
])

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
    loadKnowledgeDetail(knowledgeId)
  }
  
  // 增加浏览量
  knowledge.value.views++
})

// 方法
function goBack() {
  router.push('/user/knowledge')
}

function loadKnowledgeDetail(knowledgeId: string | number) {
  // 这里应该调用API获取知识详情
  console.log('Loading knowledge detail for ID:', knowledgeId)
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

function toggleBookmark() {
  isBookmarked.value = !isBookmarked.value
  knowledge.value.bookmarks += isBookmarked.value ? 1 : -1
  message.success(isBookmarked.value ? '收藏成功' : '取消收藏')
}

function shareKnowledge() {
  message.info('分享功能开发中')
}

function printKnowledge() {
  window.print()
}

function reportKnowledge() {
  message.info('举报功能开发中')
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

<style scoped>
.knowledge-detail-page {
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

/* 知识卡片 */
.knowledge-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 知识头部 */
.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.knowledge-main-info {
  display: flex;
  gap: 24px;
  flex: 1;
}

.knowledge-image {
  flex-shrink: 0;
}

.knowledge-image img {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
}

.knowledge-info {
  flex: 1;
}

.knowledge-title {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 16px;
  line-height: 1.3;
}

.knowledge-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #999;
}

.meta-item i {
  color: #1890ff;
}

.knowledge-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

/* 内容卡片 */
.content-card,
.features-card,
.tips-card,
.gallery-card,
.quiz-card,
.info-card,
.key-points-card,
.stats-card,
.related-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

/* 知识内容 */
.knowledge-content {
  font-size: 16px;
  line-height: 1.8;
  color: #262626;
}

.knowledge-content h3 {
  color: #262626;
  font-size: 20px;
  font-weight: 600;
  margin: 24px 0 16px;
}

.knowledge-content ul {
  margin: 16px 0;
  padding-left: 24px;
}

.knowledge-content li {
  margin-bottom: 8px;
}

.knowledge-content strong {
  color: #1890ff;
  font-weight: 600;
}

/* 特征网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.feature-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.feature-image img {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.feature-content h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 16px;
}

.feature-content p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 识别技巧 */
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.tip-item {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.tip-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.tip-content {
  flex: 1;
}

.tip-content h4 {
  color: #262626;
  margin-bottom: 12px;
  font-size: 18px;
}

.tip-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.tip-content h5 {
  color: #262626;
  margin-bottom: 12px;
  font-size: 14px;
}

.examples-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.example-item {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.example-item:hover {
  transform: translateY(-2px);
}

.example-item img {
  width: 100%;
  height: 100px;
  border-radius: 6px;
  object-fit: cover;
  margin-bottom: 8px;
}

.example-label {
  font-size: 12px;
  color: #666;
}

/* 图片画廊 */
.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.gallery-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.gallery-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.gallery-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  padding: 16px;
  transform: translateY(100%);
  transition: transform 0.3s;
}

.gallery-item:hover .image-overlay {
  transform: translateY(0);
}

.image-description {
  font-size: 14px;
  margin: 0;
}

/* 测试部分 */
.quiz-section {
  padding: 20px;
}

.quiz-question h3 {
  color: #262626;
  margin-bottom: 24px;
}

.question-content p {
  font-size: 16px;
  color: #262626;
  margin-bottom: 16px;
}

.question-image {
  margin-bottom: 20px;
  text-align: center;
}

.question-image img {
  max-width: 300px;
  height: 200px;
  border-radius: 8px;
  object-fit: cover;
}

.question-options {
  margin-bottom: 24px;
}

.option-item {
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.3s;
}

.option-item:hover {
  background: #fafafa;
}

.quiz-actions {
  text-align: center;
}

/* 测试结果 */
.quiz-result {
  text-align: center;
  padding: 32px;
}

.result-header {
  margin-bottom: 32px;
}

.result-header i {
  font-size: 48px;
  color: #faad14;
  margin-bottom: 16px;
}

.result-header h3 {
  color: #262626;
  font-size: 24px;
}

.result-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-bottom: 32px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  display: block;
  color: #262626;
  font-size: 24px;
  font-weight: bold;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 快速信息 */
.quick-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #666;
  font-size: 14px;
}

.info-value {
  color: #262626;
  font-weight: 500;
  text-align: right;
  flex: 1;
  margin-left: 16px;
}

/* 识别要点 */
.key-points {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.key-point {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
}

.point-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #52c41a;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.point-content strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.point-content p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 统计信息 */
.knowledge-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.stat-row i {
  color: #1890ff;
  width: 16px;
}

/* 相关知识 */
.related-list {
  display: flex;
  flex-direction: column;
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
}

.related-item img {
  width: 60px;
  height: 45px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
}

.related-content {
  flex: 1;
}

.related-content h4 {
  color: #262626;
  font-size: 14px;
  margin-bottom: 4px;
}

.related-content p {
  color: #666;
  font-size: 12px;
  margin-bottom: 8px;
  line-height: 1.4;
}

.related-meta {
  display: flex;
  align-items: center;
}

/* 图片预览 */
.image-preview-container {
  text-align: center;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
}

.preview-description {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .knowledge-detail-page {
    padding: 16px;
  }
  
  .knowledge-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .knowledge-main-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .knowledge-title {
    font-size: 24px;
  }
  
  .knowledge-actions {
    flex-direction: row;
    justify-content: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .tip-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .result-stats {
    flex-direction: column;
    gap: 24px;
  }
}

@media (max-width: 576px) {
  .knowledge-detail-page {
    padding: 12px;
  }
  
  .knowledge-image img {
    width: 150px;
    height: 112px;
  }
  
  .knowledge-title {
    font-size: 20px;
  }
  
  .examples-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .image-gallery {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
