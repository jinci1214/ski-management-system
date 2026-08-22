-- 已初始化数据库的升级脚本：记录游客是否已查看管理员回复
USE heilongjiang_ski_management;

ALTER TABLE feedback
    ADD COLUMN reply_read TINYINT NOT NULL DEFAULT 1 COMMENT '管理员回复是否已被游客查看：0未读 1已读' AFTER status;

-- 升级前已经存在的回复，默认按游客已阅读处理，避免出现误报红点。
UPDATE feedback SET reply_read = 1;
