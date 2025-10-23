<template>
  <a-layout :style="{ minHeight: '100vh' }">
    <!-- Header -->
    <a-layout-header :style="{ 
      background: '#ffffff', 
      color: '#262626', 
      padding: '0 12px', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'space-between', 
      height: '64px', 
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)', 
      position: 'fixed', 
      top: 0, 
      left: 0, 
      right: 0, 
      zIndex: 1000 
    }">
      <div 
        :style="{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: '12px', 
          fontSize: '20px', 
          fontWeight: 'bold', 
          cursor: 'pointer', 
          padding: '8px 12px', 
          borderRadius: '6px', 
          transition: 'background-color 0.3s ease',
          color: '#262626'
        }"
        @click="toggleSidebar"
        @mouseenter="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = '#f5f5f5'"
        @mouseleave="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = 'transparent'"
      >
        <span>智能图像识别系统</span>
      </div>
      <div :style="{ display: 'flex', alignItems: 'center', gap: '12px' }">
        <a-dropdown placement="bottomRight">
          <div :style="{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: '8px', 
            cursor: 'pointer', 
            padding: '4px 8px',
            borderRadius: '4px',
            transition: 'background-color 0.3s'
          }"
          @mouseenter="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = '#f5f5f5'"
          @mouseleave="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = 'transparent'">
            <a-avatar 
              :src="adminInfo.avatar"
              :style="{ 
                background: '#1890ff', 
                color: 'white', 
                fontWeight: 'bold'
              }"
            >{{ getAvatarText() }}</a-avatar>
            <span :style="{ color: '#262626' }">{{ adminInfo.nickname || adminInfo.username || '管理员' }}</span>
          </div>
          <template #overlay>
            <a-menu @click="handleUserMenuClick">
              <a-menu-item key="profile">
                <UserOutlined style="margin-right: 8px;" />
                个人信息
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout">
                <LogoutOutlined style="margin-right: 8px;" />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    
    <!-- Main Content -->
    <a-layout :style="{ marginTop: '64px' }">
      <!-- Sidebar -->
      <a-layout-sider 
        v-model:collapsed="collapsed" 
        :trigger="null" 
        collapsible
        :width="256"
        :style="{ 
          boxShadow: '2px 0 8px rgba(0,0,0,0.1)', 
          height: 'calc(100vh - 64px)', 
          background: 'white', 
          position: 'fixed', 
          top: '64px', 
          left: 0, 
          zIndex: 999, 
          overflow: 'auto' 
        }"
      >
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          theme="light"
          :style="{ borderRight: 'none', padding: '16px 0', width: '100%' }"
          @click="handleMenuClick"
        >
          <a-menu-item key="/dashboard">
            <template #icon>
              <DashboardOutlined />
            </template>
            数据概览
          </a-menu-item>
          
          <a-menu-item key="/users">
            <template #icon>
              <UserOutlined />
            </template>
            用户管理
          </a-menu-item>
          
          <a-menu-item key="/vip-management">
            <template #icon>
              <CrownOutlined />
            </template>
            会员管理
          </a-menu-item>
          
          <a-menu-item key="/orders">
            <template #icon>
              <ShoppingCartOutlined />
            </template>
            订单管理
          </a-menu-item>
          
          <a-menu-item key="/posts-management">
            <template #icon>
              <FileTextOutlined />
            </template>
            社区管理
          </a-menu-item>
          
          <a-menu-item key="/knowledge-management">
            <template #icon>
              <BookOutlined />
            </template>
            知识库管理
          </a-menu-item>
          
          <a-menu-item key="/recognition-management">
            <template #icon>
              <CameraOutlined />
            </template>
            识别记录
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      
      <!-- Content Area -->
      <a-layout-content :style="{ 
        marginLeft: collapsed ? '80px' : '256px', 
        minHeight: 'calc(100vh - 64px)', 
        transition: 'margin-left 0.3s ease',
        background: '#f0f2f5'
      }">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  DashboardOutlined, 
  FileTextOutlined, 
  BookOutlined, 
  UserOutlined, 
  CrownOutlined, 
  CameraOutlined, 
  ShoppingCartOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { AdminAPI } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const selectedKeys = ref(['/dashboard'])

// 管理员信息
const adminInfo = reactive({
  id: 0,
  username: '',
  nickname: '',
  avatar: '',
  email: ''
})

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (newPath) => {
  selectedKeys.value = [newPath]
}, { immediate: true })

// 获取头像显示文本
function getAvatarText() {
  if (adminInfo.nickname) {
    return adminInfo.nickname.charAt(0).toUpperCase()
  }
  if (adminInfo.username) {
    return adminInfo.username.charAt(0).toUpperCase()
  }
  return 'A'
}

// 加载管理员信息
async function loadAdminInfo() {
  try {
    const profile = await AdminAPI.getAdminProfile()
    adminInfo.id = profile.id
    adminInfo.username = profile.username
    adminInfo.nickname = profile.nickname || ''
    adminInfo.avatar = profile.avatar || ''
    adminInfo.email = profile.email || ''
  } catch (error) {
    console.error('加载管理员信息失败:', error)
    // 不显示错误提示，使用默认值
  }
}

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

// 用户菜单点击处理
function handleUserMenuClick({ key }: { key: string }) {
  if (key === 'profile') {
    router.push('/admin-profile')
  } else if (key === 'logout') {
    // 清除登录状态
    userStore.clearUserInfo()
    
    message.success('已退出登录')
    router.push('/login')
  }
}

// 组件挂载时加载管理员信息
onMounted(() => {
  loadAdminInfo()
})
</script>
