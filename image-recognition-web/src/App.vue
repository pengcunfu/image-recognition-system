<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { ConfigProvider } from 'ant-design-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 初始化：如果有 token 但没有完整用户信息，则自动获取
onMounted(() => {
  if (userStore.token && !userStore.userInfo) {
    userStore.fetchUserProfile().catch(() => {
      // 如果获取失败（token 可能已过期），清除登录状态
      userStore.clearUserInfo()
    })
  }
})
</script>

<template>
  <ConfigProvider :locale="zhCN">
    <div :style="{ width: '100%', minHeight: '100vh', margin: 0, padding: 0 }">
      <RouterView />
    </div>
  </ConfigProvider>
</template>
