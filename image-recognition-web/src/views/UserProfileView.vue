<template>
  <div :style="{ minHeight: '100vh', background: '#f0f2f5' }">
    <!-- 个人信息头部 -->
    <UserProfileHeader 
      :userInfo="displayUserInfo" 
      :userStats="userStats"
      @update-avatar="showAvatarModal"
    />

    <!-- 主要内容区域 -->
    <div>
      <a-row :gutter="24">
        <!-- 左侧内容 -->
        <a-col :xs="24" :lg="16">
          <a-tabs v-model:activeKey="activeTab" :style="{ background: 'white', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', padding: '16px' }">
            <!-- 我的动态 -->
            <a-tab-pane key="activities" tab="我的动态">
              <UserActivityList :activities="activities" />
            </a-tab-pane>

            <!-- 我的帖子 -->
            <a-tab-pane key="posts" tab="我的帖子">
              <UserPostList :posts="userPosts" @view-post="viewPost" />
            </a-tab-pane>

            <!-- 我的收藏 -->
            <a-tab-pane key="favorites" tab="我的收藏">
              <a-tabs type="card">
                <a-tab-pane key="recognition" tab="识别结果">
                  <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '16px' }">
                    <div 
                      v-for="item in favoriteRecognitions" 
                      :key="item.id"
                      :style="{ 
                        background: '#fafafa', 
                        borderRadius: '8px', 
                        overflow: 'hidden',
                        cursor: 'pointer',
                        transition: 'all 0.3s',
                        border: '1px solid #f0f0f0'
                      }"
                      @click="viewFavorite(item)"
                    >
                      <div :style="{ width: '100%', height: '150px', background: '#f5f5f5' }">
                        <img :src="item.thumbnail" :alt="item.result" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
                      </div>
                      <div :style="{ padding: '12px' }">
                        <div :style="{ fontSize: '14px', fontWeight: 'bold', color: '#262626', marginBottom: '4px' }">{{ item.result }}</div>
                        <div :style="{ fontSize: '12px', color: '#1890ff' }">置信度: {{ item.confidence }}%</div>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="knowledge" tab="知识内容">
                  <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                    <div 
                      v-for="item in favoriteKnowledge" 
                      :key="item.id"
                      :style="{ 
                        display: 'flex', 
                        gap: '16px', 
                        padding: '16px', 
                        background: '#fafafa', 
                        borderRadius: '8px',
                        border: '1px solid #f0f0f0',
                        cursor: 'pointer',
                        transition: 'all 0.3s'
                      }"
                      @click="viewKnowledge(item)"
                    >
                      <div :style="{ 
                        width: '48px', 
                        height: '48px', 
                        borderRadius: '8px', 
                        background: '#e6f7ff', 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'center',
                        flexShrink: 0
                      }">
                        <i :class="item.icon" :style="{ color: '#1890ff', fontSize: '24px' }"></i>
                      </div>
                      <div :style="{ flex: 1 }">
                        <h4 :style="{ fontSize: '15px', fontWeight: 'bold', color: '#262626', margin: '0 0 6px 0' }">{{ item.title }}</h4>
                        <p :style="{ fontSize: '13px', color: '#8c8c8c', margin: '0 0 8px 0' }">{{ item.description }}</p>
                        <a-tag :color="getCategoryColor(item.category)" size="small">
                          {{ item.category }}
                        </a-tag>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
              </a-tabs>
            </a-tab-pane>

            <!-- 设置 -->
            <a-tab-pane key="settings" tab="设置">
              <UserSettings 
                :userInfo="userInfo"
                @save-profile="saveProfile"
              />
            </a-tab-pane>
          </a-tabs>
        </a-col>

        <!-- 右侧侧边栏 -->
        <a-col :xs="24" :lg="8">
          <UserSidebar :badges="badges" :recentVisitors="recentVisitors" />
        </a-col>
      </a-row>
    </div>

    <!-- 头像上传模态框 -->
    <AvatarUploadModal
      :visible="avatarModalVisible"
      :currentAvatar="displayUserInfo.avatar"
      :userName="userInfo.name"
      :uploadLoading="uploadLoading"
      @upload="handleAvatarUpload"
      @cancel="cancelAvatarUpload"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import UserProfileHeader from '@/components/UserProfileHeader.vue'
import UserActivityList from '@/components/UserActivityList.vue'
import UserPostList from '@/components/UserPostList.vue'
import UserSettings from '@/components/UserSettings.vue'
import UserSidebar from '@/components/UserSidebar.vue'
import AvatarUploadModal from '@/components/AvatarUploadModal.vue'
import { FileAPI } from '@/api/file'
import { UserAPI } from '@/api/user'

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

// 组件挂载时设置标签并加载用户信息
onMounted(() => {
  setActiveTabFromRoute()
  loadUserProfile()
})

// 用户信息
const userInfo = reactive({
  id: 0,
  username: '',
  nickname: '',
  name: '',
  bio: '',
  avatar: '',
  joinDate: '',
  location: '',
  email: '',
  phone: '',
  role: 0,
  createdAt: ''
})

// 用户统计
const userStats = reactive({
  recognitions: 0,
  posts: 0,
  likes: 0,
  followers: 0
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
    image: undefined
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

// 计算属性：处理头像URL显示
const displayUserInfo = computed(() => ({
  ...userInfo,
  // 优先显示昵称，如果没有昵称则显示用户名
  name: userInfo.nickname || userInfo.username || '未设置',
  avatar: FileAPI.getImageUrl(userInfo.avatar),
  phone: userInfo.phone || '',
  bio: userInfo.bio || ''
}))

// 加载用户信息
async function loadUserProfile() {
  try {
    const profile = await UserAPI.getProfile()
    
    // 更新用户信息
    Object.assign(userInfo, {
      id: profile.id,
      username: profile.username,
      nickname: profile.nickname,
      name: profile.nickname || profile.username,
      bio: profile.bio || '这个人很懒，还没有填写个人简介',
      avatar: profile.avatar || '',
      email: profile.email || '',
      phone: profile.phone || '',
      role: profile.role,
      createdAt: profile.createdAt,
      // 格式化加入时间
      joinDate: formatJoinDate(profile.createdAt),
      location: '未设置' // 后端暂时没有这个字段
    })
    
    // 加载用户统计
    loadUserStats()
  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    message.error('加载用户信息失败')
  }
}

// 加载用户统计信息
async function loadUserStats() {
  try {
    const stats = await UserAPI.getStats()
    
    // 更新统计数据
    Object.assign(userStats, {
      recognitions: stats.totalRecognitions || 0,
      posts: 0, // 后端暂时没有这个统计
      likes: 0, // 后端暂时没有这个统计
      followers: 0 // 后端暂时没有这个统计
    })
  } catch (error: any) {
    console.error('加载用户统计失败:', error)
    // 统计数据加载失败不影响页面显示
  }
}

// 格式化加入时间
function formatJoinDate(dateString?: string) {
  if (!dateString) return '未知'
  
  try {
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    return `${year}年${month}月`
  } catch (error) {
    return '未知'
  }
}

// 方法
function showAvatarModal() {
  console.log('打开头像上传对话框')
  avatarModalVisible.value = true
}

function cancelAvatarUpload() {
  console.log('取消头像上传')
  avatarModalVisible.value = false
}

async function handleAvatarUpload(file: File | null, presetUrl: string | null) {
  console.log('handleAvatarUpload 被调用:', { file, presetUrl })
  uploadLoading.value = true
  
  try {
    let avatarUrl = ''
    
    // 如果是上传的文件，先上传到服务器
    if (file) {
      console.log('开始上传文件:', file.name, file.size)
      avatarUrl = await FileAPI.uploadImage(file)
      console.log('图片上传成功，URL:', avatarUrl)
    } 
    // 如果是预设头像，直接使用URL
    else if (presetUrl) {
      console.log('使用预设头像:', presetUrl)
      avatarUrl = presetUrl
    } else {
      console.log('没有文件也没有预设URL')
      message.warning('请选择头像')
      return
    }
    
    console.log('准备更新用户资料，头像URL:', avatarUrl)
    // 调用更新用户信息接口，更新头像
    await UserAPI.updateProfile({ avatar: avatarUrl })
    
    // 更新本地用户头像
    userInfo.avatar = avatarUrl
    
    message.success('头像更换成功!')
    avatarModalVisible.value = false
    
    // 重新加载用户信息以确保数据同步
    await loadUserProfile()
    
  } catch (error: any) {
    console.error('头像上传失败:', error)
    message.error(error.message || '头像上传失败，请重试')
  } finally {
    uploadLoading.value = false
  }
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

async function saveProfile(info: any) {
  try {
    console.log('保存个人资料:', info)
    
    // 验证必填字段
    if (!info.name || info.name.trim() === '') {
      message.warning('昵称不能为空')
      return
    }
    
    // 验证手机号格式（如果填写了）
    if (info.phone && info.phone.trim() !== '') {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(info.phone.trim())) {
        message.warning('请输入有效的手机号')
        return
      }
    }
    
    // 调用更新用户信息接口
    await UserAPI.updateProfile({
      nickname: info.name.trim(),
      bio: info.bio?.trim() || '',
      phone: info.phone?.trim() || ''
    })
    
    message.success('个人资料保存成功')
    
    // 重新加载用户信息以确保数据同步
    await loadUserProfile()
    
  } catch (error: any) {
    console.error('保存用户信息失败:', error)
    message.error(error.message || '保存失败，请重试')
  }
}

</script>
