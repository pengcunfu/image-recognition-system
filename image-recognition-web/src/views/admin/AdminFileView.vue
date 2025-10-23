<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题和统计 -->
    <a-card :style="{ marginBottom: '24px' }">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="图片总数" :value="stats.imageCount" :style="{ textAlign: 'center' }">
            <template #prefix>
              <FileImageOutlined />
            </template>
          </a-statistic>
        </a-col>
        <a-col :span="8">
          <a-statistic title="总大小" :value="stats.totalSizeFormatted" :style="{ textAlign: 'center' }">
            <template #prefix>
              <DatabaseOutlined />
            </template>
          </a-statistic>
        </a-col>
        <a-col :span="8">
          <a-statistic title="已选文件" :value="selectedRowKeys.length" :style="{ textAlign: 'center' }">
            <template #prefix>
              <CheckCircleOutlined />
            </template>
          </a-statistic>
        </a-col>
      </a-row>
    </a-card>

    <!-- 主要内容 -->
    <a-card title="文件管理" :style="{ minHeight: '600px' }">
      <!-- 操作栏 -->
      <template #extra>
        <a-space>
          <a-button type="primary" @click="handleUpload">
            <UploadOutlined />
            上传图片
          </a-button>
          <a-button 
            danger 
            :disabled="selectedRowKeys.length === 0"
            @click="handleBatchDelete"
          >
            <DeleteOutlined />
            批量删除
          </a-button>
          <a-button @click="loadFiles">
            <ReloadOutlined />
            刷新
          </a-button>
        </a-space>
      </template>

      <!-- 搜索和筛选 -->
      <a-row :gutter="16" :style="{ marginBottom: '16px' }">
        <a-col :span="8">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索文件名..."
            @search="handleSearch"
            allowClear
          />
        </a-col>
        <a-col :span="8">
          <a-select
            v-model:value="filterType"
            placeholder="选择文件类型"
            :style="{ width: '100%' }"
            allowClear
            @change="handleSearch"
          >
            <a-select-option value="images">识别图片</a-select-option>
            <a-select-option value="avatars">用户头像</a-select-option>
          </a-select>
        </a-col>
      </a-row>

      <!-- 文件表格 -->
      <a-table
        :columns="columns"
        :data-source="files"
        :row-key="record => record.url"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{
          selectedRowKeys: selectedRowKeys,
          onChange: onSelectChange
        }"
        @change="handleTableChange"
      >
        <!-- 文件名 -->
        <template #name="{ record }">
          <a-tooltip :title="record.path">
            <FileImageOutlined :style="{ marginRight: '8px', color: '#1890ff' }" />
            {{ record.name }}
          </a-tooltip>
        </template>

        <!-- 预览 -->
        <template #preview="{ record }">
          <a-image
            :src="record.url"
            :alt="record.name"
            :width="60"
            :height="60"
            :style="{ objectFit: 'cover', borderRadius: '4px', cursor: 'pointer' }"
          />
        </template>

        <!-- 类型 -->
        <template #type="{ record }">
          <a-tag :color="getTypeColor(record.type)">
            {{ getTypeName(record.type) }}
          </a-tag>
        </template>

        <!-- 使用状态 -->
        <template #usage="{ record }">
          <div v-if="record.inUse">
            <a-tag color="orange">使用中</a-tag>
            <a-tooltip>
              <template #title>
                <div v-for="(item, index) in record.usedBy" :key="index">
                  {{ item }}
                </div>
              </template>
              <InfoCircleOutlined :style="{ marginLeft: '4px', cursor: 'pointer' }" />
            </a-tooltip>
          </div>
          <a-tag v-else color="default">未使用</a-tag>
        </template>

        <!-- 修改时间 -->
        <template #modifiedTime="{ record }">
          {{ formatDateTime(record.modifiedTime) }}
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <a-button type="link" size="small" @click="handlePreview(record)">
              <EyeOutlined />
              预览
            </a-button>
            <a-button type="link" size="small" @click="handleCopyUrl(record)">
              <CopyOutlined />
              复制链接
            </a-button>
            <a-popconfirm
              :title="record.inUse ? `文件正在使用中（${record.usedBy.join(', ')}），确定要删除吗？` : '确定要删除这个文件吗？'"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record)"
              :disabled="record.inUse"
            >
              <a-button type="link" danger size="small" :disabled="record.inUse">
                <DeleteOutlined />
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 上传对话框 -->
    <a-modal
      v-model:open="uploadModalVisible"
      title="上传图片"
      @ok="handleUploadOk"
      @cancel="handleUploadCancel"
      :confirmLoading="uploading"
    >
      <a-upload-dragger
        v-model:file-list="fileList"
        name="file"
        :multiple="false"
        :before-upload="beforeUpload"
        accept="image/*"
        :max-count="1"
      >
        <p class="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">
          支持 JPG、PNG、GIF、WebP 等图片格式，单个文件不超过 10MB
        </p>
      </a-upload-dragger>
    </a-modal>

    <!-- 预览对话框 -->
    <a-modal
      v-model:open="previewModalVisible"
      title="图片预览"
      :footer="null"
      width="800px"
    >
      <div v-if="previewFile" :style="{ textAlign: 'center' }">
        <a-image
          :src="previewFile.url"
          :alt="previewFile.name"
          :style="{ maxWidth: '100%' }"
        />
        <a-descriptions :column="1" :style="{ marginTop: '16px' }" bordered size="small">
          <a-descriptions-item label="文件名">{{ previewFile.name }}</a-descriptions-item>
          <a-descriptions-item label="路径">{{ previewFile.path }}</a-descriptions-item>
          <a-descriptions-item label="大小">{{ previewFile.sizeFormatted }}</a-descriptions-item>
          <a-descriptions-item label="类型">{{ getTypeName(previewFile.type) }}</a-descriptions-item>
          <a-descriptions-item label="修改时间">{{ formatDateTime(previewFile.modifiedTime) }}</a-descriptions-item>
          <a-descriptions-item label="使用状态">
            <span v-if="previewFile.inUse">
              <a-tag color="orange">使用中</a-tag>
              <div v-for="(item, index) in previewFile.usedBy" :key="index" :style="{ marginTop: '4px' }">
                {{ item }}
              </div>
            </span>
            <a-tag v-else color="default">未使用</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="URL">
            <a-typography-text copyable>{{ previewFile.url }}</a-typography-text>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  FileImageOutlined,
  DatabaseOutlined,
  CheckCircleOutlined,
  UploadOutlined,
  DeleteOutlined,
  ReloadOutlined,
  EyeOutlined,
  CopyOutlined,
  InfoCircleOutlined,
  InboxOutlined
} from '@ant-design/icons-vue'
import { AdminAPI, type FileInfo, type FileStats } from '@/api/admin'
import type { UploadProps } from 'ant-design-vue'
import dayjs from 'dayjs'

// 数据状态
const loading = ref(false)
const files = ref<FileInfo[]>([])
const selectedRowKeys = ref<string[]>([])
const searchKeyword = ref('')
const filterType = ref<string>()
const stats = ref<FileStats>({
  imageCount: 0,
  totalSize: 0,
  totalSizeFormatted: '0 B'
})

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 个文件`
})

// 上传相关
const uploadModalVisible = ref(false)
const uploading = ref(false)
const fileList = ref<any[]>([])

// 预览相关
const previewModalVisible = ref(false)
const previewFile = ref<FileInfo | null>(null)

// 表格列定义
const columns = [
  {
    title: '预览',
    key: 'preview',
    width: 100,
    slots: { customRender: 'preview' }
  },
  {
    title: '文件名',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    slots: { customRender: 'name' }
  },
  {
    title: '类型',
    key: 'type',
    dataIndex: 'type',
    width: 120,
    slots: { customRender: 'type' }
  },
  {
    title: '大小',
    key: 'size',
    dataIndex: 'sizeFormatted',
    width: 100
  },
  {
    title: '使用状态',
    key: 'usage',
    width: 150,
    slots: { customRender: 'usage' }
  },
  {
    title: '修改时间',
    key: 'modifiedTime',
    dataIndex: 'modifiedTime',
    width: 180,
    slots: { customRender: 'modifiedTime' }
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
    fixed: 'right',
    slots: { customRender: 'action' }
  }
]

// 加载文件列表
async function loadFiles() {
  loading.value = true
  try {
    const response = await AdminAPI.getFiles({
      page: pagination.current,
      size: pagination.pageSize,
      type: filterType.value,
      keyword: searchKeyword.value
    })
    files.value = response.records
    pagination.total = response.total
  } catch (error) {
    console.error('加载文件列表失败:', error)
    message.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
async function loadStats() {
  try {
    stats.value = await AdminAPI.getFileStats()
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 搜索
function handleSearch() {
  pagination.current = 1
  loadFiles()
}

// 表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadFiles()
}

// 选择变化
function onSelectChange(keys: string[]) {
  selectedRowKeys.value = keys
}

// 上传
function handleUpload() {
  uploadModalVisible.value = true
  fileList.value = []
}

// 上传前检查
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('图片大小不能超过 10MB!')
    return false
  }
  return false // 阻止自动上传
}

// 确认上传
async function handleUploadOk() {
  if (fileList.value.length === 0) {
    message.warning('请选择要上传的文件')
    return
  }

  uploading.value = true
  try {
    const file = fileList.value[0].originFileObj
    await AdminAPI.uploadFile(file)
    message.success('上传成功')
    uploadModalVisible.value = false
    fileList.value = []
    loadFiles()
    loadStats()
  } catch (error) {
    console.error('上传失败:', error)
    message.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 取消上传
function handleUploadCancel() {
  uploadModalVisible.value = false
  fileList.value = []
}

// 预览
function handlePreview(record: FileInfo) {
  previewFile.value = record
  previewModalVisible.value = true
}

// 复制链接
function handleCopyUrl(record: FileInfo) {
  navigator.clipboard.writeText(window.location.origin + record.url)
  message.success('链接已复制到剪贴板')
}

// 删除单个文件
async function handleDelete(record: FileInfo) {
  if (record.inUse) {
    message.warning('文件正在使用中，无法删除')
    return
  }

  try {
    await AdminAPI.deleteFile(record.url)
    message.success('删除成功')
    loadFiles()
    loadStats()
  } catch (error: any) {
    console.error('删除失败:', error)
    message.error(error.message || '删除失败')
  }
}

// 批量删除
async function handleBatchDelete() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的文件')
    return
  }

  try {
    const result = await AdminAPI.batchDeleteFiles(selectedRowKeys.value)
    
    if (result.successCount > 0) {
      message.success(`成功删除 ${result.successCount} 个文件`)
    }
    
    if (result.failCount > 0) {
      message.warning(`${result.failCount} 个文件删除失败`)
      if (result.failedFiles.length > 0) {
        console.warn('删除失败的文件:', result.failedFiles)
      }
    }

    selectedRowKeys.value = []
    loadFiles()
    loadStats()
  } catch (error) {
    console.error('批量删除失败:', error)
    message.error('批量删除失败')
  }
}

// 获取类型颜色
function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    images: 'blue',
    avatars: 'green',
    other: 'default'
  }
  return colors[type] || 'default'
}

// 获取类型名称
function getTypeName(type: string) {
  const names: Record<string, string> = {
    images: '识别图片',
    avatars: '用户头像',
    other: '其他'
  }
  return names[type] || type
}

// 格式化时间
function formatDateTime(dateTime: string) {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  loadFiles()
  loadStats()
})
</script>

<style scoped>
:deep(.ant-table) {
  font-size: 14px;
}

:deep(.ant-table-cell) {
  padding: 12px 8px;
}
</style>

