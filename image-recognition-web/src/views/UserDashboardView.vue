<template>
  <div :style="{ padding: '24px' }">
    <!-- 欢迎横幅 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px' }">
        <div :style="{ flex: 1 }">
          <div>
            <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">欢迎回来，{{ userInfo.nickname || userInfo.username || '用户' }}！</h1>
            <p :style="{ margin: 0, fontSize: '14px', opacity: 0.65 }">开始您的智能图像识别之旅</p>
          </div>
          <div :style="{ marginTop: '24px', display: 'flex', gap: '12px' }">
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
        <div :style="{ fontSize: '80px', opacity: 0.1, marginLeft: '32px' }">
          <i class="fas fa-brain"></i>
        </div>
      </div>
    </a-card>

    <!-- 统计卡片 -->
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '24px' }">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px', transition: 'all 0.3s ease' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '12px 8px' }">
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '24px' }">
              <i class="fas fa-eye"></i>
            </div>
            <div :style="{ flex: 1 }">
              <div :style="{ fontSize: '28px', fontWeight: '600', marginBottom: '4px' }">{{ stats.recognitionCount }}</div>
              <div :style="{ fontSize: '14px', opacity: '0.65' }">总识别次数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', transition: 'all 0.3s ease' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '12px 8px' }">
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '24px' }">
              <i class="fas fa-heart"></i>
            </div>
            <div :style="{ flex: 1 }">
              <div :style="{ fontSize: '28px', fontWeight: '600', marginBottom: '4px' }">{{ stats.collectCount }}</div>
              <div :style="{ fontSize: '14px', opacity: '0.65' }">我的收藏</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px', transition: 'all 0.3s ease' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '12px 8px' }">
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '24px' }">
              <i class="fas fa-comments"></i>
            </div>
            <div :style="{ flex: 1 }">
              <div :style="{ fontSize: '28px', fontWeight: '600', marginBottom: '4px' }">{{ stats.commentCount }}</div>
              <div :style="{ fontSize: '14px', opacity: '0.65' }">评论数量</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px', transition: 'all 0.3s ease' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '12px 8px' }">
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '24px' }">
              <i class="fas fa-thumbs-up"></i>
            </div>
            <div :style="{ flex: 1 }">
              <div :style="{ fontSize: '28px', fontWeight: '600', marginBottom: '4px' }">{{ stats.likeCount }}</div>
              <div :style="{ fontSize: '14px', opacity: '0.65' }">获得点赞</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 主要内容区域 -->
    <a-row :gutter="[16, 16]">
      <!-- 最近识别 -->
      <a-col :xs="24" :lg="12">
        <a-card title="最近识别" :style="{ borderRadius: '8px', height: '100%' }">
          <template #extra>
            <a-button type="link" @click="viewAllHistory">查看全部</a-button>
          </template>
          <div v-if="recentRecognitions.length === 0" :style="{ textAlign: 'center', padding: '32px 0', opacity: 0.65 }">
            <i class="fas fa-inbox" :style="{ fontSize: '48px', marginBottom: '16px', display: 'block' }"></i>
            <div>暂无识别记录</div>
          </div>
          <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
            <div 
              v-for="item in recentRecognitions" 
              :key="item.id"
              :style="{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease' }"
              @click="viewRecognitionDetail(item)"
            >
              <div :style="{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', flexShrink: 0, background: '#f5f5f5' }">
                <img :src="item.thumbnail" :alt="item.result" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
              </div>
              <div :style="{ flex: 1, minWidth: 0 }">
                <div :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '6px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }">{{ item.result }}</div>
                <div :style="{ fontSize: '13px', opacity: '0.65' }">{{ item.time }}</div>
              </div>
              <div :style="{ width: '120px', display: 'flex', flexDirection: 'column', gap: '6px', alignItems: 'flex-end' }">
                <a-progress 
                  :percent="item.confidence" 
                  size="small" 
                  :show-info="false"
                />
                <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ item.confidence }}%</span>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 推荐内容 -->
      <a-col :xs="24" :lg="12">
        <a-card :style="{ borderRadius: '8px', height: '100%' }">
          <template #title>
            <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
              <span>推荐内容</span>
              <a-radio-group v-model:value="recommendTab" button-style="solid" size="small">
                <a-radio-button value="knowledge">知识推荐</a-radio-button>
                <a-radio-button value="community">社区推荐</a-radio-button>
              </a-radio-group>
            </div>
          </template>
          
          <!-- 知识推荐 -->
          <div v-if="recommendTab === 'knowledge'" :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
            <div 
              v-for="item in recommendedKnowledge" 
              :key="item.id"
              :style="{ display: 'flex', alignItems: 'flex-start', gap: '16px', padding: '16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease' }"
              @click="viewKnowledge(item)"
            >
              <div :style="{ width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '18px', flexShrink: 0 }">
                <i :class="item.icon"></i>
              </div>
              <div :style="{ flex: 1, minWidth: 0 }">
                <div :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '6px' }">{{ item.title }}</div>
                <div :style="{ fontSize: '13px', opacity: '0.65', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }">{{ item.description }}</div>
              </div>
            </div>
          </div>
          
          <!-- 社区推荐 -->
          <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
            <div 
              v-for="item in hotCommunityPosts" 
              :key="item.id"
              :style="{ display: 'flex', alignItems: 'flex-start', gap: '16px', padding: '16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease' }"
              @click="viewPost(item)"
            >
              <div :style="{ flex: 1 }">
                <div :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '8px' }">{{ item.title }}</div>
                <div :style="{ display: 'flex', gap: '16px', fontSize: '13px', opacity: '0.65' }">
                  <span>{{ item.author }}</span>
                  <span>{{ item.likes }} 点赞</span>
                  <span>{{ item.replies }} 回复</span>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 功能入口 -->
    <a-card title="功能导航" :style="{ marginTop: '24px', borderRadius: '8px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="12" :sm="8" :lg="4" v-for="feature in features" :key="feature.key">
          <div 
            :style="{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', padding: '32px 16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease', height: '100%' }" 
            @click="navigateToFeature(feature.path)"
          >
            <div :style="{ width: '64px', height: '64px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px', fontSize: '28px', marginBottom: '16px' }">
              <i :class="feature.icon"></i>
            </div>
            <div :style="{ fontSize: '16px', fontWeight: '500', marginBottom: '8px' }">{{ feature.title }}</div>
            <div :style="{ fontSize: '13px', opacity: '0.65' }">{{ feature.description }}</div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserAPI, type UserStats } from '@/api/user'
import { RecognitionAPI, type RecognitionInfo } from '@/api/recognition'
import { KnowledgeAPI, type KnowledgeInfo } from '@/api/knowledge'
import { CommunityAPI, type PostInfo } from '@/api/community'
import { ImageUtils } from '@/utils/image'

const router = useRouter()

// 推荐内容标签页
const recommendTab = ref<'knowledge' | 'community'>('knowledge')

// 用户信息
const userInfo = reactive({
  id: 0,
  username: '',
  nickname: '',
  avatar: '',
  email: '',
  role: 0
})

// 统计数据
const stats = reactive<UserStats>({
  userId: 0,
  recognitionCount: 0,
  postCount: 0,
  commentCount: 0,
  collectCount: 0,
  likeCount: 0
})

// 最近识别记录
interface RecentRecognition {
  id: number
  result: string
  confidence: number
  time: string
  thumbnail: string
}
const recentRecognitions = ref<RecentRecognition[]>([])

// 推荐知识
interface RecommendedKnowledge {
  id: number
  title: string
  description: string
  icon: string
}
const recommendedKnowledge = ref<RecommendedKnowledge[]>([])

// 社区热门
interface HotPost {
  id: number
  title: string
  author: string
  likes: number
  replies: number
}
const hotCommunityPosts = ref<HotPost[]>([])

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

// 加载用户信息
async function loadUserInfo() {
  try {
    const profile = await UserAPI.getProfile()
    userInfo.id = profile.id
    userInfo.username = profile.username
    userInfo.nickname = profile.nickname || ''
    userInfo.avatar = profile.avatar || ''
    userInfo.email = profile.email || ''
    userInfo.role = profile.role
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 不显示错误提示，使用默认值
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const statsData = await UserAPI.getStats()
    Object.assign(stats, statsData)
    console.log('用户统计数据:', stats)
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 静默失败，使用默认值
  }
}

// 加载最近识别记录
async function loadRecentRecognitions() {
  try {
    const response = await RecognitionAPI.getHistory({
      page: 1,
      size: 3
    })
    
    console.log('最近识别记录:', response)
    
    // 转换数据格式
    recentRecognitions.value = response.data.map((item: RecognitionInfo) => ({
      id: item.id,
      result: item.objectName || item.category || '未知',
      confidence: Math.round(item.confidence * 100),
      time: formatTime(item.createdAt),
      thumbnail: ImageUtils.getImageUrl(item.imageUrl)
    }))
  } catch (error) {
    console.error('加载最近识别记录失败:', error)
    // 静默失败，使用空数组
  }
}

// 格式化时间
function formatTime(timeStr: string): string {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
  }
}

// 加载推荐知识
async function loadRecommendedKnowledge() {
  try {
    const response = await KnowledgeAPI.getKnowledgeList({
      page: 1,
      size: 3
    })
    
    console.log('推荐知识:', response)
    
    // 定义分类图标映射
    const categoryIcons: Record<string, string> = {
      '动物': 'fas fa-paw',
      '植物': 'fas fa-leaf',
      '食物': 'fas fa-apple-alt',
      '物品': 'fas fa-box',
      '场景': 'fas fa-image',
      '其他': 'fas fa-book'
    }
    
    // 转换数据格式
    recommendedKnowledge.value = response.data.map((item: KnowledgeInfo) => ({
      id: item.id,
      title: item.title,
      description: item.description || item.content.substring(0, 30) + '...',
      icon: categoryIcons[item.category] || 'fas fa-book'
    }))
  } catch (error) {
    console.error('加载推荐知识失败:', error)
    // 静默失败，使用空数组
  }
}

// 加载社区热门帖子
async function loadHotCommunityPosts() {
  try {
    const response = await CommunityAPI.getPosts({
      page: 1,
      size: 3,
      sort: 'hot'
    })
    
    console.log('社区热门帖子:', response)
    
    // 转换数据格式
    hotCommunityPosts.value = response.data.map((item: PostInfo) => ({
      id: item.id,
      title: item.title,
      author: item.authorName || '未知用户',
      likes: item.likeCount,
      replies: item.commentCount
    }))
  } catch (error) {
    console.error('加载社区热门帖子失败:', error)
    // 静默失败，使用空数组
  }
}

// 组件挂载时加载用户信息和统计数据
onMounted(() => {
  loadUserInfo()
  loadStats()
  loadRecentRecognitions()
  loadRecommendedKnowledge()
  loadHotCommunityPosts()
})
</script>

