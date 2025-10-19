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
  router.push(`/user/recognition/${item.id}?from=dashboard`)
}

function viewKnowledge(item: any) {
  router.push(`/user/knowledge/${item.id}`)
}

function viewPost(item: any) {
  router.push(`/user/community/post/${item.id}`)
}

function navigateToFeature(path: string) {
  router.push(path)
}
</script>

