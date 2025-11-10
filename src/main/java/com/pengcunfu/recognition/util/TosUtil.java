package com.pengcunfu.recognition.util;

import com.pengcunfu.recognition.config.VolcengineConfig;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.TosException;
import com.volcengine.tos.model.object.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 火山引擎TOS对象存储工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TosUtil {
    
    private final VolcengineConfig volcengineConfig;
    
    private TOSV2 tosClient;
    
    /**
     * 初始化TOS客户端
     */
    @PostConstruct
    public void init() {
        VolcengineConfig.TosConfig tosConfig = volcengineConfig.getTos();
        
        log.info("初始化TOS客户端: region={}, endpoint={}, bucket={}", 
                tosConfig.getRegion(), tosConfig.getEndpoint(), tosConfig.getBucket());
        
        this.tosClient = new TOSV2ClientBuilder().build(
                tosConfig.getRegion(),
                tosConfig.getEndpoint(),
                volcengineConfig.getAccessKeyId(),
                volcengineConfig.getSecretAccessKey()
        );
    }
    
    /**
     * 上传文件到TOS
     * 
     * @param file 文件
     * @param prefix 文件路径前缀（可选）
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file, String prefix) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // 构建对象key：prefix/yyyy/MM/dd/uuid.ext
        String key = buildObjectKey(prefix, extension);
        
        log.info("上传文件到TOS: bucket={}, key={}, size={}", 
                volcengineConfig.getTos().getBucket(), key, file.getSize());
        
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectInput input = new PutObjectInput()
                    .setBucket(volcengineConfig.getTos().getBucket())
                    .setKey(key)
                    .setContent(inputStream)
                    .setContentLength(file.getSize());
            
            PutObjectOutput output = tosClient.putObject(input);
            
            log.info("文件上传成功: key={}, etag={}", key, output.getEtag());
            
            // 生成带签名的访问URL
            return generatePresignedUrl(key);
        } catch (TosException e) {
            log.error("上传文件到TOS失败: key={}", key, e);
            throw new RuntimeException("上传文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传字节数组到TOS
     * 
     * @param data 字节数组
     * @param filename 文件名
     * @param prefix 文件路径前缀
     * @return 文件的访问URL
     */
    public String uploadBytes(byte[] data, String filename, String prefix) {
        String extension = getFileExtension(filename);
        String key = buildObjectKey(prefix, extension);
        
        log.info("上传字节数据到TOS: bucket={}, key={}, size={}", 
                volcengineConfig.getTos().getBucket(), key, data.length);
        
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            PutObjectInput input = new PutObjectInput()
                    .setBucket(volcengineConfig.getTos().getBucket())
                    .setKey(key)
                    .setContent(inputStream)
                    .setContentLength((long) data.length);
            
            PutObjectOutput output = tosClient.putObject(input);
            
            log.info("字节数据上传成功: key={}, etag={}", key, output.getEtag());
            
            return generatePresignedUrl(key);
        } catch (TosException | IOException e) {
            log.error("上传字节数据到TOS失败: key={}", key, e);
            throw new RuntimeException("上传数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除TOS上的文件
     * 
     * @param key 对象key
     */
    public void deleteFile(String key) {
        log.info("删除TOS文件: bucket={}, key={}", 
                volcengineConfig.getTos().getBucket(), key);
        
        try {
            DeleteObjectInput input = new DeleteObjectInput()
                    .setBucket(volcengineConfig.getTos().getBucket())
                    .setKey(key);
            
            tosClient.deleteObject(input);
            log.info("文件删除成功: key={}", key);
        } catch (TosException e) {
            log.error("删除TOS文件失败: key={}", key, e);
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成预签名URL
     * 
     * @param key 对象key
     * @return 预签名URL
     */
    public String generatePresignedUrl(String key) {
        VolcengineConfig.TosConfig tosConfig = volcengineConfig.getTos();
        
        try {
            PreSignedURLInput input = new PreSignedURLInput();
            input.setBucket(tosConfig.getBucket());
            input.setKey(key);
            input.setExpires(tosConfig.getUrlExpiration() != null ? tosConfig.getUrlExpiration() : 3600);
            
            PreSignedURLOutput output = tosClient.preSignedURL(input);
            return output.getSignedUrl();
        } catch (TosException e) {
            log.error("生成预签名URL失败: key={}", key, e);
            // 如果生成预签名URL失败，返回普通URL
            return String.format("https://%s.%s/%s", 
                    tosConfig.getBucket(), 
                    tosConfig.getEndpoint().replace("https://", ""), 
                    key);
        }
    }
    
    /**
     * 从URL提取对象key
     * 
     * @param url TOS URL
     * @return 对象key
     */
    public String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        String bucket = volcengineConfig.getTos().getBucket();
        
        // 移除域名部分，只保留key
        String[] parts = url.split(bucket);
        if (parts.length > 1) {
            String key = parts[1];
            // 移除开头的斜杠和查询参数
            key = key.replaceFirst("^/", "").split("\\?")[0];
            return key;
        }
        
        return null;
    }
    
    /**
     * 构建对象key
     * 
     * @param prefix 前缀
     * @param extension 文件扩展名
     * @return 对象key
     */
    private String buildObjectKey(String prefix, String extension) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        StringBuilder keyBuilder = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            keyBuilder.append(prefix).append("/");
        }
        keyBuilder.append(datePath).append("/").append(uuid);
        if (extension != null && !extension.isEmpty()) {
            keyBuilder.append(".").append(extension);
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（不含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
}
