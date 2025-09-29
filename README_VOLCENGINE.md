# 抖音火山引擎图像识别系统集成指南

## 项目概述

本项目成功集成了抖音火山引擎的图像识别服务，为智能图像识别系统提供强大的AI识别能力。系统基于Spring Boot框架开发，支持单张和批量图像识别功能。

## 技术栈

- **后端框架**: Spring Boot 3.5.6
- **Java版本**: JDK 17
- **AI服务**: 抖音火山引擎 Visual Recognition API
- **数据库**: MySQL 8.0
- **持久层**: MyBatis
- **安全框架**: Spring Security
- **构建工具**: Maven
- **开发工具**: Lombok

## 主要功能

### 🎯 核心功能
- ✅ 单张图像识别
- ✅ 批量图像识别（最多20张）
- ✅ 多种识别模式（通用、动物、植物、食物、场景）
- ✅ 文件上传和存储
- ✅ 识别结果管理
- ✅ RESTful API接口

### 🔧 技术特性
- ✅ 火山引擎SDK集成
- ✅ 统一异常处理
- ✅ 参数验证
- ✅ CORS跨域支持
- ✅ 文件类型和大小验证
- ✅ 响应格式标准化

## 快速开始

### 1. 环境准备

确保您的开发环境已安装：
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 获取火山引擎凭证

1. 访问 [火山引擎控制台](https://console.volcengine.com/)
2. 创建项目并开通图像识别服务
3. 获取 `Access Key ID` 和 `Secret Access Key`

### 3. 配置环境变量

在系统环境变量中设置火山引擎凭证：

```bash
# Windows
set VOLCENGINE_ACCESS_KEY_ID=your_access_key_id
set VOLCENGINE_SECRET_ACCESS_KEY=your_secret_access_key

# Linux/Mac
export VOLCENGINE_ACCESS_KEY_ID=your_access_key_id
export VOLCENGINE_SECRET_ACCESS_KEY=your_secret_access_key
```

### 4. 数据库配置

修改 `application.properties` 中的数据库配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/image_recognition?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 5. 启动应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动。

## API 接口文档

### 基础信息
- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json` 或 `multipart/form-data`

### 主要接口

#### 1. 系统状态检查
```http
GET /test/status
```

**响应示例**:
```json
{
  "code": 200,
  "message": "系统运行正常",
  "data": {
    "application": "image-recognition-system",
    "status": "running",
    "version": "1.0.0",
    "timestamp": "2024-01-01T00:00:00"
  },
  "timestamp": "2024-01-01T00:00:00"
}
```

#### 2. 单张图像识别
```http
POST /recognition/image
Content-Type: multipart/form-data
```

**请求参数**:
- `file` (文件): 图像文件，必填
- `mode` (字符串): 识别模式，可选值：`general`、`animal`、`plant`、`food`、`scene`，默认 `general`
- `confidence` (数字): 置信度阈值，0-1之间，默认 0.5
- `maxResults` (数字): 最大结果数，1-10之间，默认 5
- `tags` (数组): 自定义标签，可选

**响应示例**:
```json
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "recognitionId": "rec_1640995200000_abcd1234",
    "userId": 1,
    "imageUrl": "http://localhost:8080/api/v1/files/20240101_uuid.jpg",
    "originalFileName": "dog.jpg",
    "mode": "animal",
    "confidence": 0.5,
    "maxResults": 5,
    "results": [
      {
        "label": "金毛犬",
        "confidence": 0.95,
        "category": "动物",
        "description": "识别结果: 金毛犬"
      }
    ],
    "metadata": {
      "imageSize": {
        "width": 1024,
        "height": 768
      },
      "fileSize": 245760,
      "format": "JPEG",
      "processingTime": 1.0
    },
    "status": "completed",
    "isFavorite": false,
    "tags": [],
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "timestamp": "2024-01-01T00:00:00"
}
```

#### 3. 批量图像识别
```http
POST /recognition/batch
Content-Type: multipart/form-data
```

**请求参数**:
- `files` (文件数组): 图像文件列表，最多20个
- `mode` (字符串): 识别模式
- `confidence` (数字): 置信度阈值
- `maxResults` (数字): 每张图片最大结果数
- `batchName` (字符串): 批次名称，可选

#### 4. 获取支持的识别模式
```http
GET /recognition/modes
```

#### 5. 健康检查
```http
GET /recognition/health
```

## 使用示例

### cURL 示例

```bash
# 单张图像识别
curl -X POST \
  http://localhost:8080/api/v1/recognition/image \
  -F "file=@/path/to/your/image.jpg" \
  -F "mode=animal" \
  -F "confidence=0.7" \
  -F "maxResults=3"

# 系统状态检查
curl http://localhost:8080/api/v1/test/status
```

### JavaScript 示例

```javascript
// 单张图像识别
async function recognizeImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('mode', 'general');
  formData.append('confidence', '0.5');
  formData.append('maxResults', '5');

  try {
    const response = await fetch('http://localhost:8080/api/v1/recognition/image', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    console.log('识别结果:', result);
    return result;
  } catch (error) {
    console.error('识别失败:', error);
  }
}
```

## 项目结构

```
src/main/java/com/example/demo/
├── config/                 # 配置类
│   ├── VolcengineConfig.java       # 火山引擎配置
│   ├── ImageRecognitionConfig.java # 图像识别配置
│   ├── WebConfig.java             # Web配置
│   └── ObjectMapperConfig.java    # JSON序列化配置
├── controller/             # 控制器
│   ├── ImageRecognitionController.java  # 图像识别API
│   ├── FileController.java             # 文件访问API
│   └── TestController.java             # 测试API
├── dto/                    # 数据传输对象
│   ├── RecognitionRequest.java         # 识别请求DTO
│   ├── BatchRecognitionRequest.java    # 批量识别请求DTO
│   └── ApiResponse.java               # 统一响应格式
├── entity/                 # 实体类
│   ├── RecognitionResult.java         # 识别结果实体
│   ├── RecognitionItem.java           # 识别项实体
│   └── ImageMetadata.java            # 图像元数据实体
├── service/                # 服务类
│   ├── VolcengineImageService.java    # 火山引擎服务
│   └── FileStorageService.java       # 文件存储服务
├── exception/              # 异常处理
│   └── GlobalExceptionHandler.java   # 全局异常处理器
└── DemoApplication.java    # 启动类
```

## 支持的文件格式

- **图像格式**: JPG, JPEG, PNG, BMP, GIF, WebP
- **最大文件大小**: 10MB
- **批量处理**: 最多20个文件

## 识别模式说明

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| general | 通用识别 | 不确定图像类型时使用 |
| animal | 动物识别 | 识别各种动物，包括宠物、野生动物等 |
| plant | 植物识别 | 识别花卉、树木、草本植物等 |
| food | 食物识别 | 识别菜品、水果、零食等食物 |
| scene | 场景识别 | 识别地标、建筑、自然景观等 |

## 错误处理

系统包含完善的错误处理机制：

- **400**: 参数验证失败
- **404**: 资源不存在
- **413**: 文件大小超出限制
- **500**: 服务内部错误

## 注意事项

1. **火山引擎凭证安全**: 请妥善保管您的 Access Key，不要在代码中硬编码
2. **文件存储**: 当前使用本地文件存储，生产环境建议使用云存储服务
3. **并发处理**: 系统支持并发请求，但请注意火山引擎API的调用限制
4. **日志记录**: 系统记录了详细的操作日志，便于调试和监控

## 后续扩展

- [ ] 用户认证和授权
- [ ] 识别历史记录存储
- [ ] 识别结果缓存
- [ ] 异步批量处理
- [ ] 更多AI服务集成
- [ ] 性能监控和统计

## 技术支持

如有问题，请参考：
1. [火山引擎官方文档](https://www.volcengine.com/docs)
2. [Spring Boot官方文档](https://spring.io/projects/spring-boot)
3. 项目Issue跟踪

---

**开发完成时间**: 2024年1月1日  
**版本**: v1.0.0  
**作者**: 智能图像识别系统开发团队
