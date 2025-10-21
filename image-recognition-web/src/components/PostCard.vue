<template>
  <a-card 
    hoverable
    :style="{ 
      borderRadius: '16px', 
      overflow: 'hidden', 
      transition: 'all 0.3s ease', 
      border: '1px solid #e8e8e8',
      boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
      background: 'white'
    }"
  >
    <div :style="{ padding: '20px' }">
      <!-- 帖子头部 -->
      <div :style="{ marginBottom: '16px' }">
        <div :style="{ display: 'flex', alignItems: 'flex-start', gap: '12px' }">
          <a-avatar :src="post.author.avatar" :size="36">
            {{ post.author.name.charAt(0) }}
          </a-avatar>
          <div :style="{ flex: 1, minWidth: 0 }">
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }">
              <span :style="{ fontWeight: 600, color: '#262626', fontSize: '14px' }">{{ post.author.name }}</span>
              <a-tag v-if="post.author.level" :color="getLevelColor(post.author.level)" size="small">
                {{ post.author.level }}
              </a-tag>
            </div>
            <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
              <span :style="{ fontSize: '12px', color: '#999' }">{{ post.createTime }}</span>
              <a-tag :color="getTypeColor(post.type)" size="small">{{ getTypeName(post.type) }}</a-tag>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 帖子内容 -->
      <div 
        :style="{ marginBottom: '16px', cursor: 'pointer' }"
        @click="handleViewDetail"
      >
        <h3 :style="{ 
          fontSize: '16px', 
          fontWeight: 600, 
          color: '#262626', 
          margin: '0 0 8px 0', 
          lineHeight: '1.4', 
          transition: 'color 0.3s',
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden'
        }">{{ post.title || '无标题' }}</h3>
        <p :style="{ 
          color: '#666', 
          lineHeight: '1.5', 
          margin: 0, 
          fontSize: '14px',
          display: '-webkit-box',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden'
        }">
          {{ getContentPreview(post.content) }}
          <span v-if="post.content.length > 150" :style="{ color: '#1890ff', fontWeight: 500, marginLeft: '4px' }">...阅读全文</span>
        </p>
      </div>
      
      <!-- 封面图片 -->
      <div 
        v-if="post.images?.length" 
        :style="{ 
          marginBottom: '16px', 
          borderRadius: '8px', 
          overflow: 'hidden', 
          cursor: 'pointer',
          width: '100%',
          aspectRatio: '16/9'
        }"
        @click="handlePreviewImage"
      >
        <img 
          :src="post.images[0]" 
          alt="封面图片" 
          :style="{ 
            width: '100%', 
            height: '100%', 
            objectFit: 'cover', 
            transition: 'transform 0.3s ease' 
          }"
        />
      </div>
      
      <!-- 标签 -->
      <div 
        v-if="post.tags?.length" 
        :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginBottom: '16px', minHeight: '22px' }"
      >
        <a-tag 
          v-for="tag in post.tags.slice(0, 3)" 
          :key="tag"
          size="small"
          :style="{ cursor: 'pointer', transition: 'all 0.3s', borderRadius: '12px', fontSize: '12px' }"
          @click="handleSearchByTag(tag)"
        >
          {{ tag }}
        </a-tag>
        <span v-if="post.tags.length > 3" :style="{ color: '#999', fontSize: '12px', alignSelf: 'center' }">+{{ post.tags.length - 3 }}</span>
      </div>
      
      <!-- 帖子操作 -->
      <div :style="{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        paddingTop: '16px', 
        borderTop: '1px solid #f0f0f0' 
      }">
        <div :style="{ display: 'flex', alignItems: 'center', gap: '16px' }">
          <span :style="{ display: 'flex', alignItems: 'center', gap: '4px', color: '#999', fontSize: '12px' }">
            <EyeOutlined />
            {{ post.views || 0 }} 浏览
          </span>
        </div>
        <div :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
          <a-button 
            type="text" 
            size="small"
            :style="{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '4px', 
              color: post.isLiked ? '#1890ff' : '#666',
              background: post.isLiked ? 'rgba(24, 144, 255, 0.06)' : 'transparent',
              transition: 'all 0.3s ease',
              borderRadius: '6px',
              padding: '4px 8px',
              height: 'auto',
              fontSize: '12px'
            }"
            @click="handleToggleLike"
          >
            <LikeOutlined />
            {{ post.likes }}
          </a-button>
          <a-button 
            type="text" 
            size="small"
            :style="{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '4px', 
              color: '#666',
              transition: 'all 0.3s ease',
              borderRadius: '6px',
              padding: '4px 8px',
              height: 'auto',
              fontSize: '12px'
            }"
            @click="handleReply"
          >
            <CommentOutlined />
            {{ post.replies }}
          </a-button>
          <a-button 
            type="text" 
            size="small"
            :style="{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '4px', 
              color: post.isFavorited ? '#ff4d4f' : '#666',
              background: post.isFavorited ? 'rgba(255, 77, 79, 0.06)' : 'transparent',
              transition: 'all 0.3s ease',
              borderRadius: '6px',
              padding: '4px 8px',
              height: 'auto',
              fontSize: '12px'
            }"
            @click="handleToggleFavorite"
          >
            <HeartOutlined />
          </a-button>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { EyeOutlined, LikeOutlined, CommentOutlined, HeartOutlined } from '@ant-design/icons-vue'

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
