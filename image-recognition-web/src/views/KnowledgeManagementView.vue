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
          <template v-if="column.key === 'name'">
            <div class="knowledge-title">
              <div class="title-with-image">
                <img 
                  v-if="getFirstImage(record.images)" 
                  :src="getFirstImage(record.images)" 
                  :alt="record.name"
                  class="knowledge-thumbnail"
                />
                <div class="title-content">
                  <a @click="viewKnowledge(record)" class="title-link">
                    {{ record.name }}
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
                    <a-menu @click="(e: any) => handleAction(e.key, record)">
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
            <a-form-item label="名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入知识名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="分类" name="categoryId">
              <a-select v-model:value="formData.categoryId" placeholder="选择分类">
                <a-select-option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :value="category.id"
                >
                  {{ category.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="难度" name="difficulty">
              <a-select v-model:value="formData.difficulty" placeholder="选择难度">
                <a-select-option :value="1">初级</a-select-option>
                <a-select-option :value="2">初级+</a-select-option>
                <a-select-option :value="3">中级</a-select-option>
                <a-select-option :value="4">高级</a-select-option>
                <a-select-option :value="5">专家</a-select-option>
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
        
        <a-form-item label="封面图片" name="images">
          <a-upload
            v-model:file-list="fileList"
            name="images"
            list-type="picture-card"
            class="image-uploader"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleImageChange"
          >
            <div v-if="getFirstImage(formData.images)" class="image-preview">
              <img :src="getFirstImage(formData.images)" alt="封面" />
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
          <a-input
            v-model:value="formData.tags"
            placeholder="输入标签，用逗号分隔"
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
            v-if="getFirstImage(selectedKnowledge.images)" 
            :src="getFirstImage(selectedKnowledge.images)" 
            :alt="selectedKnowledge.title"
            class="knowledge-cover"
          />
          <div class="knowledge-info">
            <h2>{{ selectedKnowledge.title }}</h2>
            <div class="knowledge-meta">
              <a-tag :color="getCategoryColor(selectedKnowledge.category || '')">
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
        
        <div class="knowledge-tags" v-if="selectedKnowledge.tags">
          <h4>相关标签</h4>
          <a-space wrap>
            <a-tag v-for="tag in selectedKnowledge.tags.split(',')" :key="tag" color="blue">
              {{ tag.trim() }}
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
          
          <div class="knowledge-actions" style="margin-top: 20px;">
            <a-space>
              <a-button @click="handleLike(selectedKnowledge)" type="primary">
                <i class="fas fa-thumbs-up"></i> 点赞
              </a-button>
              <a-button @click="handleCollect(selectedKnowledge)">
                <i class="fas fa-bookmark"></i> 收藏
              </a-button>
              <a-button @click="handleShare(selectedKnowledge)">
                <i class="fas fa-share"></i> 分享
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import KnowledgeAPI from '@/api/knowledge'
import FileAPI from '@/api/file'
import type { KnowledgeItem, KnowledgeCategory } from '@/api/types'

// 响应式数据
const loading = ref(false)
const modalVisible = ref(false)
const drawerVisible = ref(false)
const isEditing = ref(false)
const selectedKnowledge = ref<KnowledgeItem | null>(null)
const filterCategory = ref('')
const searchKeyword = ref('')
const formRef = ref()
const fileList = ref([])

// 知识库数据
const knowledgeItems = ref<KnowledgeItem[]>([])
const categories = ref<KnowledgeCategory[]>([])

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
  id: null as number | null,
  name: '',
  categoryId: null as number | null,
  difficulty: 1,
  description: '',
  content: '',
  images: '',
  tags: '',
  status: 'draft'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入知识名称', trigger: 'blur' }
  ],
  categoryId: [
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
    dataIndex: 'name', 
    key: 'name',
    width: 400
  },
  { 
    title: '作者', 
    dataIndex: 'authorId', 
    key: 'authorId',
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

// API调用函数
async function loadKnowledgeItems() {
  try {
    loading.value = true
    const response = await KnowledgeAPI.getItems({
      category: filterCategory.value || undefined,
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined
    })
    
    // 转换数据格式以适配前端显示
    knowledgeItems.value = response.data.items.map(item => ({
      ...item,
      title: item.name,
      category: getCategoryNameById(item.categoryId),
      author: `用户${item.authorId}`,
      views: item.viewCount,
      likes: item.likeCount,
      collections: item.favoriteCount,
      shares: item.shareCount,
      updateTime: formatDateTime(item.updateTime)
    })) as any[]
    
    pagination.total = response.data.total
    pagination.current = response.data.current
  } catch (error) {
    console.error('加载知识库数据失败:', error)
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const response = await KnowledgeAPI.getCategories()
    categories.value = response.data
  } catch (error) {
    console.error('加载分类数据失败:', error)
    message.error('加载分类失败')
  }
}

// 辅助函数
function getCategoryNameById(categoryId: number): string {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : '未知分类'
}

// 从JSON数组中获取第一张图片URL
function getFirstImage(imagesJson: string | null | undefined): string {
  if (!imagesJson) return ''
  try {
    const images = JSON.parse(imagesJson)
    return Array.isArray(images) && images.length > 0 ? images[0] : ''
  } catch {
    // 如果不是JSON格式，直接返回原值（兼容旧数据）
    return imagesJson
  }
}

function getDifficultyText(difficulty: number): string {
  const difficultyMap: Record<number, string> = {
    1: '初级',
    2: '初级',
    3: '中级',
    4: '高级',
    5: '高级'
  }
  return difficultyMap[difficulty] || '未知'
}

function formatDateTime(dateTime: string): string {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 过滤后的知识库列表 - 现在直接使用API返回的数据
const filteredKnowledge = computed(() => {
  return knowledgeItems.value
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
  loadKnowledgeItems()
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
  loadKnowledgeItems()
}

// 处理表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadKnowledgeItems()
}

// 显示添加模态框
function showAddModal() {
  isEditing.value = false
  resetForm()
  modalVisible.value = true
}

// 编辑知识
function editKnowledge(knowledge: KnowledgeItem) {
  isEditing.value = true
  Object.assign(formData, {
    id: knowledge.id,
    name: knowledge.name,
    categoryId: knowledge.categoryId,
    difficulty: knowledge.difficulty,
    description: knowledge.description,
    content: knowledge.content,
    images: knowledge.images,
    tags: knowledge.tags,
    status: knowledge.status
  })
  modalVisible.value = true
}

// 查看知识详情
async function viewKnowledge(knowledge: KnowledgeItem) {
  try {
    const response = await KnowledgeAPI.getItemDetail(knowledge.id.toString())
    selectedKnowledge.value = {
      ...response.data,
      title: response.data.name,
      category: getCategoryNameById(response.data.categoryId),
      author: `用户${response.data.authorId}`,
      views: response.data.viewCount,
      likes: response.data.likeCount,
      collections: response.data.favoriteCount,
      shares: response.data.shareCount,
      difficulty: getDifficultyText(response.data.difficulty),
      updateTime: formatDateTime(response.data.updateTime)
    } as any
    drawerVisible.value = true
  } catch (error) {
    console.error('获取知识详情失败:', error)
    message.error('获取详情失败')
  }
}

// 处理操作
function handleAction(action: string, knowledge: KnowledgeItem) {
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
function publishKnowledge(knowledge: KnowledgeItem) {
  Modal.confirm({
    title: '确认发布',
    content: `确定要发布知识"${knowledge.name}"吗？`,
    async onOk() {
      try {
        await KnowledgeAPI.updateItem(knowledge.id, { status: 'PUBLISHED' })
        message.success('知识已发布')
        loadKnowledgeItems() // 重新加载数据
      } catch (error: any) {
        console.error('发布知识失败:', error)
        message.error(error.message || '发布失败')
      }
    }
  })
}

// 撤回知识
function unpublishKnowledge(knowledge: KnowledgeItem) {
  Modal.confirm({
    title: '确认撤回',
    content: `确定要撤回知识"${knowledge.name}"吗？`,
    async onOk() {
      try {
        await KnowledgeAPI.updateItem(knowledge.id, { status: 'DRAFT' })
        message.success('知识已撤回')
        loadKnowledgeItems() // 重新加载数据
      } catch (error: any) {
        console.error('撤回知识失败:', error)
        message.error(error.message || '撤回失败')
      }
    }
  })
}

// 复制知识
async function duplicateKnowledge(knowledge: KnowledgeItem) {
  try {
    // 创建副本数据
    const duplicateData = {
      name: `${knowledge.name}（副本）`,
      categoryId: knowledge.categoryId,
        description: knowledge.description || '',
      content: knowledge.content || '',
      images: knowledge.images,
      tags: knowledge.tags,
      difficulty: knowledge.difficulty,
      status: 'draft' // 副本默认为草稿状态
    }
    
    await KnowledgeAPI.createItem(duplicateData)
    message.success('知识复制成功')
    loadKnowledgeItems() // 重新加载数据
  } catch (error: any) {
    console.error('复制知识失败:', error)
    message.error(error.message || '复制失败')
  }
}

// 删除知识
function deleteKnowledge(knowledge: KnowledgeItem) {
  Modal.confirm({ 
    title: '确认删除',
    content: `确定要删除知识"${knowledge.name}"吗？此操作不可恢复！`,   
    okType: 'danger',
    async onOk() {
      try {
        await KnowledgeAPI.deleteItem(knowledge.id)
        message.success('知识删除成功')
        loadKnowledgeItems() // 重新加载数据
      } catch (error: any) {
        console.error('删除知识失败:', error)
        message.error(error.message || '删除失败')
      }
    } 
  })
}

// 重置表单     
function resetForm() {
  Object.assign(formData, {
    id: null,
    name: '',
    categoryId: null,
    difficulty: 1,
    description: '',
    content: '',
    images: '',
    tags: '',
    status: 'draft'
  })
  fileList.value = []
}

// 处理提交
async function handleSubmit() {
  try {
    await formRef.value.validate()
    
    const submitData = {
      name: formData.name,
      categoryId: formData.categoryId!,
      description: formData.description,
      content: formData.content,
      images: formData.images,
      tags: formData.tags,
      difficulty: formData.difficulty,
      status: formData.status
    }
    
    if (isEditing.value && formData.id) {
      // 更新现有知识
      await KnowledgeAPI.updateItem(formData.id, submitData)
      message.success('知识更新成功')
    } else {
      // 添加新知识
      await KnowledgeAPI.createItem(submitData)
      message.success('知识添加成功')
    }
    
    modalVisible.value = false
    resetForm()
    loadKnowledgeItems() // 重新加载数据
  } catch (error: any) {
    console.error('提交知识失败:', error)
    message.error(error.message || '提交失败，请检查表单信息')
  }
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
async function handleImageChange(info: any) {
  if (info.file && info.file.status !== 'removed') {
    try {
      // 显示上传中状态
      const loadingMsg = message.loading('图片上传中...', 0)
      
      // 调用上传接口
      const response = await FileAPI.uploadFile(info.file, {
        maxSize: 2 * 1024 * 1024, // 2MB
        allowedTypes: ['image/jpeg', 'image/png', '.jpg', '.jpeg', '.png']
      })
      
      // 关闭loading提示
      loadingMsg()
      
      // 上传成功，保存文件URL（转换为JSON数组格式）
      if (response.data.url) {
        // 数据库中images字段是JSON类型，需要传递JSON数组
        formData.images = JSON.stringify([response.data.url])
        message.success('图片上传成功')
      } else {
        message.error('上传失败：未返回文件URL')
      }
    } catch (error: any) {
      console.error('图片上传失败:', error)
      message.error(error.message || '图片上传失败')
    }
  }
}

// 处理点赞
async function handleLike(knowledge: any) {
  try {
    await KnowledgeAPI.likeItem(knowledge.id)
    message.success('点赞成功')
    // 更新本地数据
    if (selectedKnowledge.value) {
      selectedKnowledge.value.likes = (selectedKnowledge.value.likes || 0) + 1
    }
  } catch (error) {
    console.error('点赞失败:', error)
    message.error('点赞失败')
  }
}

// 处理收藏
async function handleCollect(knowledge: any) {
  try {
    await KnowledgeAPI.collectItem(knowledge.id)
    message.success('收藏成功')
    // 更新本地数据
    if (selectedKnowledge.value) {
      selectedKnowledge.value.collections = (selectedKnowledge.value.collections || 0) + 1
    }
  } catch (error) {
    console.error('收藏失败:', error)
    message.error('收藏功能暂未开放')
  }
}

// 处理分享
function handleShare(knowledge: any) {
  // 简单的分享功能
  if (navigator.share) {
    navigator.share({
      title: knowledge.title,
      text: knowledge.description,
      url: window.location.href
    }).catch(() => {
      message.error('分享失败')
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href).then(() => {
      message.success('链接已复制到剪贴板')
    }).catch(() => {
      message.error('分享失败')
    })
  }
}

// 组件挂载
onMounted(async () => {
  await loadCategories()
  await loadKnowledgeItems()
})
</script>

<style scoped lang="scss">
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

// 知识标题
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

  &:hover {
    text-decoration: underline;
  }
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 12px;

  .difficulty,
  .update-time {
    font-size: 14px;
    color: #666;
  }
}

.difficulty {
  font-size: 12px;
  color: #666;
}

// 知识统计
.knowledge-stats {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #666;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  i {
    width: 12px;
    font-size: 10px;
  }
}

// 状态标签
.status-tag {
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;

  i {
    font-size: 10px;
  }
}

// 模态框样式
.image-preview {
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  i {
    font-size: 24px;
    margin-bottom: 8px;
  }
}

// 抽屉样式
.knowledge-drawer {
  :deep(.ant-drawer-body) {
    padding: 24px;
  }
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

  h2 {
    margin: 0 0 12px 0;
    color: #262626;
    font-size: 24px;
  }
}

.description {
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

.knowledge-content,
.knowledge-tags,
.knowledge-stats-detail {
  h3, h4 {
    color: #262626;
    margin-bottom: 16px;
  }
}

.content-text {
  line-height: 1.8;
  color: #262626;
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  white-space: pre-wrap;
}

// 危险操作样式
:deep(.danger-item) {
  color: #ff4d4f !important;

  &:hover {
    background-color: #fff2f0 !important;
  }
}

// 响应式
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
