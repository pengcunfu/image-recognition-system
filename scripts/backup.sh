#!/bin/bash

# 数据库备份脚本
# 支持定时备份和手动备份

set -e

# 配置
BACKUP_DIR="./backups"
MYSQL_CONTAINER="image-recognition-mysql"
DATABASE_NAME="image_recognition"
RETENTION_DAYS=${BACKUP_RETENTION_DAYS:-30}

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
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

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 检查容器是否运行
if ! docker ps | grep -q "$MYSQL_CONTAINER"; then
    error "MySQL容器未运行: $MYSQL_CONTAINER"
    exit 1
fi

# 生成备份文件名
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/image_recognition_backup_$TIMESTAMP.sql"

log "开始备份数据库: $DATABASE_NAME"

# 执行备份
if docker exec "$MYSQL_CONTAINER" mysqldump \
    --single-transaction \
    --routines \
    --triggers \
    --all-databases \
    -u root -p"${MYSQL_ROOT_PASSWORD:-imagerecognition2024}" \
    > "$BACKUP_FILE"; then
    
    log "数据库备份成功: $BACKUP_FILE"
    
    # 压缩备份文件
    gzip "$BACKUP_FILE"
    log "备份文件已压缩: ${BACKUP_FILE}.gz"
    
    # 清理旧备份
    log "清理超过 $RETENTION_DAYS 天的旧备份..."
    find "$BACKUP_DIR" -name "*.gz" -mtime +$RETENTION_DAYS -delete
    
    # 显示备份文件大小
    BACKUP_SIZE=$(du -h "${BACKUP_FILE}.gz" | cut -f1)
    log "备份完成，文件大小: $BACKUP_SIZE"
    
else
    error "数据库备份失败"
    # 清理失败的备份文件
    [ -f "$BACKUP_FILE" ] && rm -f "$BACKUP_FILE"
    exit 1
fi

# 显示备份列表
log "当前备份文件列表:"
ls -lh "$BACKUP_DIR"/*.gz 2>/dev/null || warn "无备份文件"
