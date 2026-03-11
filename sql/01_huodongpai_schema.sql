CREATE DATABASE IF NOT EXISTS `huodongpai`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `huodongpai`;

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码哈希',
  `real_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：admin/user',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  KEY `idx_sys_user_role_status` (`role`, `status`),
  CONSTRAINT `chk_sys_user_role` CHECK (`role` IN ('admin', 'user')),
  CONSTRAINT `chk_sys_user_status` CHECK (`status` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS `event_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_num` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_category_name` (`category_name`),
  KEY `idx_event_category_status_sort` (`status`, `sort_num`),
  CONSTRAINT `chk_event_category_status` CHECK (`status` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动分类表';

CREATE TABLE IF NOT EXISTS `event_info` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT '活动分类ID',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '活动封面URL',
  `location` VARCHAR(255) NOT NULL COMMENT '活动地点',
  `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
  `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
  `signup_deadline` DATETIME NOT NULL COMMENT '报名截止时间',
  `max_participants` INT NOT NULL COMMENT '最大报名人数',
  `need_audit` TINYINT NOT NULL DEFAULT 0 COMMENT '是否需要审核：1是 0否',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '基础状态：draft/published/cancelled',
  `description` TEXT COMMENT '活动详情',
  `signup_count` INT NOT NULL DEFAULT 0 COMMENT '当前占用名额人数（pending+approved）',
  `approved_count` INT NOT NULL DEFAULT 0 COMMENT '当前审核通过人数',
  `checkin_count` INT NOT NULL DEFAULT 0 COMMENT '当前已签到人数',
  `create_by` BIGINT UNSIGNED NOT NULL COMMENT '创建人',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_info_category_status` (`category_id`, `status`),
  KEY `idx_event_info_status_time` (`status`, `signup_deadline`, `start_time`, `end_time`),
  KEY `idx_event_info_start_time` (`start_time`),
  KEY `idx_event_info_create_by` (`create_by`),
  CONSTRAINT `fk_event_info_category` FOREIGN KEY (`category_id`) REFERENCES `event_category` (`id`),
  CONSTRAINT `fk_event_info_create_by` FOREIGN KEY (`create_by`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_event_info_need_audit` CHECK (`need_audit` IN (0, 1)),
  CONSTRAINT `chk_event_info_status` CHECK (`status` IN ('draft', 'published', 'cancelled')),
  CONSTRAINT `chk_event_info_time` CHECK (`signup_deadline` < `start_time` AND `start_time` < `end_time`),
  CONSTRAINT `chk_event_info_capacity` CHECK (`max_participants` > 0),
  CONSTRAINT `chk_event_info_counts` CHECK (
    `signup_count` >= 0
    AND `approved_count` >= 0
    AND `checkin_count` >= 0
    AND `signup_count` >= `approved_count`
    AND `approved_count` >= `checkin_count`
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动信息表';

CREATE TABLE IF NOT EXISTS `event_signup` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_id` BIGINT UNSIGNED NOT NULL COMMENT '活动ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `status` VARCHAR(20) NOT NULL COMMENT '报名状态：pending/approved/rejected/cancelled',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `signup_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次报名时间',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '审核人',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_signup_event_user` (`event_id`, `user_id`),
  KEY `idx_event_signup_user_status_time` (`user_id`, `status`, `signup_time`),
  KEY `idx_event_signup_event_status_time` (`event_id`, `status`, `signup_time`),
  KEY `idx_event_signup_audit` (`event_id`, `status`, `audit_time`),
  CONSTRAINT `fk_event_signup_event` FOREIGN KEY (`event_id`) REFERENCES `event_info` (`id`),
  CONSTRAINT `fk_event_signup_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_event_signup_audit_by` FOREIGN KEY (`audit_by`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_event_signup_status` CHECK (`status` IN ('pending', 'approved', 'rejected', 'cancelled'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';

CREATE TABLE IF NOT EXISTS `event_signup_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `signup_id` BIGINT UNSIGNED NOT NULL COMMENT '报名记录ID',
  `event_id` BIGINT UNSIGNED NOT NULL COMMENT '活动ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '报名用户ID',
  `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型：apply/cancel/audit_pass/audit_reject',
  `target_status` VARCHAR(20) NOT NULL COMMENT '操作后报名状态',
  `operator_id` BIGINT UNSIGNED NOT NULL COMMENT '操作人ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_signup_log_event_time` (`event_id`, `operation_time`),
  KEY `idx_event_signup_log_user_time` (`user_id`, `operation_time`),
  KEY `idx_event_signup_log_type_time` (`operation_type`, `operation_time`),
  CONSTRAINT `fk_event_signup_log_signup` FOREIGN KEY (`signup_id`) REFERENCES `event_signup` (`id`),
  CONSTRAINT `fk_event_signup_log_event` FOREIGN KEY (`event_id`) REFERENCES `event_info` (`id`),
  CONSTRAINT `fk_event_signup_log_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_event_signup_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_event_signup_log_operation_type` CHECK (`operation_type` IN ('apply', 'cancel', 'audit_pass', 'audit_reject')),
  CONSTRAINT `chk_event_signup_log_target_status` CHECK (`target_status` IN ('pending', 'approved', 'rejected', 'cancelled'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报名操作日志表';

CREATE TABLE IF NOT EXISTS `event_checkin` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_id` BIGINT UNSIGNED NOT NULL COMMENT '活动ID',
  `signup_id` BIGINT UNSIGNED NOT NULL COMMENT '报名记录ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `checkin_status` TINYINT NOT NULL DEFAULT 1 COMMENT '签到状态：1已签到；本方案在签到时创建记录',
  `checkin_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_checkin_signup` (`signup_id`),
  UNIQUE KEY `uk_event_checkin_event_user` (`event_id`, `user_id`),
  KEY `idx_event_checkin_event_time` (`event_id`, `checkin_time`),
  KEY `idx_event_checkin_user_time` (`user_id`, `checkin_time`),
  CONSTRAINT `fk_event_checkin_event` FOREIGN KEY (`event_id`) REFERENCES `event_info` (`id`),
  CONSTRAINT `fk_event_checkin_signup` FOREIGN KEY (`signup_id`) REFERENCES `event_signup` (`id`),
  CONSTRAINT `fk_event_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_event_checkin_status` CHECK (`checkin_status` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动签到表';

CREATE OR REPLACE VIEW `v_event_runtime_status` AS
SELECT
  `e`.`id`,
  `e`.`title`,
  `e`.`category_id`,
  `e`.`cover_url`,
  `e`.`location`,
  `e`.`start_time`,
  `e`.`end_time`,
  `e`.`signup_deadline`,
  `e`.`max_participants`,
  `e`.`need_audit`,
  `e`.`status` AS `base_status`,
  CASE
    WHEN `e`.`status` = 'draft' THEN 'draft'
    WHEN `e`.`status` = 'cancelled' THEN 'cancelled'
    WHEN NOW() < `e`.`signup_deadline` THEN 'signup_open'
    WHEN NOW() >= `e`.`signup_deadline` AND NOW() < `e`.`start_time` THEN 'signup_closed'
    WHEN NOW() >= `e`.`start_time` AND NOW() < `e`.`end_time` THEN 'ongoing'
    ELSE 'finished'
  END AS `runtime_status`,
  `e`.`signup_count`,
  `e`.`approved_count`,
  `e`.`checkin_count`,
  `e`.`create_by`,
  `e`.`version`,
  `e`.`create_time`,
  `e`.`update_time`
FROM `event_info` `e`;
