<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { ConfigProvider } from 'ant-design-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 初始化用户信息
onMounted(() => {
  // 从localStorage加载token并获取用户信息
  userStore.initFromStorage()
})
</script>

<template>
  <ConfigProvider :locale="zhCN">
    <div :style="{ width: '100%', minHeight: '100vh', margin: 0, padding: 0 }">
      <RouterView />
    </div>
  </ConfigProvider>
</template>
