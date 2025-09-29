<template>
  <div class="user-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userInfo.name }}！</h1>
          <p>开始您的智能图像识别之旅</p>
        </div>
        <div class="quick-actions">
          <a-button type="primary" size="large" @click="startRecognition">
            <template #icon>
              <i class="fas fa-camera"></i>
            </template>
            立即识别
          </a-button>
          <a-button size="large" @click="viewHistory">
            <template #icon>
              <i class="fas fa-history"></i>
            </template>
            查看历史
          </a-button>
        </div>
      </div>
      <div class="banner-image">
        <i class="fas fa-brain"></i>
      </div>
    </div>

    <!-- 统计卡片 -->
    <a-row :gutter="[24, 24]" class="stats-section">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-eye"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalRecognitions }}</div>
              <div class="stat-label">总识别次数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-heart"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.favorites }}</div>
              <div class="stat-label">我的收藏</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-comments"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.discussions }}</div>
              <div class="stat-label">参与讨论</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-thumbs-up"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.likes }}</div>
              <div class="stat-label">获得点赞</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 主要内容区域 -->
    <a-row :gutter="[24, 24]">
      <!-- 最近识别 -->
      <a-col :xs="24" :lg="12">
        <a-card title="最近识别" class="recent-card">
          <template #extra>
            <a-button type="link" @click="viewAllHistory">查看全部</a-button>
          </template>
          <div class="recent-list">
            <div 
              v-for="item in recentRecognitions" 
              :key="item.id"
              class="recent-item"
              @click="viewRecognitionDetail(item)"
            >
              <div class="item-image">
                <img :src="item.thumbnail" :alt="item.result" />
              </div>
              <div class="item-info">
                <div class="item-title">{{ item.result }}</div>
                <div class="item-time">{{ item.time }}</div>
              </div>
              <div class="item-confidence">
                <a-progress 
                  :percent="item.confidence" 
                  size="small" 
                  :show-info="false"
                />
                <span class="confidence-text">{{ item.confidence }}%</span>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 推荐内容 -->
      <a-col :xs="24" :lg="12">
        <a-card title="推荐内容" class="recommendation-card">
          <a-tabs>
            <a-tab-pane key="knowledge" tab="知识推荐">
              <div class="knowledge-list">
                <div 
                  v-for="item in recommendedKnowledge" 
                  :key="item.id"
                  class="knowledge-item"
                  @click="viewKnowledge(item)"
                >
                  <div class="knowledge-icon">
                    <i :class="item.icon"></i>
                  </div>
                  <div class="knowledge-info">
                    <div class="knowledge-title">{{ item.title }}</div>
                    <div class="knowledge-desc">{{ item.description }}</div>
                  </div>
                </div>
              </div>
            </a-tab-pane>
            <a-tab-pane key="community" tab="社区热门">
              <div class="community-list">
                <div 
                  v-for="item in hotCommunityPosts" 
                  :key="item.id"
                  class="community-item"
                  @click="viewPost(item)"
                >
                  <div class="post-content">
                    <div class="post-title">{{ item.title }}</div>
                    <div class="post-meta">
                      <span>{{ item.author }}</span>
                      <span>{{ item.likes }} 点赞</span>
                      <span>{{ item.replies }} 回复</span>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <!-- 功能入口 -->
    <a-card title="功能导航" class="features-card">
      <a-row :gutter="[24, 24]">
        <a-col :xs="12" :sm="8" :lg="4" v-for="feature in features" :key="feature.key">
          <div class="feature-item" @click="navigateToFeature(feature.path)">
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <div class="feature-title">{{ feature.title }}</div>
            <div class="feature-desc">{{ feature.description }}</div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const router = useRouter()

// 用户信息
const userInfo = reactive({
  name: '张三',
  avatar: ''
})

// 统计数据
const stats = reactive({
  totalRecognitions: 156,
  favorites: 23,
  discussions: 8,
  likes: 45
})

// 最近识别记录
const recentRecognitions = ref([
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

// 推荐知识
const recommendedKnowledge = ref([
  {
    id: 1,
    title: '犬类品种识别指南',
    description: '学习如何识别不同的犬类品种',
    icon: 'fas fa-dog'
  },
  {
    id: 2,
    title: '植物识别技巧',
    description: '掌握植物识别的基本方法',
    icon: 'fas fa-leaf'
  },
  {
    id: 3,
    title: '食物营养分析',
    description: '了解食物的营养成分',
    icon: 'fas fa-apple-alt'
  }
])

// 社区热门
const hotCommunityPosts = ref([
  {
    id: 1,
    title: '如何提高图像识别的准确率？',
    author: '李四',
    likes: 24,
    replies: 12
  },
  {
    id: 2,
    title: '分享一些有趣的识别结果',
    author: '王五',
    likes: 18,
    replies: 8
  },
  {
    id: 3,
    title: '讨论：AI识别的未来发展',
    author: '赵六',
    likes: 31,
    replies: 15
  }
])

// 功能导航
const features = ref([
  {
    key: 'recognition',
    title: '图像识别',
    description: '上传图片进行识别',
    icon: 'fas fa-camera',
    path: '/user/recognition'
  },
  {
    key: 'batch',
    title: '批量识别',
    description: '一次处理多张图片',
    icon: 'fas fa-images',
    path: '/user/recognition/batch'
  },
  {
    key: 'knowledge',
    title: '知识库',
    description: '浏览百科知识',
    icon: 'fas fa-book',
    path: '/user/knowledge'
  },
  {
    key: 'community',
    title: '社区',
    description: '参与讨论交流',
    icon: 'fas fa-users',
    path: '/user/community'
  },
  {
    key: 'history',
    title: '历史记录',
    description: '查看识别历史',
    icon: 'fas fa-history',
    path: '/user/history'
  },
  {
    key: 'favorites',
    title: '我的收藏',
    description: '管理收藏内容',
    icon: 'fas fa-heart',
    path: '/user/favorites'
  }
])

// 方法
function startRecognition() {
  router.push('/user/recognition')
}

function viewHistory() {
  router.push('/user/history')
}

function viewAllHistory() {
  router.push('/user/history')
}

function viewRecognitionDetail(item: any) {
  message.info(`查看识别详情：${item.result}`)
}

function viewKnowledge(item: any) {
  message.info(`查看知识：${item.title}`)
}

function viewPost(item: any) {
  message.info(`查看帖子：${item.title}`)
}

function navigateToFeature(path: string) {
  router.push(path)
}
</script>

<style scoped>
.user-dashboard {
  padding: 0;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: white;
  position: relative;
  overflow: hidden;
}

.banner-content {
  flex: 1;
  z-index: 2;
}

.welcome-text h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
  color: white;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.quick-actions {
  display: flex;
  gap: 16px;
}

.banner-image {
  font-size: 120px;
  opacity: 0.1;
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1890ff, #40a9ff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 最近识别 */
.recent-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.recent-item:hover {
  background: #fafafa;
  border-color: #1890ff;
}

.item-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-title {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-confidence {
  width: 80px;
  text-align: right;
}

.confidence-text {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
  display: block;
}

/* 推荐内容 */
.recommendation-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.knowledge-list,
.community-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.knowledge-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.knowledge-item:hover {
  background: #fafafa;
  border-color: #1890ff;
}

.knowledge-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #52c41a, #73d13d);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.knowledge-title {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.knowledge-desc {
  font-size: 12px;
  color: #999;
}

.community-item {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.community-item:hover {
  background: #fafafa;
  border-color: #1890ff;
}

.post-title {
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.post-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 功能导航 */
.features-card {
  border-radius: 12px;
}

.feature-item {
  text-align: center;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.feature-item:hover {
  background: #fafafa;
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.feature-icon {
  font-size: 32px;
  color: #1890ff;
  margin-bottom: 8px;
}

.feature-title {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 24px;
    flex-direction: column;
    text-align: center;
  }
  
  .banner-image {
    position: static;
    transform: none;
    margin-top: 20px;
    font-size: 80px;
  }
  
  .quick-actions {
    justify-content: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 576px) {
  .welcome-banner {
    padding: 20px;
  }
  
  .welcome-text h1 {
    font-size: 24px;
  }
  
  .quick-actions {
    flex-direction: column;
    width: 100%;
  }
}
</style>
