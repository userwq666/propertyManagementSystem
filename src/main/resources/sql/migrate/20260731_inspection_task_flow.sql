-- ===============================================================
-- 迁移脚本：巡检记录支持接单流与异常转报修
-- 执行日期：2026-07-31
-- ===============================================================
USE property_management_system;

ALTER TABLE inspection_record
    ADD COLUMN task_status TINYINT NOT NULL DEFAULT 0 COMMENT '任务状态：0待接单 1已接单 2已填写' AFTER inspection_time,
    ADD COLUMN repair_record_id BIGINT NULL COMMENT '关联报修单ID，异常转报修后记录' AFTER status;
