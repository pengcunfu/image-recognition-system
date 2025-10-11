# 智能图像识别系统

> 基于 Doubao-1.5-thinking-vision-pro 的通用图像识别平台

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5-blue.svg)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## 项目简介

智能图像识别系统是一个基于人工智能的图像分析平台，能够精准识别图像中的物体种类、名称、颜色、形状、材质等核心属性。系统采用前后端分离架构，集成了火山引擎 Doubao AI 大模型，提供高精度的图像识别服务。

### 核心特性

- **精准识别**: 基于 Doubao-1.5-thinking-vision-pro 大模型，识别准确率高
- **高效处理**: 精炼的提示词设计，节省 30-50% Token 消耗
- **结构化输出**: 标准 JSON 格式输出，便于程序解析和处理
- **批量处理**: 支持多图像批量识别，提高处理效率
- **Web界面**: 现代化的 Vue.js 前端界面，用户体验友好
- **容器化部署**: 完整的 Docker 部署方案，一键启动
- **监控运维**: 集成 Prometheus + Grafana 监控体系

### 技术架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 (Vue.js)  │    │  后端 (Spring)   │    │  AI 服务 (Doubao) │
│   - Ant Design  │◄──►│  - Spring Boot   │◄──►│  - 图像识别      │
│   - Nginx       │    │  - Spring Security│    │  - 自然语言处理   │
│   - TypeScript  │    │  - MyBatis       │    │  - 多模态理解     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   数据存储       │    │   缓存服务       │    │   文件存储       │
│   - MySQL 8.0   │    │   - Redis 7     │    │   - 本地/云存储   │
│   - 用户数据     │    │   - 会话缓存     │    │   - 图像文件     │
│   - 识别结果     │    │   - 结果缓存     │    │   - 日志文件     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 快速开始

### 开发模式（推荐）

使用热重载功能进行开发：

**Windows:**
```bash
./dev-start.bat
```

**Linux/macOS:**
```bash
chmod +x dev-start.sh
./dev-start.sh
```

或手动启动：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 环境要求

- **Java**: JDK 17+
- **Node.js**: 20.19.0+
- **Docker**: 20.10.0+ (推荐)
- **MySQL**: 8.0+ (或使用 Docker)
- **Redis**: 7+ (或使用 Docker)

### 一键部署 (推荐)

使用 Docker Compose 进行一键部署：

```bash
# 1. 克隆项目
git clone https://github.com/pengcunfu/image-recognition-system.git
cd image-recognition-system

# 2. 配置环境变量
cp env.example .env
# 编辑 .env 文件，设置您的 API 密钥

# 3. 一键部署
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

**Windows 用户:**
```cmd
scripts\deploy.bat
```

**远程数据库部署:**
```bash
# 使用远程数据库 (8.155.40.179)
cp env.remote.example .env
./scripts/deploy-remote.sh
```

### 手动部署

<details>
<summary>点击展开手动部署步骤</summary>

#### 后端部署

```bash
# 1. 数据库准备
mysql -u root -p < docker/mysql/init.sql

# 2. 编译运行
mvn clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

#### 前端部署

```bash
cd frontend
npm install
npm run build

# 使用 Nginx 部署
cp dist/* /var/www/html/
```

</details>

## 访问地址

部署完成后，您可以通过以下地址访问：

- **前端界面**: http://localhost:80
- **后端API**: http://localhost:8080/api/v1
- **API文档**: http://localhost:8080/api/v1/swagger-ui.html
- **健康检查**: http://localhost:8080/api/v1/doubao/image/status
- **监控面板**: http://localhost:3000 (Grafana)

## API 使用示例

### 文件上传识别

```bash
curl -X POST "http://localhost:8080/api/v1/doubao/image/recognize/upload" \
  -F "file=@your_image.jpg" \
  -F "detailedAnalysis=false" \
  -F "maxTokens=500"
```

### JSON 请求识别

```bash
curl -X POST "http://localhost:8080/api/v1/doubao/image/recognize/json" \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://example.com/image.jpg",
    "detailedAnalysis": false,
    "maxTokens": 500,
    "temperature": 0.1
  }'
```

### 响应示例

```json
{
  "success": true,
  "message": "图像识别成功",
  "data": {
    "success": true,
    "data": {
      "category": "花卉",
      "name": "大丽花",
      "color": "暖黄色",
      "shape": "多层花瓣呈放射状排列，整体饱满圆润",
      "material": "植物花瓣（表面有水珠，呈现湿润质感）",
      "attributes": ["多层花瓣", "放射状", "饱满圆润"],
      "confidence": 0.95
    },
    "processingTime": 1250,
    "tokenUsage": 156
  }
}
```

## 功能特性

### 图像识别能力

- **物体分类**: 准确识别图像中的主要物体类别
- **名称识别**: 提供具体的物体名称和品种信息
- **颜色分析**: 精确描述物体的主要颜色特征
- **形状描述**: 详细分析物体的形状和结构特点
- **材质识别**: 判断物体的材质和质感特征
- **属性提取**: 提取物体的关键属性和特征标签

### 系统功能

- **用户管理**: 用户注册、登录、权限控制
- **批量处理**: 支持多图像批量上传和识别
- **历史记录**: 完整的识别历史记录和结果管理
- **API 接口**: RESTful API 支持第三方集成
- **实时监控**: 系统性能和服务状态监控
- **数据统计**: 识别结果统计和分析报表

## 配置说明

### 环境变量配置

```bash
# Doubao AI 配置 (必需)
DOUBAO_API_KEY=your_api_key_here

# 数据库配置
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_ROOT_PASSWORD=your_root_password

# Redis 配置
REDIS_PASSWORD=your_redis_password

# JWT 配置
JWT_SECRET=your_jwt_secret

# 火山引擎配置 (可选)
VOLCENGINE_ACCESS_KEY_ID=your_access_key
VOLCENGINE_SECRET_ACCESS_KEY=your_secret_key
```

### 提示词配置

系统内置了优化的提示词模板，支持自定义：

```properties
# 默认提示词 (精炼版)
image.recognition.prompt.default=分析图像并以JSON格式输出核心属性...

# 详细分析提示词
image.recognition.prompt.detailed=详细分析图像中的所有元素...
```

## 开发指南

### 项目结构

```
image-recognition-system/
├── backend/                 # 后端代码
│   ├── src/main/java/      # Java 源码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven 配置
├── frontend/               # 前端代码
│   ├── src/               # Vue.js 源码
│   ├── public/            # 静态资源
│   └── package.json       # Node.js 配置
├── docker/                # Docker 配置
│   ├── mysql/             # 数据库配置
│   └── prometheus/        # 监控配置
├── scripts/               # 部署脚本
└── docs/                  # 项目文档
```

### 本地开发

```bash
# 后端开发
mvn spring-boot:run

# 前端开发
cd frontend
npm run dev

# 数据库
docker-compose up -d mysql redis
```

### 代码规范

- **Java**: 遵循 Google Java Style Guide
- **JavaScript/TypeScript**: 遵循 ESLint 标准
- **提交信息**: 遵循 Conventional Commits 规范

## 性能指标

- **识别速度**: 平均响应时间 < 2秒
- **识别准确率**: > 95% (基于测试数据集)
- **并发支持**: 支持 100+ 并发请求
- **Token 效率**: 相比标准提示词节省 30-50% Token

## 安全特性

- **身份认证**: JWT Token 认证机制
- **权限控制**: 基于角色的访问控制 (RBAC)
- **数据加密**: 敏感数据加密存储
- **API 限流**: 防止 API 滥用和攻击
- **文件验证**: 上传文件格式和大小验证
- **SQL 注入防护**: MyBatis 参数化查询

## 监控运维

### 健康检查

```bash
# 系统健康检查
./scripts/health-check.sh

# 服务状态
curl http://localhost:8080/api/v1/doubao/image/status
```

### 日志管理

```bash
# 查看应用日志
docker-compose logs -f backend

# 查看访问日志
docker-compose logs -f frontend
```

### 数据备份

```bash
# 数据库备份
./scripts/backup.sh

# 定时备份 (每天凌晨2点)
echo "0 2 * * * /path/to/scripts/backup.sh" | crontab -
```

## 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本项目
2. 创建功能分支: `git checkout -b feature/your-feature`
3. 提交更改: `git commit -am 'Add some feature'`
4. 推送分支: `git push origin feature/your-feature`
5. 提交 Pull Request

## 问题反馈

如遇到问题，请通过以下方式反馈：

- **GitHub Issues**: [提交问题](https://github.com/pengcunfu/image-recognition-system/issues)
- **邮箱**: 3173484026@qq.com
- **文档**: 查看 [完整文档](./docs/)

## 相关链接

- **项目参考**: https://github.com/dengcao/AI-Intelligent-Recognition.git
- **Doubao API 文档**: https://www.volcengine.com/docs/82379
- **火山引擎控制台**: https://console.volcengine.com/
- **Docker 部署指南**: [Docker 部署文档](./docs/DOCKER_DEPLOYMENT.md)

---

如果这个项目对您有帮助，请给我们一个 Star！

**版本**: v1.0.0  
**最后更新**: 2025年10月11日  
**作者**: pengcunfu
