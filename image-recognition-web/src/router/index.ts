import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'

// 公共页面组件
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'

// 管理员组件
import DashboardLayout from '@/layout/AdminLayout.vue'
import DashboardView from '@/views/admin/AdminStatsView.vue'
import UsersView from '@/views/admin/AdminUsersView.vue'
import OrdersView from '@/views/admin/AdminOrdersView.vue'
import PostsManagementView from '@/views/admin/AdminCommunityView.vue'
import KnowledgeManagementView from '@/views/admin/AdminKnowledgeView.vue'
import VipManagementView from '@/views/admin/AdminVipsView.vue'
import RecognitionManagementView from '@/views/admin/AdminRecognitionView.vue'
import AdminProfileView from '@/views/admin/AdminProfileView.vue'
import AdminFileView from '@/views/admin/AdminFileView.vue'

// 用户组件
import UserLayout from '@/layout/UserLayout.vue'
import UserDashboardView from '@/views/UserDashboardView.vue'
import RecognitionView from '@/views/RecognitionView.vue'
import RecognitionBatchView from '@/views/RecognitionBatchView.vue'
import RecognitionHistoryView from '@/views/RecognitionHistoryView.vue'
import KnowledgeView from '@/views/KnowledgeView.vue'
import CommunityPostView from '@/views/CommunityPostView.vue'
import UserProfileView from '@/views/UserProfileView.vue'
import CommunityPostDetailView from '@/views/CommunityPostDetailView.vue'
import KnowledgeDetailView from '@/views/KnowledgeDetailView.vue'
import RecognitionDetailView from '@/views/RecognitionDetailView.vue'

// VIP功能组件（已合并到 user 目录）
import AdvancedRecognitionView from '@/views/RecognitionAdvancedView.vue'
import VipAnalyticsView from '@/views/VipAnalyticsView.vue'
import AiTrainingView from '@/views/AiTrainingView.vue'
import BecomeVipView from '@/views/BecomeVipView.vue'

// 通知消息组件
import AdminNotifications from '@/views/admin/AdminNotifications.vue'
import UserNotifications from '@/views/user-profile/UserNotifications.vue'

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
  // 新增管理页面路由
  {
    path: '/posts-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'PostsManagement',
        component: PostsManagementView,
        meta: {
          title: '帖子管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/knowledge-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'KnowledgeManagement',
        component: KnowledgeManagementView,
        meta: {
          title: '知识库管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/vip-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'VipManagement',
        component: VipManagementView,
        meta: {
          title: 'VIP管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/recognition-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'RecognitionManagement',
        component: RecognitionManagementView,
        meta: {
          title: '识别记录管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/file-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'FileManagement',
        component: AdminFileView,
        meta: {
          title: '文件管理 - 智能图像识别系统'
        }
      }
    ]
  },
  {
    path: '/admin-profile',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'AdminProfile',
        component: AdminProfileView,
        meta: {
          title: '管理员信息 - 智能图像识别系统'
        }
      }
    ]
  },
  // 管理员通知消息页面
  {
    path: '/admin/notifications',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'AdminNotifications',
        component: AdminNotifications,
        meta: {
          title: '我的消息 - 智能图像识别系统'
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
        name: 'Recognition',
        component: RecognitionView,
        meta: {
          title: '图像识别 - 智能图像识别系统'
        }
      },
      {
        path: 'recognition/batch',
        name: 'RecognitionBatch',
        component: RecognitionBatchView,
        meta: {
          title: '批量识别 - 智能图像识别系统'
        }
      },
      {
        path: 'history',
        name: 'RecognitionHistory',
        component: RecognitionHistoryView,
        meta: {
          title: '历史记录 - 智能图像识别系统'
        }
      },
      {
        path: 'recognition/:id',
        name: 'RecognitionDetail',
        component: RecognitionDetailView,
        meta: {
          title: '识别详情 - 智能图像识别系统'
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
        path: 'knowledge/:id',
        name: 'KnowledgeDetail',
        component: KnowledgeDetailView,
        meta: {
          title: '知识详情 - 智能图像识别系统'
        }
      },
      {
        path: 'community',
        name: 'Community',
        component: CommunityPostView,
        meta: {
          title: '社区帖子 - 智能图像识别系统'
        }
      },
      {
        path: 'community/post/:id',
        name: 'PostDetail',
        component: CommunityPostDetailView,
        meta: {
          title: '帖子详情 - 智能图像识别系统'
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
      // 成为VIP页面
      {
        path: 'become-vip',
        name: 'BecomeVip',
        component: BecomeVipView,
        meta: {
          title: '成为VIP - 智能图像识别系统'
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
      // 用户通知消息页面
      {
        path: 'notifications',
        name: 'UserNotifications',
        component: UserNotifications,
        meta: {
          title: '我的消息 - 智能图像识别系统'
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
  
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth) {
    // 从user store检查用户是否已登录
    if (!userStore.isLoggedIn) {
      // 如果未登录，重定向到登录页
      next('/login')
      return
    }
  }
  
  // 检查VIP权限
  if (to.meta?.requiresVip) {
    // role: 0=普通用户, 1=管理员
    // vipLevel: VIP等级
    if (!userStore.isVip && !userStore.isAdmin) {
      message.warning('此功能仅限VIP用户使用')
      next('/user/dashboard')
      return
    }
  }

  // 如果已登录用户访问登录页面，重定向到用户首页
  if (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password') {
    if (userStore.isLoggedIn) {
      // 检查用户角色，决定重定向位置
      if (userStore.isAdmin) {
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
