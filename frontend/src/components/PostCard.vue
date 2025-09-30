<template>
  <a-card class="post-card" hoverable>
    <!-- 帖子头部 -->
    <div class="post-header">
      <div class="post-author-info">
        <a-avatar :src="post.author.avatar" :size="36">
          {{ post.author.name.charAt(0) }}
        </a-avatar>
        <div class="author-details">
          <div class="author-meta">
            <span class="author-name">{{ post.author.name }}</span>
            <a-tag v-if="post.author.level" :color="getLevelColor(post.author.level)" size="small">
              {{ post.author.level }}
            </a-tag>
          </div>
          <div class="post-meta">
            <span class="post-time">{{ post.createTime }}</span>
            <a-tag :color="getTypeColor(post.type)" size="small">{{ getTypeName(post.type) }}</a-tag>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 帖子内容 -->
    <div class="post-content" @click="handleViewDetail">
      <h3 class="post-title">{{ post.title || '无标题' }}</h3>
      <p class="post-preview">
        {{ getContentPreview(post.content) }}
        <span v-if="post.content.length > 150" class="read-more">...阅读全文</span>
      </p>
    </div>
    
    <!-- 封面图片 -->
    <div v-if="post.images?.length" class="post-cover" @click="handlePreviewImage">
      <img :src="post.images[0]" alt="封面图片" />
    </div>
    
    <!-- 标签 -->
    <div v-if="post.tags?.length" class="post-tags">
      <a-tag 
        v-for="tag in post.tags.slice(0, 3)" 
        :key="tag"
        size="small"
        @click="handleSearchByTag(tag)"
      >
        {{ tag }}
      </a-tag>
      <span v-if="post.tags.length > 3" class="more-tags">+{{ post.tags.length - 3 }}</span>
    </div>
    
    <!-- 帖子操作 -->
    <div class="post-actions">
      <div class="action-stats">
        <span class="stat-item">
          <i class="fas fa-eye"></i>
          {{ post.views || 0 }} 浏览
        </span>
      </div>
      <div class="action-buttons">
        <a-button 
          type="text" 
          size="small"
          :class="{ 'active': post.isLiked }"
          @click="handleToggleLike"
        >
          <i class="fas fa-thumbs-up"></i>
          {{ post.likes }}
        </a-button>
        <a-button 
          type="text" 
          size="small"
          @click="handleReply"
        >
          <i class="fas fa-comment"></i>
          {{ post.replies }}
        </a-button>
        <a-button 
          type="text" 
          size="small"
          :class="{ 'active': post.isFavorited }"
          @click="handleToggleFavorite"
        >
          <i class="fas fa-heart"></i>
        </a-button>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue'

// Props
interface Props {
  post: {
    id: number
    title: string
    content: string
    author: {
      name: string
      avatar: string
      level: string
    }
    type: string
    createTime: string
    views: number
    likes: number
    replies: number
    isLiked: boolean
    isFavorited: boolean
    tags: string[]
    images: string[]
  }
}

const props = defineProps<Props>()

// Emits
interface Emits {
  (e: 'viewDetail', post: any): void
  (e: 'previewImage', images: string[], index: number): void
  (e: 'searchByTag', tag: string): void
  (e: 'toggleLike', post: any): void
  (e: 'toggleFavorite', post: any): void
  (e: 'reply', post: any): void
}

const emit = defineEmits<Emits>()

// Methods
function getContentPreview(content: string) {
  return content.length > 150 ? content.substring(0, 150) : content
}

function getPlaceholderImage() {
  const placeholders = [
    '/api/placeholder/400/240?text=AI识别',
    '/api/placeholder/400/240?text=技术分享',
    '/api/placeholder/400/240?text=讨论交流',
    '/api/placeholder/400/240?text=经验分享',
    '/api/placeholder/400/240?text=问题求助'
  ]
  return placeholders[Math.floor(Math.random() * placeholders.length)]
}

function getLevelColor(level: string) {
  const colors: Record<string, string> = {
    '新手': 'green',
    '资深用户': 'blue',
    '专家': 'purple',
    '管理员': 'red'
  }
  return colors[level] || 'default'
}

function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    'question': 'orange',
    'share': 'blue',
    'discussion': 'purple'
  }
  return colors[type] || 'default'
}

function getTypeName(type: string) {
  const names: Record<string, string> = {
    'question': '问答',
    'share': '分享',
    'discussion': '讨论'
  }
  return names[type] || '其他'
}

// Event handlers
function handleViewDetail() {
  emit('viewDetail', props.post)
}

function handlePreviewImage() {
  const images = props.post.images?.length ? props.post.images : [getPlaceholderImage()]
  emit('previewImage', images, 0)
}

function handleSearchByTag(tag: string) {
  emit('searchByTag', tag)
}

function handleToggleLike() {
  emit('toggleLike', props.post)
}

function handleToggleFavorite() {
  emit('toggleFavorite', props.post)
}

function handleReply() {
  emit('reply', props.post)
}
</script>

<style scoped>
.post-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.post-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.post-card :deep(.ant-card-body) {
  padding: 20px;
}

/* 帖子头部 */
.post-header {
  margin-bottom: 16px;
}

.post-author-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.author-details {
  flex: 1;
  min-width: 0;
}

.author-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.author-name {
  font-weight: 600;
  color: #262626;
  font-size: 14px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

/* 帖子内容 */
.post-content {
  margin-bottom: 16px;
  cursor: pointer;
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
  line-height: 1.4;
  transition: color 0.3s;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-content:hover .post-title {
  color: #1890ff;
}

.post-preview {
  color: #666;
  line-height: 1.5;
  margin: 0;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  color: #1890ff;
  font-weight: 500;
  margin-left: 4px;
}

/* 封面图片 */
.post-cover {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
}

.post-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-cover:hover img {
  transform: scale(1.02);
}

/* 标签 */
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
  min-height: 22px;
}

.post-tags :deep(.ant-tag) {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
  font-size: 12px;
}

.post-tags :deep(.ant-tag:hover) {
  border-color: #1890ff;
  color: #1890ff;
  background: rgba(24, 144, 255, 0.05);
}

.more-tags {
  color: #999;
  font-size: 12px;
  align-self: center;
}

/* 帖子操作 */
.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 12px;
}

.stat-item i {
  font-size: 12px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-buttons :deep(.ant-btn) {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  transition: all 0.3s ease;
  border-radius: 6px;
  padding: 4px 8px;
  height: auto;
  font-size: 12px;
}

.action-buttons :deep(.ant-btn:hover) {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.06);
}

.action-buttons :deep(.ant-btn.active) {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.06);
}

.action-buttons :deep(.ant-btn i) {
  font-size: 12px;
}

/* 收藏按钮特殊样式 */
.action-buttons :deep(.ant-btn.active:last-child) {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.06);
}

.action-buttons :deep(.ant-btn:last-child:hover) {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.06);
}
</style>
