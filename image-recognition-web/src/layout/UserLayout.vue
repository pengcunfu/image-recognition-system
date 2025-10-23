<template>
  <a-layout :style="{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }">
    <!-- Header -->
    <a-layout-header :style="{ 
      background: '#001529', 
      padding: isMobile ? '0 16px' : '0 24px', 
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
        fontSize: isMobile ? '16px' : '20px', 
        fontWeight: 'bold', 
        color: 'white',
        whiteSpace: 'nowrap',
        flexShrink: 0
      }">
        <!-- 移动端汉堡菜单按钮 -->
        <a-button 
          v-if="isMobile" 
          type="text" 
          @click="drawerVisible = true"
          :style="{ color: 'white', padding: '4px 8px', flexShrink: 0 }"
        >
          <MenuOutlined :style="{ fontSize: '20px' }" />
        </a-button>
        <span :style="{ whiteSpace: 'nowrap' }">智能图像识别系统</span>
      </div>
      
      <!-- PC端导航菜单 -->
      <a-menu
        v-if="!isMobile"
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        theme="dark"
        :style="{ flex: 1, margin: '0 40px', borderBottom: 'none' }"
        @click="handleMenuClick"
      >
        <a-menu-item key="/user/dashboard">
          <template #icon>
            <HomeOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">首页</span>
        </a-menu-item>
        <a-menu-item key="/user/recognition">
          <template #icon>
            <CameraOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">图像识别</span>
        </a-menu-item>
        <a-menu-item key="/user/knowledge">
          <template #icon>
            <BookOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">知识库</span>
        </a-menu-item>
        <a-menu-item key="/user/community">
          <template #icon>
            <TeamOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">社区</span>
        </a-menu-item>
        <a-menu-item key="/user/history">
          <template #icon>
            <HistoryOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">历史记录</span>
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
      <div :style="{ display: 'flex', alignItems: 'center', gap: isMobile ? '8px' : '16px' }">
        <a-tag v-if="isVipUser && !isMobile" color="gold" :style="{ fontSize: '12px', fontWeight: 'bold' }">
          <CrownOutlined />
          VIP
        </a-tag>
        
        <a-dropdown>
          <a-button type="text" :style="{ color: 'white', display: 'flex', alignItems: 'center', gap: '8px', padding: isMobile ? '4px' : '4px 15px' }">
            <a-avatar :src="userStore.userAvatar" :size="isMobile ? 32 : 40" :style="{ background: '#1890ff', color: 'white', fontWeight: 'bold' }">
              {{ getAvatarText() }}
            </a-avatar>
            <span v-if="!isMobile" :style="{ color: 'white', marginLeft: '8px' }">{{ userStore.userNickname }}</span>
          </a-button>
          <template #overlay>
            <a-menu @click="handleUserMenuClick">
              <a-menu-item key="profile">
                <UserOutlined />
                个人中心
              </a-menu-item>
              <a-menu-item key="posts">
                <BookOutlined />
                我的帖子
              </a-menu-item>
              <a-menu-item key="favorites">
                <HeartOutlined />
                我的收藏
              </a-menu-item>
              <a-menu-item key="likes">
                <LikeOutlined />
                我的点赞
              </a-menu-item>
              <a-menu-item key="settings">
                <SettingOutlined />
                账号设置
              </a-menu-item>
              <a-menu-divider />
              <!-- 成为VIP菜单项（仅非VIP用户可见） -->
              <a-menu-item v-if="!isVipUser" key="become-vip" :style="{ color: '#ffd700', fontWeight: 'bold' }">
                <CrownOutlined :style="{ color: '#ffd700' }" />
                成为VIP会员
              </a-menu-item>
              <a-menu-divider v-if="!isVipUser" />
              <a-menu-item key="logout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    
    <!-- 移动端抽屉菜单 -->
    <a-drawer
      v-model:open="drawerVisible"
      placement="left"
      title="菜单"
      :width="280"
      :body-style="{ padding: 0 }"
    >
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :style="{ borderRight: 0 }"
        @click="handleDrawerMenuClick"
      >
        <a-menu-item key="/user/dashboard">
          <template #icon>
            <HomeOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">首页</span>
        </a-menu-item>
        <a-menu-item key="/user/recognition">
          <template #icon>
            <CameraOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">图像识别</span>
        </a-menu-item>
        <a-menu-item key="/user/knowledge">
          <template #icon>
            <BookOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">知识库</span>
        </a-menu-item>
        <a-menu-item key="/user/community">
          <template #icon>
            <TeamOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">社区</span>
        </a-menu-item>
        <a-menu-item key="/user/history">
          <template #icon>
            <HistoryOutlined :style="isVipUser ? { color: '#ffd700' } : {}" />
          </template>
          <span :style="isVipUser ? { color: '#ffd700' } : {}">历史记录</span>
        </a-menu-item>
        
        <!-- VIP专享功能菜单 -->
        <a-menu-divider v-if="isVipUser" />
        <a-menu-item-group v-if="isVipUser" title="VIP专享功能">
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
        </a-menu-item-group>
      </a-menu>
    </a-drawer>
    
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
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
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
  LikeOutlined,
  LogoutOutlined,
  MenuOutlined
} from '@ant-design/icons-vue'
import { UserAPI } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const selectedKeys = ref(['/user/dashboard'])
const drawerVisible = ref(false)
const isMobile = ref(false)

// 检查是否为VIP用户
const isVipUser = computed(() => userStore.isVip)

// 获取头像显示文本
function getAvatarText() {
  if (userStore.userNickname && userStore.userNickname !== '用户') {
    return userStore.userNickname.charAt(0).toUpperCase()
  }
  if (userStore.userName) {
    return userStore.userName.charAt(0).toUpperCase()
  }
  return 'U'
}

// 刷新用户信息（用于更新用户状态，如VIP状态）
async function loadUserInfo() {
  try {
    await userStore.fetchUserProfile()
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 不显示错误提示
  }
}

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (newPath, oldPath) => {
  // 匹配用户相关路由
  const userPaths = ['/user/dashboard', '/user/recognition', '/user/knowledge', '/user/community', '/user/history', '/user/become-vip']
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

  // 如果从成为VIP页面跳转到其他页面，刷新用户信息以更新VIP状态
  if (oldPath === '/user/become-vip' && newPath !== '/user/become-vip') {
    loadUserInfo()
  }
}, { immediate: true })

// 检测屏幕尺寸
function checkMobile() {
  isMobile.value = window.innerWidth < 992
}

// 主导航菜单点击处理
function handleMenuClick({ key }: { key: string }) {
  if (key !== route.path) {
    router.push(key)
  }
}

// 移动端抽屉菜单点击处理
function handleDrawerMenuClick({ key }: { key: string }) {
  drawerVisible.value = false
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
    case 'posts':
      // 跳转到个人中心的帖子标签
      router.push({ path: '/user/profile', query: { tab: 'posts' } })
      break
    case 'favorites':
      // 跳转到个人中心的收藏标签
      router.push({ path: '/user/profile', query: { tab: 'favorites' } })
      break
    case 'likes':
      // 跳转到个人中心的点赞标签
      router.push({ path: '/user/profile', query: { tab: 'likes' } })
      break
    case 'settings':
      // 跳转到个人中心的设置标签
      router.push({ path: '/user/profile', query: { tab: 'settings' } })
      break
    case 'become-vip':
      // 跳转到成为VIP页面
      router.push('/user/become-vip')
      break
    case 'logout':
      handleLogout()
      break
  }
}


// 退出登录
function handleLogout() {
  userStore.clearUserInfo()
  message.success('已退出登录')
  router.push('/login')
}

// 组件挂载时检测屏幕尺寸
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>
