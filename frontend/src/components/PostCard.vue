<template>
  <div class="post-item">
    <!-- 帖子头部 -->
    <div class="post-header">
      <div class="post-author-info">
        <a-avatar :src="post.author.avatar">
          {{ post.author.name.charAt(0) }}
        </a-avatar>
        <div class="author-details">
          <span class="author-name">{{ post.author.name }}</span>
          <a-tag v-if="post.author.level" :color="getLevelColor(post.author.level)" size="small">
            {{ post.author.level }}
          </a-tag>
        </div>
      </div>
      <div class="post-meta-info">
        <span class="post-time">{{ post.createTime }}</span>
        <a-tag :color="getTypeColor(post.type)" size="small">{{ getTypeName(post.type) }}</a-tag>
      </div>
    </div>
    
    <!-- 帖子标题 -->
    <div class="post-title" @click="handleViewDetail">
      <h3>{{ post.title || '无标题' }}</h3>
    </div>
    
    <!-- 帖子内容预览 -->
    <div class="post-body">
      <p class="post-preview" @click="handleViewDetail">
        {{ getContentPreview(post.content) }}
        <span v-if="post.content.length > 150" class="read-more">...阅读全文</span>
      </p>
      <div class="post-images">
        <div 
          class="post-image"
          @click="handlePreviewImage"
        >
          <img :src="post.images?.length ? post.images[0] : getPlaceholderImage()" alt="图片" />
          <div v-if="post.images?.length > 1" class="more-images">
            +{{ post.images.length - 1 }}
          </div>
        </div>
      </div>
      <div v-if="post.tags?.length" class="post-tags">
        <a-tag 
          v-for="tag in post.tags.slice(0, 2)" 
          :key="tag"
          size="small"
          @click="handleSearchByTag(tag)"
        >
          {{ tag }}
        </a-tag>
        <span v-if="post.tags.length > 2" class="more-tags">+{{ post.tags.length - 2 }}</span>
      </div>
      
      <!-- 帖子操作 -->
      <div class="post-actions-inline">
        <div class="action-group">
          <button class="action-btn" :class="{ 'liked': post.isLiked }" @click="handleToggleLike">
            <i class="fas fa-thumbs-up"></i>
            <span>{{ post.likes }}</span>
          </button>
          <button class="action-btn" @click="handleReply">
            <i class="fas fa-comment"></i>
            <span>{{ post.replies }}</span>
          </button>
          <button class="action-btn" :class="{ 'favorited': post.isFavorited }" @click="handleToggleFavorite">
            <i class="fas fa-heart"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
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
.post-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: box-shadow 0.3s;
  height: 400px;
}

.post-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.post-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-meta-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 500;
  color: #262626;
  font-size: 14px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-title {
  margin-bottom: 12px;
  cursor: pointer;
}

.post-title h3 {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0;
  line-height: 1.3;
  transition: color 0.3s;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-title:hover h3 {
  color: #1890ff;
}

.post-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-preview {
  color: #666;
  line-height: 1.4;
  margin-bottom: 12px;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-preview:hover {
  color: #1890ff;
}

.read-more {
  color: #1890ff;
  font-weight: 500;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
  min-height: 24px;
}

.post-tags :deep(.ant-tag) {
  cursor: pointer;
  transition: all 0.3s;
}

.post-tags :deep(.ant-tag:hover) {
  border-color: #1890ff;
  color: #1890ff;
}

.more-tags {
  color: #999;
  font-size: 12px;
}

.post-images {
  margin-top: auto;
  margin-bottom: 0;
}

.post-image {
  position: relative;
  width: 100%;
  height: 140px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.post-image:hover img {
  transform: scale(1.05);
}

.more-images {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: 500;
}

.post-actions-inline {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f9fa;
  border-radius: 20px;
  padding: 8px 16px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: transparent;
  border: none;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
  min-width: 45px;
  justify-content: center;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
  transform: translateY(-1px);
}

.action-btn.liked {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.action-btn.liked:hover {
  background: rgba(24, 144, 255, 0.2);
}

.action-btn.favorited {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.action-btn.favorited:hover {
  background: rgba(255, 77, 79, 0.2);
}

.action-btn i {
  font-size: 14px;
}

.action-btn span {
  font-weight: 500;
}
</style>
