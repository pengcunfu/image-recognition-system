<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select
          v-model:value="filterCategory"
          style="width: 150px;"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部分类</a-select-option>
          <a-select-option 
            v-for="category in serverCategories" 
            :key="category" 
            :value="category"
          >
            {{ category }}
          </a-select-option>
        </a-select>
        
        <a-select
          v-model:value="filterTag"
          style="width: 150px;"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部标签</a-select-option>
          <a-select-option 
            v-for="tag in serverTags" 
            :key="tag" 
            :value="tag"
          >
            {{ tag }}
          </a-select-option>
        </a-select>
        
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索知识条目"
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
          添加知识
        </a-button>
      </div>
    </a-card>
    
    <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '24px' }">
      <a-table 
        :columns="knowledgeColumns" 
        :data-source="filteredKnowledge"
        :pagination="pagination"
        :loading="tableLoading"
        row-key="id"
        @change="handleTableChange"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="knowledge-title">
              <div class="title-with-image" style="display: flex; align-items: center; gap: 12px;">
                <div style="width: 60px; height: 60px; flex-shrink: 0; border-radius: 6px; overflow: hidden; background: #f5f5f5; display: flex; align-items: center; justify-content: center;">
                  <img 
                    v-if="getFirstImage(record?.images)" 
                    :src="getFirstImage(record?.images)" 
                    :alt="record?.name || ''"
                    style="width: 100%; height: 100%; object-fit: cover;"
                  />
                  <i v-else class="fas fa-image" style="font-size: 24px; color: #d9d9d9;"></i>
                </div>
                <div class="title-content" style="flex: 1; min-width: 0;">
                  <a @click="viewKnowledge(record)" class="title-link" style="display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #1890ff; cursor: pointer; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    {{ record?.name || '' }}
                  </a>
                  <div class="knowledge-meta" style="display: flex; align-items: center; gap: 8px;">
                    <a-tag :color="getCategoryColor(record?.category || '')" size="small">
                      {{ record?.category || '未知分类' }}
                    </a-tag>
                    <span style="font-size: 12px; color: #8c8c8c;">难度: {{ record?.difficulty || 1 }}</span>
                  </div>
                </div>
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
          
          <template v-else-if="column.key === 'stats'">
            <div class="knowledge-stats" style="display: flex; gap: 12px; font-size: 13px; color: #595959;">
              <span><i class="fas fa-eye" style="margin-right: 4px;"></i>{{ record?.viewCount || 0 }}</span>
              <span><i class="fas fa-thumbs-up" style="margin-right: 4px;"></i>{{ record?.likeCount || 0 }}</span>
              <span><i class="fas fa-bookmark" style="margin-right: 4px;"></i>{{ record?.collectCount || 0 }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="record?.status === 1 ? 'success' : 'warning'"
              class="status-tag"
            >
              <i :class="record?.status === 1 ? 'fas fa-check-circle' : 'fas fa-clock'"></i>
              {{ record?.status === 1 ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="editKnowledge(record)">
                编辑
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item key="view">
                      查看详情
                    </a-menu-item>
                    <a-menu-item key="publish" v-if="record?.status !== 1">
                      发布
                    </a-menu-item>
                    <a-menu-item key="unpublish" v-if="record?.status === 1">
                      撤回
                    </a-menu-item>
                    <a-menu-item key="duplicate">
                      复制
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
    
    <!-- 添加/编辑知识模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑知识' : '添加知识'"
      width="80%"
      :maskClosable="false"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :bodyStyle="{ padding: '24px' }"
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
            <a-form-item label="分类" name="category">
              <a-auto-complete
                v-model:value="formData.category"
                :options="categoryOptions"
                placeholder="选择或输入分类"
                :filter-option="filterCategoryOption"
              />
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
            <div style="width: 104px; height: 104px; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 6px; overflow: hidden;">
              <img 
                v-if="getFirstImage(formData.images)" 
                :src="getFirstImage(formData.images)" 
                alt="封面" 
                style="width: 100%; height: 100%; object-fit: cover;"
              />
              <div v-else style="display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;">
                <i class="fas fa-plus" style="font-size: 24px; color: #8c8c8c;"></i>
                <div style="font-size: 14px; color: #8c8c8c;">上传图片</div>
              </div>
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
            v-model:value="formData.tagList"
            mode="tags"
            placeholder="选择或输入标签，按回车添加"
            style="width: 100%"
            :options="tagOptions"
            :max-tag-count="10"
          />
          <div style="margin-top: 8px; font-size: 12px; color: #8c8c8c;">
            可以从列表中选择已有标签，或输入新标签后按回车键添加
          </div>
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
      :width="600"
      placement="right"
      :bodyStyle="{ padding: '24px' }"
    >
      <div v-if="selectedKnowledge" class="knowledge-detail">
        <div class="knowledge-header" style="margin-bottom: 24px;">
          <div style="width: 100%; height: 300px; border-radius: 8px; overflow: hidden; background: #f5f5f5; display: flex; align-items: center; justify-content: center; margin-bottom: 16px;">
            <img 
              v-if="getFirstImage(selectedKnowledge.images)" 
              :src="getFirstImage(selectedKnowledge.images)" 
              :alt="selectedKnowledge.title"
              style="width: 100%; height: 100%; object-fit: cover;"
            />
            <i v-else class="fas fa-image" style="font-size: 64px; color: #d9d9d9;"></i>
          </div>
          <div class="knowledge-info">
            <h2>{{ selectedKnowledge.title || selectedKnowledge.name }}</h2>
            <div class="knowledge-meta" style="display: flex; gap: 12px; align-items: center; margin: 12px 0;">
              <a-tag :color="getCategoryColor(selectedKnowledge.category || '')">
                {{ selectedKnowledge.category }}
              </a-tag>
              <span class="update-time" style="font-size: 13px; color: #8c8c8c;">
                更新时间: {{ formatDateTime(selectedKnowledge.updatedAt) }}
              </span>
            </div>
            <p class="description" style="color: #595959; line-height: 1.6;">{{ selectedKnowledge.content }}</p>
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
              <a-statistic title="浏览量" :value="selectedKnowledge.viewCount || 0" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="点赞数" :value="selectedKnowledge.likeCount || 0" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="收藏数" :value="selectedKnowledge.collectCount || 0" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="评论数" :value="selectedKnowledge.commentCount || 0" />
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
import { AdminAPI } from '@/api/admin'
import FileAPI from '@/api/file'
import type { KnowledgeInfo } from '@/api/knowledge'

// 辅助函数：获取第一张图片URL
const getFirstImage = (images: string | null | undefined) => {
  if (!images) return ''
  try {
    const imageArray = JSON.parse(images)
    return imageArray[0] || ''
  } catch {
    return images || ''
  }
}

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
const modalVisible = ref(false)
const drawerVisible = ref(false)
const isEditing = ref(false)
const selectedKnowledge = ref<any | null>(null)
const filterCategory = ref('')
const filterTag = ref('')
const searchKeyword = ref('')
const formRef = ref()
const fileList = ref([])

// 知识库数据
const knowledgeItems = ref<KnowledgeInfo[]>([])

// 从服务端获取的分类和标签
const serverCategories = ref<string[]>([])
const serverTags = ref<string[]>([])

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
  category: '', // 修改为直接使用分类名称
  difficulty: 1,
  description: '',
  content: '',
  images: '',
  tagList: [] as string[], // 修改为标签数组
  status: 'draft'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入知识名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择或输入分类', trigger: 'blur' }
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

// 分类和标签的选项列表
const categoryOptions = computed(() => 
  serverCategories.value.map(cat => ({ value: cat, label: cat }))
)

const tagOptions = computed(() => 
  serverTags.value.map(tag => ({ value: tag, label: tag }))
)

// 分类过滤函数
const filterCategoryOption = (input: string, option: any) => {
  return option.value.toLowerCase().includes(input.toLowerCase())
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
    dataIndex: 'author', 
    key: 'author',
    width: 150
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
    dataIndex: 'updatedAt', 
    key: 'updatedAt',
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
async function loadKnowledgeItems() {
  try {
    loading.value = true
    const response = await AdminAPI.getKnowledge({
      page: pagination.current,
      size: pagination.pageSize,
      category: filterCategory.value || undefined,
      tag: filterTag.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    
    console.log('知识库数据响应:', response)
    console.log('items数据:', response.data)
    
    // 检查数据是否存在
    if (!response.data || response.data.length === 0) {
      console.warn('响应数据为空:', response)
      knowledgeItems.value = []
      pagination.total = 0
      return
    }
    
    // 直接使用后端返回的数据
    knowledgeItems.value = response.data
    
    pagination.total = response.total || 0
    pagination.current = response.page || 1
    
    console.log('转换后的知识库数据:', knowledgeItems.value)
  } catch (error) {
    console.error('加载知识库数据失败:', error)
    message.error('加载数据失败')
    knowledgeItems.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    // 从服务端获取分类列表
    serverCategories.value = await AdminAPI.getKnowledgeCategories()
  } catch (error) {
    console.error('加载分类数据失败:', error)
    message.error('加载分类失败')
  }
}

async function loadTags() {
  try {
    // 从服务端获取标签列表
    serverTags.value = await AdminAPI.getKnowledgeTags()
  } catch (error) {
    console.error('加载标签数据失败:', error)
    message.error('加载标签失败')
  }
}

// 辅助函数
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
function getCategoryColor(category: string | undefined | null) {
  if (!category) return 'default'
  
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

// 处理重置
function handleReset() {
  filterCategory.value = ''
  filterTag.value = ''
  searchKeyword.value = ''
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
function editKnowledge(knowledge: any) {
  isEditing.value = true
  
  // 处理标签：将逗号分隔的字符串转为数组
  const tagList = knowledge.tags 
    ? knowledge.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag)
    : []
  
  Object.assign(formData, {
    id: knowledge.id,
    name: knowledge.title || knowledge.name,
    category: knowledge.category || '',
    difficulty: knowledge.difficulty || 1,
    description: knowledge.description || '',
    content: knowledge.content || '',
    images: knowledge.images || '',
    tagList: tagList,
    status: knowledge.status || 'draft'
  })
  modalVisible.value = true
}

// 查看知识详情
async function viewKnowledge(knowledge: any) {
  try {
    // 直接使用当前知识信息
    selectedKnowledge.value = knowledge
    drawerVisible.value = true
  } catch (error) {
    console.error('获取知识详情失败:', error)
    message.error('获取详情失败')
  }
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
    content: `确定要发布知识"${knowledge.title || knowledge.name}"吗？`,
    async onOk() {
      try {
        await AdminAPI.updateKnowledge(knowledge.id, { 
          title: knowledge.title || knowledge.name,
          content: knowledge.content,
          category: knowledge.category
        })
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
function unpublishKnowledge(knowledge: any) {
  Modal.confirm({
    title: '确认撤回',
    content: `确定要撤回知识"${knowledge.title || knowledge.name}"吗？`,
    async onOk() {
      try {
        await AdminAPI.updateKnowledge(knowledge.id, {
          title: knowledge.title || knowledge.name,
          content: knowledge.content,
          category: knowledge.category
        })
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
async function duplicateKnowledge(knowledge: any) {
  try {
    // 创建副本数据
    const duplicateData = {
      category: knowledge.category || '未分类',
      title: `${knowledge.title || knowledge.name}（副本）`,
      content: knowledge.content || '',
      images: knowledge.images,
      tags: knowledge.tags
    }
    
    await AdminAPI.createKnowledge(duplicateData)
    message.success('知识复制成功')
    loadKnowledgeItems() // 重新加载数据
  } catch (error: any) {
    console.error('复制知识失败:', error)
    message.error(error.message || '复制失败')
  }
}

// 删除知识
function deleteKnowledge(knowledge: any) {
  Modal.confirm({ 
    title: '确认删除',
    content: `确定要删除知识"${knowledge.title || knowledge.name}"吗？此操作不可恢复！`,   
    okType: 'danger',
    async onOk() {
      try {
        await AdminAPI.deleteKnowledge(knowledge.id)
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
    category: '',
    difficulty: 1,
    description: '',
    content: '',
    images: '',
    tagList: [],
    status: 'draft'
  })
  fileList.value = []
}

// 处理提交
async function handleSubmit() {
  try {
    await formRef.value.validate()
    
    // 将标签数组转换为逗号分隔的字符串
    const tagsString = formData.tagList.join(',')
    
    const submitData = {
      category: formData.category,
      title: formData.name,
      content: formData.content,
      coverImage: formData.images ? getFirstImage(formData.images) : undefined,
      images: formData.images,
      tags: tagsString
    }
    
    if (isEditing.value && formData.id) {
      // 更新现有知识
      await AdminAPI.updateKnowledge(formData.id, submitData)
      message.success('知识更新成功')
    } else {
      // 添加新知识
      await AdminAPI.createKnowledge(submitData)
      message.success('知识添加成功')
    }
    
    modalVisible.value = false
    resetForm()
    loadKnowledgeItems() // 重新加载数据
    
    // 重新加载分类和标签（可能添加了新的）
    await loadCategories()
    await loadTags()
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
      const response = await FileAPI.uploadImage(info.file.originFileObj || info.file)
      
      // 关闭loading提示
      loadingMsg()
      
      // 上传成功，保存文件URL（转换为JSON数组格式）
      if (response) {
        // 数据库中images字段是JSON类型，需要传递JSON数组
        formData.images = JSON.stringify([response])
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
    message.info('点赞功能仅供展示')
    // 更新本地数据
    if (selectedKnowledge.value) {
      selectedKnowledge.value.likeCount = (selectedKnowledge.value.likeCount || 0) + 1
    }
  } catch (error) {
    console.error('点赞失败:', error)
    message.error('点赞失败')
  }
}

// 处理收藏
async function handleCollect(knowledge: any) {
  try {
    message.info('收藏功能仅供展示')
    // 更新本地数据
    if (selectedKnowledge.value) {
      selectedKnowledge.value.collectCount = (selectedKnowledge.value.collectCount || 0) + 1
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
  try {
    // 先加载分类和标签，再加载知识条目
    await loadCategories()
    await loadTags()
    await loadKnowledgeItems()
  } catch (error) {
    console.error('初始化失败:', error)
  }
})
</script>

