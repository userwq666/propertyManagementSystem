-- ===============================================================
-- 物业管理系统 - 数据统计验证
-- ===============================================================

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'sys_user'               AS tbl, COUNT(*) AS cnt FROM sys_user               UNION ALL
SELECT 'sys_role',              COUNT(*) FROM sys_role                              UNION ALL
SELECT 'sys_user_role',         COUNT(*) FROM sys_user_role                         UNION ALL
SELECT 'sys_menu',              COUNT(*) FROM sys_menu                              UNION ALL
SELECT 'sys_role_menu',         COUNT(*) FROM sys_role_menu                         UNION ALL
SELECT 'community_building',    COUNT(*) FROM community_building                    UNION ALL
SELECT 'community_house',       COUNT(*) FROM community_house                       UNION ALL
SELECT 'community_owner',       COUNT(*) FROM community_owner                       UNION ALL
SELECT 'community_parking',     COUNT(*) FROM community_parking                     UNION ALL
SELECT 'fee_item',              COUNT(*) FROM fee_item                              UNION ALL
SELECT 'fee_record',            COUNT(*) FROM fee_record                            UNION ALL
SELECT 'fee_notice',            COUNT(*) FROM fee_notice                            UNION ALL
SELECT 'equipment_category',    COUNT(*) FROM equipment_category                    UNION ALL
SELECT 'equipment',             COUNT(*) FROM equipment                             UNION ALL
SELECT 'equipment_maintenance', COUNT(*) FROM equipment_maintenance                 UNION ALL
SELECT 'inspection_plan',       COUNT(*) FROM inspection_plan                       UNION ALL
SELECT 'inspection_record',     COUNT(*) FROM inspection_record                     UNION ALL
SELECT 'repair_record',         COUNT(*) FROM repair_record                         UNION ALL
SELECT 'complaint_suggest',     COUNT(*) FROM complaint_suggest                     UNION ALL
SELECT 'announcement',          COUNT(*) FROM announcement;
