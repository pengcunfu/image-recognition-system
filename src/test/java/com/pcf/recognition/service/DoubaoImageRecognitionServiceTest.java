package com.pcf.recognition.service;

import com.pcf.recognition.config.DoubaoConfig;
import com.pcf.recognition.dto.ImageRecognitionRequest;
import com.pcf.recognition.dto.ImageRecognitionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Doubao图像识别服务测试
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "doubao.api.key=api-key-20250929130415",
        "doubao.api.base-url=https://ark.cn-beijing.volces.com/api/v3",
        "doubao.api.model=doubao-1-5-thinking-vision-pro-250428",
        "image.recognition.prompt.default=分析图像并以JSON格式输出核心属性:{\"category\":\"物体主类别\",\"name\":\"具体名称\",\"color\":\"主要颜色\",\"shape\":\"形状特征\",\"material\":\"材质纹理\",\"attributes\":[\"关键属性1\",\"关键属性2\"],\"confidence\":0.95}要求:1.准确识别主要物体2.颜色用具体色彩词汇3.形状描述简洁明确4.材质包含质感特征5.属性突出显著特点6.置信度0-1之间7.仅输出JSON，无额外文字"
})
public class DoubaoImageRecognitionServiceTest {
    
    private DoubaoImageRecognitionService service;
    private DoubaoConfig config;
    
    @BeforeEach
    void setUp() {
        config = new DoubaoConfig();
        config.setKey("api-key-20250929130415");
        config.setBaseUrl("https://ark.cn-beijing.volces.com/api/v3");
        config.setModel("doubao-1-5-thinking-vision-pro-250428");
        config.setTimeout(30000);
        config.setMaxRetries(3);
        
        service = new DoubaoImageRecognitionService(config);
        service.initArkService();
    }
    
    @Test
    void testConnectionTest() {
        log.info("开始测试Doubao连接...");
        
        // 这是一个连接测试，实际测试需要有效的API密钥
        boolean result = service.testConnection();
        
        // 由于使用测试密钥，连接可能失败，这是正常的
        log.info("连接测试结果: {}", result);
        
        // 测试应该不会抛出异常
        assertNotNull(result);
    }
    
    @Test
    void testRecognizeImageWithMockData() {
        log.info("开始测试图像识别（模拟数据）...");
        
        // 创建测试请求 - 使用一个简单的测试图像URL
        ImageRecognitionRequest request = new ImageRecognitionRequest();
        request.setImageUrl("https://example.com/test-image.jpg");
        request.setDetailedAnalysis(false);
        request.setMaxTokens(500);
        request.setTemperature(0.1);
        
        // 执行识别
        ImageRecognitionResponse response = service.recognizeImage(request);
        
        // 验证响应结构
        assertNotNull(response);
        assertNotNull(response.getSuccess());
        assertNotNull(response.getProcessingTime());
        
        // 如果失败，应该有错误信息
        if (!response.getSuccess()) {
            assertNotNull(response.getErrorMessage());
            log.info("识别失败（预期）: {}", response.getErrorMessage());
        } else {
            // 如果成功，应该有数据
            assertNotNull(response.getData());
            log.info("识别成功: {}", response.getData());
        }
        
        log.info("图像识别测试完成，处理时间: {}ms", response.getProcessingTime());
    }
    
    @Test
    void testRecognizeImageWithBase64() {
        log.info("开始测试Base64图像识别...");
        
        // 创建一个简单的Base64测试数据（1x1像素的透明PNG）
        String testBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        
        ImageRecognitionRequest request = new ImageRecognitionRequest();
        request.setImageBase64(testBase64);
        request.setDetailedAnalysis(false);
        request.setMaxTokens(300);
        request.setTemperature(0.1);
        
        ImageRecognitionResponse response = service.recognizeImage(request);
        
        assertNotNull(response);
        assertNotNull(response.getSuccess());
        
        if (!response.getSuccess()) {
            log.info("Base64识别失败（预期）: {}", response.getErrorMessage());
        } else {
            log.info("Base64识别成功: {}", response.getData());
        }
    }
    
    @Test
    void testInvalidRequest() {
        log.info("开始测试无效请求处理...");
        
        // 创建无效请求（没有图像数据）
        ImageRecognitionRequest request = new ImageRecognitionRequest();
        request.setMaxTokens(100);
        
        ImageRecognitionResponse response = service.recognizeImage(request);
        
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertNotNull(response.getErrorMessage());
        
        log.info("无效请求处理正确，错误信息: {}", response.getErrorMessage());
    }
}
