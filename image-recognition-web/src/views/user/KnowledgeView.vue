<template>
  <div class="knowledge-page">
    <div class="page-header">
      <h1>知识库</h1>
      <p>探索丰富的图像识别知识百科</p>
    </div>

    <!-- 搜索区域 -->
    <a-card class="search-card">
      <a-input-search
        v-model:value="searchKeyword"
        placeholder="搜索知识内容..."
        size="large"
        @search="handleSearch"
        class="search-input"
      >
        <template #enterButton>
          <a-button type="primary">
            <i class="fas fa-search"></i>
            搜索
          </a-button>
        </template>
      </a-input-search>
    </a-card>

    <!-- 分类导航 -->
    <a-card class="category-card" :loading="loading && categories.length === 0">
      <a-tabs v-model:activeKey="activeCategory" @change="handleCategoryChange">
        <a-tab-pane key="all" tab="全部">
          <div class="category-content">
            <a-empty v-if="categories.length === 0" description="暂无分类数据" />
            <div v-else class="category-grid">
              <div 
                v-for="category in categories" 
                :key="category.key"
                class="category-item"
                @click="selectCategory(category.key)"
              >
                <div class="category-icon">
                  <i :class="category.icon"></i>
                </div>
                <div class="category-info">
                  <div class="category-name">{{ category.name }}</div>
                  <div class="category-count">{{ category.itemCount }} 条目</div>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane 
          v-for="cat in categories.filter(c => c.key !== 'all')" 
          :key="cat.key" 
          :tab="cat.name"
        >
          <a-spin :spinning="loading">
            <a-empty v-if="getKnowledgeByCategory(cat.name).length === 0" description="暂无知识条目" />
            <div v-else class="knowledge-list">
              <div 
                v-for="item in getKnowledgeByCategory(cat.name)" 
                :key="item.id"
                class="knowledge-item"
                @click="viewKnowledge(item)"
              >
                <div class="knowledge-image">
                  <img :src="item.image || '/api/placeholder/120/80'" :alt="item.title" />
                </div>
                <div class="knowledge-content">
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.description }}</p>
                  <div class="knowledge-meta">
                    <a-tag :color="getCategoryColor(item.category)">{{ item.category }}</a-tag>
                    <span class="view-count">
                      <i class="fas fa-eye"></i>
                      {{ item.views }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </a-spin>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 热门推荐 -->
    <a-card title="热门推荐" class="hot-card">
      <a-empty v-if="hotKnowledge.length === 0" description="暂无热门知识" />
      <div v-else class="hot-list">
        <div 
          v-for="item in hotKnowledge" 
          :key="item.id"
          class="hot-item"
          @click="viewKnowledge(item)"
        >
          <div class="hot-rank">{{ item.rank }}</div>
          <div class="hot-image">
            <img :src="item.image || '/api/placeholder/60/40'" :alt="item.title" />
          </div>
          <div class="hot-content">
            <div class="hot-title">{{ item.title }}</div>
            <div class="hot-meta">
              <span class="hot-views">{{ item.views }} 浏览</span>
              <span class="hot-likes">{{ item.likes }} 点赞</span>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 最新更新 -->
    <a-card title="最新更新" class="latest-card">
      <a-empty v-if="latestKnowledge.length === 0" description="暂无最新知识" />
      <a-row v-else :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :lg="8" v-for="item in latestKnowledge" :key="item.id">
          <div class="latest-item" @click="viewKnowledge(item)">
            <div class="latest-image">
              <img :src="item.image || '/api/placeholder/300/150'" :alt="item.title" />
              <div class="latest-overlay">
                <i class="fas fa-book-open"></i>
              </div>
            </div>
            <div class="latest-content">
              <h4>{{ item.title }}</h4>
              <p>{{ item.description }}</p>
              <div class="latest-meta">
                <a-tag :color="getCategoryColor(item.category)">
                  {{ item.category }}
                </a-tag>
                <span class="latest-date">{{ item.updateDate }}</span>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { KnowledgeAPI } from '@/api/knowledge'
import { FileAPI } from '@/api/file'
import type { KnowledgeCategory, KnowledgeItem } from '@/api/types'
import { CategoryStatus } from '@/api/types'

const router = useRouter()
const searchKeyword = ref('')
const activeCategory = ref('all')
const loading = ref(false)

// 分类数据
const categories = ref<Array<KnowledgeCategory & { icon: string }>>([])

// 扩展的知识条目类型（包含前端显示属性）
interface ExtendedKnowledgeItem extends KnowledgeItem {
  title: string
  category: string
  image: string
  views: number
  likes: number
  updateDate: string
  rank?: number
}

// 知识库数据
const knowledgeData = ref<ExtendedKnowledgeItem[]>([])

// 热门知识
const hotKnowledge = ref<ExtendedKnowledgeItem[]>([])

// 最新知识
const latestKnowledge = ref<ExtendedKnowledgeItem[]>([])

// 分类图标映射
const categoryIcons: Record<string, string> = {
  'animals': 'fas fa-paw',
  'plants': 'fas fa-leaf',
  'food': 'fas fa-apple-alt',
  'vehicles': 'fas fa-car',
  'buildings': 'fas fa-building',
  'objects': 'fas fa-cube',
  'nature': 'fas fa-tree',
  'tools': 'fas fa-wrench'
}

// 加载分类数据
async function loadCategories() {
  try {
    const response = await KnowledgeAPI.getCategories(CategoryStatus.ACTIVE)
    categories.value = response.data.map(cat => ({
      ...cat,
      icon: categoryIcons[cat.key] || 'fas fa-folder'
    }))
    console.log('分类加载成功:', categories.value)
  } catch (error) {
    console.error('加载分类失败:', error)
    message.error('加载分类失败')
  }
}

// 加载知识库数据（按分类）
async function loadKnowledgeByCategory(categoryKey?: string) {
  try {
    loading.value = true
    const response = await KnowledgeAPI.getItems({
      category: categoryKey,
      page: 1,
      size: 20
    })
    
    // 转换数据格式以适配原有模板
    knowledgeData.value = response.data.items.map(item => ({
      ...item,
      title: item.name,
      category: getCategoryNameByItem(item),
      image: FileAPI.getFirstImage(item.images),
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: item.updateTime.split(' ')[0] // 只取日期部分
    }))
    
    console.log('知识数据加载成功:', knowledgeData.value)
  } catch (error) {
    console.error('加载知识数据失败:', error)
    message.error('加载知识数据失败')
  } finally {
    loading.value = false
  }
}

// 加载热门知识
async function loadHotKnowledge() {
  try {
    const response = await KnowledgeAPI.getPopularItems(5)
    hotKnowledge.value = response.data.map((item, index) => ({
      ...item,
      title: item.name,
      category: getCategoryNameByItem(item),
      image: FileAPI.getFirstImage(item.images),
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: item.updateTime.split(' ')[0],
      rank: index + 1
    }))
    console.log('热门知识加载成功:', hotKnowledge.value)
  } catch (error) {
    console.error('加载热门知识失败:', error)
  }
}

// 加载最新知识
async function loadLatestKnowledge() {
  try {
    const response = await KnowledgeAPI.getLatestItems(6)
    latestKnowledge.value = response.data.map(item => ({
      ...item,
      title: item.name,
      category: getCategoryNameByItem(item),
      image: FileAPI.getFirstImage(item.images),
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: item.updateTime.split(' ')[0]
    }))
    console.log('最新知识加载成功:', latestKnowledge.value)
  } catch (error) {
    console.error('加载最新知识失败:', error)
  }
}

// 根据知识条目获取分类名称
function getCategoryNameByItem(item: KnowledgeItem): string {
  const category = categories.value.find(cat => cat.id === item.categoryId)
  return category?.name || '未分类'
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

// 方法
async function handleSearch() {
  if (!searchKeyword.value.trim()) {
    message.warning('请输入搜索关键词')
    return
  }
  
  try {
    loading.value = true
    const response = await KnowledgeAPI.searchItems({
      keyword: searchKeyword.value,
      page: 1,
      size: 20
    })
    
    knowledgeData.value = response.data.items.map(item => ({
      ...item,
      title: item.name,
      category: getCategoryNameByItem(item),
      image: FileAPI.getFirstImage(item.images),
      views: item.viewCount || 0,
      likes: item.likeCount || 0,
      updateDate: item.updateTime.split(' ')[0]
    }))
    
    message.success(`找到 ${response.data.total} 条相关知识`)
  } catch (error) {
    console.error('搜索失败:', error)
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

function handleCategoryChange(key: string) {
  activeCategory.value = key
  if (key === 'all') {
    loadKnowledgeByCategory()
  } else {
    loadKnowledgeByCategory(key)
  }
}

function selectCategory(key: string) {
  activeCategory.value = key
  handleCategoryChange(key)
}

function viewKnowledge(item: any) {
  router.push(`/user/knowledge/${item.id}`)
}

// 页面加载时获取数据
onMounted(async () => {
  await loadCategories()
  await loadKnowledgeByCategory()
  await loadHotKnowledge()
  await loadLatestKnowledge()
})
</script>

