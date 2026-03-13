# Postman 使用说明

主启动步骤以 `README.md` 为准；本页只说明 Postman 的导入和使用方式。

## 文件位置

- Collection：`postman/huodongpai.postman_collection.json`
- Environment：`postman/huodongpai.local.postman_environment.json`

## 导入顺序

1. 导入 Collection
2. 导入 Environment
3. 选择环境 `huodongpai-local`

## 推荐执行顺序

1. 执行 `认证/管理员登录`
2. 执行 `认证/普通用户登录`
3. 管理端接口使用 `adminToken`
4. 用户端接口使用 `userToken`

## 变量说明

- `baseUrl`：默认 `http://localhost:8080`
- `eventCategoryId`：活动创建时使用的分类 ID，默认值为初始化分类 `1`
- `categoryId`、`eventId`、`signupId`、`managedUserId`：可由集合测试脚本自动回填
