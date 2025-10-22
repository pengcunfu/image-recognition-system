<template>
  <div :style="{ padding: '24px' }">
    <div :style="{ maxWidth: '1400px', margin: '0 auto' }">
      <!-- 用户信息卡片 -->
      <a-card :style="{ marginBottom: '16px', borderRadius: '8px' }">
        <div :style="{ display: 'flex', alignItems: 'center', gap: '16px' }">
          <!-- 头像 -->
          <div :style="{ position: 'relative' }">
            <a-avatar :size="80" :src="displayUserInfo.avatar">
              {{ displayUserInfo.name?.charAt(0) }}
            </a-avatar>
            <a-button 
              type="primary" 
              shape="circle" 
              size="small"
              :style="{ position: 'absolute', bottom: '0', right: '0' }"
              @click="showAvatarModal"
            >
              <i class="fas fa-camera"></i>
            </a-button>
          </div>
          
          <!-- 用户信息 -->
          <div :style="{ flex: 1 }">
            <h2 :style="{ margin: '0 0 6px 0', fontSize: '20px', fontWeight: '600' }">
              {{ displayUserInfo.name }}
            </h2>
            <p :style="{ margin: '0 0 8px 0', fontSize: '13px', opacity: 0.65 }">{{ displayUserInfo.bio }}</p>
            <div :style="{ display: 'flex', gap: '16px', fontSize: '13px', opacity: 0.65 }">
              <span><i class="fas fa-calendar" :style="{ marginRight: '4px' }"></i>加入时间: {{ userInfo.joinDate }}</span>
              <span v-if="displayUserInfo.email"><i class="fas fa-envelope" :style="{ marginRight: '4px' }"></i>{{ displayUserInfo.email }}</span>
            </div>
          </div>
        </div>
      </a-card>

      <!-- 主要内容区域 -->
      <a-row :gutter="16">
        <!-- 左侧菜单 -->
        <a-col :xs="24" :sm="6">
          <a-card :style="{ borderRadius: '8px' }">
            <a-menu 
              v-model:selectedKeys="selectedMenu" 
              mode="inline"
              :style="{ border: 'none' }"
              @click="handleMenuClick"
            >
              <a-menu-item key="posts">
                <i class="fas fa-book-open" :style="{ marginRight: '6px' }"></i>
                我的帖子
              </a-menu-item>
              <a-menu-item key="favorites">
                <i class="fas fa-star" :style="{ marginRight: '6px' }"></i>
                我的收藏
              </a-menu-item>
              <a-menu-item key="likes">
                <i class="fas fa-heart" :style="{ marginRight: '6px' }"></i>
                我的点赞
              </a-menu-item>
              <a-menu-item key="settings">
                <i class="fas fa-cog" :style="{ marginRight: '6px' }"></i>
                账号设置
              </a-menu-item>
            </a-menu>
          </a-card>
        </a-col>

        <!-- 右侧内容 -->
        <a-col :xs="24" :sm="18">
          <a-card :style="{ borderRadius: '8px', minHeight: '400px' }">
            <!-- 我的帖子 -->
            <div v-show="currentView === 'posts'">
              <h3 :style="{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: '600' }">我的帖子</h3>
              <UserPostList 
                :posts="userPosts" 
                @view-post="viewPost"
                @edit-post="editPost"
                @delete-post="deletePost"
                @hide-post="hidePost"
                @show-post="showPost"
              />
            </div>

            <!-- 我的收藏 -->
            <div v-show="currentView === 'favorites'">
              <h3 :style="{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: '600' }">我的收藏</h3>
              <a-tabs type="card">
                <a-tab-pane key="recognition" tab="识别结果">
                  <a-empty v-if="collections.recognitions.length === 0" description="暂无收藏的识别结果" />
                  <div v-else :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '16px' }">
                    <div 
                      v-for="item in collections.recognitions" 
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
                        <img :src="getImageUrl(item.imageUrl)" :alt="item.title" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
                      </div>
                      <div :style="{ padding: '12px' }">
                        <div :style="{ fontSize: '14px', fontWeight: 'bold', color: '#262626', marginBottom: '4px' }">{{ item.title }}</div>
                        <div :style="{ fontSize: '12px', color: '#1890ff' }">置信度: {{ item.confidence }}%</div>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="knowledge" tab="知识内容">
                  <a-empty v-if="collections.knowledge.length === 0" description="暂无收藏的知识内容" />
                  <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                    <div 
                      v-for="item in collections.knowledge" 
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
                        <i class="fas fa-book" :style="{ color: '#1890ff', fontSize: '24px' }"></i>
                      </div>
                      <div :style="{ flex: 1 }">
                        <h4 :style="{ fontSize: '15px', fontWeight: 'bold', color: '#262626', margin: '0 0 6px 0' }">{{ item.title }}</h4>
                        <p :style="{ fontSize: '13px', color: '#8c8c8c', margin: '0 0 8px 0' }">{{ item.description }}</p>
                        <div :style="{ display: 'flex', gap: '16px', fontSize: '12px', color: '#8c8c8c' }">
                          <span><i class="fas fa-heart"></i> {{ item.likeCount }}</span>
                          <span><i class="fas fa-eye"></i> {{ item.viewCount }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
              </a-tabs>
            </div>

            <!-- 我的点赞 -->
            <div v-show="currentView === 'likes'">
              <h3 :style="{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: 'bold' }">我的点赞</h3>
              <a-tabs type="card">
                <a-tab-pane key="posts" tab="帖子">
                  <a-empty v-if="likes.posts.length === 0" description="暂无点赞的帖子" />
                  <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                    <div 
                      v-for="item in likes.posts" 
                      :key="item.id"
                      :style="{ 
                        padding: '16px', 
                        background: '#fafafa', 
                        borderRadius: '8px',
                        border: '1px solid #f0f0f0',
                        cursor: 'pointer',
                        transition: 'all 0.3s'
                      }"
                      @click="viewPost({ id: item.id })"
                    >
                      <div :style="{ marginBottom: '8px' }">
                        <h4 :style="{ fontSize: '15px', fontWeight: 'bold', color: '#262626', margin: '0 0 4px 0' }">{{ item.title }}</h4>
                        <p :style="{ fontSize: '13px', color: '#8c8c8c', margin: '0', overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }">{{ item.content }}</p>
                      </div>
                      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '12px', color: '#8c8c8c' }">
                        <span>作者: {{ item.author }}</span>
                        <span><i class="fas fa-heart"></i> {{ item.likeCount }}</span>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="knowledge" tab="知识">
                  <a-empty v-if="likes.knowledge.length === 0" description="暂无点赞的知识内容" />
                  <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                    <div 
                      v-for="item in likes.knowledge" 
                      :key="item.id"
                      :style="{ 
                        padding: '16px', 
                        background: '#fafafa', 
                        borderRadius: '8px',
                        border: '1px solid #f0f0f0',
                        cursor: 'pointer',
                        transition: 'all 0.3s'
                      }"
                      @click="viewKnowledge({ id: item.id })"
                    >
                      <div :style="{ marginBottom: '8px' }">
                        <h4 :style="{ fontSize: '15px', fontWeight: 'bold', color: '#262626', margin: '0 0 4px 0' }">{{ item.title }}</h4>
                        <p :style="{ fontSize: '13px', color: '#8c8c8c', margin: '0' }">{{ item.content }}</p>
                      </div>
                      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '12px', color: '#8c8c8c' }">
                        <span>作者: {{ item.author }}</span>
                        <span><i class="fas fa-heart"></i> {{ item.likeCount }}</span>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
                <a-tab-pane key="comments" tab="评论">
                  <a-empty v-if="likes.comments.length === 0" description="暂无点赞的评论" />
                  <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                    <div 
                      v-for="item in likes.comments" 
                      :key="item.id"
                      :style="{ 
                        padding: '16px', 
                        background: '#fafafa', 
                        borderRadius: '8px',
                        border: '1px solid #f0f0f0'
                      }"
                    >
                      <p :style="{ fontSize: '13px', color: '#262626', margin: '0 0 8px 0' }">{{ item.content }}</p>
                      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '12px', color: '#8c8c8c' }">
                        <span>作者: {{ item.author }}</span>
                        <span><i class="fas fa-heart"></i> {{ item.likeCount }}</span>
                      </div>
                    </div>
                  </div>
                </a-tab-pane>
              </a-tabs>
            </div>

            <!-- 设置 -->
            <div v-show="currentView === 'settings'">
              <h3 :style="{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: 'bold' }">账号设置</h3>
              <UserSettings 
                :userInfo="userInfo"
                @save-profile="saveProfile"
              />
            </div>
          </a-card>
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

    <!-- 帖子编辑模态框 -->
    <PostEditModal
      v-model:visible="editModalVisible"
      :post="currentEditPost"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import UserPostList from './user-profile/UserPostList.vue'
import UserSettings from './user-profile/UserSettings.vue'
import AvatarUploadModal from './user-profile/AvatarUploadModal.vue'
import PostEditModal from './user-profile/PostEditModal.vue'
import { FileAPI } from '@/api/file'
import { UserAPI } from '@/api/user'
import { CommunityAPI } from '@/api/community'

const router = useRouter()
const route = useRoute()

// 当前视图
const currentView = ref('posts')
const selectedMenu = ref(['posts'])

// 菜单点击事件
function handleMenuClick({ key }: { key: string }) {
  currentView.value = key
  selectedMenu.value = [key]
}

// 监听路由查询参数变化
watch(() => route.query.tab, (tab) => {
  if (tab && typeof tab === 'string') {
    currentView.value = tab
    selectedMenu.value = [tab]
  }
}, { immediate: true })

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserProfile()
  
  // 如果URL中有tab参数,切换到对应视图
  const tab = route.query.tab as string
  if (tab) {
    currentView.value = tab
    selectedMenu.value = [tab]
  }
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
  email: '',
  phone: '',
  role: 0,
  createdAt: ''
})

// 用户帖子
const userPosts = ref<any[]>([])

// 用户收藏
const collections = reactive({
  recognitions: [] as any[],
  posts: [] as any[],
  knowledge: [] as any[]
})

// 用户点赞
const likes = reactive({
  posts: [] as any[],
  knowledge: [] as any[],
  comments: [] as any[]
})

// 头像上传相关变量
const avatarModalVisible = ref(false)
const uploadLoading = ref(false)

// 帖子编辑相关变量
const editModalVisible = ref(false)
const currentEditPost = ref<any>(null)

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
    
    // 加载用户帖子
    loadUserPosts()
    // 加载用户收藏
    loadUserCollections()
    // 加载用户点赞
    loadUserLikes()
  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    message.error('加载用户信息失败')
  }
}

// 加载用户发布的帖子
async function loadUserPosts() {
  try {
    const response = await CommunityAPI.getMyPosts({
      page: 1,
      size: 10
    })
    
    if (response && response.data) {
      // 转换数据格式以适配组件
      userPosts.value = response.data.map((post: any) => ({
        id: post.id,
        title: post.title,
        excerpt: post.content ? post.content.substring(0, 100) + '...' : '',
        type: 'share', // 可以根据实际情况设置
        likes: post.likeCount || 0,
        replies: post.commentCount || 0,
        createTime: formatDateTime(post.createdAt),
        image: post.images ? getFirstImage(post.images) : undefined,
        status: post.status || 1, // 添加状态字段: 0-待审核, 1-已发布, 2-已隐藏
        category: post.category || '',
        tags: post.tags || '',
        content: post.content || ''
      }))
      
      console.log('用户帖子加载成功:', userPosts.value.length, '条')
    }
  } catch (error: any) {
    console.error('加载用户帖子失败:', error)
    // 静默失败，不影响页面显示
  }
}

// 从images JSON字符串中获取第一张图片
function getFirstImage(imagesJson: string): string | undefined {
  try {
    const images = JSON.parse(imagesJson)
    return images && images.length > 0 ? FileAPI.getImageUrl(images[0]) : undefined
  } catch {
    return undefined
  }
}

// 格式化日期时间
function formatDateTime(dateString: any): string {
  if (!dateString) return '-'
  
  try {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    
    // 小于1小时显示分钟
    if (diff < 3600000) {
      const minutes = Math.floor(diff / 60000)
      return minutes < 1 ? '刚刚' : `${minutes}分钟前`
    }
    // 小于24小时显示小时
    if (diff < 86400000) {
      const hours = Math.floor(diff / 3600000)
      return `${hours}小时前`
    }
    // 小于7天显示天数
    if (diff < 604800000) {
      const days = Math.floor(diff / 86400000)
      return `${days}天前`
    }
    // 否则显示具体日期
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } catch {
    return '-'
  }
}

// 加载用户收藏
async function loadUserCollections() {
  try {
    const response = await UserAPI.getCollections({
      page: 1,
      size: 10
    })
    
    if (response) {
      Object.assign(collections, {
        recognitions: response.recognitions || [],
        posts: response.posts || [],
        knowledge: response.knowledge || []
      })
      
      console.log('用户收藏加载成功:', collections)
    }
  } catch (error: any) {
    console.error('加载用户收藏失败:', error)
    // 静默失败，不影响页面显示
  }
}

// 加载用户点赞
async function loadUserLikes() {
  try {
    const response = await UserAPI.getLikes({
      page: 1,
      size: 10
    })
    
    if (response) {
      Object.assign(likes, {
        posts: response.posts || [],
        knowledge: response.knowledge || [],
        comments: response.comments || []
      })
      
      console.log('用户点赞加载成功:', likes)
    }
  } catch (error: any) {
    console.error('加载用户点赞失败:', error)
    // 静默失败，不影响页面显示
  }
}

// 获取图片URL
function getImageUrl(url?: string): string {
  if (!url) return '/api/placeholder/150/150'
  return FileAPI.getImageUrl(url)
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

// 编辑帖子
function editPost(post: any) {
  console.log('编辑帖子:', post)
  currentEditPost.value = post
  editModalVisible.value = true
}

// 编辑成功回调
async function handleEditSuccess() {
  message.success('帖子更新成功!')
  // 重新加载帖子列表
  await loadUserPosts()
}

// 删除帖子
async function deletePost(post: any) {
  try {
    await new Promise((resolve) => {
      const modal = message.loading('正在删除...', 0)
      
      // 使用 Ant Design 的 Modal.confirm
      import('ant-design-vue').then(({ Modal }) => {
        modal()
        Modal.confirm({
          title: '确认删除',
          content: `确定要删除帖子"${post.title}"吗？删除后将无法恢复。`,
          okText: '确认删除',
          okType: 'danger',
          cancelText: '取消',
          onOk: async () => {
            try {
              await CommunityAPI.deletePost(post.id)
              message.success('删除成功')
              // 重新加载帖子列表
              await loadUserPosts()
              resolve(true)
            } catch (error: any) {
              message.error(error.message || '删除失败')
              throw error
            }
          },
          onCancel: () => {
            resolve(false)
          }
        })
      })
    })
  } catch (error: any) {
    console.error('删除帖子失败:', error)
  }
}

// 隐藏帖子
async function hidePost(post: any) {
  try {
    const loading = message.loading('正在隐藏...', 0)
    await CommunityAPI.updatePost(post.id, { status: 2 })
    loading()
    message.success('帖子已隐藏')
    // 更新本地状态
    const index = userPosts.value.findIndex(p => p.id === post.id)
    if (index !== -1) {
      userPosts.value[index].status = 2
    }
  } catch (error: any) {
    console.error('隐藏帖子失败:', error)
    message.error(error.message || '隐藏失败')
  }
}

// 公开帖子
async function showPost(post: any) {
  try {
    const loading = message.loading('正在公开...', 0)
    await CommunityAPI.updatePost(post.id, { status: 1 })
    loading()
    message.success('帖子已公开')
    // 更新本地状态
    const index = userPosts.value.findIndex(p => p.id === post.id)
    if (index !== -1) {
      userPosts.value[index].status = 1
    }
  } catch (error: any) {
    console.error('公开帖子失败:', error)
    message.error(error.message || '公开失败')
  }
}

</script>
