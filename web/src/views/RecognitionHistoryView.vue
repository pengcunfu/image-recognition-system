<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ textAlign: 'center' }">
        <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">历史记录</h1>
        <p :style="{ margin: 0, fontSize: '14px', opacity: 0.65 }">查看您的图像识别历史记录</p>
      </div>
    </a-card>

    <!-- 筛选器 -->
    <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
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
            :placeholder="['开始日期', '结束日期']"
            format="YYYY-MM-DD"
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
    <a-row :gutter="[16, 16]" :style="{ marginBottom: '16px' }">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="总识别次数"
            :value="stats.total"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
          <a-statistic
            title="本月识别"
            :value="stats.thisMonth"
            suffix="次"
            :value-style="{ color: '#52c41a' }"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card :style="{ borderRadius: '8px' }">
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
        <a-card :style="{ borderRadius: '8px' }">
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
    <a-card :style="{ borderRadius: '8px' }">
      <template #title>
        <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
          <span>识别记录</span>
          <div>
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

      <!-- 加载状态 -->
      <a-spin :spinning="loading">
        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'">
          <!-- 空状态 -->
          <a-empty 
            v-if="!loading && filteredHistory.length === 0" 
            description="暂无识别记录"
            :style="{ minHeight: '400px', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }"
          />
          
          <!-- 有数据时显示网格 -->
          <template v-else>
            <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '16px', minHeight: '400px' }">
              <div 
                v-for="item in filteredHistory" 
                :key="item.id"
                class="grid-item"
                :style="{ borderRadius: '8px', overflow: 'hidden', cursor: 'pointer', transition: 'all 0.3s ease', border: '1px solid #d9d9d9' }"
                @click="viewDetail(item)"
              >
                <div :style="{ position: 'relative', paddingBottom: '56.25%', overflow: 'hidden' }">
                  <img :src="item.thumbnail" :alt="item.result" :style="{ position: 'absolute', width: '100%', height: '100%', objectFit: 'cover' }" />
                  <div class="image-overlay" :style="{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', opacity: 0, transition: 'opacity 0.3s ease', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }">
                    <div :style="{ display: 'flex', gap: '8px' }">
                      <a-button type="primary" size="small" ghost>
                        <i class="fas fa-eye"></i>
                      </a-button>
                      <a-button 
                        size="small" 
                        ghost
                        @click.stop="toggleFavorite(item)"
                      >
                        <i class="fas fa-heart" :style="{ color: item.isFavorite ? '#f5222d' : 'white' }"></i>
                      </a-button>
                      <a-button size="small" ghost @click.stop="shareItem(item)">
                        <i class="fas fa-share"></i>
                      </a-button>
                    </div>
                  </div>
                </div>
                
                <div :style="{ padding: '16px', background: 'white' }">
                  <div :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '8px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }">{{ item.result }}</div>
                  <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }">
                    <span :style="{ fontSize: '13px' }">{{ item.category }}</span>
                    <span :style="{ fontSize: '13px', fontWeight: '500', color: getConfidenceColor(item.confidence) }">{{ item.confidence }}%</span>
                  </div>
                  <div :style="{ fontSize: '12px', opacity: '0.65' }">{{ item.time }}</div>
                </div>
              </div>
            </div>
            
            <!-- 网格视图分页 -->
            <div v-if="filteredHistory.length > 0" :style="{ marginTop: '16px', textAlign: 'center' }">
              <a-pagination
                v-model:current="pagination.current"
                v-model:page-size="pagination.pageSize"
                :total="pagination.total"
                :show-size-changer="true"
                :show-quick-jumper="true"
                :show-total="(total: number) => `共 ${total} 条记录`"
                @change="loadHistory"
              />
            </div>
          </template>
        </div>

        <!-- 列表视图 -->
        <div v-else>
          <a-table 
            :columns="tableColumns" 
            :data-source="filteredHistory"
            :pagination="{ 
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total: number) => `共 ${total} 条记录`
            }"
            row-key="id"
            @change="handleTableChange"
          >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'image'">
              <div :style="{ width: '60px', height: '60px', borderRadius: '6px', overflow: 'hidden' }">
                <img :src="record.thumbnail" :alt="record.result" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
              </div>
            </template>
            <template v-else-if="column.key === 'result'">
              <div>
                <div :style="{ fontSize: '14px', fontWeight: '500', marginBottom: '4px' }">{{ record.result }}</div>
                <a-tag :color="getCategoryColor(record.category)">
                  {{ record.category }}
                </a-tag>
              </div>
            </template>
            <template v-else-if="column.key === 'confidence'">
              <div :style="{ display: 'flex', flexDirection: 'column', gap: '4px' }">
                <a-progress 
                  :percent="record.confidence" 
                  size="small"
                  :stroke-color="getConfidenceColor(record.confidence)"
                />
                <span :style="{ fontSize: '13px' }">{{ record.confidence }}%</span>
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
                    <a-menu @click="({ key }: { key: string }) => handleMoreAction(key, record)">
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
    </a-spin>
  </a-card>
</div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import { RecognitionAPI, type RecognitionStats } from '@/api/recognition'
import { ImageUtils } from '@/utils/image'

const router = useRouter()
const viewMode = ref('grid')
const isTableView = computed(() => viewMode.value === 'table')
const loading = ref(false)

// 筛选器
const filters = reactive({
  category: '',
  dateRange: null as [Dayjs, Dayjs] | null,
  confidence: '',
  keyword: ''
})

// 统计数据
const stats = reactive<RecognitionStats>({
  total: 0,
  thisMonth: 0,
  averageConfidence: 0,
  favorites: 0
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
const historyData = ref<any[]>([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0
})

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

// 加载历史数据
async function loadHistory() {
  loading.value = true
  try {
    const response = await RecognitionAPI.getHistory({
      page: pagination.current,
      size: pagination.pageSize
    })
    
    if (response && response.data) {
      // 转换数据格式 - 使用 ImageUtils
      historyData.value = response.data.map((item: any) => ({
        id: item.id,
        result: item.mainCategory || item.category || '未知',
        category: getCategoryFromResult(item.mainCategory || item.category || '未知'),
        confidence: item.confidence ? Math.round(Number(item.confidence) * 100) : 0,
        time: formatDateTime(item.createdAt),
        thumbnail: ImageUtils.getImageUrl(item.imageUrl) || '/api/placeholder/150/150',
        isFavorite: false // TODO: 从收藏接口获取
      }))
      
      // 更新分页信息
      pagination.total = response.total || 0
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
    message.error('加载历史记录失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const statsData = await RecognitionAPI.getStats()
    if (statsData) {
      Object.assign(stats, statsData)
      console.log('统计数据:', stats)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 静默失败,不显示错误提示
  }
}

// 从识别结果推断分类
function getCategoryFromResult(result: string): string {
  const resultLower = result.toLowerCase()
  if (resultLower.includes('犬') || resultLower.includes('猫') || resultLower.includes('动物')) {
    return '动物'
  } else if (resultLower.includes('花') || resultLower.includes('树') || resultLower.includes('植物')) {
    return '植物'
  } else if (resultLower.includes('食') || resultLower.includes('果') || resultLower.includes('菜')) {
    return '食物'
  } else if (resultLower.includes('车') || resultLower.includes('飞机') || resultLower.includes('船')) {
    return '交通工具'
  } else if (resultLower.includes('建筑') || resultLower.includes('房') || resultLower.includes('楼')) {
    return '建筑'
  }
  return '其他'
}

// 格式化日期时间
function formatDateTime(dateTime: any): string {
  if (!dateTime) return '-'
  
  const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 方法
function applyFilters() {
  // 重新加载数据
  pagination.current = 1
  loadHistory()
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
  router.push(`/user/recognition/${item.id}?from=history`)
}

function toggleFavorite(item: any) {
  item.isFavorite = !item.isFavorite
  message.success(item.isFavorite ? '已添加到收藏' : '已取消收藏')
}

function shareItem(item: any) {
  message.info(`分享：${item.result}`)
}

async function handleMoreAction(key: string, record: any) {
  switch (key) {
    case 'share':
      shareItem(record)
      break
    case 'download':
      message.info(`下载：${record.result}`)
      break
    case 'delete':
      await deleteRecord(record)
      break
  }
}

// 删除识别记录
async function deleteRecord(record: any) {
  try {
    await RecognitionAPI.deleteRecognition(record.id)
    message.success('删除成功')
    // 重新加载数据
    await loadHistory()
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

// 表格分页变化处理
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadHistory()
}

// 组件挂载时加载数据
onMounted(() => {
  loadStats()
  loadHistory()
})
</script>

<style scoped>
.grid-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important;
  transform: translateY(-2px);
}

.grid-item:hover .image-overlay {
  opacity: 1 !important;
}
</style>

