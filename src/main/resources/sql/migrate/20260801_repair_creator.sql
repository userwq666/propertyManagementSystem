-- ===============================================================
-- 迁移脚本：报修单记录创建人
-- 执行日期：2026-08-01
-- 说明：系统代报/管理员代报时需要展示管理员联系方式
-- ===============================================================
USE property_management_system;

ALTER TABLE repair_record
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建人ID' AFTER evaluate_time;
