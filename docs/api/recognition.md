# 图像识别接口

## 单张图像识别

### POST /api/v1/recognition/image

上传并识别单张图像。

**请求头：**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数：**
- `file` (文件): 图像文件，必填
- `mode` (字符串): 识别模式，可选值：`general`(通用)、`animal`(动物)、`plant`(植物)、`food`(食物)、`scene`(场景)，默认为`general`
- `confidence` (数字): 最小置信度阈值，0-1之间，默认0.5
- `maxResults` (数字): 最大返回结果数，1-10之间，默认5
- `tags` (字符串): 自定义标签，可选

**响应示例：**

```json
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "recognitionId": "rec_123456789",
    "imageUrl": "https://storage.example.com/images/abc123.jpg",
    "results": [
      {
        "label": "金毛犬",
        "confidence": 0.95,
        "category": "动物",
        "description": "一种友善的大型犬类，毛色为金黄色",
        "boundingBox": {
          "x": 100,
          "y": 50,
          "width": 200,
          "height": 300
        }
      },
      {
        "label": "狗",
        "confidence": 0.92,
        "category": "动物",
        "description": "哺乳动物，人类的忠实伴侣"
      }
    ],
    "metadata": {
      "imageSize": {
        "width": 1024,
        "height": 768
      },
      "fileSize": 245760,
      "format": "JPEG",
      "processingTime": 1.2
    },
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 批量图像识别

### POST /api/v1/recognition/batch

批量上传并识别多张图像。

**请求头：**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数：**
- `files` (文件数组): 图像文件数组，最多20个文件
- `mode` (字符串): 识别模式，同单张识别
- `confidence` (数字): 最小置信度阈值
- `maxResults` (数字): 每张图片最大返回结果数
- `batchName` (字符串): 批次名称，可选

**响应示例：**

```json
{
  "code": 200,
  "message": "批量识别已开始",
  "data": {
    "batchId": "batch_123456789",
    "status": "processing",
    "totalFiles": 5,
    "processedFiles": 0,
    "results": [],
    "createdAt": "2024-01-01T00:00:00Z",
    "estimatedTime": 30
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取批量识别状态

### GET /api/v1/recognition/batch/{batchId}

获取批量识别的处理状态和结果。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `batchId`: 批次ID

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "batchId": "batch_123456789",
    "status": "completed",
    "totalFiles": 5,
    "processedFiles": 5,
    "successFiles": 4,
    "failedFiles": 1,
    "results": [
      {
        "fileName": "image1.jpg",
        "recognitionId": "rec_111",
        "status": "success",
        "results": [/* 识别结果 */]
      },
      {
        "fileName": "image2.jpg",
        "recognitionId": "rec_112",
        "status": "failed",
        "error": "图像格式不支持"
      }
    ],
    "createdAt": "2024-01-01T00:00:00Z",
    "completedAt": "2024-01-01T00:01:30Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取识别历史

### GET /api/v1/recognition/history

获取用户的识别历史记录。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20，最大100
- `mode` (字符串): 识别模式筛选
- `startDate` (字符串): 开始日期，格式：YYYY-MM-DD
- `endDate` (字符串): 结束日期，格式：YYYY-MM-DD
- `keyword` (字符串): 搜索关键词
- `minConfidence` (数字): 最小置信度
- `category` (字符串): 分类筛选

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 156,
    "page": 1,
    "pageSize": 20,
    "records": [
      {
        "recognitionId": "rec_123456789",
        "imageUrl": "https://storage.example.com/images/abc123.jpg",
        "thumbnail": "https://storage.example.com/thumbnails/abc123_thumb.jpg",
        "topResult": {
          "label": "金毛犬",
          "confidence": 0.95,
          "category": "动物"
        },
        "mode": "animal",
        "createdAt": "2024-01-01T00:00:00Z",
        "isFavorite": false,
        "tags": ["宠物", "可爱"]
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取识别详情

### GET /api/v1/recognition/{recognitionId}

获取单次识别的详细结果。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `recognitionId`: 识别ID

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "recognitionId": "rec_123456789",
    "imageUrl": "https://storage.example.com/images/abc123.jpg",
    "originalFileName": "my_dog.jpg",
    "results": [/* 完整识别结果 */],
    "metadata": {/* 元数据信息 */},
    "mode": "animal",
    "confidence": 0.5,
    "maxResults": 5,
    "createdAt": "2024-01-01T00:00:00Z",
    "isFavorite": true,
    "tags": ["宠物", "可爱"],
    "shareUrl": "https://app.example.com/share/rec_123456789"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 添加/移除收藏

### POST /api/v1/recognition/{recognitionId}/favorite

添加到收藏夹。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "添加收藏成功",
  "data": {
    "isFavorite": true
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### DELETE /api/v1/recognition/{recognitionId}/favorite

从收藏夹移除。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "移除收藏成功",
  "data": {
    "isFavorite": false
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 添加标签

### POST /api/v1/recognition/{recognitionId}/tags

为识别结果添加自定义标签。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "tags": ["标签1", "标签2", "标签3"]
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "标签添加成功",
  "data": {
    "tags": ["标签1", "标签2", "标签3", "已存在的标签"]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 删除识别记录

### DELETE /api/v1/recognition/{recognitionId}

删除识别记录（仅删除记录，不删除图片）。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取识别统计

### GET /api/v1/recognition/statistics

获取用户的识别统计信息。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `period` (字符串): 统计周期，可选值：`today`、`week`、`month`、`year`、`all`，默认`month`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "month",
    "totalRecognitions": 156,
    "successRecognitions": 148,
    "avgConfidence": 0.87,
    "categoryDistribution": {
      "动物": 45,
      "植物": 32,
      "食物": 28,
      "场景": 25,
      "其他": 18
    },
    "dailyStats": [
      {
        "date": "2024-01-01",
        "count": 12,
        "avgConfidence": 0.89
      }
    ],
    "topResults": [
      {
        "label": "金毛犬",
        "count": 8,
        "avgConfidence": 0.94
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 分享识别结果

### POST /api/v1/recognition/{recognitionId}/share

生成识别结果的分享链接。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "platform": "link",        // 分享平台：link, weibo, wechat, qq
  "includeDetails": true,     // 是否包含详细信息
  "expiresIn": 7             // 有效期（天），默认7天
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "分享链接生成成功",
  "data": {
    "shareUrl": "https://app.example.com/share/rec_123456789?token=abc123",
    "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "expiresAt": "2024-01-08T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 识别模式说明

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| general | 通用识别 | 不确定图像类型时使用 |
| animal | 动物识别 | 识别各种动物，包括宠物、野生动物等 |
| plant | 植物识别 | 识别花卉、树木、草本植物等 |
| food | 食物识别 | 识别菜品、水果、零食等食物 |
| scene | 场景识别 | 识别地标、建筑、自然景观等 |
| product | 商品识别 | 识别商品、品牌、logo等 |
| text | 文字识别 | 提取图像中的文字内容 |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 50001 | 文件格式不支持 |
| 50002 | 文件大小超出限制 |
| 50003 | 图像质量过低 |
| 50004 | 识别服务不可用 |
| 50005 | 批量任务数量超出限制 |
| 50006 | 识别结果不存在 |
| 50007 | 识别模式无效 |
| 50008 | 用户识别次数已用完 |
| 50009 | 图像中未检测到有效内容 |
