<template>
  <div :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
    <div 
      v-for="activity in activities" 
      :key="activity.id"
      :style="{ 
        display: 'flex', 
        gap: '16px', 
        padding: '16px', 
        background: '#fafafa', 
        borderRadius: '8px',
        border: '1px solid #f0f0f0',
        transition: 'all 0.3s'
      }"
      @mouseenter="(e) => e.currentTarget.style.background = '#f5f5f5'"
      @mouseleave="(e) => e.currentTarget.style.background = '#fafafa'"
    >
      <div :style="{ 
        width: '40px', 
        height: '40px', 
        borderRadius: '50%', 
        background: '#e6f7ff', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        flexShrink: 0
      }">
        <i :class="getActivityIcon(activity.type)" :style="{ color: '#1890ff', fontSize: '18px' }"></i>
      </div>
      <div :style="{ flex: 1 }">
        <div :style="{ fontSize: '14px', color: '#262626', marginBottom: '4px' }">{{ activity.description }}</div>
        <div :style="{ fontSize: '12px', color: '#8c8c8c' }">{{ activity.time }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  activities: Array<{
    id: number
    type: string
    description: string
    time: string
  }>
}>()

function getActivityIcon(type: string) {
  const icons: Record<string, string> = {
    'recognition': 'fas fa-eye',
    'post': 'fas fa-edit',
    'like': 'fas fa-thumbs-up',
    'follow': 'fas fa-user-plus'
  }
  return icons[type] || 'fas fa-circle'
}
</script>

