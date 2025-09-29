@echo off
:: 图像识别系统 Docker 部署脚本 (远程数据库版本)
:: 远程数据库服务器: 8.155.40.179

setlocal enabledelayedexpansion
chcp 65001 >nul

echo ========================================
echo   图像识别系统 Docker 部署脚本
echo      (远程数据库版本)
echo   数据库服务器: 8.155.40.179
echo ========================================
echo.

:: 检查Docker是否安装
call :check_docker
if errorlevel 1 goto :error

:: 检查环境变量文件
call :check_env_file
if errorlevel 1 goto :error

:: 检查远程服务连接
call :check_remote_services

:: 根据参数执行不同操作
if "%1"=="build" goto :build_only
if "%1"=="start" goto :start_only
if "%1"=="stop" goto :stop_services
if "%1"=="restart" goto :restart_services
if "%1"=="logs" goto :show_logs
if "%1"=="status" goto :check_status
if "%1"=="check" goto :check_remote_only

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
    echo [WARN] .env文件不存在，从远程数据库示例文件创建...
    if exist "env.remote.example" (
        copy "env.remote.example" ".env" >nul
        echo [WARN] 已创建.env文件，请编辑设置您的配置
        echo [WARN] 重要: 请设置正确的数据库密码和Redis密码
        set /p edit_env="是否现在编辑.env文件? (y/n): "
        if /i "!edit_env!"=="y" notepad .env
    ) else (
        echo [ERROR] env.remote.example文件不存在
        exit /b 1
    )
)

:: 检查关键配置
findstr "your_mysql_password" .env >nul 2>&1
if not errorlevel 1 (
    echo [WARN] 检测到默认的数据库密码，请设置正确的密码
)

findstr "your_redis_password" .env >nul 2>&1
if not errorlevel 1 (
    echo [WARN] 检测到默认的Redis密码，请设置正确的密码
)

echo [INFO] 环境变量配置检查完成
goto :eof

:check_remote_services
echo [INFO] 检查远程服务连接...

:: 检查MySQL连接
echo [INFO] 检查MySQL连接 (8.155.40.179:3306)...
telnet 8.155.40.179 3306 2>nul | findstr "Connected" >nul
if not errorlevel 1 (
    echo [INFO] ✓ MySQL服务器连接正常
) else (
    echo [WARN] ✗ 无法连接到MySQL服务器，请检查网络和防火墙设置
)

:: 检查Redis连接
echo [INFO] 检查Redis连接 (8.155.40.179:6379)...
telnet 8.155.40.179 6379 2>nul | findstr "Connected" >nul
if not errorlevel 1 (
    echo [INFO] ✓ Redis服务器连接正常
) else (
    echo [WARN] ✗ 无法连接到Redis服务器，请检查网络和防火墙设置
)

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

:: 启动后端服务
echo [INFO] 启动后端服务...
docker-compose up -d backend

:: 等待后端启动
echo [INFO] 等待后端服务启动...
timeout /t 30 /nobreak >nul

:: 启动前端服务
echo [INFO] 启动前端服务...
docker-compose up -d frontend

echo [INFO] 所有服务启动完成
goto :eof

:check_health
echo [INFO] 检查服务健康状态...

:: 检查后端
set /a attempt=1
set /a max_attempts=5

:check_backend_loop
curl -f -s http://localhost:8080/api/v1/doubao/image/status >nul 2>&1
if not errorlevel 1 (
    echo [INFO] ✓ 后端服务运行正常
    goto :check_frontend
)

echo [WARN] 后端服务检查失败，尝试 %attempt%/%max_attempts%
if %attempt% geq %max_attempts% (
    echo [ERROR] ✗ 后端服务启动失败，请检查日志
    goto :check_frontend
)

timeout /t 10 /nobreak >nul
set /a attempt+=1
goto :check_backend_loop

:check_frontend
:: 检查前端
curl -f -s http://localhost:80/health >nul 2>&1
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
echo 🗄️  远程数据库: 8.155.40.179:3306
echo 📊 远程Redis: 8.155.40.179:6379
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

:full_deploy
call :build_images
if errorlevel 1 goto :error
call :start_services
if errorlevel 1 goto :error
call :check_health
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

:check_remote_only
call :check_remote_services
goto :end

:error
echo [ERROR] 部署过程中发生错误
pause
exit /b 1

:end
if not "%1"=="logs" pause
exit /b 0
