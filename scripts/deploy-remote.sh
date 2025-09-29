#!/bin/bash

# 图像识别系统 Docker 部署脚本 (远程数据库版本)
# 远程数据库服务器: 8.155.40.179

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
        warn ".env 文件不存在，从远程数据库示例文件创建..."
        if [ -f "env.remote.example" ]; then
            cp env.remote.example .env
            warn "已创建 .env 文件，请编辑 .env 文件设置您的配置"
            warn "重要: 请设置正确的数据库密码和Redis密码"
            read -p "是否现在编辑 .env 文件? (y/n): " edit_env
            if [ "$edit_env" = "y" ] || [ "$edit_env" = "Y" ]; then
                ${EDITOR:-nano} .env
            fi
        else
            error "env.remote.example 文件不存在，无法创建 .env 文件"
        fi
    fi
    
    # 检查关键配置
    if grep -q "your_mysql_password" .env; then
        warn "检测到默认的数据库密码，请设置正确的密码"
    fi
    
    if grep -q "your_redis_password" .env; then
        warn "检测到默认的Redis密码，请设置正确的密码"
    fi
    
    info "环境变量配置检查完成"
}

# 检查远程服务连接
check_remote_services() {
    log "检查远程服务连接..."
    
    # 检查MySQL连接
    info "检查MySQL连接 (8.155.40.179:3306)..."
    if timeout 10 bash -c "</dev/tcp/8.155.40.179/3306"; then
        log "✓ MySQL服务器连接正常"
    else
        warn "✗ 无法连接到MySQL服务器，请检查网络和防火墙设置"
    fi
    
    # 检查Redis连接
    info "检查Redis连接 (8.155.40.179:6379)..."
    if timeout 10 bash -c "</dev/tcp/8.155.40.179/6379"; then
        log "✓ Redis服务器连接正常"
    else
        warn "✗ 无法连接到Redis服务器，请检查网络和防火墙设置"
    fi
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
    
    # 启动后端服务
    info "启动后端服务..."
    docker-compose up -d backend
    
    # 等待后端启动
    info "等待后端服务启动..."
    sleep 30
    
    # 启动前端服务
    info "启动前端服务..."
    docker-compose up -d frontend
    
    log "所有服务启动完成"
}

# 检查服务健康状态
check_health() {
    log "检查服务健康状态..."
    
    # 检查后端
    local max_attempts=5
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s http://localhost:8080/api/v1/doubao/image/status &> /dev/null; then
            info "✓ 后端服务运行正常"
            break
        else
            warn "后端服务检查失败，尝试 $attempt/$max_attempts"
            if [ $attempt -eq $max_attempts ]; then
                error "✗ 后端服务启动失败，请检查日志"
            fi
            sleep 10
            ((attempt++))
        fi
    done
    
    # 检查前端
    if curl -f -s http://localhost:80/health &> /dev/null; then
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
    echo "🗄️  远程数据库: 8.155.40.179:3306"
    echo "📊 远程Redis: 8.155.40.179:6379"
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

# 主要部署流程
main() {
    echo "========================================"
    echo "  图像识别系统 Docker 部署脚本"
    echo "      (远程数据库版本)"
    echo "  数据库服务器: 8.155.40.179"
    echo "========================================"
    echo ""
    
    check_requirements
    check_env_file
    check_remote_services
    build_images
    start_services
    check_health
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
    "check")
        log "检查远程服务连接..."
        check_remote_services
        ;;
    *)
        main
        ;;
esac
