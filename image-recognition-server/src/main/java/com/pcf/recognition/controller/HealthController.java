package com.pcf.recognition.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pcf.recognition.dto.ApiResponse;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查控制器
 * 提供运行时健康状态检查
 */
@RestController
@RequestMapping("/api/v1/health")
@Slf4j
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 健康检查接口
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        // 检查 MySQL
        boolean mysqlHealthy = checkMySQLHealth();
        healthStatus.put("mysql", createServiceStatus("MySQL", mysqlHealthy));
        
        // 检查 Redis
        boolean redisHealthy = checkRedisHealth();
        healthStatus.put("redis", createServiceStatus("Redis", redisHealthy));
        
        // 整体状态
        boolean overallHealthy = mysqlHealthy && redisHealthy;
        healthStatus.put("overall", createServiceStatus("System", overallHealthy));
        healthStatus.put("timestamp", System.currentTimeMillis());
        
        String message = overallHealthy ? "系统健康状态良好" : "系统存在健康问题";
        
        return ApiResponse.success(healthStatus, message);
    }

    /**
     * 详细健康检查接口
     */
    @GetMapping("/detailed")
    public ApiResponse<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> detailedStatus = new HashMap<>();
        
        // MySQL 详细检查
        Map<String, Object> mysqlDetails = checkMySQLDetailed();
        detailedStatus.put("mysql", mysqlDetails);
        
        // Redis 详细检查
        Map<String, Object> redisDetails = checkRedisDetailed();
        detailedStatus.put("redis", redisDetails);
        
        // 系统信息
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("java_version", System.getProperty("java.version"));
        systemInfo.put("os_name", System.getProperty("os.name"));
        systemInfo.put("available_processors", Runtime.getRuntime().availableProcessors());
        systemInfo.put("max_memory", Runtime.getRuntime().maxMemory());
        systemInfo.put("total_memory", Runtime.getRuntime().totalMemory());
        systemInfo.put("free_memory", Runtime.getRuntime().freeMemory());
        detailedStatus.put("system", systemInfo);
        
        detailedStatus.put("timestamp", System.currentTimeMillis());
        
        return ApiResponse.success(detailedStatus, "详细健康检查完成");
    }

    private boolean checkMySQLHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(3);
        } catch (Exception e) {
            log.warn("MySQL 健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkRedisHealth() {
        try {
            String pong = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            log.warn("Redis 健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    private Map<String, Object> checkMySQLDetailed() {
        Map<String, Object> details = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(3);
            details.put("status", isValid ? "healthy" : "unhealthy");
            details.put("url", connection.getMetaData().getURL());
            details.put("username", connection.getMetaData().getUserName());
            details.put("driver_name", connection.getMetaData().getDriverName());
            details.put("driver_version", connection.getMetaData().getDriverVersion());
            details.put("database_product", connection.getMetaData().getDatabaseProductName());
            details.put("database_version", connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            details.put("status", "error");
            details.put("error", e.getMessage());
        }
        return details;
    }

    private Map<String, Object> checkRedisDetailed() {
        Map<String, Object> details = new HashMap<>();
        try {
            String pong = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            
            details.put("status", "PONG".equals(pong) ? "healthy" : "unhealthy");
            details.put("ping_response", pong);
            
            // 测试读写性能
            long startTime = System.currentTimeMillis();
            String testKey = "health_check_" + startTime;
            redisTemplate.opsForValue().set(testKey, "test");
            Object value = redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            long endTime = System.currentTimeMillis();
            
            details.put("read_write_test", "test".equals(value) ? "passed" : "failed");
            details.put("response_time_ms", endTime - startTime);
            
        } catch (Exception e) {
            details.put("status", "error");
            details.put("error", e.getMessage());
        }
        return details;
    }

    private Map<String, Object> createServiceStatus(String service, boolean healthy) {
        Map<String, Object> status = new HashMap<>();
        status.put("service", service);
        status.put("status", healthy ? "UP" : "DOWN");
        status.put("healthy", healthy);
        return status;
    }
}
