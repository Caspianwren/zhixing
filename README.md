# 知行课堂（zhixing）

面向培训机构的课程学习平台：运营管课程和章节，学员选课、看章节、上报学习进度、给课程打分评价。

**这不是一个卖课系统。** 没有购物车、没有支付、没有订单状态机——那些是云座（预约系统）的重点。
这里的核心是「内容管理」和「学习行为记录」，两种项目刻意做成两个方向，避免简历上重复。

## 和云座项目的分工

| | 云座（自习室预约） | 知行课堂 |
|---|---|---|
| 定位 | 资源抢占 + 部署运维 | 内容与学习行为系统 |
| 核心对象 | 场地、时段、预约单 | 课程、章节、学习记录、评价 |
| 关键机制 | 唯一索引防重复预约、超时释放 | 进度只增不减、完成度聚合、评价唯一性 |
| 缓存 | Spring Cache 注解 | **RedisTemplate 手写**（自控 key/TTL/空值） |
| 部署 | Docker Compose 上云 | 本地运行 |

## 技术栈

Spring Boot 3.3.5 / Java 21 / MyBatis-Plus 3.5.7 / MySQL 8 / Redis / JWT

## 快速开始

1. **建库**（会重建表，旧版本遗留的 `course_order` 会被自动删除）

```bash
mysql -u root -p --default-character-set=utf8mb4 < sql/init.sql
```

2. **配密码**：改 `src/main/resources/application.yml` 的 `password`，或设环境变量 `DB_PASSWORD`

3. **启动**（端口 **8081**，避开云座的 8080）

```bash
mvn spring-boot:run
```

4. 浏览器打开 <http://localhost:8081>，用 `admin / 123456` 登录

## 账号

| 端 | 账号 | 密码 / 验证码 |
|---|---|---|
| 后台 | admin | 123456 |
| 学员 | 手机号 13900139000 | 验证码 1234（模拟短信） |

## 数据表（7 张）

`admin_user` 后台账号 / `student` 学员 / `course` 课程 / `chapter` 章节 /
`enrollment` 选课记录 / `study_record` 学习记录 / `course_review` 课程评价

三条唯一索引是关键：

- `enrollment(student_id, course_id)`：一人一课只有一条记录，退课再选是改状态，不新增行
- `study_record(student_id, chapter_id)`：一个章节只有一条进度
- `course_review(student_id, course_id)`：一人一课只能评一次

## 接口清单

### 后台 `/admin/**`（token 的 role 必须是 admin）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/admin/login` | 登录（免登录） |
| GET | `/admin/course/page` | 课程分页 |
| POST / PUT / DELETE | `/admin/course` | 课程增改删 |
| POST | `/admin/course/status/{status}` | 上架 / 下架 |
| GET | `/admin/chapter/list?courseId=` | 章节列表 |
| POST / PUT / DELETE | `/admin/chapter` | 章节增改删 |
| GET | `/admin/statistics` | 今日选课数 / 新增学员 / 评价数 / 平均分 |

### 学员端 `/student/**`（token 的 role 必须是 student）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/student/login` | 手机号 + 验证码 1234 |
| GET | `/student/course/list` | 只看上架课程 |
| GET | `/student/course/detail/{id}` | 课程详情 + 章节（有缓存） |
| POST | `/student/enrollment/{courseId}` | 选课 |
| DELETE | `/student/enrollment/{courseId}` | 退课 |
| GET | `/student/enrollment/my` | 我的课程 + 完成度 |
| POST | `/student/study/report` | 上报进度 `{courseId,chapterId,progressSeconds,finished}` |
| GET | `/student/study/progress/{courseId}` | 每章进度 |
| POST | `/student/review` | 评价 `{courseId,rating,content}` |
| GET | `/student/review/list?courseId=` | 课程评价列表 |
| GET | `/student/review/avg?courseId=` | 课程平均分 |

## 四个值得讲的实现

### 1. 选课不用订单

云座的下单要处理支付、状态流转、超时；这里选课只是一条报名记录，`status` 只有 1（有效）和 0（已退课）。
退课后重新选，是把同一行的状态改回 1 并刷新时间，**不会产生第二条记录**——唯一的索引保证了这一点。

### 2. 学习进度只增不减

```java
record.setProgressSeconds(Math.max(record.getProgressSeconds(), progress));
```

用户把进度条拖回去重看，不能把"已看时长"改小；`finished` 一旦置 1 也不会被改回 0。

### 3. 完成度是算出来的，不存

`myCourses()` 对每门已选课程实时算 `已学完章节 / 总章节 × 100`。
好处是不会出现"进度字段和实际记录对不上"，坏处是数据量大时要加缓存或定时预计算——面试可以主动说这个取舍。

### 4. 评价有两道闸

- 没选课不能评（`REVIEW_NOT_ALLOWED`）
- 已评过不能再评（`ALREADY_REVIEWED`，同时有唯一索引兜底）

平均分用「查出来求和 ÷ 条数」算，数据量大了应该换成 SQL 的 `avg()` 或缓存。

### 5. 缓存用 RedisTemplate 手写，不用 Spring Cache

云座用的是 `@Cacheable` 注解，这里刻意换一种做法，因为手写能控制更多东西：

```java
private static final String KEY_PREFIX = "school:course:detail:";
private static final Duration TTL = Duration.ofMinutes(30);      // 正常缓存 30 分钟
private static final Duration NULL_TTL = Duration.ofMinutes(5);  // 空值只缓存 5 分钟
```

**三个设计点**：

1. **空值缓存防穿透**：查不到的课程也写一个 `__NULL__` 标记（5 分钟），
   别人拿随机 id 刷接口时不会每次都打到数据库
2. **SCAN 而不是 KEYS 清理**：`KEYS *` 会阻塞 Redis，数据量大时是事故；
   这里用 `SCAN` 游标分批取，再一次性删除
3. **Redis 挂了自动降级**：读写异常只打 warn 日志，直接查库，主流程不受影响

实测：Redis 没启动时接口照常返回（日志里是「降级查库」），启动后 key 正常写入，TTL 1800 秒。

## 已验证流程（临时 MySQL 实例上真实跑过）

1. 后台登录、学员登录
2. 课程 3 门，初始已选 1 门（种子数据）
3. 选课 courseId=2 → 成功
4. 重复选课 → 拦截 `3002 你已经选过这门课`
5. 未选课上报进度 → 拦截 `3001 请先选课再学习`
6. 已选课上报进度 → 成功，已选变 2 门
7. 评价 → 成功
8. 重复评价 → 拦截 `3003 你已经评价过这门课`
9. 未选课评价 → 拦截 `3004 选课后才能评价`
10. 课程平均分返回 4.0
11. 退课 → 成功，已选回到 1 门
12. 今日数据：选课 2、新增学员 1、评价 2、平均分 4.5

## 没做的部分（面试要能说）

- 视频上传与转码：`video_url` 先留空
- 讲师角色与数据权限：现在只有 admin / student 两种角色
- 学习时长按天的统计报表：只有单门课的完成度
