-- ===============================================================
-- 迁移脚本：报修单支持系统代报（业主/房屋可空）
-- 执行日期：2026-07-31
-- 说明：管理员报修公共设施时可不选具体业主和房屋
-- ===============================================================
USE property_management_system;

ALTER TABLE repair_record MODIFY owner_id BIGINT NULL COMMENT '报修业主id，系统代报可为空';
ALTER TABLE repair_record MODIFY house_id BIGINT NULL COMMENT '报修房屋id，公共区域可为空';
