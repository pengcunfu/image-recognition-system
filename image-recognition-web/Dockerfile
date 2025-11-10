# 构建阶段
FROM node:20-alpine AS build

# 安装必要的构建工具
RUN apk add --no-cache python3 make g++

# 设置工作目录
WORKDIR /app

# 复制npm国内镜像配置
COPY ../docker/npm/.npmrc /root/.npmrc

# 复制前端项目文件
COPY package*.json ./

# 安装依赖（生产环境需要devDependencies来构建）
RUN npm ci

# 复制前端源代码
COPY . .

# 构建前端应用
RUN npm run build

# 生产阶段 - 使用 Nginx 提供静态文件服务
FROM nginx:1.25-alpine

# 安装必要工具
RUN apk add --no-cache curl

# 复制构建好的静态文件
COPY --from=build /app/dist /usr/share/nginx/html

# 复制自定义 nginx 配置
COPY ../docker/nginx/nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:80/ || exit 1

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
