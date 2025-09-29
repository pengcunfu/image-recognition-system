#!/bin/bash

# 系统健康检查脚本
# 检查所有服务的健康状态

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# 检查 Docker 服务
check_docker() {
    if ! docker ps &> /dev/null; then
        error "Docker服务未运行"
        return 1
    fi
    log "✓ Docker服务正常"
}

# 检查容器状态
check_containers() {
    local services=("mysql" "redis" "backend" "frontend")
    local all_healthy=true
    
    for service in "${services[@]}"; do
        if docker-compose ps | grep "$service" | grep -q "Up"; then
            log "✓ $service 容器运行正常"
        else
            error "✗ $service 容器未运行"
            all_healthy=false
        fi
    done
    
    return $($all_healthy && echo 0 || echo 1)
}

# 检查数据库连接
check_database() {
    if docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        log "✓ MySQL数据库连接正常"
        
        # 检查数据库大小
        local db_size=$(docker-compose exec -T mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD:-imagerecognition2024}" \
            -e "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) AS 'DB Size in MB' FROM information_schema.tables WHERE table_schema='image_recognition';" \
            --silent --skip-column-names 2>/dev/null)
        info "数据库大小: ${db_size}MB"
    else
        error "✗ MySQL数据库连接失败"
        return 1
    fi
}

# 检查Redis连接
check_redis() {
    if docker-compose exec -T redis redis-cli ping 2>/dev/null | grep -q "PONG"; then
        log "✓ Redis缓存连接正常"
        
        # 检查Redis内存使用
        local redis_memory=$(docker-compose exec -T redis redis-cli info memory | grep "used_memory_human" | cut -d: -f2 | tr -d '\r')
        info "Redis内存使用: $redis_memory"
    else
        warn "✗ Redis缓存连接失败"
    fi
}

# 检查后端API
check_backend() {
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s http://localhost:8080/api/v1/doubao/image/status &> /dev/null; then
            log "✓ 后端API服务正常"
            
            # 检查API响应时间
            local response_time=$(curl -o /dev/null -s -w '%{time_total}' http://localhost:8080/api/v1/doubao/image/status)
            info "API响应时间: ${response_time}s"
            return 0
        fi
        
        warn "后端API检查失败，尝试 $attempt/$max_attempts"
        sleep 5
        ((attempt++))
    done
    
    error "✗ 后端API服务连接失败"
    return 1
}

# 检查前端服务
check_frontend() {
    if curl -f -s http://localhost:80/health &> /dev/null; then
        log "✓ 前端服务正常"
    else
        warn "✗ 前端服务连接失败"
    fi
}

# 检查磁盘空间
check_disk_space() {
    local disk_usage=$(df -h . | awk 'NR==2 {print $5}' | sed 's/%//')
    
    if [ "$disk_usage" -lt 80 ]; then
        log "✓ 磁盘空间充足 (已使用: ${disk_usage}%)"
    elif [ "$disk_usage" -lt 90 ]; then
        warn "磁盘空间告警 (已使用: ${disk_usage}%)"
    else
        error "磁盘空间不足 (已使用: ${disk_usage}%)"
    fi
}

# 检查内存使用
check_memory() {
    local memory_usage=$(free | grep Mem | awk '{printf "%.1f", $3/$2 * 100.0}')
    local memory_used=$(free -h | grep Mem | awk '{print $3}')
    local memory_total=$(free -h | grep Mem | awk '{print $2}')
    
    if (( $(echo "$memory_usage < 80" | bc -l) )); then
        log "✓ 内存使用正常 (已使用: ${memory_used}/${memory_total})"
    elif (( $(echo "$memory_usage < 90" | bc -l) )); then
        warn "内存使用告警 (已使用: ${memory_used}/${memory_total})"
    else
        error "内存使用过高 (已使用: ${memory_used}/${memory_total})"
    fi
}

# 检查Docker资源使用
check_docker_resources() {
    info "Docker容器资源使用情况:"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}"
}

# 检查服务日志错误
check_logs() {
    local error_count=$(docker-compose logs --since="1h" 2>&1 | grep -i error | wc -l)
    
    if [ "$error_count" -eq 0 ]; then
        log "✓ 近1小时内无错误日志"
    elif [ "$error_count" -lt 10 ]; then
        warn "近1小时内发现 $error_count 条错误日志"
    else
        error "近1小时内发现大量错误日志: $error_count 条"
    fi
}

# 检查Doubao API连接
check_doubao_api() {
    if curl -f -s http://localhost:8080/api/v1/doubao/image/test &> /dev/null; then
        log "✓ Doubao API连接正常"
    else
        warn "✗ Doubao API连接失败，请检查API密钥配置"
    fi
}

# 主健康检查函数
main_health_check() {
    echo "========================================"
    echo "    图像识别系统健康检查报告"
    echo "    检查时间: $(date)"
    echo "========================================"
    echo
    
    local checks=(
        "check_docker"
        "check_containers"
        "check_database"
        "check_redis"
        "check_backend"
        "check_frontend"
        "check_doubao_api"
        "check_disk_space"
        "check_memory"
        "check_logs"
    )
    
    local failed_checks=0
    
    for check in "${checks[@]}"; do
        if ! $check; then
            ((failed_checks++))
        fi
        echo
    done
    
    # 显示资源使用情况
    check_docker_resources
    echo
    
    # 总结
    if [ $failed_checks -eq 0 ]; then
        log "🎉 系统健康检查通过，所有服务运行正常"
        exit 0
    else
        error "❌ 健康检查发现 $failed_checks 个问题，请及时处理"
        exit 1
    fi
}

# 简单检查（用于监控脚本）
simple_check() {
    if check_containers && check_backend; then
        echo "healthy"
        exit 0
    else
        echo "unhealthy"
        exit 1
    fi
}

# 命令行参数处理
case "${1:-full}" in
    "simple")
        simple_check
        ;;
    "full"|*)
        main_health_check
        ;;
esac
