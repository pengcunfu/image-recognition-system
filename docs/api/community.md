# 社区功能接口

## 帖子管理

### GET /api/v1/community/posts

获取社区帖子列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20，最大100
- `category` (字符串): 分类筛选，可选值：`share`、`question`、`discussion`、`tutorial`
- `tag` (字符串): 标签筛选
- `sortBy` (字符串): 排序方式，可选值：`latest`、`hot`、`likes`、`replies`，默认`latest`
- `keyword` (字符串): 搜索关键词
- `authorId` (字符串): 按作者筛选
- `timeRange` (字符串): 时间范围，可选值：`today`、`week`、`month`、`all`

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 1567,
    "page": 1,
    "pageSize": 20,
    "posts": [
      {
        "postId": "post_123456789",
        "title": "AI识别技巧分享",
        "content": "今天分享一些提高图像识别准确率的实用技巧...",
        "excerpt": "分享一些提高图像识别准确率的实用技巧",
        "category": "share",
        "tags": ["技巧", "AI", "识别"],
        "author": {
          "userId": "user_123",
          "username": "ai_expert",
          "nickname": "AI专家",
          "avatar": "https://example.com/avatar.jpg",
          "role": "vip",
          "badges": ["认证专家", "活跃用户"]
        },
        "images": [
          {
            "url": "https://example.com/post_image1.jpg",
            "thumbnail": "https://example.com/post_image1_thumb.jpg",
            "alt": "识别示例图"
          }
        ],
        "statistics": {
          "views": 1234,
          "likes": 89,
          "dislikes": 3,
          "replies": 23,
          "shares": 12,
          "favorites": 45
        },
        "status": "published",
        "isPinned": false,
        "isLocked": false,
        "userInteraction": {
          "isLiked": true,
          "isDisliked": false,
          "isFavorited": false,
          "isFollowing": true
        },
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:30:00Z",
        "lastReplyAt": "2024-01-01T12:00:00Z"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/community/posts

发布新帖子。

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求参数：**

```json
{
  "title": "帖子标题",
  "content": "帖子内容，支持Markdown格式",
  "category": "share",
  "tags": ["标签1", "标签2"],
  "images": [
    {
      "url": "https://example.com/image1.jpg",
      "alt": "图片描述"
    }
  ],
  "attachments": [
    {
      "url": "https://example.com/file.pdf",
      "name": "附件文件.pdf",
      "size": 1024000
    }
  ],
  "settings": {
    "allowComments": true,
    "isAnonymous": false,
    "publishAt": null
  }
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "postId": "post_987654321",
    "title": "帖子标题",
    "url": "https://app.example.com/community/posts/post_987654321",
    "status": "published",
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### GET /api/v1/community/posts/{postId}

获取单个帖子详情。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `postId`: 帖子ID

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "postId": "post_123456789",
    "title": "AI识别技巧分享",
    "content": "完整的帖子内容...",
    "category": "share",
    "tags": ["技巧", "AI", "识别"],
    "author": {/* 作者信息 */},
    "images": [/* 图片列表 */],
    "attachments": [/* 附件列表 */],
    "statistics": {/* 统计信息 */},
    "userInteraction": {/* 用户交互状态 */},
    "relatedPosts": [
      {
        "postId": "post_111",
        "title": "相关帖子标题",
        "author": {/* 作者信息 */},
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:30:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### PUT /api/v1/community/posts/{postId}

更新帖子（仅作者或管理员）。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "title": "更新后的标题",
  "content": "更新后的内容",
  "tags": ["新标签1", "新标签2"],
  "editReason": "修正错误信息"
}
```

### DELETE /api/v1/community/posts/{postId}

删除帖子（仅作者或管理员）。

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

## 评论管理

### GET /api/v1/community/posts/{postId}/comments

获取帖子评论列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `sortBy` (字符串): 排序方式，可选值：`latest`、`oldest`、`likes`，默认`latest`
- `parentId` (字符串): 父评论ID（获取回复）

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 89,
    "page": 1,
    "pageSize": 20,
    "comments": [
      {
        "commentId": "comment_123456",
        "content": "很有用的分享，谢谢！",
        "author": {
          "userId": "user_456",
          "username": "learner",
          "nickname": "学习者",
          "avatar": "https://example.com/avatar2.jpg",
          "role": "user"
        },
        "parentId": null,
        "replyTo": null,
        "likes": 12,
        "dislikes": 0,
        "replies": 3,
        "userInteraction": {
          "isLiked": false,
          "isDisliked": false
        },
        "createdAt": "2024-01-01T01:00:00Z",
        "updatedAt": null
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/community/posts/{postId}/comments

发表评论。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "content": "评论内容",
  "parentId": null,
  "replyToUserId": null
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "commentId": "comment_987654",
    "content": "评论内容",
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 点赞和收藏

### POST /api/v1/community/posts/{postId}/like

点赞帖子。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "isLiked": true,
    "likesCount": 90
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### DELETE /api/v1/community/posts/{postId}/like

取消点赞。

**请求头：**
```
Authorization: Bearer <token>
```

### POST /api/v1/community/posts/{postId}/favorite

收藏帖子。

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
    "favoritesCount": 46
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/community/comments/{commentId}/like

点赞评论。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "isLiked": true,
    "likesCount": 13
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 关注和订阅

### POST /api/v1/community/posts/{postId}/follow

关注帖子（接收回复通知）。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "关注成功",
  "data": {
    "isFollowing": true
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### GET /api/v1/community/categories

获取社区分类列表。

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "categories": [
      {
        "id": "share",
        "name": "经验分享",
        "description": "分享使用技巧和经验",
        "icon": "fas fa-share-alt",
        "color": "#1890ff",
        "postCount": 567,
        "order": 1
      },
      {
        "id": "question",
        "name": "问答求助",
        "description": "提问和寻求帮助",
        "icon": "fas fa-question-circle",
        "color": "#52c41a",
        "postCount": 234,
        "order": 2
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 标签管理

### GET /api/v1/community/tags

获取热门标签列表。

**查询参数：**
- `limit` (数字): 返回数量限制，默认50
- `category` (字符串): 按分类筛选

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "tags": [
      {
        "name": "AI识别",
        "count": 234,
        "category": "share"
      },
      {
        "name": "技巧分享",
        "count": 189,
        "category": "share"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 搜索功能

### GET /api/v1/community/search

搜索社区内容。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `q` (字符串): 搜索关键词，必填
- `type` (字符串): 搜索类型，可选值：`posts`、`comments`、`users`、`all`，默认`posts`
- `category` (字符串): 分类筛选
- `timeRange` (字符串): 时间范围
- `page` (数字): 页码
- `pageSize` (数字): 每页数量

**响应示例：**

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "total": 45,
    "page": 1,
    "pageSize": 20,
    "results": [
      {
        "type": "post",
        "item": {
          "postId": "post_123",
          "title": "AI识别技巧分享",
          "excerpt": "包含搜索关键词的内容片段...",
          "author": {/* 作者信息 */},
          "createdAt": "2024-01-01T00:00:00Z",
          "highlights": ["<mark>关键词</mark>匹配的内容"]
        }
      }
    ],
    "suggestions": ["AI技术", "图像识别", "深度学习"]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 举报和审核

### POST /api/v1/community/posts/{postId}/report

举报帖子。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "reason": "spam",
  "description": "具体的举报说明",
  "category": "违规内容"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "举报已提交",
  "data": {
    "reportId": "report_123456",
    "status": "pending",
    "submittedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### POST /api/v1/community/comments/{commentId}/report

举报评论。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**（同举报帖子）

## 社区统计

### GET /api/v1/community/statistics

获取社区整体统计信息。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "overview": {
      "totalPosts": 12456,
      "totalComments": 34567,
      "totalUsers": 2345,
      "activeUsers": 567
    },
    "trends": {
      "dailyPosts": [
        {"date": "2024-01-01", "count": 45},
        {"date": "2024-01-02", "count": 52}
      ],
      "popularCategories": [
        {"category": "share", "count": 234},
        {"category": "question", "count": 189}
      ]
    },
    "topContributors": [
      {
        "userId": "user_123",
        "username": "expert",
        "posts": 89,
        "likes": 1234
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 80001 | 帖子不存在 |
| 80002 | 评论不存在 |
| 80003 | 无权限编辑 |
| 80004 | 无权限删除 |
| 80005 | 帖子已被锁定 |
| 80006 | 内容包含敏感词 |
| 80007 | 发帖频率过高 |
| 80008 | 已经点赞过 |
| 80009 | 已经收藏过 |
| 80010 | 搜索关键词过短 |
