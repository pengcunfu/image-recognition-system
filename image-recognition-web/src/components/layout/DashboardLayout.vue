<template>
  <a-layout class="dashboard-layout">
    <!-- Header -->
    <a-layout-header class="header">
      <div class="logo" @click="toggleSidebar">
        <!-- <i class="fas fa-eye"></i> -->
        <span>智能图像识别系统</span>
      </div>
      <div class="user-info">
        <span>欢迎，管理员</span>
        <a-avatar class="user-avatar" @click="goToProfile">A</a-avatar>
        <a-button type="primary" @click="handleLogout">
          <template #icon>
            <i class="fas fa-sign-out-alt"></i>
          </template>
          退出
        </a-button>
      </div>
    </a-layout-header>
    
    <!-- Main Content -->
    <a-layout>
      <!-- Sidebar -->
      <a-layout-sider 
        v-model:collapsed="collapsed" 
        :trigger="null" 
        collapsible
        class="sidebar"
        width="256"
      >
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          theme="light"
          class="menu"
          @click="handleMenuClick"
        >
          <a-menu-item key="/dashboard">
            <template #icon>
              <i class="fas fa-tachometer-alt"></i>
            </template>
            仪表板
          </a-menu-item>
          
          <!-- 内容管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="内容管理">
            <a-menu-item key="/posts-management">
              <template #icon>
                <i class="fas fa-clipboard-list"></i>
              </template>
              帖子管理
            </a-menu-item>
            <a-menu-item key="/knowledge-management">
              <template #icon>
                <i class="fas fa-book"></i>
              </template>
              知识库管理
            </a-menu-item>
            <a-menu-item key="/category-management">
              <template #icon>
                <i class="fas fa-tags"></i>
              </template>
              知识库分类
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 用户管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="用户管理">
            <a-menu-item key="/users">
              <template #icon>
                <i class="fas fa-users"></i>
              </template>
              用户管理
            </a-menu-item>
            <a-menu-item key="/vip-management">
              <template #icon>
                <i class="fas fa-crown"></i>
              </template>
              VIP管理
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 数据管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="数据管理">
            <a-menu-item key="/recognition-management">
              <template #icon>
                <i class="fas fa-camera"></i>
              </template>
              识别记录
            </a-menu-item>
            <a-menu-item key="/analytics">
              <template #icon>
                <i class="fas fa-chart-bar"></i>
              </template>
              数据分析
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 系统管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="系统管理">
            <a-menu-item key="/orders">
              <template #icon>
                <i class="fas fa-shopping-cart"></i>
              </template>
              订单管理
            </a-menu-item>
            <a-menu-item key="/settings">
              <template #icon>
                <i class="fas fa-cog"></i>
              </template>
              系统设置
            </a-menu-item>
          </a-menu-item-group>
        </a-menu>
      </a-layout-sider>
      
      <!-- Content Area -->
      <a-layout-content class="content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const selectedKeys = ref(['/dashboard'])

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (newPath) => {
  selectedKeys.value = [newPath]
}, { immediate: true })

// 菜单点击处理
function handleMenuClick({ key }: { key: string }) {
  if (key !== route.path) {
    router.push(key)
  }
}

// 切换侧边栏
function toggleSidebar() {
  collapsed.value = !collapsed.value
}

// 跳转到个人信息页面
function goToProfile() {
  router.push('/admin-profile')
}

// 退出登录
function handleLogout() {
  // 清除登录状态
  localStorage.removeItem('userToken')
  localStorage.removeItem('rememberedUser')
  
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
}

/* Header */
.header {
  background: #001529;
  color: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.logo:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.logo i {
  font-size: 24px;
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  background: #1890ff;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: #40a9ff;
  transform: scale(1.05);
}

/* Sidebar */
.sidebar {
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  height: 100vh;
  background: white;
  position: fixed;
  top: 64px;
  left: 0;
  z-index: 999;
  width: 256px !important;
  min-width: 256px;
  max-width: 256px;
}

.sidebar :deep(.ant-layout-sider-children) {
  background: white;
}

.menu {
  border-right: none;
  padding: 16px 0;
  width: 100%;
  overflow-x: hidden;
}

/* 菜单项样式 */
.menu :deep(.ant-menu-item) {
  margin: 2px 8px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
  width: calc(100% - 16px);
  overflow: hidden;
}

.menu :deep(.ant-menu-item:hover) {
  background-color: rgba(24, 144, 255, 0.08);
  color: #1890ff;
  transform: none;
}

.menu :deep(.ant-menu-item-selected) {
  background-color: #e6f7ff !important;
  color: #1890ff !important;
  border-radius: 8px;
  font-weight: 600;
}

.menu :deep(.ant-menu-item-selected::after) {
  display: none;
}

.menu :deep(.ant-menu-item .anticon) {
  font-size: 16px;
  width: 20px;
  margin-right: 12px;
}

.menu :deep(.ant-menu-item-selected .anticon) {
  color: #1890ff;
}

.menu :deep(.ant-menu-item:hover .anticon) {
  color: #1890ff;
}

/* Content */
.content {
  padding: 24px;
  overflow-y: auto;
  margin-top: 64px;
  margin-left: 256px;
  min-height: calc(100vh - 64px);
  transition: margin-left 0.3s ease;
}

/* 侧边栏收缩时的内容区域调整 */
.dashboard-layout :deep(.ant-layout-sider-collapsed) + .ant-layout .content {
  margin-left: 80px;
}

.sidebar:deep(.ant-layout-sider-collapsed) {
  width: 80px !important;
  min-width: 80px;
  max-width: 80px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

/* Responsive */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }
  
  .content {
    padding: 16px;
    margin-left: 200px;
  }
  
  .dashboard-layout :deep(.ant-layout-sider-collapsed) + .ant-layout .content {
    margin-left: 80px;
  }
}

@media (max-width: 576px) {
  .header {
    padding: 0 16px;
  }
  
  .logo span {
    display: none;
  }
  
  .user-info span {
    display: none;
  }
  
  .sidebar {
    width: 100% !important;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar.mobile-open {
    transform: translateX(0);
  }
  
  .content {
    margin-left: 0 !important;
  }
}
</style>
