# 认证相关接口

## 用户注册

### POST /api/v1/auth/register

用户注册接口。

**请求参数：**

```json
{
  "username": "string",     // 用户名，3-20字符，必填
  "email": "string",        // 邮箱地址，必填
  "password": "string",     // 密码，6-20字符，必填
  "confirmPassword": "string", // 确认密码，必填
  "phone": "string",        // 手机号，可选
  "inviteCode": "string"    // 邀请码，可选
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": "12345",
    "username": "testuser",
    "email": "test@example.com",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 3600
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 用户登录

### POST /api/v1/auth/login

用户登录接口，支持用户名/邮箱登录。

**请求参数：**

```json
{
  "username": "string",     // 用户名或邮箱，必填
  "password": "string",     // 密码，必填
  "remember": "boolean",    // 是否记住登录状态，可选，默认false
  "captcha": "string",      // 验证码，可选（多次失败后需要）
  "captchaId": "string"     // 验证码ID，可选
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "12345",
    "username": "testuser",
    "email": "test@example.com",
    "role": "user",          // user, vip, admin
    "avatar": "https://example.com/avatar.jpg",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 3600,
    "permissions": ["recognition", "community"],
    "profile": {
      "nickname": "用户昵称",
      "bio": "个人简介",
      "location": "所在地",
      "joinDate": "2024-01-01"
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 刷新Token

### POST /api/v1/auth/refresh

使用refresh token获取新的access token。

**请求参数：**

```json
{
  "refreshToken": "string"  // refresh token，必填
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "token": "new_access_token",
    "refreshToken": "new_refresh_token",
    "expiresIn": 3600
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 退出登录

### POST /api/v1/auth/logout

用户退出登录，使token失效。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "退出登录成功",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 忘记密码

### POST /api/v1/auth/forgot-password

发送密码重置邮件。

**请求参数：**

```json
{
  "email": "string"         // 邮箱地址，必填
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "密码重置邮件已发送",
  "data": {
    "email": "test@example.com",
    "sentAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 重置密码

### POST /api/v1/auth/reset-password

使用重置令牌设置新密码。

**请求参数：**

```json
{
  "token": "string",        // 密码重置令牌，必填
  "newPassword": "string",  // 新密码，必填
  "confirmPassword": "string" // 确认新密码，必填
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 修改密码

### POST /api/v1/auth/change-password

修改当前用户密码。

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**

```json
{
  "currentPassword": "string", // 当前密码，必填
  "newPassword": "string",     // 新密码，必填
  "confirmPassword": "string"  // 确认新密码，必填
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 验证Token

### GET /api/v1/auth/verify

验证当前token是否有效。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "Token有效",
  "data": {
    "valid": true,
    "userId": "12345",
    "username": "testuser",
    "role": "user",
    "expiresAt": "2024-01-01T01:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 获取验证码

### GET /api/v1/auth/captcha

获取图形验证码。

**响应示例：**

```json
{
  "code": 200,
  "message": "验证码获取成功",
  "data": {
    "captchaId": "captcha_id_123",
    "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 邮箱验证

### POST /api/v1/auth/verify-email

发送邮箱验证邮件。

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "验证邮件已发送",
  "data": {
    "email": "test@example.com",
    "sentAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### GET /api/v1/auth/verify-email/{token}

确认邮箱验证。

**路径参数：**
- `token`: 邮箱验证令牌

**响应示例：**

```json
{
  "code": 200,
  "message": "邮箱验证成功",
  "data": {
    "verified": true,
    "verifiedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 40001 | 用户名已存在 |
| 40002 | 邮箱已被注册 |
| 40003 | 用户名或密码错误 |
| 40004 | 账户已被禁用 |
| 40005 | 验证码错误 |
| 40006 | Token已过期 |
| 40007 | Token无效 |
| 40008 | 密码重置令牌无效 |
| 40009 | 邮箱验证令牌无效 |
| 40010 | 当前密码错误 |

## 使用示例

### JavaScript 示例

```javascript
// 用户登录
async function login(username, password) {
  try {
    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username,
        password
      })
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      // 保存token
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      return data.data;
    } else {
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
}

// 自动刷新token
async function refreshToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  try {
    const response = await fetch('/api/v1/auth/refresh', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        refreshToken
      })
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      return data.data.token;
    } else {
      // 刷新失败，重新登录
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
  } catch (error) {
    console.error('Token refresh failed:', error);
  }
}
```

### Python 示例

```python
import requests
import json

class AuthAPI:
    def __init__(self, base_url):
        self.base_url = base_url
        self.token = None
        self.refresh_token = None
    
    def login(self, username, password):
        """用户登录"""
        url = f"{self.base_url}/api/v1/auth/login"
        data = {
            "username": username,
            "password": password
        }
        
        response = requests.post(url, json=data)
        result = response.json()
        
        if result['code'] == 200:
            self.token = result['data']['token']
            self.refresh_token = result['data']['refreshToken']
            return result['data']
        else:
            raise Exception(result['message'])
    
    def get_headers(self):
        """获取请求头"""
        if not self.token:
            raise Exception("未登录")
        
        return {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
    
    def refresh_token_if_needed(self):
        """刷新token"""
        if not self.refresh_token:
            raise Exception("没有refresh token")
        
        url = f"{self.base_url}/api/v1/auth/refresh"
        data = {"refreshToken": self.refresh_token}
        
        response = requests.post(url, json=data)
        result = response.json()
        
        if result['code'] == 200:
            self.token = result['data']['token']
            self.refresh_token = result['data']['refreshToken']
            return True
        else:
            return False

# 使用示例
auth = AuthAPI("https://api.image-recognition.com")
user_data = auth.login("testuser", "password123")
print(f"登录成功，用户ID: {user_data['userId']}")
```
