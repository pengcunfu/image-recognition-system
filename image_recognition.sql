-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.31 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 image_recognition 的数据库结构
DROP DATABASE IF EXISTS `image_recognition`;
CREATE DATABASE IF NOT EXISTS `image_recognition` /*!40100 DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `image_recognition`;

-- 导出  表 image_recognition.api_usage_stats 结构
DROP TABLE IF EXISTS `api_usage_stats`;
CREATE TABLE IF NOT EXISTS `api_usage_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `date` date NOT NULL COMMENT '统计日期',
  `service_type` enum('DOUBAO','VOLCENGINE','OTHER') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '服务类型',
  `request_count` int DEFAULT '0' COMMENT '请求次数',
  `success_count` int DEFAULT '0' COMMENT '成功次数',
  `total_tokens` bigint DEFAULT '0' COMMENT '总Token使用量',
  `total_processing_time` bigint DEFAULT '0' COMMENT '总处理时间(毫秒)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date_service` (`user_id`,`date`,`service_type`),
  KEY `idx_date` (`date`),
  KEY `idx_service_type` (`service_type`),
  CONSTRAINT `api_usage_stats_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='API使用统计表';

-- 正在导出表  image_recognition.api_usage_stats 的数据：~10 rows (大约)
DELETE FROM `api_usage_stats`;
INSERT INTO `api_usage_stats` (`id`, `user_id`, `date`, `service_type`, `request_count`, `success_count`, `total_tokens`, `total_processing_time`, `created_at`, `updated_at`) VALUES
	(1, 1, '2025-10-13', 'DOUBAO', 15, 14, 12560, 18500, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(2, 1, '2025-10-14', 'DOUBAO', 22, 21, 18900, 27300, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(3, 2, '2025-10-13', 'VOLCENGINE', 8, 8, 7450, 11200, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(4, 2, '2025-10-14', 'DOUBAO', 12, 11, 9870, 14500, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(5, 3, '2025-10-13', 'DOUBAO', 6, 6, 4230, 6800, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(6, 3, '2025-10-14', 'VOLCENGINE', 9, 8, 7120, 10100, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(7, 4, '2025-10-13', 'VOLCENGINE', 11, 10, 9340, 14800, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(8, 4, '2025-10-14', 'DOUBAO', 18, 17, 15600, 23400, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(9, 5, '2025-10-13', 'DOUBAO', 5, 5, 3450, 5200, '2025-10-14 03:08:57', '2025-10-14 03:08:57'),
	(10, 5, '2025-10-14', 'DOUBAO', 7, 7, 5890, 8700, '2025-10-14 03:08:57', '2025-10-14 03:08:57');

-- 导出  表 image_recognition.community_comments 结构
DROP TABLE IF EXISTS `community_comments`;
CREATE TABLE IF NOT EXISTS `community_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `author_id` bigint NOT NULL COMMENT '评论者ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT '状态：PUBLISHED-已发布, HIDDEN-已隐藏, DELETED-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区评论表';

-- 正在导出表  image_recognition.community_comments 的数据：~10 rows (大约)
DELETE FROM `community_comments`;
INSERT INTO `community_comments` (`id`, `post_id`, `author_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 1, 2, '这篇文章写得很好，学到了很多关于猫的知识！', NULL, 5, 'PUBLISHED', '2025-10-13 10:30:00', '2025-10-14 11:07:43', 0),
	(2, 1, 3, '确实，我也发现我家的猫有这些特征。', 1, 3, 'PUBLISHED', '2025-10-13 11:15:00', '2025-10-14 11:07:43', 0),
	(3, 1, 5, '感谢分享，非常实用的信息。', NULL, 8, 'PUBLISHED', '2025-10-13 14:20:00', '2025-10-14 11:07:43', 0),
	(4, 2, 1, '期待更多这样的教程！', NULL, 12, 'PUBLISHED', '2025-10-13 15:45:00', '2025-10-14 11:07:43', 0),
	(5, 2, 4, '已经按照教程试了，识别准确率提高了不少。', NULL, 6, 'PUBLISHED', '2025-10-13 16:30:00', '2025-10-14 11:07:43', 0),
	(6, 3, 2, '新手入门必读！', NULL, 15, 'PUBLISHED', '2025-10-13 17:00:00', '2025-10-14 11:07:43', 0),
	(7, 3, 3, '建议收藏，写得很详细。', NULL, 10, 'PUBLISHED', '2025-10-13 18:10:00', '2025-10-14 11:07:43', 0),
	(8, 4, 5, 'AI识别确实越来越厉害了。', NULL, 7, 'PUBLISHED', '2025-10-14 09:00:00', '2025-10-14 11:07:43', 0),
	(9, 4, 1, '未来可期！', 8, 4, 'PUBLISHED', '2025-10-14 09:30:00', '2025-10-14 11:07:43', 0),
	(10, 5, 2, '如何提高模型的泛化能力呢？', NULL, 9, 'PUBLISHED', '2025-10-14 10:00:00', '2025-10-14 11:07:43', 0);

-- 导出  表 image_recognition.community_posts 结构
DROP TABLE IF EXISTS `community_posts`;
CREATE TABLE IF NOT EXISTS `community_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子内容',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签（JSON格式）',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片URL列表（JSON格式）',
  `view_count` int DEFAULT '0' COMMENT '浏览数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `share_count` int DEFAULT '0' COMMENT '分享数',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `is_featured` tinyint(1) DEFAULT '0' COMMENT '是否精选',
  `status` enum('DRAFT','PENDING','PUBLISHED','REJECTED','HIDDEN','DELETED') COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿, PENDING-待审核, PUBLISHED-已发布, REJECTED-已拒绝, HIDDEN-已隐藏, DELETED-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_top` (`is_top`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';

-- 正在导出表  image_recognition.community_posts 的数据：~5 rows (大约)
DELETE FROM `community_posts`;
INSERT INTO `community_posts` (`id`, `title`, `content`, `author_id`, `category`, `tags`, `images`, `view_count`, `like_count`, `comment_count`, `share_count`, `is_top`, `is_featured`, `status`, `create_time`, `update_time`, `deleted`) VALUES
	(1, '如何提高图像识别的准确率？深度学习技术分享', '这是一篇关于如何提高图像识别准确率的详细教程。首先，我们需要了解图像识别的基本原理...', 1, '技术分享', '["深度学习", "图像识别", "AI"]', NULL, 1250, 89, 23, 0, 1, 1, 'PUBLISHED', '2025-10-14 01:22:51', '2025-10-14 01:22:51', 0),
	(2, 'AI图像识别在医疗领域的应用前景', '探讨AI图像识别技术在医疗诊断中的应用。医疗影像分析是AI技术最有前景的应用领域之一...', 2, '行业应用', '["医疗AI", "图像识别", "应用"]', NULL, 680, 45, 12, 0, 0, 0, 'PENDING', '2025-10-14 01:22:51', '2025-10-14 01:22:51', 0),
	(3, '新手入门：图像识别基础概念解析', '为初学者介绍图像识别的基本概念和原理。本文将从零开始，详细讲解图像识别的核心技术...', 3, '新手教程', '["入门", "基础", "教程"]', NULL, 892, 68, 18, 0, 0, 1, 'PUBLISHED', '2025-10-14 01:22:51', '2025-10-14 12:16:43', 0),
	(4, '违规内容测试帖子', '这是一个包含违规内容的测试帖子...', 4, '其他', '["测试"]', NULL, 12, 0, 0, 0, 0, 0, 'REJECTED', '2025-10-14 01:22:51', '2025-10-14 11:00:12', 0),
	(5, '被隐藏的帖子示例', '这是一个被管理员隐藏的帖子...', 5, '讨论', '["讨论"]', NULL, 163, 3, 1, 0, 1, 0, 'HIDDEN', '2025-10-14 01:22:51', '2025-10-14 11:00:09', 0);

-- 导出  表 image_recognition.db_config 结构
DROP TABLE IF EXISTS `db_config`;
CREATE TABLE IF NOT EXISTS `db_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
  `config_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Configuration key (e.g., spring.mail.username)',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT 'Configuration value',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Configuration description',
  `is_encrypted` tinyint(1) DEFAULT '0' COMMENT 'Whether this configuration is encrypted',
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Configuration category (e.g., mail, volcengine, jwt, doubao)',
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'Whether this configuration is active',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` tinyint(1) DEFAULT '0' COMMENT 'Logical delete flag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_category` (`category`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Database configuration table for storing sensitive parameters';

-- 正在导出表  image_recognition.db_config 的数据：~6 rows (大约)
DELETE FROM `db_config`;
INSERT INTO `db_config` (`id`, `config_key`, `config_value`, `description`, `is_encrypted`, `category`, `is_active`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 'spring.mail.username', 'huaqiwill@qq.com', 'Email username for SMTP', 0, 'mail', 1, '2025-10-15 04:14:01', '2025-10-15 04:24:53', 0),
	(2, 'spring.mail.password', 'anbdmjgjzdnrdced', 'Email password or authorization code', 0, 'mail', 1, '2025-10-15 04:14:01', '2025-10-15 04:28:15', 0),
	(3, 'volcengine.access-key-id', 'YOUR_VOLCENGINE_ACCESS_KEY_ID', 'Volcengine Access Key ID', 0, 'volcengine', 1, '2025-10-15 04:14:01', '2025-10-15 04:33:52', 0),
	(4, 'volcengine.secret-access-key', 'YOUR_VOLCENGINE_SECRET_ACCESS_KEY', 'Volcengine Secret Access Key', 0, 'volcengine', 1, '2025-10-15 04:14:01', '2025-10-15 04:33:52', 0),
	(5, 'jwt.secret', '8ZkP3xQ7vT2bN9mK4sF6jH1dG5fR8aL0cB3eY7uI2oP5qS8tD', 'JWT secret key for token signing', 0, 'jwt', 1, '2025-10-15 04:14:01', '2025-10-15 04:35:35', 0),
	(6, 'doubao.api.key', 'YOUR_DOUBAO_API_KEY', 'Doubao AI API key', 0, 'doubao', 1, '2025-10-15 04:14:01', '2025-10-15 04:33:53', 0);

-- 导出  表 image_recognition.image_metadata 结构
DROP TABLE IF EXISTS `image_metadata`;
CREATE TABLE IF NOT EXISTS `image_metadata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `original_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'MIME类型',
  `width` int DEFAULT NULL COMMENT '图像宽度',
  `height` int DEFAULT NULL COMMENT '图像高度',
  `md5_hash` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT 'MD5哈希值',
  `upload_ip` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '上传IP',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_md5_hash` (`md5_hash`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `image_metadata_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='图像元数据表';

-- 正在导出表  image_recognition.image_metadata 的数据：~10 rows (大约)
DELETE FROM `image_metadata`;
INSERT INTO `image_metadata` (`id`, `user_id`, `original_name`, `file_name`, `file_path`, `file_size`, `mime_type`, `width`, `height`, `md5_hash`, `upload_ip`, `created_at`) VALUES
	(1, 1, 'cat_photo.jpg', 'img_20251013_001.jpg', '/uploads/2025/10/13/img_20251013_001.jpg', 245678, 'image/jpeg', 1920, 1080, 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', '192.168.1.100', '2025-10-13 00:30:00'),
	(2, 2, 'dog_picture.png', 'img_20251013_002.png', '/uploads/2025/10/13/img_20251013_002.png', 198234, 'image/png', 1600, 900, 'b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7', '192.168.1.101', '2025-10-13 01:15:00'),
	(3, 3, 'plant_leaf.jpg', 'img_20251013_003.jpg', '/uploads/2025/10/13/img_20251013_003.jpg', 312456, 'image/jpeg', 2048, 1536, 'c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8', '192.168.1.102', '2025-10-13 02:00:00'),
	(4, 1, 'bird_sky.jpg', 'img_20251013_004.jpg', '/uploads/2025/10/13/img_20251013_004.jpg', 278901, 'image/jpeg', 1920, 1280, 'd4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9', '192.168.1.100', '2025-10-13 03:30:00'),
	(5, 4, 'flower_garden.png', 'img_20251013_005.png', '/uploads/2025/10/13/img_20251013_005.png', 445678, 'image/png', 2560, 1440, 'e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', '192.168.1.103', '2025-10-13 05:00:00'),
	(6, 5, 'insect_macro.jpg', 'img_20251014_001.jpg', '/uploads/2025/10/14/img_20251014_001.jpg', 356789, 'image/jpeg', 3000, 2000, 'f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1', '192.168.1.104', '2025-10-14 00:00:00'),
	(7, 2, 'tree_forest.jpg', 'img_20251014_002.jpg', '/uploads/2025/10/14/img_20251014_002.jpg', 489012, 'image/jpeg', 2400, 1600, 'g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2', '192.168.1.101', '2025-10-14 01:30:00'),
	(8, 3, 'fish_aquarium.png', 'img_20251014_003.png', '/uploads/2025/10/14/img_20251014_003.png', 267890, 'image/png', 1800, 1200, 'h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3', '192.168.1.102', '2025-10-14 02:45:00'),
	(9, 1, 'mountain_view.jpg', 'img_20251014_004.jpg', '/uploads/2025/10/14/img_20251014_004.jpg', 534567, 'image/jpeg', 3840, 2160, 'i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4', '192.168.1.100', '2025-10-14 04:00:00'),
	(10, 4, 'sunset_beach.jpg', 'img_20251014_005.jpg', '/uploads/2025/10/14/img_20251014_005.jpg', 412345, 'image/jpeg', 2880, 1620, 'j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5', '192.168.1.103', '2025-10-14 06:30:00');

-- 导出  表 image_recognition.knowledge_categories 结构
DROP TABLE IF EXISTS `knowledge_categories`;
CREATE TABLE IF NOT EXISTS `knowledge_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '分类名称',
  `key` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '分类键值',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `image` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '分类图片URL',
  `item_count` int DEFAULT '0' COMMENT '条目数量',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `status` enum('ACTIVE','INACTIVE','HIDDEN') COLLATE utf8mb3_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态(1-ACTIVE活跃, 2-INACTIVE不活跃, 3-HIDDEN隐藏)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`),
  KEY `idx_key` (`key`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='知识分类表';

-- 正在导出表  image_recognition.knowledge_categories 的数据：7 rows
DELETE FROM `knowledge_categories`;
/*!40000 ALTER TABLE `knowledge_categories` DISABLE KEYS */;
INSERT INTO `knowledge_categories` (`id`, `name`, `key`, `description`, `image`, `item_count`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`) VALUES
	(1, '动物', 'animal', '各种动物的识别和知识', '/api/v1/files/2025/10/14/1b7238da-de88-4ef6-a8c1-179611b4a883.jpg', 1, 1, 'ACTIVE', '2025-10-13 12:14:22', '2025-10-13 19:42:29', 0),
	(2, '植物', 'plant', '植物、花卉、树木等的识别', '/api/v1/files/2025/10/14/3ebe195e-ef13-4ee6-97c8-adb3b9f6885f.jpg', 1, 2, 'ACTIVE', '2025-10-13 12:14:22', '2025-10-13 19:28:04', 0),
	(3, '食物', 'food', '各种食物、菜品的识别', '/api/v1/files/2025/10/14/c974b06b-8ef4-45ab-9313-4c6855413b0d.jpg', 0, 3, 'ACTIVE', '2025-10-13 12:14:22', '2025-10-13 19:12:12', 0),
	(4, '物品', 'object', '日常物品、工具等的识别', '/api/v1/files/2025/10/14/18b47a33-c267-48c8-98ee-54e943e5a6e7.jpg', 0, 4, 'ACTIVE', '2025-10-13 12:14:22', '2025-10-13 19:12:17', 0),
	(5, '场景', 'scene', '场景、建筑、地标等的识别', '/api/v1/files/2025/10/14/6a2997ec-6fcf-4597-80d5-cef4dc54e56a.jpg', 0, 5, 'ACTIVE', '2025-10-13 12:14:22', '2025-10-13 19:12:22', 0),
	(6, '1212', 'fff', '11212', '', 0, 0, 'INACTIVE', '2025-10-13 13:47:02', '2025-10-13 18:33:06', 1),
	(7, '动物 (副本)', 'animal_copy_1760380177077', '各种动物的识别和知识', NULL, 0, 0, 'ACTIVE', '2025-10-13 18:29:37', '2025-10-13 18:33:03', 1);
/*!40000 ALTER TABLE `knowledge_categories` ENABLE KEYS */;

-- 导出  表 image_recognition.knowledge_items 结构
DROP TABLE IF EXISTS `knowledge_items`;
CREATE TABLE IF NOT EXISTS `knowledge_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识条目ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条目名称',
  `key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条目键值',
  `scientific_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学名',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述信息',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '详细内容（富文本）',
  `images` json DEFAULT NULL COMMENT '图片列表(JSON格式)',
  `characteristics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '特征描述',
  `habitat` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '栖息地/生长环境',
  `lifespan` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生命周期/寿命',
  `related_items` json DEFAULT NULL COMMENT '相关条目ID列表(JSON格式)',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int DEFAULT '0' COMMENT '收藏数',
  `share_count` int DEFAULT '0' COMMENT '分享数',
  `difficulty` tinyint DEFAULT '1' COMMENT '难度等级(1-5)',
  `sort_order` int DEFAULT '0' COMMENT '排序权重',
  `status` enum('DRAFT','PENDING','PUBLISHED','REJECTED','HIDDEN') COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT '状态(DRAFT-草稿, PENDING-待审核, PUBLISHED-已发布, REJECTED-已拒绝, HIDDEN-已隐藏)',
  `author_id` bigint DEFAULT NULL COMMENT '作者用户ID',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核员ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_items_key` (`key`),
  KEY `idx_knowledge_items_category` (`category_id`),
  KEY `idx_knowledge_items_status` (`status`),
  KEY `idx_knowledge_items_author` (`author_id`),
  KEY `idx_knowledge_items_create_time` (`create_time`),
  KEY `idx_knowledge_items_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库条目表';

-- 正在导出表  image_recognition.knowledge_items 的数据：~7 rows (大约)
DELETE FROM `knowledge_items`;
INSERT INTO `knowledge_items` (`id`, `category_id`, `name`, `key`, `scientific_name`, `description`, `content`, `images`, `characteristics`, `habitat`, `lifespan`, `related_items`, `tags`, `view_count`, `like_count`, `favorite_count`, `share_count`, `difficulty`, `sort_order`, `status`, `author_id`, `reviewer_id`, `review_time`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 1, '东北虎', 'northeast_tiger', 'Panthera tigris altaica', '东北虎是现存体型最大的猫科动物，也是世界上最濒危的动物之一。', '1212', '["/api/v1/files/2025/10/14/335422be-528f-4e49-b89c-30272285b489.jpg"]', '体型巨大，毛色橙黄，有黑色条纹，冬季毛发浓密', '中国东北、俄罗斯远东地区的针叶林', '15-20年', '[2, 3]', '猫科,肉食动物,濒危物种', 1251, 89, 156, 23, 4, 100, 'PUBLISHED', 1, NULL, NULL, '2025-10-13 21:44:21', '2025-10-14 11:21:48', 0),
	(2, 1, '华南虎', 'south_china_tiger', 'Panthera tigris amoyensis', '华南虎是中国特有的虎亚种，目前野外可能已经灭绝。', '1212', '["/api/v1/files/2025/10/14/dc32f002-f41c-4321-b2c1-85237279ac94.jpg"]', '体型中等，条纹较窄且间距较大', '中国南方山区森林', '15-18年', '[1]', '猫科,肉食动物,极度濒危', 892, 67, 134, 18, 5, 90, 'PUBLISHED', 1, NULL, NULL, '2025-10-13 21:44:21', '2025-10-14 11:21:51', 0),
	(3, 2, '银杏', 'ginkgo', 'Ginkgo biloba', '银杏是世界上最古老的树种之一，被称为植物界的"活化石"。', '1212', '["/api/v1/files/2025/10/14/2e6a39b2-0622-41f6-9394-53374a2cde7c.jpg"]', '叶片扇形，秋季变黄，雌雄异株', '温带地区，适应性强', '1000年以上', '[4]', '古老植物,落叶乔木,药用植物', 2103, 145, 267, 45, 2, 80, 'PUBLISHED', 1, NULL, NULL, '2025-10-13 21:44:21', '2025-10-14 11:21:54', 0),
	(4, 2, '水杉', 'dawn_redwood', 'Metasequoia glyptostroboides', '水杉是中国特有的孑遗植物，曾被认为已经灭绝。', '1212', '["/api/v1/files/2025/10/14/998a1720-1ed0-43e3-8838-14db4966c6ab.jpg"]', '落叶针叶树，树干通直，生长迅速', '湿润的山谷和河岸', '600年以上', '[3]', '孑遗植物,落叶针叶树,珍稀树种', 1680, 112, 198, 32, 3, 70, 'PUBLISHED', 1, NULL, NULL, '2025-10-13 21:44:21', '2025-10-14 03:42:59', 0),
	(5, 3, '中华鲟', 'chinese_sturgeon', 'Acipenser sinensis', '中华鲟是中国特有的古老鱼类，被誉为"水中大熊猫"。', '1212', '["/api/v1/files/2025/10/14/269a20c4-7025-4730-a99f-a8a5bb4d2573.jpg"]', '体型巨大，无鳞片，有骨板，寿命极长', '长江流域，海河洄游', '40年以上', '[]', '古老鱼类,洄游鱼类,国家保护动物', 1456, 98, 187, 28, 4, 85, 'PUBLISHED', 1, NULL, NULL, '2025-10-13 21:44:21', '2025-10-14 03:43:06', 0),
	(6, 2, '12', '12_1760383683992', NULL, '12', '12', '["/api/v1/files/2025/10/14/b1897ed5-95f6-4da4-b964-ef9756a1f2f3.jpg"]', NULL, NULL, NULL, NULL, '12', 3, 0, 0, 0, 1, 0, 'PUBLISHED', 1, NULL, NULL, '2025-10-14 03:28:04', '2025-10-14 11:19:47', 0),
	(7, 1, '华南虎（副本）', '华南虎（副本）_1760384549497', NULL, '华南虎是中国特有的虎亚种，目前野外可能已经灭绝。', '1212', '["/api/v1/files/2025/10/14/dc32f002-f41c-4321-b2c1-85237279ac94.jpg"]', NULL, NULL, NULL, NULL, '猫科,肉食动物,极度濒危', 0, 0, 0, 0, 5, 0, 'DRAFT', 1, NULL, NULL, '2025-10-14 03:42:29', '2025-10-14 03:42:29', 0);

-- 导出  表 image_recognition.recognition_items 结构
DROP TABLE IF EXISTS `recognition_items`;
CREATE TABLE IF NOT EXISTS `recognition_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '批次ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `image_id` bigint NOT NULL COMMENT '图像ID',
  `result_id` bigint DEFAULT NULL COMMENT '识别结果ID',
  `status` enum('PENDING','PROCESSING','COMPLETED','FAILED') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT 'PENDING' COMMENT '处理状态',
  `error_message` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '错误信息',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `image_id` (`image_id`),
  KEY `result_id` (`result_id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `recognition_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recognition_items_ibfk_2` FOREIGN KEY (`image_id`) REFERENCES `image_metadata` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recognition_items_ibfk_3` FOREIGN KEY (`result_id`) REFERENCES `recognition_results` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='识别项目表';

-- 正在导出表  image_recognition.recognition_items 的数据：~10 rows (大约)
DELETE FROM `recognition_items`;
INSERT INTO `recognition_items` (`id`, `batch_id`, `user_id`, `image_id`, `result_id`, `status`, `error_message`, `created_at`, `updated_at`) VALUES
	(1, 'BATCH-20251013-001', 1, 1, 1, 'COMPLETED', NULL, '2025-10-13 00:30:00', '2025-10-14 03:09:40'),
	(2, 'BATCH-20251013-001', 1, 4, 4, 'COMPLETED', NULL, '2025-10-13 00:31:00', '2025-10-14 03:09:40'),
	(3, 'BATCH-20251013-001', 1, 9, 9, 'COMPLETED', NULL, '2025-10-13 00:32:00', '2025-10-14 03:09:40'),
	(4, 'BATCH-20251013-002', 2, 2, 2, 'COMPLETED', NULL, '2025-10-13 01:15:00', '2025-10-14 03:09:40'),
	(5, 'BATCH-20251013-002', 2, 7, 7, 'COMPLETED', NULL, '2025-10-13 01:16:00', '2025-10-14 03:09:40'),
	(6, 'BATCH-20251013-003', 3, 3, 3, 'COMPLETED', NULL, '2025-10-13 02:00:00', '2025-10-14 03:09:40'),
	(7, 'BATCH-20251013-003', 3, 8, 8, 'COMPLETED', NULL, '2025-10-13 02:01:00', '2025-10-14 03:09:40'),
	(8, 'BATCH-20251013-004', 4, 5, 5, 'COMPLETED', NULL, '2025-10-13 05:00:00', '2025-10-14 03:09:40'),
	(9, 'BATCH-20251013-004', 4, 10, 10, 'COMPLETED', NULL, '2025-10-13 05:01:00', '2025-10-14 03:09:40'),
	(10, 'BATCH-20251014-001', 5, 6, 6, 'COMPLETED', NULL, '2025-10-14 00:00:00', '2025-10-14 03:09:40');

-- 导出  表 image_recognition.recognition_results 结构
DROP TABLE IF EXISTS `recognition_results`;
CREATE TABLE IF NOT EXISTS `recognition_results` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `image_id` bigint NOT NULL COMMENT '图像ID',
  `service_type` enum('DOUBAO','VOLCENGINE','OTHER') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '识别服务类型',
  `category` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '物体类别',
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '物体名称',
  `color` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '主要颜色',
  `shape` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '形状描述',
  `material` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '材质描述',
  `attributes` json DEFAULT NULL COMMENT '属性列表',
  `confidence` decimal(3,2) DEFAULT NULL COMMENT '置信度(0-1)',
  `raw_response` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '原始AI响应',
  `processing_time` int DEFAULT NULL COMMENT '处理时间(毫秒)',
  `token_usage` int DEFAULT NULL COMMENT 'Token使用量',
  `prompt_used` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '使用的提示词',
  `model_version` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '模型版本',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '识别时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_image_id` (`image_id`),
  KEY `idx_service_type` (`service_type`),
  KEY `idx_category` (`category`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `recognition_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recognition_results_ibfk_2` FOREIGN KEY (`image_id`) REFERENCES `image_metadata` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='识别结果表';

-- 正在导出表  image_recognition.recognition_results 的数据：~10 rows (大约)
DELETE FROM `recognition_results`;
INSERT INTO `recognition_results` (`id`, `user_id`, `image_id`, `service_type`, `category`, `name`, `color`, `shape`, `material`, `attributes`, `confidence`, `raw_response`, `processing_time`, `token_usage`, `prompt_used`, `model_version`, `created_at`) VALUES
	(1, 1, 1, 'DOUBAO', '动物', '家猫', '橙白相间', '蹲坐姿态，耳朵竖立', '毛发', '{"age": "成年", "size": "中等", "breed": "橘猫"}', 0.95, '这是一只橘猫，毛色橙白相间，处于蹲坐姿态...', 1250, 856, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-13 00:35:00'),
	(2, 2, 2, 'VOLCENGINE', '动物', '金毛犬', '金黄色', '站立姿态，尾巴上扬', '毛发', '{"age": "幼年", "size": "大型", "breed": "金毛寻回犬"}', 0.92, '这是一只金毛犬，处于站立姿态...', 1450, 923, '请识别图片中的物体', 'volcengine-v2.0', '2025-10-13 01:20:00'),
	(3, 3, 3, 'DOUBAO', '植物', '绿萝叶片', '深绿色', '心形叶片', '植物组织', '{"type": "观叶植物", "光泽": "有光泽", "condition": "健康"}', 0.89, '这是绿萝植物的叶片，呈心形...', 980, 645, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-13 02:05:00'),
	(4, 1, 4, 'DOUBAO', '动物', '蓝山雀', '蓝色和黄色', '飞行姿态，翅膀展开', '羽毛', '{"habitat": "树林", "species": "雀形目", "activity": "飞行中"}', 0.87, '这是一只蓝山雀，正在飞行...', 1100, 778, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-13 03:35:00'),
	(5, 4, 5, 'VOLCENGINE', '植物', '月季花', '粉红色', '花瓣层叠，花朵盛开', '花瓣', '{"petals": "多层", "fragrance": "芳香", "bloom_status": "盛开"}', 0.93, '这是一朵盛开的月季花...', 1320, 891, '请识别图片中的物体', 'volcengine-v2.0', '2025-10-13 05:05:00'),
	(6, 5, 6, 'DOUBAO', '动物', '瓢虫', '红色带黑斑', '半球形外壳', '甲壳', '{"size": "小型", "type": "七星瓢虫", "spots": 7}', 0.91, '这是一只七星瓢虫...', 890, 567, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-14 00:05:00'),
	(7, 2, 7, 'DOUBAO', '植物', '松树', '深绿色', '针叶树冠，树干挺直', '木质', '{"age": "成熟", "type": "针叶树", "height": "高大"}', 0.88, '这是一棵松树，树冠茂密...', 1180, 812, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-14 01:35:00'),
	(8, 3, 8, 'VOLCENGINE', '动物', '金鱼', '橙红色', '椭圆形身体，尾鳍飘逸', '鳞片', '{"fins": "长尾", "type": "观赏鱼", "environment": "水族箱"}', 0.94, '这是一条金鱼，颜色鲜艳...', 1050, 734, '请识别图片中的物体', 'volcengine-v2.0', '2025-10-14 02:50:00'),
	(9, 1, 9, 'DOUBAO', '自然景观', '雪山', '白色和灰色', '山峰连绵，积雪覆盖', '岩石和冰雪', '{"season": "冬季", "weather": "晴朗", "elevation": "高海拔"}', 0.90, '这是一座雪山，山峰被积雪覆盖...', 1400, 945, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-14 04:05:00'),
	(10, 4, 10, 'DOUBAO', '自然景观', '日落海滩', '橙红渐变', '地平线分割，太阳半隐', '沙滩和海水', '{"tide": "退潮", "time": "傍晚", "weather": "晴朗"}', 0.86, '这是日落时分的海滩景色...', 1280, 867, '请识别图片中的物体', 'doubao-vision-v1', '2025-10-14 06:35:00');

-- 导出  表 image_recognition.system_logs 结构
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE IF NOT EXISTS `system_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `action` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '操作类型',
  `resource` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '操作资源',
  `details` json DEFAULT NULL COMMENT '操作详情',
  `ip_address` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '用户代理',
  `status` enum('SUCCESS','FAILED') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '操作状态',
  `error_message` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '错误信息',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_action` (`action`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `system_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='系统日志表';

-- 正在导出表  image_recognition.system_logs 的数据：~12 rows (大约)
DELETE FROM `system_logs`;
INSERT INTO `system_logs` (`id`, `user_id`, `action`, `resource`, `details`, `ip_address`, `user_agent`, `status`, `error_message`, `created_at`) VALUES
	(1, 1, 'USER_LOGIN', 'auth/login', '{"method": "password", "username": "admin"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 00:00:00'),
	(2, 2, 'USER_LOGIN', 'auth/login', '{"method": "password", "username": "user1"}', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 00:30:00'),
	(3, 1, 'IMAGE_UPLOAD', 'files/upload', '{"size": 245678, "filename": "cat_photo.jpg"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 00:30:00'),
	(4, 1, 'IMAGE_RECOGNITION', 'recognition/analyze', '{"service": "DOUBAO", "image_id": 1}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 00:35:00'),
	(5, 2, 'IMAGE_UPLOAD', 'files/upload', '{"size": 198234, "filename": "dog_picture.png"}', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 01:15:00'),
	(6, 3, 'USER_LOGIN', 'auth/login', '{"method": "password", "username": "vip"}', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 01:45:00'),
	(7, NULL, 'USER_REGISTER', 'auth/register', '{"email": "new@example.com", "username": "newuser"}', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'FAILED', '用户名已存在', '2025-10-13 02:30:00'),
	(8, 4, 'POST_CREATE', 'community/posts', '{"title": "如何提高识别准确率", "post_id": 5}', '192.168.1.103', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-13 03:00:00'),
	(9, 1, 'USER_UPDATE', 'user/profile', '{"field": "avatar", "value": "/api/v1/files/default-avatar.png"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-14 01:00:00'),
	(10, 5, 'COMMENT_CREATE', 'community/comments', '{"post_id": 4, "comment_id": 8}', '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-14 01:00:00'),
	(11, 1, 'KNOWLEDGE_CREATE', 'knowledge/items', '{"name": "橘猫", "category_id": 1}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-14 02:00:00'),
	(12, 2, 'VIP_UPGRADE', 'user/vip', '{"level": 1, "expire": "2026-10-14", "user_id": 2}', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'SUCCESS', NULL, '2025-10-14 03:30:00');

-- 导出  表 image_recognition.users 结构
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '密码(加密)',
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` enum('USER','VIP','ADMIN') COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER=普通用户，VIP=VIP用户，ADMIN=管理员',
  `status` enum('ACTIVE','INACTIVE','BANNED','DELETED') COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE=激活，INACTIVE=未激活，BANNED=封禁，DELETED=已删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `bio` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `vip_level` int DEFAULT NULL,
  `vip_expire_time` datetime DEFAULT NULL COMMENT 'VIP过期时间',
  `last_login_time` timestamp NULL DEFAULT NULL,
  `deleted` enum('NOT_DELETED','DELETED') COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'NOT_DELETED' COMMENT '删除状态：NOT_DELETED=未删除，DELETED=已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='用户表';

-- 正在导出表  image_recognition.users 的数据：~6 rows (大约)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `avatar`, `role`, `status`, `create_time`, `update_time`, `name`, `bio`, `vip_level`, `vip_expire_time`, `last_login_time`, `deleted`) VALUES
	(1, 'admin', '123456', 'admin@example.com', '18574824160', '/api/v1/files/2025/10/14/5cf71696-5a2d-4d8e-ae07-cf63cae5d9dd.jpg', 'ADMIN', 'ACTIVE', '2025-09-30 00:15:23', '2025-10-14 02:41:37', '管理员', NULL, 0, NULL, '2025-10-15 03:06:50', 'NOT_DELETED'),
	(2, 'user', '123456', 'test@example.com', '15567678989', '/api/v1/files/2025/10/14/2c6aaa6e-9618-46f7-bc87-7903bdfb162d.jpg', 'USER', 'ACTIVE', '2025-09-30 00:15:23', '2025-10-14 02:43:07', '用户', NULL, 0, NULL, '2025-10-14 21:39:17', 'NOT_DELETED'),
	(3, 'vip', '123456', '31734840209@qq.com', NULL, '/api/v1/files/2025/10/14/2c6aaa6e-9618-46f7-bc87-7903bdfb162d.jpg', 'VIP', 'ACTIVE', '2025-10-12 11:19:22', '2025-10-14 02:49:10', 'newuser2025', NULL, 1, '2026-12-31 23:59:59', '2025-10-14 21:40:43', 'NOT_DELETED'),
	(4, 'admin123', '123456', '3173484026@qq.com', '15576364885', '/api/v1/files/2025/10/14/57a3439f-196a-4741-9209-432cf1fcb7a6.jpg', 'USER', 'ACTIVE', '2025-10-13 05:37:00', '2025-10-14 02:38:22', 'admin123', NULL, 0, NULL, '2025-10-13 05:37:00', 'NOT_DELETED'),
	(5, 'admin11', '123456', 'qqq111@qq.com', '15576364885', '/api/v1/files/2025/10/14/3c0e6500-0b8e-45f3-8507-26dc433a17d8.jpg', 'USER', 'ACTIVE', '2025-10-14 01:36:30', '2025-10-14 02:42:45', '彭存福', NULL, 1, NULL, NULL, 'NOT_DELETED'),
	(6, 'admin115', '123456', '31734840264@qq.com', '18574824160', '/api/v1/files/2025/10/14/75eff25f-2f55-4ca8-809c-ebf678fce4b3.jpg', 'USER', 'ACTIVE', '2025-10-14 02:20:13', '2025-10-14 02:29:39', '彭存福', NULL, 1, NULL, NULL, 'DELETED');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
