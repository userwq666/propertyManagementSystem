-- ===============================================================
-- 物业管理系统 - 系统管理模块
-- ===============================================================
-- 1. 系统基础表
-- =====================================================================
-- 系统用户表
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0禁用 1正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '系统用户表';
-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '权限标识',
    remark VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '角色表';
-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户id',
    role_id BIGINT NOT NULL COMMENT '角色id',
    UNIQUE KEY uk_user_role (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '用户角色关联表';
-- 菜单权限表
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单id',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    icon VARCHAR(50) COMMENT '菜单图标',
    path VARCHAR(255) COMMENT '前端路由',
    component VARCHAR(255) COMMENT '前端组件地址',
    perms VARCHAR(100) COMMENT '权限标识',
    menu_type TINYINT COMMENT '类型：0目录 1菜单 2按钮',
    sort INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '启用状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '菜单权限表';
-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色id',
    menu_id BIGINT NOT NULL COMMENT '菜单id',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '角色菜单关联表';
-- 操作日志表
-- =====================================================================
-- ===============================================================
-- 初始数据
-- ===============================================================
-- 1. 系统角色
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES
(1, '超级管理员', 'root',           '系统最高权限'),
(2, '物业管理员', 'property_admin',  '社区日常业务管理'),
(3, '业主',       'owner',           '个人线上服务'),
(4, '维修工',     'repair_worker',   '处理报修工单'),
(5, '巡检员',     'inspector',       '设备巡检与记录'),
(6, '财务',       'finance',         '负责收费记录管理');
-- 2. 系统用户 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, status) VALUES
(1,  'root',      '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '系统管理员', '13800000001', 1),
(2,  'admin',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '王经理',     '13800000002', 1),
(3,  'zhouwei',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周伟',       '13900001101', 1),
(4, 'chenjie',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '陈姐',       '13900001202', 1),
(5, 'zhouan',    '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周安',       '13900001301', 1),
(6, 'caiwu',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '财务',       '13900001401', 1);
-- 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),(6,6);
-- 3. 菜单权限 (menu_type: 0目录 1菜单 2按钮)
-- 以下菜单与权限点由系统菜单管理维护，固化当前分配
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (1,0,'系统管理','Setting','/system','','',0,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (2,1,'用户管理','User','/system/users','system/user/index','system:user:list',1,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (3,1,'角色管理','Avatar','/system/roles','system/role/index','system:role:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (4,1,'菜单管理','Menu','/system/menus','system/menu/index','system:menu:list',1,3,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (6,0,'社区管理','OfficeBuilding','/community','','',0,3,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (7,6,'楼栋管理','HomeFilled','/community/buildings','community/building/index','community:building:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (8,6,'房屋管理','House','/community/houses','community/house/index','community:house:list',1,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (9,6,'业主管理','UserFilled','/community/owners','community/owner/index','community:owner:list',1,3,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (10,6,'车位管理','Van','/community/parkings','community/parking/index','community:parking:list',1,4,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (11,0,'费用模块','Money','/fee','','',0,7,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (12,11,'收费项目','List','/fee/items','fee/item/index','fee:item:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (13,11,'收费记录','Tickets','/fee/records','fee/record/index','fee:record:list',1,3,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (14,11,'收费通知','Bell','/fee/notices','fee/notice/index','fee:notice:list',1,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (15,0,'报修模块','Tools','/repair','','',0,6,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (16,15,'报修工单','Ticket','/repair/record','repair/index','repair:record:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (17,0,'投诉建议','ChatLineSquare','/complaint','','',0,7,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (18,17,'投诉列表','ChatDotSquare','/complaint','complaint/index','complaint:list:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (19,0,'设备管理','Monitor','/equipment','','',0,4,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (20,19,'设备分类','Collection','/equipment/categories','equipment/category/index','equipment:category:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (21,19,'设备台账','Cpu','/equipment/equipments','equipment/equipment/index','equipment:list:list',1,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (22,19,'设备记录','Tools','/equipment/records','equipment/record/index','equipment:record:list',1,3,1,'2026-07-31 01:02:22','2026-08-01 00:37:26',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (23,0,'巡检管理','Search','/inspection','','',0,5,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (24,23,'巡检计划','Calendar','/inspection/plans','inspection/plan/index','inspection:plan:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (25,23,'巡检记录','Finished','/inspection/records','inspection/record/index','inspection:record:list',1,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (26,0,'数据统计','DataAnalysis','/statistics','','',0,8,1,'2026-07-31 01:02:22','2026-07-31 22:43:17',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (27,26,'统计面板','PieChart','/statistics','statistics/index','statistics:overview:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 22:43:17',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (28,0,'公告通知','Notification','/announcement','','',0,2,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (29,28,'公告列表','Document','/announcement','announcement/index','announcement:list:list',1,1,1,'2026-07-31 01:02:22','2026-07-31 21:35:03',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (30,2,'用户新增',NULL,'','','system:user:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (31,2,'用户编辑',NULL,'','','system:user:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (32,2,'用户删除',NULL,'','','system:user:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (34,3,'角色新增',NULL,'','','system:role:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (35,3,'角色编辑',NULL,'','','system:role:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (36,3,'角色删除',NULL,'','','system:role:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (38,4,'菜单新增',NULL,'','','system:menu:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (39,4,'菜单编辑',NULL,'','','system:menu:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (40,4,'菜单删除',NULL,'','','system:menu:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (42,7,'楼栋新增',NULL,'','','community:building:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (43,7,'楼栋编辑',NULL,'','','community:building:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (44,7,'楼栋删除',NULL,'','','community:building:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (45,8,'房屋新增',NULL,'','','community:house:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (46,8,'房屋编辑',NULL,'','','community:house:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (47,8,'房屋删除',NULL,'','','community:house:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (48,9,'业主新增',NULL,'','','community:owner:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (49,9,'业主编辑',NULL,'','','community:owner:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (50,9,'业主删除',NULL,'','','community:owner:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (51,10,'车位新增',NULL,'','','community:parking:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (52,10,'车位编辑',NULL,'','','community:parking:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (53,10,'车位删除',NULL,'','','community:parking:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (54,12,'项目新增',NULL,'','','fee:item:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (55,12,'项目编辑',NULL,'','','fee:item:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (56,12,'项目删除',NULL,'','','fee:item:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (57,13,'生成账单',NULL,'','','fee:record:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (58,13,'缴纳费用',NULL,'','','fee:record:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (59,14,'通知新增',NULL,'','','fee:notice:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (60,14,'通知编辑',NULL,'','','fee:notice:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (61,14,'通知删除',NULL,'','','fee:notice:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (63,16,'报修新增',NULL,'','','repair:record:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (64,16,'报修编辑',NULL,'','','repair:record:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (65,16,'报修删除',NULL,'','','repair:record:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (66,16,'处理报修',NULL,'','','repair:record:process',2,4,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (67,16,'服务评价',NULL,'','','repair:record:evaluate',2,5,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (68,18,'投诉新增',NULL,'','','complaint:list:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (69,18,'投诉编辑',NULL,'','','complaint:list:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (70,18,'投诉删除',NULL,'','','complaint:list:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (72,20,'分类新增',NULL,'','','equipment:category:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (73,20,'分类编辑',NULL,'','','equipment:category:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (74,20,'分类删除',NULL,'','','equipment:category:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (75,21,'设备新增',NULL,'','','equipment:list:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (76,21,'设备编辑',NULL,'','','equipment:list:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (77,21,'设备删除',NULL,'','','equipment:list:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (81,24,'计划新增',NULL,'','','inspection:plan:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (82,24,'计划编辑',NULL,'','','inspection:plan:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (83,24,'计划删除',NULL,'','','inspection:plan:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (84,25,'记录新增',NULL,'','','inspection:record:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (85,25,'记录编辑',NULL,'','','inspection:record:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (86,25,'记录删除',NULL,'','','inspection:record:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (87,29,'公告新增',NULL,'','','announcement:list:add',2,1,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (88,29,'公告编辑',NULL,'','','announcement:list:edit',2,2,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (89,29,'公告删除',NULL,'','','announcement:list:delete',2,3,1,'2026-07-31 01:02:22','2026-07-31 01:02:22',0);
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status, create_time, update_time, deleted) VALUES (94,16,'报修派单',NULL,'','','repair:record:assign',2,6,1,'2026-08-01 01:24:29','2026-08-01 01:24:29',0);
-- 4. 角色-菜单权限分配（固化当前分配）
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1,1,1);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (2,1,2);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3,1,3);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (4,1,4);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (6,1,6);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (7,1,7);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (8,1,8);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (9,1,9);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (10,1,10);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (11,1,11);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (12,1,12);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (13,1,13);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (14,1,14);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (15,1,15);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (16,1,16);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (17,1,17);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (18,1,18);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (19,1,19);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (20,1,20);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (21,1,21);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (22,1,22);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (23,1,23);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (24,1,24);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (25,1,25);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (26,1,26);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (27,1,27);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (28,1,28);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (29,1,29);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (30,1,30);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (31,1,31);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (32,1,32);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (34,1,34);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (35,1,35);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (36,1,36);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (38,1,38);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (39,1,39);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (40,1,40);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (42,1,42);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (43,1,43);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (44,1,44);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (45,1,45);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (46,1,46);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (47,1,47);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (48,1,48);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (49,1,49);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (50,1,50);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (51,1,51);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (52,1,52);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (53,1,53);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (54,1,54);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (55,1,55);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (56,1,56);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (57,1,57);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (58,1,58);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (59,1,59);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (60,1,60);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (61,1,61);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (63,1,63);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (64,1,64);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (65,1,65);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (66,1,66);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (67,1,67);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (68,1,68);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (69,1,69);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (70,1,70);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (72,1,72);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (73,1,73);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (74,1,74);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (75,1,75);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (76,1,76);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (77,1,77);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (81,1,81);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (82,1,82);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (83,1,83);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (84,1,84);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (85,1,85);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (86,1,86);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (87,1,87);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (88,1,88);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (89,1,89);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3382,1,94);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1935,2,1);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1849,2,2);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1936,2,3);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1854,2,4);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1868,2,6);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1869,2,7);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1873,2,8);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1877,2,9);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1881,2,10);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1920,2,11);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1921,2,12);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1925,2,13);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1928,2,14);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1907,2,15);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1908,2,16);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1914,2,17);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1915,2,18);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1885,2,19);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1886,2,20);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1890,2,21);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1894,2,22);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1898,2,23);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1899,2,24);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1903,2,25);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1933,2,26);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1934,2,27);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1860,2,28);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1861,2,29);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1850,2,30);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1851,2,31);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1852,2,32);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1847,2,34);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1848,2,35);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1855,2,38);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1856,2,39);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1857,2,40);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1870,2,42);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1871,2,43);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1872,2,44);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1874,2,45);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1875,2,46);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1876,2,47);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1878,2,48);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1879,2,49);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1880,2,50);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1882,2,51);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1883,2,52);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1884,2,53);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1922,2,54);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1923,2,55);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1924,2,56);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1926,2,57);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1927,2,58);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1929,2,59);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1930,2,60);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1931,2,61);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1909,2,63);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1910,2,64);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1911,2,65);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1912,2,66);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1913,2,67);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1916,2,68);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1917,2,69);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1918,2,70);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1887,2,72);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1888,2,73);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1889,2,74);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1891,2,75);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1892,2,76);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1893,2,77);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1900,2,81);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1901,2,82);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1902,2,83);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1904,2,84);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1905,2,85);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1906,2,86);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1862,2,87);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1863,2,88);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (1864,2,89);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3383,2,94);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3050,3,6);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3051,3,7);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3052,3,8);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3053,3,10);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3069,3,11);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3070,3,14);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3056,3,15);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3057,3,16);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3063,3,17);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3064,3,18);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3054,3,19);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3055,3,22);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3071,3,26);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3072,3,27);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3048,3,28);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3049,3,29);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3058,3,63);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3059,3,64);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3060,3,65);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3062,3,67);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3065,3,68);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3066,3,69);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3067,3,70);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3085,4,6);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3075,4,7);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3076,4,8);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3080,4,15);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3081,4,16);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3077,4,19);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3078,4,21);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3079,4,22);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3083,4,26);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3084,4,27);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3073,4,28);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3074,4,29);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3082,4,66);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3491,5,6);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3492,5,7);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3493,5,8);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3494,5,10);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3504,5,15);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3505,5,16);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3495,5,19);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3496,5,21);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3497,5,22);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3498,5,23);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3499,5,24);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3500,5,25);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3507,5,26);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3508,5,27);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3489,5,28);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3490,5,29);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3506,5,67);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3501,5,84);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3502,5,85);
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (3503,5,86);
-- 财务 -> 收费管理（项目查看、记录管理）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(6,11),(6,12),(6,13),  -- 收费管理目录、收费项目、收费记录
(6,57),(6,58);  -- 收费记录：生成账单、缴纳费用
-- 统计面板子权限（root/property_admin）
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status) VALUES
(95, 27, '费用统计', NULL, '', '', 'statistics:fee:list', 2, 1, 1),
(96, 27, '报修统计', NULL, '', '', 'statistics:repair:list', 2, 2, 1),
(97, 27, '设备统计', NULL, '', '', 'statistics:equipment:list', 2, 3, 1),
(98, 27, '投诉统计', NULL, '', '', 'statistics:complaint:list', 2, 4, 1),
(99, 27, '巡检统计', NULL, '', '', 'statistics:inspection:list', 2, 5, 1),
(105, 27, '人员统计', NULL, '', '', 'statistics:user:list', 2, 6, 1);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu WHERE id BETWEEN 95 AND 99;
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, id FROM sys_menu WHERE id BETWEEN 95 AND 99;
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1,105),(2,105);
-- 统计模块按角色开放
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(6,27),(6,95),(6,96),   -- 财务：总览+费用+维修(支出)
(4,96),                 -- 维修工：维修统计
(5,97),(5,99),          -- 巡检员：设备+巡检
(3,95);                 -- 业主：物业费收支
-- 消费事项（收费管理子菜单：管理员维护，财务/业主查看公示）
INSERT INTO sys_menu (id, parent_id, menu_name, icon, path, component, perms, menu_type, sort, status) VALUES
(100, 11, '消费事项', 'List', '/fee/expenses', 'fee/expense/index', 'fee:expense:list', 1, 4, 1),
(101, 100, '事项新增', NULL, '', '', 'fee:expense:add', 2, 1, 1),
(102, 100, '事项编辑', NULL, '', '', 'fee:expense:edit', 2, 2, 1),
(103, 100, '事项删除', NULL, '', '', 'fee:expense:delete', 2, 3, 1),
(104, 100, '事项审核', NULL, '', '', 'fee:expense:audit', 2, 4, 1);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu WHERE id BETWEEN 100 AND 104;
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, id FROM sys_menu WHERE id BETWEEN 100 AND 104;
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (6,100), (3,100);
-- 财务可审核消费事项
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (6,102),(6,104);
-- 财务查看收费记录需业主/房屋列表
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (6,8),(6,9);
-- 维修工可查看并编辑自己申报的消费事项
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (4,100),(4,102);
-- 业主可查看自己的缴费记录
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3,13);
-- 任何人均可投诉（维修工/巡检员分配投诉查看与新增）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (4,17),(4,18),(4,68),(5,17),(5,18),(5,68);
-- 物业管理员默认不开放系统角色/菜单管理，仅保留用户管理及对应按钮
DELETE FROM sys_role_menu WHERE role_id = 2 AND menu_id IN (3, 4, 34, 35, 36, 38, 39, 40);
SET FOREIGN_KEY_CHECKS = 1;
SET FOREIGN_KEY_CHECKS = 1;
