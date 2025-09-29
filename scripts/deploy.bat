@echo off
:: 图像识别系统 Docker 部署脚本 (Windows)
:: 作者: AI Assistant
:: 版本: 1.0.0

setlocal enabledelayedexpansion
chcp 65001 >nul

echo ========================================
echo     图像识别系统 Docker 部署脚本
echo ========================================
echo.

:: 检查Docker是否安装
call :check_docker
if errorlevel 1 goto :error

:: 检查环境变量文件
call :check_env_file
if errorlevel 1 goto :error

:: 创建必要目录
call :create_directories

:: 根据参数执行不同操作
if "%1"=="build" goto :build_only
if "%1"=="start" goto :start_only
if "%1"=="stop" goto :stop_services
if "%1"=="restart" goto :restart_services
if "%1"=="logs" goto :show_logs
if "%1"=="status" goto :check_status
if "%1"=="clean" goto :clean_system

:: 默认完整部署
goto :full_deploy

:check_docker
echo [INFO] 检查Docker环境...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker未安装或未启动，请先安装Docker Desktop
    exit /b 1
)
docker-compose --version >nul 2>&1
if not errorlevel 1 goto :docker_ok
docker compose version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose未安装
    exit /b 1
)
:docker_ok
echo [INFO] Docker环境检查通过
goto :eof

:check_env_file
echo [INFO] 检查环境变量配置...
if not exist ".env" (
    echo [WARN] .env文件不存在，从示例文件创建...
    if exist "env.example" (
        copy "env.example" ".env" >nul
        echo [WARN] 已创建.env文件，请编辑设置您的配置
        echo [WARN] 特别注意设置DOUBAO_API_KEY
        set /p edit_env="是否现在编辑.env文件? (y/n): "
        if /i "!edit_env!"=="y" notepad .env
    ) else (
        echo [ERROR] env.example文件不存在
        exit /b 1
    )
)
echo [INFO] 环境变量配置检查完成
goto :eof

:create_directories
echo [INFO] 创建必要目录...
if not exist "docker\mysql" mkdir "docker\mysql"
if not exist "docker\prometheus" mkdir "docker\prometheus"
if not exist "logs" mkdir "logs"
if not exist "uploads" mkdir "uploads"
if not exist "backups" mkdir "backups"
echo [INFO] 目录创建完成
goto :eof

:build_images
echo [INFO] 构建Docker镜像...
echo [INFO] 构建后端镜像...
docker build -f Dockerfile.backend -t image-recognition-backend:latest .
if errorlevel 1 (
    echo [ERROR] 后端镜像构建失败
    exit /b 1
)

echo [INFO] 构建前端镜像...
docker build -f Dockerfile.frontend -t image-recognition-frontend:latest .
if errorlevel 1 (
    echo [ERROR] 前端镜像构建失败
    exit /b 1
)
echo [INFO] 镜像构建完成
goto :eof

:start_services
echo [INFO] 启动Docker服务...

:: 检查是否有运行的容器
docker-compose ps | findstr "Up" >nul
if not errorlevel 1 (
    echo [WARN] 检测到运行中的容器，先停止现有服务...
    docker-compose down
)

:: 启动基础服务
echo [INFO] 启动数据库和缓存服务...
docker-compose up -d mysql redis

:: 等待数据库启动
echo [INFO] 等待数据库启动...
timeout /t 30 /nobreak >nul

:: 检查数据库健康状态
:wait_mysql
docker-compose exec mysql mysqladmin ping -h localhost --silent >nul 2>&1
if errorlevel 1 (
    echo [INFO] 等待数据库启动完成...
    timeout /t 5 /nobreak >nul
    goto :wait_mysql
)

:: 启动应用服务
echo [INFO] 启动应用服务...
docker-compose up -d backend

:: 等待后端启动
echo [INFO] 等待后端服务启动...
timeout /t 20 /nobreak >nul

:: 启动前端服务
echo [INFO] 启动前端服务...
docker-compose up -d frontend

echo [INFO] 所有服务启动完成
goto :eof

:check_health
echo [INFO] 检查服务健康状态...

:: 检查数据库
docker-compose exec mysql mysqladmin ping -h localhost --silent >nul 2>&1
if not errorlevel 1 (
    echo [INFO] ✓ MySQL数据库运行正常
) else (
    echo [ERROR] ✗ MySQL数据库连接失败
)

:: 检查Redis
docker-compose exec redis redis-cli ping | findstr "PONG" >nul 2>&1
if not errorlevel 1 (
    echo [INFO] ✓ Redis缓存运行正常
) else (
    echo [WARN] ✗ Redis缓存连接失败
)

:: 检查后端
timeout /t 10 /nobreak >nul
curl -f http://localhost:8080/api/v1/doubao/image/status >nul 2>&1
if not errorlevel 1 (
    echo [INFO] ✓ 后端服务运行正常
) else (
    echo [WARN] ✗ 后端服务连接失败，请检查日志
)

:: 检查前端
curl -f http://localhost:80/health >nul 2>&1
if not errorlevel 1 (
    echo [INFO] ✓ 前端服务运行正常
) else (
    echo [WARN] ✗ 前端服务连接失败，请检查日志
)

echo [INFO] 健康检查完成
goto :eof

:show_info
echo.
echo ========================================
echo 🌐 前端地址: http://localhost:80
echo 🔧 后端API: http://localhost:8080/api/v1
echo 🗄️  数据库: localhost:3306
echo 📊 Redis: localhost:6379
echo.
echo 🔍 API测试:
echo   健康检查: curl http://localhost:8080/api/v1/doubao/image/status
echo   连接测试: curl http://localhost:8080/api/v1/doubao/image/test
echo.
echo 📋 常用命令:
echo   查看日志: docker-compose logs -f [service_name]
echo   重启服务: docker-compose restart [service_name]
echo   停止服务: docker-compose down
echo   查看状态: docker-compose ps
echo ========================================
echo.
goto :eof

:start_monitoring
set /p start_monitor="是否启动监控服务 (Prometheus + Grafana)? (y/n): "
if /i "!start_monitor!"=="y" (
    echo [INFO] 启动监控服务...
    docker-compose --profile monitoring up -d prometheus grafana
    echo.
    echo 📊 监控服务:
    echo   Prometheus: http://localhost:9090
    echo   Grafana: http://localhost:3000 (admin/admin123)
)
goto :eof

:full_deploy
call :build_images
if errorlevel 1 goto :error
call :start_services
if errorlevel 1 goto :error
call :check_health
call :start_monitoring
call :show_info
echo [SUCCESS] 🎉 部署完成！系统已成功启动
goto :end

:build_only
call :build_images
goto :end

:start_only
call :start_services
call :check_health
call :show_info
goto :end

:stop_services
echo [INFO] 停止服务...
docker-compose down
echo [INFO] 服务已停止
goto :end

:restart_services
echo [INFO] 重启服务...
docker-compose restart
call :check_health
goto :end

:show_logs
if "%2"=="" (
    docker-compose logs -f
) else (
    docker-compose logs -f %2
)
goto :end

:check_status
echo [INFO] 检查服务状态...
docker-compose ps
call :check_health
goto :end

:clean_system
echo [INFO] 清理系统...
docker-compose down -v
docker system prune -f
echo [INFO] 系统清理完成
goto :end

:error
echo [ERROR] 部署过程中发生错误
pause
exit /b 1

:end
if not "%1"=="logs" pause
exit /b 0
