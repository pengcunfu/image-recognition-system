<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">分类管理</h1>
      <a-space>
        <a-select
          v-model:value="filterStatus"
          placeholder="筛选状态"
          style="width: 120px"
          @change="handleFilterChange"
        >
          <a-select-option :value="undefined">全部</a-select-option>
          <a-select-option :value="CategoryStatus.ACTIVE">启用</a-select-option>
          <a-select-option :value="CategoryStatus.INACTIVE">禁用</a-select-option>
        </a-select>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索分类"
          style="width: 300px"
          @search="handleSearch"
        />
        <a-button type="primary" @click="showAddModal">
          <i class="fas fa-plus"></i>
          添加分类
        </a-button>
      </a-space>
    </div>
    
    <a-card class="table-card">
      <a-table 
        :columns="categoryColumns" 
        :data-source="filteredCategories"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="category-info">
              <div class="category-content">
                <img 
                  v-if="record.image" 
                  :src="getImageUrl(record.image)" 
                  :alt="record.name"
                  class="category-icon"
                />
                <div class="category-details">
                  <div class="category-name">{{ record.name }}</div>
                  <div class="category-key">键值: {{ record.key }}</div>
                  <div class="category-description" v-if="record.description">
                    {{ record.description }}
                  </div>
                </div>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'itemCount'">
            <a-tag color="blue">{{ record.itemCount || 0 }} 个条目</a-tag>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="record.status === CategoryStatus.ACTIVE ? 'success' : 'default'"
              class="status-tag"
            >
              <i :class="record.status === CategoryStatus.ACTIVE ? 'fas fa-check-circle' : 'fas fa-pause-circle'"></i>
              {{ record.status === CategoryStatus.ACTIVE ? '启用' : '禁用' }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="editCategory(record)">
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
                    <a-menu-item key="toggle" v-if="record.status === CategoryStatus.ACTIVE">
                      <i class="fas fa-pause"></i> 禁用
                    </a-menu-item>
                    <a-menu-item key="toggle" v-if="record.status !== CategoryStatus.ACTIVE">
                      <i class="fas fa-play"></i> 启用
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
    
    <!-- 添加/编辑分类模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑分类' : '添加分类'"
      width="600px"
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
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="分类名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入分类名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分类键值" name="key">
              <a-input 
                v-model:value="formData.key" 
                placeholder="请输入分类键值（英文）"
                :disabled="isEditing"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="分类描述" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            placeholder="请输入分类描述"
            :rows="3"
          />
        </a-form-item>
        
        <a-form-item label="分类图标">
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
              <img :src="getImageUrl(formData.image)" alt="分类图标" />
            </div>
            <div v-else class="upload-placeholder">
              <i class="fas fa-plus"></i>
              <div>上传图标</div>
            </div>
          </a-upload>
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="排序顺序" name="sortOrder">
              <a-input-number 
                v-model:value="formData.sortOrder" 
                :min="0"
                placeholder="排序顺序"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="选择状态">
                <a-select-option :value="CategoryStatus.ACTIVE">启用</a-select-option>
                <a-select-option :value="CategoryStatus.INACTIVE">禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
    
    <!-- 分类详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="分类详情"
      width="50%"
      class="category-drawer"
    >
      <div v-if="selectedCategory" class="category-detail">
        <div class="category-header">
          <img 
            v-if="selectedCategory.image" 
            :src="getImageUrl(selectedCategory.image)" 
            :alt="selectedCategory.name"
            class="category-cover"
          />
          <div class="category-info-detail">
            <h2>{{ selectedCategory.name }}</h2>
            <div class="category-meta">
              <a-tag :color="selectedCategory.status === CategoryStatus.ACTIVE ? 'success' : 'default'">
                {{ selectedCategory.status === CategoryStatus.ACTIVE ? '启用' : '禁用' }}
              </a-tag>
              <span class="category-key">键值: {{ selectedCategory.key }}</span>
              <span class="update-time">更新时间: {{ formatDateTime(selectedCategory.updateTime) }}</span>
            </div>
            <p class="description">{{ selectedCategory.description }}</p>
          </div>
        </div>
        
        <div class="category-stats">
          <h3>统计信息</h3>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-statistic title="知识条目数" :value="selectedCategory.itemCount || 0" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="排序顺序" :value="selectedCategory.sortOrder || 0" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="创建时间" :value="formatDateTime(selectedCategory.createTime)" />
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
import KnowledgeAPI from '@/api/knowledge'
import FileAPI from '@/api/file'
import type { KnowledgeCategory } from '@/api/types'
import { CategoryStatus } from '@/api/types'
import { getImageUrl } from '@/utils/image'

// 响应式数据
const loading = ref(false)
const modalVisible = ref(false)
const drawerVisible = ref(false)
const isEditing = ref(false)
const selectedCategory = ref<KnowledgeCategory | null>(null)
const searchKeyword = ref('')
const filterStatus = ref<number | undefined>(undefined)
const formRef = ref()
const fileList = ref([])

// 分类数据
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
  key: '',
  description: '',
  image: '',
  sortOrder: 0,
  status: CategoryStatus.ACTIVE
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '分类名称不能超过50个字符', trigger: 'blur' }
  ],
  key: [
    { required: true, message: '请输入分类键值', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '键值只能包含字母、数字、下划线和横线', trigger: 'blur' },
    { max: 30, message: '键值不能超过30个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 表格列定义
const categoryColumns = [
  { 
    title: '分类信息', 
    dataIndex: 'name', 
    key: 'name',
    width: 350
  },
  { 
    title: '条目数量', 
    key: 'itemCount',
    width: 120,
    align: 'center'
  },
  { 
    title: '状态', 
    dataIndex: 'status', 
    key: 'status',
    width: 100,
    align: 'center'
  },
  { 
    title: '排序', 
    dataIndex: 'sortOrder', 
    key: 'sortOrder',
    width: 80,
    align: 'center'
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
async function loadCategories() {
  try {
    loading.value = true
    const response = await KnowledgeAPI.getCategories(
      filterStatus.value,
      searchKeyword.value || undefined
    )
    categories.value = response.data
    pagination.total = response.data.length
  } catch (error) {
    console.error('加载分类数据失败:', error)
    message.error('加载分类数据失败')
  } finally {
    loading.value = false
  }
}

// 辅助函数
function formatDateTime(dateTime: string): string {
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 使用categories作为表格数据源（筛选在后端完成）
const filteredCategories = computed(() => categories.value)

// 处理筛选变化
function handleFilterChange() {
  pagination.current = 1
  loadCategories()
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
  loadCategories()
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

// 编辑分类
function editCategory(category: KnowledgeCategory) {
  isEditing.value = true
  Object.assign(formData, {
    id: category.id,
    name: category.name,
    key: category.key,
    description: category.description,
    image: category.image,
    sortOrder: category.sortOrder,
    status: category.status
  })
  modalVisible.value = true
}

// 查看分类详情
function viewCategory(category: KnowledgeCategory) {
  selectedCategory.value = category
  drawerVisible.value = true
}

// 处理操作
function handleAction(action: string, category: KnowledgeCategory) {
  switch (action) {
    case 'view':
      viewCategory(category)
      break
    case 'toggle':
      toggleCategoryStatus(category)
      break
    case 'duplicate':
      duplicateCategory(category)
      break
    case 'delete':
      deleteCategory(category)
      break
  }
}

// 切换分类状态
async function toggleCategoryStatus(category: KnowledgeCategory) {
  const newStatus = category.status === CategoryStatus.ACTIVE ? CategoryStatus.INACTIVE : CategoryStatus.ACTIVE
  const actionText = newStatus === CategoryStatus.ACTIVE ? '启用' : '禁用'
  
  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}分类"${category.name}"吗？`,
    async onOk() {
      try {
        await KnowledgeAPI.updateCategory(category.id, {
          status: newStatus
        })
        message.success(`分类已${actionText}`)
        loadCategories() // 重新加载数据
      } catch (error) {
        console.error('状态更新失败:', error)
        message.error(`${actionText}失败`)
      }
    }
  })
}

// 复制分类
async function duplicateCategory(category: KnowledgeCategory) {
  try {
    await KnowledgeAPI.createCategory({
      name: category.name + ' (副本)',
      key: category.key + '_copy_' + Date.now(),
      description: category.description,
      image: category.image,
      sortOrder: (category.sortOrder || 0) + 1
    })
    message.success('分类复制成功')
    loadCategories() // 重新加载数据
  } catch (error) {
    console.error('分类复制失败:', error)
    message.error('分类复制失败')
  }
}

// 删除分类
function deleteCategory(category: KnowledgeCategory) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除分类"${category.name}"吗？此操作不可恢复！`,
    okType: 'danger',
    async onOk() {
      try {
        await KnowledgeAPI.deleteCategory(category.id)
        message.success('分类删除成功')
        loadCategories() // 重新加载数据
      } catch (error) {
        console.error('分类删除失败:', error)
        message.error('分类删除失败')
      }
    }
  })
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    id: null,
    name: '',
    key: '',
    description: '',
    image: '',
    sortOrder: 0,
    status: CategoryStatus.ACTIVE
  })
  fileList.value = []
}

// 处理提交
async function handleSubmit() {
  try {
    await formRef.value.validate()
    
    if (isEditing.value) {
      // 更新现有分类
      await KnowledgeAPI.updateCategory(formData.id!, {
        name: formData.name,
        description: formData.description,
        image: formData.image,
        sortOrder: formData.sortOrder,
        status: formData.status
      })
      message.success('分类更新成功')
    } else {
      // 添加新分类
      await KnowledgeAPI.createCategory({
        name: formData.name,
        key: formData.key,
        description: formData.description,
        image: formData.image,
        sortOrder: formData.sortOrder
      })
      message.success('分类添加成功')
    }
    
    modalVisible.value = false
    resetForm()
    loadCategories() // 重新加载数据
  } catch (error) {
    console.error('提交失败:', error)
    message.error(isEditing.value ? '分类更新失败' : '分类添加失败')
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
      
      // 上传成功，保存文件URL
      if (response.data.url) {
        formData.image = response.data.url
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

// 组件挂载
onMounted(async () => {
  await loadCategories()
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

/* 分类信息 */
.category-info {
  display: flex;
  flex-direction: column;
}

.category-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.category-icon {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.category-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-name {
  font-weight: 500;
  font-size: 16px;
  color: #262626;
}

.category-key {
  font-size: 12px;
  color: #666;
  font-family: 'Courier New', monospace;
}

.category-description {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
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
.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

/* 抽屉样式 */
.category-drawer :deep(.ant-drawer-body) {
  padding: 24px;
}

.category-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.category-header {
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

.category-cover {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  flex-shrink: 0;
}

.category-info-detail {
  flex: 1;
}

.category-info-detail h2 {
  margin: 0 0 12px 0;
  color: #262626;
  font-size: 24px;
}

.category-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.category-meta .category-key,
.category-meta .update-time {
  font-size: 14px;
  color: #666;
}

.description {
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

.category-stats h3 {
  color: #262626;
  margin-bottom: 16px;
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
  
  .category-drawer {
    width: 90% !important;
  }
  
  .category-header {
    flex-direction: column;
  }
  
  .category-cover {
    width: 100%;
    height: 200px;
  }
  
  .category-content {
    flex-direction: column;
  }
  
  .category-icon {
    width: 100%;
    height: 120px;
  }
}
</style>
