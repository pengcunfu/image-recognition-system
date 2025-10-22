<template>
  <div :style="{ minHeight: '100vh', background: '#f5f5f5', padding: '40px 0' }">
    <div :style="{ maxWidth: '1200px', margin: '0 auto', padding: '0 24px' }">
      <!-- 页面头部 -->
      <div :style="{ textAlign: 'center', marginBottom: '40px' }">
        <h1 :style="{ fontSize: '48px', fontWeight: 'bold', margin: '0 0 16px 0', color: '#1890ff' }">
          <i class="fas fa-comments" :style="{ marginRight: '16px' }"></i>
          社区讨论
        </h1>
        <p :style="{ fontSize: '18px', margin: 0, color: '#595959' }">分享交流您的图像识别经验和见解</p>
      </div>

    <!-- 发布对话框 -->
    <PostPublishModal
      v-model:open="publishModalVisible"
      :current-user="currentUser"
      @publish="handlePublish"
      @save-draft="handleSaveDraft"
    />

      <!-- 筛选和搜索 -->
      <a-card :style="{ borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)', marginBottom: '32px', border: 'none' }">
        <!-- 分类标签 -->
        <div :style="{ marginBottom: '16px' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }">
            <span :style="{ fontSize: '14px', fontWeight: '500', color: '#8c8c8c' }">分类:</span>
            <a-tag
              :color="selectedCategory === '' ? 'blue' : 'default'"
              :style="{ cursor: 'pointer', fontSize: '14px', padding: '4px 12px' }"
              @click="selectCategory('')"
            >
              全部 ({{ totalPostsCount }})
            </a-tag>
            <a-tag
              v-for="category in categories"
              :key="category.name"
              :color="selectedCategory === category.name ? 'blue' : 'default'"
              :style="{ cursor: 'pointer', fontSize: '14px', padding: '4px 12px' }"
              @click="selectCategory(category.name)"
            >
              {{ category.name }} ({{ category.count }})
            </a-tag>
          </div>
        </div>

        <!-- 热门标签 -->
        <div v-if="tags.length > 0" :style="{ marginBottom: '16px', paddingTop: '12px', borderTop: '1px solid #f0f0f0' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }">
            <span :style="{ fontSize: '14px', fontWeight: '500', color: '#8c8c8c' }">热门标签:</span>
            <a-tag
              :color="selectedTag === '' ? 'orange' : 'default'"
              :style="{ cursor: 'pointer', fontSize: '13px', padding: '2px 10px' }"
              @click="selectTag('')"
            >
              全部
            </a-tag>
            <a-tag
              v-for="tag in tags.slice(0, 15)"
              :key="tag.name"
              :color="selectedTag === tag.name ? 'orange' : 'default'"
              :style="{ cursor: 'pointer', fontSize: '13px', padding: '2px 10px' }"
              @click="selectTag(tag.name)"
            >
              # {{ tag.name }} ({{ tag.count }})
            </a-tag>
          </div>
        </div>
        
        <div :style="{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', marginBottom: '16px' }">
          <div :style="{ display: 'flex', gap: '12px' }">
            <a-input-search
              v-model:value="searchKeyword"
              placeholder="搜索帖子内容、作者..."
              @search="handleSearch"
              @change="handleSearchChange"
              :style="{ width: '320px' }"
              size="large"
            />
            <a-button type="primary" size="large" @click="openPublishModal">
              <i class="fas fa-edit"></i>
              我要发布
            </a-button>
          </div>
        </div>
        
        <!-- 搜索结果统计 -->
        <div v-if="searchKeyword" :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px', background: '#e6f7ff', borderRadius: '8px' }">
          <span :style="{ color: '#1890ff', fontSize: '14px' }">找到 {{ filteredPosts.length }} 个相关结果</span>
          <a-button type="link" size="small" @click="clearSearch">
            <i class="fas fa-times"></i>
            清除搜索
          </a-button>
        </div>
      </a-card>

      <!-- 帖子列表 -->
      <a-spin :spinning="loading && postsData.length === 0" tip="加载中...">
        <a-empty v-if="!loading && filteredPosts.length === 0" description="暂无帖子数据" />
        <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
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
      <div v-if="filteredPosts.length > 0" :style="{ textAlign: 'center', marginTop: '32px' }">
        <a-button @click="loadMore" :loading="loading" size="large" :style="{ minWidth: '200px' }">
          {{ pagination.current * pagination.pageSize >= pagination.total ? '没有更多了' : '加载更多' }}
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import PostPublishModal from '@/components/PostPublishModal.vue'
import PostCard from '@/components/PostCard.vue'
import { CommunityAPI, type CreatePostRequest, type CategoryInfo, type TagInfo } from '@/api/community'
import { ImageUtils } from '@/utils/image'

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
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')
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

// 分类数据
const categories = ref<CategoryInfo[]>([])

// 标签数据
const tags = ref<TagInfo[]>([])

// 总帖子数
const totalPostsCount = computed(() => {
  return categories.value.reduce((sum, cat) => sum + cat.count, 0)
})

// 加载帖子列表
async function loadPosts() {
  try {
    loading.value = true
    
    const response = await CommunityAPI.getPosts({
      page: pagination.current,
      size: pagination.pageSize,
      category: selectedCategory.value || undefined,
      sort: 'latest'
    })
    
    console.log('帖子列表响应:', response)
    console.log('响应数据:', response.data)
    
    // 转换数据格式 - 使用 ImageUtils
    postsData.value = response.data.map(post => {
      // 解析图片数据
      let images: string[] = []
      if (post.images) {
        try {
          const parsed = JSON.parse(post.images)
          images = Array.isArray(parsed) ? parsed.map((url: string) => ImageUtils.getImageUrl(url)) : []
        } catch (e) {
          console.warn('解析图片失败:', post.images, e)
          images = []
        }
      }
      
      return {
        id: post.id,
        title: post.title,
        content: post.content,
        category: post.category,
        author: {
          name: post.authorName || '未知用户',
          avatar: ImageUtils.getImageUrl(post.authorAvatar),
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
        images,
        topReplies: []
      }
    }) as ExtendedPost[]
    
    pagination.total = response.total
    
    console.log('转换后的帖子数据:', postsData.value)
    console.log('帖子数量:', postsData.value.length)
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

  // 按标签筛选
  if (selectedTag.value) {
    result = result.filter(post => 
      post.tags.some(tag => tag === selectedTag.value)
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
    
    await CommunityAPI.createPost(createRequest)
    
    message.success('发布成功！')
    publishModalVisible.value = false
    // 重新加载帖子列表
    pagination.current = 1
    await loadPosts()
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

// 加载分类列表
async function loadCategories() {
  try {
    const response = await CommunityAPI.getCategories()
    categories.value = response || []
    console.log('分类列表:', categories.value)
  } catch (error) {
    console.error('加载分类列表失败:', error)
    // 不显示错误提示,静默失败
  }
}

// 加载标签列表
async function loadTags() {
  try {
    const response = await CommunityAPI.getTags()
    tags.value = response || []
    console.log('标签列表:', tags.value)
  } catch (error) {
    console.error('加载标签列表失败:', error)
    // 不显示错误提示,静默失败
  }
}

// 选择分类
function selectCategory(categoryName: string) {
  selectedCategory.value = categoryName
  selectedTag.value = '' // 清除标签选择
  pagination.current = 1
  loadPosts()
}

// 选择标签
function selectTag(tagName: string) {
  selectedTag.value = tagName
  pagination.current = 1
  loadPosts()
}

// 页面加载时获取数据
onMounted(() => {
  loadCategories()
  loadTags()
  loadPosts()
})

// 导出给模板使用
defineExpose({
  loadPosts
})
</script>

