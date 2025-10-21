<template>
  <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
    <div 
      v-for="post in posts" 
      :key="post.id"
      :style="{ 
        display: 'flex', 
        gap: '16px', 
        padding: '20px', 
        background: '#fafafa', 
        borderRadius: '8px',
        border: '1px solid #f0f0f0',
        cursor: 'pointer',
        transition: 'all 0.3s'
      }"
      @click="emit('view-post', post)"
      @mouseenter="(e) => { e.currentTarget.style.background = '#f5f5f5'; e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)' }"
      @mouseleave="(e) => { e.currentTarget.style.background = '#fafafa'; e.currentTarget.style.boxShadow = 'none' }"
    >
      <div :style="{ flex: 1 }">
        <h4 :style="{ fontSize: '16px', fontWeight: 'bold', color: '#262626', margin: '0 0 8px 0' }">{{ post.title }}</h4>
        <p :style="{ fontSize: '14px', color: '#8c8c8c', margin: '0 0 12px 0', lineHeight: '1.6' }">{{ post.excerpt }}</p>
        <div :style="{ display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }">
          <a-tag :color="getTypeColor(post.type)">
            {{ getTypeName(post.type) }}
          </a-tag>
          <span :style="{ fontSize: '13px', color: '#595959', display: 'flex', alignItems: 'center', gap: '12px' }">
            <span :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
              <i class="fas fa-thumbs-up" :style="{ color: '#1890ff' }"></i>
              {{ post.likes }}
            </span>
            <span :style="{ display: 'flex', alignItems: 'center', gap: '4px' }">
              <i class="fas fa-comment" :style="{ color: '#1890ff' }"></i>
              {{ post.replies }}
            </span>
          </span>
          <span :style="{ fontSize: '12px', color: '#8c8c8c', marginLeft: 'auto' }">{{ post.createTime }}</span>
        </div>
      </div>
      <div v-if="post.image" :style="{ width: '100px', height: '80px', flexShrink: 0, borderRadius: '6px', overflow: 'hidden' }">
        <img :src="post.image" :alt="post.title" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  posts: Array<{
    id: number
    title: string
    excerpt: string
    type: string
    likes: number
    replies: number
    createTime: string
    image?: string
  }>
}>()

const emit = defineEmits<{
  'view-post': [post: any]
}>()

function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    'share': 'blue',
    'question': 'orange',
    'discussion': 'purple'
  }
  return colors[type] || 'default'
}

function getTypeName(type: string) {
  const names: Record<string, string> = {
    'share': '分享',
    'question': '问答',
    'discussion': '讨论'
  }
  return names[type] || '其他'
}
</script>

