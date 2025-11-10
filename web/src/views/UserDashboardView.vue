<template>
  <div :style="{ padding: '24px' }">
    <!-- 欢迎横幅 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }" :bodyStyle="{ padding: '20px' }">
      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
        <div :style="{ flex: 1 }">
          <div>
            <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">
              欢迎回来，{{ userInfo.nickname || userInfo.username || '用户' }}
              <span v-if="userInfo.role === 1" :style="{ color: '#faad14', fontSize: '20px', marginLeft: '8px' }">VIP</span>！
            </h1>
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
                    <!-- 识别统计 -->
                    <div :style="{ marginTop: '20px' }">
            <div :style="{ fontSize: '14px', fontWeight: '500', marginBottom: '12px', opacity: 0.8 }">识别统计</div>
            <a-row :gutter="[6, 6]">
              <a-col :span="6" v-for="stat in statsItems" :key="stat.key">
                <div 
                  :style="{ 
                    display: 'flex', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                    textAlign: 'center', 
                    padding: '10px 6px', 
                    borderRadius: '6px', 
                    border: '1px solid #f0f0f0',
                    background: '#fafafa'
                  }" 
                >
                  <div :style="{ fontSize: '14px', marginBottom: '4px', color: stat.color }">
                    <i :class="stat.icon"></i>
                  </div>
                  <div :style="{ fontSize: '16px', fontWeight: '600', marginBottom: '2px' }">{{ stat.value }}</div>
                  <div :style="{ fontSize: '11px', fontWeight: '500', lineHeight: '1.2', opacity: 0.8 }">{{ stat.label }}</div>
                </div>
              </a-col>
            </a-row>
          </div>
          <!-- 功能导航 -->
          <div :style="{ marginTop: '20px' }">
            <div :style="{ fontSize: '14px', fontWeight: '500', marginBottom: '12px', opacity: 0.8 }">快捷功能</div>
            <a-row :gutter="[6, 6]">
              <a-col :span="3" v-for="feature in quickFeatures" :key="feature.key">
                <div 
                  :style="{ 
                    display: 'flex', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                    textAlign: 'center', 
                    padding: '8px 4px', 
                    borderRadius: '6px', 
                    cursor: 'pointer', 
                    transition: 'all 0.3s ease',
                    border: '1px solid #f0f0f0',
                    background: '#fafafa'
                  }" 
                  @click="navigateToFeature(feature.path)"
                  @mouseenter="(e) => handleMouseEnter(e)"
                  @mouseleave="(e) => handleMouseLeave(e)"
                >
                  <div :style="{ fontSize: '14px', marginBottom: '3px', color: feature.color }">
                    <i :class="feature.icon"></i>
                  </div>
                  <div :style="{ fontSize: '11px', fontWeight: '500', lineHeight: '1.2' }">{{ feature.title }}</div>
                </div>
              </a-col>
            </a-row>
          </div>
          

        </div>
        <div :style="{ fontSize: '60px', opacity: 0.1, marginLeft: '24px' }">
          <i class="fas fa-brain"></i>
        </div>
      </div>
    </a-card>

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
              <div :style="{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', flexShrink: 0, background: '#f5f5f5' }">
                <img 
                  v-if="item.coverImage" 
                  :src="item.coverImage" 
                  :alt="item.title" 
                  :style="{ width: '100%', height: '100%', objectFit: 'cover' }" 
                />
                <div 
                  v-else 
                  :style="{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '18px', color: item.iconColor || '#52c41a' }"
                >
                  <i :class="item.icon"></i>
                </div>
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
              <div :style="{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', flexShrink: 0, background: '#f5f5f5' }">
                <img 
                  v-if="item.images && item.images.length > 0" 
                  :src="item.images[0]" 
                  :alt="item.title" 
                  :style="{ width: '100%', height: '100%', objectFit: 'cover' }" 
                />
                <div 
                  v-else 
                  :style="{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '18px', color: '#722ed1' }"
                >
                  <i class="fas fa-comments"></i>
                </div>
              </div>
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

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
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
  iconColor?: string
  coverImage?: string
}
const recommendedKnowledge = ref<RecommendedKnowledge[]>([])

// 社区热门
interface HotPost {
  id: number
  title: string
  author: string
  likes: number
  replies: number
  images?: string[]
}
const hotCommunityPosts = ref<HotPost[]>([])

// 快捷功能导航 - 根据用户角色动态显示
const quickFeatures = computed(() => {
  const baseFeatures = [
    {
      key: 'knowledge',
      title: '知识库',
      icon: 'fas fa-book',
      color: '#52c41a',
      path: '/user/knowledge'
    },
    {
      key: 'community',
      title: '社区',
      icon: 'fas fa-users',
      color: '#722ed1',
      path: '/user/community'
    },
    {
      key: 'my-posts',
      title: '我的贴子',
      icon: 'fas fa-edit',
      color: '#13c2c2',
      path: '/user/profile?tab=posts'
    },
    {
      key: 'my-likes',
      title: '我的点赞',
      icon: 'fas fa-thumbs-up',
      color: '#fa8c16',
      path: '/user/profile?tab=likes'
    },
    {
      key: 'favorites',
      title: '我的收藏',
      icon: 'fas fa-heart',
      color: '#eb2f96',
      path: '/user/profile?tab=favorites'
    },
    {
      key: 'history',
      title: '历史记录',
      icon: 'fas fa-history',
      color: '#8c8c8c',
      path: '/user/history'
    }
  ]

  // 根据用户角色添加不同的识别功能
  if (userInfo.role === 1) {
    // VIP用户 - 显示批量识别和高级功能
    return [
      {
        key: 'batch',
        title: '批量识别',
        icon: 'fas fa-images',
        color: '#1890ff',
        path: '/user/recognition/batch'
      },
      {
        key: 'advanced',
        title: '高级识别',
        icon: 'fas fa-magic',
        color: '#faad14',
        path: '/user/advanced-recognition'
      },
      ...baseFeatures
    ]
  } else {
    // 普通用户 - 显示单张识别
    return [
      {
        key: 'single',
        title: '单张识别',
        icon: 'fas fa-camera',
        color: '#1890ff',
        path: '/user/recognition'
      },
      {
        key: 'upgrade',
        title: '升级VIP',
        icon: 'fas fa-crown',
        color: '#faad14',
        path: '/user/become-vip'
      },
      ...baseFeatures
    ]
  }
})

// 统计数据项
const statsItems = ref([
  {
    key: 'recognition',
    label: '总识别次数',
    icon: 'fas fa-eye',
    color: '#1890ff',
    value: computed(() => stats.recognitionCount)
  },
  {
    key: 'collect',
    label: '我的收藏',
    icon: 'fas fa-heart',
    color: '#fa8c16',
    value: computed(() => stats.collectCount)
  },
  {
    key: 'comment',
    label: '评论数量',
    icon: 'fas fa-comments',
    color: '#52c41a',
    value: computed(() => stats.commentCount)
  },
  {
    key: 'like',
    label: '获得点赞',
    icon: 'fas fa-thumbs-up',
    color: '#eb2f96',
    value: computed(() => stats.likeCount)
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
  // 处理带查询参数的路径
  if (path.includes('?')) {
    const [routePath, queryString] = path.split('?')
    const queryParams = new URLSearchParams(queryString)
    const query: Record<string, string> = {}
    
    for (const [key, value] of queryParams) {
      query[key] = value
    }
    
    router.push({ path: routePath, query })
  } else {
    router.push(path)
  }
}

function handleMouseEnter(e: Event) {
  const target = e.target as HTMLElement
  if (target) {
    target.style.background = '#f0f0f0'
  }
}

function handleMouseLeave(e: Event) {
  const target = e.target as HTMLElement
  if (target) {
    target.style.background = '#fafafa'
  }
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
      icon: categoryIcons[item.category] || 'fas fa-book',
      iconColor: '#52c41a',
      coverImage: item.coverImage ? ImageUtils.getImageUrl(item.coverImage) : undefined
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
      replies: item.commentCount,
      images: item.images ? item.images.map((img: string) => ImageUtils.getImageUrl(img)).filter((img: string) => img) : []
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

