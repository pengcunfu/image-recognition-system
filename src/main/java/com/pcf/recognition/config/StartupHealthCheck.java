package com.pcf.recognition.config;

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
        log.info("========== 系统启动健康检查开始 ==========");

        boolean mysqlHealthy = checkMySQLConnection();
        boolean redisHealthy = checkRedisConnection();

        if (mysqlHealthy && redisHealthy) {
            log.info("✅ 所有依赖服务连接正常，系统启动成功！");
        } else {
            log.error("❌ 部分依赖服务连接失败，请检查配置！");
            if (!mysqlHealthy) {
                log.error("   - MySQL 连接失败");
            }
            if (!redisHealthy) {
                log.error("   - Redis 连接失败");
            }
        }

        log.info("========== 系统启动健康检查结束 ==========");
    }

    /**
     * 检查 MySQL 连接
     */
    private boolean checkMySQLConnection() {
        try {
            log.info("🔍 检查 MySQL 连接...");

            try (Connection connection = dataSource.getConnection()) {
                // 执行一个简单的查询来验证连接
                boolean isValid = connection.isValid(5); // 5秒超时

                if (isValid) {
                    String url = connection.getMetaData().getURL();
                    String username = connection.getMetaData().getUserName();
                    log.info("✅ MySQL 连接正常");
                    log.info("   - 数据库URL: {}", url);
                    log.info("   - 用户名: {}", username);
                    return true;
                } else {
                    log.error("❌ MySQL 连接无效");
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error("❌ MySQL 连接失败: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ MySQL 连接检查异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Redis 连接
     */
    private boolean checkRedisConnection() {
        try {
            log.info("🔍 检查 Redis 连接...");

            // 执行 PING 命令测试连接
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();

            if ("PONG".equals(pong)) {
                log.info("✅ Redis 连接正常");

                // 测试基本的读写操作
                String testKey = "startup_health_check";
                String testValue = "test_" + System.currentTimeMillis();

                redisTemplate.opsForValue().set(testKey, testValue);
                Object retrievedValue = redisTemplate.opsForValue().get(testKey);
                redisTemplate.delete(testKey);

                if (testValue.equals(retrievedValue)) {
                    log.info("   - Redis 读写测试通过");
                    return true;
                } else {
                    log.error("❌ Redis 读写测试失败");
                    return false;
                }
            } else {
                log.error("❌ Redis PING 失败，响应: {}", pong);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Redis 连接失败: {}", e.getMessage());
            log.warn("💡 请确保 Redis 服务已启动并且配置正确");
            return false;
        }
    }
}
