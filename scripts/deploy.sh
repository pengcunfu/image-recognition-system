#!/bin/bash

# 图像识别系统 Docker 部署脚本
# 作者: AI Assistant
# 版本: 1.0.0

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# 检查必要的命令
check_requirements() {
    log "检查系统要求..."
    
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装。请先安装 Docker"
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "Docker Compose 未安装。请先安装 Docker Compose"
    fi
    
    info "系统要求检查通过"
}

# 检查.env文件
check_env_file() {
    log "检查环境变量配置..."
    
    if [ ! -f ".env" ]; then
        warn ".env 文件不存在，从示例文件创建..."
        if [ -f "env.example" ]; then
            cp env.example .env
            warn "已创建 .env 文件，请编辑 .env 文件设置您的配置"
            warn "特别注意设置 DOUBAO_API_KEY"
            read -p "是否现在编辑 .env 文件? (y/n): " edit_env
            if [ "$edit_env" = "y" ] || [ "$edit_env" = "Y" ]; then
                ${EDITOR:-nano} .env
            fi
        else
            error "env.example 文件不存在，无法创建 .env 文件"
        fi
    fi
    
    # 检查关键配置
    if grep -q "DOUBAO_API_KEY=api-key-20250929130415" .env; then
        warn "检测到默认的 DOUBAO_API_KEY，请确保已设置正确的API密钥"
    fi
    
    info "环境变量配置检查完成"
}

# 创建必要的目录
create_directories() {
    log "创建必要的目录..."
    
    mkdir -p docker/mysql
    mkdir -p docker/prometheus
    mkdir -p logs
    mkdir -p uploads
    mkdir -p backups
    
    info "目录创建完成"
}

# 构建镜像
build_images() {
    log "构建 Docker 镜像..."
    
    # 构建后端镜像
    info "构建后端镜像..."
    docker build -f Dockerfile.backend -t image-recognition-backend:latest .
    
    # 构建前端镜像
    info "构建前端镜像..."
    docker build -f Dockerfile.frontend -t image-recognition-frontend:latest .
    
    log "镜像构建完成"
}

# 启动服务
start_services() {
    log "启动 Docker 服务..."
    
    # 检查是否已有运行的容器
    if docker-compose ps | grep -q "Up"; then
        warn "检测到运行中的容器，将先停止现有服务..."
        docker-compose down
    fi
    
    # 启动基础服务
    info "启动数据库和缓存服务..."
    docker-compose up -d mysql redis
    
    # 等待数据库启动
    info "等待数据库启动..."
    sleep 30
    
    # 检查数据库健康状态
    while ! docker-compose exec mysql mysqladmin ping -h localhost --silent; do
        info "等待数据库启动完成..."
        sleep 5
    done
    
    # 启动应用服务
    info "启动应用服务..."
    docker-compose up -d backend
    
    # 等待后端启动
    info "等待后端服务启动..."
    sleep 20
    
    # 启动前端服务
    info "启动前端服务..."
    docker-compose up -d frontend
    
    log "所有服务启动完成"
}

# 检查服务健康状态
check_health() {
    log "检查服务健康状态..."
    
    # 检查数据库
    if docker-compose exec mysql mysqladmin ping -h localhost --silent; then
        info "✓ MySQL 数据库运行正常"
    else
        error "✗ MySQL 数据库连接失败"
    fi
    
    # 检查Redis
    if docker-compose exec redis redis-cli ping | grep -q "PONG"; then
        info "✓ Redis 缓存运行正常"
    else
        warn "✗ Redis 缓存连接失败"
    fi
    
    # 检查后端
    sleep 10
    if curl -f http://localhost:8080/api/v1/doubao/image/status &> /dev/null; then
        info "✓ 后端服务运行正常"
    else
        warn "✗ 后端服务连接失败，请检查日志"
    fi
    
    # 检查前端
    if curl -f http://localhost:80/health &> /dev/null; then
        info "✓ 前端服务运行正常"
    else
        warn "✗ 前端服务连接失败，请检查日志"
    fi
    
    log "健康检查完成"
}

# 显示服务信息
show_info() {
    log "服务信息:"
    echo ""
    echo "🌐 前端地址: http://localhost:80"
    echo "🔧 后端API: http://localhost:8080/api/v1"
    echo "🗄️  数据库: localhost:3306"
    echo "📊 Redis: localhost:6379"
    echo ""
    echo "🔍 API测试:"
    echo "  健康检查: curl http://localhost:8080/api/v1/doubao/image/status"
    echo "  连接测试: curl http://localhost:8080/api/v1/doubao/image/test"
    echo ""
    echo "📋 常用命令:"
    echo "  查看日志: docker-compose logs -f [service_name]"
    echo "  重启服务: docker-compose restart [service_name]"
    echo "  停止服务: docker-compose down"
    echo "  查看状态: docker-compose ps"
    echo ""
}

# 启动监控服务
start_monitoring() {
    read -p "是否启动监控服务 (Prometheus + Grafana)? (y/n): " start_monitor
    if [ "$start_monitor" = "y" ] || [ "$start_monitor" = "Y" ]; then
        log "启动监控服务..."
        docker-compose --profile monitoring up -d prometheus grafana
        echo ""
        echo "📊 监控服务:"
        echo "  Prometheus: http://localhost:9090"
        echo "  Grafana: http://localhost:3000 (admin/admin123)"
    fi
}

# 主要部署流程
main() {
    echo "========================================"
    echo "    图像识别系统 Docker 部署脚本"
    echo "========================================"
    echo ""
    
    check_requirements
    check_env_file
    create_directories
    build_images
    start_services
    check_health
    start_monitoring
    show_info
    
    log "🎉 部署完成！系统已成功启动"
}

# 命令行参数处理
case "${1:-}" in
    "build")
        log "仅构建镜像..."
        check_requirements
        build_images
        ;;
    "start")
        log "启动服务..."
        start_services
        check_health
        show_info
        ;;
    "stop")
        log "停止服务..."
        docker-compose down
        ;;
    "restart")
        log "重启服务..."
        docker-compose restart
        check_health
        ;;
    "logs")
        log "查看日志..."
        docker-compose logs -f ${2:-}
        ;;
    "status")
        log "检查服务状态..."
        docker-compose ps
        check_health
        ;;
    "clean")
        log "清理系统..."
        docker-compose down -v
        docker system prune -f
        ;;
    *)
        main
        ;;
esac
