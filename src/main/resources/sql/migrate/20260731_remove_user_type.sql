-- ===============================================================
-- 迁移脚本：移除 sys_user 表的 user_type 列
-- 执行日期：2026-07-31
-- 说明：userType 与角色冗余，统一用 sys_user_role 管理用户身份
-- ===============================================================
USE property_management_system;

ALTER TABLE sys_user DROP COLUMN user_type;
