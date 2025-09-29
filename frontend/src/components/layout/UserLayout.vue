<template>
  <a-layout class="user-layout">
    <!-- Header -->
    <a-layout-header class="header">
      <div class="logo">
        <i class="fas fa-eye"></i>
        <span>智能图像识别系统</span>
      </div>
      
      <!-- 导航菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        theme="dark"
        class="nav-menu"
        @click="handleMenuClick"
      >
        <a-menu-item key="/user/dashboard">
          <template #icon>
            <i class="fas fa-home"></i>
          </template>
          首页
        </a-menu-item>
        <a-menu-item key="/user/recognition">
          <template #icon>
            <i class="fas fa-camera"></i>
          </template>
          图像识别
        </a-menu-item>
        <a-menu-item key="/user/knowledge">
          <template #icon>
            <i class="fas fa-book"></i>
          </template>
          知识库
        </a-menu-item>
        <a-menu-item key="/user/community">
          <template #icon>
            <i class="fas fa-users"></i>
          </template>
          社区
        </a-menu-item>
        <a-menu-item key="/user/history">
          <template #icon>
            <i class="fas fa-history"></i>
          </template>
          历史记录
        </a-menu-item>
        
        <!-- VIP专享功能菜单 -->
        <a-sub-menu v-if="isVipUser" key="vip-features">
          <template #icon>
            <i class="fas fa-crown" style="color: #ffd700;"></i>
          </template>
          <template #title>
            <span style="color: #ffd700;">VIP专享</span>
          </template>
          <a-menu-item key="/user/advanced-recognition">
            <template #icon>
              <i class="fas fa-magic" style="color: #ffd700;"></i>
            </template>
            高级识别
          </a-menu-item>
          <a-menu-item key="/user/vip-analytics">
            <template #icon>
              <i class="fas fa-chart-line" style="color: #ffd700;"></i>
            </template>
            VIP数据分析
          </a-menu-item>
          <a-menu-item key="/user/ai-training">
            <template #icon>
              <i class="fas fa-brain" style="color: #ffd700;"></i>
            </template>
            AI模型训练
          </a-menu-item>
          <a-menu-item key="/user/api-access">
            <template #icon>
              <i class="fas fa-key" style="color: #ffd700;"></i>
            </template>
            API访问
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
      
      <!-- 用户信息 -->
      <div class="user-info">
        <a-badge :count="messageCount" :offset="[10, 0]">
          <a-button type="text" class="message-btn" @click="showMessages">
            <i class="fas fa-bell"></i>
          </a-button>
        </a-badge>
        
        <a-tag v-if="isVipUser" color="gold" class="vip-badge">
          <i class="fas fa-crown"></i>
          VIP
        </a-tag>
        
        <a-dropdown>
          <a-button type="text" class="user-btn">
            <a-avatar :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.name.charAt(0) }}
            </a-avatar>
            <span class="user-name">{{ userInfo.name }}</span>
            <i class="fas fa-chevron-down"></i>
          </a-button>
          <template #overlay>
            <a-menu @click="handleUserMenuClick">
              <a-menu-item key="profile">
                <i class="fas fa-user"></i>
                个人中心
              </a-menu-item>
              <a-menu-item key="settings">
                <i class="fas fa-cog"></i>
                我的设置
              </a-menu-item>
              <a-menu-item key="favorites">
                <i class="fas fa-heart"></i>
                我的收藏
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout">
                <i class="fas fa-sign-out-alt"></i>
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    
    <!-- Main Content -->
    <a-layout-content class="content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </a-layout-content>
    
    <!-- Footer -->
    <a-layout-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 智能图像识别系统. All rights reserved.</p>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">联系我们</a>
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
        </div>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'

const router = useRouter()
const route = useRoute()
const selectedKeys = ref(['/user/dashboard'])
const messageCount = ref(3)

// 用户信息
const userInfo = reactive({
  name: '张三',
  avatar: '',
  email: 'zhangsan@example.com'
})

// 检查是否为VIP用户
const isVipUser = computed(() => {
  const userRole = localStorage.getItem('userRole')
  return userRole === 'vip'
})

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (newPath) => {
  // 匹配用户相关路由
  const userPaths = ['/user/dashboard', '/user/recognition', '/user/knowledge', '/user/community', '/user/history']
  const vipPaths = ['/user/advanced-recognition', '/user/vip-analytics', '/user/ai-training', '/user/api-access']
  
  // 优先匹配VIP路径
  const matchedVipPath = vipPaths.find(path => newPath.startsWith(path))
  if (matchedVipPath) {
    selectedKeys.value = [matchedVipPath]
    return
  }
  
  // 然后匹配普通用户路径
  const matchedPath = userPaths.find(path => newPath.startsWith(path))
  if (matchedPath) {
    selectedKeys.value = [matchedPath]
  }
}, { immediate: true })

// 主导航菜单点击处理
function handleMenuClick({ key }: { key: string }) {
  if (key !== route.path) {
    router.push(key)
  }
}

// 用户菜单点击处理
function handleUserMenuClick({ key }: { key: string }) {
  switch (key) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'settings':
      router.push('/user/settings')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 显示消息
function showMessages() {
  message.info('消息功能开发中')
}

// 退出登录
function handleLogout() {
  localStorage.removeItem('userToken')
  localStorage.removeItem('rememberedUser')
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
}

/* Header */
.header {
  background: #001529;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.logo i {
  font-size: 24px;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.vip-badge {
  font-size: 12px;
  font-weight: bold;
}

.message-btn,
.user-btn {
  color: white !important;
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-btn {
  font-size: 18px;
}

.user-avatar {
  background: #1890ff;
  color: white;
  font-weight: bold;
}

.user-name {
  color: white;
  margin-left: 8px;
}

/* Content */
.content {
  padding: 24px;
  min-height: calc(100vh - 64px - 70px);
  background: #f0f2f5;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

/* Footer */
.footer {
  background: #001529;
  color: white;
  text-align: center;
  padding: 24px 50px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: white;
}

/* Responsive */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .nav-menu {
    margin: 0 20px;
  }
  
  .logo span {
    display: none;
  }
  
  .user-name {
    display: none;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 16px;
  }
}

@media (max-width: 576px) {
  .content {
    padding: 16px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .footer-links {
    flex-wrap: wrap;
    gap: 12px;
  }
}

/* VIP菜单样式 */
:deep(.ant-menu-dark .ant-menu-submenu-title) {
  color: rgba(255, 255, 255, 0.85);
}

:deep(.ant-menu-dark .ant-menu-submenu:hover .ant-menu-submenu-title) {
  color: #ffd700;
}

:deep(.ant-menu-dark .ant-menu-submenu-selected .ant-menu-submenu-title) {
  color: #ffd700;
}

:deep(.ant-menu-submenu-popup .ant-menu-item) {
  padding-left: 24px !important;
}

:deep(.ant-menu-submenu-popup .ant-menu-item:hover) {
  background-color: rgba(255, 215, 0, 0.1);
}
</style>
