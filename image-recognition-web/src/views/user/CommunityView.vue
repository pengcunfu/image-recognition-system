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
          <a-radio-group v-model:value="activeFilter" button-style="solid">
            <a-radio-button value="all">全部</a-radio-button>
            <a-radio-button value="hot">热门</a-radio-button>
            <a-radio-button value="latest">最新</a-radio-button>
            <a-radio-button value="question">问答</a-radio-button>
            <a-radio-button value="share">分享</a-radio-button>
            <a-radio-button value="discussion">讨论</a-radio-button>
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
    <div class="posts-container">
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

    <!-- 加载更多 -->
    <div class="load-more">
      <a-button @click="loadMore" :loading="loading">
        加载更多
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import PostPublishModal from '@/components/PostPublishModal.vue'
import PostCard from '@/components/PostCard.vue'

const router = useRouter()
const activeFilter = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const publishModalVisible = ref(false)
const showAdvancedSearch = ref(false)

// 当前用户信息
const currentUser = reactive({
  name: '张三',
  avatar: ''
})

// 高级搜索条件
const advancedSearch = reactive({
  timeRange: '',
  authorLevel: '',
  sortBy: 'time'
})

// 帖子数据
const postsData = ref([
  {
    id: 1,
    title: '求助：如何提高模糊图片的识别准确率？',
    author: {
      name: '张三',
      avatar: '',
      level: '资深用户'
    },
    content: '今天用AI识别了一张模糊的动物照片，结果准确率达到了95%！真的很神奇，但是我想进一步提高准确率。想请教大家有什么提高识别准确率的技巧吗？比如图片预处理、参数调整等方面的经验。我尝试了一些基本的方法，但效果不是特别明显，希望能得到大家的指导。',
    type: 'question',
    createTime: '2小时前',
    views: 156,
    likes: 24,
    replies: 8,
    isLiked: false,
    isFavorited: false,
    tags: ['AI识别', '图像处理', '问题求助'],
    images: ['/api/placeholder/300/200'],
    topReplies: []
  },
  {
    id: 2,
    title: '花卉识别分享 - AI识别准确率惊人！',
    author: {
      name: '王五',
      avatar: '',
      level: '新手'
    },
    content: '分享一下我今天识别的几种花卉，AI的识别能力真的越来越强了！特别是对于这些常见花卉的识别准确率非常高。我用系统识别了玫瑰、菊花、百合等多种花卉，准确率都在90%以上。这对于我这个植物爱好者来说真是太有用了！推荐大家也试试看。',
    type: 'share',
    createTime: '4小时前',
    views: 89,
    likes: 15,
    replies: 3,
    isLiked: true,
    isFavorited: false,
    tags: ['技术分享', '花卉识别', '经验总结'],
    images: ['/api/placeholder/300/200', '/api/placeholder/300/200', '/api/placeholder/300/200', '/api/placeholder/300/200'],
    topReplies: []
  },
  {
    id: 3,
    title: '讨论：AI图像识别的未来发展趋势',
    author: {
      name: '赵六',
      avatar: '',
      level: '专家'
    },
    content: '大家觉得AI图像识别技术在未来会如何发展？会不会完全替代人工识别？我个人认为AI在标准化场景下已经表现得非常出色，但在一些特殊情况下，人工识别仍然有其不可替代的价值。比如艺术品鉴定、医学影像的最终确认等。欢迎大家发表看法，让我们一起探讨这个有趣的话题！',
    type: 'discussion',
    createTime: '1天前',
    views: 234,
    likes: 45,
    replies: 18,
    isLiked: false,
    isFavorited: true,
    tags: ['AI识别', '深度学习', '技术讨论', '未来趋势'],
    images: ['/api/placeholder/400/240'],
    topReplies: []
  }
])

// 筛选后的帖子
const filteredPosts = computed(() => {
  let result = postsData.value

  // 按类型筛选
  if (activeFilter.value !== 'all') {
    if (activeFilter.value === 'hot') {
      result = result.sort((a, b) => (b.likes + b.replies) - (a.likes + a.replies))
    } else if (activeFilter.value === 'latest') {
      result = result.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    } else {
      result = result.filter(post => post.type === activeFilter.value)
    }
  }

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(post => 
      post.content.toLowerCase().includes(keyword) ||
      post.author.name.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 方法
function openPublishModal() {
  publishModalVisible.value = true
}

function handlePublish(postData: any) {
  // 添加到帖子列表
  postsData.value.unshift(postData)
}

function handleSaveDraft(draftData: any) {
  // 处理保存草稿逻辑
  console.log('保存草稿:', draftData)
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
}


function toggleLike(post: any) {
  post.isLiked = !post.isLiked
  post.likes += post.isLiked ? 1 : -1
  message.success(post.isLiked ? '点赞成功' : '取消点赞')
}

function toggleFavorite(post: any) {
  post.isFavorited = !post.isFavorited
  message.success(post.isFavorited ? '收藏成功' : '取消收藏')
}

function replyPost(post: any) {
  message.info(`回复帖子：${post.content.substring(0, 20)}...`)
}

function sharePost(post: any) {
  message.info('分享功能开发中')
}

function previewImage(images: string[], index: number) {
  message.info(`预览图片 ${index + 1}`)
}


function loadMore() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.info('已加载更多内容')
  }, 1000)
}
</script>

<style scoped>
.community-page {
  padding: 0;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 16px;
  color: #666;
}



/* 筛选区域 */
.filter-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.advanced-search {
  margin-top: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.search-results-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding: 12px 16px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 6px;
  font-size: 14px;
  color: #0050b3;
}

/* 帖子列表 */
.posts-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.post-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: box-shadow 0.3s;
  height: 400px;
}

.post-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.post-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-meta-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 500;
  color: #262626;
  font-size: 14px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-title {
  margin-bottom: 12px;
  cursor: pointer;
}

.post-title h3 {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0;
  line-height: 1.3;
  transition: color 0.3s;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-title:hover h3 {
  color: #1890ff;
}

.post-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-preview {
  color: #666;
  line-height: 1.4;
  margin-bottom: 12px;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-preview:hover {
  color: #1890ff;
}

.read-more {
  color: #1890ff;
  font-weight: 500;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
  min-height: 24px;
}

.post-tags .ant-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.post-tags .ant-tag:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.more-tags {
  color: #999;
  font-size: 12px;
}

.post-images {
  margin-top: auto;
  margin-bottom: 0;
}

.post-image {
  position: relative;
  width: 100%;
  height: 140px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.post-image:hover img {
  transform: scale(1.05);
}

.more-images {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: 500;
}

.post-actions-inline {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f9fa;
  border-radius: 20px;
  padding: 8px 16px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: transparent;
  border: none;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
  min-width: 45px;
  justify-content: center;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
  transform: translateY(-1px);
}

.action-btn.liked {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.action-btn.liked:hover {
  background: rgba(24, 144, 255, 0.2);
}

.action-btn.favorited {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.action-btn.favorited:hover {
  background: rgba(255, 77, 79, 0.2);
}

.action-btn i {
  font-size: 14px;
}

.action-btn span {
  font-weight: 500;
}


/* 加载更多 */
.load-more {
  text-align: center;
  padding: 24px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .posts-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .posts-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .post-item {
    padding: 16px;
  }
  
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .post-author-info {
    width: 100%;
  }
  
  .post-meta-info {
    width: 100%;
    justify-content: flex-start;
  }
  
  .post-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .post-actions {
    justify-content: space-around;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-section {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}

@media (max-width: 576px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .post-options {
    flex-direction: column;
    align-items: stretch;
  }
  
  .post-actions {
    flex-wrap: wrap;
    gap: 4px;
  }
  
  .post-images {
    justify-content: center;
  }
}
</style>
