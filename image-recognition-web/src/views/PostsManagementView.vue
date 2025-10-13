<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">帖子管理</h1>
      <a-space>
        <a-select
          v-model:value="filterStatus"
          placeholder="筛选状态"
          style="width: 120px"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="published">已发布</a-select-option>
          <a-select-option value="pending">待审核</a-select-option>
          <a-select-option value="rejected">已拒绝</a-select-option>
          <a-select-option value="hidden">已隐藏</a-select-option>
        </a-select>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索帖子标题或作者"
          style="width: 300px"
          @search="handleSearch"
        />
      </a-space>
    </div>
    
    <a-card class="table-card">
      <a-table 
        :columns="postColumns" 
        :data-source="filteredPosts"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="post-title">
              <a @click="viewPost(record)" class="title-link">
                {{ record.title }}
              </a>
              <div class="post-meta">
                <a-tag v-if="record.isTop" color="red" size="small">置顶</a-tag>
                <a-tag v-if="record.isHot" color="orange" size="small">热门</a-tag>
                <span class="post-category">{{ record.category }}</span>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'author'">
            <div class="author-info">
              <a-avatar :src="record.authorAvatar" size="small">
                {{ record.author.charAt(0) }}
              </a-avatar>
              <span class="author-name">{{ record.author }}</span>
              <a-tag v-if="record.authorVip" color="gold" size="small">VIP</a-tag>
            </div>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="getStatusColor(record.status)"
              class="status-tag"
            >
              <i :class="getStatusIcon(record.status)"></i>
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'stats'">
            <div class="post-stats">
              <span><i class="fas fa-eye"></i> {{ record.views }}</span>
              <span><i class="fas fa-thumbs-up"></i> {{ record.likes }}</span>
              <span><i class="fas fa-comment"></i> {{ record.replies }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="viewPost(record)">
                查看
              </a-button>
              <a-dropdown>
                <a-button size="small">
                  操作 <i class="fas fa-chevron-down"></i>
                </a-button>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item v-if="record.status === 'pending'" key="approve">
                      <i class="fas fa-check"></i> 审核通过
                    </a-menu-item>
                    <a-menu-item v-if="record.status === 'pending'" key="reject">
                      <i class="fas fa-times"></i> 拒绝发布
                    </a-menu-item>
                    <a-menu-item key="top" v-if="!record.isTop">
                      <i class="fas fa-arrow-up"></i> 置顶
                    </a-menu-item>
                    <a-menu-item key="untop" v-if="record.isTop">
                      <i class="fas fa-arrow-down"></i> 取消置顶
                    </a-menu-item>
                    <a-menu-item key="hide" v-if="record.status !== 'hidden'">
                      <i class="fas fa-eye-slash"></i> 隐藏
                    </a-menu-item>
                    <a-menu-item key="show" v-if="record.status === 'hidden'">
                      <i class="fas fa-eye"></i> 显示
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="delete" class="danger-item">
                      <i class="fas fa-trash"></i> 删除
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 帖子详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="帖子详情"
      width="60%"
      class="post-drawer"
    >
      <div v-if="selectedPost" class="post-detail">
        <div class="post-header">
          <h2>{{ selectedPost.title }}</h2>
          <div class="post-info">
            <a-avatar :src="selectedPost.author.avatar">
              {{ selectedPost.author.username.charAt(0) }}
            </a-avatar>
            <div class="author-details">
              <span class="author-name">{{ selectedPost.author.username }}</span>
              <span class="post-time">{{ selectedPost.createTime }}</span>
            </div>
            <a-tag :color="getStatusColor(selectedPost.status)">
              {{ getStatusText(selectedPost.status) }}
            </a-tag>
          </div>
        </div>
        
        <div class="post-content" v-html="selectedPost.content"></div>
        
        <div class="post-images" v-if="selectedPost.images && selectedPost.images.length > 0">
          <h4>图片附件</h4>
          <div class="image-grid">
            <img 
              v-for="(image, index) in selectedPost.images" 
              :key="index"
              :src="image"
              :alt="`图片${index + 1}`"
              @click="previewImage(image)"
            />
          </div>
        </div>
        
        <div class="post-actions">
          <a-space>
            <a-button 
              v-if="selectedPost.status === 'pending'" 
              type="primary" 
              @click="approvePost(selectedPost)"
            >
              审核通过
            </a-button>
            <a-button 
              v-if="selectedPost.status === 'pending'" 
              danger 
              @click="rejectPost(selectedPost)"
            >
              拒绝发布
            </a-button>
            <a-button @click="toggleTop(selectedPost)">
              {{ selectedPost.isTop ? '取消置顶' : '置顶' }}
            </a-button>
            <a-button @click="toggleVisibility(selectedPost)">
              {{ selectedPost.status === 'hidden' ? '显示' : '隐藏' }}
            </a-button>
          </a-space>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import CommunityAPI from '@/api/community'
import type { Post } from '@/api/types'

// 响应式数据
const loading = ref(false)
const drawerVisible = ref(false)
const selectedPost = ref<Post | null>(null)
const filterStatus = ref('')
const searchKeyword = ref('')

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// 帖子数据
const posts = ref<Post[]>([])

// 表格列定义
const postColumns = [
  { 
    title: '标题', 
    dataIndex: 'title', 
    key: 'title',
    width: 300,
    ellipsis: true
  },
  { 
    title: '作者', 
    dataIndex: 'author', 
    key: 'author',
    width: 150
  },
  { 
    title: '状态', 
    dataIndex: 'status', 
    key: 'status',
    width: 120
  },
  { 
    title: '统计', 
    key: 'stats',
    width: 150
  },
  { 
    title: '发布时间', 
    dataIndex: 'createTime', 
    key: 'createTime',
    width: 180
  },
  { 
    title: '操作', 
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

// API调用函数
async function loadPosts() {
  try {
    loading.value = true
    const response = await CommunityAPI.getPosts({
      page: pagination.current,
      size: pagination.pageSize,
      category: filterStatus.value || undefined
    })
    
    // 后端返回的是 data 字段，不是 posts
    posts.value = response.data.data || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('加载帖子失败:', error)
    message.error('加载帖子失败')
  } finally {
    loading.value = false
  }
}

// 过滤后的帖子列表（现在主要在后端处理）
const filteredPosts = computed(() => {
  return posts.value
})

// 获取状态颜色
function getStatusColor(status: string) {
  switch (status) {
    case 'published': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'error'
    case 'hidden': return 'default'
    default: return 'default'
  }
}

// 获取状态图标
function getStatusIcon(status: string) {
  switch (status) {
    case 'published': return 'fas fa-check-circle'
    case 'pending': return 'fas fa-clock'
    case 'rejected': return 'fas fa-times-circle'
    case 'hidden': return 'fas fa-eye-slash'
    default: return 'fas fa-question-circle'
  }
}

// 获取状态文本
function getStatusText(status: string) {
  switch (status) {
    case 'published': return '已发布'
    case 'pending': return '待审核'
    case 'rejected': return '已拒绝'
    case 'hidden': return '已隐藏'
    default: return '未知'
  }
}

// 处理筛选变化
function handleFilterChange() {
  pagination.current = 1
  loadPosts()
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
  loadPosts()
}

// 处理表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadPosts()
}

// 查看帖子详情
async function viewPost(post: Post) {
  try {
    const response = await CommunityAPI.getPostDetail(post.id)
    selectedPost.value = response.data.post
    drawerVisible.value = true
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    message.error('获取帖子详情失败')
  }
}

// 处理操作
function handleAction(action: string, post: any) {
  switch (action) {
    case 'approve':
      approvePost(post)
      break
    case 'reject':
      rejectPost(post)
      break
    case 'top':
      toggleTop(post)
      break
    case 'untop':
      toggleTop(post)
      break
    case 'hide':
      toggleVisibility(post)
      break
    case 'show':
      toggleVisibility(post)
      break
    case 'delete':
      deletePost(post)
      break
  }
}

// 审核通过
async function approvePost(post: Post) {
  Modal.confirm({
    title: '确认审核通过',
    content: `确定要审核通过帖子"${post.title}"吗？`,
    async onOk() {
      try {
        await CommunityAPI.approvePost(post.id)
        message.success('帖子审核通过')
        loadPosts() // 重新加载列表
      } catch (error) {
        console.error('审核失败:', error)
        message.error('审核失败')
      }
    }
  })
}

// 拒绝发布
async function rejectPost(post: Post) {
  Modal.confirm({
    title: '确认拒绝发布',
    content: `确定要拒绝发布帖子"${post.title}"吗？`,
    async onOk() {
      try {
        await CommunityAPI.rejectPost(post.id)
        message.success('已拒绝发布该帖子')
        loadPosts() // 重新加载列表
      } catch (error) {
        console.error('拒绝失败:', error)
        message.error('拒绝失败')
      }
    }
  })
}

// 切换置顶状态
async function toggleTop(post: Post) {
  const newTopState = !post.isTop
  try {
    await CommunityAPI.toggleTopPost(post.id, newTopState)
    message.success(newTopState ? '帖子已置顶' : '已取消置顶')
    loadPosts() // 重新加载列表
  } catch (error) {
    console.error('置顶操作失败:', error)
    message.error('操作失败')
  }
}

// 切换显示/隐藏状态
async function toggleVisibility(post: Post) {
  const willBeHidden = post.status !== 'hidden'
  try {
    await CommunityAPI.togglePostVisibility(post.id, willBeHidden)
    message.success(willBeHidden ? '帖子已隐藏' : '帖子已显示')
    loadPosts() // 重新加载列表
  } catch (error) {
    console.error('切换可见性失败:', error)
    message.error('操作失败')
  }
}

// 删除帖子
async function deletePost(post: Post) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除帖子"${post.title}"吗？此操作不可恢复！`,
    okType: 'danger',
    async onOk() {
      try {
        await CommunityAPI.deletePostByAdmin(post.id)
        message.success('帖子已删除')
        drawerVisible.value = false
        loadPosts() // 重新加载列表
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
      }
    }
  })
}

// 预览图片
function previewImage(image: string) {
  // 这里可以实现图片预览功能
  message.info('图片预览功能')
}

// 组件挂载
onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.page {
  width: 100%;
}

.page-title {
  margin-bottom: 24px;
  color: #262626;
  font-size: 24px;
  font-weight: 600;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

/* 帖子标题 */
.post-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
}

.title-link:hover {
  text-decoration: underline;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-category {
  font-size: 12px;
  color: #666;
}

/* 作者信息 */
.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-weight: 500;
}

/* 状态标签 */
.status-tag {
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-tag i {
  font-size: 10px;
}

/* 帖子统计 */
.post-stats {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #666;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-stats i {
  width: 12px;
  font-size: 10px;
}

/* 抽屉样式 */
.post-drawer :deep(.ant-drawer-body) {
  padding: 24px;
}

.post-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-header {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
}

.post-header h2 {
  margin: 0 0 12px 0;
  color: #262626;
}

.post-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-weight: 500;
  color: #262626;
}

.post-time {
  font-size: 12px;
  color: #666;
}

.post-content {
  line-height: 1.6;
  color: #262626;
}

.post-images h4 {
  margin-bottom: 12px;
  color: #262626;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.image-grid img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.image-grid img:hover {
  transform: scale(1.05);
}

.post-actions {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

/* 危险操作样式 */
:deep(.danger-item) {
  color: #ff4d4f !important;
}

:deep(.danger-item:hover) {
  background-color: #fff2f0 !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .post-drawer {
    width: 90% !important;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }
  
  .image-grid img {
    height: 80px;
  }
}
</style>
