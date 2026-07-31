-- ===============================================================
-- 迁移脚本：公告支持预发布（定时发布）
-- 执行日期：2026-07-31
-- ===============================================================
USE property_management_system;

ALTER TABLE announcement
    ADD COLUMN scheduled_publish_time DATETIME NULL COMMENT '预发布时间' AFTER publish_time;
