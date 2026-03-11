SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `huodongpai`;

UPDATE `sys_user`
SET `real_name` = CASE `username`
  WHEN 'admin' THEN '系统管理员'
  WHEN 'test01' THEN '测试用户'
  ELSE `real_name`
END
WHERE `username` IN ('admin', 'test01');
