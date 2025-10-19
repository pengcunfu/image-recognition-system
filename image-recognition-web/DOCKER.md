# Docker 部署指南

本文档说明如何使用 Docker 构建和部署前端应用。

## 文件说明

- **Dockerfile**: 多阶段构建配置，包含构建和生产两个阶段
- **.npmrc**: npm 国内镜像配置，加速依赖安装
- **nginx.conf**: Nginx 配置文件，用于生产环境提供静态文件服务
- **.dockerignore**: Docker 构建时忽略的文件

## 本地构建

### 1. 构建镜像

```powershell
# 在 image-recognition-web 目录下执行
docker build -t image-recognition-web:latest .
```

### 2. 运行容器

```powershell
# 运行容器，映射到本地 3000 端口
docker run -d -p 3000:80 --name frontend image-recognition-web:latest
```

### 3. 查看日志

```powershell
docker logs frontend
```

### 4. 停止和删除容器

```powershell
docker stop frontend
docker rm frontend
```

## Docker Compose 部署

如果与后端一起部署，请参考项目根目录的 `docker-compose.yml` 文件。

### 启动所有服务

```powershell
# 在项目根目录执行
docker-compose up -d
```

### 查看前端服务状态

```powershell
docker-compose ps frontend
```

### 查看前端日志

```powershell
docker-compose logs -f frontend
```

### 重新构建前端

```powershell
docker-compose build frontend
docker-compose up -d frontend
```

### 停止所有服务

```powershell
docker-compose down
```

## 环境变量配置

构建时可以通过 build args 传入环境变量：

```powershell
docker build --build-arg VITE_API_BASE_URL=http://api.example.com -t image-recognition-web:latest .
```

或者在 docker-compose.yml 中配置：

```yaml
services:
  frontend:
    build:
      context: ./image-recognition-web
      args:
        VITE_API_BASE_URL: ${API_BASE_URL}
```

## Nginx 配置说明

### 静态文件缓存

- JS、CSS、图片等静态资源：缓存 1 年
- index.html：不缓存，确保始终获取最新版本

### API 代理

所有 `/api/` 开头的请求会被代理到后端服务：

```
http://localhost:3000/api/users -> http://backend:8080/api/users
```

### SPA 路由支持

配置了 `try_files` 确保 Vue Router 的 history 模式正常工作。

### 文件上传

- 最大上传大小：10MB
- 超时时间：60秒

可根据需要在 `nginx.conf` 中调整。

## 健康检查

容器包含健康检查配置：

- 检查间隔：30秒
- 超时时间：10秒
- 启动期：30秒
- 重试次数：3次

查看健康状态：

```powershell
docker inspect --format='{{.State.Health.Status}}' frontend
```

## 优化建议

### 1. 镜像大小优化

当前使用 Alpine Linux 基础镜像，已经比较小。如需进一步优化：

- 删除不必要的依赖
- 使用 multi-stage build（已实现）
- 清理构建缓存

### 2. 构建速度优化

- 使用 `.dockerignore` 减少构建上下文
- 使用 npm 国内镜像加速依赖安装（已配置）
- 使用 Docker 缓存层

### 3. 生产环境优化

- 启用 CDN 加速静态资源
- 配置 HTTPS（在反向代理层）
- 启用 HTTP/2
- 配置安全响应头

## 故障排查

### 容器无法启动

```powershell
# 查看容器日志
docker logs frontend

# 进入容器检查
docker exec -it frontend sh
```

### Nginx 配置错误

```powershell
# 测试 nginx 配置
docker exec frontend nginx -t

# 重新加载配置
docker exec frontend nginx -s reload
```

### 构建失败

```powershell
# 清理构建缓存
docker builder prune

# 使用 --no-cache 重新构建
docker build --no-cache -t image-recognition-web:latest .
```

## 常见问题

### Q: API 请求失败？

A: 检查 nginx.conf 中的 `proxy_pass` 配置，确保后端服务地址正确。

### Q: 页面刷新 404？

A: 确保 nginx.conf 中配置了 `try_files $uri $uri/ /index.html`。

### Q: 静态资源加载慢？

A: 检查是否启用了 gzip 压缩和静态资源缓存。

## 安全建议

1. 不要在镜像中包含敏感信息
2. 使用非 root 用户运行容器（可选优化）
3. 定期更新基础镜像
4. 扫描镜像漏洞：`docker scan image-recognition-web:latest`

