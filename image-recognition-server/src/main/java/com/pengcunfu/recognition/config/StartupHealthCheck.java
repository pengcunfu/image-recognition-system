package com.pengcunfu.recognition.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 启动时健康检查
 * 检查 MySQL 和 Redis 连接是否正常
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StartupHealthCheck implements CommandLineRunner {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("系统启动健康检查开始");

        boolean mysqlHealthy = checkMySQLConnection();
        boolean redisHealthy = checkRedisConnection();

        if (mysqlHealthy && redisHealthy) {
            log.info("所有依赖服务连接正常");
        } else {
            log.error("部分依赖服务连接失败");
            if (!mysqlHealthy) {
                log.error("MySQL 连接失败");
            }
            if (!redisHealthy) {
                log.error("Redis 连接失败");
            }
        }

        log.info("系统启动健康检查结束");
    }

    /**
     * 检查 MySQL 连接
     */
    private boolean checkMySQLConnection() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                if (isValid) {
                    log.info("MySQL 连接正常");
                    return true;
                } else {
                    log.error("MySQL 连接无效");
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error("MySQL 连接失败: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("MySQL 连接检查异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Redis 连接
     */
    private boolean checkRedisConnection() {
        try {
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();

            if ("PONG".equals(pong)) {
                log.info("Redis 连接正常");
                
                // 测试基本的读写操作
                String testKey = "startup_health_check";
                String testValue = "test_" + System.currentTimeMillis();

                redisTemplate.opsForValue().set(testKey, testValue);
                Object retrievedValue = redisTemplate.opsForValue().get(testKey);
                redisTemplate.delete(testKey);

                if (testValue.equals(retrievedValue)) {
                    return true;
                } else {
                    log.error("Redis 读写测试失败");
                    return false;
                }
            } else {
                log.error("Redis PING 失败，响应: {}", pong);
                return false;
            }
        } catch (Exception e) {
            log.error("Redis 连接失败: {}", e.getMessage());
            return false;
        }
    }
}
