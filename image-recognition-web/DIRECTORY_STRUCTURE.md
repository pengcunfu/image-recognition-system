# 前端项目目录结构说明

## 📁 项目结构组织

项目采用模块化、分层的目录结构，便于维护和扩展。

### 完整目录结构

```
src/
├── api/                # API接口模块
├── components/         # 公共组件
├── layout/            # 布局组件 ⭐ 新位置
│   ├── DashboardLayout.vue
│   └── UserLayout.vue
├── router/            # 路由配置
├── stores/            # 状态管理
├── utils/             # 工具函数
├── views/             # 视图页面
├── App.vue            # 根组件
└── main.ts            # 入口文件
```

### 视图目录结构

```
src/views/
├── admin/              # 管理员视图 (11个文件)
│   ├── AdminProfileView.vue          # 管理员个人资料
│   ├── AnalyticsView.vue             # 数据分析
│   ├── CategoryManagementView.vue    # 知识库分类管理
│   ├── DashboardView.vue             # 管理员仪表板
│   ├── KnowledgeManagementView.vue   # 知识库管理
│   ├── OrdersView.vue                # 订单管理
│   ├── PostsManagementView.vue       # 帖子管理
│   ├── RecognitionManagementView.vue # 识别记录管理
│   ├── SettingsView.vue              # 系统设置
│   ├── UsersView.vue                 # 用户管理
│   └── VipManagementView.vue         # VIP管理
│
├── user/               # 用户视图 (10个文件)
│   ├── BatchRecognitionView.vue      # 批量识别
│   ├── CommunityView.vue             # 社区页面
│   ├── HistoryView.vue               # 历史记录
│   ├── ImageRecognitionView.vue      # 图像识别
│   ├── KnowledgeDetailView.vue       # 知识库详情
│   ├── KnowledgeView.vue             # 知识库浏览
│   ├── PostDetailView.vue            # 帖子详情
│   ├── RecognitionDetailView.vue     # 识别详情
│   ├── UserDashboardView.vue         # 用户仪表板
│   └── UserProfileView.vue           # 用户个人资料
│
├── vip/                # VIP专属视图 (4个文件)
│   ├── AdvancedRecognitionView.vue   # 高级识别
│   ├── AiTrainingView.vue            # AI模型训练
│   ├── ApiAccessView.vue             # API访问管理
│   └── VipAnalyticsView.vue          # VIP数据分析
│
└── (根目录)            # 公共视图 (7个文件)
    ├── AboutView.vue                 # 关于我们
    ├── ContactView.vue               # 联系我们
    ├── ForgotPasswordView.vue        # 忘记密码
    ├── LoginView.vue                 # 登录
    ├── PrivacyView.vue               # 隐私政策
    ├── RegisterView.vue              # 注册
    └── TermsView.vue                 # 服务条款
```

## 📊 统计信息

| 目录 | 文件数 | 功能说明 |
|------|--------|----------|
| `admin/` | 11 | 管理员后台管理功能 |
| `user/` | 10 | 普通用户功能页面 |
| `vip/` | 4 | VIP会员专属功能 |
| 根目录 | 7 | 公共页面（认证、信息页面） |
| **总计** | **32** | **所有视图文件** |

## 🎯 路由组织

### 管理员路由（需要管理员权限）
- `/dashboard` - 仪表板
- `/users` - 用户管理
- `/analytics` - 数据分析
- `/posts-management` - 帖子管理
- `/knowledge-management` - 知识库管理
- `/category-management` - 分类管理
- `/vip-management` - VIP管理
- `/recognition-management` - 识别记录管理
- `/orders` - 订单管理
- `/settings` - 系统设置
- `/admin-profile` - 管理员资料

### 用户路由（需要登录）
- `/user/dashboard` - 用户首页
- `/user/recognition` - 图像识别
- `/user/recognition/batch` - 批量识别
- `/user/history` - 历史记录
- `/user/recognition/:id` - 识别详情
- `/user/knowledge` - 知识库
- `/user/knowledge/:id` - 知识详情
- `/user/community` - 社区
- `/user/community/post/:id` - 帖子详情
- `/user/profile` - 个人中心

### VIP路由（需要VIP权限）
- `/user/advanced-recognition` - 高级识别
- `/user/vip-analytics` - VIP数据分析
- `/user/ai-training` - AI模型训练
- `/user/api-access` - API访问管理

### 公共路由（无需登录）
- `/login` - 登录
- `/register` - 注册
- `/forgot-password` - 忘记密码
- `/about` - 关于我们
- `/contact` - 联系我们
- `/privacy` - 隐私政策
- `/terms` - 服务条款

## 🔐 权限说明

### 权限级别
1. **公共访问** - 无需登录即可访问
2. **用户访问** - 需要登录（`requiresAuth: true`）
3. **VIP访问** - 需要VIP权限（`requiresVip: true`）
4. **管理员访问** - 需要管理员权限（`requiresAdmin: true`）

### 路由守卫
路由守卫在 `src/router/index.ts` 中配置：
- 检查用户登录状态
- 验证用户角色权限
- 自动重定向到相应页面

## 🎨 布局组件

### DashboardLayout
- **路径**: `src/layout/DashboardLayout.vue` ⭐ 已移至src根目录
- **用途**: 管理员后台布局
- **特点**: 侧边栏导航、固定头部、内容区域
- **导入**: `import DashboardLayout from '@/layout/DashboardLayout.vue'`

### UserLayout
- **路径**: `src/layout/UserLayout.vue` ⭐ 已移至src根目录
- **用途**: 用户前台布局
- **特点**: 顶部导航栏、页脚、响应式设计
- **导入**: `import UserLayout from '@/layout/UserLayout.vue'`

## 📝 命名规范

### 文件命名
- 所有视图文件使用 `PascalCase` + `View.vue` 后缀
- 例如: `DashboardView.vue`, `UserProfileView.vue`

### 路由命名
- 路由名称使用 `PascalCase`
- 例如: `Dashboard`, `UserProfile`, `ImageRecognition`

### 目录命名
- 目录名使用 `kebab-case` 或 `camelCase`
- 例如: `admin/`, `user/`, `vip/`

## 🔄 重构历史

### 2025-10-20
#### Layout组件重构
- ✅ 将 `layout` 目录从 `components/` 移至 `src/` 根目录
- ✅ 更新路由导入路径：`@/components/layout` → `@/layout`
- ✅ 布局组件现在位于 `src/layout/`

#### 视图文件重组
- ✅ 创建 `admin/` 目录
- ✅ 移动 11 个管理员视图文件到 `views/admin/`
- ✅ 更新路由配置导入路径
- ✅ 验证构建和类型检查通过

### 之前的重构
- ✅ 移除所有 CSS/SCSS 文件
- ✅ 采用 Ant Design Vue 组件库
- ✅ 使用行内样式替代样式文件
- ✅ 整合图标系统为 Ant Design Icons

## 💡 最佳实践

### 添加新视图
1. 确定视图所属类别（admin/user/vip/public）
2. 在相应目录创建 `.vue` 文件
3. 使用 Ant Design Vue 组件
4. 在 `router/index.ts` 中添加路由配置
5. 设置正确的权限元信息

### 代码组织
- 每个视图文件应该职责单一
- 复杂逻辑抽取到 composables
- API 调用统一通过 `src/api` 模块
- 避免在视图中直接操作 DOM

### 性能优化
- 使用路由懒加载（已实现）
- 大型列表使用虚拟滚动
- 图片使用懒加载
- 合理使用 Vue 的性能优化特性

## 🚀 未来规划

- [ ] 考虑进一步细分 `admin/` 目录（如 `admin/content/`, `admin/system/`）
- [ ] 实现视图级别的代码分割
- [ ] 添加视图单元测试
- [ ] 完善错误边界处理

---

**最后更新**: 2025-10-20  
**维护者**: 彭存福

