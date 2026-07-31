-- ===============================================================
-- 迁移脚本：菜单表支持图标，菜单路径与前端路由对齐
-- 执行日期：2026-07-31
-- 说明：侧边栏改为数据库菜单驱动，需要 icon 字段；路径必须与前端路由一致
-- ===============================================================
USE property_management_system;

ALTER TABLE sys_menu ADD COLUMN icon VARCHAR(50) NULL COMMENT '菜单图标' AFTER menu_name;

UPDATE sys_menu SET icon = 'Setting'       WHERE id = 1;
UPDATE sys_menu SET icon = 'User'          WHERE id = 2;
UPDATE sys_menu SET icon = 'Avatar'        WHERE id = 3;
UPDATE sys_menu SET icon = 'Menu'          WHERE id = 4;
UPDATE sys_menu SET icon = 'Document'      WHERE id = 5;
UPDATE sys_menu SET icon = 'OfficeBuilding' WHERE id = 6;
UPDATE sys_menu SET icon = 'HomeFilled'    WHERE id = 7;
UPDATE sys_menu SET icon = 'House'         WHERE id = 8;
UPDATE sys_menu SET icon = 'UserFilled'    WHERE id = 9;
UPDATE sys_menu SET icon = 'Van'           WHERE id = 10;
UPDATE sys_menu SET icon = 'Money'         WHERE id = 11;
UPDATE sys_menu SET icon = 'List'          WHERE id = 12;
UPDATE sys_menu SET icon = 'Tickets'       WHERE id = 13;
UPDATE sys_menu SET icon = 'Bell'          WHERE id = 14;
UPDATE sys_menu SET icon = 'Tools'         WHERE id = 15;
UPDATE sys_menu SET icon = 'Ticket'        WHERE id = 16;
UPDATE sys_menu SET icon = 'ChatLineSquare' WHERE id = 17;
UPDATE sys_menu SET icon = 'ChatDotSquare' WHERE id = 18;
UPDATE sys_menu SET icon = 'Monitor'       WHERE id = 19;
UPDATE sys_menu SET icon = 'Collection'    WHERE id = 20;
UPDATE sys_menu SET icon = 'Cpu'           WHERE id = 21;
UPDATE sys_menu SET icon = 'Tools'         WHERE id = 22;
UPDATE sys_menu SET icon = 'Search'        WHERE id = 23;
UPDATE sys_menu SET icon = 'Calendar'      WHERE id = 24;
UPDATE sys_menu SET icon = 'Finished'      WHERE id = 25;
UPDATE sys_menu SET icon = 'DataAnalysis'  WHERE id = 26;
UPDATE sys_menu SET icon = 'PieChart'      WHERE id = 27;
UPDATE sys_menu SET icon = 'Notification'  WHERE id = 28;
UPDATE sys_menu SET icon = 'Document'      WHERE id = 29;

-- 路径与前端路由对齐
UPDATE sys_menu SET path = '/system/users'       WHERE id = 2;
UPDATE sys_menu SET path = '/system/roles'       WHERE id = 3;
UPDATE sys_menu SET path = '/system/menus'       WHERE id = 4;
UPDATE sys_menu SET path = '/system/operLogs'    WHERE id = 5;
UPDATE sys_menu SET path = '/community/buildings' WHERE id = 7;
UPDATE sys_menu SET path = '/community/houses'   WHERE id = 8;
UPDATE sys_menu SET path = '/community/owners'   WHERE id = 9;
UPDATE sys_menu SET path = '/community/parkings' WHERE id = 10;
UPDATE sys_menu SET path = '/fee/items'          WHERE id = 12;
UPDATE sys_menu SET path = '/fee/records'        WHERE id = 13;
UPDATE sys_menu SET path = '/fee/notices'        WHERE id = 14;
UPDATE sys_menu SET menu_name = '报修工单', path = '/repair/record', component = 'repair/index', menu_type = 1 WHERE id = 16;
UPDATE sys_menu SET path = '/complaint'          WHERE id = 18;
UPDATE sys_menu SET path = '/equipment/categories' WHERE id = 20;
UPDATE sys_menu SET path = '/equipment/equipments' WHERE id = 21;
UPDATE sys_menu SET path = '/equipment/maintenances' WHERE id = 22;
UPDATE sys_menu SET path = '/inspection/plans'   WHERE id = 24;
UPDATE sys_menu SET path = '/inspection/records' WHERE id = 25;
UPDATE sys_menu SET path = '/dashboard'          WHERE id = 27;
UPDATE sys_menu SET path = '/announcement'       WHERE id = 29;
