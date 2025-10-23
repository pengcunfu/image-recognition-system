#!/bin/bash

# 智能图像识别系统 - Docker 构建脚本
# 用法: ./build.sh [选项]
# 选项:
#   all       - 构建所有服务（默认）
#   backend   - 仅构建后端
#   frontend  - 仅构建前端
#   clean     - 清理并重新构建

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印函数
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    print_info "Docker 和 Docker Compose 已安装"
}

# 检查环境变量文件
check_env() {
    if [ ! -f ".env" ]; then
        print_warn ".env 文件不存在"
        if [ -f ".env.example" ]; then
            print_info "从 .env.example 复制创建 .env 文件"
            cp .env.example .env
            print_warn "请编辑 .env 文件，填入正确的配置信息"
        else
            print_error ".env.example 文件不存在，无法创建 .env"
            exit 1
        fi
    else
        print_info ".env 文件存在"
    fi
}

# 构建所有服务
build_all() {
    print_info "开始构建所有服务..."
    cd ..
    docker-compose build
    print_info "所有服务构建完成"
}

# 构建后端
build_backend() {
    print_info "开始构建后端服务..."
    cd ..
    docker-compose build backend
    print_info "后端服务构建完成"
}

# 构建前端
build_frontend() {
    print_info "开始构建前端服务..."
    cd ..
    docker-compose build frontend
    print_info "前端服务构建完成"
}

# 清理并重新构建
build_clean() {
    print_info "停止并删除所有容器..."
    cd ..
    docker-compose down
    
    print_info "清理 Docker 缓存..."
    docker system prune -f
    
    print_info "重新构建所有服务..."
    docker-compose build --no-cache
    
    print_info "清理并重新构建完成"
}

# 主函数
main() {
    print_info "智能图像识别系统 - Docker 构建脚本"
    
    # 检查 Docker
    check_docker
    
    # 检查环境变量
    check_env
    
    # 根据参数执行相应操作
    case "${1:-all}" in
        all)
            build_all
            ;;
        backend)
            build_backend
            ;;
        frontend)
            build_frontend
            ;;
        clean)
            build_clean
            ;;
        *)
            print_error "未知选项: $1"
            echo "用法: $0 [all|backend|frontend|clean]"
            exit 1
            ;;
    esac
    
    print_info "构建脚本执行完成"
    print_info "使用 'docker-compose up -d' 启动服务"
}

# 执行主函数
main "$@"

