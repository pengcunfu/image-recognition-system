<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
      <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
        <div :style="{ textAlign: 'center' }">
          <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }">
            社区讨论
          </h1>
          <p :style="{ margin: 0, fontSize: '14px', opacity: 0.65 }">分享交流您的图像识别经验和见解</p>
        </div>
      </a-card>

    <!-- 发布对话框 -->
    <PostPublishModal
      v-model:open="publishModalVisible"
      :current-user="currentUser"
      @publish="handlePublish"
      @save-draft="handleSaveDraft"
    />

      <!-- 筛选和搜索 -->
      <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
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
        <div v-else :style="{ 
          columnCount: 'auto',
          columnWidth: '280px',
          columnGap: '16px',
          columnFill: 'balance'
        }">
          <div 
            v-for="post in filteredPosts"
            :key="post.id"
            :style="{ 
              breakInside: 'avoid',
              marginBottom: '16px',
              width: '100%'
            }"
          >
            <div 
              :style="{ 
                borderRadius: '8px', 
                overflow: 'hidden', 
                cursor: 'pointer',
                transition: 'all 0.3s',
                boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                background: 'white',
                border: '1px solid #e8e8e8'
              }"
              @click="viewPostDetail(post)"
              @mouseenter="(e) => { setStyle(e, 'box-shadow', '0 4px 16px rgba(24,144,255,0.2)'); setStyle(e, 'border-color', '#1890ff'); }"
              @mouseleave="(e) => { setStyle(e, 'box-shadow', '0 2px 8px rgba(0,0,0,0.06)'); setStyle(e, 'border-color', '#e8e8e8'); }"
            >
              <!-- 作者信息 -->
              <div :style="{ padding: '12px 12px 8px 12px' }">
                <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', height: '32px' }">
                  <a-avatar :src="post.author.avatar" :size="24">
                    {{ post.author.name.charAt(0) }}
                  </a-avatar>
                  <div :style="{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', justifyContent: 'center' }">
                    <div :style="{ display: 'flex', alignItems: 'center', gap: '6px', height: '16px', marginBottom: '2px' }">
                      <span :style="{ 
                        fontWeight: 600, 
                        color: '#262626', 
                        fontSize: '13px',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                        maxWidth: '120px'
                      }">{{ post.author.name }}</span>
                      <a-tag 
                        v-if="post.author.level" 
                        :color="getLevelColor(post.author.level)" 
                        size="small" 
                        :style="{ 
                          fontSize: '10px', 
                          padding: '0 6px', 
                          height: '16px', 
                          lineHeight: '16px',
                          borderRadius: '8px',
                          transform: 'scale(0.9)',
                          transformOrigin: 'left center'
                        }"
                      >
                        {{ post.author.level }}
                      </a-tag>
                    </div>
                    <div :style="{ display: 'flex', alignItems: 'center', gap: '6px', height: '14px' }">
                      <span :style="{ 
                        fontSize: '11px', 
                        color: '#8c8c8c',
                        lineHeight: '14px'
                      }">{{ post.createTime }}</span>
                      <span :style="{ color: '#d9d9d9', fontSize: '10px' }">•</span>
                      <a-tag 
                        :color="getTypeColor(post.type)" 
                        size="small" 
                        :style="{ 
                          fontSize: '10px', 
                          padding: '0 4px', 
                          height: '14px', 
                          lineHeight: '14px',
                          borderRadius: '6px',
                          transform: 'scale(0.85)',
                          transformOrigin: 'left center'
                        }"
                      >
                        {{ getTypeName(post.type) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 标题和内容 -->
              <div :style="{ padding: '0 12px 8px 12px' }">
                <h3 :style="{ 
                  fontSize: '15px', 
                  fontWeight: 600, 
                  color: '#262626', 
                  margin: '0 0 6px 0', 
                  lineHeight: '22px', 
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap'
                }">{{ post.title || '无标题' }}</h3>
                <p :style="{ 
                  color: '#666', 
                  lineHeight: '20px', 
                  margin: 0, 
                  fontSize: '13px',
                  height: '40px',
                  display: '-webkit-box',
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden'
                }">{{ getContentPreview(post.content) }}</p>
              </div>

              <!-- 图片展示 -->
              <div 
                v-if="post.images?.length" 
                :style="{ 
                  position: 'relative', 
                  width: '100%', 
                  paddingBottom: '66.67%',
                  background: '#f5f5f5',
                  overflow: 'hidden',
                  margin: '0 12px 12px 12px',
                  borderRadius: '8px'
                }"
                @click.stop="previewImage(post.images, 0)"
              >
                <img 
                  :src="post.images[0]" 
                  :alt="post.title" 
                  :style="{ 
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%', 
                    height: '100%', 
                    objectFit: 'cover',
                    transition: 'transform 0.3s'
                  }" 
                  @mouseenter="(e) => setStyle(e, 'transform', 'scale(1.05)')"
                  @mouseleave="(e) => setStyle(e, 'transform', 'scale(1)')"
                />
              </div>

              <!-- 标签 -->
              <div 
                v-if="post.tags?.length" 
                :style="{ padding: '0 12px 12px 12px' }"
              >
                <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '4px', height: '24px', overflow: 'hidden' }">
                  <a-tag 
                    v-for="tag in post.tags.slice(0, 3)" 
                    :key="tag"
                    size="small"
                    :style="{ cursor: 'pointer', transition: 'all 0.3s', borderRadius: '8px', fontSize: '12px' }"
                    @click.stop="searchByTag(tag)"
                  >
                    {{ tag }}
                  </a-tag>
                  <span v-if="post.tags.length > 3" :style="{ color: '#999', fontSize: '12px', alignSelf: 'center' }">+{{ post.tags.length - 3 }}</span>
                </div>
              </div>

              <!-- 操作栏 -->
              <div :style="{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center', 
                padding: '8px 12px 12px 12px',
                borderTop: '1px solid #f0f0f0',
                marginTop: 'auto',
                height: '40px'
              }">
                <div :style="{ display: 'flex', alignItems: 'center', gap: '16px' }">
                  <span :style="{ display: 'flex', alignItems: 'center', gap: '4px', color: '#999', fontSize: '12px' }">
                    <i class="fas fa-eye"></i>
                    {{ post.views || 0 }} 浏览
                  </span>
                </div>
                <div :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
                  <a-button 
                    type="text" 
                    size="small"
                    :style="{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      gap: '4px', 
                      color: post.isLiked ? '#1890ff' : '#666',
                      background: post.isLiked ? 'rgba(24, 144, 255, 0.06)' : 'transparent',
                      transition: 'all 0.3s ease',
                      borderRadius: '6px',
                      padding: '4px 8px',
                      height: 'auto',
                      fontSize: '12px'
                    }"
                    @click.stop="toggleLike(post)"
                  >
                    <i class="fas fa-heart"></i>
                    {{ post.likes }}
                  </a-button>
                  <a-button 
                    type="text" 
                    size="small"
                    :style="{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      gap: '4px', 
                      color: '#666',
                      transition: 'all 0.3s ease',
                      borderRadius: '6px',
                      padding: '4px 8px',
                      height: 'auto',
                      fontSize: '12px'
                    }"
                    @click.stop="replyPost(post)"
                  >
                    <i class="fas fa-comment"></i>
                    {{ post.replies }}
                  </a-button>
                  <a-button 
                    type="text" 
                    size="small"
                    :style="{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      gap: '4px', 
                      color: post.isFavorited ? '#ff4d4f' : '#666',
                      background: post.isFavorited ? 'rgba(255, 77, 79, 0.06)' : 'transparent',
                      transition: 'all 0.3s ease',
                      borderRadius: '6px',
                      padding: '4px 8px',
                      height: 'auto',
                      fontSize: '12px'
                    }"
                    @click.stop="toggleFavorite(post)"
                  >
                    <i class="fas fa-star"></i>
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-spin>

      <!-- 加载更多 -->
      <div v-if="filteredPosts.length > 0" :style="{ textAlign: 'center', marginTop: '16px' }">
        <a-button @click="loadMore" :loading="loading" size="large" :style="{ minWidth: '200px' }">
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
      // 处理图片数据
      let images: string[] = []
      if (post.images) {
        if (Array.isArray(post.images)) {
          images = post.images.map((url: string) => ImageUtils.getImageUrl(url))
        } else if (typeof post.images === 'string') {
          try {
            const parsed = JSON.parse(post.images)
            images = Array.isArray(parsed) ? parsed.map((url: string) => ImageUtils.getImageUrl(url)) : []
          } catch (e) {
            console.warn('解析图片失败:', post.images, e)
            images = []
          }
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

// 辅助函数：安全设置元素样式
function setStyle(event: Event, property: string, value: string) {
  const target = event.currentTarget as HTMLElement
  if (target) {
    target.style.setProperty(property, value)
  }
}

// 获取内容预览
function getContentPreview(content: string) {
  return content.length > 100 ? content.substring(0, 100) : content
}

// 获取用户等级颜色
function getLevelColor(level: string) {
  const colors: Record<string, string> = {
    '新手': 'green',
    '资深用户': 'blue',
    '专家': 'purple',
    '管理员': 'red',
    'VIP用户1': 'gold',
    'VIP用户2': 'gold',
    '用户': 'default'
  }
  return colors[level] || 'default'
}

// 获取帖子类型颜色
function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    'question': 'orange',
    'share': 'blue',
    'discussion': 'purple',
    '问答': 'orange',
    '分享': 'blue',
    '讨论': 'purple'
  }
  return colors[type] || 'default'
}

// 获取帖子类型名称
function getTypeName(type: string) {
  const names: Record<string, string> = {
    'question': '问答',
    'share': '分享',
    'discussion': '讨论'
  }
  return names[type] || type || '讨论'
}

// 导出给模板使用
defineExpose({
  loadPosts
})
</script>

