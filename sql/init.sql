-- ============================================================
-- 在线学堂 建库建表 + 初始数据
-- 设计说明：这是"内容与学习行为"系统，不做交易
--   没有购物车、没有支付、没有订单状态机 —— 那些是外卖项目的重点
--   这里只有：课程 / 章节 / 选课记录 / 学习记录 / 评价
-- 用法：mysql -u root -p < init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS zhixing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE zhixing;

-- 旧版本（下单版）遗留的表，升级时顺手清掉
DROP TABLE IF EXISTS course_order;

-- ---------- 后台账号 ----------
DROP TABLE IF EXISTS admin_user;
CREATE TABLE admin_user
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    username    VARCHAR(32) NOT NULL,
    password    VARCHAR(64) NOT NULL COMMENT 'MD5',
    nickname    VARCHAR(32) DEFAULT NULL,
    status      INT         DEFAULT 1 COMMENT '1 启用 0 禁用',
    create_time DATETIME    DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT ='后台账号';

-- ---------- 学员 ----------
DROP TABLE IF EXISTS student;
CREATE TABLE student
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    phone       VARCHAR(11) NOT NULL,
    nickname    VARCHAR(32) DEFAULT NULL,
    avatar      VARCHAR(255) DEFAULT NULL,
    create_time DATETIME    DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_phone (phone)
) ENGINE = InnoDB COMMENT ='学员';

-- ---------- 课程 ----------
DROP TABLE IF EXISTS course;
CREATE TABLE course
(
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    title        VARCHAR(100)   NOT NULL,
    cover        VARCHAR(255)   DEFAULT NULL,
    intro        VARCHAR(500)   DEFAULT NULL,
    teacher_name VARCHAR(32)    DEFAULT NULL,
    price        DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '展示用，本项目不收款',
    status       INT            DEFAULT 1 COMMENT '1 上架 0 下架',
    create_time  DATETIME       DEFAULT NULL,
    update_time  DATETIME       DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_status (status)
) ENGINE = InnoDB COMMENT ='课程';

-- ---------- 章节 ----------
DROP TABLE IF EXISTS chapter;
CREATE TABLE chapter
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    sort        INT          DEFAULT 0,
    duration    INT          DEFAULT 0 COMMENT '时长（分钟）',
    video_url   VARCHAR(255) DEFAULT NULL,
    create_time DATETIME     DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_course (course_id)
) ENGINE = InnoDB COMMENT ='章节';

-- ---------- 选课记录 ----------
DROP TABLE IF EXISTS enrollment;
CREATE TABLE enrollment
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    student_id  BIGINT   NOT NULL,
    course_id   BIGINT   NOT NULL,
    status      INT      DEFAULT 1 COMMENT '1 有效 0 已退课',
    create_time DATETIME DEFAULT NULL,
    cancel_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    -- 一个学员一门课只有一条记录：退课再选是改状态，不是新增
    UNIQUE KEY uk_student_course (student_id, course_id),
    KEY idx_course (course_id)
) ENGINE = InnoDB COMMENT ='选课记录';

-- ---------- 学习记录 ----------
DROP TABLE IF EXISTS study_record;
CREATE TABLE study_record
(
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    student_id       BIGINT   NOT NULL,
    course_id        BIGINT   NOT NULL,
    chapter_id       BIGINT   NOT NULL,
    progress_seconds INT      DEFAULT 0 COMMENT '已看秒数',
    finished         TINYINT  DEFAULT 0 COMMENT '1 已学完',
    update_time      DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_chapter (student_id, chapter_id),
    KEY idx_course (course_id)
) ENGINE = InnoDB COMMENT ='学习记录';

-- ---------- 课程评价 ----------
DROP TABLE IF EXISTS course_review;
CREATE TABLE course_review
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    student_id  BIGINT       NOT NULL,
    rating      INT          NOT NULL COMMENT '1-5 星',
    content     VARCHAR(500) DEFAULT NULL,
    create_time DATETIME     DEFAULT NULL,
    PRIMARY KEY (id),
    -- 一人一课只能评一次
    UNIQUE KEY uk_student_course (student_id, course_id),
    KEY idx_course (course_id)
) ENGINE = InnoDB COMMENT ='课程评价';


-- ============================================================
-- 初始数据
-- ============================================================

-- 后台账号：admin / 123456
INSERT INTO admin_user (username, password, nickname, status, create_time)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '运营小王', 1, NOW());

INSERT INTO student (phone, nickname, create_time)
VALUES ('13900139000', '测试学员', NOW());

INSERT INTO course (title, cover, intro, teacher_name, price, status, create_time, update_time)
VALUES ('Java 基础入门', '/static/covers/java.jpg', '从语法到面向对象，适合零基础', '王老师', 99.00, 1, NOW(), NOW()),
       ('MySQL 实战', '/static/covers/mysql.jpg', '索引、事务、锁，配合真实业务表', '李老师', 129.00, 1, NOW(), NOW()),
       ('Spring Boot 快速开发', '/static/covers/boot.jpg', '从零写一个可上线的单体应用', '张老师', 199.00, 1, NOW(), NOW());

INSERT INTO chapter (course_id, title, sort, duration, video_url, create_time)
VALUES (1, '环境搭建与第一个程序', 1, 25, '', NOW()),
       (1, '变量与数据类型', 2, 30, '', NOW()),
       (1, '面向对象基础', 3, 40, '', NOW()),
       (2, '索引为什么能加快查询', 1, 35, '', NOW()),
       (2, '事务与隔离级别', 2, 45, '', NOW()),
       (3, '创建第一个 Spring Boot 工程', 1, 20, '', NOW()),
       (3, 'MyBatis-Plus 增删改查', 2, 50, '', NOW());

-- 预置一条选课和一条评价，方便启动后直接看效果
INSERT INTO enrollment (student_id, course_id, status, create_time)
VALUES (1, 1, 1, NOW());

INSERT INTO course_review (course_id, student_id, rating, content, create_time)
VALUES (1, 1, 5, '讲得清楚，适合入门', NOW());
