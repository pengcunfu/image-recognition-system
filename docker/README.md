# Docker 部署说明

本目录包含智能图像识别系统的 Docker 部署配置文件。

## 目录结构

```
docker/
├── Dockerfile.backend      # 后端服务 Dockerfile
├── Dockerfile.frontend     # 前端服务 Dockerfile
├── mysql/                  # MySQL 配置
│   ├── my.cnf             # MySQL 配置文件
│   └── image_recognition.sql  # 数据库初始化脚本
├── nginx/                  # Nginx 配置
│   └── nginx.conf         # Nginx 配置文件
└── README.md              # 本文件
```

## 部署前准备

### 1. 安装 Docker 和 Docker Compose

确保已安装 Docker 和 Docker Compose：
```bash
docker --version
docker-compose --version
```

### 2. 配置环境变量

复制环境变量模板文件并修改：
```bash
cp .env.example .env
```

编辑 `.env` 文件，填入实际的配置信息：
- `DOUBAO_API_KEY`: 火山引擎豆包 API 密钥
- `JWT_SECRET`: JWT 加密密钥（建议使用随机字符串）
- `VOLCENGINE_ACCESS_KEY_ID`: 火山引擎对象存储访问密钥
- `VOLCENGINE_SECRET_ACCESS_KEY`: 火山引擎对象存储密钥

### 3. 准备数据库文件

确保 `docker/mysql/image_recognition.sql` 文件存在且为最新版本。

## 快速部署

### 1. 构建并启动所有服务

```bash
docker-compose up -d
```

### 2. 查看服务状态

```bash
docker-compose ps
```

### 3. 查看服务日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
docker-compose logs -f redis
```

## 服务访问

部署成功后，可以通过以下地址访问服务：

- **前端界面**: http://localhost
- **后端API**: http://localhost:9094/api
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

## 常用命令

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart backend
docker-compose restart frontend
```

### 停止服务

```bash
# 停止所有服务
docker-compose stop

# 停止特定服务
docker-compose stop backend
```

### 删除服务

```bash
# 停止并删除所有容器
docker-compose down

# 停止并删除所有容器及数据卷（谨慎使用）
docker-compose down -v
```

### 重新构建服务

```bash
# 重新构建所有服务
docker-compose build

# 重新构建特定服务
docker-compose build backend
docker-compose build frontend

# 重新构建并启动
docker-compose up -d --build
```

### 进入容器

```bash
# 进入后端容器
docker-compose exec backend sh

# 进入前端容器
docker-compose exec frontend sh

# 进入MySQL容器
docker-compose exec mysql bash

# 进入Redis容器
docker-compose exec redis sh
```

## 数据持久化

数据会持久化存储在以下目录（相对于用户主目录）：

```
~/image-recognition-data/
├── mysql/
│   ├── data/          # MySQL 数据文件
│   └── logs/          # MySQL 日志
├── redis/
│   └── data/          # Redis 数据文件
├── backend/
│   ├── logs/          # 后端日志
│   └── uploads/       # 上传的文件
└── frontend/
    └── logs/          # Nginx 日志
```

## 故障排查

### 1. 后端服务启动失败

检查日志：
```bash
docker-compose logs backend
```

常见问题：
- 数据库连接失败：检查 MySQL 是否正常启动
- 环境变量配置错误：检查 `.env` 文件配置

### 2. 前端无法访问后端

检查 Nginx 配置：
```bash
docker-compose exec frontend cat /etc/nginx/nginx.conf
```

确认后端服务正常：
```bash
curl http://localhost:9094/api/health
```

### 3. 数据库初始化失败

删除数据卷并重新初始化：
```bash
docker-compose down -v
docker-compose up -d
```

### 4. 查看容器资源使用

```bash
docker stats
```

## 更新部署

### 更新代码后重新部署

```bash
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build
```

### 仅更新前端

```bash
docker-compose up -d --build frontend
```

### 仅更新后端

```bash
docker-compose up -d --build backend
```

## 生产环境建议

1. **安全性**
   - 修改默认密码
   - 使用强密码
   - 配置防火墙规则
   - 启用 HTTPS（配置 SSL 证书）

2. **性能优化**
   - 调整 MySQL 配置参数
   - 配置 Redis 持久化策略
   - 调整 JVM 内存参数

3. **监控**
   - 配置日志收集
   - 设置告警通知
   - 监控资源使用

4. **备份**
   - 定期备份数据库
   - 备份上传的文件
   - 备份配置文件

## 技术栈

- **前端**: Vue 3 + TypeScript + Vite + Ant Design Vue
- **后端**: Spring Boot 3 + Java 17 + MyBatis Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **Web服务器**: Nginx 1.25
- **容器**: Docker + Docker Compose

## 支持

如有问题，请查看：
- 项目文档
- GitHub Issues
- 日志文件

