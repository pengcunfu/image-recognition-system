<template>
  <div>
    <!-- 成就徽章 -->
    <a-card title="成就徽章" :style="{ marginBottom: '16px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }">
      <div :style="{ display: 'grid', gridTemplateColumns: '1fr', gap: '12px' }">
        <div 
          v-for="badge in badges" 
          :key="badge.id"
          :style="{ 
            display: 'flex', 
            gap: '12px', 
            padding: '12px', 
            background: badge.earned ? '#e6f7ff' : '#f5f5f5', 
            borderRadius: '6px',
            border: `1px solid ${badge.earned ? '#91d5ff' : '#d9d9d9'}`,
            opacity: badge.earned ? 1 : 0.6
          }"
        >
          <div :style="{ 
            width: '40px', 
            height: '40px', 
            borderRadius: '50%', 
            background: badge.earned ? '#1890ff' : '#8c8c8c', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            flexShrink: 0,
            color: 'white'
          }">
            <i :class="badge.icon" :style="{ fontSize: '20px' }"></i>
          </div>
          <div :style="{ flex: 1 }">
            <div :style="{ fontSize: '14px', fontWeight: 'bold', color: '#262626', marginBottom: '2px' }">{{ badge.name }}</div>
            <div :style="{ fontSize: '12px', color: '#8c8c8c' }">{{ badge.description }}</div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 最近访客 -->
    <a-card title="最近访客" :style="{ marginBottom: '16px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }">
      <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
        <div 
          v-for="visitor in recentVisitors" 
          :key="visitor.id"
          :style="{ display: 'flex', gap: '12px', alignItems: 'center' }"
        >
          <a-avatar :src="visitor.avatar" size="small" :style="{ background: '#1890ff' }">
            {{ visitor.name.charAt(0) }}
          </a-avatar>
          <div :style="{ flex: 1 }">
            <div :style="{ fontSize: '14px', color: '#262626' }">{{ visitor.name }}</div>
            <div :style="{ fontSize: '12px', color: '#8c8c8c' }">{{ visitor.visitTime }}</div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 数据统计 -->
    <a-card title="数据统计" :style="{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }">
      <div :style="{ 
        height: '200px', 
        background: '#fafafa', 
        borderRadius: '6px', 
        display: 'flex', 
        flexDirection: 'column',
        alignItems: 'center', 
        justifyContent: 'center',
        gap: '8px',
        color: '#8c8c8c'
      }">
        <i class="fas fa-chart-line" :style="{ fontSize: '48px', color: '#d9d9d9' }"></i>
        <span :style="{ fontSize: '14px' }">本月识别趋势图</span>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  badges: Array<{
    id: number
    name: string
    description: string
    icon: string
    earned: boolean
  }>
  recentVisitors: Array<{
    id: number
    name: string
    avatar: string
    visitTime: string
  }>
}>()
</script>

