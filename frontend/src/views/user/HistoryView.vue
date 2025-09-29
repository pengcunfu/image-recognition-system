<template>
  <div class="history-page">
    <div class="page-header">
      <h1>历史记录</h1>
      <p>查看您的图像识别历史记录</p>
    </div>

    <!-- 筛选器 -->
    <a-card class="filter-card">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6">
          <a-select
            v-model:value="filters.category"
            placeholder="选择分类"
            style="width: 100%"
            @change="applyFilters"
          >
            <a-select-option value="">全部分类</a-select-option>
            <a-select-option value="动物">动物</a-select-option>
            <a-select-option value="植物">植物</a-select-option>
            <a-select-option value="食物">食物</a-select-option>
            <a-select-option value="交通工具">交通工具</a-select-option>
            <a-select-option value="建筑">建筑</a-select-option>
          </a-select>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-range-picker
            v-model:value="filters.dateRange"
            style="width: 100%"
            @change="applyFilters"
          />
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-select
            v-model:value="filters.confidence"
            placeholder="置信度范围"
            style="width: 100%"
            @change="applyFilters"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="high">高 (≥90%)</a-select-option>
            <a-select-option value="medium">中 (70-89%)</a-select-option>
            <a-select-option value="low">低 (<70%)</a-select-option>
          </a-select>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-input-search
            v-model:value="filters.keyword"
            placeholder="搜索识别结果"
            @search="applyFilters"
            @pressEnter="applyFilters"
          />
        </a-col>
      </a-row>
    </a-card>

    <!-- 统计信息 -->
    <a-row :gutter="[24, 24]" class="stats-section">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="总识别次数"
            :value="stats.total"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="本月识别"
            :value="stats.thisMonth"
            suffix="次"
            :value-style="{ color: '#52c41a' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="平均置信度"
            :value="stats.averageConfidence"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#faad14' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="收藏数量"
            :value="stats.favorites"
            suffix="个"
            :value-style="{ color: '#f5222d' }"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 视图切换 -->
    <a-card class="content-card">
      <template #title>
        <div class="card-header">
          <span>识别记录</span>
          <div class="header-actions">
            <a-radio-group v-model:value="viewMode" button-style="solid">
              <a-radio-button value="grid">
                <i class="fas fa-th"></i>
                网格视图
              </a-radio-button>
              <a-radio-button value="list">
                <i class="fas fa-list"></i>
                列表视图
              </a-radio-button>
            </a-radio-group>
          </div>
        </div>
      </template>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="grid-view">
        <div class="history-grid">
          <div 
            v-for="item in filteredHistory" 
            :key="item.id"
            class="history-item"
            @click="viewDetail(item)"
          >
            <div class="item-image">
              <img :src="item.thumbnail" :alt="item.result" />
              <div class="item-overlay">
                <div class="overlay-actions">
                  <a-button type="primary" size="small" ghost>
                    <i class="fas fa-eye"></i>
                  </a-button>
                  <a-button 
                    size="small" 
                    ghost
                    :class="{ 'favorited': item.isFavorite }"
                    @click.stop="toggleFavorite(item)"
                  >
                    <i class="fas fa-heart"></i>
                  </a-button>
                  <a-button size="small" ghost @click.stop="shareItem(item)">
                    <i class="fas fa-share"></i>
                  </a-button>
                </div>
              </div>
            </div>
            
            <div class="item-content">
              <div class="item-title">{{ item.result }}</div>
              <div class="item-meta">
                <span class="item-category">{{ item.category }}</span>
                <span class="item-confidence">{{ item.confidence }}%</span>
              </div>
              <div class="item-time">{{ item.time }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <a-table 
          :columns="tableColumns" 
          :data-source="filteredHistory"
          :pagination="{ 
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'image'">
              <div class="table-image">
                <img :src="record.thumbnail" :alt="record.result" />
              </div>
            </template>
            <template v-else-if="column.key === 'result'">
              <div class="result-info">
                <div class="result-name">{{ record.result }}</div>
                <a-tag :color="getCategoryColor(record.category)">
                  {{ record.category }}
                </a-tag>
              </div>
            </template>
            <template v-else-if="column.key === 'confidence'">
              <div class="confidence-display">
                <a-progress 
                  :percent="record.confidence" 
                  size="small"
                  :stroke-color="getConfidenceColor(record.confidence)"
                />
                <span>{{ record.confidence }}%</span>
              </div>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="viewDetail(record)">
                  查看
                </a-button>
                <a-button 
                  type="link" 
                  size="small" 
                  :class="{ 'favorited': record.isFavorite }"
                  @click="toggleFavorite(record)"
                >
                  {{ record.isFavorite ? '取消收藏' : '收藏' }}
                </a-button>
                <a-dropdown>
                  <a-button type="link" size="small">
                    更多
                    <i class="fas fa-chevron-down"></i>
                  </a-button>
                  <template #overlay>
                    <a-menu @click="({ key }) => handleMoreAction(key, record)">
                      <a-menu-item key="share">
                        <i class="fas fa-share"></i>
                        分享
                      </a-menu-item>
                      <a-menu-item key="download">
                        <i class="fas fa-download"></i>
                        下载
                      </a-menu-item>
                      <a-menu-item key="delete" danger>
                        <i class="fas fa-trash"></i>
                        删除
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'

const viewMode = ref('grid')

// 筛选器
const filters = reactive({
  category: '',
  dateRange: null as [Dayjs, Dayjs] | null,
  confidence: '',
  keyword: ''
})

// 统计数据
const stats = reactive({
  total: 156,
  thisMonth: 42,
  averageConfidence: 87.5,
  favorites: 23
})

// 表格列定义
const tableColumns = [
  { title: '图片', key: 'image', width: 80 },
  { title: '识别结果', key: 'result' },
  { title: '置信度', key: 'confidence', sorter: true },
  { title: '识别时间', dataIndex: 'time', key: 'time', sorter: true },
  { title: '操作', key: 'action', width: 200 }
]

// 历史记录数据
const historyData = ref([
  {
    id: 1,
    result: '金毛寻回犬',
    category: '动物',
    confidence: 95,
    time: '2024-03-20 14:30',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: true
  },
  {
    id: 2,
    result: '玫瑰花',
    category: '植物',
    confidence: 88,
    time: '2024-03-20 10:15',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: false
  },
  {
    id: 3,
    result: '苹果',
    category: '食物',
    confidence: 92,
    time: '2024-03-19 16:22',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: true
  },
  {
    id: 4,
    result: '轿车',
    category: '交通工具',
    confidence: 85,
    time: '2024-03-19 09:45',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: false
  },
  {
    id: 5,
    result: '现代建筑',
    category: '建筑',
    confidence: 78,
    time: '2024-03-18 20:10',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: false
  },
  {
    id: 6,
    result: '波斯猫',
    category: '动物',
    confidence: 91,
    time: '2024-03-18 15:33',
    thumbnail: '/api/placeholder/150/150',
    isFavorite: true
  }
])

// 筛选后的历史记录
const filteredHistory = computed(() => {
  let result = historyData.value

  // 按分类筛选
  if (filters.category) {
    result = result.filter(item => item.category === filters.category)
  }

  // 按置信度筛选
  if (filters.confidence) {
    switch (filters.confidence) {
      case 'high':
        result = result.filter(item => item.confidence >= 90)
        break
      case 'medium':
        result = result.filter(item => item.confidence >= 70 && item.confidence < 90)
        break
      case 'low':
        result = result.filter(item => item.confidence < 70)
        break
    }
  }

  // 按关键词搜索
  if (filters.keyword) {
    const keyword = filters.keyword.toLowerCase()
    result = result.filter(item => 
      item.result.toLowerCase().includes(keyword) ||
      item.category.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 方法
function applyFilters() {
  // 筛选逻辑在 computed 中自动处理
}

function getCategoryColor(category: string) {
  const colors: Record<string, string> = {
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange',
    '交通工具': 'purple',
    '建筑': 'red'
  }
  return colors[category] || 'default'
}

function getConfidenceColor(confidence: number) {
  if (confidence >= 90) return '#52c41a'
  if (confidence >= 70) return '#faad14'
  return '#ff4d4f'
}

function viewDetail(item: any) {
  message.info(`查看详情：${item.result}`)
}

function toggleFavorite(item: any) {
  item.isFavorite = !item.isFavorite
  message.success(item.isFavorite ? '已添加到收藏' : '已取消收藏')
}

function shareItem(item: any) {
  message.info(`分享：${item.result}`)
}

function handleMoreAction(key: string, record: any) {
  switch (key) {
    case 'share':
      shareItem(record)
      break
    case 'download':
      message.info(`下载：${record.result}`)
      break
    case 'delete':
      message.info(`删除：${record.result}`)
      break
  }
}
</script>

<style scoped>
.history-page {
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

/* 筛选器 */
.filter-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

/* 统计信息 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  text-align: center;
}

/* 内容卡片 */
.content-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 网格视图 */
.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.history-item {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.item-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.history-item:hover .item-overlay {
  opacity: 1;
}

.overlay-actions {
  display: flex;
  gap: 8px;
}

.overlay-actions .ant-btn {
  color: white;
  border-color: white;
}

.overlay-actions .favorited {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.item-content {
  padding: 16px;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-category {
  font-size: 12px;
  color: #666;
}

.item-confidence {
  font-size: 14px;
  font-weight: 500;
  color: #52c41a;
}

.item-time {
  font-size: 12px;
  color: #999;
}

/* 列表视图 */
.table-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
}

.table-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.result-name {
  font-weight: 500;
  color: #262626;
}

.confidence-display {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.confidence-display span {
  font-size: 12px;
  color: #666;
}

.favorited {
  color: #ff4d4f !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .history-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .history-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .stats-section .ant-col {
    margin-bottom: 16px;
  }
}
</style>
