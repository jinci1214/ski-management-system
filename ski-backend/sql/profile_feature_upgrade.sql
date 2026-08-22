-- 已初始化数据库的升级脚本：为游客资料编辑功能增加字段
USE heilongjiang_ski_management;

ALTER TABLE `user`
    ADD COLUMN avatar VARCHAR(500) NULL COMMENT '游客头像地址' AFTER phone,
    ADD COLUMN username_update_time DATETIME NULL COMMENT '上次修改用户名时间，每月可修改一次' AFTER avatar,
    ADD COLUMN password_update_time DATETIME NULL COMMENT '上次修改密码时间，每月可修改一次' AFTER username_update_time;
