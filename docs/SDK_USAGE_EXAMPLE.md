# 火山引擎官方SDK使用示例

## 概述

项目已更新使用火山引擎官方SDK `volcengine-java-sdk-ark-runtime`，提供更稳定、更高效的API调用体验。

## 核心改进

### 🚀 使用官方SDK
- 更稳定的连接管理
- 内置的错误处理和重试机制
- 原生支持多模态内容

### 📦 依赖更新
```xml
<!-- 火山引擎官方SDK -->
<dependency>
    <groupId>com.volcengine</groupId>
    <artifactId>volcengine-java-sdk-ark-runtime</artifactId>
    <version>1.0.13</version>
</dependency>
```

## 快速测试

### 1. 运行快速测试类
```bash
# 编译项目
mvn compile

# 运行快速测试（可选）
java -cp target/classes com.example.demo.test.QuickTest
```

### 2. 启动Spring Boot应用
```bash
mvn spring-boot:run
```

### 3. 测试API接口

#### 测试连接
```bash
curl http://localhost:8080/api/v1/doubao/image/test
```

#### 花卉识别测试
```bash
curl http://localhost:8080/api/v1/test/image-recognition/test-flower
```

## API使用示例

### 1. 文件上传识别
```bash
curl -X POST "http://localhost:8080/api/v1/doubao/image/recognize/upload" \
  -F "file=@flower.jpg" \
  -F "detailedAnalysis=false" \
  -F "maxTokens=500"
```

### 2. JSON请求识别
```bash
curl -X POST "http://localhost:8080/api/v1/doubao/image/recognize/json" \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400",
    "detailedAnalysis": false,
    "maxTokens": 500,
    "temperature": 0.1
  }'
```

### 3. 自定义提示词测试
```bash
curl -X POST "http://localhost:8080/api/v1/test/image-recognition/test-custom-prompt" \
  -d "imageUrl=https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400" \
  -d "prompt=识别这朵花的具体品种、颜色和生长状态"
```

## 代码示例

### Java调用示例
```java
@Autowired
private DoubaoImageRecognitionService doubaoService;

public void recognizeFlower() {
    // 创建识别请求
    ImageRecognitionRequest request = new ImageRecognitionRequest();
    request.setImageUrl("https://example.com/flower.jpg");
    request.setDetailedAnalysis(false);
    request.setMaxTokens(500);
    request.setTemperature(0.1);
    
    // 执行识别
    ImageRecognitionResponse response = doubaoService.recognizeImage(request);
    
    if (response.getSuccess()) {
        System.out.println("识别结果：");
        System.out.println("类别：" + response.getData().getCategory());
        System.out.println("名称：" + response.getData().getName());
        System.out.println("颜色：" + response.getData().getColor());
        System.out.println("置信度：" + response.getData().getConfidence());
        System.out.println("处理时间：" + response.getProcessingTime() + "ms");
        System.out.println("Token使用：" + response.getTokenUsage());
    } else {
        System.err.println("识别失败：" + response.getErrorMessage());
    }
}
```

## 预期响应格式

### 成功响应示例
```json
{
  "success": true,
  "message": "图像识别成功",
  "data": {
    "success": true,
    "data": {
      "category": "花卉",
      "name": "大丽花",
      "color": "暖黄色",
      "shape": "多层花瓣呈放射状排列，整体饱满圆润",
      "material": "植物花瓣（表面有水珠，呈现湿润质感）",
      "attributes": ["多层花瓣", "放射状", "饱满圆润", "湿润质感"],
      "confidence": 0.95,
      "rawResponse": "完整AI响应内容..."
    },
    "processingTime": 1250,
    "tokenUsage": 156
  }
}
```

## 配置说明

### application.properties配置
```properties
# Doubao AI配置
doubao.api.key=api-key-20250929130415
doubao.api.base-url=https://ark.cn-beijing.volces.com/api/v3
doubao.api.model=doubao-1-5-thinking-vision-pro-250428
doubao.api.timeout=30000
doubao.api.max-retries=3

# 图像识别提示词配置
image.recognition.prompt.default=分析图像并以JSON格式输出核心属性:{"category":"物体主类别","name":"具体名称","color":"主要颜色","shape":"形状特征","material":"材质纹理","attributes":["关键属性1","关键属性2"],"confidence":0.95}要求:1.准确识别主要物体2.颜色用具体色彩词汇3.形状描述简洁明确4.材质包含质感特征5.属性突出显著特点6.置信度0-1之间7.仅输出JSON，无额外文字
```

## 环境变量设置

为了安全，建议将API Key设置为环境变量：

### Windows
```cmd
set DOUBAO_API_KEY=api-key-20250929130415
```

### Linux/Mac
```bash
export DOUBAO_API_KEY=api-key-20250929130415
```

然后在配置文件中引用：
```properties
doubao.api.key=${DOUBAO_API_KEY}
```

## 性能优化建议

1. **连接池配置**：SDK内置连接池，可根据需要调整
2. **Token控制**：使用精炼提示词，控制maxTokens参数
3. **缓存策略**：对相同图像可实现结果缓存
4. **批量处理**：对多张图像可并发处理

## 错误处理

### 常见错误及解决方案

1. **API Key无效**
   - 检查API Key是否正确
   - 确认API Key有效期

2. **网络连接失败**
   - 检查网络连接
   - 确认baseUrl配置正确

3. **图像格式不支持**
   - 支持jpg、png、gif等常见格式
   - 检查图像大小是否超限

4. **Token超限**
   - 调整maxTokens参数
   - 使用更精炼的提示词

## 技术支持

如遇到问题，请查看：
1. 应用日志：`logs/application.log`
2. 控制台输出
3. API响应错误信息

---

*本文档基于火山引擎官方SDK v1.0.13编写*
