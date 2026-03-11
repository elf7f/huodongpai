SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `huodongpai`;

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`)
VALUES
  ('admin', '$2a$10$r.M2UKfipccmdHsXu2SQPuNpGOG6XUuMBAsUCydobLUr27W4g8.oa', '系统管理员', '13800000000', 'admin', 1),
  ('test01', '$2a$10$r.M2UKfipccmdHsXu2SQPuNpGOG6XUuMBAsUCydobLUr27W4g8.oa', '测试用户', '13900000000', 'user', 1)
ON DUPLICATE KEY UPDATE
  `real_name` = VALUES(`real_name`),
  `phone` = VALUES(`phone`),
  `role` = VALUES(`role`),
  `status` = VALUES(`status`);

INSERT INTO `event_category` (`category_name`, `sort_num`, `status`)
VALUES
  ('讲座', 1, 1),
  ('培训', 2, 1),
  ('比赛', 3, 1),
  ('沙龙', 4, 1),
  ('会议', 5, 1)
ON DUPLICATE KEY UPDATE
  `sort_num` = VALUES(`sort_num`),
  `status` = VALUES(`status`);
