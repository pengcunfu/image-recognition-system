@echo off
REM 智能图像识别系统 - Docker 构建脚本 (Windows)
REM 用法: build.bat [选项]
REM 选项:
REM   all       - 构建所有服务（默认）
REM   backend   - 仅构建后端
REM   frontend  - 仅构建前端
REM   clean     - 清理并重新构建

setlocal enabledelayedexpansion

REM 设置变量
set "ACTION=%~1"
if "%ACTION%"=="" set "ACTION=all"

echo ========================================
echo 智能图像识别系统 - Docker 构建脚本
echo ========================================
echo.

REM 检查 Docker 是否安装
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Docker 未安装，请先安装 Docker
    exit /b 1
)

where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose 未安装，请先安装 Docker Compose
    exit /b 1
)

echo [INFO] Docker 和 Docker Compose 已安装
echo.

REM 检查环境变量文件
if not exist "..\\.env" (
    echo [WARN] .env 文件不存在
    if exist "..\\.env.example" (
        echo [INFO] 从 .env.example 复制创建 .env 文件
        copy "..\\.env.example" "..\\.env"
        echo [WARN] 请编辑 .env 文件，填入正确的配置信息
    ) else (
        echo [ERROR] .env.example 文件不存在，无法创建 .env
        exit /b 1
    )
) else (
    echo [INFO] .env 文件存在
)
echo.

REM 切换到项目根目录
cd ..

REM 根据参数执行相应操作
if "%ACTION%"=="all" (
    echo [INFO] 开始构建所有服务...
    docker-compose build
    if %errorlevel% neq 0 (
        echo [ERROR] 构建失败
        exit /b 1
    )
    echo [INFO] 所有服务构建完成
) else if "%ACTION%"=="backend" (
    echo [INFO] 开始构建后端服务...
    docker-compose build backend
    if %errorlevel% neq 0 (
        echo [ERROR] 后端构建失败
        exit /b 1
    )
    echo [INFO] 后端服务构建完成
) else if "%ACTION%"=="frontend" (
    echo [INFO] 开始构建前端服务...
    docker-compose build frontend
    if %errorlevel% neq 0 (
        echo [ERROR] 前端构建失败
        exit /b 1
    )
    echo [INFO] 前端服务构建完成
) else if "%ACTION%"=="clean" (
    echo [INFO] 停止并删除所有容器...
    docker-compose down
    
    echo [INFO] 清理 Docker 缓存...
    docker system prune -f
    
    echo [INFO] 重新构建所有服务...
    docker-compose build --no-cache
    if %errorlevel% neq 0 (
        echo [ERROR] 构建失败
        exit /b 1
    )
    echo [INFO] 清理并重新构建完成
) else (
    echo [ERROR] 未知选项: %ACTION%
    echo 用法: %~nx0 [all^|backend^|frontend^|clean]
    exit /b 1
)

echo.
echo ========================================
echo [INFO] 构建脚本执行完成
echo [INFO] 使用 'docker-compose up -d' 启动服务
echo ========================================

endlocal

