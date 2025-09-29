#!/bin/bash

echo "🚀 启动图像识别系统 - 开发模式"
echo "================================"

echo "📦 检查 Maven 依赖..."
mvn dependency:resolve -q

echo "🔥 启动热重载开发服务器..."
echo "💡 提示: 修改代码后会自动重启"
echo "🌐 Swagger UI: http://localhost:8080/api/v1/"
echo "🔍 LiveReload: http://localhost:35729"
echo "⏹️  按 Ctrl+C 停止服务器"
echo

mvn spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true"
