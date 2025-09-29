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
    <a-card class="category-card">
      <a-tabs v-model:activeKey="activeCategory" @change="handleCategoryChange">
        <a-tab-pane key="all" tab="全部">
          <div class="category-content">
            <div class="category-grid">
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
                  <div class="category-count">{{ category.count }} 条目</div>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="animals" tab="动物">
          <div class="knowledge-list">
            <div 
              v-for="item in getKnowledgeByCategory('动物')" 
              :key="item.id"
              class="knowledge-item"
              @click="viewKnowledge(item)"
            >
              <div class="knowledge-image">
                <img :src="item.image" :alt="item.title" />
              </div>
              <div class="knowledge-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.description }}</p>
                <div class="knowledge-meta">
                  <a-tag color="blue">{{ item.category }}</a-tag>
                  <span class="view-count">
                    <i class="fas fa-eye"></i>
                    {{ item.views }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="plants" tab="植物">
          <div class="knowledge-list">
            <div 
              v-for="item in getKnowledgeByCategory('植物')" 
              :key="item.id"
              class="knowledge-item"
              @click="viewKnowledge(item)"
            >
              <div class="knowledge-image">
                <img :src="item.image" :alt="item.title" />
              </div>
              <div class="knowledge-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.description }}</p>
                <div class="knowledge-meta">
                  <a-tag color="green">{{ item.category }}</a-tag>
                  <span class="view-count">
                    <i class="fas fa-eye"></i>
                    {{ item.views }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="food" tab="食物">
          <div class="knowledge-list">
            <div 
              v-for="item in getKnowledgeByCategory('食物')" 
              :key="item.id"
              class="knowledge-item"
              @click="viewKnowledge(item)"
            >
              <div class="knowledge-image">
                <img :src="item.image" :alt="item.title" />
              </div>
              <div class="knowledge-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.description }}</p>
                <div class="knowledge-meta">
                  <a-tag color="orange">{{ item.category }}</a-tag>
                  <span class="view-count">
                    <i class="fas fa-eye"></i>
                    {{ item.views }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 热门推荐 -->
    <a-card title="热门推荐" class="hot-card">
      <div class="hot-list">
        <div 
          v-for="item in hotKnowledge" 
          :key="item.id"
          class="hot-item"
          @click="viewKnowledge(item)"
        >
          <div class="hot-rank">{{ item.rank }}</div>
          <div class="hot-image">
            <img :src="item.image" :alt="item.title" />
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
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :lg="8" v-for="item in latestKnowledge" :key="item.id">
          <div class="latest-item" @click="viewKnowledge(item)">
            <div class="latest-image">
              <img :src="item.image" :alt="item.title" />
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
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'

const searchKeyword = ref('')
const activeCategory = ref('all')

// 分类数据
const categories = ref([
  { key: 'animals', name: '动物', icon: 'fas fa-paw', count: 245 },
  { key: 'plants', name: '植物', icon: 'fas fa-leaf', count: 189 },
  { key: 'food', name: '食物', icon: 'fas fa-apple-alt', count: 156 },
  { key: 'vehicles', name: '交通工具', icon: 'fas fa-car', count: 98 },
  { key: 'buildings', name: '建筑', icon: 'fas fa-building', count: 87 },
  { key: 'objects', name: '物品', icon: 'fas fa-cube', count: 234 }
])

// 知识库数据
const knowledgeData = ref([
  {
    id: 1,
    title: '金毛寻回犬',
    category: '动物',
    description: '金毛寻回犬是一种大型犬，以其友善、聪明和对家庭的忠诚而闻名。它们拥有金黄色的毛发，是理想的宠物和工作犬。',
    image: '/api/placeholder/300/200',
    views: 1245,
    likes: 89,
    updateDate: '2024-03-20'
  },
  {
    id: 2,
    title: '玫瑰花',
    category: '植物',
    description: '玫瑰是蔷薇科蔷薇属的植物，以其美丽的花朵和芬芳的香气而闻名。它们是爱情和美丽的象征。',
    image: '/api/placeholder/300/200',
    views: 987,
    likes: 156,
    updateDate: '2024-03-19'
  },
  {
    id: 3,
    title: '苹果',
    category: '食物',
    description: '苹果是一种营养丰富的水果，含有丰富的维生素C和纤维。有句话说"一天一苹果，医生远离我"。',
    image: '/api/placeholder/300/200',
    views: 756,
    likes: 67,
    updateDate: '2024-03-18'
  },
  {
    id: 4,
    title: '波斯猫',
    category: '动物',
    description: '波斯猫以其长毛和平静的性格而闻名，是世界上最受欢迎的猫品种之一。',
    image: '/api/placeholder/300/200',
    views: 654,
    likes: 78,
    updateDate: '2024-03-17'
  },
  {
    id: 5,
    title: '向日葵',
    category: '植物',
    description: '向日葵是一种高大的一年生植物，以其巨大的黄色花朵而闻名，象征着忠诚和热情。',
    image: '/api/placeholder/300/200',
    views: 543,
    likes: 92,
    updateDate: '2024-03-16'
  },
  {
    id: 6,
    title: '巧克力',
    category: '食物',
    description: '巧克力是由可可豆制成的甜食，含有天然的抗氧化剂，适量食用对健康有益。',
    image: '/api/placeholder/300/200',
    views: 432,
    likes: 55,
    updateDate: '2024-03-15'
  }
])

// 热门知识
const hotKnowledge = computed(() => {
  return knowledgeData.value
    .sort((a, b) => b.views - a.views)
    .slice(0, 5)
    .map((item, index) => ({
      ...item,
      rank: index + 1
    }))
})

// 最新知识
const latestKnowledge = computed(() => {
  return knowledgeData.value
    .sort((a, b) => new Date(b.updateDate).getTime() - new Date(a.updateDate).getTime())
    .slice(0, 6)
})

// 根据分类获取知识
function getKnowledgeByCategory(category: string) {
  return knowledgeData.value.filter(item => item.category === category)
}

// 获取分类颜色
function getCategoryColor(category: string) {
  const colors: Record<string, string> = {
    '动物': 'green',
    '植物': 'blue',
    '食物': 'orange',
    '交通工具': 'purple',
    '建筑': 'red',
    '物品': 'cyan'
  }
  return colors[category] || 'default'
}

// 方法
function handleSearch() {
  if (!searchKeyword.value.trim()) {
    message.warning('请输入搜索关键词')
    return
  }
  message.info(`搜索：${searchKeyword.value}`)
}

function handleCategoryChange(key: string) {
  activeCategory.value = key
}

function selectCategory(key: string) {
  activeCategory.value = key
}

function viewKnowledge(item: any) {
  message.info(`查看知识：${item.title}`)
}
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
