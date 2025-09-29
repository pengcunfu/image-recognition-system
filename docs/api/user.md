# 用户管理接口

## 获取当前用户信息

### GET /api/v1/user/profile

获取当前登录用户的详细信息。

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
    "userId": "12345",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13812345678",
    "avatar": "https://example.com/avatar.jpg",
    "role": "vip",
    "status": "active",
    "profile": {
      "nickname": "测试用户",
      "bio": "热爱AI技术的图像识别爱好者",
      "location": "北京市",
      "website": "https://myblog.com",
      "birthday": "1990-01-01",
      "gender": "male"
    },
    "statistics": {
      "totalRecognitions": 156,
      "totalPosts": 12,
      "totalLikes": 89,
      "totalFollowers": 45,
      "totalFollowing": 32
    },
    "preferences": {
      "language": "zh-CN",
      "timezone": "Asia/Shanghai",
      "theme": "light",
      "emailNotifications": true,
      "pushNotifications": true
    },
    "subscription": {
      "plan": "vip",
      "expiresAt": "2024-12-31T23:59:59Z",
      "autoRenew": true
    },
    "joinDate": "2024-01-01T00:00:00Z",
    "lastLoginAt": "2024-01-01T00:00:00Z",
    "emailVerified": true,
    "phoneVerified": false
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 更新用户资料

### PUT /api/v1/user/profile

更新用户的个人资料信息。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "nickname": "新昵称",
  "bio": "新的个人简介",
  "location": "上海市",
  "website": "https://newblog.com",
  "birthday": "1990-01-01",
  "gender": "female"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "profile": {
      "nickname": "新昵称",
      "bio": "新的个人简介",
      "location": "上海市",
      "website": "https://newblog.com",
      "birthday": "1990-01-01",
      "gender": "female"
    },
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 上传头像

### POST /api/v1/user/avatar

上传用户头像。

**请求头：**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数：**
- `avatar` (文件): 头像文件，支持jpg、png格式，最大2MB

**响应示例：**

```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": {
    "avatar": "https://example.com/avatars/user123_new.jpg",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 更新用户设置

### PUT /api/v1/user/preferences

更新用户偏好设置。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "language": "en-US",
  "timezone": "America/New_York",
  "theme": "dark",
  "emailNotifications": false,
  "pushNotifications": true,
  "privacySettings": {
    "showEmail": false,
    "showPhone": false,
    "allowFollow": true,
    "showOnlineStatus": true
  }
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "设置更新成功",
  "data": {
    "preferences": {
      "language": "en-US",
      "timezone": "America/New_York",
      "theme": "dark",
      "emailNotifications": false,
      "pushNotifications": true,
      "privacySettings": {
        "showEmail": false,
        "showPhone": false,
        "allowFollow": true,
        "showOnlineStatus": true
      }
    },
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取用户统计数据

### GET /api/v1/user/statistics

获取当前用户的详细统计数据。

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
    "overview": {
      "totalRecognitions": 156,
      "totalPosts": 12,
      "totalLikes": 89,
      "totalComments": 34,
      "totalShares": 18
    },
    "recognitionStats": {
      "dailyAverage": 5.2,
      "successRate": 0.95,
      "avgConfidence": 0.87,
      "topCategories": [
        { "category": "动物", "count": 45 },
        { "category": "植物", "count": 32 }
      ]
    },
    "socialStats": {
      "postViews": 1248,
      "profileViews": 89,
      "newFollowers": 8,
      "engagementRate": 0.12
    },
    "activityTrend": [
      {
        "date": "2024-01-01",
        "recognitions": 12,
        "posts": 2,
        "likes": 8
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取关注列表

### GET /api/v1/user/following

获取当前用户关注的用户列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `keyword` (字符串): 搜索关键词

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 32,
    "page": 1,
    "pageSize": 20,
    "users": [
      {
        "userId": "67890",
        "username": "expert_user",
        "nickname": "AI专家",
        "avatar": "https://example.com/avatar2.jpg",
        "bio": "专业的AI研究者",
        "role": "vip",
        "isFollowing": true,
        "isFollowedBy": false,
        "followedAt": "2024-01-01T00:00:00Z",
        "mutualFollowers": 5,
        "statistics": {
          "followers": 1234,
          "recognitions": 567
        }
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取粉丝列表

### GET /api/v1/user/followers

获取关注当前用户的粉丝列表。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `keyword` (字符串): 搜索关键词

**响应示例：**（格式与关注列表相同）

## 关注用户

### POST /api/v1/user/follow/{userId}

关注指定用户。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `userId`: 要关注的用户ID

**响应示例：**

```json
{
  "code": 200,
  "message": "关注成功",
  "data": {
    "userId": "67890",
    "isFollowing": true,
    "followedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 取消关注

### DELETE /api/v1/user/follow/{userId}

取消关注指定用户。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `userId`: 要取消关注的用户ID

**响应示例：**

```json
{
  "code": 200,
  "message": "取消关注成功",
  "data": {
    "userId": "67890",
    "isFollowing": false
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取用户收藏

### GET /api/v1/user/favorites

获取用户的收藏内容。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `type` (字符串): 收藏类型，可选值：`recognition`、`post`、`knowledge`、`all`，默认`all`
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `keyword` (字符串): 搜索关键词

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 45,
    "page": 1,
    "pageSize": 20,
    "items": [
      {
        "id": "fav_123",
        "type": "recognition",
        "itemId": "rec_123456789",
        "item": {
          "recognitionId": "rec_123456789",
          "imageUrl": "https://example.com/image.jpg",
          "topResult": {
            "label": "金毛犬",
            "confidence": 0.95
          }
        },
        "favoritedAt": "2024-01-01T00:00:00Z",
        "tags": ["宠物", "可爱"]
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 搜索用户

### GET /api/v1/user/search

搜索系统中的用户。

**请求头：**
```
Authorization: Bearer <token>
```

**查询参数：**
- `keyword` (字符串): 搜索关键词，必填
- `page` (数字): 页码，默认1
- `pageSize` (数字): 每页数量，默认20
- `role` (字符串): 用户角色筛选，可选值：`user`、`vip`、`admin`
- `location` (字符串): 地区筛选

**响应示例：**

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "total": 15,
    "page": 1,
    "pageSize": 20,
    "users": [
      {
        "userId": "67890",
        "username": "search_result",
        "nickname": "搜索结果用户",
        "avatar": "https://example.com/avatar.jpg",
        "bio": "简介内容",
        "role": "vip",
        "location": "北京市",
        "isFollowing": false,
        "isFollowedBy": false,
        "mutualFollowers": 2,
        "statistics": {
          "followers": 234,
          "recognitions": 123
        },
        "lastActiveAt": "2024-01-01T00:00:00Z"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取其他用户信息

### GET /api/v1/user/{userId}

获取指定用户的公开信息。

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
- `userId`: 用户ID

**响应示例：**

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": "67890",
    "username": "other_user",
    "nickname": "其他用户",
    "avatar": "https://example.com/avatar.jpg",
    "role": "vip",
    "status": "active",
    "profile": {
      "bio": "用户简介",
      "location": "上海市",
      "website": "https://website.com",
      "joinDate": "2023-01-01T00:00:00Z"
    },
    "statistics": {
      "totalRecognitions": 234,
      "totalPosts": 18,
      "totalLikes": 156,
      "totalFollowers": 89,
      "totalFollowing": 45
    },
    "relationship": {
      "isFollowing": true,
      "isFollowedBy": false,
      "mutualFollowers": 12
    },
    "recentActivity": [
      {
        "type": "recognition",
        "description": "识别了一张图片",
        "time": "2024-01-01T00:00:00Z"
      }
    ],
    "badges": [
      {
        "id": "active_user",
        "name": "活跃用户",
        "icon": "https://example.com/badge.png",
        "description": "连续登录30天"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 注销账户

### DELETE /api/v1/user/account

注销当前用户账户（软删除）。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "password": "current_password",
  "reason": "注销原因",
  "deleteData": false
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "账户注销成功",
  "data": {
    "deactivatedAt": "2024-01-01T00:00:00Z",
    "reactivationDeadline": "2024-01-31T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 60001 | 用户不存在 |
| 60002 | 用户已被禁用 |
| 60003 | 权限不足 |
| 60004 | 头像文件格式不支持 |
| 60005 | 头像文件过大 |
| 60006 | 无法关注自己 |
| 60007 | 已经关注该用户 |
| 60008 | 未关注该用户 |
| 60009 | 用户设置无效 |
| 60010 | 账户注销失败 |
