<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="header-content">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <a-avatar :size="100" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.name.charAt(0) }}
            </a-avatar>
            <div class="avatar-overlay" @click="showAvatarModal">
              <i class="fas fa-camera"></i>
              <span>更换头像</span>
            </div>
          </div>
        </div>
        
        <div class="user-info">
          <h1>{{ userInfo.name }}</h1>
          <p>{{ userInfo.bio }}</p>
          <div class="user-meta">
            <span>
              <i class="fas fa-calendar"></i>
              加入于 {{ userInfo.joinDate }}
            </span>
            <span>
              <i class="fas fa-map-marker-alt"></i>
              {{ userInfo.location }}
            </span>
          </div>
        </div>
        
        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-number">{{ userStats.recognitions }}</div>
            <div class="stat-label">识别次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.posts }}</div>
            <div class="stat-label">发布帖子</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.likes }}</div>
            <div class="stat-label">获得点赞</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.followers }}</div>
            <div class="stat-label">粉丝</div>
          </div>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <a-row :gutter="24">
        <!-- 左侧内容 -->
        <a-col :xs="24" :lg="16">
          <a-tabs v-model:activeKey="activeTab">
            <!-- 我的动态 -->
            <a-tab-pane key="activities" tab="我的动态">
              <div class="activities-list">
                <div 
                  v-for="activity in activities" 
                  :key="activity.id"
                  class="activity-item"
                >
                  <div class="activity-icon">
                    <i :class="getActivityIcon(activity.type)"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-text">{{ activity.description }}</div>
                    <div class="activity-time">{{ activity.time }}</div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 我的帖子 -->
            <a-tab-pane key="posts" tab="我的帖子">
              <div class="posts-list">
                <div 
                  v-for="post in userPosts" 
                  :key="post.id"
                  class="post-item"
                  @click="viewPost(post)"
                >
                  <div class="post-content">
                    <h4>{{ post.title }}</h4>
                    <p>{{ post.excerpt }}</p>
                    <div class="post-meta">
                      <a-tag :color="getTypeColor(post.type)">
                        {{ getTypeName(post.type) }}
                      </a-tag>
                      <span class="post-stats">
                        <i class="fas fa-thumbs-up"></i>
                        {{ post.likes }}
                        <i class="fas fa-comment"></i>
                        {{ post.replies }}
                      </span>
                      <span class="post-time">{{ post.createTime }}</span>
                    </div>
                  </div>
                  <div v-if="post.image" class="post-image">
                    <img :src="post.image" :alt="post.title" />
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 我的收藏 -->
            <a-tab-pane key="favorites" tab="我的收藏">
              <a-tabs type="card">
                <a-tab-pane key="recognition" tab="识别结果">
                  <div class="favorites-grid">
                    <div 
                      v-for="item in favoriteRecognitions" 
                      :key="item.id"
                      class="favorite-item"
                      @click="viewFavorite(item)"
                    >
                      <div class="favorite-image">
                        <img :src="item.thumbnail" :alt="item.result" />
                      </div>
                      <div class="favorite-info">
                        <div class="favorite-title">{{ item.result }}</div>
                        <div class="favorite-confidence">{{ item.confidence }}%</div>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="knowledge" tab="知识内容">
                  <div class="knowledge-list">
                    <div 
                      v-for="item in favoriteKnowledge" 
                      :key="item.id"
                      class="knowledge-item"
                      @click="viewKnowledge(item)"
                    >
                      <div class="knowledge-icon">
                        <i :class="item.icon"></i>
                      </div>
                      <div class="knowledge-content">
                        <h4>{{ item.title }}</h4>
                        <p>{{ item.description }}</p>
                        <a-tag :color="getCategoryColor(item.category)">
                          {{ item.category }}
                        </a-tag>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="posts" tab="帖子">
                  <div class="favorite-posts">
                    <div 
                      v-for="post in favoritePosts" 
                      :key="post.id"
                      class="favorite-post"
                      @click="viewPost(post)"
                    >
                      <div class="post-header">
                        <span class="post-author">{{ post.author }}</span>
                        <span class="post-time">{{ post.createTime }}</span>
                      </div>
                      <div class="post-content">{{ post.content }}</div>
                      <div class="post-stats">
                        <span>{{ post.likes }} 点赞</span>
                        <span>{{ post.replies }} 回复</span>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
              </a-tabs>
            </a-tab-pane>

            <!-- 我的点赞 -->
            <a-tab-pane key="likes" tab="我的点赞">
              <div class="likes-container">
                <div class="likes-stats">
                  <a-row :gutter="16">
                    <a-col :span="8">
                      <a-statistic title="总点赞数" :value="likeStats.total" suffix="次" />
                    </a-col>
                    <a-col :span="8">
                      <a-statistic title="本月点赞" :value="likeStats.thisMonth" suffix="次" />
                    </a-col>
                    <a-col :span="8">
                      <a-statistic title="今日点赞" :value="likeStats.today" suffix="次" />
                    </a-col>
                  </a-row>
                </div>

                <a-tabs type="card" class="likes-tabs">
                  <a-tab-pane key="posts" tab="帖子点赞">
                    <div class="liked-posts">
                      <div 
                        v-for="post in likedPosts" 
                        :key="post.id"
                        class="liked-item"
                        @click="viewPost(post)"
                      >
                        <div class="liked-content">
                          <div class="liked-header">
                            <a-avatar :src="post.authorAvatar" size="small">
                              {{ post.author.charAt(0) }}
                            </a-avatar>
                            <div class="author-info">
                              <span class="author-name">{{ post.author }}</span>
                              <span class="like-time">{{ post.likeTime }}</span>
                            </div>
                          </div>
                          <div class="post-title">{{ post.title }}</div>
                          <div class="post-excerpt">{{ post.excerpt }}</div>
                          <div class="post-meta">
                            <a-tag :color="getTypeColor(post.type)">
                              {{ getTypeName(post.type) }}
                            </a-tag>
                            <span class="post-stats">
                              <i class="fas fa-thumbs-up"></i>
                              {{ post.totalLikes }}
                              <i class="fas fa-comment"></i>
                              {{ post.replies }}
                            </span>
                          </div>
                        </div>
                        <div class="like-indicator">
                          <i class="fas fa-heart liked"></i>
                        </div>
                      </div>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="comments" tab="评论点赞">
                    <div class="liked-comments">
                      <div 
                        v-for="comment in likedComments" 
                        :key="comment.id"
                        class="liked-item"
                      >
                        <div class="liked-content">
                          <div class="liked-header">
                            <a-avatar :src="comment.authorAvatar" size="small">
                              {{ comment.author.charAt(0) }}
                            </a-avatar>
                            <div class="author-info">
                              <span class="author-name">{{ comment.author }}</span>
                              <span class="like-time">{{ comment.likeTime }}</span>
                            </div>
                          </div>
                          <div class="comment-content">{{ comment.content }}</div>
                          <div class="comment-context">
                            <span class="context-label">来自帖子：</span>
                            <span class="context-title">{{ comment.postTitle }}</span>
                          </div>
                          <div class="comment-stats">
                            <span>
                              <i class="fas fa-thumbs-up"></i>
                              {{ comment.likes }}
                            </span>
                          </div>
                        </div>
                        <div class="like-indicator">
                          <i class="fas fa-heart liked"></i>
                        </div>
                      </div>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="knowledge" tab="知识点赞">
                    <div class="liked-knowledge">
                      <div 
                        v-for="item in likedKnowledge" 
                        :key="item.id"
                        class="liked-item"
                        @click="viewKnowledge(item)"
                      >
                        <div class="liked-content">
                          <div class="knowledge-header">
                            <div class="knowledge-icon">
                              <i :class="item.icon"></i>
                            </div>
                            <div class="knowledge-info">
                              <div class="knowledge-title">{{ item.title }}</div>
                              <div class="like-time">{{ item.likeTime }}</div>
                            </div>
                          </div>
                          <div class="knowledge-description">{{ item.description }}</div>
                          <div class="knowledge-meta">
                            <a-tag :color="getCategoryColor(item.category)">
                              {{ item.category }}
                            </a-tag>
                            <span class="knowledge-stats">
                              <i class="fas fa-thumbs-up"></i>
                              {{ item.likes }}
                            </span>
                          </div>
                        </div>
                        <div class="like-indicator">
                          <i class="fas fa-heart liked"></i>
                        </div>
                      </div>
                    </div>
                  </a-tab-pane>
                </a-tabs>
              </div>
            </a-tab-pane>

            <!-- 设置 -->
            <a-tab-pane key="settings" tab="设置">
              <a-card title="个人资料" class="settings-card">
                <a-form layout="vertical">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-form-item label="昵称">
                        <a-input v-model:value="editInfo.name" />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item label="邮箱">
                        <a-input v-model:value="editInfo.email" />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-form-item label="个人简介">
                    <a-textarea v-model:value="editInfo.bio" :rows="3" />
                  </a-form-item>
                  <a-form-item label="所在地">
                    <a-input v-model:value="editInfo.location" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveProfile">
                      保存修改
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-card>

              <a-card title="隐私设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.showEmail">
                      公开邮箱地址
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.showLocation">
                      公开所在地
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="privacySettings.allowFollow">
                      允许其他用户关注我
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="savePrivacy">
                      保存设置
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-card>

            </a-tab-pane>
          </a-tabs>
        </a-col>

        <!-- 右侧侧边栏 -->
        <a-col :xs="24" :lg="8">
          <!-- 成就徽章 -->
          <a-card title="成就徽章" class="achievements-card">
            <div class="badges-grid">
              <div 
                v-for="badge in badges" 
                :key="badge.id"
                class="badge-item"
                :class="{ 'earned': badge.earned }"
              >
                <div class="badge-icon">
                  <i :class="badge.icon"></i>
                </div>
                <div class="badge-info">
                  <div class="badge-name">{{ badge.name }}</div>
                  <div class="badge-desc">{{ badge.description }}</div>
                </div>
              </div>
            </div>
          </a-card>

          <!-- 最近访客 -->
          <a-card title="最近访客" class="visitors-card">
            <div class="visitors-list">
              <div 
                v-for="visitor in recentVisitors" 
                :key="visitor.id"
                class="visitor-item"
              >
                <a-avatar :src="visitor.avatar" size="small">
                  {{ visitor.name.charAt(0) }}
                </a-avatar>
                <div class="visitor-info">
                  <div class="visitor-name">{{ visitor.name }}</div>
                  <div class="visitor-time">{{ visitor.visitTime }}</div>
                </div>
              </div>
            </div>
          </a-card>

          <!-- 数据统计 -->
          <a-card title="数据统计" class="stats-card">
            <div class="stats-chart">
              <div class="chart-placeholder">
                <i class="fas fa-chart-line"></i>
                <span>本月识别趋势图</span>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 头像上传模态框 -->
    <a-modal
      v-model:open="avatarModalVisible"
      title="更换头像"
      :width="600"
      @ok="handleAvatarUpload"
      @cancel="cancelAvatarUpload"
      :confirm-loading="uploadLoading"
    >
      <div class="avatar-upload-container">
        <!-- 当前头像预览 -->
        <div class="current-avatar">
          <h4>当前头像</h4>
          <a-avatar :size="80" :src="userInfo.avatar" class="preview-avatar">
            {{ userInfo.name.charAt(0) }}
          </a-avatar>
        </div>

        <!-- 新头像预览 -->
        <div class="new-avatar" v-if="newAvatarUrl">
          <h4>新头像预览</h4>
          <a-avatar :size="80" :src="newAvatarUrl" class="preview-avatar" />
        </div>

        <!-- 上传区域 -->
        <div class="upload-section">
          <a-upload
            v-model:file-list="avatarFileList"
            name="avatar"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
            :before-upload="beforeAvatarUpload"
            @change="handleAvatarChange"
            accept="image/*"
          >
            <div v-if="!newAvatarUrl" class="upload-placeholder">
              <i class="fas fa-plus"></i>
              <div class="upload-text">选择图片</div>
            </div>
            <img v-else :src="newAvatarUrl" alt="avatar" class="upload-preview" />
          </a-upload>
          
          <div class="upload-tips">
            <p><i class="fas fa-info-circle"></i> 支持 JPG、PNG 格式</p>
            <p><i class="fas fa-info-circle"></i> 建议尺寸 200x200 像素</p>
            <p><i class="fas fa-info-circle"></i> 文件大小不超过 2MB</p>
          </div>
        </div>

        <!-- 预设头像选择 -->
        <div class="preset-avatars">
          <h4>选择预设头像</h4>
          <div class="preset-grid">
            <div 
              v-for="(preset, index) in presetAvatars" 
              :key="index"
              class="preset-item"
              :class="{ 'selected': selectedPreset === index }"
              @click="selectPresetAvatar(preset, index)"
            >
              <a-avatar :size="50" :src="preset.url" />
              <span class="preset-name">{{ preset.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const activeTab = ref('activities')

// 根据路由设置活动标签
function setActiveTabFromRoute() {
  const routeName = route.name
  switch (routeName) {
    case 'UserProfile':
      activeTab.value = 'activities'
      break
    case 'UserFavorites':
      activeTab.value = 'favorites'
      break
    case 'UserSettings':
      activeTab.value = 'settings'
      break
    default:
      activeTab.value = 'activities'
  }
}

// 监听路由变化
watch(() => route.name, () => {
  setActiveTabFromRoute()
}, { immediate: true })

// 组件挂载时设置标签
onMounted(() => {
  setActiveTabFromRoute()
})

// 用户信息
const userInfo = reactive({
  name: '张三',
  bio: '热爱AI技术的图像识别爱好者，喜欢分享和交流技术经验',
  avatar: '',
  joinDate: '2025年1月',
  location: '北京市',
  email: 'zhangsan@example.com'
})

// 编辑信息
const editInfo = reactive({
  name: userInfo.name,
  bio: userInfo.bio,
  location: userInfo.location,
  email: userInfo.email
})

// 用户统计
const userStats = reactive({
  recognitions: 156,
  posts: 12,
  likes: 89,
  followers: 45
})

// 隐私设置
const privacySettings = reactive({
  showEmail: false,
  showLocation: true,
  allowFollow: true
})


// 活动记录
const activities = ref([
  {
    id: 1,
    type: 'recognition',
    description: '识别了一张金毛犬的图片，置信度 95%',
    time: '2小时前'
  },
  {
    id: 2,
    type: 'post',
    description: '发布了新帖子《AI识别技巧分享》',
    time: '1天前'
  },
  {
    id: 3,
    type: 'like',
    description: '点赞了帖子《深度学习在图像识别中的应用》',
    time: '2天前'
  },
  {
    id: 4,
    type: 'follow',
    description: '关注了用户 "AI专家李四"',
    time: '3天前'
  }
])

// 用户帖子
const userPosts = ref([
  {
    id: 1,
    title: 'AI识别技巧分享',
    excerpt: '分享一些提高图像识别准确率的实用技巧...',
    type: 'share',
    likes: 24,
    replies: 8,
    createTime: '1天前',
    image: '/api/placeholder/100/80'
  },
  {
    id: 2,
    title: '遇到识别问题求助',
    excerpt: '最近在识别某些特殊角度的物体时准确率较低...',
    type: 'question',
    likes: 12,
    replies: 15,
    createTime: '3天前',
    image: null
  }
])

// 收藏的识别结果
const favoriteRecognitions = ref([
  {
    id: 1,
    result: '金毛犬',
    confidence: 95,
    thumbnail: '/api/placeholder/120/120'
  },
  {
    id: 2,
    result: '玫瑰花',
    confidence: 88,
    thumbnail: '/api/placeholder/120/120'
  }
])

// 收藏的知识
const favoriteKnowledge = ref([
  {
    id: 1,
    title: '犬类品种识别指南',
    description: '详细介绍各种犬类品种的特征',
    category: '动物',
    icon: 'fas fa-dog'
  },
  {
    id: 2,
    title: '花卉识别技巧',
    description: '掌握花卉识别的基本方法',
    category: '植物',
    icon: 'fas fa-flower'
  }
])

// 收藏的帖子
const favoritePosts = ref([
  {
    id: 1,
    author: '王五',
    content: '关于AI识别准确率提升的一些思考...',
    likes: 45,
    replies: 18,
    createTime: '2天前'
  }
])

// 点赞统计
const likeStats = reactive({
  total: 234,
  thisMonth: 45,
  today: 3
})

// 点赞的帖子
const likedPosts = ref([
  {
    id: 1,
    title: '深度学习在图像识别中的应用',
    excerpt: '本文详细介绍了深度学习技术在现代图像识别系统中的核心应用...',
    author: '李教授',
    authorAvatar: '',
    type: 'share',
    totalLikes: 128,
    replies: 34,
    likeTime: '2小时前'
  },
  {
    id: 2,
    title: '如何提高识别准确率？',
    excerpt: '在实际使用中，我发现通过调整这些参数可以显著提升识别效果...',
    author: '技术小王',
    authorAvatar: '',
    type: 'question',
    totalLikes: 89,
    replies: 23,
    likeTime: '1天前'
  },
  {
    id: 3,
    title: 'AI识别的未来趋势讨论',
    excerpt: '随着技术的不断发展，AI识别将会在更多领域发挥重要作用...',
    author: '未来学者',
    authorAvatar: '',
    type: 'discussion',
    totalLikes: 156,
    replies: 67,
    likeTime: '3天前'
  }
])

// 点赞的评论
const likedComments = ref([
  {
    id: 1,
    author: '专家张三',
    authorAvatar: '',
    content: '这个观点很有见地，特别是关于数据预处理的部分，确实能够显著提升识别准确率。',
    postTitle: '深度学习在图像识别中的应用',
    likes: 45,
    likeTime: '3小时前'
  },
  {
    id: 2,
    author: '算法工程师',
    authorAvatar: '',
    content: '建议可以尝试使用数据增强技术，比如旋转、缩放等，这样可以提高模型的泛化能力。',
    postTitle: '如何提高识别准确率？',
    likes: 32,
    likeTime: '1天前'
  },
  {
    id: 3,
    author: 'AI研究员',
    authorAvatar: '',
    content: '同意楼主的看法，多模态融合确实是未来的发展方向，值得深入研究。',
    postTitle: 'AI识别的未来趋势讨论',
    likes: 28,
    likeTime: '2天前'
  }
])

// 点赞的知识
const likedKnowledge = ref([
  {
    id: 1,
    title: '卷积神经网络基础',
    description: 'CNN在图像识别中的基本原理和应用方法详解',
    category: '技术',
    icon: 'fas fa-brain',
    likes: 89,
    likeTime: '1小时前'
  },
  {
    id: 2,
    title: '常见动物识别特征',
    description: '详细介绍各种常见动物的识别要点和特征分析',
    category: '动物',
    icon: 'fas fa-paw',
    likes: 67,
    likeTime: '4小时前'
  },
  {
    id: 3,
    title: '图像预处理技术',
    description: '提升识别准确率的图像预处理方法和技巧',
    category: '技术',
    icon: 'fas fa-image',
    likes: 123,
    likeTime: '1天前'
  }
])

// 成就徽章
const badges = ref([
  {
    id: 1,
    name: '新手上路',
    description: '完成首次图像识别',
    icon: 'fas fa-seedling',
    earned: true
  },
  {
    id: 2,
    name: '识别达人',
    description: '累计识别100次',
    icon: 'fas fa-eye',
    earned: true
  },
  {
    id: 3,
    name: '社区活跃',
    description: '发布10篇帖子',
    icon: 'fas fa-comment',
    earned: true
  },
  {
    id: 4,
    name: '人气之星',
    description: '获得100个点赞',
    icon: 'fas fa-star',
    earned: false
  }
])

// 最近访客
const recentVisitors = ref([
  {
    id: 1,
    name: '李四',
    avatar: '',
    visitTime: '1小时前'
  },
  {
    id: 2,
    name: '王五',
    avatar: '',
    visitTime: '3小时前'
  }
])

// 头像上传相关变量
const avatarModalVisible = ref(false)
const uploadLoading = ref(false)
const newAvatarUrl = ref('')
const avatarFileList = ref([])
const selectedPreset = ref(-1)

// 预设头像
const presetAvatars = ref([
  { name: '默认1', url: 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg' },
  { name: '默认2', url: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png' },
  { name: '默认3', url: 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png' },
  { name: '默认4', url: 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg' },
  { name: '默认5', url: 'https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png' },
  { name: '默认6', url: 'https://gw.alipayobjects.com/zos/rmsportal/OKJXDXrmkNshAMvwtvhu.png' }
])

// 方法
function showAvatarModal() {
  avatarModalVisible.value = true
  // 重置状态
  newAvatarUrl.value = ''
  avatarFileList.value = []
  selectedPreset.value = -1
}

function cancelAvatarUpload() {
  avatarModalVisible.value = false
  newAvatarUrl.value = ''
  avatarFileList.value = []
  selectedPreset.value = -1
}

function beforeAvatarUpload(file: any) {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return false // 阻止自动上传，我们手动处理
}

function handleAvatarChange(info: any) {
  if (info.file && info.file.originFileObj) {
    const reader = new FileReader()
    reader.addEventListener('load', () => {
      newAvatarUrl.value = reader.result as string
      selectedPreset.value = -1 // 清除预设选择
    })
    reader.readAsDataURL(info.file.originFileObj)
  }
}

function selectPresetAvatar(preset: any, index: number) {
  selectedPreset.value = index
  newAvatarUrl.value = preset.url
  avatarFileList.value = []
}

async function handleAvatarUpload() {
  if (!newAvatarUrl.value) {
    message.warning('请选择头像')
    return
  }

  uploadLoading.value = true
  
  try {
    // 模拟上传过程
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 更新用户头像
    userInfo.avatar = newAvatarUrl.value
    
    // 这里应该调用实际的API上传头像
    // const formData = new FormData()
    // if (avatarFileList.value.length > 0) {
    //   formData.append('avatar', avatarFileList.value[0].originFileObj)
    //   const response = await uploadAvatar(formData)
    //   userInfo.avatar = response.data.avatarUrl
    // } else {
    //   // 如果是预设头像，直接保存URL
    //   const response = await updateUserAvatar({ avatarUrl: newAvatarUrl.value })
    //   userInfo.avatar = response.data.avatarUrl
    // }
    
    message.success('头像更换成功!')
    avatarModalVisible.value = false
    
    // 重置状态
    newAvatarUrl.value = ''
    avatarFileList.value = []
    selectedPreset.value = -1
    
  } catch (error) {
    console.error('头像上传失败:', error)
    message.error('头像上传失败，请重试')
  } finally {
    uploadLoading.value = false
  }
}

function getActivityIcon(type: string) {
  const icons: Record<string, string> = {
    'recognition': 'fas fa-eye',
    'post': 'fas fa-edit',
    'like': 'fas fa-thumbs-up',
    'follow': 'fas fa-user-plus'
  }
  return icons[type] || 'fas fa-circle'
}

function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    'share': 'blue',
    'question': 'orange',
    'discussion': 'purple'
  }
  return colors[type] || 'default'
}

function getTypeName(type: string) {
  const names: Record<string, string> = {
    'share': '分享',
    'question': '问答',
    'discussion': '讨论'
  }
  return names[type] || '其他'
}

function getCategoryColor(category: string) {
  const colors: Record<string, string> = {
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange'
  }
  return colors[category] || 'default'
}

function viewPost(post: any) {
  router.push(`/user/community/post/${post.id}`)
}

function viewFavorite(item: any) {
  router.push(`/user/recognition/${item.id}`)
}

function viewKnowledge(item: any) {
  router.push(`/user/knowledge/${item.id}`)
}

function saveProfile() {
  Object.assign(userInfo, editInfo)
  message.success('个人资料保存成功')
}

function savePrivacy() {
  message.success('隐私设置保存成功')
}

</script>

