# 知识库接口

## 获取知识内容列表

### GET /api/v1/knowledge/articles

获取知识库文章列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20，最大100
- `category` (字符串): 分类筛选，可选值：`animals`、`plants`、`technology`、`tutorial`
- `difficulty` (字符串): 难度等级，可选值：`beginner`、`intermediate`、`advanced`
- `keyword` (字符串): 搜索关键词
- `sortBy` (字符串): 排序方式，可选值：`latest`、`popular`、`likes`、`views`，默认`latest`
- `tag` (字符串): 标签筛选

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 567,
    "page": 1,
    "pageSize": 20,
    "articles": [
      {
        "articleId": "kb_123456789",
        "title": "深度学习在图像识别中的应用",
        "summary": "详细介绍深度学习技术在现代图像识别系统中的核心应用和发展趋势",
        "category": "technology",
        "difficulty": "intermediate",
        "tags": ["深度学习", "图像识别", "AI"],
        "author": {
          "userId": "expert_001",
          "username": "ai_researcher",
          "nickname": "AI研究员",
          "avatar": "https://example.com/expert_avatar.jpg",
          "credentials": ["博士", "资深研究员"]
        },
        "thumbnail": "https://example.com/knowledge/thumbnail1.jpg",
        "readingTime": 8,
        "statistics": {
          "views": 2345,
          "likes": 189,
          "favorites": 67,
          "shares": 23
        },
        "userInteraction": {
          "isLiked": false,
          "isFavorited": true,
          "progress": 0.6
        },
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T12:00:00Z",
        "publishedAt": "2024-01-01T00:00:00Z"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取知识内容详情

### GET /api/v1/knowledge/articles/{articleId}

获取单篇知识文章的详细内容。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `articleId`: 文章ID

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "articleId": "kb_123456789",
    "title": "深度学习在图像识别中的应用",
    "content": "# 深度学习在图像识别中的应用\n\n## 引言\n深度学习作为机器学习的一个重要分支...",
    "summary": "详细介绍深度学习技术在现代图像识别系统中的核心应用",
    "category": "technology",
    "difficulty": "intermediate",
    "tags": ["深度学习", "图像识别", "AI"],
    "author": {
      "userId": "expert_001",
      "username": "ai_researcher",
      "nickname": "AI研究员",
      "avatar": "https://example.com/expert_avatar.jpg",
      "bio": "专注于计算机视觉和深度学习研究",
      "credentials": ["博士", "资深研究员"],
      "experience": "10年以上"
    },
    "images": [
      {
        "url": "https://example.com/knowledge/image1.jpg",
        "alt": "深度学习网络结构图",
        "caption": "典型的卷积神经网络结构"
      }
    ],
    "attachments": [
      {
        "url": "https://example.com/knowledge/paper.pdf",
        "name": "相关研究论文.pdf",
        "size": 2048000,
        "type": "pdf"
      }
    ],
    "outline": [
      {
        "level": 1,
        "title": "引言",
        "anchor": "#introduction"
      },
      {
        "level": 2,
        "title": "基础概念",
        "anchor": "#concepts"
      }
    ],
    "relatedArticles": [
      {
        "articleId": "kb_111",
        "title": "卷积神经网络基础",
        "category": "technology",
        "thumbnail": "https://example.com/thumb2.jpg"
      }
    ],
    "prerequisites": [
      {
        "articleId": "kb_222",
        "title": "机器学习基础",
        "required": true
      }
    ],
    "statistics": {
      "views": 2345,
      "likes": 189,
      "favorites": 67,
      "shares": 23,
      "comments": 34
    },
    "userInteraction": {
      "isLiked": false,
      "isFavorited": true,
      "progress": 0.6,
      "lastReadAt": "2024-01-01T10:00:00Z"
    },
    "seo": {
      "keywords": ["深度学习", "图像识别", "卷积神经网络"],
      "description": "详细介绍深度学习技术在图像识别中的应用"
    },
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T12:00:00Z",
    "publishedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取知识分类

### GET /api/v1/knowledge/categories

获取知识库分类列表。

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "categories": [
      {
        "id": "animals",
        "name": "动物知识",
        "description": "各种动物的特征、习性和识别要点",
        "icon": "fas fa-paw",
        "color": "#52c41a",
        "articleCount": 145,
        "subcategories": [
          {
            "id": "mammals",
            "name": "哺乳动物",
            "articleCount": 67
          },
          {
            "id": "birds",
            "name": "鸟类",
            "articleCount": 45
          }
        ],
        "order": 1
      },
      {
        "id": "plants",
        "name": "植物知识",
        "description": "植物分类、特征和识别方法",
        "icon": "fas fa-leaf",
        "color": "#1890ff",
        "articleCount": 128,
        "subcategories": [
          {
            "id": "flowers",
            "name": "花卉",
            "articleCount": 56
          },
          {
            "id": "trees",
            "name": "树木",
            "articleCount": 42
          }
        ],
        "order": 2
      },
      {
        "id": "technology",
        "name": "技术原理",
        "description": "AI技术、算法原理和实现方法",
        "icon": "fas fa-microchip",
        "color": "#722ed1",
        "articleCount": 89,
        "subcategories": [
          {
            "id": "deep-learning",
            "name": "深度学习",
            "articleCount": 34
          },
          {
            "id": "computer-vision",
            "name": "计算机视觉",
            "articleCount": 28
          }
        ],
        "order": 3
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 搜索知识内容

### GET /api/v1/knowledge/search

搜索知识库内容。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `q` (字符串): 搜索关键词，必填
- `category` (字符串): 分类筛选
- `difficulty` (字符串): 难度筛选
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `highlight` (布尔): 是否高亮关键词，默认true

**响应示例：**

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "total": 23,
    "page": 1,
    "pageSize": 20,
    "query": "深度学习",
    "results": [
      {
        "articleId": "kb_123456789",
        "title": "<mark>深度学习</mark>在图像识别中的应用",
        "summary": "详细介绍<mark>深度学习</mark>技术在现代图像识别系统中的核心应用",
        "category": "technology",
        "relevanceScore": 0.95,
        "highlights": [
          "<mark>深度学习</mark>作为机器学习的重要分支",
          "卷积神经网络是<mark>深度学习</mark>的经典架构"
        ],
        "author": {
          "nickname": "AI研究员",
          "avatar": "https://example.com/avatar.jpg"
        },
        "statistics": {
          "views": 2345,
          "likes": 189
        },
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "suggestions": [
      "深度学习算法",
      "深度学习框架",
      "深度学习应用"
    ],
    "facets": {
      "categories": [
        {"name": "technology", "count": 15},
        {"name": "tutorial", "count": 8}
      ],
      "difficulties": [
        {"name": "intermediate", "count": 12},
        {"name": "advanced", "count": 11}
      ]
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 点赞知识内容

### POST /api/v1/knowledge/articles/{articleId}/like

点赞知识文章。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `articleId`: 文章ID

**响应示例：**

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "isLiked": true,
    "likesCount": 190
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### DELETE /api/v1/knowledge/articles/{articleId}/like

取消点赞。

**请求头：**
```
Authorization: Bearer <token>
```

## 收藏知识内容

### POST /api/v1/knowledge/articles/{articleId}/favorite

收藏知识文章。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "isFavorited": true,
    "favoritesCount": 68
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 更新阅读进度

### PUT /api/v1/knowledge/articles/{articleId}/progress

更新文章阅读进度。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "progress": 0.75,
  "currentSection": "实践应用",
  "timeSpent": 300
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "进度更新成功",
  "data": {
    "progress": 0.75,
    "currentSection": "实践应用",
    "totalTimeSpent": 1800,
    "isCompleted": false,
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取学习统计

### GET /api/v1/knowledge/user/statistics

获取用户的学习统计数据。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `period` (字符串): 统计周期，可选值：`week`、`month`、`year`、`all`，默认`month`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "month",
    "overview": {
      "articlesRead": 23,
      "articlesCompleted": 15,
      "totalTimeSpent": 14400,
      "averageReadingTime": 626,
      "favoriteArticles": 12
    },
    "categories": [
      {
        "category": "technology",
        "articlesRead": 12,
        "timeSpent": 7200,
        "completionRate": 0.75
      },
      {
        "category": "animals",
        "articlesRead": 8,
        "timeSpent": 4800,
        "completionRate": 0.88
      }
    ],
    "achievements": [
      {
        "id": "knowledge_seeker",
        "name": "知识探索者",
        "description": "阅读了10篇技术文章",
        "icon": "fas fa-search",
        "earnedAt": "2024-01-15T00:00:00Z"
      }
    ],
    "readingTrend": [
      {
        "date": "2024-01-01",
        "articlesRead": 2,
        "timeSpent": 1200
      }
    ],
    "recommendations": [
      {
        "articleId": "kb_456",
        "title": "推荐文章标题",
        "reason": "基于您的阅读偏好"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取推荐内容

### GET /api/v1/knowledge/recommendations

获取个性化推荐的知识内容。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `limit` (数字): 推荐数量，默认10，最大50
- `type` (字符串): 推荐类型，可选值：`similar`、`trending`、`personalized`、`continue`，默认`personalized`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "recommendations": [
      {
        "articleId": "kb_789",
        "title": "卷积神经网络详解",
        "category": "technology",
        "difficulty": "intermediate",
        "reason": "基于您对深度学习的兴趣",
        "relevanceScore": 0.92,
        "thumbnail": "https://example.com/thumb.jpg",
        "readingTime": 12,
        "statistics": {
          "views": 1567,
          "likes": 123
        }
      }
    ],
    "continueReading": [
      {
        "articleId": "kb_456",
        "title": "机器学习基础",
        "progress": 0.6,
        "lastReadAt": "2024-01-01T10:00:00Z"
      }
    ],
    "trending": [
      {
        "articleId": "kb_321",
        "title": "最新AI技术趋势",
        "trendScore": 95,
        "category": "technology"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 评论功能

### GET /api/v1/knowledge/articles/{articleId}/comments

获取文章评论列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `sortBy` (字符串): 排序方式，可选值：`latest`、`oldest`、`likes`，默认`latest`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 34,
    "page": 1,
    "pageSize": 20,
    "comments": [
      {
        "commentId": "comment_kb_123",
        "content": "很好的文章，讲解得很清楚！",
        "author": {
          "userId": "user_789",
          "username": "student",
          "nickname": "学习者",
          "avatar": "https://example.com/avatar.jpg"
        },
        "likes": 8,
        "replies": 2,
        "userInteraction": {
          "isLiked": false
        },
        "createdAt": "2024-01-01T02:00:00Z"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/knowledge/articles/{articleId}/comments

发表评论。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "content": "评论内容",
  "parentId": null
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "commentId": "comment_kb_456",
    "content": "评论内容",
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 90001 | 知识文章不存在 |
| 90002 | 分类不存在 |
| 90003 | 搜索关键词过短 |
| 90004 | 已经点赞过该文章 |
| 90005 | 已经收藏过该文章 |
| 90006 | 评论内容为空 |
| 90007 | 评论不存在 |
| 90008 | 无权限编辑评论 |
| 90009 | 进度数值无效 |
| 90010 | 文章暂未发布 |
