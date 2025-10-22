<template>
  <a-empty v-if="posts.length === 0" description="暂无发布的帖子" />
  <div v-else :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
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
        transition: 'all 0.3s',
        position: 'relative'
      }"
    >
      <!-- 内容区域 -->
      <div 
        :style="{ flex: 1, cursor: 'pointer' }"
        @click="emit('view-post', post)"
      >
        <div :style="{ display: 'flex', alignItems: 'flex-start', gap: '8px', marginBottom: '8px' }">
          <h4 :style="{ flex: 1, fontSize: '16px', fontWeight: 'bold', color: '#262626', margin: 0 }">{{ post.title }}</h4>
          <a-tag v-if="post.status === 0" color="orange">待审核</a-tag>
          <a-tag v-else-if="post.status === 2" color="red">已隐藏</a-tag>
        </div>
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

      <!-- 缩略图 -->
      <div v-if="post.image" :style="{ width: '100px', height: '80px', flexShrink: 0, borderRadius: '6px', overflow: 'hidden' }">
        <img :src="post.image" :alt="post.title" :style="{ width: '100%', height: '100%', objectFit: 'cover' }" />
      </div>

      <!-- 操作按钮 -->
      <div :style="{ position: 'absolute', top: '16px', right: '16px' }">
        <a-dropdown :trigger="['click']">
          <a-button size="small" type="text" @click.stop>
            <i class="fas fa-ellipsis-v"></i>
          </a-button>
          <template #overlay>
            <a-menu @click="handleAction($event, post)">
              <a-menu-item key="edit">
                <i class="fas fa-edit" :style="{ marginRight: '8px' }"></i>
                编辑
              </a-menu-item>
              <a-menu-item v-if="post.status === 1" key="hide">
                <i class="fas fa-eye-slash" :style="{ marginRight: '8px' }"></i>
                隐藏
              </a-menu-item>
              <a-menu-item v-if="post.status === 2" key="show">
                <i class="fas fa-eye" :style="{ marginRight: '8px' }"></i>
                公开
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="delete" danger>
                <i class="fas fa-trash" :style="{ marginRight: '8px' }"></i>
                删除
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
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
    status?: number
  }>
}>()

const emit = defineEmits<{
  'view-post': [post: any]
  'edit-post': [post: any]
  'delete-post': [post: any]
  'hide-post': [post: any]
  'show-post': [post: any]
}>()

function handleAction({ key }: { key: string }, post: any) {
  switch (key) {
    case 'edit':
      emit('edit-post', post)
      break
    case 'delete':
      emit('delete-post', post)
      break
    case 'hide':
      emit('hide-post', post)
      break
    case 'show':
      emit('show-post', post)
      break
  }
}

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

