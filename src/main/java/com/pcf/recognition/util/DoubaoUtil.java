package com.pcf.recognition.util;

import com.pcf.recognition.config.DoubaoConfig;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 豆包视觉识别工具类
 * 使用火山引擎ARK Runtime SDK调用豆包大模型
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DoubaoUtil {
    
    private final DoubaoConfig doubaoConfig;
    
    private ArkService arkService;
    
    /**
     * 初始化ARK服务
     */
    @PostConstruct
    public void init() {
        log.info("初始化豆包视觉服务: baseUrl={}, model={}", 
                doubaoConfig.getBaseUrl(), doubaoConfig.getModel());
        
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        
        this.arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(doubaoConfig.getBaseUrl())
                .apiKey(doubaoConfig.getKey())
                .build();
    }
    
    /**
     * 识别图像内容
     * 
     * @param imageUrl 图像URL（必须是公网可访问的URL）
     * @param prompt 提示词
     * @return 识别结果
     */
    public String recognizeImage(String imageUrl, String prompt) {
        log.info("调用豆包视觉识别: imageUrl={}, prompt={}", imageUrl, prompt);
        
        try {
            // 构建消息内容
            List<ChatCompletionContentPart> multiParts = new ArrayList<>();
            
            // 添加图片
            multiParts.add(ChatCompletionContentPart.builder()
                    .type("image_url")
                    .imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(imageUrl))
                    .build());
            
            // 添加文本提示
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
            
            // 构建请求
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(doubaoConfig.getModel())
                    .messages(messages)
                    .build();
            
            // 调用API
            StringBuilder result = new StringBuilder();
            arkService.createChatCompletion(request).getChoices().forEach(choice -> {
                Object content = choice.getMessage().getContent();
                if (content != null) {
                    result.append(content.toString());
                    log.debug("豆包返回内容: {}", content);
                }
            });
            
            String finalResult = result.toString();
            log.info("豆包识别完成，结果长度: {}", finalResult.length());
            
            return finalResult;
            
        } catch (Exception e) {
            log.error("豆包视觉识别失败", e);
            throw new RuntimeException("图像识别失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量识别图像
     * 
     * @param imageUrls 图像URL列表
     * @param prompt 提示词
     * @return 识别结果列表
     */
    public List<String> recognizeImages(List<String> imageUrls, String prompt) {
        log.info("批量识别图像，数量: {}", imageUrls.size());
        
        List<String> results = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            try {
                String result = recognizeImage(imageUrl, prompt);
                results.add(result);
            } catch (Exception e) {
                log.error("识别图像失败: imageUrl={}", imageUrl, e);
                results.add("识别失败: " + e.getMessage());
            }
        }
        
        return results;
    }
    
    /**
     * 使用默认提示词识别图像
     * 
     * @param imageUrl 图像URL
     * @return 识别结果
     */
    public String recognizeImageWithDefaultPrompt(String imageUrl) {
        String defaultPrompt = "请详细描述这张图片的内容，包括：\n" +
                "1. 主要对象或主题\n" +
                "2. 场景和背景\n" +
                "3. 颜色和光线\n" +
                "4. 其他重要细节";
        
        return recognizeImage(imageUrl, defaultPrompt);
    }
    
    /**
     * 识别图像中的特定对象
     * 
     * @param imageUrl 图像URL
     * @param targetObject 目标对象
     * @return 识别结果
     */
    public String recognizeSpecificObject(String imageUrl, String targetObject) {
        String prompt = String.format("请识别图片中的%s，并提供详细信息，包括：\n" +
                "1. 该对象的位置和数量\n" +
                "2. 外观特征\n" +
                "3. 状态和条件\n" +
                "4. 相关的其他信息", targetObject);
        
        return recognizeImage(imageUrl, prompt);
    }
    
    /**
     * 关闭服务
     */
    @PreDestroy
    public void shutdown() {
        if (arkService != null) {
            arkService.shutdownExecutor();
            log.info("豆包视觉服务已关闭");
        }
    }
}

