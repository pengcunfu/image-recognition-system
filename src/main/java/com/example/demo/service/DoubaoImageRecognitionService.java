package com.example.demo.service;

import com.example.demo.config.DoubaoConfig;
import com.example.demo.dto.ImageRecognitionRequest;
import com.example.demo.dto.ImageRecognitionResponse;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Doubao图像识别服务
 * 使用火山引擎官方SDK实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoubaoImageRecognitionService {
    
    private final DoubaoConfig doubaoConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ArkService arkService;
    
    @Value("${image.recognition.prompt.default}")
    private String defaultPrompt;
    
    @Value("${image.recognition.prompt.detailed}")
    private String detailedPrompt;
    
    /**
     * 初始化Ark服务客户端
     */
    @PostConstruct
    public void initArkService() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        
        this.arkService = ArkService.builder()
                .apiKey(doubaoConfig.getKey())
                .baseUrl(doubaoConfig.getBaseUrl())
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .build();
                
        log.info("Ark服务客户端初始化完成，baseUrl: {}", doubaoConfig.getBaseUrl());
    }
    
    /**
     * 销毁资源
     */
    @PreDestroy
    public void destroy() {
        if (arkService != null) {
            arkService.shutdownExecutor();
            log.info("Ark服务客户端已关闭");
        }
    }
    
    /**
     * 识别图像 - 主要方法
     */
    public ImageRecognitionResponse recognizeImage(ImageRecognitionRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 构建消息
            List<ChatMessage> messages = buildChatMessages(request);
            
            // 构建请求
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model(doubaoConfig.getModel())
                    .messages(messages)
                    .maxTokens(request.getMaxTokens())
                    .temperature(request.getTemperature())
                    .build();
            
            // 发送请求并获取响应
            ChatCompletionResult response = arkService.createChatCompletion(chatRequest);
            
            // 解析响应
            ImageRecognitionResponse result = parseResponse(response);
            
            // 设置处理时间
            result.setProcessingTime(System.currentTimeMillis() - startTime);
            
            return result;
            
        } catch (Exception e) {
            log.error("图像识别失败", e);
            return ImageRecognitionResponse.builder()
                    .success(false)
                    .errorMessage("图像识别失败: " + e.getMessage())
                    .processingTime(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    /**
     * 构建聊天消息
     */
    private List<ChatMessage> buildChatMessages(ImageRecognitionRequest request) {
        // 选择提示词
        String prompt = request.getCustomPrompt() != null ? 
                request.getCustomPrompt() : 
                (Boolean.TRUE.equals(request.getDetailedAnalysis()) ? detailedPrompt : defaultPrompt);
        
        // 构建多模态内容
        List<ChatCompletionContentPart> multiParts = new ArrayList<>();
        
        // 添加图像部分
        if (request.getImageUrl() != null) {
            multiParts.add(ChatCompletionContentPart.builder()
                    .type("image_url")
                    .imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(
                            request.getImageUrl()
                    ))
                    .build());
        } else if (request.getImageBase64() != null) {
            // 如果是base64，转换为data URL格式
            String dataUrl = "data:image/jpeg;base64," + request.getImageBase64();
            multiParts.add(ChatCompletionContentPart.builder()
                    .type("image_url")
                    .imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(
                            dataUrl
                    ))
                    .build());
        }
        
        // 添加文本提示词
        multiParts.add(ChatCompletionContentPart.builder()
                .type("text")
                .text(prompt)
                .build());
        
        // 构建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .multiContent(multiParts)
                .build();
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(userMessage);
        
        return messages;
    }
    
    /**
     * 解析响应结果
     */
    private ImageRecognitionResponse parseResponse(ChatCompletionResult response) {
        try {
            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("API响应中没有选择项");
            }
            
            String content = response.getChoices().get(0).getMessage().getContent();
            log.debug("AI响应内容: {}", content);
            
            // 尝试提取JSON
            String jsonContent = extractJsonFromText(content);
            
            // 解析JSON为识别数据
            ImageRecognitionResponse.RecognitionData data = parseRecognitionData(jsonContent);
            data.setRawResponse(content);
            
            return ImageRecognitionResponse.builder()
                    .success(true)
                    .data(data)
                    .tokenUsage(response.getUsage() != null ? response.getUsage().getTotalTokens() : null)
                    .build();
            
        } catch (Exception e) {
            log.error("解析响应失败", e);
            return ImageRecognitionResponse.builder()
                    .success(false)
                    .errorMessage("解析AI响应失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 从文本中提取JSON
     */
    private String extractJsonFromText(String text) {
        // 尝试匹配JSON对象
        Pattern jsonPattern = Pattern.compile("\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}", Pattern.DOTALL);
        Matcher matcher = jsonPattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        // 如果没找到完整JSON，返回原文本
        return text;
    }
    
    /**
     * 解析识别数据
     */
    private ImageRecognitionResponse.RecognitionData parseRecognitionData(String jsonContent) {
        try {
            // 尝试直接解析为我们的格式
            return objectMapper.readValue(jsonContent, ImageRecognitionResponse.RecognitionData.class);
        } catch (Exception e) {
            log.warn("JSON解析失败，尝试手动解析: {}", e.getMessage());
            
            // 如果JSON解析失败，创建一个基础的响应
            return ImageRecognitionResponse.RecognitionData.builder()
                    .category("未知")
                    .name("解析失败")
                    .color("未知")
                    .shape("未知")
                    .material("未知")
                    .attributes(Arrays.asList("JSON解析异常"))
                    .confidence(0.0)
                    .build();
        }
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            // 构建一个简单的测试请求
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage testMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content("测试连接")
                    .build();
            messages.add(testMessage);
            
            ChatCompletionRequest testRequest = ChatCompletionRequest.builder()
                    .model(doubaoConfig.getModel())
                    .messages(messages)
                    .maxTokens(10)
                    .temperature(0.1)
                    .build();
            
            ChatCompletionResult response = arkService.createChatCompletion(testRequest);
            return response != null && response.getChoices() != null && !response.getChoices().isEmpty();
            
        } catch (Exception e) {
            log.error("连接测试失败", e);
            return false;
        }
    }
}
