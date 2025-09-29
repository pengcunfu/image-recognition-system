import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { message } from 'ant-design-vue'

// 路由组件
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'

// 管理员组件
import DashboardLayout from '@/components/layout/DashboardLayout.vue'
import DashboardView from '@/views/DashboardView.vue'
import ProductsView from '@/views/ProductsView.vue'
import UsersView from '@/views/UsersView.vue'
import OrdersView from '@/views/OrdersView.vue'
import AnalyticsView from '@/views/AnalyticsView.vue'
import SettingsView from '@/views/SettingsView.vue'

// 用户组件
import UserLayout from '@/components/layout/UserLayout.vue'
import UserDashboardView from '@/views/user/UserDashboardView.vue'
import ImageRecognitionView from '@/views/user/ImageRecognitionView.vue'
import BatchRecognitionView from '@/views/user/BatchRecognitionView.vue'
import HistoryView from '@/views/user/HistoryView.vue'
import KnowledgeView from '@/views/user/KnowledgeView.vue'
import CommunityView from '@/views/user/CommunityView.vue'
import UserProfileView from '@/views/user/UserProfileView.vue'

// VIP用户组件
import AdvancedRecognitionView from '@/views/vip/AdvancedRecognitionView.vue'
import VipAnalyticsView from '@/views/vip/VipAnalyticsView.vue'
import AiTrainingView from '@/views/vip/AiTrainingView.vue'
import ApiAccessView from '@/views/vip/ApiAccessView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: {
      title: '登录 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: {
      title: '注册 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPasswordView,
    meta: {
      title: '找回密码 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: DashboardView,
        meta: {
          title: '仪表板 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/products',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Products',
        component: ProductsView,
        meta: {
          title: '产品管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/users',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Users',
        component: UsersView,
        meta: {
          title: '用户管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/orders',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Orders',
        component: OrdersView,
        meta: {
          title: '订单管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/analytics',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Analytics',
        component: AnalyticsView,
        meta: {
          title: '数据分析 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/settings',
    component: DashboardLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Settings',
        component: SettingsView,
        meta: {
          title: '系统设置 - 智能图像识别系统'
        }
      }
    ]
  },
  // 用户路由
  {
    path: '/user',
    component: UserLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: UserDashboardView,
        meta: {
          title: '用户首页 - 智能图像识别系统'
        }
      },
      {
        path: 'recognition',
        name: 'ImageRecognition',
        component: ImageRecognitionView,
        meta: {
          title: '图像识别 - 智能图像识别系统'
        }
      },
      {
        path: 'recognition/batch',
        name: 'BatchRecognition',
        component: BatchRecognitionView,
        meta: {
          title: '批量识别 - 智能图像识别系统'
        }
      },
      {
        path: 'history',
        name: 'History',
        component: HistoryView,
        meta: {
          title: '历史记录 - 智能图像识别系统'
        }
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: KnowledgeView,
        meta: {
          title: '知识库 - 智能图像识别系统'
        }
      },
      {
        path: 'community',
        name: 'Community',
        component: CommunityView,
        meta: {
          title: '社区 - 智能图像识别系统'
        }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: UserProfileView,
        meta: {
          title: '个人中心 - 智能图像识别系统'
        }
      },
      {
        path: 'favorites',
        name: 'UserFavorites',
        component: UserProfileView,
        meta: {
          title: '我的收藏 - 智能图像识别系统'
        }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: UserProfileView,
        meta: {
          title: '我的设置 - 智能图像识别系统'
        }
      },
      // VIP专享功能
      {
        path: 'advanced-recognition',
        name: 'AdvancedRecognition',
        component: AdvancedRecognitionView,
        meta: {
          title: '高级识别 - 智能图像识别系统',
          requiresVip: true
        }
      },
      {
        path: 'vip-analytics',
        name: 'VipAnalytics',
        component: VipAnalyticsView,
        meta: {
          title: 'VIP数据分析 - 智能图像识别系统',
          requiresVip: true
        }
      },
      {
        path: 'ai-training',
        name: 'AiTraining',
        component: AiTrainingView,
        meta: {
          title: 'AI模型训练 - 智能图像识别系统',
          requiresVip: true
        }
      },
      {
        path: 'api-access',
        name: 'ApiAccess',
        component: ApiAccessView,
        meta: {
          title: 'API访问管理 - 智能图像识别系统',
          requiresVip: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta?.title) {
    document.title = to.meta.title as string
  }
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth) {
    // 这里应该检查用户是否已登录
    // 暂时简化处理，实际项目中应该检查 token 或用户状态
    const isLoggedIn = localStorage.getItem('userToken') || false
    
    if (!isLoggedIn) {
      // 如果未登录，重定向到登录页
      next('/login')
      return
    }
  }
  
        // 检查VIP权限
        if (to.meta?.requiresVip) {
          const userRole = localStorage.getItem('userRole') || 'user'
          if (userRole !== 'vip') {
            message.warning('此功能仅限VIP用户使用')
            next('/user/dashboard')
            return
          }
        }

        // 如果已登录用户访问登录页面，重定向到用户首页
        if (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password') {
          const isLoggedIn = localStorage.getItem('userToken') || false
          if (isLoggedIn) {
            // 检查用户角色，决定重定向位置
            const userRole = localStorage.getItem('userRole') || 'user'
            if (userRole === 'admin') {
              next('/dashboard')
            } else {
              next('/user/dashboard')
            }
            return
          }
        }
  
  next()
})

export default router
