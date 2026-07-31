-- ===============================================================
-- 迁移脚本：巡检记录增加填写人字段
-- 执行日期：2026-07-31
-- ===============================================================
USE property_management_system;

ALTER TABLE inspection_record
    ADD COLUMN filler_user_id BIGINT NULL COMMENT '填写人ID' AFTER inspector_user_id;
