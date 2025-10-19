<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">识别记录管理</h1>
      <a-space>
        <a-select
          v-model:value="filterType"
          placeholder="识别类型"
          style="width: 120px"
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
          style="width: 120px"
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
          style="width: 240px"
        />
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索用户或识别内容"
          style="width: 300px"
          @search="handleSearch"
        />
      </a-space>
    </div>
    
    <!-- 统计卡片 -->
    <a-row :gutter="[24, 24]" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card total-recognition">
          <a-statistic
            title="总识别次数"
            :value="recognitionStats.total"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+15.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card success-recognition">
          <a-statistic
            title="成功识别"
            :value="recognitionStats.success"
            suffix="次"
            :value-style="{ color: '#52c41a' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+12.8%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card today-recognition">
          <a-statistic
            title="今日识别"
            :value="recognitionStats.today"
            suffix="次"
            :value-style="{ color: '#faad14' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+22.1%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card accuracy-rate">
          <a-statistic
            title="识别准确率"
            :value="recognitionStats.accuracy"
            suffix="%"
            :value-style="{ color: '#722ed1' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+2.3%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <a-card class="table-card">
      <a-table 
        :columns="recognitionColumns" 
        :data-source="filteredRecognitions"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
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
              <a-button type="primary" size="small" @click="viewDetail(record)">
                详情
              </a-button>
              <a-dropdown>
                <a-button size="small">
                  更多 <i class="fas fa-chevron-down"></i>
                </a-button>
                <template #overlay>
                  <a-menu @click="(e: any) => handleAction(e.key, record)">
                    <a-menu-item key="reprocess" v-if="record.status === 'failed'">
                      <i class="fas fa-redo"></i> 重新识别
                    </a-menu-item>
                    <a-menu-item key="feedback">
                      <i class="fas fa-comment"></i> 添加反馈
                    </a-menu-item>
                    <a-menu-item key="export">
                      <i class="fas fa-download"></i> 导出数据
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="delete" class="danger-item">
                      <i class="fas fa-trash"></i> 删除记录
                    </a-menu-item>
                  </a-menu>
                </template>
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
      width="60%"
      class="recognition-drawer"
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

// 响应式数据
const loading = ref(false)
const drawerVisible = ref(false)
const previewVisible = ref(false)
const selectedRecord = ref<any>(null)
const previewImageUrl = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dateRange = ref<[Dayjs, Dayjs] | null>(null)
const searchKeyword = ref('')

// 识别统计数据
const recognitionStats = reactive({
  total: 125678,
  success: 118234,
  today: 1890,
  accuracy: 94.1
})

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

// 模拟识别记录数据
const recognitionRecords = ref([
  {
    id: 1,
    username: '张三',
    userAvatar: '',
    isVip: true,
    imageUrl: '/images/cat-sample.jpg',
    imageName: 'cat-photo.jpg',
    imageSize: '2.3MB',
    imageDimensions: '1920x1080',
    imageFormat: 'JPEG',
    type: 'single',
    result: '猫',
    confidence: 96.8,
    status: 'success',
    duration: 1250,
    modelVersion: 'v2.1.0',
    createTime: '2025-01-15 14:30:25',
    ipAddress: '192.168.1.100',
    deviceInfo: 'Chrome 120.0 / Windows 10',
    alternativeResults: [
      { rank: 1, result: '猫', confidence: 96.8 },
      { rank: 2, result: '小猫', confidence: 89.2 },
      { rank: 3, result: '宠物猫', confidence: 85.6 }
    ]
  },
  {
    id: 2,
    username: '李四',
    userAvatar: '',
    isVip: false,
    imageUrl: '/images/dog-sample.jpg',
    imageName: 'dog-image.png',
    imageSize: '1.8MB',
    imageDimensions: '1600x1200',
    imageFormat: 'PNG',
    type: 'single',
    result: '金毛犬',
    confidence: 92.4,
    status: 'success',
    duration: 980,
    modelVersion: 'v2.1.0',
    createTime: '2025-01-15 14:25:10',
    ipAddress: '192.168.1.101',
    deviceInfo: 'Safari 17.0 / macOS 14',
    alternativeResults: [
      { rank: 1, result: '金毛犬', confidence: 92.4 },
      { rank: 2, result: '拉布拉多', confidence: 78.9 },
      { rank: 3, result: '大型犬', confidence: 65.3 }
    ]
  },
  {
    id: 3,
    username: '王五',
    userAvatar: '',
    isVip: true,
    imageUrl: '/images/flower-sample.jpg',
    imageName: 'flower.jpg',
    imageSize: '3.1MB',
    imageDimensions: '2048x1536',
    imageFormat: 'JPEG',
    type: 'batch',
    result: '玫瑰花',
    confidence: 88.7,
    status: 'success',
    duration: 2100,
    modelVersion: 'v2.1.0',
    createTime: '2025-01-15 14:20:45',
    ipAddress: '192.168.1.102',
    deviceInfo: 'Chrome 120.0 / Android 12',
    alternativeResults: [
      { rank: 1, result: '玫瑰花', confidence: 88.7 },
      { rank: 2, result: '红玫瑰', confidence: 82.1 },
      { rank: 3, result: '花朵', confidence: 75.4 }
    ]
  },
  {
    id: 4,
    username: '赵六',
    userAvatar: '',
    isVip: false,
    imageUrl: '/images/error-sample.jpg',
    imageName: 'blurry-image.jpg',
    imageSize: '0.8MB',
    imageDimensions: '800x600',
    imageFormat: 'JPEG',
    type: 'api',
    result: '识别失败',
    confidence: 0,
    status: 'failed',
    duration: 500,
    modelVersion: 'v2.1.0',
    createTime: '2025-01-15 14:15:30',
    ipAddress: '192.168.1.103',
    deviceInfo: 'API Client v1.0',
    feedback: {
      type: '结果错误',
      content: '图片太模糊，无法正确识别',
      time: '2025-01-15 14:16:00'
    }
  },
  {
    id: 5,
    username: '钱七',
    userAvatar: '',
    isVip: true,
    imageUrl: '/images/processing-sample.jpg',
    imageName: 'large-image.png',
    imageSize: '5.2MB',
    imageDimensions: '4096x3072',
    imageFormat: 'PNG',
    type: 'single',
    result: '处理中...',
    confidence: 0,
    status: 'processing',
    duration: 0,
    modelVersion: 'v2.1.0',
    createTime: '2025-01-15 14:35:15',
    ipAddress: '192.168.1.104',
    deviceInfo: 'Firefox 121.0 / Linux'
  }
])

// 过滤后的识别记录列表
const filteredRecognitions = computed(() => {
  let result = recognitionRecords.value

  // 类型筛选
  if (filterType.value) {
    result = result.filter(record => record.type === filterType.value)
  }

  // 状态筛选
  if (filterStatus.value) {
    result = result.filter(record => record.status === filterStatus.value)
  }

  // 日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    result = result.filter(record => {
      const recordDate = new Date(record.createTime)
      return recordDate >= start.toDate() && recordDate <= end.toDate()
    })
  }

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(record => 
      record.username.toLowerCase().includes(keyword) ||
      record.result.toLowerCase().includes(keyword) ||
      record.imageName.toLowerCase().includes(keyword)
    )
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
}

// 处理日期变化
function handleDateChange() {
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
function deleteRecord(record: any) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除识别记录 #${record.id} 吗？此操作不可恢复！`,
    okType: 'danger',
    onOk() {
      const index = recognitionRecords.value.findIndex(r => r.id === record.id)
      if (index > -1) {
        recognitionRecords.value.splice(index, 1)
        message.success('识别记录已删除')
        drawerVisible.value = false
      }
    }
  })
}

// 组件挂载
onMounted(() => {
  pagination.total = recognitionRecords.value.length
})
</script>

