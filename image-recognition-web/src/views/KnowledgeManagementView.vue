<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">知识库管理</h1>
      <a-space>
        <a-select
          v-model:value="filterCategory"
          placeholder="筛选分类"
          style="width: 120px"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部分类</a-select-option>
          <a-select-option value="动物">动物</a-select-option>
          <a-select-option value="植物">植物</a-select-option>
          <a-select-option value="食物">食物</a-select-option>
          <a-select-option value="物品">物品</a-select-option>
          <a-select-option value="场景">场景</a-select-option>
        </a-select>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索知识条目"
          style="width: 300px"
          @search="handleSearch"
        />
        <a-button type="primary" @click="showAddModal">
          <i class="fas fa-plus"></i>
          添加知识
        </a-button>
      </a-space>
    </div>
    
    <a-card class="table-card">
      <a-table 
        :columns="knowledgeColumns" 
        :data-source="filteredKnowledge"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="knowledge-title">
              <div class="title-with-image">
                <img 
                  v-if="record.image" 
                  :src="record.image" 
                  :alt="record.title"
                  class="knowledge-thumbnail"
                />
                <div class="title-content">
                  <a @click="viewKnowledge(record)" class="title-link">
                    {{ record.title }}
                  </a>
                  <div class="knowledge-meta">
                    <a-tag :color="getCategoryColor(record.category)" size="small">
                      {{ record.category }}
                    </a-tag>
                    <span class="difficulty">难度: {{ record.difficulty }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'stats'">
            <div class="knowledge-stats">
              <span><i class="fas fa-eye"></i> {{ record.views }}</span>
              <span><i class="fas fa-thumbs-up"></i> {{ record.likes }}</span>
              <span><i class="fas fa-bookmark"></i> {{ record.collections }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="record.status === 'published' ? 'success' : 'warning'"
              class="status-tag"
            >
              <i :class="record.status === 'published' ? 'fas fa-check-circle' : 'fas fa-clock'"></i>
              {{ record.status === 'published' ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="editKnowledge(record)">
                编辑
              </a-button>
              <a-dropdown>
                <a-button size="small">
                  更多 <i class="fas fa-chevron-down"></i>
                </a-button>
                <template #overlay>
                  <a-menu @click="(e) => handleAction(e.key, record)">
                    <a-menu-item key="view">
                      <i class="fas fa-eye"></i> 查看详情
                    </a-menu-item>
                    <a-menu-item key="publish" v-if="record.status === 'draft'">
                      <i class="fas fa-upload"></i> 发布
                    </a-menu-item>
                    <a-menu-item key="unpublish" v-if="record.status === 'published'">
                      <i class="fas fa-download"></i> 撤回
                    </a-menu-item>
                    <a-menu-item key="duplicate">
                      <i class="fas fa-copy"></i> 复制
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
    
    <!-- 添加/编辑知识模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑知识' : '添加知识'"
      width="80%"
      :maskClosable="false"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="标题" name="title">
              <a-input v-model:value="formData.title" placeholder="请输入知识标题" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="分类" name="category">
              <a-select v-model:value="formData.category" placeholder="选择分类">
                <a-select-option value="动物">动物</a-select-option>
                <a-select-option value="植物">植物</a-select-option>
                <a-select-option value="食物">食物</a-select-option>
                <a-select-option value="物品">物品</a-select-option>
                <a-select-option value="场景">场景</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="难度" name="difficulty">
              <a-select v-model:value="formData.difficulty" placeholder="选择难度">
                <a-select-option value="初级">初级</a-select-option>
                <a-select-option value="中级">中级</a-select-option>
                <a-select-option value="高级">高级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="描述" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            placeholder="请输入知识描述"
            :rows="3"
          />
        </a-form-item>
        
        <a-form-item label="封面图片" name="image">
          <a-upload
            v-model:file-list="fileList"
            name="image"
            list-type="picture-card"
            class="image-uploader"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleImageChange"
          >
            <div v-if="formData.image" class="image-preview">
              <img :src="formData.image" alt="封面" />
            </div>
            <div v-else class="upload-placeholder">
              <i class="fas fa-plus"></i>
              <div>上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        
        <a-form-item label="详细内容" name="content">
          <a-textarea 
            v-model:value="formData.content" 
            placeholder="请输入详细内容，支持Markdown格式"
            :rows="10"
          />
        </a-form-item>
        
        <a-form-item label="标签">
          <a-select
            v-model:value="formData.tags"
            mode="tags"
            placeholder="输入标签，按回车添加"
            style="width: 100%"
          />
        </a-form-item>
        
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio value="draft">保存为草稿</a-radio>
            <a-radio value="published">立即发布</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 知识详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="知识详情"
      width="60%"
      class="knowledge-drawer"
    >
      <div v-if="selectedKnowledge" class="knowledge-detail">
        <div class="knowledge-header">
          <img 
            v-if="selectedKnowledge.image" 
            :src="selectedKnowledge.image" 
            :alt="selectedKnowledge.title"
            class="knowledge-cover"
          />
          <div class="knowledge-info">
            <h2>{{ selectedKnowledge.title }}</h2>
            <div class="knowledge-meta">
              <a-tag :color="getCategoryColor(selectedKnowledge.category)">
                {{ selectedKnowledge.category }}
              </a-tag>
              <span class="difficulty">难度: {{ selectedKnowledge.difficulty }}</span>
              <span class="update-time">更新时间: {{ selectedKnowledge.updateTime }}</span>
            </div>
            <p class="description">{{ selectedKnowledge.description }}</p>
          </div>
        </div>
        
        <div class="knowledge-content">
          <h3>详细内容</h3>
          <div class="content-text" v-html="selectedKnowledge.content"></div>
        </div>
        
        <div class="knowledge-tags" v-if="selectedKnowledge.tags && selectedKnowledge.tags.length > 0">
          <h4>相关标签</h4>
          <a-space wrap>
            <a-tag v-for="tag in selectedKnowledge.tags" :key="tag" color="blue">
              {{ tag }}
            </a-tag>
          </a-space>
        </div>
        
        <div class="knowledge-stats-detail">
          <h4>统计信息</h4>
          <a-row :gutter="16">
            <a-col :span="6">
              <a-statistic title="浏览量" :value="selectedKnowledge.views" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="点赞数" :value="selectedKnowledge.likes" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="收藏数" :value="selectedKnowledge.collections" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="分享数" :value="selectedKnowledge.shares || 0" />
            </a-col>
          </a-row>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'

// 响应式数据
const loading = ref(false)
const modalVisible = ref(false)
const drawerVisible = ref(false)
const isEditing = ref(false)
const selectedKnowledge = ref<any>(null)
const filterCategory = ref('')
const searchKeyword = ref('')
const formRef = ref()
const fileList = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// 表单数据
const formData = reactive({
  id: null,
  title: '',
  category: '',
  difficulty: '',
  description: '',
  content: '',
  image: '',
  tags: [],
  status: 'draft'
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入知识标题', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入知识描述', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入详细内容', trigger: 'blur' }
  ]
}

// 表格列定义
const knowledgeColumns = [
  { 
    title: '知识内容', 
    dataIndex: 'title', 
    key: 'title',
    width: 400
  },
  { 
    title: '作者', 
    dataIndex: 'author', 
    key: 'author',
    width: 120
  },
  { 
    title: '统计', 
    key: 'stats',
    width: 150
  },
  { 
    title: '状态', 
    dataIndex: 'status', 
    key: 'status',
    width: 100
  },
  { 
    title: '更新时间', 
    dataIndex: 'updateTime', 
    key: 'updateTime',
    width: 180
  },
  { 
    title: '操作', 
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

// 模拟知识库数据
const knowledgeItems = ref([
  {
    id: 1,
    title: '猫科动物识别指南',
    category: '动物',
    difficulty: '中级',
    description: '详细介绍各种猫科动物的特征和识别方法',
    content: '猫科动物是一个大家族，包括狮子、老虎、豹子、猎豹等...',
    image: '/images/cat-family.jpg',
    author: '动物专家',
    tags: ['猫科', '动物识别', '特征'],
    status: 'published',
    views: 2340,
    likes: 156,
    collections: 89,
    shares: 23,
    createTime: '2025-01-10 14:30',
    updateTime: '2025-01-15 10:20'
  },
  {
    id: 2,
    title: '常见花卉识别手册',
    category: '植物',
    difficulty: '初级',
    description: '帮助初学者识别常见的花卉植物',
    content: '本手册收录了50种常见花卉的识别要点...',
    image: '/images/flowers.jpg',
    author: '植物学家',
    tags: ['花卉', '植物', '识别'],
    status: 'published',
    views: 1890,
    likes: 234,
    collections: 167,
    shares: 45,
    createTime: '2025-01-08 09:15',
    updateTime: '2025-01-14 16:45'
  },
  {
    id: 3,
    title: '中式菜品识别大全',
    category: '食物',
    difficulty: '高级',
    description: '全面介绍中式菜品的特点和识别方法',
    content: '中式菜品种类繁多，本文将从色香味形等方面...',
    image: '/images/chinese-food.jpg',
    author: '美食专家',
    tags: ['中式菜品', '美食', '文化'],
    status: 'draft',
    views: 0,
    likes: 0,
    collections: 0,
    shares: 0,
    createTime: '2025-01-12 11:30',
    updateTime: '2025-01-15 14:20'
  },
  {
    id: 4,
    title: '建筑风格识别指南',
    category: '场景',
    difficulty: '高级',
    description: '介绍各种建筑风格的特点和历史背景',
    content: '建筑风格反映了不同时代的文化特征...',
    image: '/images/architecture.jpg',
    author: '建筑师',
    tags: ['建筑', '风格', '历史'],
    status: 'published',
    views: 1456,
    likes: 98,
    collections: 76,
    shares: 12,
    createTime: '2025-01-05 16:20',
    updateTime: '2025-01-13 09:30'
  }
])

// 过滤后的知识库列表
const filteredKnowledge = computed(() => {
  let result = knowledgeItems.value

  // 分类筛选
  if (filterCategory.value) {
    result = result.filter(item => item.category === filterCategory.value)
  }

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.title.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword) ||
      item.author.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 获取分类颜色
function getCategoryColor(category: string) {
  switch (category) {
    case '动物': return 'orange'
    case '植物': return 'green'
    case '食物': return 'red'
    case '物品': return 'blue'
    case '场景': return 'purple'
    default: return 'default'
  }
}

// 处理筛选变化
function handleFilterChange() {
  pagination.current = 1
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
}

// 处理表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
}

// 显示添加模态框
function showAddModal() {
  isEditing.value = false
  resetForm()
  modalVisible.value = true
}

// 编辑知识
function editKnowledge(knowledge: any) {
  isEditing.value = true
  Object.assign(formData, knowledge)
  modalVisible.value = true
}

// 查看知识详情
function viewKnowledge(knowledge: any) {
  selectedKnowledge.value = knowledge
  drawerVisible.value = true
}

// 处理操作
function handleAction(action: string, knowledge: any) {
  switch (action) {
    case 'view':
      viewKnowledge(knowledge)
      break
    case 'publish':
      publishKnowledge(knowledge)
      break
    case 'unpublish':
      unpublishKnowledge(knowledge)
      break
    case 'duplicate':
      duplicateKnowledge(knowledge)
      break
    case 'delete':
      deleteKnowledge(knowledge)
      break
  }
}

// 发布知识
function publishKnowledge(knowledge: any) {
  Modal.confirm({
    title: '确认发布',
    content: `确定要发布知识"${knowledge.title}"吗？`,
    onOk() {
      knowledge.status = 'published'
      message.success('知识已发布')
    }
  })
}

// 撤回知识
function unpublishKnowledge(knowledge: any) {
  Modal.confirm({
    title: '确认撤回',
    content: `确定要撤回知识"${knowledge.title}"吗？`,
    onOk() {
      knowledge.status = 'draft'
      message.success('知识已撤回')
    }
  })
}

// 复制知识
function duplicateKnowledge(knowledge: any) {
  const newKnowledge = {
    ...knowledge,
    id: Date.now(),
    title: knowledge.title + ' (副本)',
    status: 'draft',
    views: 0,
    likes: 0,
    collections: 0,
    shares: 0,
    createTime: new Date().toLocaleString(),
    updateTime: new Date().toLocaleString()
  }
  knowledgeItems.value.unshift(newKnowledge)
  message.success('知识已复制')
}

// 删除知识
function deleteKnowledge(knowledge: any) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除知识"${knowledge.title}"吗？此操作不可恢复！`,
    okType: 'danger',
    onOk() {
      const index = knowledgeItems.value.findIndex(item => item.id === knowledge.id)
      if (index > -1) {
        knowledgeItems.value.splice(index, 1)
        message.success('知识已删除')
      }
    }
  })
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    id: null,
    title: '',
    category: '',
    difficulty: '',
    description: '',
    content: '',
    image: '',
    tags: [],
    status: 'draft'
  })
  fileList.value = []
}

// 处理提交
function handleSubmit() {
  formRef.value.validate().then(() => {
    if (isEditing.value) {
      // 更新现有知识
      const index = knowledgeItems.value.findIndex(item => item.id === formData.id)
      if (index > -1) {
        Object.assign(knowledgeItems.value[index], {
          ...formData,
          updateTime: new Date().toLocaleString()
        })
        message.success('知识更新成功')
      }
    } else {
      // 添加新知识
      const newKnowledge = {
        ...formData,
        id: Date.now(),
        author: '管理员',
        views: 0,
        likes: 0,
        collections: 0,
        shares: 0,
        createTime: new Date().toLocaleString(),
        updateTime: new Date().toLocaleString()
      }
      knowledgeItems.value.unshift(newKnowledge)
      message.success('知识添加成功')
    }
    
    modalVisible.value = false
    resetForm()
  }).catch(() => {
    message.error('请完善表单信息')
  })
}

// 处理取消
function handleCancel() {
  modalVisible.value = false
  resetForm()
}

// 图片上传前处理
function beforeUpload(file: any) {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return false // 阻止自动上传
}

// 处理图片变化
function handleImageChange(info: any) {
  if (info.file) {
    // 这里应该上传到服务器，现在只是模拟
    const reader = new FileReader()
    reader.onload = (e) => {
      formData.image = e.target?.result as string
    }
    reader.readAsDataURL(info.file)
  }
}

// 组件挂载
onMounted(() => {
  pagination.total = knowledgeItems.value.length
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

/* 知识标题 */
.knowledge-title {
  display: flex;
  flex-direction: column;
}

.title-with-image {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.knowledge-thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.title-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
  font-size: 16px;
}

.title-link:hover {
  text-decoration: underline;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.difficulty {
  font-size: 12px;
  color: #666;
}

/* 知识统计 */
.knowledge-stats {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.knowledge-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.knowledge-stats i {
  width: 12px;
  font-size: 10px;
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

/* 模态框样式 */
.image-uploader {
  display: flex;
  justify-content: center;
}

.image-uploader :deep(.ant-upload) {
  width: 120px;
  height: 120px;
}

.image-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.upload-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

/* 抽屉样式 */
.knowledge-drawer :deep(.ant-drawer-body) {
  padding: 24px;
}

.knowledge-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.knowledge-header {
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

.knowledge-cover {
  width: 200px;
  height: 150px;
  object-fit: cover;
  border-radius: 12px;
  flex-shrink: 0;
}

.knowledge-info {
  flex: 1;
}

.knowledge-info h2 {
  margin: 0 0 12px 0;
  color: #262626;
  font-size: 24px;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.knowledge-meta .difficulty,
.knowledge-meta .update-time {
  font-size: 14px;
  color: #666;
}

.description {
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

.knowledge-content h3,
.knowledge-tags h4,
.knowledge-stats-detail h4 {
  color: #262626;
  margin-bottom: 16px;
}

.content-text {
  line-height: 1.8;
  color: #262626;
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  white-space: pre-wrap;
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
  
  .knowledge-drawer {
    width: 90% !important;
  }
  
  .knowledge-header {
    flex-direction: column;
  }
  
  .knowledge-cover {
    width: 100%;
    height: 200px;
  }
  
  .title-with-image {
    flex-direction: column;
  }
  
  .knowledge-thumbnail {
    width: 100%;
    height: 120px;
  }
}
</style>
