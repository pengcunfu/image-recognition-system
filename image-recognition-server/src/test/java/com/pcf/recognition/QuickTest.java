package com.pcf.recognition;

import com.pcf.recognition.config.DoubaoConfig;
import com.pcf.recognition.config.ImageRecognitionConfig;
import com.pcf.recognition.dto.ImageRecognitionRequest;
import com.pcf.recognition.dto.ImageRecognitionResponse;
import com.pcf.recognition.service.DoubaoImageRecognitionService;

/**
 * 快速测试类
 * 用于验证SDK集成是否正常
 */
public class QuickTest {
    
    public static void main(String[] args) {
        System.out.println("=== Doubao图像识别快速测试 ===");
        
        // 配置
        DoubaoConfig config = new DoubaoConfig();
        config.setKey("api-key-20250929130415");
        config.setBaseUrl("https://ark.cn-beijing.volces.com/api/v3");
        config.setModel("doubao-1-5-thinking-vision-pro-250428");
        config.setTimeout(30000);
        config.setMaxRetries(3);
        
        // 图像识别配置
        ImageRecognitionConfig imageConfig = new ImageRecognitionConfig();
        imageConfig.setEnabled(true);
        imageConfig.setMaxBatchSize(20);
        imageConfig.setSupportedFormats("jpg,jpeg,png,bmp,gif,webp");
        imageConfig.setMaxFileSize(10485760L);
        
        // 设置提示词内容（模拟从文件加载）
        imageConfig.setDefaultPromptContent("分析图像并以JSON格式输出核心属性:{\"category\":\"物体主类别\",\"name\":\"具体名称\",\"color\":\"主要颜色\",\"shape\":\"形状特征\",\"material\":\"材质纹理\",\"attributes\":[\"关键属性1\",\"关键属性2\"],\"confidence\":0.95}要求:1.准确识别主要物体2.颜色用具体色彩词汇3.形状描述简洁明确4.材质包含质感特征5.属性突出显著特点6.置信度0-1之间7.仅输出JSON，无额外文字");
        imageConfig.setDetailedPromptContent("详细分析图像中的所有元素，包括主要物体、背景、环境、光线等，以结构化JSON格式输出详细信息");
        
        // 创建服务
        DoubaoImageRecognitionService service = new DoubaoImageRecognitionService(config, imageConfig);
        service.initArkService();
        
        try {
            // 测试连接
            System.out.println("1. 测试连接...");
            boolean connected = service.testConnection();
            System.out.println("连接结果: " + (connected ? "成功" : "失败"));
            
            if (connected) {
                // 测试图像识别
                System.out.println("\n2. 测试图像识别...");
                ImageRecognitionRequest request = new ImageRecognitionRequest();
                request.setImageUrl("https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400&h=400&fit=crop");
                request.setDetailedAnalysis(false);
                request.setMaxTokens(500);
                request.setTemperature(0.1);
                
                ImageRecognitionResponse response = service.recognizeImage(request);
                
                System.out.println("识别结果:");
                System.out.println("- 成功: " + response.getSuccess());
                System.out.println("- 处理时间: " + response.getProcessingTime() + "ms");
                
                if (response.getSuccess()) {
                    System.out.println("- 类别: " + response.getData().getCategory());
                    System.out.println("- 名称: " + response.getData().getName());
                    System.out.println("- 颜色: " + response.getData().getColor());
                    System.out.println("- 置信度: " + response.getData().getConfidence());
                    System.out.println("- Token使用: " + response.getTokenUsage());
                } else {
                    System.out.println("- 错误: " + response.getErrorMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("测试出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理资源
            service.destroy();
            System.out.println("\n测试完成，资源已清理");
        }
    }
}
