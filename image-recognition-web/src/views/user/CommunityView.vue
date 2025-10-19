<template>
  <div class="community-page">
    <div class="page-header">
      <h1>社区讨论</h1>
      <p>分享交流您的图像识别经验和见解</p>
    </div>


    <!-- 发布对话框 -->
    <PostPublishModal
      v-model:open="publishModalVisible"
      :current-user="currentUser"
      @publish="handlePublish"
      @save-draft="handleSaveDraft"
    />

    <!-- 筛选和搜索 -->
    <a-card class="filter-card">
      <div class="filter-bar">
        <div class="filter-tabs">
          <a-radio-group v-model:value="activeFilter" button-style="solid" @change="handleFilterChange">
            <a-radio-button value="all">全部</a-radio-button>
            <a-radio-button value="hot">热门</a-radio-button>
            <a-radio-button value="latest">最新</a-radio-button>
          </a-radio-group>
        </div>
        <div class="search-section">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索帖子内容、作者..."
            @search="handleSearch"
            @change="handleSearchChange"
            style="width: 320px"
            size="large"
          >
            <template #suffix>
              <a-dropdown v-if="searchKeyword">
                <a-button type="text" size="small">
                  <i class="fas fa-filter"></i>
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="searchInContent">
                      <i class="fas fa-file-text"></i>
                      搜索内容
                    </a-menu-item>
                    <a-menu-item @click="searchInAuthor">
                      <i class="fas fa-user"></i>
                      搜索作者
                    </a-menu-item>
                    <a-menu-item @click="searchInTags">
                      <i class="fas fa-tag"></i>
                      搜索标签
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </template>
          </a-input-search>
          <a-button type="primary" size="large" @click="openPublishModal">
            <i class="fas fa-edit"></i>
            我要发布
          </a-button>
        </div>
      </div>
      
      <!-- 高级搜索 -->
      <div v-if="showAdvancedSearch" class="advanced-search">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-select
              v-model:value="advancedSearch.timeRange"
              placeholder="时间范围"
              style="width: 100%"
            >
              <a-select-option value="">全部时间</a-select-option>
              <a-select-option value="today">今天</a-select-option>
              <a-select-option value="week">本周</a-select-option>
              <a-select-option value="month">本月</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="8">
            <a-select
              v-model:value="advancedSearch.authorLevel"
              placeholder="作者等级"
              style="width: 100%"
            >
              <a-select-option value="">全部等级</a-select-option>
              <a-select-option value="新手">新手</a-select-option>
              <a-select-option value="资深用户">资深用户</a-select-option>
              <a-select-option value="专家">专家</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="8">
            <a-select
              v-model:value="advancedSearch.sortBy"
              placeholder="排序方式"
              style="width: 100%"
            >
              <a-select-option value="time">时间排序</a-select-option>
              <a-select-option value="likes">点赞数</a-select-option>
              <a-select-option value="replies">回复数</a-select-option>
              <a-select-option value="views">浏览数</a-select-option>
            </a-select>
          </a-col>
        </a-row>
      </div>
      
      <!-- 搜索结果统计 -->
      <div v-if="searchKeyword" class="search-results-info">
        <span>找到 {{ filteredPosts.length }} 个相关结果</span>
        <a-button type="link" size="small" @click="clearSearch">
          <i class="fas fa-times"></i>
          清除搜索
        </a-button>
      </div>
    </a-card>

    <!-- 帖子列表 -->
    <a-spin :spinning="loading && postsData.length === 0" tip="加载中...">
      <a-empty v-if="!loading && filteredPosts.length === 0" description="暂无帖子数据" />
      <div v-else class="posts-container">
        <PostCard
          v-for="post in filteredPosts"
          :key="post.id"
          :post="post"
          @viewDetail="viewPostDetail"
          @previewImage="previewImage"
          @searchByTag="searchByTag"
          @toggleLike="toggleLike"
          @toggle-favorite="toggleFavorite"
          @reply="replyPost"
        />
      </div>
    </a-spin>

    <!-- 加载更多 -->
    <div v-if="filteredPosts.length > 0" class="load-more">
      <a-button @click="loadMore" :loading="loading" size="large">
        {{ pagination.current * pagination.pageSize >= pagination.total ? '没有更多了' : '加载更多' }}
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import PostPublishModal from '@/components/PostPublishModal.vue'
import PostCard from '@/components/PostCard.vue'
import { CommunityAPI } from '@/api/community'
import { FileAPI } from '@/api/file'
import type { Post, CreatePostRequest } from '@/api/types'

// 扩展的帖子类型（用于前端显示）
interface ExtendedPost {
  id: number
  title: string
  content: string
  category: string
  author: {
    name: string
    avatar: string
    level: string
  }
  type: string
  views: number
  likes: number
  replies: number
  createTime: string
  isLiked: boolean
  isFavorited: boolean
  tags: string[]
  images: string[]
  topReplies?: any[]
}

const router = useRouter()
const activeFilter = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const publishModalVisible = ref(false)
const showAdvancedSearch = ref(false)

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

// 当前用户信息（从localStorage或Vuex获取）
const currentUser = reactive({
  name: localStorage.getItem('userName') || '用户',
  avatar: localStorage.getItem('userAvatar') || ''
})

// 高级搜索条件
const advancedSearch = reactive({
  timeRange: '',
  authorLevel: '',
  sortBy: 'time'
})

// 帖子数据
const postsData = ref<ExtendedPost[]>([])

// 加载帖子列表
async function loadPosts() {
  try {
    loading.value = true
    
    // 确定排序方式
    let sort: 'latest' | 'hot' | 'top' = 'latest'
    if (activeFilter.value === 'hot') {
      sort = 'hot'
    } else if (activeFilter.value === 'latest') {
      sort = 'latest'
    }
    
    const response = await CommunityAPI.getPosts({
      page: pagination.current,
      size: pagination.pageSize,
      sort
    })
    
    console.log('帖子列表响应:', response.data)
    
    // 转换数据格式
    postsData.value = response.data.data.map(post => ({
      id: post.id,
      title: post.title,
      content: post.content,
      category: post.category,
      author: {
        name: post.authorName || '未知用户',
        avatar: FileAPI.getImageUrl(post.authorAvatar),
        level: '用户' // 可以根据用户角色设置
      },
      type: post.category || 'discussion',
      views: post.viewCount,
      likes: post.likeCount,
      replies: post.commentCount,
      createTime: formatTime(post.createTime),
      isLiked: false, // 需要从后端获取当前用户的点赞状态
      isFavorited: false, // 需要从后端获取当前用户的收藏状态
      tags: post.tags ? post.tags.split(',').filter((t: string) => t.trim()) : [],
      images: post.images ? JSON.parse(post.images).map((url: string) => FileAPI.getImageUrl(url)) : [],
      topReplies: []
    })) as ExtendedPost[]
    
    pagination.total = response.data.total
    
    console.log('转换后的帖子数据:', postsData.value)
  } catch (error) {
    console.error('加载帖子列表失败:', error)
    message.error('加载帖子列表失败')
  } finally {
    loading.value = false
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
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
  }
}

// 筛选后的帖子（客户端筛选）
const filteredPosts = computed(() => {
  let result = postsData.value

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(post => 
      post.content.toLowerCase().includes(keyword) ||
      post.title.toLowerCase().includes(keyword) ||
      post.author.name.toLowerCase().includes(keyword) ||
      post.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  return result
})

// 方法
function openPublishModal() {
  publishModalVisible.value = true
}

async function handlePublish(postData: any) {
  try {
    console.log('发布帖子数据:', postData)
    
    // 构建请求数据
    const tags = Array.isArray(postData.tags) 
      ? postData.tags 
      : (typeof postData.tags === 'string' ? postData.tags.split(',').map((t: string) => t.trim()).filter((t: string) => t) : [])
    
    const createRequest: CreatePostRequest = {
      title: postData.title,
      content: postData.content,
      category: postData.category || '综合讨论',
      tags: tags,
      images: postData.images || undefined
    }
    
    const response = await CommunityAPI.createPost(createRequest)
    
    if (response.data.success) {
      message.success('发布成功！')
      publishModalVisible.value = false
      // 重新加载帖子列表
      pagination.current = 1
      await loadPosts()
    } else {
      message.error(response.data.message || '发布失败')
    }
  } catch (error) {
    console.error('发布帖子失败:', error)
    message.error('发布失败，请重试')
  }
}

async function handleSaveDraft(draftData: any) {
  // 保存草稿到本地存储
  try {
    const drafts = JSON.parse(localStorage.getItem('postDrafts') || '[]')
    drafts.push({
      ...draftData,
      saveTime: new Date().toISOString()
    })
    localStorage.setItem('postDrafts', JSON.stringify(drafts))
    message.success('草稿已保存')
  } catch (error) {
    console.error('保存草稿失败:', error)
    message.error('保存草稿失败')
  }
}


function viewPostDetail(post: any) {
  router.push(`/user/community/post/${post.id}`)
}

function searchByTag(tag: string) {
  searchKeyword.value = tag
  handleSearch()
}

function handleSearchChange() {
  // 搜索变化时的处理
}

function searchInContent() {
  message.info('搜索内容功能')
}

function searchInAuthor() {
  message.info('搜索作者功能')
}

function searchInTags() {
  message.info('搜索标签功能')
}

function clearSearch() {
  searchKeyword.value = ''
  showAdvancedSearch.value = false
}

function addImage() {
  message.info('添加图片功能开发中')
}

function addLocation() {
  message.info('添加位置功能开发中')
}

function handleSearch() {
  // 搜索逻辑在 computed 中处理
  console.log('搜索关键词:', searchKeyword.value)
}

async function toggleLike(post: any) {
  try {
    if (post.isLiked) {
      // 取消点赞
      await CommunityAPI.unlikePost(post.id)
      post.isLiked = false
      post.likes--
      message.success('取消点赞')
    } else {
      // 点赞
      await CommunityAPI.likePost(post.id)
      post.isLiked = true
      post.likes++
      message.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    message.error('操作失败，请重试')
  }
}

async function toggleFavorite(post: any) {
  // 收藏功能暂时使用本地状态
  // TODO: 等待后端添加收藏API
  post.isFavorited = !post.isFavorited
  
  // 保存到本地存储
  try {
    const favorites = JSON.parse(localStorage.getItem('postFavorites') || '[]')
    if (post.isFavorited) {
      if (!favorites.includes(post.id)) {
        favorites.push(post.id)
      }
      message.success('收藏成功')
    } else {
      const index = favorites.indexOf(post.id)
      if (index > -1) {
        favorites.splice(index, 1)
      }
      message.success('取消收藏')
    }
    localStorage.setItem('postFavorites', JSON.stringify(favorites))
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}

function replyPost(post: any) {
  // 跳转到帖子详情页并聚焦评论框
  router.push({
    path: `/user/community/post/${post.id}`,
    query: { action: 'reply' }
  })
}

function sharePost(post: any) {
  // 复制分享链接
  const shareUrl = `${window.location.origin}/user/community/post/${post.id}`
  navigator.clipboard.writeText(shareUrl).then(() => {
    message.success('分享链接已复制到剪贴板')
  }).catch(() => {
    message.info(`分享链接：${shareUrl}`)
  })
}

function previewImage(images: string[], index: number) {
  // 可以使用图片预览组件
  console.log('预览图片:', images, index)
  message.info(`预览图片 ${index + 1}`)
}

async function loadMore() {
  if (pagination.current * pagination.pageSize >= pagination.total) {
    message.info('没有更多内容了')
    return
  }
  
  pagination.current++
  await loadPosts()
}

// 监听筛选器变化
function handleFilterChange() {
  pagination.current = 1
  loadPosts()
}

// 页面加载时获取数据
onMounted(() => {
  loadPosts()
})

// 导出给模板使用
defineExpose({
  loadPosts,
  handleFilterChange
})
</script>

