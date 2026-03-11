# GitHub 仓库展示建议

这份文档用于你上传仓库时，直接复用 GitHub 上的仓库描述、标签和展示文案。

## 仓库名称建议

- `huodongpai`
- `event-registration-checkin-system`
- `event-management-system`

## 仓库简介建议

### 中文

基于 Spring Boot + Vue 3 的活动报名与签到管理系统，支持活动发布、在线报名、审核、线下签到与统计分析。

### English

An event registration and check-in management system built with Spring Boot and Vue 3, supporting event publishing, online registration, approval workflow, on-site check-in, and statistics.

## GitHub Topics 建议

- `spring-boot`
- `vue3`
- `vite`
- `element-plus`
- `mybatis-plus`
- `mysql`
- `redis`
- `jwt`
- `event-management`
- `checkin-system`
- `admin-dashboard`

## 推荐仓库封面信息

你可以在 README 首页强调以下几点：

- 完整业务闭环：发布、报名、审核、签到、统计
- 单体架构：适合个人项目展示和面试讲解
- 技术栈完整：Spring Boot、MySQL、Redis、Vue 3、Element Plus
- 可直接本地运行：前后端本地启动，MySQL/Redis 通过 Docker Compose 提供

## 上传前自查

- 确认 `.env` 未提交
- 确认 `frontend/node_modules` 未提交
- 确认 `frontend/dist` 未提交
- 确认数据库密码等敏感信息仅保留在 `.env.example`
- 确认 `README.md` 与 `README_EN.md` 链接可点击
- 确认默认演示账号说明与实际种子数据一致
