# 活动派数据库设计说明

## 1. 本版优化点

- 活动表只保存基础状态：`draft`、`published`、`cancelled`
- `signup_open`、`signup_closed`、`ongoing`、`finished` 通过时间字段实时推导
- 报名表对 `(event_id, user_id)` 做唯一约束，重新报名时复用原记录而不是新增历史行
- 新增 `event_signup_log` 保存报名操作日志，保证报名趋势统计口径准确
- 签到表采用“实际签到时创建记录”的方案，不预生成未签到数据
- 活动人数统计口径固定为：
  - `signup_count`：占用名额人数，统计 `pending + approved`
  - `approved_count`：审核通过人数
  - `checkin_count`：已签到人数

## 2. 为什么这样设计

### 2.1 活动状态避免冗余存储

如果把 `signup_open`、`signup_closed`、`ongoing`、`finished` 都直接落库，后续就需要额外定时任务或更新逻辑来维护状态流转，容易出现数据库状态和时间区间不一致的问题。

因此本版只把“人工可操作的基础状态”落库，运行态状态通过视图 `v_event_runtime_status` 或 Service 层规则实时计算。

### 2.2 报名唯一性更适合个人项目

为保证“一人一活动只有一条当前报名记录”，数据库直接约束 `(event_id, user_id)` 唯一。

这意味着：

- 首次报名：插入记录
- 已取消后重新报名：更新原记录为新状态，并刷新 `signup_time`
- 已驳回后重新报名：同样更新原记录，而不是新增第二条

这种方式更容易控制“重复报名”“释放名额”“签到唯一性”这三类核心规则。

### 2.3 报名趋势需要单独日志

由于 `event_signup` 只保留“当前有效报名记录”，如果用户发生“取消后重新报名”或“驳回后再次报名”，主表中的 `signup_time` 会被刷新，因此它不适合直接作为历史趋势统计来源。

因此本版额外增加 `event_signup_log`：

- 报名提交成功时写入一条 `apply` 日志
- 统计模块按日志表做“报名趋势”聚合
- 主表继续负责当前状态，日志表负责历史统计

### 2.4 签到只记录成功结果

签到表仅在真正签到时插入一条数据，因此：

- 不需要为每个已通过报名的人预生成一条“未签到”记录
- 查询未签到名单时，可以通过 `approved` 报名记录左连接签到表得到
- `event_checkin` 上的 `signup_id` 和 `(event_id, user_id)` 均做唯一约束，避免重复签到

## 3. 后续编码时要遵守的服务层规则

### 3.1 报名

- 仅当活动基础状态为 `published` 时允许进入运行态判断
- 运行态必须为 `signup_open`
- 报名人数判断使用 `signup_count < max_participants`
- 不需要审核：报名后写入 `approved`
- 需要审核：报名后写入 `pending`

### 3.2 取消报名

- 仅允许取消 `pending` 或 `approved` 的报名
- 取消后更新报名状态为 `cancelled`
- 同时回收活动 `signup_count`
- 如果取消前状态为 `approved`，还需回收 `approved_count`

### 3.3 审核

- `pending -> approved`：`approved_count + 1`
- `pending -> rejected`：`signup_count - 1`
- 驳回不会保留名额占用

### 3.4 签到

- 仅允许对 `approved` 状态报名执行签到
- 签到时插入 `event_checkin`
- 成功后更新活动 `checkin_count + 1`

## 4. 文件说明

- 建表脚本：`sql/01_huodongpai_schema.sql`
- 建议数据库版本：MySQL 8.0+
