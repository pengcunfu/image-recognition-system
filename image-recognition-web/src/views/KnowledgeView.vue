<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面头部 -->
      <a-card :style="{ marginBottom: '16px', borderRadius: '8px', textAlign: 'center' }">
        <h1 :style="{ fontSize: '28px', fontWeight: '600', margin: '0 0 8px 0' }">知识库</h1>
        <p :style="{ fontSize: '14px', margin: 0, opacity: 0.65 }">探索丰富的图像识别知识百科</p>
      </a-card>

      <!-- 搜索区域 -->
      <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索知识内容、标签..."
          @search="handleSearch"
          :style="{ borderRadius: '8px' }"
        >
          <template #enterButton>
            <a-button type="primary">
              <i class="fas fa-search" :style="{ marginRight: '4px' }"></i>
              搜索
            </a-button>
          </template>
        </a-input-search>
      </a-card>

      <!-- 主内容区域：左侧知识列表 + 右侧边栏 -->
      <div :style="{ display: 'flex', gap: '24px', alignItems: 'flex-start' }">
        <!-- 左侧：知识列表 -->
        <div :style="{ flex: 1, minWidth: 0 }">
          <!-- 知识列表（按分类） -->
          <a-card :loading="loading && categories.length === 0" :style="{ borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)', marginBottom: '32px', border: 'none' }">
            <a-tabs v-model:activeKey="activeCategory" @change="handleCategoryChange" :style="{ marginTop: '-8px' }">
              <a-tab-pane key="" tab="全部">
                <a-spin :spinning="loading">
                  <a-empty v-if="!loading && knowledgeData.length === 0" description="暂无知识条目" />
                  <div v-else :style="{ 
                    columnCount: 'auto',
                    columnWidth: '280px',
                    columnGap: '16px',
                    columnFill: 'balance'
                  }">
                    <div 
                      v-for="item in knowledgeData" 
                      :key="item.id"
                      :style="{ 
                        breakInside: 'avoid',
                        marginBottom: '16px',
                        width: '100%'
                      }"
                    >
                      <div 
                        :style="{ 
                          borderRadius: '8px', 
                          overflow: 'hidden', 
                          cursor: 'pointer',
                          transition: 'all 0.3s',
                          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                          height: '100%',
                          display: 'flex',
                          flexDirection: 'column',
                          background: 'white',
                          border: '1px solid #e8e8e8'
                        }"
                        @click="viewKnowledge(item)"
                        @mouseenter="(e) => { setStyle(e, 'box-shadow', '0 4px 16px rgba(24,144,255,0.2)'); setStyle(e, 'border-color', '#1890ff'); }"
                        @mouseleave="(e) => { setStyle(e, 'box-shadow', '0 2px 8px rgba(0,0,0,0.06)'); setStyle(e, 'border-color', '#e8e8e8'); }"
                      >
                        <div :style="{ 
                          position: 'relative', 
                          width: '100%', 
                          paddingBottom: '66.67%',
                          background: '#f5f5f5',
                          overflow: 'hidden'
                        }">
                          <img 
                            :src="item.image || '/api/placeholder/300/200'" 
                            :alt="item.title" 
                            :style="{ 
                              position: 'absolute',
                              top: 0,
                              left: 0,
                              width: '100%', 
                              height: '100%', 
                              objectFit: 'cover',
                              transition: 'transform 0.3s'
                            }" 
                            @mouseenter="(e) => setStyle(e, 'transform', 'scale(1.05)')"
                            @mouseleave="(e) => setStyle(e, 'transform', 'scale(1)')"
                          />
                        </div>
                        <div :style="{ padding: '12px', flex: 1, display: 'flex', flexDirection: 'column', gap: '6px' }">
                          <h3 :style="{ 
                            margin: 0, 
                            fontSize: '16px', 
                            fontWeight: 'bold', 
                            color: '#262626',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }">{{ item.title }}</h3>
                          <p :style="{ 
                            margin: 0, 
                            fontSize: '13px', 
                            color: '#8c8c8c',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            display: '-webkit-box',
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: 'vertical',
                            flex: 1
                          }">{{ item.description }}</p>
                          <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto' }">
                            <a-tag :color="getCategoryColor(item.category)">{{ item.category }}</a-tag>
                            <span :style="{ fontSize: '12px', color: '#8c8c8c', display: 'flex', alignItems: 'center', gap: '4px' }">
                              <i class="fas fa-eye"></i>
                              {{ item.views || 0 }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </a-spin>
              </a-tab-pane>
              <a-tab-pane 
                v-for="cat in categories" 
                :key="cat" 
                :tab="cat"
              >
                <a-spin :spinning="loading">
                  <a-empty v-if="!loading && getKnowledgeByCategory(cat).length === 0" description="暂无知识条目" />
                  <div v-else :style="{ 
                    columnCount: 'auto',
                    columnWidth: '280px',
                    columnGap: '16px',
                    columnFill: 'balance'
                  }">
                    <div 
                      v-for="item in getKnowledgeByCategory(cat)" 
                      :key="item.id"
                      :style="{ 
                        breakInside: 'avoid',
                        marginBottom: '16px',
                        width: '100%'
                      }"
                    >
                      <div 
                        :style="{ 
                          borderRadius: '8px', 
                          overflow: 'hidden', 
                          cursor: 'pointer',
                          transition: 'all 0.3s',
                          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                          height: '100%',
                          display: 'flex',
                          flexDirection: 'column',
                          background: 'white',
                          border: '1px solid #e8e8e8'
                        }"
                        @click="viewKnowledge(item)"
                        @mouseenter="(e) => { setStyle(e, 'box-shadow', '0 4px 16px rgba(24,144,255,0.2)'); setStyle(e, 'border-color', '#1890ff'); }"
                        @mouseleave="(e) => { setStyle(e, 'box-shadow', '0 2px 8px rgba(0,0,0,0.06)'); setStyle(e, 'border-color', '#e8e8e8'); }"
                      >
                        <div :style="{ 
                          position: 'relative', 
                          width: '100%', 
                          paddingBottom: '66.67%',
                          background: '#f5f5f5',
                          overflow: 'hidden'
                        }">
                          <img 
                            :src="item.image || '/api/placeholder/300/200'" 
                            :alt="item.title" 
                            :style="{ 
                              position: 'absolute',
                              top: 0,
                              left: 0,
                              width: '100%', 
                              height: '100%', 
                              objectFit: 'cover',
                              transition: 'transform 0.3s'
                            }" 
                            @mouseenter="(e) => setStyle(e, 'transform', 'scale(1.05)')"
                            @mouseleave="(e) => setStyle(e, 'transform', 'scale(1)')"
                          />
                        </div>
                        <div :style="{ padding: '12px', flex: 1, display: 'flex', flexDirection: 'column', gap: '6px' }">
                          <h3 :style="{ 
                            margin: 0, 
                            fontSize: '16px', 
                            fontWeight: 'bold', 
                            color: '#262626',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }">{{ item.title }}</h3>
                          <p :style="{ 
                            margin: 0, 
                            fontSize: '13px', 
                            color: '#8c8c8c',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            display: '-webkit-box',
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: 'vertical',
                            flex: 1
                          }">{{ item.description }}</p>
                          <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto' }">
                            <a-tag :color="getCategoryColor(item.category)">{{ item.category }}</a-tag>
                            <span :style="{ fontSize: '12px', color: '#8c8c8c', display: 'flex', alignItems: 'center', gap: '4px' }">
                              <i class="fas fa-eye"></i>
                              {{ item.views || 0 }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </a-spin>
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </div>

        <!-- 右侧边栏：热门推荐、最新更新 -->
        <div :style="{ width: '320px', flexShrink: '0' }">
          <!-- 热门推荐 -->
          <a-card :style="{ borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)', border: 'none', marginBottom: '24px' }">
            <template #title>
              <span :style="{ fontSize: '16px', fontWeight: 'bold', color: '#262626', display: 'flex', alignItems: 'center', gap: '8px' }">
                <i class="fas fa-fire" :style="{ color: '#ff4d4f' }"></i>
                热门推荐
              </span>
            </template>
            <a-empty v-if="hotKnowledge.length === 0" description="暂无热门知识" />
            <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
              <div 
                v-for="item in hotKnowledge" 
                :key="item.id"
                :style="{ 
                  cursor: 'pointer',
                  paddingBottom: '12px',
                  borderBottom: '1px solid #f0f0f0',
                  display: 'flex',
                  gap: '12px',
                  alignItems: 'flex-start',
                  transition: 'all 0.3s'
                }"
                @click="viewKnowledge(item)"
                @mouseenter="(e) => setStyle(e, 'background', '#f5f5f5')"
                @mouseleave="(e) => setStyle(e, 'background', 'transparent')"
              >
                <div :style="{ 
                  width: '28px', 
                  height: '28px', 
                  borderRadius: '50%', 
                  background: (item.rank || 0) <= 3 ? '#1890ff' : '#f0f0f0',
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center',
                  fontSize: '14px',
                  fontWeight: 'bold',
                  color: (item.rank || 0) <= 3 ? '#fff' : '#8c8c8c',
                  flexShrink: '0'
                }">
                  {{ item.rank }}
                </div>
                <div :style="{ flex: 1, minWidth: 0 }">
                  <h4 :style="{ 
                    margin: '0 0 8px 0', 
                    fontSize: '14px', 
                    fontWeight: 'bold', 
                    color: '#262626',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    display: '-webkit-box',
                    WebkitLineClamp: 2,
                    WebkitBoxOrient: 'vertical'
                  }">{{ item.title }}</h4>
                  <div :style="{ display: 'flex', gap: '12px', fontSize: '11px', color: '#8c8c8c' }">
                    <span :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
                      <i class="fas fa-eye"></i>
                      {{ item.views || 0 }}
                    </span>
                    <span :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
                      <i class="fas fa-heart"></i>
                      {{ item.likes || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </a-card>

          <!-- 最新更新 -->
          <a-card :style="{ borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)', border: 'none', position: 'sticky', top: '24px' }">
            <template #title>
              <span :style="{ fontSize: '16px', fontWeight: 'bold', color: '#262626', display: 'flex', alignItems: 'center', gap: '8px' }">
                <i class="fas fa-clock" :style="{ color: '#1890ff' }"></i>
                最新更新
              </span>
            </template>
            <a-empty v-if="latestKnowledge.length === 0" description="暂无最新知识" />
            <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
              <div 
                v-for="item in latestKnowledge.slice(0, 5)" 
                :key="item.id"
                :style="{ 
                  cursor: 'pointer',
                  paddingBottom: '12px',
                  borderBottom: '1px solid #f0f0f0',
                  transition: 'all 0.3s'
                }"
                @click="viewKnowledge(item)"
                @mouseenter="(e) => setStyle(e, 'background', '#f5f5f5')"
                @mouseleave="(e) => setStyle(e, 'background', 'transparent')"
              >
                <h4 :style="{ 
                  margin: '0 0 8px 0', 
                  fontSize: '14px', 
                  fontWeight: 'bold', 
                  color: '#262626',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  display: '-webkit-box',
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: 'vertical'
                }">{{ item.title }}</h4>
                <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
                  <a-tag :color="getCategoryColor(item.category)" size="small">{{ item.category }}</a-tag>
                  <span :style="{ fontSize: '11px', color: '#8c8c8c' }">{{ item.updateDate }}</span>
                </div>
              </div>
            </div>
          </a-card>
        </div>
      </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { KnowledgeAPI, type KnowledgeInfo } from '@/api/knowledge'
import { FileAPI } from '@/api/file'

const router = useRouter()
const searchKeyword = ref('')
const activeCategory = ref('')
const loading = ref(false)

// 分类数据（简化为字符串数组）
const categories = ref<string[]>([])

// 扩展的知识条目类型（包含前端显示属性）
interface ExtendedKnowledgeItem extends KnowledgeInfo {
  category: string
  image: string
  updateDate: string
  rank?: number
  description?: string
  views?: number
  likes?: number
}

// 知识库数据
const knowledgeData = ref<ExtendedKnowledgeItem[]>([])

// 热门知识
const hotKnowledge = ref<ExtendedKnowledgeItem[]>([])

// 最新知识
const latestKnowledge = ref<ExtendedKnowledgeItem[]>([])

// 加载分类数据（从服务器获取）
async function loadCategories() {
  try {
    const categoryNames = await KnowledgeAPI.getCategories()
    console.log('从服务器获取的分类:', categoryNames)
    
    categories.value = categoryNames
    
    console.log('分类加载成功:', categories.value)
  } catch (error: any) {
    console.error('加载分类失败:', error)
    message.error(error.message || '加载分类失败')
    // 如果加载失败，使用默认分类
    categories.value = []
  }
}

// 加载知识库数据（按分类）
async function loadKnowledgeByCategory(categoryKey?: string) {
  try {
    loading.value = true
    const response = await KnowledgeAPI.getKnowledgeList({
      category: categoryKey,
      page: 1,
      size: 20
    })
    
    console.log('API响应:', response)
    
    // 处理分页响应数据 - PageResponse 的 data 字段包含记录列表
    const records = response.data || []
    knowledgeData.value = records.map((item: KnowledgeInfo) => ({
      ...item,
      title: item.title || item.name || '未命名',
      category: item.category || '未分类',
      image: item.coverImage || item.imageUrl || '',
      updateDate: formatDate(item.updatedAt || item.createdAt),
      description: item.description || item.content?.substring(0, 100) || '',
      views: item.viewCount || 0,
      likes: item.likeCount || 0
    }))
    
    console.log('知识数据加载成功:', knowledgeData.value.length, '条')
  } catch (error: any) {
    console.error('加载知识数据失败:', error)
    message.error(error.message || '加载知识数据失败')
  } finally {
    loading.value = false
  }
}

// 加载热门知识
async function loadHotKnowledge() {
  try {
    const response = await KnowledgeAPI.getKnowledgeList({
      page: 1,
      size: 10
    })
    
    const records = response.data || []
    // 按点赞数排序获取热门知识
    const sorted = [...records].sort((a: any, b: any) => (b.likeCount || 0) - (a.likeCount || 0))
    
    hotKnowledge.value = sorted.slice(0, 5).map((item: any, index: number) => ({
      ...item,
      title: item.title || item.name || '未命名',
      category: item.category || '未分类',
      image: item.coverImage || item.imageUrl || '',
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: formatDate(item.updatedAt || item.createdAt),
      rank: index + 1
    }))
    
    console.log('热门知识加载成功:', hotKnowledge.value.length, '条')
  } catch (error: any) {
    console.error('加载热门知识失败:', error)
  }
}

// 加载最新知识
async function loadLatestKnowledge() {
  try {
    const response = await KnowledgeAPI.getKnowledgeList({
      page: 1,
      size: 6
    })
    
    const records = response.data || []
    latestKnowledge.value = records.map((item: any) => ({
      ...item,
      title: item.title || item.name || '未命名',
      category: item.category || '未分类',
      image: item.coverImage || item.imageUrl || '',
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: formatDate(item.updatedAt || item.createdAt),
      description: item.description || item.content?.substring(0, 80) || ''
    }))
    
    console.log('最新知识加载成功:', latestKnowledge.value.length, '条')
  } catch (error: any) {
    console.error('加载最新知识失败:', error)
  }
}

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN')
  } catch {
    return dateStr.split(' ')[0] || ''
  }
}

// 根据分类获取知识
function getKnowledgeByCategory(categoryName: string): ExtendedKnowledgeItem[] {
  return knowledgeData.value.filter(item => item.category === categoryName)
}

// 获取分类颜色
function getCategoryColor(category: string | undefined) {
  if (!category) return 'default'
  const colors: Record<string, string> = {
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange',
    '交通工具': 'purple',
    '建筑': 'red',
    '物品': 'cyan',
    '自然': 'cyan',
    '工具': 'geekblue'
  }
  return colors[category] || 'default'
}

// 搜索功能
async function handleSearch() {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    message.warning('请输入搜索关键词')
    return
  }
  
  try {
    loading.value = true
    const response = await KnowledgeAPI.getKnowledgeList({
      keyword: keyword,
      page: 1,
      size: 20
    })
    
    const records = response.data || []
    knowledgeData.value = records.map((item: KnowledgeInfo) => ({
      ...item,
      title: item.title || item.name || '未命名',
      category: item.category || '未分类',
      image: item.coverImage || item.imageUrl || '',
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: formatDate(item.updatedAt || item.createdAt),
      description: item.description || item.content?.substring(0, 100) || ''
    }))
    
    // 切换到全部标签页显示搜索结果
    activeCategory.value = ''
    
    message.success(`找到 ${response.total || records.length} 条相关知识`)
  } catch (error: any) {
    console.error('搜索失败:', error)
    message.error(error.message || '搜索失败')
  } finally {
    loading.value = false
  }
}

function handleCategoryChange(categoryName: string) {
  activeCategory.value = categoryName
  // 如果是空字符串(全部),不传分类参数
  if (categoryName === '') {
    loadKnowledgeByCategory()
  } else {
    loadKnowledgeByCategory(categoryName)
  }
}

function selectCategory(categoryName: string) {
  activeCategory.value = categoryName
  handleCategoryChange(categoryName)
}

function viewKnowledge(item: any) {
  router.push(`/user/knowledge/${item.id}`)
}

// 辅助函数：安全设置元素样式
function setStyle(event: Event, property: string, value: string) {
  const target = event.currentTarget as HTMLElement
  if (target) {
    target.style.setProperty(property, value)
  }
}

// 页面加载时获取数据
onMounted(async () => {
  await loadCategories()
  await loadKnowledgeByCategory()
  await loadHotKnowledge()
  await loadLatestKnowledge()
})
</script>

