# 通知系统使用指南

## 📋 系统概述

基于SSE（Server-Sent Events）的实时通知系统，支持服务器主动推送通知到前端。

## 🔧 后端使用

### 1. 注入NotificationService

```java
@Service
@RequiredArgsConstructor
public class YourService {
    private final NotificationService notificationService;
}
```

### 2. 发送通知

#### 简单通知
```java
notificationService.sendNotification(
    userId,                           // 接收用户ID
    NotificationType.COMMENT,         // 通知类型
    "有人评论了你的帖子",              // 标题
    "用户张三评论了你的帖子"           // 内容
);
```

#### 完整通知（带链接和附加数据）
```java
Map<String, Object> data = new HashMap<>();
data.put("postId", 123);
data.put("commentId", 456);

notificationService.sendNotification(
    userId,                           // 接收用户ID
    NotificationType.COMMENT,         // 通知类型
    "有人评论了你的帖子",              // 标题
    "用户张三评论了你的帖子",          // 内容
    "/user/community/post/123",       // 跳转链接
    senderId,                         // 发送者ID
    data                              // 附加数据（JSON）
);
```

### 3. 通知类型

```java
public enum NotificationType {
    SYSTEM(0, "系统通知"),
    COMMENT(1, "评论通知"),
    LIKE(2, "点赞通知"),
    COLLECT(3, "收藏通知"),
    AUDIT(4, "审核通知"),
    VIP(5, "VIP通知"),
    REPORT(6, "举报通知"),
    RECOGNITION(7, "识别完成"),
    REPLY(8, "回复通知");
}
```

## 🎨 前端使用

### 1. 在布局中添加通知中心

```vue
<template>
  <a-layout>
    <a-layout-header>
      <!-- 其他导航元素 -->
      <NotificationCenter />
    </a-layout-header>
  </a-layout>
</template>

<script setup lang="ts">
import NotificationCenter from '@/components/NotificationCenter.vue'
</script>
```

### 2. 手动调用API

```typescript
import NotificationAPI from '@/api/notification'

// 获取未读数量
const count = await NotificationAPI.getUnreadCount()

// 获取通知列表
const notifications = await NotificationAPI.getNotifications({
  page: 1,
  size: 10,
  isRead: 0 // 0-未读, 1-已读, 不传为全部
})

// 标记为已读
await NotificationAPI.markAsRead(notificationId)

// 全部标记为已读
await NotificationAPI.markAllAsRead()

// 删除通知
await NotificationAPI.deleteNotification(notificationId)
```

### 3. 自定义SSE连接

```typescript
import NotificationAPI from '@/api/notification'

const eventSource = NotificationAPI.createSseConnection(
  // 接收到新通知
  (notification) => {
    console.log('新通知:', notification)
    // 播放声音、显示桌面通知等
  },
  // 未读数更新
  (count) => {
    console.log('未读数:', count)
  },
  // 错误处理
  (error) => {
    console.error('连接错误:', error)
  }
)

// 关闭连接
onUnmounted(() => {
  eventSource?.close()
})
```

## 📱 集成示例

### 示例1：帖子被点赞时发送通知

```java
// CommunityService.java
@Transactional
public void likePost(Long userId, Long postId) {
    // ... 点赞逻辑 ...
    
    // 获取帖子作者
    CommunityPost post = communityPostRepository.selectById(postId);
    
    // 发送通知给帖子作者（不给自己发）
    if (!post.getUserId().equals(userId)) {
        User likeUser = userRepository.selectById(userId);
        String userName = likeUser.getNickname() != null ? 
            likeUser.getNickname() : likeUser.getUsername();
        
        Map<String, Object> data = new HashMap<>();
        data.put("postId", postId);
        data.put("postTitle", post.getTitle());
        
        notificationService.sendNotification(
            post.getUserId(),
            NotificationType.LIKE,
            "有人点赞了你的帖子",
            userName + " 点赞了你的帖子《" + post.getTitle() + "》",
            "/user/community/post/" + postId,
            userId,
            data
        );
    }
}
```

### 示例2：识别完成通知

```java
// RecognitionService.java
@Transactional
public void completeRecognition(Long userId, Long resultId, String category) {
    // ... 识别逻辑 ...
    
    Map<String, Object> data = new HashMap<>();
    data.put("resultId", resultId);
    data.put("category", category);
    
    notificationService.sendNotification(
        userId,
        NotificationType.RECOGNITION,
        "图像识别完成",
        "您的图像识别任务已完成，识别结果为：" + category,
        "/user/recognition/detail/" + resultId,
        null,
        data
    );
}
```

### 示例3：VIP到期提醒

```java
// VipService.java (定时任务)
@Scheduled(cron = "0 0 10 * * ?") // 每天10点执行
public void checkVipExpiring() {
    // 查询即将到期的VIP用户（3天内）
    LocalDateTime threeDaysLater = LocalDateTime.now().plusDays(3);
    List<User> expiringUsers = userRepository.findVipExpiringSoon(threeDaysLater);
    
    for (User user : expiringUsers) {
        long daysLeft = ChronoUnit.DAYS.between(
            LocalDateTime.now(), 
            user.getVipExpireTime()
        );
        
        notificationService.sendNotification(
            user.getId(),
            NotificationType.VIP,
            "VIP即将到期",
            "您的VIP会员将在" + daysLeft + "天后到期，请及时续费",
            "/user/vip",
            null,
            null
        );
    }
}
```

## 🔄 路由配置

在路由文件中添加通知页面：

```typescript
// router/index.ts
{
  path: '/user/notifications',
  name: 'Notifications',
  component: () => import('@/views/NotificationView.vue'),
  meta: { requiresAuth: true }
}
```

## 📊 数据库字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 通知ID |
| user_id | BIGINT | 接收用户ID |
| type | TINYINT | 通知类型 |
| title | VARCHAR(200) | 通知标题 |
| content | TEXT | 通知内容 |
| data | JSON | 附加数据 |
| link_url | VARCHAR(500) | 跳转链接 |
| sender_id | BIGINT | 发送者ID |
| is_read | TINYINT | 是否已读 |
| read_time | DATETIME | 阅读时间 |
| created_at | DATETIME | 创建时间 |

## 🌟 高级功能

### 浏览器通知

```typescript
// 请求通知权限
if ('Notification' in window && Notification.permission === 'default') {
  Notification.requestPermission()
}

// 显示桌面通知
function showDesktopNotification(notification: NotificationInfo) {
  if (Notification.permission === 'granted') {
    new Notification(notification.title, {
      body: notification.content,
      icon: notification.senderAvatar || '/favicon.png'
    })
  }
}
```

### 声音提示

```typescript
const notificationSound = new Audio('/notification.mp3')

function playNotificationSound() {
  notificationSound.play().catch(error => {
    console.log('播放声音失败:', error)
  })
}
```

## 🎯 API接口列表

### 后端接口

- `GET /api/notifications/stream` - 建立SSE连接
- `GET /api/notifications/unread-count` - 获取未读数量
- `GET /api/notifications?page=1&size=10&isRead=0` - 获取通知列表
- `PUT /api/notifications/{id}/read` - 标记为已读
- `PUT /api/notifications/read-all` - 全部标记为已读
- `DELETE /api/notifications/{id}` - 删除通知

### SSE事件

- `connected` - 连接成功
- `notification` - 新通知推送
- `unread_count` - 未读数更新

## ⚠️ 注意事项

1. **SSE超时**：连接30分钟后自动断开，前端会自动重连
2. **跨域问题**：确保SSE请求携带认证token
3. **性能优化**：只连接已登录用户的SSE
4. **错误处理**：SSE断开后应有重连机制
5. **浏览器兼容**：IE不支持SSE，需要降级方案（轮询）

## 📝 待扩展功能

- [ ] 邮件通知
- [ ] 短信通知  
- [ ] 微信公众号通知
- [ ] 通知分组（系统、互动、私信等）
- [ ] 通知设置（用户可配置接收哪些类型）
- [ ] IP定位服务集成

