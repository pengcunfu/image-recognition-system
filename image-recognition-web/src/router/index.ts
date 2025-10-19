import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { message } from 'ant-design-vue'

// 路由组件
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'

// 管理员组件
import DashboardLayout from '@/layout/DashboardLayout.vue'
import DashboardView from '@/views/admin/DashboardView.vue'
import UsersView from '@/views/admin/UsersView.vue'
import OrdersView from '@/views/admin/OrdersView.vue'
import AnalyticsView from '@/views/admin/AnalyticsView.vue'
import SettingsView from '@/views/admin/SettingsView.vue'
import PostsManagementView from '@/views/admin/PostsManagementView.vue'
import KnowledgeManagementView from '@/views/admin/KnowledgeManagementView.vue'
import CategoryManagementView from '@/views/admin/CategoryManagementView.vue'
import VipManagementView from '@/views/admin/VipManagementView.vue'
import RecognitionManagementView from '@/views/admin/RecognitionManagementView.vue'
import AdminProfileView from '@/views/admin/AdminProfileView.vue'

// 用户组件
import UserLayout from '@/layout/UserLayout.vue'
import UserDashboardView from '@/views/user/UserDashboardView.vue'
import ImageRecognitionView from '@/views/user/ImageRecognitionView.vue'
import BatchRecognitionView from '@/views/user/BatchRecognitionView.vue'
import HistoryView from '@/views/user/HistoryView.vue'
import KnowledgeView from '@/views/user/KnowledgeView.vue'
import CommunityView from '@/views/user/CommunityView.vue'
import UserProfileView from '@/views/user/UserProfileView.vue'
import PostDetailView from '@/views/user/PostDetailView.vue'
import KnowledgeDetailView from '@/views/user/KnowledgeDetailView.vue'
import RecognitionDetailView from '@/views/user/RecognitionDetailView.vue'

// VIP用户组件
import AdvancedRecognitionView from '@/views/vip/AdvancedRecognitionView.vue'
import VipAnalyticsView from '@/views/vip/VipAnalyticsView.vue'
import AiTrainingView from '@/views/vip/AiTrainingView.vue'
import ApiAccessView from '@/views/vip/ApiAccessView.vue'

// 公共页面组件
import AboutView from '@/views/AboutView.vue'
import ContactView from '@/views/ContactView.vue'
import PrivacyView from '@/views/PrivacyView.vue'
import TermsView from '@/views/TermsView.vue'

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
    path: '/category-management',
    component: DashboardLayout,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: '',
        name: 'CategoryManagement',
        component: CategoryManagementView,
        meta: {
          title: '分类管理 - 智能图像识别系统'
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
        component: CommunityView,
        meta: {
          title: '社区 - 智能图像识别系统'
        }
      },
      {
        path: 'community/post/:id',
        name: 'PostDetail',
        component: PostDetailView,
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
  // 公共页面路由（不需要登录）
  {
    path: '/about',
    name: 'About',
    component: AboutView,
    meta: {
      title: '关于我们 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/contact',
    name: 'Contact',
    component: ContactView,
    meta: {
      title: '联系我们 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/privacy',
    name: 'Privacy',
    component: PrivacyView,
    meta: {
      title: '隐私政策 - 智能图像识别系统',
      requiresAuth: false
    }
  },
  {
    path: '/terms',
    name: 'Terms',
    component: TermsView,
    meta: {
      title: '服务条款 - 智能图像识别系统',
      requiresAuth: false
    }
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
