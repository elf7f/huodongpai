# Postman 使用说明

## 文件位置

- Collection：`postman/huodongpai.postman_collection.json`
- Environment：`postman/huodongpai.local.postman_environment.json`

## 导入顺序

1. 导入 Collection
2. 导入 Environment
3. 选择环境 `huodongpai-local`

## 推荐执行顺序

1. 先执行 `认证/管理员登录`
2. 再执行 `认证/普通用户登录`
3. 管理端接口使用 `adminToken`
4. 用户端接口使用 `userToken`

## 变量说明

- `baseUrl`：默认 `http://localhost:8080`
- `eventCategoryId`：活动创建时使用的分类 ID，默认是初始化分类 `1`
- `categoryId`、`eventId`、`signupId`、`managedUserId`：可由集合中的测试脚本自动回填

## 联调前提

1. 启动 Spring Boot 服务
2. 执行数据库脚本：
   - `sql/01_huodongpai_schema.sql`
   - `sql/02_huodongpai_seed.sql`
3. 确保 MySQL 和 Redis 可用
