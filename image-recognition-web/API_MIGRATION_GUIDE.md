# API 调用迁移指南

## 变更说明

### 之前的 API 调用方式
```typescript
// 返回完整的 ApiResponse<T>
const result = await request.get<UserData>('/api/users')
console.log(result.data) // 访问数据需要 .data
console.log(result.code) // 访问状态码
console.log(result.message) // 访问消息
```

### 现在的 API 调用方式
```typescript
// 直接返回 T（data 字段）
const userData = await request.get<UserData>('/api/users')
console.log(userData) // 直接就是数据，不需要 .data

// 如果需要完整响应，使用 Raw 方法
const result = await request.getRaw<UserData>('/api/users')
console.log(result.data)
console.log(result.code)
console.log(result.message)
```

## 错误处理变更

### 之前
- 前端自定义错误消息
- 需要在每个调用处检查 `result.code`

### 现在
- 统一在拦截器中处理错误
- 直接使用后端返回的错误消息
- 错误会自动显示 message 提示
- 调用处只需 try-catch 捕获异常（可选）

## 需要修改的模式

### 1. 访问 `.data` 属性
```typescript
// ❌ 旧代码
const result = await someAPI()
if (result.data) {
  console.log(result.data.id)
}

// ✅ 新代码
const data = await someAPI()
if (data) {
  console.log(data.id)
}
```

### 2. 检查 `.code` 属性
```typescript
// ❌ 旧代码
const result = await AuthAPI.register(...)
if (result.code == 200) {
  // 成功处理
}

// ✅ 新代码
try {
  await AuthAPI.register(...)
  // 成功处理（不会抛出异常就是成功）
} catch (error) {
  // 错误已经在拦截器中处理并显示
}
```

### 3. 访问 `.message` 或 `.total` 等属性
```typescript
// ❌ 旧代码
const result = await getList()
pagination.total = result.total
list.value = result.data

// ✅ 新代码
const result = await getList()
pagination.total = result.total  // 如果 API 返回的就是 { data, total }
list.value = result.data
```

## 批量替换建议

1. 搜索 `result.data` 替换为 `result`（但要注意上下文）
2. 搜索 `result.code == 200` 或 `result.code === 200`，移除这些检查
3. 搜索 `.data.data`，替换为 `.data`（嵌套的情况）
4. 检查所有 API 调用，确保类型正确

## 特殊情况

如果某些 API 需要访问完整的响应对象（包括 code、message、timestamp），使用 Raw 方法：
- `getRaw()`
- `postRaw()`
- `putRaw()`
- `deleteRaw()`
- `patchRaw()`

