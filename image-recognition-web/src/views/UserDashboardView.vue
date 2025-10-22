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
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px', fontSize: '24px' }">
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
        <a-card :style="{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', transition: 'all 0.3s ease' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '20px', padding: '12px 8px' }">
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px', fontSize: '24px' }">
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
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px', fontSize: '24px' }">
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
            <div :style="{ width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px', fontSize: '24px' }">
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
          <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
            <div 
              v-for="item in recentRecognitions" 
              :key="item.id"
              :style="{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.3s ease' }"
              @click="viewRecognitionDetail(item)"
            >
              <div :style="{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', flexShrink: 0 }">
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
        <a-card title="推荐内容" :style="{ borderRadius: '8px', height: '100%' }">
          <a-tabs>
            <a-tab-pane key="knowledge" tab="知识推荐">
              <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
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
            </a-tab-pane>
            <a-tab-pane key="community" tab="社区热门">
              <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
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
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <!-- 功能入口 -->
    <a-card title="功能导航" :style="{ marginTop: '24px', borderRadius: '8px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="12" :sm="8" :lg="4" v-for="feature in features" :key="feature.key">
          <div 
            :style="{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', padding: '32px 16px', borderRadius: '12px', cursor: 'pointer', transition: 'all 0.3s ease', height: '100%' }" 
            @click="navigateToFeature(feature.path)"
          >
            <div :style="{ width: '64px', height: '64px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '16px', fontSize: '28px', marginBottom: '16px' }">
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

const router = useRouter()

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

// 组件挂载时加载用户信息和统计数据
onMounted(() => {
  loadUserInfo()
  loadStats()
})
</script>

