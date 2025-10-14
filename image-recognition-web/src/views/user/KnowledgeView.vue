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

<style scoped>
.knowledge-page {
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

/* 搜索区域 */
.search-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.search-input {
  max-width: 600px;
  margin: 0 auto;
  display: block;
}

/* 分类卡片 */
.category-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background: #fafafa;
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1890ff, #40a9ff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.category-name {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.category-count {
  font-size: 14px;
  color: #666;
}

/* 知识列表 */
.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.knowledge-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.knowledge-item:hover {
  background: #fafafa;
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.knowledge-image {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.knowledge-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.knowledge-content {
  flex: 1;
}

.knowledge-content h3 {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.knowledge-content p {
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.view-count {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 热门推荐 */
.hot-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.hot-item:hover {
  background: #fafafa;
}

.hot-rank {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.hot-image {
  width: 60px;
  height: 40px;
  border-radius: 6px;
  overflow: hidden;
}

.hot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hot-content {
  flex: 1;
}

.hot-title {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.hot-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 最新更新 */
.latest-card {
  border-radius: 12px;
}

.latest-item {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.latest-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.latest-image {
  position: relative;
  height: 150px;
  overflow: hidden;
}

.latest-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.latest-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  opacity: 0;
  transition: opacity 0.3s;
}

.latest-item:hover .latest-overlay {
  opacity: 1;
}

.latest-content {
  padding: 16px;
}

.latest-content h4 {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.latest-content p {
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.latest-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.latest-date {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: 1fr;
  }
  
  .knowledge-item {
    flex-direction: column;
  }
  
  .knowledge-image {
    width: 100%;
    height: 120px;
  }
  
  .hot-item {
    flex-wrap: wrap;
  }
}

@media (max-width: 576px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .category-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>
