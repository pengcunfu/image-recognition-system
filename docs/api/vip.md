# VIP功能接口

## 高级图像识别

### POST /api/v1/vip/recognition/advanced

VIP专享的高级图像识别功能，提供更高精度和更多识别选项。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**请求参数：**
- `file` (文件): 图像文件，必填
- `mode` (字符串): 高级识别模式，可选值：`professional`、`detailed`、`custom`，默认`professional`
- `modelVersion` (字符串): 模型版本，可选值：`latest`、`stable`、`experimental`，默认`latest`
- `features` (数组): 启用的特征分析，可选值：`color`、`texture`、`shape`、`scene`、`emotion`
- `outputFormat` (字符串): 输出格式，可选值：`standard`、`detailed`、`scientific`，默认`detailed`
- `customModel` (字符串): 自定义模型ID（如果有）

**响应示例：**

```json
{
  "code": 200,
  "message": "高级识别成功",
  "data": {
    "recognitionId": "vip_rec_123456789",
    "imageUrl": "https://storage.example.com/images/abc123.jpg",
    "results": [
      {
        "label": "金毛寻回犬",
        "confidence": 0.987,
        "category": "动物",
        "subcategory": "犬科",
        "breed": "Golden Retriever",
        "description": "一种友善、可靠的大型工作犬，原产于苏格兰",
        "attributes": {
          "age": "成年",
          "size": "大型",
          "coat": "长毛",
          "color": "金黄色",
          "emotion": "友好",
          "health": "健康"
        },
        "boundingBox": {
          "x": 100,
          "y": 50,
          "width": 200,
          "height": 300,
          "accuracy": 0.95
        },
        "landmarks": [
          {"name": "nose", "x": 150, "y": 120},
          {"name": "left_eye", "x": 135, "y": 100},
          {"name": "right_eye", "x": 165, "y": 100}
        ]
      }
    ],
    "analysis": {
      "colorAnalysis": {
        "dominantColors": ["#D4AF37", "#8B7355", "#FFFFFF"],
        "colorHarmony": "warm",
        "brightness": 0.75
      },
      "textureAnalysis": {
        "roughness": 0.6,
        "pattern": "fluffy",
        "uniformity": 0.8
      },
      "sceneAnalysis": {
        "environment": "outdoor",
        "lighting": "natural",
        "background": "garden",
        "weather": "sunny"
      }
    },
    "metadata": {
      "processingTime": 3.5,
      "modelVersion": "v2.1.0",
      "gpuTime": 2.1,
      "memoryUsage": "1.2GB"
    },
    "exportOptions": {
      "pdf": "https://api.example.com/export/pdf/vip_rec_123456789",
      "excel": "https://api.example.com/export/excel/vip_rec_123456789",
      "json": "https://api.example.com/export/json/vip_rec_123456789"
    },
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## VIP数据分析

### GET /api/v1/vip/analytics/dashboard

获取VIP用户专享的详细数据分析仪表板。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**查询参数：**
- `period` (字符串): 分析周期，可选值：`today`、`week`、`month`、`quarter`、`year`、`custom`
- `startDate` (字符串): 自定义开始日期（period=custom时必填）
- `endDate` (字符串): 自定义结束日期（period=custom时必填）
- `metrics` (数组): 指定分析指标，可选值：`accuracy`、`performance`、`usage`、`trends`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "month",
    "overview": {
      "totalRecognitions": 1567,
      "averageAccuracy": 0.952,
      "processingTime": 2.3,
      "costSaved": 234.56,
      "apiCalls": 3421
    },
    "accuracyAnalysis": {
      "trend": "increasing",
      "improvement": 0.023,
      "byCategory": {
        "animals": 0.967,
        "plants": 0.943,
        "objects": 0.951
      },
      "confidenceDistribution": [
        {"range": "0.9-1.0", "count": 1245},
        {"range": "0.8-0.9", "count": 234},
        {"range": "0.7-0.8", "count": 67}
      ]
    },
    "performanceMetrics": {
      "avgProcessingTime": 2.3,
      "peakProcessingTime": 5.8,
      "throughput": 145.6,
      "uptime": 0.999,
      "errorRate": 0.001
    },
    "usagePatterns": {
      "peakHours": ["09:00", "14:00", "20:00"],
      "frequentCategories": ["animals", "plants", "food"],
      "deviceDistribution": {
        "mobile": 0.65,
        "desktop": 0.25,
        "api": 0.10
      }
    },
    "trends": {
      "daily": [
        {
          "date": "2024-01-01",
          "recognitions": 67,
          "accuracy": 0.95,
          "apiCalls": 123
        }
      ],
      "predictions": {
        "nextWeek": {
          "recognitions": 450,
          "accuracy": 0.955
        }
      }
    },
    "comparisons": {
      "vsLastPeriod": {
        "recognitions": "+15%",
        "accuracy": "+2.3%",
        "apiCalls": "+23%"
      },
      "vsAverage": {
        "recognitions": "+8%",
        "accuracy": "+1.2%"
      }
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## AI模型训练

### POST /api/v1/vip/ai/training/dataset

创建训练数据集。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**请求参数：**

```json
{
  "name": "我的宠物识别模型",
  "description": "专门识别我家宠物的自定义模型",
  "category": "animals",
  "baseModel": "animal_v2.0",
  "images": [
    {
      "url": "https://example.com/image1.jpg",
      "label": "我的猫咪",
      "annotations": [
        {
          "type": "bbox",
          "coordinates": [100, 50, 200, 150],
          "label": "cat_face"
        }
      ]
    }
  ],
  "trainingParams": {
    "epochs": 50,
    "learningRate": 0.001,
    "batchSize": 32,
    "validation_split": 0.2
  }
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "数据集创建成功",
  "data": {
    "datasetId": "dataset_123456",
    "name": "我的宠物识别模型",
    "status": "created",
    "totalImages": 150,
    "categories": ["我的猫咪", "我的狗狗"],
    "estimatedTrainingTime": "2-4小时",
    "cost": 15.99,
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/vip/ai/training/start/{datasetId}

开始模型训练。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "训练已开始",
  "data": {
    "trainingId": "training_789012",
    "datasetId": "dataset_123456",
    "status": "training",
    "startedAt": "2024-01-01T00:00:00Z",
    "estimatedCompletion": "2024-01-01T04:00:00Z",
    "progress": 0,
    "logs": []
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### GET /api/v1/vip/ai/training/{trainingId}/status

获取训练状态。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "trainingId": "training_789012",
    "status": "completed",
    "progress": 100,
    "accuracy": 0.943,
    "loss": 0.067,
    "startedAt": "2024-01-01T00:00:00Z",
    "completedAt": "2024-01-01T03:45:00Z",
    "epochs": 50,
    "logs": [
      {
        "epoch": 50,
        "accuracy": 0.943,
        "loss": 0.067,
        "val_accuracy": 0.921,
        "val_loss": 0.089,
        "timestamp": "2024-01-01T03:45:00Z"
      }
    ],
    "model": {
      "modelId": "model_345678",
      "size": "125MB",
      "version": "1.0.0",
      "downloadUrl": "https://api.example.com/models/model_345678/download"
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## API密钥管理

### GET /api/v1/vip/api/keys

获取API密钥列表。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "keys": [
      {
        "keyId": "key_123456",
        "name": "生产环境密钥",
        "key": "vip_prod_abcd1234****",
        "status": "active",
        "permissions": ["recognition", "analytics"],
        "rateLimit": {
          "requests": 10000,
          "period": "hour",
          "current": 1234
        },
        "usage": {
          "totalRequests": 45678,
          "thisMonth": 12345,
          "thisWeek": 2341,
          "today": 234
        },
        "createdAt": "2024-01-01T00:00:00Z",
        "lastUsedAt": "2024-01-01T12:00:00Z",
        "expiresAt": "2024-12-31T23:59:59Z"
      }
    ],
    "quota": {
      "maxKeys": 5,
      "usedKeys": 2,
      "requestsLimit": 100000,
      "requestsUsed": 45678
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/vip/api/keys

创建新的API密钥。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**请求参数：**

```json
{
  "name": "测试环境密钥",
  "permissions": ["recognition"],
  "rateLimit": 1000,
  "expiresIn": 365
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "API密钥创建成功",
  "data": {
    "keyId": "key_789012",
    "name": "测试环境密钥",
    "key": "vip_test_efgh5678abcd1234mnop9012",
    "permissions": ["recognition"],
    "rateLimit": {
      "requests": 1000,
      "period": "hour"
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "expiresAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### PUT /api/v1/vip/api/keys/{keyId}

更新API密钥配置。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**请求参数：**

```json
{
  "name": "新的密钥名称",
  "permissions": ["recognition", "analytics"],
  "rateLimit": 2000,
  "status": "active"
}
```

### DELETE /api/v1/vip/api/keys/{keyId}

删除API密钥。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "API密钥删除成功",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## API使用统计

### GET /api/v1/vip/api/usage

获取API使用详细统计。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**查询参数：**
- `period` (字符串): 统计周期
- `keyId` (字符串): 指定API密钥ID
- `groupBy` (字符串): 分组方式，可选值：`day`、`hour`、`endpoint`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "month",
    "overview": {
      "totalRequests": 45678,
      "successRequests": 44567,
      "errorRequests": 1111,
      "avgResponseTime": 234,
      "dataTransfer": "125.6GB"
    },
    "byEndpoint": [
      {
        "endpoint": "/api/v1/recognition/image",
        "requests": 35000,
        "success": 34800,
        "errors": 200,
        "avgResponseTime": 1200
      }
    ],
    "byStatus": {
      "200": 44567,
      "400": 234,
      "401": 45,
      "403": 12,
      "500": 820
    },
    "timeline": [
      {
        "date": "2024-01-01",
        "requests": 1567,
        "success": 1534,
        "errors": 33
      }
    ],
    "topErrors": [
      {
        "error": "Rate limit exceeded",
        "count": 456,
        "percentage": 0.41
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 自定义模型管理

### GET /api/v1/vip/models

获取用户的自定义模型列表。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "models": [
      {
        "modelId": "model_123456",
        "name": "我的宠物识别",
        "version": "1.0.0",
        "status": "active",
        "accuracy": 0.943,
        "categories": ["我的猫咪", "我的狗狗"],
        "size": "125MB",
        "trainingTime": "3小时45分钟",
        "createdAt": "2024-01-01T00:00:00Z",
        "lastUsed": "2024-01-01T12:00:00Z",
        "usage": {
          "totalInferences": 567,
          "thisMonth": 123
        }
      }
    ],
    "quota": {
      "maxModels": 10,
      "usedModels": 3,
      "storageLimit": "10GB",
      "storageUsed": "1.2GB"
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## VIP订阅管理

### GET /api/v1/vip/subscription

获取VIP订阅信息。

**请求头：**
```
Authorization: Bearer <token>
X-VIP-Required: true
```

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "subscription": {
      "plan": "vip_premium",
      "status": "active",
      "startDate": "2024-01-01T00:00:00Z",
      "endDate": "2024-12-31T23:59:59Z",
      "autoRenew": true,
      "price": 99.99,
      "currency": "USD",
      "billingCycle": "monthly"
    },
    "features": {
      "apiCalls": {
        "limit": 100000,
        "used": 12345,
        "period": "month"
      },
      "storage": {
        "limit": "100GB",
        "used": "12.5GB"
      },
      "customModels": {
        "limit": 10,
        "used": 3
      },
      "advancedAnalytics": true,
      "prioritySupport": true
    },
    "nextBilling": {
      "date": "2024-02-01T00:00:00Z",
      "amount": 99.99
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 70001 | VIP权限不足 |
| 70002 | VIP订阅已过期 |
| 70003 | API密钥数量超出限制 |
| 70004 | API调用次数超出限制 |
| 70005 | 自定义模型数量超出限制 |
| 70006 | 存储空间不足 |
| 70007 | 训练数据集格式错误 |
| 70008 | 模型训练失败 |
| 70009 | API密钥无效 |
| 70010 | 高级功能暂时不可用 |
