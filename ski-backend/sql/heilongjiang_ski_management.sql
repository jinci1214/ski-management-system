-- 黑龙江滑雪场管理系统初始化脚本
-- MySQL 8.0，数据库表共 15 张

CREATE DATABASE IF NOT EXISTS heilongjiang_ski_management
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE heilongjiang_ski_management;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS safety_inspection;
DROP TABLE IF EXISTS maintenance_record;
DROP TABLE IF EXISTS facility;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS weather;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS ski_run;
DROP TABLE IF EXISTS coach;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS ski_resort;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 滑雪场信息表
CREATE TABLE ski_resort (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    city            VARCHAR(50) NOT NULL,
    address         VARCHAR(200) NOT NULL,
    phone           VARCHAR(20),
    open_time       TIME,
    close_time      TIME,
    introduction    TEXT,
    image           VARCHAR(500),
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '0停业 1营业',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='滑雪场基本信息';

-- 2. 游客表
CREATE TABLE `user` (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    name            VARCHAR(50),
    phone           VARCHAR(20),
    avatar          VARCHAR(500) COMMENT '游客头像地址',
    username_update_time DATETIME COMMENT '上次修改用户名时间，每月可修改一次',
    password_update_time DATETIME COMMENT '上次修改密码时间，每月可修改一次',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='游客账号';

-- 3. 员工表
CREATE TABLE employee (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    name            VARCHAR(50) NOT NULL,
    phone           VARCHAR(20),
    position        VARCHAR(50),
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='管理人员和工作人员';

-- 4. 教练表
CREATE TABLE coach (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(50) NOT NULL,
    gender              VARCHAR(10),
    phone               VARCHAR(20),
    level               VARCHAR(30) NOT NULL COMMENT '初级教练 中级教练 高级教练',
    years_of_experience INT NOT NULL DEFAULT 0,
    certificate         VARCHAR(100),
    status              TINYINT NOT NULL DEFAULT 1 COMMENT '0停用 1可预约',
    introduction        VARCHAR(500),
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='滑雪教练';

-- 5. 雪道表
CREATE TABLE ski_run (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    resort_id       INT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    level           VARCHAR(20) NOT NULL COMMENT '初级 中级 高级 专家级',
    length          DECIMAL(10,2),
    height          DECIMAL(10,2),
    image           VARCHAR(500),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_run_resort FOREIGN KEY (resort_id) REFERENCES ski_resort(id)
) ENGINE=InnoDB COMMENT='滑雪雪道';

-- 6. 雪票表
CREATE TABLE ticket (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    ski_run_id      INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    hour            INT NOT NULL,
    description     VARCHAR(500),
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    CONSTRAINT fk_ticket_run FOREIGN KEY (ski_run_id) REFERENCES ski_run(id)
) ENGINE=InnoDB COMMENT='雪票产品';

-- 7. 雪具表
CREATE TABLE equipment (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    resort_id       INT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    price           DECIMAL(10,2) NOT NULL COMMENT '日租金',
    total           INT NOT NULL DEFAULT 0,
    remaining       INT NOT NULL DEFAULT 0,
    overtime_fee    DECIMAL(10,2) DEFAULT 0,
    deposit         DECIMAL(10,2) DEFAULT 0,
    CONSTRAINT fk_equipment_resort FOREIGN KEY (resort_id) REFERENCES ski_resort(id)
) ENGINE=InnoDB COMMENT='雪具库存';

-- 8. 课程表
CREATE TABLE courses (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(100) NOT NULL,
    content         VARCHAR(500),
    teacher         VARCHAR(50),
    coach_id        INT,
    ski_run_id      INT NOT NULL,
    number          INT NOT NULL DEFAULT 0 COMMENT '剩余名额',
    course_time     DATETIME NOT NULL,
    hours           INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    level           VARCHAR(20),
    CONSTRAINT fk_course_coach FOREIGN KEY (coach_id) REFERENCES coach(id) ON DELETE SET NULL,
    CONSTRAINT fk_course_run FOREIGN KEY (ski_run_id) REFERENCES ski_run(id)
) ENGINE=InnoDB COMMENT='滑雪教学课程';

-- 9. 公告表
CREATE TABLE announcements (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(150) NOT NULL,
    content         TEXT NOT NULL,
    category        VARCHAR(30),
    publish_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='系统公告';

-- 10. 天气雪况表
CREATE TABLE weather (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    resort_id           INT NOT NULL,
    date                DATE NOT NULL,
    temperature_min     DECIMAL(5,2),
    temperature_max     DECIMAL(5,2),
    weather             VARCHAR(20),
    wind                DECIMAL(5,2) COMMENT '风速m/s',
    snow                VARCHAR(30) COMMENT '新雪 压雪 硬雪',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weather_resort FOREIGN KEY (resort_id) REFERENCES ski_resort(id),
    UNIQUE KEY uk_resort_weather_date (resort_id, date)
) ENGINE=InnoDB COMMENT='天气和雪况';

-- 11. 订单表
CREATE TABLE orders (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    order_id        VARCHAR(40) NOT NULL UNIQUE,
    user_id         INT NOT NULL,
    order_type      VARCHAR(30) NOT NULL COMMENT '雪票 雪具租赁 课程',
    ticket_id       INT,
    equipment_id    INT,
    course_id       INT,
    amount          DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT '待支付',
    payment_method  VARCHAR(20),
    cancel_reason   VARCHAR(200),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time        DATETIME,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_order_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE SET NULL,
    CONSTRAINT fk_order_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE SET NULL,
    CONSTRAINT fk_order_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='游客订单';

-- 12. 基础设施表
CREATE TABLE facility (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    resort_id       INT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(30) NOT NULL COMMENT '缆车 造雪机 服务设施 救援设施',
    location        VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT '正常',
    last_check_time DATETIME,
    description     VARCHAR(500),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_facility_resort FOREIGN KEY (resort_id) REFERENCES ski_resort(id)
) ENGINE=InnoDB COMMENT='雪场基础设施';

-- 13. 维护记录表
CREATE TABLE maintenance_record (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    facility_id         INT NOT NULL,
    employee_id         INT NOT NULL,
    content             VARCHAR(500) NOT NULL,
    cost                DECIMAL(10,2) DEFAULT 0,
    maintenance_time    DATETIME NOT NULL,
    result              VARCHAR(200),
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_facility FOREIGN KEY (facility_id) REFERENCES facility(id),
    CONSTRAINT fk_maintenance_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB COMMENT='设施维护记录';

-- 14. 安全巡检表
CREATE TABLE safety_inspection (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    ski_run_id          INT NOT NULL,
    employee_id         INT NOT NULL,
    inspection_time     DATETIME NOT NULL,
    snow_condition      VARCHAR(50),
    safety_status       VARCHAR(20) NOT NULL COMMENT '安全 需处理 已关闭',
    problem             VARCHAR(500),
    action              VARCHAR(500),
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inspection_run FOREIGN KEY (ski_run_id) REFERENCES ski_run(id),
    CONSTRAINT fk_inspection_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB COMMENT='雪道安全巡检';

-- 15. 游客反馈表
CREATE TABLE feedback (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    user_id         INT NOT NULL,
    type            VARCHAR(30) NOT NULL COMMENT '服务评价 设施建议 安全问题 其他',
    content         VARCHAR(1000) NOT NULL,
    rating          TINYINT COMMENT '1至5分',
    reply           VARCHAR(1000),
    status          VARCHAR(20) NOT NULL DEFAULT '待处理',
    reply_read      TINYINT NOT NULL DEFAULT 1 COMMENT '管理员回复是否已被游客查看：0未读 1已读',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reply_time      DATETIME,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT ck_feedback_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 5)
) ENGINE=InnoDB COMMENT='游客评价与意见反馈';

-- 演示数据。员工初始密码为 123456；游客初始密码为 123456 的 MD5 值。
INSERT INTO ski_resort
(name, city, address, phone, open_time, close_time, introduction, status)
VALUES
('亚布力阳光度假村', '哈尔滨市', '尚志市亚布力滑雪旅游度假区', '0451-53458888', '08:00:00', '16:30:00', '黑龙江省代表性山地滑雪度假区。', 1),
('哈尔滨热雪奇迹', '哈尔滨市', '松北区世茂大道99号', '0451-58568888', '09:30:00', '21:00:00', '室内四季滑雪场，适合教学和大众体验。', 1);

INSERT INTO `user` (username, password, name, phone)
VALUES ('student', MD5('123456'), '演示游客', '13800000000');

INSERT INTO employee (username, password, name, phone, position, status)
VALUES
('admin', '123456', '系统管理员', '13900000000', '管理员', 1),
('safe01', '123456', '安全巡检员', '13900000001', '安全员', 1);

INSERT INTO coach
(name, gender, phone, level, years_of_experience, certificate, status, introduction)
VALUES
('李教练', '男', '13700000001', '高级教练', 8, '社会体育指导员（滑雪）', 1, '擅长双板初中级教学。'),
('王教练', '女', '13700000002', '中级教练', 5, '大众滑雪技术等级证书', 1, '擅长单板入门教学。');

INSERT INTO ski_run (resort_id, name, level, length, height)
VALUES
(1, '阳光初级道', '初级', 800.00, 120.00),
(1, '高山竞技道', '高级', 2700.00, 540.00),
(2, '室内欢乐雪道', '初级', 500.00, 80.00);

INSERT INTO ticket (name, ski_run_id, price, hour, description, status)
VALUES
('亚布力初级道4小时票', 1, 198.00, 4, '包含雪道和缆车使用。', 1),
('亚布力高级道全天票', 2, 398.00, 8, '仅建议有经验的游客购买。', 1),
('室内滑雪3小时票', 3, 258.00, 3, '四季可用，节假日价格以公告为准。', 1);

INSERT INTO equipment (resort_id, name, price, total, remaining, overtime_fee, deposit)
VALUES
(1, '双板基础套装', 80.00, 60, 60, 20.00, 300.00),
(1, '单板基础套装', 100.00, 40, 40, 25.00, 400.00),
(2, '儿童双板套装', 60.00, 30, 30, 15.00, 200.00);

INSERT INTO courses
(title, content, teacher, coach_id, ski_run_id, number, course_time, hours, price, level)
VALUES
('双板零基础入门课', '安全知识、穿脱雪板、犁式制动。', '李教练', 1, 1, 8, '2026-12-05 09:00:00', 2, 299.00, '初级'),
('单板入门体验课', '站姿、推坡、落叶飘。', '王教练', 2, 3, 6, '2026-12-06 14:00:00', 2, 329.00, '初级');

INSERT INTO announcements (title, content, category, publish_time)
VALUES ('2026—2027雪季营业安排', '亚布力雪场计划于十一月下旬开放，实际日期以天气和雪况为准。', '开放时间', NOW());

INSERT INTO weather (resort_id, date, temperature_min, temperature_max, weather, wind, snow)
VALUES
(1, '2026-12-05', -22.00, -12.00, '晴', 3.20, '压雪'),
(2, '2026-12-05', -6.00, -4.00, '室内', 0.00, '粉雪');

INSERT INTO facility (resort_id, name, type, location, status, last_check_time, description)
VALUES
(1, '一号高速缆车', '缆车', '初级道入口', '正常', NOW(), '连接游客中心与初级道顶部。'),
(1, 'A区造雪机', '造雪机', '初级道A区', '正常', NOW(), '用于雪季初期和雪面补充。'),
(2, '医务救援站', '救援设施', '雪场出口旁', '正常', NOW(), '提供基础急救和转运联络。');

INSERT INTO maintenance_record
(facility_id, employee_id, content, cost, maintenance_time, result)
VALUES (1, 2, '检查缆车制动与应急停止装置。', 0.00, NOW(), '运行正常');

INSERT INTO safety_inspection
(ski_run_id, employee_id, inspection_time, snow_condition, safety_status, problem, action)
VALUES (1, 2, NOW(), '压雪平整', '安全', '未发现明显安全隐患', '正常开放');

INSERT INTO feedback (user_id, type, content, rating, status)
VALUES (1, '服务评价', '教练讲解清楚，雪具领取流程可以再快一些。', 4, '待处理');

-- 补充演示数据：用于毕业设计的列表、统计、订单和运维功能展示。
-- 游客账号和新增员工账号的演示密码均为 123456。
START TRANSACTION;

INSERT INTO ski_resort
(name, city, address, phone, open_time, close_time, introduction, status)
VALUES
('帽儿山高山滑雪场', '哈尔滨市', '尚志市帽儿山镇大房子村', '0451-53309001', '08:00:00', '16:00:00', '依托山地林区建设，设有大众教学和进阶滑雪区。', 1),
('吉华长寿山滑雪场', '哈尔滨市', '宾县宾西镇长寿国家森林公园', '0451-53309002', '08:30:00', '16:30:00', '适合家庭客群和中初级滑雪者，配套救援与休息设施。', 1),
('平山神鹿滑雪场', '哈尔滨市', '阿城区平山镇', '0451-53309003', '08:30:00', '16:00:00', '以初级教学和团队冰雪活动为主，当前处于季节性检修状态。', 0),
('伊春梅花山滑雪场', '伊春市', '乌翠区梅花山地区', '0458-53309004', '08:00:00', '16:30:00', '林区特色滑雪场，提供高山滑雪、越野滑雪和冬季研学服务。', 1);

INSERT INTO `user` (username, password, name, phone)
VALUES
('linxue', MD5('123456'), '林雪', '13800000001'),
('zhangning', MD5('123456'), '张宁', '13800000002'),
('wangyu', MD5('123456'), '王宇', '13800000003'),
('chenxi', MD5('123456'), '陈曦', '13800000004'),
('liuyang', MD5('123456'), '刘洋', '13800000005');

INSERT INTO employee (username, password, name, phone, position, status)
VALUES
('operate01', '123456', '赵新宇', '13900000002', '运营管理员', 1),
('ticket01', '123456', '刘婷', '13900000003', '票务管理员', 1),
('equip01', '123456', '陈浩', '13900000004', '设备管理员', 1),
('rescue01', '123456', '朱凯', '13900000005', '安全管理员', 1),
('service01', '123456', '孙悦', '13900000006', '客服人员', 1);

INSERT INTO coach
(name, gender, phone, level, years_of_experience, certificate, status, introduction)
VALUES
('张教练', '男', '13700000003', '高级教练', 10, '国家级大众滑雪技术证书', 1, '擅长双板刻滑和中高级技术提升。'),
('赵教练', '女', '13700000004', '初级教练', 3, '社会体育指导员证书', 1, '擅长儿童双板入门与安全教学。'),
('刘教练', '男', '13700000005', '中级教练', 6, '单板滑雪技术等级证书', 1, '擅长单板转弯和公园基础教学。'),
('陈教练', '女', '13700000006', '高级教练', 9, '高山滑雪教练证书', 0, '擅长高山滑雪和赛事训练，当前暂停预约。');

INSERT INTO ski_run (resort_id, name, level, length, height)
VALUES
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山森林初级道', '初级', 950.00, 135.00),
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山云顶中级道', '中级', 1800.00, 360.00),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山家庭雪道', '初级', 720.00, 105.00),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山挑战道', '高级', 2100.00, 430.00),
((SELECT id FROM ski_resort WHERE name = '平山神鹿滑雪场' LIMIT 1), '平山教学雪道', '初级', 600.00, 82.00),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山林海雪道', '中级', 1650.00, 310.00),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山越野雪道', '专家级', 5000.00, 220.00);

INSERT INTO ticket (name, ski_run_id, price, hour, description, status)
VALUES
('帽儿山初级道半日票', (SELECT id FROM ski_run WHERE name = '帽儿山森林初级道' LIMIT 1), 188.00, 4, '含雪道、魔毯和基础保险。', 1),
('帽儿山中级道全天票', (SELECT id FROM ski_run WHERE name = '帽儿山云顶中级道' LIMIT 1), 328.00, 8, '适合已掌握基础转弯技术的滑雪者。', 1),
('长寿山亲子4小时票', (SELECT id FROM ski_run WHERE name = '长寿山家庭雪道' LIMIT 1), 168.00, 4, '家庭雪道和休息区套票。', 1),
('长寿山挑战道全天票', (SELECT id FROM ski_run WHERE name = '长寿山挑战道' LIMIT 1), 358.00, 8, '限有中高级滑雪经验的游客使用。', 1),
('平山教学道2小时票', (SELECT id FROM ski_run WHERE name = '平山教学雪道' LIMIT 1), 98.00, 2, '场地检修期间暂停售卖。', 0),
('梅花山林海全天票', (SELECT id FROM ski_run WHERE name = '梅花山林海雪道' LIMIT 1), 298.00, 8, '包含林海雪道和往返索道使用。', 1);

INSERT INTO equipment (resort_id, name, price, total, remaining, overtime_fee, deposit)
VALUES
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山双板进阶套装', 120.00, 35, 35, 30.00, 500.00),
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山护具套装', 45.00, 50, 49, 10.00, 150.00),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山单板套装', 110.00, 28, 28, 25.00, 450.00),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山儿童护具', 35.00, 40, 40, 8.00, 100.00),
((SELECT id FROM ski_resort WHERE name = '平山神鹿滑雪场' LIMIT 1), '平山双板教学套装', 70.00, 20, 0, 15.00, 260.00),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山越野滑雪套装', 130.00, 24, 22, 30.00, 520.00),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山保暖装备包', 40.00, 30, 30, 10.00, 120.00);

INSERT INTO courses
(title, content, teacher, coach_id, ski_run_id, number, course_time, hours, price, level)
VALUES
('帽儿山双板提高课', '平行式转弯、节奏控制和雪道安全。', '张教练', (SELECT id FROM coach WHERE name = '张教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '帽儿山云顶中级道' LIMIT 1), 7, '2026-12-07 09:00:00', 3, 399.00, '中级'),
('帽儿山儿童入门课', '儿童雪地适应、犡式制动和安全停车。', '赵教练', (SELECT id FROM coach WHERE name = '赵教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '帽儿山森林初级道' LIMIT 1), 10, '2026-12-07 13:30:00', 2, 269.00, '初级'),
('长寿山单板转弯课', '落叶飘衔接、基础换刃和连续转弯。', '刘教练', (SELECT id FROM coach WHERE name = '刘教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '长寿山挑战道' LIMIT 1), 6, '2026-12-08 09:30:00', 3, 429.00, '中级'),
('长寿山亲子体验课', '家庭安全知识、基础滑行和亲子互动练习。', '王教练', (SELECT id FROM coach WHERE name = '王教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '长寿山家庭雪道' LIMIT 1), 9, '2026-12-08 14:00:00', 2, 259.00, '初级'),
('平山滑雪安全基础课', '雪场规则、护具使用、摔倒保护和应急处理。', '李教练', (SELECT id FROM coach WHERE name = '李教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '平山教学雪道' LIMIT 1), 0, '2026-12-09 10:00:00', 2, 199.00, '初级'),
('梅花山林海进阶课', '复杂雪面通过、林海雪道路线选择和体能分配。', '张教练', (SELECT id FROM coach WHERE name = '张教练' LIMIT 1), (SELECT id FROM ski_run WHERE name = '梅花山林海雪道' LIMIT 1), 4, '2026-12-10 09:00:00', 4, 459.00, '高级');

INSERT INTO announcements (title, content, category, publish_time)
VALUES
('新雪季雪道安全须知', '请游客根据自身水平选择雪道，入场前检查护具并遵守雪场标识。', '雪道更新', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('景区交通接驳服务说明', '雪季周末将开通市区至主要雪场的预约接驳车，具体班次请关注后续公告。', '开放时间', DATE_SUB(NOW(), INTERVAL 4 DAY)),
('雪具租赁与归还流程', '租赁雪具需携带有效证件，使用结束后请在营业时间内完成归还。', '宣传', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('大学生冰雪体验周活动', '学生游客凭有效学生证可参与指定入门课程优惠活动。', '课程更新', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('平山雪场设备检修通知', '平山雪场正在进行造雪与输送设备年度检修，恢复营业时间将另行通知。', '开放时间', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO weather
(resort_id, date, temperature_min, temperature_max, weather, wind, snow)
VALUES
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '2026-12-05', -24.00, -14.00, '晴', 3.80, '压雪'),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '2026-12-05', -21.00, -11.00, '多云', 4.10, '硬雪'),
((SELECT id FROM ski_resort WHERE name = '平山神鹿滑雪场' LIMIT 1), '2026-12-05', -20.00, -10.00, '雪', 2.70, '新雪'),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '2026-12-05', -27.00, -16.00, '晴', 2.40, '粉雪'),
((SELECT id FROM ski_resort WHERE name = '亚布力阳光度假村' LIMIT 1), '2026-12-06', -23.00, -13.00, '多云', 3.60, '压雪'),
((SELECT id FROM ski_resort WHERE name = '哈尔滨热雪奇迹' LIMIT 1), '2026-12-06', -6.00, -4.00, '室内', 0.00, '粉雪'),
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '2026-12-06', -22.00, -12.00, '雪', 4.50, '新雪'),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '2026-12-06', -20.00, -9.00, '晴', 3.30, '压雪');

INSERT INTO facility
(resort_id, name, type, location, status, last_check_time, description)
VALUES
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山二号索道', '缆车', '中级道入口', '正常', DATE_SUB(NOW(), INTERVAL 2 DAY), '连接中心服务区与云顶中级道。'),
((SELECT id FROM ski_resort WHERE name = '帽儿山高山滑雪场' LIMIT 1), '帽儿山游客服务中心', '服务设施', '雪场主入口', '正常', DATE_SUB(NOW(), INTERVAL 1 DAY), '提供售票、咨询、寄存和失物招领服务。'),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山自动造雪区', '造雪机', '挑战道中段', '维护中', NOW(), '包含六台自动造雪机，当前正在更换喷头。'),
((SELECT id FROM ski_resort WHERE name = '吉华长寿山滑雪场' LIMIT 1), '长寿山巡逻救援站', '救援设施', '家庭雪道与挑战道交汇处', '正常', DATE_SUB(NOW(), INTERVAL 1 DAY), '配备雪地救援车、担架和基础急救设备。'),
((SELECT id FROM ski_resort WHERE name = '平山神鹿滑雪场' LIMIT 1), '平山初级道魔毯', '缆车', '教学雪道右侧', '停用', DATE_SUB(NOW(), INTERVAL 3 DAY), '检修期间暂停使用，完成验收后恢复。'),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山越野救援点', '救援设施', '越野雪道三公里处', '正常', DATE_SUB(NOW(), INTERVAL 2 DAY), '提供保暖、通信和应急返程服务。'),
((SELECT id FROM ski_resort WHERE name = '伊春梅花山滑雪场' LIMIT 1), '梅花山雪具服务大厅', '服务设施', '雪场综合服务楼一层', '正常', DATE_SUB(NOW(), INTERVAL 1 DAY), '提供雪具租赁、调试、维护和保管服务。');

INSERT INTO maintenance_record
(facility_id, employee_id, content, cost, maintenance_time, result)
VALUES
((SELECT id FROM facility WHERE name = '帽儿山二号索道' LIMIT 1), (SELECT id FROM employee WHERE username = 'equip01' LIMIT 1), '完成索道钢索张紧度和车厢门锁巡检。', 860.50, DATE_SUB(NOW(), INTERVAL 6 DAY), '参数正常，通过试运行。'),
((SELECT id FROM facility WHERE name = 'A区造雪机' LIMIT 1), (SELECT id FROM employee WHERE username = 'equip01' LIMIT 1), '清洗造雪机水路并更换两组喷嘴。', 1250.00, DATE_SUB(NOW(), INTERVAL 5 DAY), '出雪压力恢复正常。'),
((SELECT id FROM facility WHERE name = '长寿山自动造雪区' LIMIT 1), (SELECT id FROM employee WHERE username = 'equip01' LIMIT 1), '更换三台造雪机的高压喷头并检查电控箱。', 3200.00, DATE_SUB(NOW(), INTERVAL 4 DAY), '设备已启动，等待低温工况复检。'),
((SELECT id FROM facility WHERE name = '平山初级道魔毯' LIMIT 1), (SELECT id FROM employee WHERE username = 'equip01' LIMIT 1), '检查魔毯传动轴与急停开关，更换防护罩。', 680.00, DATE_SUB(NOW(), INTERVAL 3 DAY), '完成维修，待安全人员验收。'),
((SELECT id FROM facility WHERE name = '医务救援站' LIMIT 1), (SELECT id FROM employee WHERE username = 'rescue01' LIMIT 1), '清点急救物资并更换过期保暖用品。', 460.00, DATE_SUB(NOW(), INTERVAL 2 DAY), '物资齐全，通信设备正常。'),
((SELECT id FROM facility WHERE name = '梅花山雪具服务大厅' LIMIT 1), (SELECT id FROM employee WHERE username = 'equip01' LIMIT 1), '保养雪板绑定器调试台并校准扭矩工具。', 980.00, DATE_SUB(NOW(), INTERVAL 1 DAY), '调试台运行正常。');

INSERT INTO safety_inspection
(ski_run_id, employee_id, inspection_time, snow_condition, safety_status, problem, action)
VALUES
((SELECT id FROM ski_run WHERE name = '高山竞技道' LIMIT 1), (SELECT id FROM employee WHERE username = 'safe01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 6 DAY), '压雪平整，边缘雪墙稳定', '安全', '未发现明显隐患', '正常开放'),
((SELECT id FROM ski_run WHERE name = '帽儿山森林初级道' LIMIT 1), (SELECT id FROM employee WHERE username = 'rescue01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 5 DAY), '新雪后已完成压雪', '安全', '未发现明显隐患', '正常开放'),
((SELECT id FROM ski_run WHERE name = '帽儿山云顶中级道' LIMIT 1), (SELECT id FROM employee WHERE username = 'safe01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 4 DAY), '雪面偏硬，中段局部结冰', '需处理', '中段弯道警示牌不足', '已增设减速标志并安排人员值守'),
((SELECT id FROM ski_run WHERE name = '长寿山家庭雪道' LIMIT 1), (SELECT id FROM employee WHERE username = 'rescue01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 3 DAY), '压雪平整，雪质稳定', '安全', '未发现明显隐患', '正常开放'),
((SELECT id FROM ski_run WHERE name = '长寿山挑战道' LIMIT 1), (SELECT id FROM employee WHERE username = 'safe01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 2 DAY), '顶部横风较大，雪面结冰', '已关闭', '高段横风和结冰影响安全通行', '关闭雪道并安排压雪车整备'),
((SELECT id FROM ski_run WHERE name = '平山教学雪道' LIMIT 1), (SELECT id FROM employee WHERE username = 'rescue01' LIMIT 1), DATE_SUB(NOW(), INTERVAL 1 DAY), '场地处于检修期，无游客', '已关闭', '输送设备未完成验收', '入口设置硬隔离并暂停开放'),
((SELECT id FROM ski_run WHERE name = '梅花山林海雪道' LIMIT 1), (SELECT id FROM employee WHERE username = 'safe01' LIMIT 1), NOW(), '粉雪覆盖，雪层厚度达标', '安全', '未发现明显隐患', '正常开放');

INSERT INTO orders
(order_id, user_id, order_type, ticket_id, equipment_id, course_id, amount, status,
 payment_method, cancel_reason, create_time, end_time)
VALUES
('20260816000100000000000000000001', (SELECT id FROM `user` WHERE username = 'linxue' LIMIT 1), '雪票', (SELECT id FROM ticket WHERE name = '帽儿山初级道半日票' LIMIT 1), NULL, NULL, 188.00, '已支付', '微信', NULL, DATE_SUB(NOW(), INTERVAL 6 DAY), NULL),
('20260817000100000000000000000002', (SELECT id FROM `user` WHERE username = 'zhangning' LIMIT 1), '雪具租赁', NULL, (SELECT id FROM equipment WHERE name = '帽儿山双板进阶套装' LIMIT 1), NULL, 120.00, '已完成', '支付宝', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('20260818000100000000000000000003', (SELECT id FROM `user` WHERE username = 'wangyu' LIMIT 1), '课程', NULL, NULL, (SELECT id FROM courses WHERE title = '帽儿山双板提高课' LIMIT 1), 399.00, '已支付', '微信', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), NULL),
('20260819000100000000000000000004', (SELECT id FROM `user` WHERE username = 'chenxi' LIMIT 1), '雪票', (SELECT id FROM ticket WHERE name = '长寿山亲子4小时票' LIMIT 1), NULL, NULL, 168.00, '已取消', NULL, '行程临时调整', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('20260820000100000000000000000005', (SELECT id FROM `user` WHERE username = 'liuyang' LIMIT 1), '雪具租赁', NULL, (SELECT id FROM equipment WHERE name = '长寿山单板套装' LIMIT 1), NULL, 110.00, '已退款', '支付宝', '天气原因取消租赁', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('20260821000100000000000000000006', (SELECT id FROM `user` WHERE username = 'student' LIMIT 1), '雪票', (SELECT id FROM ticket WHERE name = '亚布力初级道4小时票' LIMIT 1), NULL, NULL, 198.00, '已支付', '现金', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
('20260822000100000000000000000007', (SELECT id FROM `user` WHERE username = 'linxue' LIMIT 1), '课程', NULL, NULL, (SELECT id FROM courses WHERE title = '长寿山亲子体验课' LIMIT 1), 259.00, '待支付', NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL),
('20260822000200000000000000000008', (SELECT id FROM `user` WHERE username = 'zhangning' LIMIT 1), '雪具租赁', NULL, (SELECT id FROM equipment WHERE name = '帽儿山护具套装' LIMIT 1), NULL, 45.00, '已支付', '微信', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL),
('20260822000300000000000000000009', (SELECT id FROM `user` WHERE username = 'wangyu' LIMIT 1), '雪票', (SELECT id FROM ticket WHERE name = '梅花山林海全天票' LIMIT 1), NULL, NULL, 298.00, '已完成', '支付宝', NULL, DATE_SUB(NOW(), INTERVAL 90 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('20260822000400000000000000000010', (SELECT id FROM `user` WHERE username = 'chenxi' LIMIT 1), '课程', NULL, NULL, (SELECT id FROM courses WHERE title = '梅花山林海进阶课' LIMIT 1), 459.00, '已支付', '微信', NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL);

INSERT INTO feedback
(user_id, type, content, rating, reply, status, create_time, reply_time)
VALUES
((SELECT id FROM `user` WHERE username = 'linxue' LIMIT 1), '服务评价', '帽儿山教练很耐心，入门练习安排得很清楚。', 5, '感谢您的认可，我们会将好评转达给教学团队。', '已回复', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
((SELECT id FROM `user` WHERE username = 'zhangning' LIMIT 1), '设施建议', '建议在雪具大厅增加更多手机充电柜。', 4, NULL, '待处理', DATE_SUB(NOW(), INTERVAL 5 DAY), NULL),
((SELECT id FROM `user` WHERE username = 'wangyu' LIMIT 1), '安全问题', '云顶中级道中段的减速提示不够醒目。', 3, '已增设减速标志和巡逻人员，感谢您及时反馈。', '已回复', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
((SELECT id FROM `user` WHERE username = 'chenxi' LIMIT 1), '其他', '接驳车司机服务很好，希望增加晚间返程班次。', 5, '感谢建议，运营人员正在评估周末增班方案。', '已回复', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM `user` WHERE username = 'liuyang' LIMIT 1), '服务评价', '雪具归还流程比较快，但高峰时段等候区座位偏少。', 4, NULL, '待处理', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
((SELECT id FROM `user` WHERE username = 'student' LIMIT 1), '设施建议', '建议在初级道入口增加实时等待时间提示屏。', 3, '建议已记录，后续将结合客流统计系统进行评估。', '已回复', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

COMMIT;
