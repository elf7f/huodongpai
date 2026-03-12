# 活动派

[中文](README.md) | [English](README_EN.md)

活动派是一个面向讲座、培训、比赛、沙龙、社团活动等场景的活动报名与签到管理系统。  
项目采用 **Spring Boot 单体后端 + Vue 3 前端管理台**，覆盖活动发布、用户报名、审核管理、线下签到和统计分析的完整业务闭环。

## 项目特性

- 活动发布、编辑、发布、取消、删除
- 在线报名与人数控制
- 报名审核与状态流转
- 线下签到核销
- 仪表盘、热门活动、报名趋势统计
- JWT + Redis 登录鉴权
- MySQL 持久化存储
- Postman 接口集合可直接导入
- Docker Compose 一键启动 MySQL 与 Redis

## 技术栈

### 后端

- Spring Boot 3
- MyBatis-Plus
- MySQL 8
- Redis 7
- JWT
- Lombok

### 前端

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia

## 项目结构

```text
huodongpai
├── src                         # Spring Boot 后端
├── frontend                    # Vue3 前端
├── sql                         # 建表与初始化脚本
├── docs                        # 设计与使用文档
├── postman                     # Postman 集合与环境
└── docker-compose.yml          # MySQL/Redis 本地依赖编排
```

## 功能模块

### 用户端

- 登录
- 活动列表
- 活动详情
- 提交报名
- 我的报名
- 取消报名

### 管理端

- 仪表盘
- 用户管理
- 活动分类管理
- 活动管理
- 报名审核
- 签到管理
- 统计分析

## 核心状态设计

### 活动基础状态

- `draft`：草稿
- `published`：已发布
- `cancelled`：已取消

### 活动运行状态

- `signup_open`：报名中
- `signup_closed`：报名结束
- `ongoing`：进行中
- `finished`：已结束

### 报名状态

- `pending`：待审核
- `approved`：已通过
- `rejected`：已驳回
- `cancelled`：已取消

## 默认测试账号

- 管理员：`admin / 123456`
- 普通用户：`test01 / 123456`

初始化数据脚本见 `sql/02_huodongpai_seed.sql`。

## 本地启动

### 0. 环境变量模板

项目提供了环境变量模板：

```bash
cp .env.example .env
```

说明：

- `docker compose` 会自动读取根目录 `.env`
- 后端本地运行时也可以复用同一份 `.env`

### 1. 启动依赖

先准备：

- JDK 17
- Maven 3.9+
- Node.js 20+
- Docker / Docker Compose（仅用于启动 MySQL、Redis）

### 2. 启动 MySQL 和 Redis

```bash
cp .env.example .env
docker compose up -d
```

默认端口：

- MySQL：`127.0.0.1:3306`
- Redis：`127.0.0.1:6379`

### 3. 初始化数据库

首次执行 `docker compose up -d` 时，MySQL 会自动加载 `sql/` 目录下的初始化脚本：

- `sql/01_huodongpai_schema.sql`
- `sql/02_huodongpai_seed.sql`

以上脚本已显式指定 `utf8mb4` 客户端编码，避免初始化中文种子数据出现乱码。

如果你之前已经启动过 MySQL 且保留了数据卷，需要重置后再重新初始化：

```bash
docker compose down -v
docker compose up -d
```

### 4. 启动后端

```bash
mvn spring-boot:run
```

后端默认地址：

- `http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

- `http://localhost:5173`

## Docker Compose 启动本地依赖

项目的 Docker Compose 仅用于启动：

- MySQL
- Redis

### 启动命令

```bash
cp .env.example .env
docker compose up -d
```

### 停止命令

```bash
docker compose down
```

如需同时删除数据库与 Redis 数据卷：

```bash
docker compose down -v
```

## 环境变量说明

环境变量模板文件：

- `.env.example`

常用变量：

- `MYSQL_ROOT_PASSWORD`：MySQL root 密码
- `MYSQL_DATABASE`：初始化数据库名
- `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD`：后端数据库连接
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE` / `REDIS_PASSWORD`：Redis 连接
- `JWT_SECRET`：JWT 签名密钥
- `SERVER_PORT`：后端服务端口

## 容器说明

### MySQL

- 服务名：`mysql`
- 容器名：`huodongpai-mysql`
- 数据卷：`mysql-data`
- 初始化目录：`./sql`

### Redis

- 服务名：`redis`
- 容器名：`huodongpai-redis`
- 数据卷：`redis-data`

### Backend

- 本地通过 `mvn spring-boot:run` 启动
- 默认端口：`8080`

### Frontend

- 本地通过 `cd frontend && npm run dev` 启动
- 默认端口：`5173`

## 接口调试

已提供 Postman 文件：

- Collection：`postman/huodongpai.postman_collection.json`
- Environment：`postman/huodongpai.local.postman_environment.json`

使用说明见：

- `docs/postman-usage.md`

## 文档索引

- 数据库设计：`docs/schema-design.md`
- 初始化数据：`docs/init-data.md`
- Postman 使用说明：`docs/postman-usage.md`
- GitHub 仓库展示建议：`docs/github-repo-metadata.md`

## 当前实现范围

已完成：

- 后端核心业务接口
- 前端管理台与用户端页面
- Docker Compose 基础依赖编排
- Postman 调试集合

暂未实现：

- 二维码签到
- 短信/邮件通知
- 支付功能
- WebSocket 实时通知
- 多租户与分布式架构

## 快速体验建议

建议按以下顺序体验：

1. 管理员登录
2. 创建活动分类
3. 创建并发布活动
4. 普通用户登录并报名
5. 管理员审核报名
6. 管理员执行签到
7. 查看统计分析页
