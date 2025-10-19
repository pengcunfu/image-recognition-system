<template>
  <a-layout :style="{ minHeight: '100vh' }">
    <!-- Header -->
    <a-layout-header :style="{ 
      background: '#001529', 
      color: 'white', 
      padding: '0 24px', 
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
          transition: 'background-color 0.3s ease' 
        }"
        @click="toggleSidebar"
        @mouseenter="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = 'rgba(255, 255, 255, 0.1)'"
        @mouseleave="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.backgroundColor = 'transparent'"
      >
        <span>智能图像识别系统</span>
      </div>
      <div :style="{ display: 'flex', alignItems: 'center', gap: '16px' }">
        <span>欢迎，管理员</span>
        <a-avatar 
          :style="{ 
            background: '#1890ff', 
            color: 'white', 
            fontWeight: 'bold', 
            cursor: 'pointer', 
            transition: 'all 0.3s ease' 
          }"
          @click="goToProfile"
        >A</a-avatar>
        <a-button type="primary" @click="handleLogout">
          <template #icon>
            <LogoutOutlined />
          </template>
          退出
        </a-button>
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
            仪表板
          </a-menu-item>
          
          <!-- 内容管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="内容管理">
            <a-menu-item key="/posts-management">
              <template #icon>
                <FileTextOutlined />
              </template>
              帖子管理
            </a-menu-item>
            <a-menu-item key="/knowledge-management">
              <template #icon>
                <BookOutlined />
              </template>
              知识库管理
            </a-menu-item>
            <a-menu-item key="/category-management">
              <template #icon>
                <TagsOutlined />
              </template>
              知识库分类
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 用户管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="用户管理">
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
              VIP管理
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 数据管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="数据管理">
            <a-menu-item key="/recognition-management">
              <template #icon>
                <CameraOutlined />
              </template>
              识别记录
            </a-menu-item>
            <a-menu-item key="/analytics">
              <template #icon>
                <BarChartOutlined />
              </template>
              数据分析
            </a-menu-item>
          </a-menu-item-group>
          
          <!-- 系统管理分组 -->
          <a-menu-divider />
          <a-menu-item-group title="系统管理">
            <a-menu-item key="/orders">
              <template #icon>
                <ShoppingCartOutlined />
              </template>
              订单管理
            </a-menu-item>
            <a-menu-item key="/settings">
              <template #icon>
                <SettingOutlined />
              </template>
              系统设置
            </a-menu-item>
          </a-menu-item-group>
        </a-menu>
      </a-layout-sider>
      
      <!-- Content Area -->
      <a-layout-content :style="{ 
        padding: '24px', 
        marginLeft: collapsed ? '80px' : '256px', 
        minHeight: 'calc(100vh - 64px)', 
        transition: 'margin-left 0.3s ease' 
      }">
        <div :style="{ maxWidth: '1200px', margin: '0 auto' }">
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
import { 
  DashboardOutlined, 
  FileTextOutlined, 
  BookOutlined, 
  TagsOutlined, 
  UserOutlined, 
  CrownOutlined, 
  CameraOutlined, 
  BarChartOutlined, 
  ShoppingCartOutlined, 
  SettingOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'

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
