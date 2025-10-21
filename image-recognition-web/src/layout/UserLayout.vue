<template>
  <a-layout :style="{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }">
    <!-- Header -->
    <a-layout-header :style="{ 
      background: '#001529', 
      padding: '0 24px', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'space-between', 
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      zIndex: 1000,
      height: '64px'
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
        
        <!-- VIP专享功能菜单（一级菜单） -->
        <template v-if="isVipUser">
          <a-menu-item key="/user/advanced-recognition">
            <template #icon>
              <ThunderboltOutlined :style="{ color: '#ffd700' }" />
            </template>
            <span :style="{ color: '#ffd700' }">高级识别</span>
          </a-menu-item>
          <a-menu-item key="/user/vip-analytics">
            <template #icon>
              <LineChartOutlined :style="{ color: '#ffd700' }" />
            </template>
            <span :style="{ color: '#ffd700' }">VIP数据分析</span>
          </a-menu-item>
          <a-menu-item key="/user/ai-training">
            <template #icon>
              <RobotOutlined :style="{ color: '#ffd700' }" />
            </template>
            <span :style="{ color: '#ffd700' }">AI模型训练</span>
          </a-menu-item>
        </template>
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
              {{ getAvatarText() }}
            </a-avatar>
            <span :style="{ color: 'white', marginLeft: '8px' }">{{ userInfo.nickname || userInfo.username || '用户' }}</span>
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
    <a-layout-content :style="{ 
      padding: '0', 
      minHeight: 'calc(100vh - 64px - 70px)', 
      background: '#f0f2f5',
      marginTop: '64px',
      marginBottom: '70px',
      flex: 1
    }">
      <router-view />
    </a-layout-content>
    
    <!-- Footer -->
    <a-layout-footer :style="{ 
      background: '#001529', 
      color: 'white', 
      textAlign: 'center', 
      padding: '24px 50px',
      position: 'fixed',
      bottom: 0,
      left: 0,
      right: 0,
      zIndex: 999
    }">
      <p :style="{ margin: 0 }">&copy; 2025 智能图像识别系统. All rights reserved. Designed by 彭存福</p>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
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
  LogoutOutlined
} from '@ant-design/icons-vue'
import { UserAPI } from '@/api/user'

const router = useRouter()
const route = useRoute()
const selectedKeys = ref(['/user/dashboard'])

// 用户信息
const userInfo = reactive({
  id: 0,
  username: '',
  nickname: '',
  avatar: '',
  email: '',
  role: 0
})

// 检查是否为VIP用户
const isVipUser = computed(() => {
  // 1 = VIP, 根据后端的 UserRole 枚举
  return userInfo.role === 1
})

// 获取头像显示文本
function getAvatarText() {
  if (userInfo.nickname) {
    return userInfo.nickname.charAt(0).toUpperCase()
  }
  if (userInfo.username) {
    return userInfo.username.charAt(0).toUpperCase()
  }
  return 'U'
}

// 加载用户信息
async function loadUserInfo() {
  try {
    const profile = await UserAPI.getProfile()
    userInfo.id = profile.id
    userInfo.username = profile.username
    userInfo.nickname = profile.nickname || ''
    userInfo.avatar = profile.avatar || ''
    userInfo.email = profile.email || ''
    userInfo.role = profile.role
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 不显示错误提示，使用默认值
  }
}

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (newPath) => {
  // 匹配用户相关路由
  const userPaths = ['/user/dashboard', '/user/recognition', '/user/knowledge', '/user/community', '/user/history']
  const vipPaths = ['/user/advanced-recognition', '/user/vip-analytics', '/user/ai-training']
  
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

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserInfo()
})
</script>
