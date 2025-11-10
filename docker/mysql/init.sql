-- 图像识别系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `image_recognition` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `image_recognition`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` ENUM('USER', 'VIP', 'ADMIN') DEFAULT 'USER' COMMENT '用户角色',
    `status` ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`)
) COMMENT '用户表';

-- 图像元数据表
CREATE TABLE IF NOT EXISTS `image_metadata` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `file_name` VARCHAR(255) NOT NULL COMMENT '存储文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    `width` INT COMMENT '图像宽度',
    `height` INT COMMENT '图像高度',
    `md5_hash` VARCHAR(32) COMMENT 'MD5哈希值',
    `upload_ip` VARCHAR(45) COMMENT '上传IP',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_md5_hash` (`md5_hash`),
    INDEX `idx_created_at` (`created_at`)
) COMMENT '图像元数据表';

-- 识别结果表
CREATE TABLE IF NOT EXISTS `recognition_results` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `image_id` BIGINT NOT NULL COMMENT '图像ID',
    `service_type` ENUM('DOUBAO', 'VOLCENGINE', 'OTHER') NOT NULL COMMENT '识别服务类型',
    `category` VARCHAR(100) COMMENT '物体类别',
    `name` VARCHAR(200) COMMENT '物体名称',
    `color` VARCHAR(100) COMMENT '主要颜色',
    `shape` TEXT COMMENT '形状描述',
    `material` VARCHAR(200) COMMENT '材质描述',
    `attributes` JSON COMMENT '属性列表',
    `confidence` DECIMAL(3,2) COMMENT '置信度(0-1)',
    `raw_response` TEXT COMMENT '原始AI响应',
    `processing_time` INT COMMENT '处理时间(毫秒)',
    `token_usage` INT COMMENT 'Token使用量',
    `prompt_used` TEXT COMMENT '使用的提示词',
    `model_version` VARCHAR(100) COMMENT '模型版本',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '识别时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`image_id`) REFERENCES `image_metadata`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_image_id` (`image_id`),
    INDEX `idx_service_type` (`service_type`),
    INDEX `idx_category` (`category`),
    INDEX `idx_created_at` (`created_at`)
) COMMENT '识别结果表';

-- 识别项目表 (用于批量识别)
CREATE TABLE IF NOT EXISTS `recognition_items` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `batch_id` VARCHAR(100) NOT NULL COMMENT '批次ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `image_id` BIGINT NOT NULL COMMENT '图像ID',
    `result_id` BIGINT COMMENT '识别结果ID',
    `status` ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING' COMMENT '处理状态',
    `error_message` TEXT COMMENT '错误信息',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`image_id`) REFERENCES `image_metadata`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`result_id`) REFERENCES `recognition_results`(`id`) ON DELETE SET NULL,
    INDEX `idx_batch_id` (`batch_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) COMMENT '识别项目表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS `system_logs` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '用户ID',
    `action` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `resource` VARCHAR(200) COMMENT '操作资源',
    `details` JSON COMMENT '操作详情',
    `ip_address` VARCHAR(45) COMMENT 'IP地址',
    `user_agent` TEXT COMMENT '用户代理',
    `status` ENUM('SUCCESS', 'FAILED') NOT NULL COMMENT '操作状态',
    `error_message` TEXT COMMENT '错误信息',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_action` (`action`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) COMMENT '系统日志表';

-- API使用统计表
CREATE TABLE IF NOT EXISTS `api_usage_stats` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `date` DATE NOT NULL COMMENT '统计日期',
    `service_type` ENUM('DOUBAO', 'VOLCENGINE', 'OTHER') NOT NULL COMMENT '服务类型',
    `request_count` INT DEFAULT 0 COMMENT '请求次数',
    `success_count` INT DEFAULT 0 COMMENT '成功次数',
    `total_tokens` BIGINT DEFAULT 0 COMMENT '总Token使用量',
    `total_processing_time` BIGINT DEFAULT 0 COMMENT '总处理时间(毫秒)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_date_service` (`user_id`, `date`, `service_type`),
    INDEX `idx_date` (`date`),
    INDEX `idx_service_type` (`service_type`)
) COMMENT 'API使用统计表';

-- 插入默认管理员用户
INSERT INTO `users` (`username`, `password`, `email`, `role`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@example.com', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 插入测试用户
INSERT INTO `users` (`username`, `password`, `email`, `role`, `status`) VALUES 
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test@example.com', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 创建存储过程：清理过期数据
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS `CleanExpiredData`()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 删除30天前的系统日志
    DELETE FROM `system_logs` WHERE `created_at` < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 删除90天前的识别结果(保留重要数据)
    DELETE FROM `recognition_results` 
    WHERE `created_at` < DATE_SUB(NOW(), INTERVAL 90 DAY) 
    AND `user_id` IN (SELECT `id` FROM `users` WHERE `role` = 'USER');
    
    COMMIT;
END$$
DELIMITER ;

-- 创建视图：用户识别统计
CREATE OR REPLACE VIEW `user_recognition_stats` AS
SELECT 
    u.id AS user_id,
    u.username,
    u.role,
    COUNT(rr.id) AS total_recognitions,
    COUNT(CASE WHEN rr.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) AS recent_recognitions,
    AVG(rr.confidence) AS avg_confidence,
    SUM(rr.token_usage) AS total_tokens,
    AVG(rr.processing_time) AS avg_processing_time,
    MAX(rr.created_at) AS last_recognition_time
FROM `users` u
LEFT JOIN `recognition_results` rr ON u.id = rr.user_id
GROUP BY u.id, u.username, u.role;

-- 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS `idx_recognition_results_composite` ON `recognition_results` (`user_id`, `created_at`, `service_type`);
CREATE INDEX IF NOT EXISTS `idx_image_metadata_composite` ON `image_metadata` (`user_id`, `created_at`);

-- 设置字符集
ALTER DATABASE `image_recognition` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
