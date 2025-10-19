<template>
  <a-layout :style="{ minHeight: '100vh' }">
    <!-- Header -->
    <a-layout-header :style="{ 
      background: '#001529', 
      padding: '0 24px', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'space-between', 
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)' 
    }">
      <div :style="{ 
        display: 'flex', 
        alignItems: 'center', 
        gap: '12px', 
        fontSize: '20px', 
        fontWeight: 'bold', 
        color: 'white' 
      }">
        <span>智能图像识别系统</span>
      </div>
      
      <!-- 导航菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        theme="dark"
        :style="{ flex: 1, margin: '0 40px', borderBottom: 'none' }"
        @click="handleMenuClick"
      >
        <a-menu-item key="/user/dashboard">
          <template #icon>
            <HomeOutlined />
          </template>
          首页
        </a-menu-item>
        <a-menu-item key="/user/recognition">
          <template #icon>
            <CameraOutlined />
          </template>
          图像识别
        </a-menu-item>
        <a-menu-item key="/user/knowledge">
          <template #icon>
            <BookOutlined />
          </template>
          知识库
        </a-menu-item>
        <a-menu-item key="/user/community">
          <template #icon>
            <TeamOutlined />
          </template>
          社区
        </a-menu-item>
        <a-menu-item key="/user/history">
          <template #icon>
            <HistoryOutlined />
          </template>
          历史记录
        </a-menu-item>
        
        <!-- VIP专享功能菜单 -->
        <a-sub-menu v-if="isVipUser" key="vip-features">
          <template #icon>
            <CrownOutlined :style="{ color: '#ffd700' }" />
          </template>
          <template #title>
            <span :style="{ color: '#ffd700' }">VIP专享</span>
          </template>
          <a-menu-item key="/user/advanced-recognition">
            <template #icon>
              <ThunderboltOutlined :style="{ color: '#ffd700' }" />
            </template>
            高级识别
          </a-menu-item>
          <a-menu-item key="/user/vip-analytics">
            <template #icon>
              <LineChartOutlined :style="{ color: '#ffd700' }" />
            </template>
            VIP数据分析
          </a-menu-item>
          <a-menu-item key="/user/ai-training">
            <template #icon>
              <RobotOutlined :style="{ color: '#ffd700' }" />
            </template>
            AI模型训练
          </a-menu-item>
          <a-menu-item key="/user/api-access">
            <template #icon>
              <ApiOutlined :style="{ color: '#ffd700' }" />
            </template>
            API访问
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
      
      <!-- 用户信息 -->
      <div :style="{ display: 'flex', alignItems: 'center', gap: '16px' }">
        <a-tag v-if="isVipUser" color="gold" :style="{ fontSize: '12px', fontWeight: 'bold' }">
          <CrownOutlined />
          VIP
        </a-tag>
        
        <a-dropdown>
          <a-button type="text" :style="{ color: 'white', display: 'flex', alignItems: 'center', gap: '8px' }">
            <a-avatar :src="userInfo.avatar" :style="{ background: '#1890ff', color: 'white', fontWeight: 'bold' }">
              {{ userInfo.name.charAt(0) }}
            </a-avatar>
            <span :style="{ color: 'white', marginLeft: '8px' }">{{ userInfo.name }}</span>
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="handleUserMenuClick">
              <a-menu-item key="profile">
                <UserOutlined />
                个人中心
              </a-menu-item>
              <a-menu-item key="settings">
                <SettingOutlined />
                我的设置
              </a-menu-item>
              <a-menu-item key="favorites">
                <HeartOutlined />
                我的收藏
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    
    <!-- Main Content -->
    <a-layout-content :style="{ padding: '24px', minHeight: 'calc(100vh - 64px - 70px)', background: '#f0f2f5' }">
      <div :style="{ maxWidth: '1200px', margin: '0 auto' }">
        <router-view />
      </div>
    </a-layout-content>
    
    <!-- Footer -->
    <a-layout-footer :style="{ background: '#001529', color: 'white', textAlign: 'center', padding: '24px 50px' }">
      <div :style="{ maxWidth: '1200px', margin: '0 auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
        <p :style="{ margin: 0 }">&copy; 2025 智能图像识别系统. All rights reserved. Designed by 彭存福</p>
        <div :style="{ display: 'flex', gap: '24px' }">
          <router-link to="/about" :style="{ color: 'rgba(255, 255, 255, 0.7)', textDecoration: 'none', transition: 'color 0.3s' }">关于我们</router-link>
          <router-link to="/contact" :style="{ color: 'rgba(255, 255, 255, 0.7)', textDecoration: 'none', transition: 'color 0.3s' }">联系我们</router-link>
          <router-link to="/privacy" :style="{ color: 'rgba(255, 255, 255, 0.7)', textDecoration: 'none', transition: 'color 0.3s' }">隐私政策</router-link>
          <router-link to="/terms" :style="{ color: 'rgba(255, 255, 255, 0.7)', textDecoration: 'none', transition: 'color 0.3s' }">服务条款</router-link>
        </div>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  HomeOutlined, 
  CameraOutlined, 
  BookOutlined, 
  TeamOutlined, 
  HistoryOutlined, 
  CrownOutlined, 
  ThunderboltOutlined, 
  LineChartOutlined, 
  RobotOutlined, 
  ApiOutlined, 
  UserOutlined, 
  SettingOutlined, 
  HeartOutlined, 
  LogoutOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const selectedKeys = ref(['/user/dashboard'])

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


// 退出登录
function handleLogout() {
  localStorage.removeItem('userToken')
  localStorage.removeItem('rememberedUser')
  message.success('已退出登录')
  router.push('/login')
}
</script>
