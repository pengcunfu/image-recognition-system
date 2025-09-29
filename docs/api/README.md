# 智能图像识别系统 API 文档

## 概述

智能图像识别系统提供了完整的RESTful API，支持用户认证、图像识别、社区交互、数据管理等功能。本文档详细描述了所有可用的API接口。

## 目录结构

- [认证相关接口](./auth.md) - 用户登录、注册、密码重置
- [用户管理接口](./user.md) - 用户信息、个人资料、权限管理
- [图像识别接口](./recognition.md) - 图像上传、识别处理、结果管理
- [知识库接口](./knowledge.md) - 知识内容、分类管理、搜索
- [社区接口](./community.md) - 帖子发布、评论、点赞、关注
- [VIP功能接口](./vip.md) - 高级识别、AI训练、API密钥管理
- [管理员接口](./admin.md) - 系统管理、用户管理、数据分析
- [文件上传接口](./upload.md) - 文件上传、存储管理
- [通知接口](./notification.md) - 消息通知、系统公告

## 基础信息

### 服务器地址
- 开发环境：`http://localhost:8080`
- 测试环境：`https://test-api.image-recognition.com`
- 生产环境：`https://api.image-recognition.com`

### API版本
当前版本：`v1`

所有API路径都以 `/api/v1` 开头。

### 请求格式
- Content-Type: `application/json`
- 字符编码: `UTF-8`

### 响应格式
所有API响应都采用统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 状态码说明
- `200` - 请求成功
- `400` - 请求参数错误
- `401` - 未授权，需要登录
- `403` - 权限不足
- `404` - 资源不存在
- `500` - 服务器内部错误

### 认证方式

#### JWT Token
大多数API需要在请求头中包含JWT Token：

```
Authorization: Bearer <your_jwt_token>
```

#### API Key (VIP用户)
VIP用户可以使用API Key进行认证：

```
X-API-Key: <your_api_key>
```

### 分页参数
支持分页的接口统一使用以下参数：

```json
{
  "page": 1,
  "pageSize": 20,
  "total": 100,
  "data": []
}
```

### 错误处理
当请求失败时，响应体包含错误信息：

```json
{
  "code": 400,
  "message": "参数验证失败",
  "error": "用户名不能为空",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 限流策略
- 普通用户：100次/分钟
- VIP用户：1000次/分钟
- API Key：根据套餐限制

### 文件上传限制
- 单文件大小：最大10MB
- 支持格式：jpg, jpeg, png, gif, bmp, webp
- 批量上传：最多20个文件

## 快速开始

### 1. 用户注册
```bash
curl -X POST https://api.image-recognition.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. 用户登录
```bash
curl -X POST https://api.image-recognition.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. 图像识别
```bash
curl -X POST https://api.image-recognition.com/api/v1/recognition/image \
  -H "Authorization: Bearer <your_token>" \
  -F "file=@/path/to/image.jpg"
```

## SDK支持

我们提供了多种编程语言的SDK：

- [JavaScript/Node.js SDK](https://github.com/image-recognition/js-sdk)
- [Python SDK](https://github.com/image-recognition/python-sdk)
- [Java SDK](https://github.com/image-recognition/java-sdk)
- [PHP SDK](https://github.com/image-recognition/php-sdk)

## 更新日志

### v1.2.0 (2024-03-01)
- 新增批量识别接口
- 优化识别性能
- 增加更多识别类型

### v1.1.0 (2024-02-01)
- 新增VIP功能接口
- 增加API Key认证
- 优化社区功能

### v1.0.0 (2024-01-01)
- 初始版本发布
- 基础功能完整

## 联系我们

如有任何问题，请联系：
- 技术支持：support@image-recognition.com
- API问题：api@image-recognition.com
- 文档反馈：docs@image-recognition.com
