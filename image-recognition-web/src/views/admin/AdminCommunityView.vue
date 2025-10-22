<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select
          v-model:value="filterStatus"
          placeholder="筛选状态"
          style="width: 150px;"
          @change="handleFilterChange"
        >
          <a-select-option :value="undefined">全部</a-select-option>
          <a-select-option :value="PostStatus.PUBLISHED">已发布</a-select-option>
          <a-select-option :value="PostStatus.PENDING">待审核</a-select-option>
          <a-select-option :value="PostStatus.REJECTED">已拒绝</a-select-option>
          <a-select-option :value="PostStatus.HIDDEN">已隐藏</a-select-option>
          <a-select-option :value="PostStatus.DRAFT">草稿</a-select-option>
        </a-select>
        
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索帖子标题或作者"
          style="width: 300px;"
          @pressEnter="handleSearch"
        />
        
        <a-button type="primary" @click="handleSearch">
          <template #icon>
            <i class="fas fa-search"></i>
          </template>
          搜索
        </a-button>
        
        <a-button @click="handleReset">
          <template #icon>
            <i class="fas fa-redo"></i>
          </template>
          重置
        </a-button>
        
        <div style="flex: 1;"></div>
        
        <a-button type="primary" @click="showAddModal">
          <template #icon>
            <i class="fas fa-plus"></i>
          </template>
          添加帖子
        </a-button>
      </div>
    </a-card>
    
    <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '24px' }">
      <a-table 
        :columns="postColumns" 
        :data-source="filteredPosts"
        :pagination="pagination"
        :loading="tableLoading"
        row-key="id"
        @change="handleTableChange"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="post-title">
              <a @click="viewPost(record)" class="title-link">
                {{ record.title }}
              </a>
              <div class="post-meta">
                <a-tag v-if="record.isTop" color="red" size="small">置顶</a-tag>
                <a-tag v-if="record.isFeatured" color="orange" size="small">精选</a-tag>
                <span class="post-category">{{ record.category }}</span>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'author'">
            <div class="author-info" style="display: flex; align-items: center; gap: 8px;">
              <a-avatar :src="FileAPI.getImageUrl(record.authorAvatar)" size="small">
                {{ record.authorName ? record.authorName.charAt(0) : 'U' }}
              </a-avatar>
              <span class="author-name">{{ record.authorName || '未知用户' }}</span>
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
            <div class="post-stats" style="display: flex; gap: 12px; font-size: 13px; color: #595959;">
              <span><i class="fas fa-eye" style="margin-right: 4px;"></i>{{ record.viewCount }}</span>
              <span><i class="fas fa-thumbs-up" style="margin-right: 4px;"></i>{{ record.likeCount }}</span>
              <span><i class="fas fa-comment" style="margin-right: 4px;"></i>{{ record.commentCount }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewPost(record)">
                查看
              </a-button>
              <a-button type="link" size="small" @click="editPost(record)">
                编辑
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item v-if="record.status === 2" key="approve">
                      审核通过
                    </a-menu-item>
                    <a-menu-item v-if="record.status === 2" key="reject">
                      拒绝发布
                    </a-menu-item>
                    <a-menu-item key="top" v-if="!record.isTop">
                      置顶
                    </a-menu-item>
                    <a-menu-item key="untop" v-if="record.isTop">
                      取消置顶
                    </a-menu-item>
                    <a-menu-item key="hide" v-if="record.status !== 4">
                      隐藏
                    </a-menu-item>
                    <a-menu-item key="show" v-if="record.status === 4">
                      显示
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger key="delete">
                      删除
                    </a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  更多 <i class="fas fa-chevron-down"></i>
                </a-button>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 添加/编辑帖子模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑帖子' : '添加帖子'"
      :width="800"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirmLoading="submitLoading"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="标题" name="title">
          <a-input
            v-model:value="formData.title"
            placeholder="请输入帖子标题"
            :maxlength="100"
            show-count
          />
        </a-form-item>

        <a-form-item label="分类" name="category">
          <a-select
            v-model:value="formData.category"
            placeholder="请选择分类"
          >
            <a-select-option value="技术交流">技术交流</a-select-option>
            <a-select-option value="问题求助">问题求助</a-select-option>
            <a-select-option value="经验分享">经验分享</a-select-option>
            <a-select-option value="资源下载">资源下载</a-select-option>
            <a-select-option value="公告通知">公告通知</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="内容" name="content">
          <a-textarea
            v-model:value="formData.content"
            placeholder="请输入帖子内容"
            :rows="10"
            :maxlength="10000"
            show-count
          />
        </a-form-item>

        <a-form-item label="标签" name="tags">
          <a-input
            v-model:value="formData.tags"
            placeholder="请输入标签，多个标签用逗号分隔"
          />
        </a-form-item>

        <a-form-item label="图片" name="images">
          <a-upload
            v-model:file-list="imageFileList"
            list-type="picture-card"
            :before-upload="beforeImageUpload"
            :customRequest="handleImageUpload"
            :max-count="9"
            accept="image/*"
          >
            <div v-if="imageFileList.length < 9">
              <i class="fas fa-plus"></i>
              <div style="margin-top: 8px">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-select
            v-model:value="formData.status"
            placeholder="请选择帖子状态"
          >
            <a-select-option :value="PostStatus.DRAFT">草稿</a-select-option>
            <a-select-option :value="PostStatus.PUBLISHED">已发布</a-select-option>
            <a-select-option :value="PostStatus.PENDING">待审核</a-select-option>
            <a-select-option :value="PostStatus.HIDDEN">已隐藏</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 帖子详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="帖子详情"
      :width="600"
      placement="right"
      :bodyStyle="{ padding: '24px' }"
    >
      <div v-if="selectedPost" class="post-detail">
        <div class="post-header">
          <h2 style="margin-bottom: 16px;">{{ selectedPost.title }}</h2>
          <div class="post-info" style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
            <a-avatar :src="FileAPI.getImageUrl(selectedPost.authorAvatar)">
              {{ selectedPost.authorName ? selectedPost.authorName.charAt(0) : 'U' }}
            </a-avatar>
            <div class="author-details" style="flex: 1;">
              <div style="font-weight: 500; color: #262626;">{{ selectedPost.authorName || '未知用户' }}</div>
              <div style="font-size: 12px; color: #8c8c8c; margin-top: 2px;">{{ formatDateTime(selectedPost.createdAt) }}</div>
            </div>
            <a-tag :color="getStatusColor(selectedPost.status)">
              {{ getStatusText(selectedPost.status) }}
            </a-tag>
          </div>
        </div>
        
        <div class="post-content" v-html="selectedPost.content"></div>
        
        <div class="post-images" v-if="selectedPost.images && selectedPost.images.length > 0" :style="{ marginTop: '24px' }">
          <h4 :style="{ marginBottom: '12px', fontSize: '15px', fontWeight: '500' }">图片附件</h4>
          <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '12px' }">
            <img 
              v-for="(image, index) in selectedPost.images" 
              :key="index"
              :src="FileAPI.getImageUrl(image)"
              :alt="`图片${index + 1}`"
              :style="{ 
                width: '100%', 
                height: 'auto', 
                borderRadius: '8px', 
                cursor: 'pointer',
                border: '1px solid #e8e8e8',
                transition: 'all 0.3s'
              }"
              @click="previewImage(image)"
            />
          </div>
        </div>
        
        <div class="post-actions" :style="{ marginTop: '24px', paddingTop: '24px', borderTop: '1px solid #f0f0f0' }">
          <a-space>
            <a-button 
              v-if="selectedPost.status === 2" 
              type="primary" 
              @click="approvePost(selectedPost)"
            >
              审核通过
            </a-button>
            <a-button 
              v-if="selectedPost.status === 2" 
              danger 
              @click="rejectPost(selectedPost)"
            >
              拒绝发布
            </a-button>
            <a-button @click="toggleTop(selectedPost)">
              {{ selectedPost.isTop ? '取消置顶' : '置顶' }}
            </a-button>
            <a-button @click="toggleVisibility(selectedPost)">
              {{ selectedPost.status === 4 ? '显示' : '隐藏' }}
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
import type { UploadProps } from 'ant-design-vue'
import { AdminAPI } from '@/api/admin'
import FileAPI from '@/api/file'
import type { PostInfo } from '@/api/community'

// 帖子状态枚举
enum PostStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  PENDING = 2,
  REJECTED = 3,
  HIDDEN = 4
}

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
const submitLoading = ref(false)
const drawerVisible = ref(false)
const modalVisible = ref(false)
const isEditing = ref(false)
const selectedPost = ref<PostInfo | null>(null)
const filterStatus = ref<number | undefined>(undefined)
const searchKeyword = ref('')
const formRef = ref()

// 表单数据
const formData = reactive({
  id: 0,
  title: '',
  content: '',
  category: '',
  tags: '',
  images: '',
  status: PostStatus.PUBLISHED
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应在 5 到 100 个字符之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, message: '内容长度至少 10 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 图片文件列表
const imageFileList = ref<any[]>([])

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
const posts = ref<PostInfo[]>([])

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
    dataIndex: 'createdAt', 
    key: 'createdAt',
    width: 180,
    customRender: ({ text }: { text: string }) => formatDateTime(text)
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
    // 使用管理员专用接口，可以看到所有状态的帖子
    const response = await AdminAPI.getPosts({
      page: pagination.current,
      size: pagination.pageSize,
      status: filterStatus.value,
      keyword: searchKeyword.value || undefined
    })
    
    // 后端返回的是 PageResponse<PostInfo>
    posts.value = response.data || []
    pagination.total = response.total || 0
    pagination.current = response.page || 1
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
function getStatusColor(status: number | string) {
  const statusNum = typeof status === 'string' ? parseInt(status) : status
  switch (statusNum) {
    case PostStatus.PUBLISHED: return 'success'
    case PostStatus.PENDING: return 'warning'
    case PostStatus.REJECTED: return 'error'
    case PostStatus.HIDDEN: return 'default'
    case PostStatus.DRAFT: return 'default'
    default: return 'default'
  }
}

// 获取状态图标
function getStatusIcon(status: number | string) {
  const statusNum = typeof status === 'string' ? parseInt(status) : status
  switch (statusNum) {
    case PostStatus.PUBLISHED: return 'fas fa-check-circle'
    case PostStatus.PENDING: return 'fas fa-clock'
    case PostStatus.REJECTED: return 'fas fa-times-circle'
    case PostStatus.HIDDEN: return 'fas fa-eye-slash'
    case PostStatus.DRAFT: return 'fas fa-file'
    default: return 'fas fa-question-circle'
  }
}

// 获取状态文本
function getStatusText(status: number | string) {
  const statusNum = typeof status === 'string' ? parseInt(status) : status
  switch (statusNum) {
    case PostStatus.PUBLISHED: return '已发布'
    case PostStatus.PENDING: return '待审核'
    case PostStatus.REJECTED: return '已拒绝'
    case PostStatus.HIDDEN: return '已隐藏'
    case PostStatus.DRAFT: return '草稿'
    default: return '未知'
  }
}

// 格式化日期时间
function formatDateTime(dateTime: string): string {
  if (!dateTime) return '-'
  try {
    return new Date(dateTime).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return dateTime
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

// 处理重置
function handleReset() {
  filterStatus.value = undefined
  searchKeyword.value = ''
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
async function viewPost(post: PostInfo) {
  try {
    loading.value = true
    // 调用API获取完整的帖子详情
    const detail = await AdminAPI.getPostDetail(post.id)
    
    // 后端已经返回解析好的数组,直接使用
    selectedPost.value = detail as any
    drawerVisible.value = true
  } catch (error: any) {
    console.error('获取帖子详情失败:', error)
    message.error(error.message || '获取帖子详情失败')
  } finally {
    loading.value = false
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
async function approvePost(post: PostInfo) {
  Modal.confirm({
    title: '确认审核通过',
    content: `确定要审核通过帖子"${post.title}"吗？`,
    async onOk() {
      try {
        await AdminAPI.updatePostStatus(post.id, PostStatus.PUBLISHED)
        message.success('帖子审核通过')
        drawerVisible.value = false
        loadPosts() // 重新加载列表
      } catch (error) {
        console.error('审核失败:', error)
        message.error('审核失败')
      }
    }
  })
}

// 拒绝发布
async function rejectPost(post: PostInfo) {
  Modal.confirm({
    title: '确认拒绝发布',
    content: `确定要拒绝发布帖子"${post.title}"吗？`,
    async onOk() {
      try {
        await AdminAPI.updatePostStatus(post.id, PostStatus.REJECTED)
        message.success('已拒绝发布该帖子')
        drawerVisible.value = false
        loadPosts() // 重新加载列表
      } catch (error) {
        console.error('拒绝失败:', error)
        message.error('拒绝失败')
      }
    }
  })
}

// 切换置顶状态
async function toggleTop(post: PostInfo) {
  const newTopState = post.isTop ? 0 : 1
  try {
    await AdminAPI.togglePostTop(post.id, newTopState)
    message.success(newTopState ? '帖子已置顶' : '已取消置顶')
    drawerVisible.value = false
    loadPosts() // 重新加载列表
  } catch (error) {
    console.error('置顶操作失败:', error)
    message.error('操作失败')
  }
}

// 切换显示/隐藏状态
async function toggleVisibility(post: PostInfo) {
  const newStatus = post.status === PostStatus.HIDDEN ? PostStatus.PUBLISHED : PostStatus.HIDDEN
  try {
    await AdminAPI.updatePostStatus(post.id, newStatus)
    message.success(newStatus === PostStatus.HIDDEN ? '帖子已隐藏' : '帖子已显示')
    drawerVisible.value = false
    loadPosts() // 重新加载列表
  } catch (error) {
    console.error('切换可见性失败:', error)
    message.error('操作失败')
  }
}

// 删除帖子
async function deletePost(post: PostInfo) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除帖子"${post.title}"吗？此操作不可恢复！`,
    okType: 'danger',
    async onOk() {
      try {
        await AdminAPI.deletePost(post.id)
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

// 显示添加帖子模态框
function showAddModal() {
  isEditing.value = false
  resetForm()
  modalVisible.value = true
}

// 编辑帖子
async function editPost(post: PostInfo) {
  try {
    loading.value = true
    isEditing.value = true
    
    // 调用API获取完整的帖子详情
    const detail = await AdminAPI.getPostDetail(post.id)
    
    // 填充表单数据
    formData.id = detail.id
    formData.title = detail.title
    formData.content = detail.content
    formData.category = detail.category
    formData.tags = detail.tags || ''
    formData.status = detail.status
    
    // 处理图片 - 后端已经返回解析好的数组,直接使用原始URL
    if (detail.images && Array.isArray(detail.images) && detail.images.length > 0) {
      imageFileList.value = detail.images.map((url: string, index: number) => ({
        uid: `-${index}`,
        name: `image-${index}.jpg`,
        status: 'done',
        url: FileAPI.getImageUrl(url),  // 在这里添加URL前缀
        response: url  // 保存原始URL用于提交
      }))
    } else {
      imageFileList.value = []
    }
    
    modalVisible.value = true
  } catch (error: any) {
    console.error('获取帖子详情失败:', error)
    message.error(error.message || '获取帖子详情失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
function resetForm() {
  formData.id = 0
  formData.title = ''
  formData.content = ''
  formData.category = ''
  formData.tags = ''
  formData.images = ''
  formData.status = PostStatus.PUBLISHED
  imageFileList.value = []
  formRef.value?.resetFields()
}

// 表单提交
async function handleSubmit() {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    // 收集图片URL - 转为数组
    const imageUrls = imageFileList.value
      .filter(file => file.url || file.response)
      .map(file => file.url || file.response)
    
    // 处理标签 - 将逗号分隔的字符串转为数组
    const tagsArray = formData.tags 
      ? formData.tags.split(',').map(tag => tag.trim()).filter(tag => tag)
      : []
    
    if (isEditing.value) {
      // 编辑帖子
      await AdminAPI.updatePost(formData.id, {
        title: formData.title,
        content: formData.content,
        category: formData.category,
        tags: tagsArray,
        images: imageUrls,
        status: formData.status
      })
      message.success('帖子更新成功')
    } else {
      // 创建帖子
      await AdminAPI.createPost({
        title: formData.title,
        content: formData.content,
        category: formData.category,
        tags: tagsArray,
        images: imageUrls,
        status: formData.status
      })
      message.success('帖子创建成功')
    }
    
    modalVisible.value = false
    resetForm()
    loadPosts()
  } catch (error: any) {
    console.error('提交失败:', error)
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消表单
function handleCancel() {
  modalVisible.value = false
  resetForm()
}

// 图片上传前验证
function beforeImageUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 自定义图片上传
async function handleImageUpload(options: any) {
  const { file, onSuccess, onError } = options
  
  try {
    const url = await FileAPI.uploadImage(file)
    onSuccess(url)
    message.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    onError(error)
    message.error('图片上传失败')
  }
}

// 组件挂载
onMounted(() => {
  loadPosts()
})
</script>

