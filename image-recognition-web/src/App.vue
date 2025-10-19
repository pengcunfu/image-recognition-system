<script setup lang="ts">
import { RouterView } from 'vue-router'
import { ConfigProvider } from 'ant-design-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
</script>

<template>
  <ConfigProvider :locale="zhCN">
    <div :style="{ width: '100%', minHeight: '100vh', margin: 0, padding: 0 }">
      <RouterView />
    </div>
  </ConfigProvider>
</template>
