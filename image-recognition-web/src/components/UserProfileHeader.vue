<template>
  <div :style="{ background: 'white', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', marginBottom: '24px', padding: '32px' }">
    <div :style="{ display: 'flex', alignItems: 'flex-start', gap: '32px', flexWrap: 'wrap' }">
      <!-- 头像区域 -->
      <div :style="{ position: 'relative' }">
        <div :style="{ position: 'relative', cursor: 'pointer' }" @click="emit('update-avatar')">
          <a-avatar :size="120" :src="userInfo.avatar" :style="{ background: '#1890ff', fontSize: '48px' }">
            {{ (userInfo.name || 'U').charAt(0) }}
          </a-avatar>
          <div :style="{ 
            position: 'absolute', 
            bottom: 0, 
            right: 0, 
            width: '36px', 
            height: '36px', 
            borderRadius: '50%', 
            background: '#1890ff', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            color: 'white',
            fontSize: '16px',
            border: '3px solid white'
          }">
            <i class="fas fa-camera"></i>
          </div>
        </div>
      </div>
      
      <!-- 用户信息 -->
      <div :style="{ flex: 1, minWidth: '300px' }">
        <h1 :style="{ fontSize: '28px', fontWeight: 'bold', margin: '0 0 12px 0', color: '#262626' }">{{ userInfo.name }}</h1>
        <p :style="{ fontSize: '14px', color: '#8c8c8c', margin: '0 0 16px 0', lineHeight: '1.6' }">{{ userInfo.bio }}</p>
        <div :style="{ display: 'flex', gap: '24px', flexWrap: 'wrap' }">
          <span :style="{ fontSize: '14px', color: '#595959', display: 'flex', alignItems: 'center', gap: '6px' }">
            <i class="fas fa-calendar" :style="{ color: '#1890ff' }"></i>
            加入于 {{ userInfo.joinDate }}
          </span>
          <span :style="{ fontSize: '14px', color: '#595959', display: 'flex', alignItems: 'center', gap: '6px' }">
            <i class="fas fa-map-marker-alt" :style="{ color: '#1890ff' }"></i>
            {{ userInfo.location }}
          </span>
        </div>
      </div>
      
      <!-- 统计数据 -->
      <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '24px', minWidth: '400px' }">
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '8px' }">{{ userStats.recognitions }}</div>
          <div :style="{ fontSize: '14px', color: '#8c8c8c' }">识别次数</div>
        </div>
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '8px' }">{{ userStats.posts }}</div>
          <div :style="{ fontSize: '14px', color: '#8c8c8c' }">发布帖子</div>
        </div>
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '8px' }">{{ userStats.likes }}</div>
          <div :style="{ fontSize: '14px', color: '#8c8c8c' }">获得点赞</div>
        </div>
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '8px' }">{{ userStats.followers }}</div>
          <div :style="{ fontSize: '14px', color: '#8c8c8c' }">粉丝</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  userInfo: {
    name: string
    bio: string
    avatar: string
    joinDate: string
    location: string
  }
  userStats: {
    recognitions: number
    posts: number
    likes: number
    followers: number
  }
}>()

const emit = defineEmits<{
  'update-avatar': []
}>()
</script>

