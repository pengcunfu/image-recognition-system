# 🔥 热重载开发指南

## 📋 功能概述

已为项目配置了 Spring Boot DevTools 热重载功能，支持以下特性：

- **自动重启**: 当 Java 类文件或配置文件发生变化时自动重启应用
- **LiveReload**: 浏览器自动刷新（需要浏览器插件支持）
- **缓存禁用**: 开发环境下禁用模板和静态资源缓存
- **远程调试**: 支持 IDE 远程调试功能

## 🚀 使用方法

### 1. 启动开发模式

#### 方式一：使用开发配置文件
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 方式二：在 IDE 中设置
- **IntelliJ IDEA**: 
  1. Edit Configurations → Active profiles: `dev`
  2. 或在 VM options 中添加: `-Dspring.profiles.active=dev`
  
- **Eclipse/STS**:
  1. Run Configurations → Arguments → Program arguments: `--spring.profiles.active=dev`

### 2. 触发热重载

热重载会在以下情况自动触发：

#### ✅ 会触发重启的文件类型：
- `.java` 文件（控制器、服务、配置类等）
- `.yml` 和 `.properties` 配置文件
- `src/main/resources` 下的文件

#### ❌ 不会触发重启的文件类型：
- `static/**` 静态资源文件
- `public/**` 公共资源文件
- `templates/**` 模板文件（但会清除缓存）

### 3. IDE 配置建议

#### IntelliJ IDEA 配置：
1. **启用自动编译**:
   - `File` → `Settings` → `Build, Execution, Deployment` → `Compiler`
   - 勾选 `Build project automatically`

2. **启用运行时编译**:
   - `Ctrl+Shift+Alt+/` → `Registry`
   - 勾选 `compiler.automake.allow.when.app.running`

3. **LiveReload 浏览器插件**:
   - Chrome: 安装 "LiveReload" 扩展
   - Firefox: 安装 "LiveReload" 插件
   - 启用插件后访问 `http://localhost:35729`

#### Eclipse/STS 配置：
1. **启用自动构建**:
   - `Project` → `Build Automatically`

2. **启用热部署**:
   - 确保项目使用 Maven/Gradle 自动构建

## 📊 开发环境特性

### 配置文件层次：
1. `application.yml` - 基础配置
2. `application-dev.yml` - 开发环境专用配置
3. `spring-devtools.properties` - DevTools 特定配置

### 监控端点：
开发环境下启用了所有 Actuator 端点：
- 健康检查: `http://localhost:8080/api/v1/actuator/health`
- 应用信息: `http://localhost:8080/api/v1/actuator/info`
- 所有端点: `http://localhost:8080/api/v1/actuator`

### 日志配置：
- 开发环境使用彩色日志输出
- DEBUG 级别日志用于调试
- 包含详细的 Spring Security 和 Web 层日志

## 🛠️ 故障排除

### 热重载不工作？

1. **检查依赖**:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
       <optional>true</optional>
   </dependency>
   ```

2. **检查配置**:
   ```yaml
   spring:
     devtools:
       restart:
         enabled: true
   ```

3. **IDE 设置**:
   - 确保启用了自动编译
   - 确保项目正确导入 Maven 依赖

4. **文件修改**:
   - 保存文件后等待 1-2 秒
   - 查看控制台是否有重启日志

### LiveReload 不工作？

1. **检查端口**:
   - 确保端口 35729 未被占用
   - 检查防火墙设置

2. **浏览器插件**:
   - 确保安装并启用 LiveReload 插件
   - 点击插件图标确保连接到服务器

### 性能问题？

1. **调整 JVM 参数**:
   ```
   -Xmx512m -Xms256m -XX:+UseG1GC
   ```

2. **排除不必要的文件**:
   ```yaml
   spring:
     devtools:
       restart:
         exclude: static/**,public/**,node_modules/**
   ```

## 🎯 开发技巧

1. **快速测试 API**:
   - 修改控制器后，直接刷新 Swagger UI 页面
   - 访问: `http://localhost:8080/api/v1/`

2. **配置热更新**:
   - 修改 `application-dev.yml` 会立即生效
   - 无需重启整个应用

3. **数据库连接**:
   - 开发环境建议使用 H2 内存数据库进行快速测试
   - 生产环境配置保持不变

4. **调试模式**:
   - 启用远程调试: 取消注释 `.mvn/jvm.config` 中的调试参数
   - IDE 中连接到端口 5005 进行调试

## 📝 注意事项

- DevTools 仅在开发环境使用，生产环境会自动禁用
- 热重载会保持应用状态，但某些更改可能需要完全重启
- 大量文件更改时可能触发多次重启，这是正常现象
- 静态资源更改不会触发重启，但会清除缓存

---

💡 **提示**: 如有问题，请检查控制台日志输出，DevTools 会显示详细的重启信息。
