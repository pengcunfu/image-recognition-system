# 智能图像识别系统 - 前端项目

基于 Vue 3 + TypeScript + Ant Design Vue 的图像识别系统前端应用。

## 技术栈

- **框架**: Vue 3.5.18
- **语言**: TypeScript 5.8.0
- **UI 库**: Ant Design Vue 4.2.6
- **图标**: @ant-design/icons-vue 7.0.1
- **状态管理**: Pinia 3.0.3
- **路由**: Vue Router 4.5.1
- **HTTP 客户端**: Axios 1.12.2
- **图表**: ECharts 6.0.0 + Vue-ECharts 8.0.1
- **日期处理**: dayjs 1.11.13
- **构建工具**: Vite 7.0.6

## 系统要求

- Node.js: ^20.19.0 || >=22.12.0
- npm: 最新版本

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

访问 http://localhost:5173 (默认端口)

### 生产构建

```bash
npm run build
```

### 类型检查

```bash
npm run type-check
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
image-recognition-web/
├── public/               # 静态资源
│   └── favicon.png
├── src/
│   ├── api/             # API 接口
│   │   ├── auth.ts      # 认证 API
│   │   ├── community.ts # 社区 API
│   │   ├── file.ts      # 文件 API
│   │   ├── knowledge.ts # 知识库 API
│   │   ├── types.ts     # 类型定义
│   │   ├── user.ts      # 用户 API
│   │   └── index.ts     # API 统一导出
│   ├── components/      # 组件
│   │   ├── layout/      # 布局组件
│   │   │   ├── DashboardLayout.vue  # 管理员布局
│   │   │   └── UserLayout.vue       # 用户布局
│   │   ├── PostCard.vue             # 帖子卡片
│   │   └── PostPublishModal.vue     # 发布帖子弹窗
│   ├── router/          # 路由配置
│   │   └── index.ts
│   ├── stores/          # Pinia 状态管理
│   │   └── counter.ts
│   ├── utils/           # 工具函数
│   │   └── request.ts   # Axios 封装
│   ├── views/           # 视图页面
│   │   ├── user/        # 用户页面
│   │   ├── vip/         # VIP 页面
│   │   └── ...          # 其他页面
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
├── package.json         # 项目配置
├── vite.config.ts       # Vite 配置
├── tsconfig.json        # TypeScript 配置
└── README.md            # 项目说明

```

## 功能模块

### 用户功能
- ✅ 用户注册/登录/忘记密码
- ✅ 图像识别（单张/批量）
- ✅ 识别历史记录
- ✅ 知识库浏览
- ✅ 社区互动（发帖、评论、点赞）
- ✅ 个人中心

### VIP 专属功能
- ✅ 高级识别（多目标检测、实例分割）
- ✅ VIP 数据分析
- ✅ AI 模型训练
- ✅ API 访问权限管理

### 管理员功能
- ✅ 用户管理
- ✅ 内容管理（帖子、知识库）
- ✅ 识别记录管理
- ✅ VIP 管理
- ✅ 订单管理
- ✅ 数据分析
- ✅ 系统设置

## 设计特点

### UI 设计
- **纯组件化**: 完全使用 Ant Design Vue 组件
- **无样式依赖**: 移除所有 CSS/SCSS，使用行内样式
- **图标系统**: 统一使用 Ant Design Icons
- **响应式**: 支持桌面端和移动端

### 代码规范
- **TypeScript 严格模式**: 确保类型安全
- **模块化 API**: 清晰的 API 模块划分
- **组件复用**: 高度可复用的组件设计

## 环境配置

### 开发环境
复制 `env.development` 文件并根据实际情况配置：

```env
# API 基础路径
VITE_API_BASE_URL=http://localhost:8080
VITE_API_PREFIX=/api/v1

# 应用配置
VITE_APP_TITLE=智能图像识别系统
VITE_APP_DESCRIPTION=基于深度学习的通用图像识别平台

# 文件上传配置
VITE_UPLOAD_MAX_SIZE=10485760
VITE_UPLOAD_ACCEPT=image/*

# 功能开关
VITE_ENABLE_MOCK=false
VITE_ENABLE_DEV_TOOLS=true
```

## API 接口

所有 API 接口统一通过 `src/api` 模块管理：

```typescript
import { AuthAPI, KnowledgeAPI, CommunityAPI, FileAPI } from '@/api'

// 认证
await AuthAPI.login({ username, password, captcha })

// 知识库
const categories = await KnowledgeAPI.getCategories()

// 社区
const posts = await CommunityAPI.getPosts({ page, pageSize })

// 文件上传
const result = await FileAPI.uploadFile(file)
```

## 开发指南

### 添加新页面

1. 在 `src/views` 下创建新的 Vue 文件
2. 使用 Ant Design 组件和行内样式
3. 在 `src/router/index.ts` 中添加路由配置
4. 确保 TypeScript 类型正确

### 添加新 API

1. 在 `src/api/types.ts` 中定义类型
2. 在对应的 API 模块中添加方法
3. 在 `src/api/index.ts` 中导出

### 样式处理

❌ 不要使用：
```vue
<style scoped>
.my-class {
  color: red;
}
</style>
```

✅ 推荐使用：
```vue
<div :style="{ color: 'red', fontSize: '14px', padding: '10px' }">
  内容
</div>
```

或者使用 Ant Design 的主题定制功能。

## 性能优化

### 已实施
- ✅ Vite 快速构建
- ✅ 组件按需加载
- ✅ 路由懒加载
- ✅ 图片懒加载

### 待优化
- ⚠️ 代码分割（当前打包文件较大）
- ⚠️ 组件级别的性能优化
- ⚠️ 图片资源 CDN 加速

## 浏览器兼容性

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 常见问题

### 开发服务器启动失败
确保 Node.js 版本正确，并删除 `node_modules` 重新安装依赖。

### 类型检查报错
运行 `npm run type-check` 查看详细错误信息，确保所有 TypeScript 类型正确。

### 构建后样式丢失
检查是否正确引入了 Ant Design Vue 的样式文件。

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'feat: add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 其他更改

## 许可证

本项目仅供学习交流使用。

## 联系方式

- 作者: 彭存福
- 学校: [您的学校]
- 专业: 计算机科学与技术

---

**最后更新**: 2025-10-19
