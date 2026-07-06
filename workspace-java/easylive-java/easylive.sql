/*
 Navicat Premium Data Transfer

 Source Server         : easylive
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3310
 Source Schema         : easylive

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 06/05/2026 18:01:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for play_history
-- ----------------------------
DROP TABLE IF EXISTS `play_history`;
CREATE TABLE `play_history`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `play_time` datetime NOT NULL COMMENT '播放时间',
  `progress` int NULL DEFAULT 0 COMMENT '播放进度(秒)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '播放历史' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of play_history
-- ----------------------------

-- ----------------------------
-- Table structure for search_keyword
-- ----------------------------
DROP TABLE IF EXISTS `search_keyword`;
CREATE TABLE `search_keyword`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关键词',
  `search_count` int NULL DEFAULT 0 COMMENT '搜索次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_keyword`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '搜索关键词' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of search_keyword
-- ----------------------------
INSERT INTO `search_keyword` VALUES (1, '2024', 10, '2026-04-08 16:00:57');
INSERT INTO `search_keyword` VALUES (2, '视频剪辑', 1, '2026-04-08 16:09:30');
INSERT INTO `search_keyword` VALUES (3, '恶意', 2, '2026-04-26 17:31:25');

-- ----------------------------
-- Table structure for tb_audit_config
-- ----------------------------
DROP TABLE IF EXISTS `tb_audit_config`;
CREATE TABLE `tb_audit_config`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_audit_config
-- ----------------------------
INSERT INTO `tb_audit_config` VALUES (1, 'ai_provider', 'openai', 'AI服务提供商: openai/baidu/ali/tongyi', '2026-03-26 03:14:56', '2026-03-26 03:14:56');
INSERT INTO `tb_audit_config` VALUES (2, 'ai_api_key', '', 'API Key', '2026-03-26 03:14:56', '2026-03-26 03:14:56');
INSERT INTO `tb_audit_config` VALUES (3, 'ai_model', 'gpt-4o', '使用的模型', '2026-03-26 03:14:56', '2026-03-26 03:14:56');
INSERT INTO `tb_audit_config` VALUES (4, 'ai_auto_publish', 'true', 'AI审核通过后是否自动发布', '2026-03-26 03:14:56', '2026-03-26 03:14:56');

-- ----------------------------
-- Table structure for tb_category_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_category_info`;
CREATE TABLE `tb_category_info`  (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '自增分类ID',
  `category_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码',
  `category_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `p_category_id` int NOT NULL COMMENT '父级分类ID',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `background` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '背景图',
  `sort` tinyint NOT NULL COMMENT '排序号',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `idx_key_category_code`(`category_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_category_info
-- ----------------------------
INSERT INTO `tb_category_info` VALUES (32, 'java', 'JAVA', 0, 'java-icon', 'java-bg', 1);
INSERT INTO `tb_category_info` VALUES (33, 'girl', '女生', 0, 'girl-icon', 'girl-bg', 1);
INSERT INTO `tb_category_info` VALUES (34, 'tech', '科技', 0, 'tech-icon', 'tech-bg', 1);
INSERT INTO `tb_category_info` VALUES (35, 'life', '生活', 0, 'life-icon', 'life-bg', 2);
INSERT INTO `tb_category_info` VALUES (36, 'game', '游戏', 0, 'game-icon', 'game-bg', 3);
INSERT INTO `tb_category_info` VALUES (37, 'fashion', '时尚', 33, 'fashion-icon', 'fashion-bg', 1);
INSERT INTO `tb_category_info` VALUES (38, 'beauty', '美妆', 33, 'beauty-icon', 'beauty-bg', 2);
INSERT INTO `tb_category_info` VALUES (39, 'style', '穿搭', 33, 'style-icon', 'style-bg', 3);
INSERT INTO `tb_category_info` VALUES (40, 'trend', '潮流', 33, 'trend-icon', 'trend-bg', 4);
INSERT INTO `tb_category_info` VALUES (41, 'mobile', '手机', 34, 'mobile-icon', 'mobile-bg', 1);
INSERT INTO `tb_category_info` VALUES (42, 'computer', '电脑', 34, 'computer-icon', 'computer-bg', 2);
INSERT INTO `tb_category_info` VALUES (43, 'digital', '数码', 34, 'digital-icon', 'digital-bg', 3);
INSERT INTO `tb_category_info` VALUES (44, 'ai', '人工智能', 34, 'ai-icon', 'ai-bg', 4);
INSERT INTO `tb_category_info` VALUES (45, 'internet', '互联网', 34, 'internet-icon', 'internet-bg', 5);
INSERT INTO `tb_category_info` VALUES (46, 'food', '美食', 35, 'food-icon', 'food-bg', 1);
INSERT INTO `tb_category_info` VALUES (47, 'travel', '旅行', 35, 'travel-icon', 'travel-bg', 2);
INSERT INTO `tb_category_info` VALUES (48, 'health', '健康', 35, 'health-icon', 'health-bg', 3);
INSERT INTO `tb_category_info` VALUES (49, 'home', '家居', 35, 'home-icon', 'home-bg', 4);
INSERT INTO `tb_category_info` VALUES (50, 'fitness', '运动健身', 35, 'fitness-icon', 'fitness-bg', 5);
INSERT INTO `tb_category_info` VALUES (51, 'mobile_game', '手游', 36, 'mobile-game-icon', 'mobile-game-bg', 1);
INSERT INTO `tb_category_info` VALUES (52, 'pc_game', '端游', 36, 'pc-game-icon', 'pc-game-bg', 2);
INSERT INTO `tb_category_info` VALUES (54, 'esports', '电竞', 36, 'esports-icon', 'esports-bg', 4);

-- ----------------------------
-- Table structure for tb_video_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_video_info`;
CREATE TABLE `tb_video_info`  (
  `video_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `video_cover` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频封面',
  `video_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频名称',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '最后更新时间',
  `p_category_id` int NOT NULL COMMENT '父级分类ID',
  `category_id` int NULL DEFAULT NULL COMMENT '分类ID',
  `post_type` tinyint NOT NULL COMMENT '0:自制 1:转载',
  `origin_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原资源说明',
  `tags` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `introduction` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `interaction` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '互动设置',
  `duration` int NULL DEFAULT NULL COMMENT '持续时间（秒）',
  `play_count` int NULL DEFAULT 0 COMMENT '播放数量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数量',
  `danku_count` int NULL DEFAULT 0 COMMENT '弹幕数量',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数量',
  `coin_count` int NULL DEFAULT 0 COMMENT '投币数量',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数量',
  `recommend_type` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐0:未推荐 1:已推荐',
  `last_play_time` datetime NULL DEFAULT NULL COMMENT '最后播放时间',
  PRIMARY KEY (`video_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_p_category_id`(`p_category_id` ASC) USING BTREE,
  INDEX `idx_recommend_type`(`recommend_type` ASC) USING BTREE,
  INDEX `idx_last_play_time`(`last_play_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_video_info
-- ----------------------------
INSERT INTO `tb_video_info` VALUES ('0cnMuWl8VB', 'cover/20260417/xMTab1PlJmnJpgscpWe08paBx9oPaf.png', '切切切切', 's2uZDmW9PZ', '2026-04-17 15:57:44', '2026-04-17 15:57:45', 42, NULL, 0, NULL, '999', NULL, NULL, 2, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('4j9ZbSAPht', 'cover/20250910/W2XF62EI62tr0v3igQ08XjB2yrQl3t.png', '最美幼儿园', 'TAhBXO5QVk', '2025-09-10 21:13:32', '2025-09-10 21:13:32', 33, NULL, 0, NULL, '校园', '最美幼儿园', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('510xOjscJf', 'cover/20250911/b0kNxZ8ucnuq0XLyqq9UMZLIBA7EqX.png', '视频剪辑', 'TAhBXO5QVk', '2025-09-11 16:51:38', '2025-09-11 16:51:38', 33, NULL, 0, NULL, '剪辑', '快来跟我学剪辑', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('6hAU1YBNoy', 'cover/20250906/95cQz5DMJZpUsBeELIyqqoG7wl71zo.png', '你喜欢的贺卡是这样的吗', 'TAhBXO5QVk', '2025-09-06 16:14:19', '2025-09-06 16:14:19', 33, NULL, 0, NULL, '最美贺卡', '最美贺卡', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('9497RtR2e5', 'cover/20250906/E75pqV0iML9oKPKzEFZFIbxg1rn7iI.png', '感人电影 触不可及', 'TAhBXO5QVk', '2025-09-06 16:02:41', '2025-09-06 16:02:41', 33, NULL, 0, '無', '电影', '好看到哭啊', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('dogam72Tl0', 'cover/20260417/UWvLbMWaKyNyFY3J1Ixt8YpUz2gcwY.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-17 16:19:41', '2026-04-17 16:19:43', 33, NULL, 0, NULL, '3334', NULL, NULL, 2, 1, 0, 0, 0, 0, 0, 1, '2026-05-05 13:19:04');
INSERT INTO `tb_video_info` VALUES ('F1HCboCIht', 'cover/20260417/2UPVTE4Kj1AmyM9d7LJfKtwlqhXtms.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:13:28', '2026-04-17 16:13:35', 33, NULL, 0, NULL, '000', NULL, NULL, 365, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('hu4T3wmqKD', 'cover/20260408/54EHJRkIlZKAqQ0Y2tNOyx2YvIhXs7.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 19:34:22', '2026-04-08 19:34:24', 34, NULL, 0, NULL, '123', NULL, '', 2, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('IJG4zyVGW1', 'cover/20260408/vOpcsQo6mnK0JqOSAIDfpATMtVsI9X.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 10:45:37', '2026-04-08 10:45:38', 33, NULL, 0, NULL, '11', NULL, NULL, 2, 1, 1, 0, 0, 1, 1, 1, '2026-05-06 08:40:20');
INSERT INTO `tb_video_info` VALUES ('LpDsvuoDl1', 'cover/20260417/qLZCWoAyzQGxCP5B1TGkMRXQEmEeQd.png', '恶意', 's2uZDmW9PZ', '2026-04-17 16:39:16', '2026-04-17 16:39:21', 33, NULL, 0, NULL, '34565', NULL, NULL, 7, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('mK2b6AiVaT', 'cover/20260408/FBvIrv8tUXljQQLXGIWWqtsyTFy087.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 19:38:14', '2026-04-08 19:38:15', 33, NULL, 0, NULL, '123', NULL, NULL, 2, 3, 0, 0, 0, 0, 0, 1, '2026-05-05 13:26:10');
INSERT INTO `tb_video_info` VALUES ('mkdXQoC5Dg', 'cover/20260417/hyk1vpI5AK6AlqLmtksquj43XV5Uro.png', 'testtest', 's2uZDmW9PZ', '2026-04-17 15:52:33', '2026-04-17 15:52:34', 33, NULL, 0, NULL, '888', NULL, NULL, 2, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('OmeUKuJq2T', 'cover/20250906/l0aOXprKTslCCOwwLoQ3raDBxMhHvc.png', '唱歌', 'TAhBXO5QVk', '2025-09-06 14:36:46', '2025-09-06 14:36:46', 33, NULL, 0, '呵呵哈哈哈', '無', '嘿嘿', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('rU7Dwfq8bx', 'cover/20260422/QEXpm9hBmwJSIvH2qIY2nO1H7P4Zsg.png', '恶意3', 's2uZDmW9PZ', '2026-04-22 20:11:57', '2026-04-22 20:12:24', 33, NULL, 0, NULL, '56', NULL, NULL, 7, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('ufogfZiHIX', 'cover/20260417/LeSouCkExAWRoRapyiaclOVt0kqQnf.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:09:08', '2026-04-17 16:09:09', 33, NULL, 0, NULL, '444', NULL, NULL, 365, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('vEQ3UBebjW', 'cover/20260422/BuOAh5GKB3jAGiqiUAP2Rk9Im11tCR.png', '恶意', 's2uZDmW9PZ', '2026-04-22 19:48:56', '2026-04-22 19:49:01', 33, NULL, 0, NULL, '1445', NULL, NULL, 7, 10, 1, 0, 0, 0, 0, 1, '2026-05-05 12:27:54');
INSERT INTO `tb_video_info` VALUES ('WAtOvrX1O4', 'cover/20260417/mvQrHxHKWxkmaZp2ehtrMv1Ulnwyzt.png', 'test', 's2uZDmW9PZ', '2026-04-17 15:42:46', '2026-04-17 15:42:48', 33, NULL, 0, NULL, '666', NULL, '', 127, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('WVEBRsIJph', 'cover/20260417/DMfYLw0iAWqyWGoULvbG9nZef874uT.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:20:58', '2026-04-17 16:21:23', 34, NULL, 0, NULL, '456', NULL, NULL, 365, 0, 0, 0, 0, 0, 0, 1, NULL);
INSERT INTO `tb_video_info` VALUES ('y3bXaLrWe3', 'cover/20250910/lnNVJ7OXQZcMr1mFfaQgyPYnZ4jsEe.png', '最新款手表你值得拥有', 'TAhBXO5QVk', '2025-09-10 21:40:37', '2025-09-10 21:40:37', 33, NULL, 0, NULL, '首饰,手表', '高级手表', NULL, NULL, 0, 0, 0, 0, 0, 0, 1, NULL);

-- ----------------------------
-- Table structure for tb_video_info_file
-- ----------------------------
DROP TABLE IF EXISTS `tb_video_info_file`;
CREATE TABLE `tb_video_info_file`  (
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_index` int NOT NULL COMMENT '文件索引',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件路径',
  `duration` int NULL DEFAULT NULL COMMENT '持续时间（秒）',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频文件信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_video_info_file
-- ----------------------------
INSERT INTO `tb_video_info_file` VALUES ('3oB4shLkVP7aL5uPeZwp', 's2uZDmW9PZ', 'F1HCboCIht', 'kali攻击', 1, 21811721, 'video/20260417/s2uZDmW9PZmvE2im67RWC3DON', 365);
INSERT INTO `tb_video_info_file` VALUES ('5j3w0T2o0BUWQ9kspFYB', 's2uZDmW9PZ', 'WAtOvrX1O4', '2025-09-11 20-40-28', 1, 7731546, 'video/20260417/s2uZDmW9PZvchvawJbnGOO5xl', 127);
INSERT INTO `tb_video_info_file` VALUES ('bYvCrJ2QXim3GpDPws7Q', 's2uZDmW9PZ', 'ufogfZiHIX', 'kali攻击', 1, 21811721, 'video/20260417/s2uZDmW9PZeqhjCSA4UlfZtbH', 365);
INSERT INTO `tb_video_info_file` VALUES ('Eavlq7Y7X3OvbVvSgr8O', 's2uZDmW9PZ', 'IJG4zyVGW1', 'Video_2024-12-27_121133', 1, 421771, 'video/20260408/s2uZDmW9PZQEr6siyy6RdD5UT', 2);
INSERT INTO `tb_video_info_file` VALUES ('gB1N2q2xgyfml5wVcp3i', 's2uZDmW9PZ', 'mkdXQoC5Dg', 'Video_2024-12-27_121133', 1, 421771, 'video/20260417/s2uZDmW9PZWIyt3GAspT27A2i', 2);
INSERT INTO `tb_video_info_file` VALUES ('gWorVxgIhJkGp7HklTW6', 's2uZDmW9PZ', 'rU7Dwfq8bx', '恶意', 1, 443902, 'video/20260422/s2uZDmW9PZXmGteAmD7MLtVcj', 7);
INSERT INTO `tb_video_info_file` VALUES ('huU5Fo1nCoy0f3RQGoIe', 'TAhBXO5QVk', '510xOjscJf', '粒子1', 1, 3860594, 'video/20250911/TAhBXO5QVkVCjcs5aP3ET3eL4', 6);
INSERT INTO `tb_video_info_file` VALUES ('Jxo1h8bfHxFyEdDpEfzJ', 's2uZDmW9PZ', 'hu4T3wmqKD', 'Video_2024-12-27_121133', 1, 421771, 'video/20260408/s2uZDmW9PZLYcZkuc3dPfcQEs', 2);
INSERT INTO `tb_video_info_file` VALUES ('l8st7sedlSNoKtbcSOty', 's2uZDmW9PZ', 'dogam72Tl0', 'Video_2024-12-27_121133', 1, 421771, 'video/20260417/s2uZDmW9PZoRAmw4Q2BpOjIZ7', 2);
INSERT INTO `tb_video_info_file` VALUES ('LqeLSO7SKLR2UV6ODlew', 's2uZDmW9PZ', 'WVEBRsIJph', 'kali攻击', 1, 21811721, 'video/20260417/s2uZDmW9PZhas84QXM0iOs1nC', 365);
INSERT INTO `tb_video_info_file` VALUES ('nIm8aqgkqGz7hA4PGGGD', 's2uZDmW9PZ', 'mK2b6AiVaT', 'Video_2024-12-27_121133', 1, 421771, 'video/20260408/s2uZDmW9PZTSnWdb3xa4ttu95', 2);
INSERT INTO `tb_video_info_file` VALUES ('qeAl32lcf7nlv884dD1n', 'TAhBXO5QVk', 'y3bXaLrWe3', '手表素材', 1, 51362086, 'video/20250910/TAhBXO5QVk9bW1jwV2xCFhIJ8', 30);
INSERT INTO `tb_video_info_file` VALUES ('toGgb9us2AKoPmV1k7UC', 's2uZDmW9PZ', '0cnMuWl8VB', 'Video_2024-12-27_121133', 1, 421771, 'video/20260417/s2uZDmW9PZDY9JvrUqGpH0CCG', 2);
INSERT INTO `tb_video_info_file` VALUES ('uaFrQQYmiSRDDP6yC0GA', 's2uZDmW9PZ', 'vEQ3UBebjW', '恶意', 1, 443902, 'video/20260422/s2uZDmW9PZFf1UEbY2Sdhdkbc', 7);
INSERT INTO `tb_video_info_file` VALUES ('yEYlJQY2Q42eSijOiKzk', 'TAhBXO5QVk', '4j9ZbSAPht', '试题ZM', 1, 69205108, 'video/20250910/TAhBXO5QVkmMWCCW1RjUC8Ewo', 54);
INSERT INTO `tb_video_info_file` VALUES ('YnEIcGfu1RCh0wMzBDBU', 's2uZDmW9PZ', 'LpDsvuoDl1', '恶意', 1, 443902, 'video/20260417/s2uZDmW9PZOMD9WfrVDTiB85p', 7);
INSERT INTO `tb_video_info_file` VALUES ('ZZXu20onj2p6C1p', 'TAhBXO5QVk', '510xOjscJf', '权志龙', 2, 5169097, 'video/20250911/TAhBXO5QVkZZXu20onj2p6C1p', 4);

-- ----------------------------
-- Table structure for tb_video_info_file_post
-- ----------------------------
DROP TABLE IF EXISTS `tb_video_info_file_post`;
CREATE TABLE `tb_video_info_file_post`  (
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一ID',
  `upload_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `file_index` int NOT NULL COMMENT '文件索引',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件路径',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `transfer_result` tinyint NULL DEFAULT NULL COMMENT '0:无更新 1:有更新',
  `duration` int NULL DEFAULT NULL COMMENT '持续时间（秒）',
  `post_type` tinyint NULL DEFAULT NULL COMMENT '0:转码中 1:转码成功 2:转码失败',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `idx_key_upload_id`(`upload_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频文件信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_video_info_file_post
-- ----------------------------
INSERT INTO `tb_video_info_file_post` VALUES ('3oB4shLkVP7aL5uPeZwp', 'mvE2im67RWC3DON', 's2uZDmW9PZ', 'F1HCboCIht', 1, 'kali攻击', 21811721, 'video/20260417/s2uZDmW9PZmvE2im67RWC3DON', NULL, 1, 365, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('5j3w0T2o0BUWQ9kspFYB', 'vchvawJbnGOO5xl', 's2uZDmW9PZ', 'WAtOvrX1O4', 1, '2025-09-11 20-40-28', 7731546, 'video/20260417/s2uZDmW9PZvchvawJbnGOO5xl', NULL, 1, 127, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('bYvCrJ2QXim3GpDPws7Q', 'eqhjCSA4UlfZtbH', 's2uZDmW9PZ', 'ufogfZiHIX', 1, 'kali攻击', 21811721, 'video/20260417/s2uZDmW9PZeqhjCSA4UlfZtbH', NULL, 1, 365, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('Eavlq7Y7X3OvbVvSgr8O', 'QEr6siyy6RdD5UT', 's2uZDmW9PZ', 'IJG4zyVGW1', 1, 'Video_2024-12-27_121133', 421771, 'video/20260408/s2uZDmW9PZQEr6siyy6RdD5UT', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('gB1N2q2xgyfml5wVcp3i', 'WIyt3GAspT27A2i', 's2uZDmW9PZ', 'mkdXQoC5Dg', 1, 'Video_2024-12-27_121133', 421771, 'video/20260417/s2uZDmW9PZWIyt3GAspT27A2i', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('gWorVxgIhJkGp7HklTW6', 'XmGteAmD7MLtVcj', 's2uZDmW9PZ', 'rU7Dwfq8bx', 1, '恶意', 443902, 'video/20260422/s2uZDmW9PZXmGteAmD7MLtVcj', NULL, 1, 7, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('huU5Fo1nCoy0f3RQGoIe', 'VCjcs5aP3ET3eL4', 'TAhBXO5QVk', '510xOjscJf', 1, '粒子1', 3860594, 'video/20250911/TAhBXO5QVkVCjcs5aP3ET3eL4', NULL, 1, 6, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('JLmWePMh8CTK2fnWfpb2', 'jopIWN2dLaYidZw', 's2uZDmW9PZ', 'AW2hAubMkN', 1, '恶意', 443902, 'video/20260506/s2uZDmW9PZjopIWN2dLaYidZw', NULL, 1, 7, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('Jxo1h8bfHxFyEdDpEfzJ', 'LYcZkuc3dPfcQEs', 's2uZDmW9PZ', 'hu4T3wmqKD', 1, 'Video_2024-12-27_121133', 421771, 'video/20260408/s2uZDmW9PZLYcZkuc3dPfcQEs', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('l8st7sedlSNoKtbcSOty', 'oRAmw4Q2BpOjIZ7', 's2uZDmW9PZ', 'dogam72Tl0', 1, 'Video_2024-12-27_121133', 421771, 'video/20260417/s2uZDmW9PZoRAmw4Q2BpOjIZ7', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('LqeLSO7SKLR2UV6ODlew', 'has84QXM0iOs1nC', 's2uZDmW9PZ', 'WVEBRsIJph', 1, 'kali攻击', 21811721, 'video/20260417/s2uZDmW9PZhas84QXM0iOs1nC', NULL, 1, 365, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('nIm8aqgkqGz7hA4PGGGD', 'TSnWdb3xa4ttu95', 's2uZDmW9PZ', 'mK2b6AiVaT', 1, 'Video_2024-12-27_121133', 421771, 'video/20260408/s2uZDmW9PZTSnWdb3xa4ttu95', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('qeAl32lcf7nlv884dD1n', '9bW1jwV2xCFhIJ8', 'TAhBXO5QVk', 'y3bXaLrWe3', 1, '手表素材', 51362086, 'video/20250910/TAhBXO5QVk9bW1jwV2xCFhIJ8', NULL, 1, 30, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('ryBgzgZNm7mTxfe9tmaK', 'ZZXu20onj2p6C1p', 'TAhBXO5QVk', '510xOjscJf', 2, '权志龙', 5169097, 'video/20250911/TAhBXO5QVkZZXu20onj2p6C1p', NULL, 1, 4, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('toGgb9us2AKoPmV1k7UC', 'DY9JvrUqGpH0CCG', 's2uZDmW9PZ', '0cnMuWl8VB', 1, 'Video_2024-12-27_121133', 421771, 'video/20260417/s2uZDmW9PZDY9JvrUqGpH0CCG', NULL, 1, 2, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('uaFrQQYmiSRDDP6yC0GA', 'Ff1UEbY2Sdhdkbc', 's2uZDmW9PZ', 'vEQ3UBebjW', 1, '恶意', 443902, 'video/20260422/s2uZDmW9PZFf1UEbY2Sdhdkbc', NULL, 1, 7, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('yEYlJQY2Q42eSijOiKzk', 'mMWCCW1RjUC8Ewo', 'TAhBXO5QVk', '4j9ZbSAPht', 1, '试题ZM', 69205108, 'video/20250910/TAhBXO5QVkmMWCCW1RjUC8Ewo', NULL, 1, 54, 1);
INSERT INTO `tb_video_info_file_post` VALUES ('YnEIcGfu1RCh0wMzBDBU', 'OMD9WfrVDTiB85p', 's2uZDmW9PZ', 'LpDsvuoDl1', 1, '恶意', 443902, 'video/20260417/s2uZDmW9PZOMD9WfrVDTiB85p', NULL, 1, 7, 1);

-- ----------------------------
-- Table structure for tb_video_info_post
-- ----------------------------
DROP TABLE IF EXISTS `tb_video_info_post`;
CREATE TABLE `tb_video_info_post`  (
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '视频ID',
  `video_cover` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频封面',
  `video_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频名称',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '最后更新时间',
  `p_category_id` int NOT NULL COMMENT '父级分类ID',
  `category_id` int NULL DEFAULT NULL COMMENT '分类ID',
  `status` tinyint(1) NOT NULL COMMENT '0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败 5:待人工复核',
  `post_type` tinyint(1) NOT NULL COMMENT '0:自制 1:转载',
  `origin_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原资源说明',
  `tags` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `introduction` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `interaction` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '互动设置',
  `duration` int NULL DEFAULT NULL COMMENT '持续时间（秒）',
  `ai_audit_status` tinyint(1) NULL DEFAULT NULL COMMENT 'AI审核状态: 0-待审核, 1-通过, 2-不通过',
  `ai_audit_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI审核返回的详细结果(JSON)',
  `ai_audit_time` datetime NULL DEFAULT NULL COMMENT 'AI审核时间',
  `ai_audit_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI审核不通过原因',
  `review_status` tinyint(1) NULL DEFAULT NULL COMMENT '人工复核状态: 0-待复核, 1-通过, 2-不通过',
  `review_time` datetime NULL DEFAULT NULL COMMENT '人工复核时间',
  `reviewer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '复核人ID',
  `review_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人工复核不通过原因',
  PRIMARY KEY (`video_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_p_category_id`(`p_category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_video_info_post
-- ----------------------------
INSERT INTO `tb_video_info_post` VALUES ('0cnMuWl8VB', 'cover/20260417/xMTab1PlJmnJpgscpWe08paBx9oPaf.png', '切切切切', 's2uZDmW9PZ', '2026-04-17 15:57:44', '2026-04-17 15:57:44', 42, NULL, 3, 0, NULL, '999', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('AW2hAubMkN', 'cover/20260506/z3XSD4K4DiWgYyOc7qVSGcqNNTDiAx.png', '待审核', 's2uZDmW9PZ', '2026-05-06 09:30:13', '2026-05-06 09:30:13', 34, NULL, 4, 0, NULL, '234', NULL, NULL, 7, NULL, NULL, NULL, NULL, 2, '2026-05-06 15:25:36', NULL, '辱骂');
INSERT INTO `tb_video_info_post` VALUES ('dogam72Tl0', 'cover/20260417/UWvLbMWaKyNyFY3J1Ixt8YpUz2gcwY.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-17 16:19:41', '2026-04-17 16:19:41', 33, NULL, 3, 0, NULL, '3334', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('F1HCboCIht', 'cover/20260417/2UPVTE4Kj1AmyM9d7LJfKtwlqhXtms.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:13:28', '2026-04-17 16:13:28', 33, NULL, 3, 0, NULL, '000', NULL, NULL, 365, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('hu4T3wmqKD', 'cover/20260408/54EHJRkIlZKAqQ0Y2tNOyx2YvIhXs7.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 19:34:22', '2026-04-08 19:34:22', 34, NULL, 3, 0, NULL, '123', NULL, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('IJG4zyVGW1', 'cover/20260408/vOpcsQo6mnK0JqOSAIDfpATMtVsI9X.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 10:45:37', '2026-04-08 10:45:37', 33, NULL, 3, 0, NULL, '11', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('LpDsvuoDl1', 'cover/20260417/qLZCWoAyzQGxCP5B1TGkMRXQEmEeQd.png', '恶意', 's2uZDmW9PZ', '2026-04-17 16:39:16', '2026-04-17 16:39:16', 33, NULL, 3, 0, NULL, '34565', NULL, NULL, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('mK2b6AiVaT', 'cover/20260408/FBvIrv8tUXljQQLXGIWWqtsyTFy087.png', 'Video_2024-12-27_121133', 's2uZDmW9PZ', '2026-04-08 19:38:14', '2026-04-08 19:38:14', 33, NULL, 3, 0, NULL, '123', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('mkdXQoC5Dg', 'cover/20260417/hyk1vpI5AK6AlqLmtksquj43XV5Uro.png', 'testtest', 's2uZDmW9PZ', '2026-04-17 15:52:33', '2026-04-17 15:52:33', 33, NULL, 3, 0, NULL, '888', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('rU7Dwfq8bx', 'cover/20260422/QEXpm9hBmwJSIvH2qIY2nO1H7P4Zsg.png', '恶意3', 's2uZDmW9PZ', '2026-04-22 20:11:57', '2026-04-22 20:11:57', 33, NULL, 3, 0, NULL, '56', NULL, NULL, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('ufogfZiHIX', 'cover/20260417/LeSouCkExAWRoRapyiaclOVt0kqQnf.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:09:08', '2026-04-17 16:09:08', 33, NULL, 3, 0, NULL, '444', NULL, NULL, 365, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('vEQ3UBebjW', 'cover/20260422/BuOAh5GKB3jAGiqiUAP2Rk9Im11tCR.png', '恶意', 's2uZDmW9PZ', '2026-04-22 19:48:56', '2026-04-22 19:48:56', 33, NULL, 3, 0, NULL, '1445', NULL, NULL, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('WAtOvrX1O4', 'cover/20260417/mvQrHxHKWxkmaZp2ehtrMv1Ulnwyzt.png', 'test', 's2uZDmW9PZ', '2026-04-17 15:42:46', '2026-04-17 15:42:46', 33, NULL, 3, 0, NULL, '666', NULL, '', 127, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_video_info_post` VALUES ('WVEBRsIJph', 'cover/20260417/DMfYLw0iAWqyWGoULvbG9nZef874uT.png', 'kali攻击', 's2uZDmW9PZ', '2026-04-17 16:20:58', '2026-04-17 16:20:58', 34, NULL, 3, 0, NULL, '456', NULL, NULL, 365, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_action
-- ----------------------------
DROP TABLE IF EXISTS `user_action`;
CREATE TABLE `user_action`  (
  `action_id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `video_user_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频用户ID',
  `comment_id` int NOT NULL DEFAULT 0 COMMENT '评论ID',
  `action_type` tinyint(1) NOT NULL COMMENT '操作类型 0:评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币 ',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`action_id`) USING BTREE,
  UNIQUE INDEX `idx_key_video_comment_type_user`(`video_id` ASC, `comment_id` ASC, `action_type` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_type`(`action_type` ASC) USING BTREE,
  INDEX `idx_action_time`(`action_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为 点赞、评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_action
-- ----------------------------
INSERT INTO `user_action` VALUES (62, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, 4, 's2uZDmW9PZ', '2026-04-08 10:55:46');
INSERT INTO `user_action` VALUES (63, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, 1, 's2uZDmW9PZ', '2026-04-08 10:55:48');
INSERT INTO `user_action` VALUES (64, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, 3, 's2uZDmW9PZ', '2026-04-08 10:55:53');
INSERT INTO `user_action` VALUES (66, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, 0, 's2uZDmW9PZ', '2026-04-08 11:42:41');
INSERT INTO `user_action` VALUES (69, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, 0, 's2uZDmW9PZ', '2026-05-05 10:58:10');

-- ----------------------------
-- Table structure for user_focus
-- ----------------------------
DROP TABLE IF EXISTS `user_focus`;
CREATE TABLE `user_focus`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `focus_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被关注用户ID',
  `create_time` datetime NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_focus`(`user_id` ASC, `focus_user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_focus_user_id`(`focus_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_focus
-- ----------------------------
INSERT INTO `user_focus` VALUES (1, 's2uZDmW9PZ', '', '2026-04-26 19:15:45');
INSERT INTO `user_focus` VALUES (2, 's2uZDmW9PZ', 'TAhBXO5QVk', '2026-04-26 19:58:58');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `nick_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '0:女 1:男 2:未知',
  `birthday` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出生日期',
  `school` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学校',
  `person_introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登入时间',
  `last_login_ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登入ip',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '0;禁用 1:正常',
  `notice_info` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间公告',
  `total_coin_count` int NULL DEFAULT NULL COMMENT '硬币总数量',
  `current_coin_count` int NULL DEFAULT NULL COMMENT '当前硬币数',
  `theme` tinyint(1) NULL DEFAULT 1 COMMENT '主题',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_key_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `idx_nick_name`(`nick_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('0ZPodAIKg2', 'hhh', NULL, '9543ru@qq.com', 'd8c840035f897d596de4e82a6ce05a95', 2, NULL, NULL, NULL, '2025-08-02 14:50:28', NULL, NULL, 1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('MgMTE6cPtz', '小红', NULL, '1@qq.com', 'f8faf88f6816bbd535b4de2957a2edd5', 2, NULL, NULL, NULL, '2026-03-24 23:07:16', '2026-03-24 23:07:28', '0:0:0:0:0:0:0:1', 1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('rJbS11RofU', '哈哈哈', NULL, '943ru@qq.com', '6da36e8462ee3b2d72bf8a3b91910903', 2, NULL, NULL, NULL, '2025-08-01 22:11:47', '2025-08-02 17:47:53', '0:0:0:0:0:0:0:1', 1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('s2uZDmW9PZ', 'soda', NULL, '2@qq.com', 'bbad8d72c1fac1d081727158807a8798', 2, NULL, NULL, '我是大帅哥', '2026-04-08 10:42:25', '2026-05-06 17:57:36', '0:0:0:0:0:0:0:1', 1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('TAhBXO5QVk', '张德美', NULL, 'TEST@qq.com', '7d18d4c9ddb6790d732f70211345d1b5', 2, NULL, NULL, NULL, '2025-08-03 01:07:42', '2025-08-27 15:09:34', '0:0:0:0:0:0:0:1', 1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('user1002', '李四', NULL, 'lisi@example.com', 'securePwd456', 1, '1992-08-20', '清华大学', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES ('user1003', '王五', NULL, 'wangwu@example.com', 'strongPwd789', 0, '1995-03-10', '复旦大学', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES ('V5FvsAmPPO', 'logintest', NULL, '123@qq.com', 'bbad8d72c1fac1d081727158807a8798', 2, NULL, NULL, NULL, '2026-05-06 17:55:42', '2026-05-06 17:57:02', '0:0:0:0:0:0:0:1', -1, NULL, 10, 10, 1);
INSERT INTO `user_info` VALUES ('zhangsan', '张三', NULL, 'update@qq.com', '1234', 1, '2000.1.2', '美团', '哈哈哈', '2025-05-01 11:49:48', '2025-05-07 11:49:58', '北京', 1, '无', 20, 10, 1);

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收用户ID',
  `message_type` tinyint NOT NULL COMMENT '消息类型 1:点赞 2:投币 3:收藏 4:关注 5:评论 6:系统通知',
  `from_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送用户ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频ID',
  `comment_id` int NULL DEFAULT NULL COMMENT '评论ID',
  `message_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读 0:未读 1:已读',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, '', 4, 's2uZDmW9PZ', NULL, NULL, '关注了你', 0, '2026-04-26 19:15:45');
INSERT INTO `user_message` VALUES (2, 'TAhBXO5QVk', 4, 's2uZDmW9PZ', NULL, NULL, '关注了你', 0, '2026-04-26 19:58:58');

-- ----------------------------
-- Table structure for video_comment
-- ----------------------------
DROP TABLE IF EXISTS `video_comment`;
CREATE TABLE `video_comment`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `p_comment_id` int NOT NULL COMMENT '父级评论ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `video_user_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复内容',
  `img_path` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `reply_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复人ID',
  `top_type` tinyint NULL DEFAULT 0 COMMENT '0:未置顶 1:置顶',
  `post_time` datetime NOT NULL COMMENT '发布时间',
  `like_count` int NULL DEFAULT 0 COMMENT '喜欢数量',
  `hate_count` int NULL DEFAULT 0 COMMENT '讨厌数量',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0:正常 -1:管理员删除',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_post_time`(`post_time` ASC) USING BTREE,
  INDEX `idx_top`(`top_type` ASC) USING BTREE,
  INDEX `idx_p_id`(`p_comment_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of video_comment
-- ----------------------------
INSERT INTO `video_comment` VALUES (22, 0, 'IJG4zyVGW1', 's2uZDmW9PZ', '1111', NULL, 's2uZDmW9PZ', '', 0, '2026-04-08 11:15:26', 0, 0, 0);
INSERT INTO `video_comment` VALUES (23, 0, 'IJG4zyVGW1', 's2uZDmW9PZ', '1111', NULL, 's2uZDmW9PZ', '', 0, '2026-04-08 11:32:21', 0, 0, 0);
INSERT INTO `video_comment` VALUES (24, 0, 'IJG4zyVGW1', 's2uZDmW9PZ', '123', NULL, 's2uZDmW9PZ', '', 0, '2026-04-08 11:42:35', 0, 0, 0);
INSERT INTO `video_comment` VALUES (26, 0, 'rU7Dwfq8bx', 's2uZDmW9PZ', '4.26test\r\n', NULL, 's2uZDmW9PZ', '', 0, '2026-04-26 19:26:43', 0, 0, 0);
INSERT INTO `video_comment` VALUES (27, 0, 'dogam72Tl0', 's2uZDmW9PZ', 'test 4.26\r\n', NULL, 's2uZDmW9PZ', '', 0, '2026-04-26 19:58:32', 0, 0, 0);
INSERT INTO `video_comment` VALUES (28, 0, 'mK2b6AiVaT', 's2uZDmW9PZ', '123', NULL, 's2uZDmW9PZ', '', 0, '2026-05-05 10:01:12', 0, 0, 0);
INSERT INTO `video_comment` VALUES (29, 0, 'vEQ3UBebjW', 's2uZDmW9PZ', '124', NULL, 's2uZDmW9PZ', '', 0, '2026-05-05 10:07:08', 3, 0, 0);
INSERT INTO `video_comment` VALUES (30, 0, 'vEQ3UBebjW', 's2uZDmW9PZ', '1234', NULL, 's2uZDmW9PZ', '', 0, '2026-05-05 10:28:30', 0, 0, 0);
INSERT INTO `video_comment` VALUES (31, 29, 'vEQ3UBebjW', 's2uZDmW9PZ', '345', NULL, 's2uZDmW9PZ', 's2uZDmW9PZ', 0, '2026-05-05 10:34:05', 0, 0, 0);
INSERT INTO `video_comment` VALUES (32, 29, 'vEQ3UBebjW', 's2uZDmW9PZ', '789', NULL, 's2uZDmW9PZ', 's2uZDmW9PZ', 0, '2026-05-05 10:34:34', 0, 0, 0);
INSERT INTO `video_comment` VALUES (33, 29, 'vEQ3UBebjW', 's2uZDmW9PZ', '324', NULL, 's2uZDmW9PZ', 's2uZDmW9PZ', 0, '2026-05-05 10:40:49', 0, 0, 0);
INSERT INTO `video_comment` VALUES (34, 33, 'vEQ3UBebjW', 's2uZDmW9PZ', '234', NULL, 's2uZDmW9PZ', 's2uZDmW9PZ', 0, '2026-05-05 10:40:57', 0, 0, 0);
INSERT INTO `video_comment` VALUES (37, 28, 'mK2b6AiVaT', 's2uZDmW9PZ', 'test\r\n', NULL, 's2uZDmW9PZ', 's2uZDmW9PZ', 0, '2026-05-05 21:20:35', 0, 0, -1);

-- ----------------------------
-- Table structure for video_comment_audit
-- ----------------------------
DROP TABLE IF EXISTS `video_comment_audit`;
CREATE TABLE `video_comment_audit`  (
  `audit_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` int NULL DEFAULT NULL COMMENT '评论ID(审核失败时可能为空)',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `p_comment_id` int NOT NULL DEFAULT 0,
  `reply_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `audit_status` tinyint NOT NULL COMMENT '1通过 2失败',
  `audit_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ai_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `audit_time` datetime NOT NULL,
  PRIMARY KEY (`audit_id`) USING BTREE,
  UNIQUE INDEX `uk_comment_id`(`comment_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_audit_time`(`audit_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论AI审核记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_comment_audit
-- ----------------------------
INSERT INTO `video_comment_audit` VALUES (1, 22, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, '', '1111', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (2, 23, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, '', '1111', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (3, 24, 'IJG4zyVGW1', 's2uZDmW9PZ', 0, '', '123', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (4, 25, 'GZgcLaVux2', 's2uZDmW9PZ', 0, '', '4.26test', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (5, 26, 'rU7Dwfq8bx', 's2uZDmW9PZ', 0, '', '4.26test\r\n', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (6, 27, 'dogam72Tl0', 's2uZDmW9PZ', 0, '', 'test 4.26\r\n', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (7, 28, 'mK2b6AiVaT', 's2uZDmW9PZ', 0, '', '123', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (8, 29, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, '', '124', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (9, 30, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, '', '1234', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (10, 31, 'vEQ3UBebjW', 's2uZDmW9PZ', 29, 's2uZDmW9PZ', '345', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (11, 32, 'vEQ3UBebjW', 's2uZDmW9PZ', 29, 's2uZDmW9PZ', '789', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (12, 33, 'vEQ3UBebjW', 's2uZDmW9PZ', 29, 's2uZDmW9PZ', '324', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (13, 34, 'vEQ3UBebjW', 's2uZDmW9PZ', 33, 's2uZDmW9PZ', '234', 1, NULL, 'init', '2026-05-05 12:06:32');
INSERT INTO `video_comment_audit` VALUES (16, 35, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, '', 'test', 1, NULL, 'deepseek-v3-2-251201', '2026-05-05 12:18:42');
INSERT INTO `video_comment_audit` VALUES (17, 36, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, '', '操你妈你妈死了', 1, NULL, 'deepseek-v3-2-251201', '2026-05-05 12:20:57');
INSERT INTO `video_comment_audit` VALUES (18, NULL, 'vEQ3UBebjW', 's2uZDmW9PZ', 0, '', '操你妈你妈死了', 2, '文本内容包含辱骂、侮辱性语言及暴力倾向表达，违反文明交流规范。', 'deepseek-v3-2-251201', '2026-05-05 12:24:15');
INSERT INTO `video_comment_audit` VALUES (19, 37, 'mK2b6AiVaT', 's2uZDmW9PZ', 28, 's2uZDmW9PZ', 'test\r\n', 1, NULL, 'deepseek-v3-2-251201', '2026-05-05 13:20:35');

-- ----------------------------
-- Table structure for video_danmu
-- ----------------------------
DROP TABLE IF EXISTS `video_danmu`;
CREATE TABLE `video_danmu`  (
  `danmu_id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `post_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `is_show` tinyint(1) NULL DEFAULT NULL COMMENT '展示设置',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颜色',
  `time` int NULL DEFAULT NULL COMMENT '展示时间',
  PRIMARY KEY (`danmu_id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频弹幕' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of video_danmu
-- ----------------------------
INSERT INTO `video_danmu` VALUES (19, 'IJG4zyVGW1', '', 's2uZDmW9PZ', '2026-04-08 11:04:47', '12', 1, '#ffffff', 0);
INSERT INTO `video_danmu` VALUES (20, 'IJG4zyVGW1', '', 's2uZDmW9PZ', '2026-04-08 11:32:33', '123', 1, '#ffffff', 0);
INSERT INTO `video_danmu` VALUES (21, 'IJG4zyVGW1', '', 's2uZDmW9PZ', '2026-04-08 15:23:39', '4545645646465', 0, '#ffffff', 0);

-- ----------------------------
-- Table structure for video_series
-- ----------------------------
DROP TABLE IF EXISTS `video_series`;
CREATE TABLE `video_series`  (
  `series_id` int NOT NULL AUTO_INCREMENT COMMENT '系列ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `series_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列名称',
  `series_cover` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '系列封面',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`series_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频系列' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of video_series
-- ----------------------------

-- ----------------------------
-- Table structure for video_series_video
-- ----------------------------
DROP TABLE IF EXISTS `video_series_video`;
CREATE TABLE `video_series_video`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `series_id` int NOT NULL COMMENT '系列ID',
  `video_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_series_id`(`series_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系列视频关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of video_series_video
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
