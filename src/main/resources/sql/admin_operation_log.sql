/*
 Navicat Premium Data Transfer

 Source Server         : xiaoyanoo
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3306
 Source Schema         : linkme

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 08/07/2026 14:05:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_operation_log`;
CREATE TABLE `admin_operation_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `admin_id` int(0) NOT NULL COMMENT '管理员 user_id',
  `target_user_id` int(0) NULL DEFAULT NULL COMMENT '目标用户ID',
  `target_id` bigint(0) NULL DEFAULT NULL COMMENT '目标资源ID（帖子/评论等）',
  `target_type` tinyint(0) NULL DEFAULT NULL COMMENT '0-帖子, 1-评论, 2-用户',
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型：WARN/TEMP_BANNED/HIDE_POST/DELETE_USER 等',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作原因',
  `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '补充说明',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id`) USING BTREE,
  INDEX `idx_target_user`(`target_user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for audit_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '内容发布者ID',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型: post / comment / message',
  `content_id` bigint(0) NULL DEFAULT NULL COMMENT '内容ID（帖子ID/评论ID/消息ID）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '原始内容（截断存储，建议前500字）',
  `is_violation` tinyint(1) NULL DEFAULT 0 COMMENT '是否违规: 0-否, 1-是',
  `matched_words` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '命中的敏感词（逗号分隔）',
  `categories` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '命中分类（逗号分隔）',
  `audit_result` tinyint(0) NULL DEFAULT 0 COMMENT '审核结果: 0-自动通过, 1-送人工, 2-人工通过, 3-人工拒绝, 4-事后下架',
  `auditor_id` bigint(0) NULL DEFAULT NULL COMMENT '审核员ID（人工审核时记录）',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_content`(`content_type`, `content_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_audit_result`(`audit_result`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审核日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for block
-- ----------------------------
DROP TABLE IF EXISTS `block`;
CREATE TABLE `block`  (
  `blocker_id` int(0) NOT NULL COMMENT '屏蔽者ID',
  `blocked_id` int(0) NOT NULL COMMENT '被屏蔽者ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '屏蔽时间',
  PRIMARY KEY (`blocker_id`, `blocked_id`) USING BTREE,
  INDEX `idx_blocked_id`(`blocked_id`) USING BTREE,
  INDEX `idx_blocker_id`(`blocker_id`) USING BTREE,
  CONSTRAINT `block_ibfk_1` FOREIGN KEY (`blocker_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `block_ibfk_2` FOREIGN KEY (`blocked_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '屏蔽表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` int(0) NOT NULL COMMENT '帖子ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `moderation_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'visible' COMMENT 'visible|hidden|deleted',
  `parent_id` int(0) NULL DEFAULT NULL COMMENT '父评论ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_post_id_created_at`(`post_id`, `created_at`) USING BTREE,
  INDEX `idx_user_id_created_at`(`user_id`, `created_at`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for communication_expectation
-- ----------------------------
DROP TABLE IF EXISTS `communication_expectation`;
CREATE TABLE `communication_expectation`  (
  `expectation_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '期待ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '期待名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期待描述',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`expectation_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '沟通期待表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `conversation_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user1_id` int(0) NOT NULL COMMENT '用户1ID',
  `user2_id` int(0) NOT NULL COMMENT '用户2ID',
  `user1_muted` tinyint(1) NULL DEFAULT 0 COMMENT '用户1是否免打扰',
  `user2_muted` tinyint(1) NULL DEFAULT 0 COMMENT '用户2是否免打扰',
  `user1_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '用户1是否置顶',
  `user2_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '用户2是否置顶',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`conversation_id`) USING BTREE,
  UNIQUE INDEX `uk_user_pair`(`user1_id`, `user2_id`) USING BTREE,
  INDEX `idx_user1_id`(`user1_id`) USING BTREE,
  INDEX `idx_user2_id`(`user2_id`) USING BTREE,
  CONSTRAINT `conversation_ibfk_1` FOREIGN KEY (`user1_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `conversation_ibfk_2` FOREIGN KEY (`user2_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `favorite_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `post_id` int(0) NOT NULL COMMENT '帖子ID',
  `folder_id` int(0) NOT NULL COMMENT '收藏夹ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`favorite_id`) USING BTREE,
  UNIQUE INDEX `uk_user_post_folder`(`user_id`, `post_id`, `folder_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE,
  INDEX `idx_folder_id`(`folder_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `favorite_ibfk_3` FOREIGN KEY (`folder_id`) REFERENCES `favorite_folder` (`folder_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorite_folder
-- ----------------------------
DROP TABLE IF EXISTS `favorite_folder`;
CREATE TABLE `favorite_folder`  (
  `folder_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏夹名称',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`folder_id`) USING BTREE,
  UNIQUE INDEX `uk_user_folder_name`(`user_id`, `name`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `favorite_folder_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏夹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `follower_id` int(0) NOT NULL COMMENT '关注者ID',
  `followee_id` int(0) NOT NULL COMMENT '被关注者ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '关注时间',
  PRIMARY KEY (`follower_id`, `followee_id`) USING BTREE,
  INDEX `idx_followee_id`(`followee_id`) USING BTREE,
  INDEX `idx_follower_id`(`follower_id`) USING BTREE,
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`followee_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for heart
-- ----------------------------
DROP TABLE IF EXISTS `heart`;
CREATE TABLE `heart`  (
  `heart_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '红心ID',
  `from_user_id` int(0) NOT NULL COMMENT '发送者ID',
  `to_user_id` int(0) NOT NULL COMMENT '接收者ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`heart_id`) USING BTREE,
  UNIQUE INDEX `uk_from_to_user`(`from_user_id`, `to_user_id`) USING BTREE,
  INDEX `idx_to_user_id`(`to_user_id`) USING BTREE,
  INDEX `idx_from_user_id`(`from_user_id`) USING BTREE,
  CONSTRAINT `heart_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `heart_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '红心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hobby
-- ----------------------------
DROP TABLE IF EXISTS `hobby`;
CREATE TABLE `hobby`  (
  `hobby_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '爱好ID',
  `category_id` int(0) NOT NULL COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '爱好名称',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`hobby_id`) USING BTREE,
  UNIQUE INDEX `uk_category_name`(`category_id`, `name`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE,
  CONSTRAINT `hobby_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `hobby_category` (`category_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '爱好表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hobby_category
-- ----------------------------
DROP TABLE IF EXISTS `hobby_category`;
CREATE TABLE `hobby_category`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '爱好分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for like_post
-- ----------------------------
DROP TABLE IF EXISTS `like_post`;
CREATE TABLE `like_post`  (
  `like_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `post_id` int(0) NOT NULL COMMENT '帖子ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`like_id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id`, `post_id`) USING BTREE,
  INDEX `idx_post_id_created_at`(`post_id`, `created_at`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `like_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `like_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for manual_review_queue
-- ----------------------------
DROP TABLE IF EXISTS `manual_review_queue`;
CREATE TABLE `manual_review_queue`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '内容发布者ID',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型: post / comment / message',
  `content_id` bigint(0) NULL DEFAULT NULL COMMENT '内容ID（若内容已创建）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始完整内容',
  `matched_words` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '算法命中的敏感词',
  `categories` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '命中分类',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
  `reviewer_id` bigint(0) NULL DEFAULT NULL COMMENT '审核员ID',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '提交时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '来源类型：system/user_report',
  `reporter_id` bigint(0) NULL DEFAULT NULL COMMENT '举报人ID',
  `report_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '举报原因',
  `target_user_id` bigint(0) NULL DEFAULT NULL COMMENT '被举报用户ID',
  `process_action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理动作',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '人工复审队列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for match_user
-- ----------------------------
DROP TABLE IF EXISTS `match_user`;
CREATE TABLE `match_user`  (
  `match_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '匹配ID',
  `user1_id` int(0) NOT NULL COMMENT '用户1ID',
  `user2_id` int(0) NOT NULL COMMENT '用户2ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '匹配时间',
  `status` int(0) NULL DEFAULT 0 COMMENT '状态：0-进行中，1-已结束',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`match_id`) USING BTREE,
  UNIQUE INDEX `uk_user_pair`(`user1_id`, `user2_id`) USING BTREE,
  INDEX `idx_user1_id`(`user1_id`) USING BTREE,
  INDEX `idx_user2_id`(`user2_id`) USING BTREE,
  CONSTRAINT `match_user_ibfk_1` FOREIGN KEY (`user1_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `match_user_ibfk_2` FOREIGN KEY (`user2_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '匹配表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for matching_dimension
-- ----------------------------
DROP TABLE IF EXISTS `matching_dimension`;
CREATE TABLE `matching_dimension`  (
  `dimension_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '维度ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '维度名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '维度代码',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`dimension_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '匹配维度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` int(0) NOT NULL COMMENT '会话ID',
  `sender_id` int(0) NOT NULL COMMENT '发送者ID',
  `hidden_for_user_id` int(0) NULL DEFAULT NULL COMMENT '对哪个用户隐藏（屏蔽功能使用）',
  `content_type` enum('text','image','video','voice','file') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'text' COMMENT '内容类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_conversation_created`(`conversation_id`, `created_at`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id`) USING BTREE,
  INDEX `idx_is_read`(`is_read`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_hidden_for_user_id`(`hidden_for_user_id`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_3` FOREIGN KEY (`hidden_for_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `notification_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `type` enum('message','follow','heart','like','comment','match','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型',
  `actor_id` int(0) NOT NULL COMMENT '操作者ID',
  `related_id` int(0) NULL DEFAULT NULL COMMENT '关联实体ID',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联实体类型',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知内容',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '通知时间',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `idx_user_created`(`user_id`, `created_at`) USING BTREE,
  INDEX `idx_user_read`(`user_id`, `is_read`, `created_at`) USING BTREE,
  INDEX `idx_actor_id`(`actor_id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_related`(`related_id`, `related_type`) USING BTREE,
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`actor_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for personality_trait_category
-- ----------------------------
DROP TABLE IF EXISTS `personality_trait_category`;
CREATE TABLE `personality_trait_category`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `trait_type` enum('self','ideal') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '特质类型：self-自身特质，ideal-理想对象特质',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `uk_name_type`(`name`, `trait_type`) USING BTREE,
  INDEX `idx_trait_type`(`trait_type`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '性格特质分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for personality_trait_option
-- ----------------------------
DROP TABLE IF EXISTS `personality_trait_option`;
CREATE TABLE `personality_trait_option`  (
  `option_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `category_id` int(0) NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项名称',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`option_id`) USING BTREE,
  UNIQUE INDEX `uk_category_name`(`category_id`, `name`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE,
  CONSTRAINT `personality_trait_option_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `personality_trait_category` (`category_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '性格特质选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `post_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `moderation_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'visible' COMMENT 'visible|hidden|deleted',
  `privacy_level` enum('public','followers','intimate','private') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'public' COMMENT '隐私级别',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_privacy_level`(`privacy_level`) USING BTREE,
  INDEX `idx_topic`(`topic`) USING BTREE,
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_image
-- ----------------------------
DROP TABLE IF EXISTS `post_image`;
CREATE TABLE `post_image`  (
  `image_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `post_id` int(0) NOT NULL COMMENT '帖子ID',
  `image_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片Base64编码字符串',
  `image_order` int(0) NULL DEFAULT 0 COMMENT '图片顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE,
  INDEX `idx_image_order`(`image_order`) USING BTREE,
  CONSTRAINT `post_image_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `post_id` int(0) NOT NULL COMMENT '帖子ID',
  `tag_id` int(0) NOT NULL COMMENT '标签ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`post_id`, `tag_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `post_tag_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag_def` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for privacy_setting
-- ----------------------------
DROP TABLE IF EXISTS `privacy_setting`;
CREATE TABLE `privacy_setting`  (
  `privacy_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '隐私设置ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `allow_match` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许匹配',
  `allow_private_messages` enum('all','followed','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'followed' COMMENT '允许私聊',
  `allow_profile_view` enum('all','followed','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'all' COMMENT '允许查看个人资料',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`privacy_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `privacy_setting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '隐私设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relationship_mode
-- ----------------------------
DROP TABLE IF EXISTS `relationship_mode`;
CREATE TABLE `relationship_mode`  (
  `mode_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '模式ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模式名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模式描述',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`mode_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关系模式表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relationship_quality
-- ----------------------------
DROP TABLE IF EXISTS `relationship_quality`;
CREATE TABLE `relationship_quality`  (
  `quality_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '品质ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品质名称',
  `display_order` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`quality_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE,
  INDEX `idx_display_order`(`display_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关系品质表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag_def
-- ----------------------------
DROP TABLE IF EXISTS `tag_def`;
CREATE TABLE `tag_def`  (
  `tag_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `created_by` int(0) NULL DEFAULT NULL COMMENT '创建者ID，NULL为系统设定',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `tag_type` enum('post','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签类型',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  UNIQUE INDEX `uk_name_type`(`name`, `tag_type`) USING BTREE,
  INDEX `idx_tag_type`(`tag_type`) USING BTREE,
  INDEX `idx_created_by`(`created_by`) USING BTREE,
  CONSTRAINT `tag_def_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `password_hash` char(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '地区',
  `avatar_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '头像Base64编码字符串',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
  `role` enum('customer','admin','moderator') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'customer' COMMENT '用户角色（权限，与 account_status 处罚状态分离）',
  `account_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'normal' COMMENT '账号状态: normal|warned|restricted_post|restricted_comment|temp_banned|perm_banned',
  `ban_until` datetime(0) NULL DEFAULT NULL COMMENT '临时封禁截止',
  `status_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处罚原因',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `matching_questionnaire_completed` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成匹配问卷',
  `matching_questionnaire_completed_at` datetime(0) NULL DEFAULT NULL COMMENT '问卷完成时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `idx_phone`(`phone`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_hobby
-- ----------------------------
DROP TABLE IF EXISTS `user_hobby`;
CREATE TABLE `user_hobby`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `hobby_id` int(0) NOT NULL COMMENT '爱好ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `hobby_id`) USING BTREE,
  INDEX `idx_hobby_id`(`hobby_id`) USING BTREE,
  CONSTRAINT `user_hobby_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_hobby_ibfk_2` FOREIGN KEY (`hobby_id`) REFERENCES `hobby` (`hobby_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户爱好关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_like
-- ----------------------------
DROP TABLE IF EXISTS `user_like`;
CREATE TABLE `user_like`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `from_user_id` int(0) NOT NULL COMMENT '发送喜欢的用户ID',
  `to_user_id` int(0) NOT NULL COMMENT '接收喜欢的用户ID',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_from_to`(`from_user_id`, `to_user_id`) USING BTREE COMMENT '防止重复喜欢',
  INDEX `idx_from_user`(`from_user_id`) USING BTREE,
  INDEX `idx_to_user`(`to_user_id`) USING BTREE,
  INDEX `idx_from_to`(`from_user_id`, `to_user_id`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  CONSTRAINT `user_like_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_like_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户喜欢记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_matching_must_dimension
-- ----------------------------
DROP TABLE IF EXISTS `user_matching_must_dimension`;
CREATE TABLE `user_matching_must_dimension`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `dimension_id` int(0) NOT NULL COMMENT '维度ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `dimension_id`) USING BTREE,
  INDEX `idx_dimension_id`(`dimension_id`) USING BTREE,
  CONSTRAINT `user_matching_must_dimension_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_matching_must_dimension_ibfk_2` FOREIGN KEY (`dimension_id`) REFERENCES `matching_dimension` (`dimension_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户匹配必须维度关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_matching_preference
-- ----------------------------
DROP TABLE IF EXISTS `user_matching_preference`;
CREATE TABLE `user_matching_preference`  (
  `preference_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '偏好ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `age_min` int(0) NULL DEFAULT NULL COMMENT '最小年龄要求',
  `age_max` int(0) NULL DEFAULT NULL COMMENT '最大年龄要求',
  `age_unlimited` tinyint(1) NULL DEFAULT 0 COMMENT '是否无年龄限制',
  `distance_preference` enum('same_city','same_city_or_remote','unlimited') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'same_city' COMMENT '关系距离要求：same_city-同城优先，same_city_or_remote-同城/异地均可，unlimited-不限距离',
  `relationship_mode_id` int(0) NULL DEFAULT NULL COMMENT '理想关系模式ID',
  `communication_expectation_id` int(0) NULL DEFAULT NULL COMMENT '沟通期待ID',
  `additional_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '其他未被覆盖的交友要求',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`preference_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `relationship_mode_id`(`relationship_mode_id`) USING BTREE,
  INDEX `communication_expectation_id`(`communication_expectation_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_matching_preference_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_matching_preference_ibfk_2` FOREIGN KEY (`relationship_mode_id`) REFERENCES `relationship_mode` (`mode_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `user_matching_preference_ibfk_3` FOREIGN KEY (`communication_expectation_id`) REFERENCES `communication_expectation` (`expectation_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户匹配偏好表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_matching_priority_dimension
-- ----------------------------
DROP TABLE IF EXISTS `user_matching_priority_dimension`;
CREATE TABLE `user_matching_priority_dimension`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `dimension_id` int(0) NOT NULL COMMENT '维度ID',
  `priority_order` int(0) NULL DEFAULT 0 COMMENT '优先级顺序',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `dimension_id`) USING BTREE,
  INDEX `idx_dimension_id`(`dimension_id`) USING BTREE,
  INDEX `idx_priority_order`(`priority_order`) USING BTREE,
  CONSTRAINT `user_matching_priority_dimension_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_matching_priority_dimension_ibfk_2` FOREIGN KEY (`dimension_id`) REFERENCES `matching_dimension` (`dimension_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户匹配优先维度关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_personality
-- ----------------------------
DROP TABLE IF EXISTS `user_personality`;
CREATE TABLE `user_personality`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `option_id` int(0) NOT NULL COMMENT '选项ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`user_id`, `option_id`) USING BTREE,
  INDEX `idx_option_id`(`option_id`) USING BTREE,
  CONSTRAINT `user_personality_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_personality_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `personality_trait_option` (`option_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户性格特质表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_questionnaire_completion
-- ----------------------------
DROP TABLE IF EXISTS `user_questionnaire_completion`;
CREATE TABLE `user_questionnaire_completion`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `first_completed_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '首次完成时间',
  `last_submitted_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '上次提交时间',
  `submission_count` int(0) NULL DEFAULT 1 COMMENT '提交次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_questionnaire_completion_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户问卷完成记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_relationship_quality
-- ----------------------------
DROP TABLE IF EXISTS `user_relationship_quality`;
CREATE TABLE `user_relationship_quality`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `quality_id` int(0) NOT NULL COMMENT '品质ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `quality_id`) USING BTREE,
  INDEX `idx_quality_id`(`quality_id`) USING BTREE,
  CONSTRAINT `user_relationship_quality_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_relationship_quality_ibfk_2` FOREIGN KEY (`quality_id`) REFERENCES `relationship_quality` (`quality_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户关系品质关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_tag
-- ----------------------------
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`  (
  `user_id` int(0) NOT NULL COMMENT '用户ID',
  `tag_id` int(0) NOT NULL COMMENT '标签ID',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `tag_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `user_tag_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag_def` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for verification_code
-- ----------------------------
DROP TABLE IF EXISTS `verification_code`;
CREATE TABLE `verification_code`  (
  `code_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
  `type` enum('email','phone') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `purpose` enum('register','login','reset_password') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'register' COMMENT '用途',
  `expire_at` datetime(0) NOT NULL COMMENT '过期时间',
  `is_used` tinyint(1) NULL DEFAULT 0 COMMENT '是否已使用',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发送时间',
  PRIMARY KEY (`code_id`) USING BTREE,
  INDEX `idx_code_type`(`code`, `type`) USING BTREE,
  INDEX `idx_expire_at`(`expire_at`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_used`(`is_used`) USING BTREE,
  CONSTRAINT `verification_code_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '验证码表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
