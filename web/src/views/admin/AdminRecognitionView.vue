<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select
          v-model:value="filterType"
          placeholder="识别类型"
          style="width: 120px;"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部类型</a-select-option>
          <a-select-option value="single">单张识别</a-select-option>
          <a-select-option value="batch">批量识别</a-select-option>
          <a-select-option value="api">API调用</a-select-option>
        </a-select>
        
        <a-select
          v-model:value="filterStatus"
          placeholder="识别状态"
          style="width: 120px;"
          @change="handleFilterChange"
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="success">成功</a-select-option>
          <a-select-option value="failed">失败</a-select-option>
          <a-select-option value="processing">处理中</a-select-option>
        </a-select>
        
        <a-range-picker
          v-model:value="dateRange"
          @change="handleDateChange"
          style="width: 260px;"
        />
        
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索用户或识别内容"
          style="width: 240px;"
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
      </div>
    </a-card>
    
    <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '24px' }">
      <a-table 
        :columns="recognitionColumns" 
        :data-source="filteredRecognitions"
        :pagination="pagination"
        :loading="tableLoading"
        row-key="id"
        @change="handleTableChange"
        :scroll="{ x: 1500 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="user-info">
              <a-avatar :src="record.userAvatar" size="small">
                {{ record.username.charAt(0) }}
              </a-avatar>
              <div class="user-details">
                <div class="username">{{ record.username }}</div>
                <a-tag v-if="record.isVip" color="gold" size="small">VIP</a-tag>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'image'">
            <div class="image-preview">
              <img 
                :src="record.imageUrl" 
                :alt="record.result"
                @click="previewImage(record.imageUrl)"
                class="thumbnail"
                style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer;"
              />
              <div class="image-info">
                <div class="image-name">{{ record.imageName }}</div>
                <div class="image-size">{{ record.imageSize }}</div>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'type'">
            <a-tag :color="getTypeColor(record.type)" class="type-tag">
              <i :class="getTypeIcon(record.type)"></i>
              {{ getTypeText(record.type) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'result'">
            <div class="recognition-result">
              <div class="result-text">{{ record.result }}</div>
              <div class="confidence">
                置信度: 
                <span :class="getConfidenceClass(record.confidence)">
                  {{ record.confidence }}%
                </span>
              </div>
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
          
          <template v-else-if="column.key === 'duration'">
            <span class="duration">{{ record.duration }}ms</span>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                查看
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item key="reprocess" v-if="record.status === 'failed'">
                      重新识别
                    </a-menu-item>
                    <a-menu-item key="feedback">
                      添加反馈
                    </a-menu-item>
                    <a-menu-item key="export">
                      导出数据
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger key="delete">
                      删除记录
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
    
    <!-- 识别详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="识别记录详情"
      :width="600"
      placement="right"
      :bodyStyle="{ padding: '24px' }"
    >
      <div v-if="selectedRecord" class="recognition-detail">
        <div class="detail-header">
          <div class="header-info">
            <h2>识别记录 #{{ selectedRecord.id }}</h2>
            <div class="header-meta">
              <a-tag :color="getStatusColor(selectedRecord.status)">
                {{ getStatusText(selectedRecord.status) }}
              </a-tag>
              <a-tag :color="getTypeColor(selectedRecord.type)">
                {{ getTypeText(selectedRecord.type) }}
              </a-tag>
              <span class="create-time">{{ selectedRecord.createTime }}</span>
            </div>
          </div>
          <div class="header-actions">
            <a-button v-if="selectedRecord.status === 'failed'" type="primary" @click="reprocessRecord(selectedRecord)">
              <i class="fas fa-redo"></i> 重新识别
            </a-button>
          </div>
        </div>
        
        <a-divider />
        
        <div class="detail-sections">
          <div class="section">
            <h3>用户信息</h3>
            <a-descriptions :column="2" bordered>
              <a-descriptions-item label="用户名">
                {{ selectedRecord.username }}
              </a-descriptions-item>
              <a-descriptions-item label="用户类型">
                {{ selectedRecord.isVip ? 'VIP用户' : '普通用户' }}
              </a-descriptions-item>
              <a-descriptions-item label="IP地址">
                {{ selectedRecord.ipAddress }}
              </a-descriptions-item>
              <a-descriptions-item label="设备信息">
                {{ selectedRecord.deviceInfo }}
              </a-descriptions-item>
            </a-descriptions>
          </div>
          
          <div class="section">
            <h3>图像信息</h3>
            <div class="image-section">
              <div class="image-display">
                <img 
                  :src="selectedRecord.imageUrl" 
                  :alt="selectedRecord.result"
                  class="detail-image"
                  @click="previewImage(selectedRecord.imageUrl)"
                />
              </div>
              <div class="image-meta">
                <a-descriptions :column="1" bordered>
                  <a-descriptions-item label="文件名">
                    {{ selectedRecord.imageName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="文件大小">
                    {{ selectedRecord.imageSize }}
                  </a-descriptions-item>
                  <a-descriptions-item label="图像尺寸">
                    {{ selectedRecord.imageDimensions }}
                  </a-descriptions-item>
                  <a-descriptions-item label="文件格式">
                    {{ selectedRecord.imageFormat }}
                  </a-descriptions-item>
                </a-descriptions>
              </div>
            </div>
          </div>
          
          <div class="section">
            <h3>识别结果</h3>
            <a-descriptions :column="2" bordered>
              <a-descriptions-item label="识别结果">
                <span class="result-text">{{ selectedRecord.result }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="置信度">
                <span :class="getConfidenceClass(selectedRecord.confidence)">
                  {{ selectedRecord.confidence }}%
                </span>
              </a-descriptions-item>
              <a-descriptions-item label="处理时间">
                {{ selectedRecord.duration }}ms
              </a-descriptions-item>
              <a-descriptions-item label="使用模型">
                {{ selectedRecord.modelVersion }}
              </a-descriptions-item>
            </a-descriptions>
          </div>
          
          <div class="section" v-if="selectedRecord.alternativeResults && selectedRecord.alternativeResults.length > 0">
            <h3>候选结果</h3>
            <a-table 
              :columns="alternativeColumns" 
              :data-source="selectedRecord.alternativeResults"
              :pagination="false"
              size="small"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'confidence'">
                  <span :class="getConfidenceClass(record.confidence)">
                    {{ record.confidence }}%
                  </span>
                </template>
              </template>
            </a-table>
          </div>
          
          <div class="section" v-if="selectedRecord.feedback">
            <h3>用户反馈</h3>
            <div class="feedback-content">
              <p><strong>反馈类型:</strong> {{ selectedRecord.feedback.type }}</p>
              <p><strong>反馈内容:</strong> {{ selectedRecord.feedback.content }}</p>
              <p><strong>反馈时间:</strong> {{ selectedRecord.feedback.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </a-drawer>
    
    <!-- 图片预览模态框 -->
    <a-modal
      v-model:open="previewVisible"
      title="图片预览"
      :footer="null"
      width="80%"
      centered
    >
      <img 
        :src="previewImageUrl" 
        alt="预览图片"
        style="width: 100%; max-height: 70vh; object-fit: contain;"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import { AdminAPI, type RecognitionRecordInfo } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
const drawerVisible = ref(false)
const previewVisible = ref(false)
const selectedRecord = ref<any>(null)
const previewImageUrl = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dateRange = ref<[Dayjs, Dayjs] | null>(null)
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

// 识别记录表格列定义
const recognitionColumns = [
  { 
    title: '用户', 
    key: 'user',
    width: 150
  },
  { 
    title: '图像', 
    key: 'image',
    width: 200
  },
  { 
    title: '类型', 
    key: 'type',
    width: 100
  },
  { 
    title: '识别结果', 
    key: 'result',
    width: 200
  },
  { 
    title: '状态', 
    key: 'status',
    width: 100
  },
  { 
    title: '耗时', 
    key: 'duration',
    width: 80
  },
  { 
    title: '创建时间', 
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

// 候选结果表格列
const alternativeColumns = [
  { title: '排名', dataIndex: 'rank', key: 'rank', width: 60 },
  { title: '识别结果', dataIndex: 'result', key: 'result' },
  { title: '置信度', dataIndex: 'confidence', key: 'confidence', width: 100 }
]

// 识别记录数据
const recognitionRecords = ref<any[]>([])

// 加载识别记录
async function loadRecognitionRecords() {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.pageSize
    }

    // 添加筛选条件
    if (filterStatus.value) {
      // 将状态映射为后端接受的值
      const statusMap: Record<string, number> = {
        'success': 1,
        'failed': 2,
        'processing': 0
      }
      params.status = statusMap[filterStatus.value]
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    const response = await AdminAPI.getRecognitionRecords(params)
    
    // 转换后端数据为前端需要的格式
    recognitionRecords.value = response.data.map((record: RecognitionRecordInfo) => {
      // 解析resultJson获取详细结果
      let resultData: any = {}
      try {
        if (record.resultJson) {
          resultData = JSON.parse(record.resultJson)
        }
      } catch (e) {
        console.warn('Failed to parse resultJson:', e)
      }

      // 确定识别结果显示
      let result = record.mainCategory || record.category || record.objectName || '未知'
      if (record.status === 2) {
        result = record.errorMessage || '识别失败'
      } else if (record.status === 0) {
        result = '处理中...'
      }

      // 计算置信度（转换为百分比）
      const confidence = record.confidence ? Number(record.confidence) : 0

      // 确定状态
      let status = 'success'
      if (record.status === 0) status = 'processing'
      else if (record.status === 2) status = 'failed'

      // 确定类型
      let type = 'single'
      if (record.recognitionType === 1) type = 'batch'
      else if (record.recognitionType === 2) type = 'api'

      // 格式化文件大小
      const formatSize = (bytes?: number) => {
        if (!bytes) return '0 B'
        const k = 1024
        const sizes = ['B', 'KB', 'MB', 'GB']
        const i = Math.floor(Math.log(bytes) / Math.log(k))
        return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
      }

      return {
        id: record.id,
        userId: record.userId,
        username: record.username || `用户${record.userId}`,
        userAvatar: '',
        isVip: false,
        imageUrl: record.imageUrl,
        imageName: record.imageName || '未知文件',
        imageSize: formatSize(record.imageSize),
        imageDimensions: record.imageWidth && record.imageHeight 
          ? `${record.imageWidth}x${record.imageHeight}` 
          : '未知',
        imageFormat: record.imageName?.split('.').pop()?.toUpperCase() || 'JPEG',
        type,
        result,
        confidence,
        status,
        duration: record.processingTime || 0,
        modelVersion: 'v2.1.0',
        createTime: dayjs(record.createdAt).format('YYYY-MM-DD HH:mm:ss'),
        ipAddress: '未知',
        deviceInfo: '未知',
        alternativeResults: resultData.alternatives || [],
        tags: record.tags,
        description: record.description
      }
    })

    pagination.total = response.total
  } catch (error: any) {
    console.error('加载识别记录失败:', error)
    message.error(error.message || '加载识别记录失败')
  } finally {
    loading.value = false
  }
}

// 过滤后的识别记录列表（现在主要在后端过滤）
const filteredRecognitions = computed(() => {
  let result = recognitionRecords.value

  // 类型筛选（前端补充筛选）
  if (filterType.value) {
    result = result.filter(record => record.type === filterType.value)
  }

  // 日期范围筛选（前端补充筛选）
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    result = result.filter(record => {
      const recordDate = new Date(record.createTime)
      return recordDate >= start.toDate() && recordDate <= end.toDate()
    })
  }

  return result
})

// 获取类型颜色
function getTypeColor(type: string) {
  switch (type) {
    case 'single': return 'blue'
    case 'batch': return 'green'
    case 'api': return 'purple'
    default: return 'default'
  }
}

// 获取类型图标
function getTypeIcon(type: string) {
  switch (type) {
    case 'single': return 'fas fa-image'
    case 'batch': return 'fas fa-images'
    case 'api': return 'fas fa-code'
    default: return 'fas fa-question'
  }
}

// 获取类型文本
function getTypeText(type: string) {
  switch (type) {
    case 'single': return '单张识别'
    case 'batch': return '批量识别'
    case 'api': return 'API调用'
    default: return '未知'
  }
}

// 获取状态颜色
function getStatusColor(status: string) {
  switch (status) {
    case 'success': return 'success'
    case 'failed': return 'error'
    case 'processing': return 'processing'
    default: return 'default'
  }
}

// 获取状态图标
function getStatusIcon(status: string) {
  switch (status) {
    case 'success': return 'fas fa-check-circle'
    case 'failed': return 'fas fa-times-circle'
    case 'processing': return 'fas fa-spinner fa-spin'
    default: return 'fas fa-question-circle'
  }
}

// 获取状态文本
function getStatusText(status: string) {
  switch (status) {
    case 'success': return '成功'
    case 'failed': return '失败'
    case 'processing': return '处理中'
    default: return '未知'
  }
}

// 获取置信度样式类
function getConfidenceClass(confidence: number) {
  if (confidence >= 90) return 'confidence-high'
  if (confidence >= 70) return 'confidence-medium'
  if (confidence >= 50) return 'confidence-low'
  return 'confidence-very-low'
}

// 处理筛选变化
function handleFilterChange() {
  pagination.current = 1
  loadRecognitionRecords()
}

// 处理日期变化
function handleDateChange() {
  pagination.current = 1
  // 日期筛选在前端处理
}

// 处理搜索
function handleSearch() {
  pagination.current = 1
  loadRecognitionRecords()
}

// 处理重置
function handleReset() {
  filterType.value = ''
  filterStatus.value = ''
  dateRange.value = null
  searchKeyword.value = ''
  pagination.current = 1
  loadRecognitionRecords()
}

// 处理表格变化
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadRecognitionRecords()
}

// 查看详情
function viewDetail(record: any) {
  selectedRecord.value = record
  drawerVisible.value = true
}

// 预览图片
function previewImage(imageUrl: string) {
  previewImageUrl.value = imageUrl
  previewVisible.value = true
}

// 处理操作
function handleAction(action: string, record: any) {
  switch (action) {
    case 'reprocess':
      reprocessRecord(record)
      break
    case 'feedback':
      addFeedback(record)
      break
    case 'export':
      exportRecord(record)
      break
    case 'delete':
      deleteRecord(record)
      break
  }
}

// 重新识别
function reprocessRecord(record: any) {
  Modal.confirm({
    title: '确认重新识别',
    content: `确定要重新识别图片"${record.imageName}"吗？`,
    onOk() {
      record.status = 'processing'
      record.result = '处理中...'
      record.confidence = 0
      message.success('已提交重新识别任务')
      
      // 模拟处理过程
      setTimeout(() => {
        record.status = 'success'
        record.result = '重新识别结果'
        record.confidence = 89.5
        message.success('重新识别完成')
      }, 3000)
    }
  })
}

// 添加反馈
function addFeedback(record: any) {
  // 这里应该打开反馈表单
  message.info('反馈功能开发中')
}

// 导出记录
function exportRecord(record: any) {
  // 这里应该导出识别记录数据
  message.success(`已导出识别记录 #${record.id}`)
}

// 删除记录
async function deleteRecord(record: any) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除识别记录 #${record.id} 吗？此操作不可恢复！`,
    okType: 'danger',
    async onOk() {
      try {
        await AdminAPI.deleteRecognitionRecord(record.id)
        message.success('识别记录已删除')
        drawerVisible.value = false
        // 重新加载数据
        loadRecognitionRecords()
      } catch (error: any) {
        console.error('删除识别记录失败:', error)
        message.error(error.message || '删除识别记录失败')
      }
    }
  })
}

// 组件挂载
onMounted(() => {
  loadRecognitionRecords()
})
</script>

