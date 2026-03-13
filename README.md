# 活动派

[中文](README.md) | [English](README_EN.md)

活动派是一个面向讲座、培训、比赛、沙龙、社团活动等场景的活动报名与签到管理系统。  
项目采用 `Spring Boot` 单体后端和 `Vue 3` 前端，覆盖活动发布、用户报名、审核管理、线下签到和统计分析的完整业务闭环。

## 项目特性

- 活动发布、编辑、发布、取消、删除
- 在线报名、人数控制、取消报名
- 报名审核与状态流转
- 线下签到核销
- 仪表盘、热门活动、报名趋势统计
- `JWT + Redis` 登录鉴权
- `MySQL` 持久化存储
- Postman 接口集合可直接导入

## 技术栈

**后端**
- `Spring Boot 3`
- `MyBatis-Plus`
- `MySQL 8`
- `Redis 7`
- `JWT`

**前端**
- `Vue 3`
- `Vite`
- `Element Plus`
- `Axios`
- `Vue Router`
- `Pinia`

## 项目结构

```text
huodongpai
├── src                         # Spring Boot 后端
├── frontend                    # Vue 3 前端
├── sql                         # 建表与初始化脚本
├── docs                        # 补充文档
├── postman                     # Postman 集合与环境
└── docker-compose.yml          # MySQL / Redis 本地依赖
```

## 核心模块

**用户端**
- 登录
- 活动列表 / 活动详情
- 提交报名
- 我的报名
- 取消报名

**管理端**
- 仪表盘
- 用户管理
- 活动分类管理
- 活动管理
- 报名审核
- 签到管理
- 统计分析

## 状态设计

**活动基础状态**
- `draft`
- `published`
- `cancelled`

**活动运行状态**
- `signup_open`
- `signup_closed`
- `ongoing`
- `finished`

**报名状态**
- `pending`
- `approved`
- `rejected`
- `cancelled`

## 默认测试账号

- 管理员：`admin / 123456`
- 普通用户：`test01 / 123456`

初始化数据见 `sql/02_huodongpai_seed.sql`，补充说明见 `docs/init-data.md`。

## 本地启动

### 1. 准备环境

- `JDK 17`
- `Maven 3.9+`
- `Node.js 20+`
- `Docker / Docker Compose`（仅用于启动 `MySQL`、`Redis`）

### 2. 复制环境变量

```bash
cp .env.example .env
```

常用变量：
- `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD`
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE` / `REDIS_PASSWORD`
- `JWT_SECRET`
- `SERVER_PORT`

### 3. 启动 MySQL 和 Redis

```bash
docker compose up -d
```

默认端口：
- `MySQL: 127.0.0.1:3306`
- `Redis: 127.0.0.1:6379`

首次启动时，MySQL 会自动执行：
- `sql/01_huodongpai_schema.sql`
- `sql/02_huodongpai_seed.sql`

如果之前保留了旧数据卷，需要重新初始化：

```bash
docker compose down -v
docker compose up -d
```

### 4. 启动后端

```bash
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

## 接口调试

已提供 Postman 文件：
- Collection：`postman/huodongpai.postman_collection.json`
- Environment：`postman/huodongpai.local.postman_environment.json`

使用说明见 `docs/postman-usage.md`。

## 文档索引

- 数据库设计：`docs/schema-design.md`
- 初始化数据：`docs/init-data.md`
- Postman 使用说明：`docs/postman-usage.md`
- GitHub 仓库展示建议：`docs/github-repo-metadata.md`

## 当前实现范围

**已完成**
- 后端核心业务接口
- 前端用户端与管理端页面
- `Docker Compose` 本地依赖编排
- Postman 调试集合

**暂未实现**
- 二维码签到
- 短信 / 邮件通知
- 支付功能
- WebSocket 实时通知
- 多租户与分布式架构

## 快速体验建议

1. 管理员登录
2. 创建活动分类
3. 创建并发布活动
4. 普通用户登录并报名
5. 管理员审核报名
6. 管理员执行签到
7. 查看统计分析页
