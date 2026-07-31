-- ===============================================================
-- 物业管理系统 - 系统管理模块
-- ===============================================================

-- 1. 系统基础表
-- =====================================================================

-- 系统用户表
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
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL COMMENT '操作人ID',
    user_name VARCHAR(50) COMMENT '操作人账号(冗余)',
    oper_module VARCHAR(50) COMMENT '操作模块',
    oper_type VARCHAR(20) COMMENT '操作类型（新增/编辑/删除）',
    oper_ip VARCHAR(50) COMMENT '请求ip',
    oper_desc VARCHAR(500) COMMENT '操作描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oper_log_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '操作日志表';

-- 索引
CREATE INDEX idx_user_role_user ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role ON sys_user_role(role_id);
CREATE INDEX idx_role_menu_role ON sys_role_menu(role_id);
CREATE INDEX idx_role_menu_menu ON sys_role_menu(menu_id);
CREATE INDEX idx_oper_log_user ON sys_oper_log(user_id);
CREATE INDEX idx_oper_log_time ON sys_oper_log(create_time);

-- =====================================================================

-- ===============================================================
-- 初始数据
-- ===============================================================
-- 1. 系统角色
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES
(1, '超级管理员', 'admin',          '系统最高权限'),
(2, '物业管理员', 'property_admin',  '社区日常业务管理'),
(3, '业主',       'owner',           '个人线上服务'),
(4, '维修工',     'repair_worker',   '处理报修工单'),
(5, '巡检员',     'inspector',       '设备巡检与记录');

-- 2. 系统用户 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, status) VALUES
(1,  'root',      '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '系统管理员', '13800000001', 1),
(2,  'admin',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '王经理',     '13800000002', 1),
(3,  'zhouwei',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周伟',       '13900001101', 1),
(4, 'chenjie',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '陈姐',       '13900001202', 1),
(5, 'zhouan',    '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周安',       '13900001301', 1);

-- 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1,1),(2,2),(3,3),(4,4),(5,5);

-- 3. 菜单权限 (menu_type: 0目录 1菜单 2按钮)

-- 3.1 系统管理 (parent: 1)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(1,  0, '系统管理',   '/system',             NULL,                     NULL,                      0, 1, 1),
(2,  1, '用户管理',   '/system/user',        'system/user/index',      'system:user:list',        1, 1, 1),
(3,  1, '角色管理',   '/system/role',        'system/role/index',      'system:role:list',        1, 2, 1),
(4,  1, '菜单管理',   '/system/menu',        'system/menu/index',      'system:menu:list',        1, 3, 1),
(5,  1, '操作日志',   '/system/operlog',     'system/operlog/index',   'system:operLog:list',     1, 4, 1);

-- 3.2 社区管理 (parent: 6)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(6,  0, '社区管理',   '/community',          NULL,                     NULL,                      0, 2, 1),
(7,  6, '楼栋管理',   '/community/building', 'community/building/index','community:building:list', 1, 1, 1),
(8,  6, '房屋管理',   '/community/house',    'community/house/index',   'community:house:list',    1, 2, 1),
(9,  6, '业主管理',   '/community/owner',    'community/owner/index',   'community:owner:list',    1, 3, 1),
(10, 6, '车位管理',   '/community/parking',  'community/parking/index', 'community:parking:list',  1, 4, 1);

-- 3.3 收费管理 (parent: 11)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(11, 0, '收费管理',   '/fee',                NULL,                     NULL,                      0, 3, 1),
(12, 11,'收费项目',   '/fee/item',           'fee/item/index',         'fee:item:list',           1, 1, 1),
(13, 11,'收费记录',   '/fee/record',         'fee/record/index',       'fee:record:list',         1, 2, 1),
(14, 11,'收费通知',   '/fee/notice',         'fee/notice/index',       'fee:notice:list',         1, 3, 1);

-- 3.4 报修管理 (parent: 15)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(15, 0, '报修管理',   '/repair',             NULL,                     NULL,                      0, 4, 1),
(16, 15,'报修记录',   '/repair/record',      'repair/index',           'repair:record:list',      1, 1, 1);

-- 3.5 投诉建议 (parent: 17)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(17, 0, '投诉建议',   '/complaint',          NULL,                     NULL,                      0, 5, 1),
(18, 17,'投诉列表',   '/complaint/list',     'complaint/index',        'complaint:list:list',     1, 1, 1);

-- 3.6 设备管理 (parent: 19)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(19, 0, '设备管理',   '/equipment',          NULL,                     NULL,                      0, 6, 1),
(20, 19,'设备分类',   '/equipment/category', 'equipment/category/index','equipment:category:list', 1, 1, 1),
(21, 19,'设备台账',   '/equipment/list',     'equipment/equipment/index','equipment:list:list',     1, 2, 1),
(22, 19,'维保记录',   '/equipment/maintenance','equipment/maintenance/index','equipment:maintenance:list', 1, 3, 1);

-- 3.7 巡检管理 (parent: 23)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(23, 0, '巡检管理',   '/inspection',         NULL,                     NULL,                      0, 7, 1),
(24, 23,'巡检计划',   '/inspection/plan',    'inspection/plan/index',  'inspection:plan:list',    1, 1, 1),
(25, 23,'巡检记录',   '/inspection/record',  'inspection/record/index','inspection:record:list',  1, 2, 1);

-- 3.8 数据统计 (parent: 26)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(26, 0, '数据统计',   '/statistics',         NULL,                     NULL,                      0, 8, 1),
(27, 26,'统计面板',   '/statistics/dashboard','statistics/index',      'statistics:overview:list', 1, 1, 1);

-- 3.9 公告通知 (parent: 28)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(28, 0, '公告通知',   '/announcement',       NULL,                     NULL,                      0, 9, 1),
(29, 28,'公告列表',   '/announcement/list',  'announcement/index',     'announcement:list:list',   1, 1, 1);

-- 3.10 按钮权限

-- 用户管理按钮 (parent: 2)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(30, 2, '用户新增', '', '', 'system:user:add',      2, 1, 1),
(31, 2, '用户编辑', '', '', 'system:user:edit',     2, 2, 1),
(32, 2, '用户删除', '', '', 'system:user:delete',   2, 3, 1),
(33, 2, '重置密码', '', '', 'system:user:resetPwd', 2, 4, 1);

-- 角色管理按钮 (parent: 3)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(34, 3, '角色新增', '', '', 'system:role:add',         2, 1, 1),
(35, 3, '角色编辑', '', '', 'system:role:edit',        2, 2, 1),
(36, 3, '角色删除', '', '', 'system:role:delete',      2, 3, 1),
(37, 3, '分配菜单', '', '', 'system:role:assignMenus', 2, 4, 1);

-- 菜单管理按钮 (parent: 4)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(38, 4, '菜单新增', '', '', 'system:menu:add',    2, 1, 1),
(39, 4, '菜单编辑', '', '', 'system:menu:edit',   2, 2, 1),
(40, 4, '菜单删除', '', '', 'system:menu:delete', 2, 3, 1);

-- 操作日志按钮 (parent: 5)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(41, 5, '日志删除', '', '', 'system:operLog:delete', 2, 1, 1);

-- 楼栋管理按钮 (parent: 7)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(42, 7, '楼栋新增', '', '', 'community:building:add',    2, 1, 1),
(43, 7, '楼栋编辑', '', '', 'community:building:edit',   2, 2, 1),
(44, 7, '楼栋删除', '', '', 'community:building:delete', 2, 3, 1);

-- 房屋管理按钮 (parent: 8)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(45, 8, '房屋新增', '', '', 'community:house:add',    2, 1, 1),
(46, 8, '房屋编辑', '', '', 'community:house:edit',   2, 2, 1),
(47, 8, '房屋删除', '', '', 'community:house:delete', 2, 3, 1);

-- 业主管理按钮 (parent: 9)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(48, 9, '业主新增', '', '', 'community:owner:add',    2, 1, 1),
(49, 9, '业主编辑', '', '', 'community:owner:edit',   2, 2, 1),
(50, 9, '业主删除', '', '', 'community:owner:delete', 2, 3, 1);

-- 车位管理按钮 (parent: 10)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(51, 10,'车位新增', '', '', 'community:parking:add',    2, 1, 1),
(52, 10,'车位编辑', '', '', 'community:parking:edit',   2, 2, 1),
(53, 10,'车位删除', '', '', 'community:parking:delete', 2, 3, 1);

-- 收费项目按钮 (parent: 12)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(54, 12,'项目新增', '', '', 'fee:item:add',    2, 1, 1),
(55, 12,'项目编辑', '', '', 'fee:item:edit',   2, 2, 1),
(56, 12,'项目删除', '', '', 'fee:item:delete', 2, 3, 1);

-- 收费记录按钮 (parent: 13)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(57, 13,'生成账单', '', '', 'fee:record:add',  2, 1, 1),
(58, 13,'缴纳费用', '', '', 'fee:record:edit', 2, 2, 1);

-- 收费通知按钮 (parent: 14)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(59, 14,'通知新增', '', '', 'fee:notice:add',    2, 1, 1),
(60, 14,'通知编辑', '', '', 'fee:notice:edit',   2, 2, 1),
(61, 14,'通知删除', '', '', 'fee:notice:delete', 2, 3, 1),
(62, 14,'发送通知', '', '', 'fee:notice:send',   2, 4, 1);

-- 报修管理按钮 (parent: 16)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(63, 16,'报修新增', '', '', 'repair:record:add',      2, 1, 1),
(64, 16,'报修编辑', '', '', 'repair:record:edit',     2, 2, 1),
(65, 16,'报修删除', '', '', 'repair:record:delete',   2, 3, 1),
(66, 16,'处理报修', '', '', 'repair:record:process',  2, 4, 1),
(67, 16,'服务评价', '', '', 'repair:record:evaluate', 2, 5, 1);

-- 投诉建议按钮 (parent: 18)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(68, 18,'投诉新增', '', '', 'complaint:list:add',      2, 1, 1),
(69, 18,'投诉编辑', '', '', 'complaint:list:edit',     2, 2, 1),
(70, 18,'投诉删除', '', '', 'complaint:list:delete',   2, 3, 1),
(71, 18,'处理投诉', '', '', 'complaint:list:process',  2, 4, 1);

-- 设备分类按钮 (parent: 20)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(72, 20,'分类新增', '', '', 'equipment:category:add',    2, 1, 1),
(73, 20,'分类编辑', '', '', 'equipment:category:edit',   2, 2, 1),
(74, 20,'分类删除', '', '', 'equipment:category:delete', 2, 3, 1);

-- 设备台账按钮 (parent: 21)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(75, 21,'设备新增', '', '', 'equipment:list:add',    2, 1, 1),
(76, 21,'设备编辑', '', '', 'equipment:list:edit',   2, 2, 1),
(77, 21,'设备删除', '', '', 'equipment:list:delete', 2, 3, 1);

-- 维保记录按钮 (parent: 22)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(78, 22,'维保新增', '', '', 'equipment:maintenance:add',    2, 1, 1),
(79, 22,'维保编辑', '', '', 'equipment:maintenance:edit',   2, 2, 1),
(80, 22,'维保删除', '', '', 'equipment:maintenance:delete', 2, 3, 1);

-- 巡检计划按钮 (parent: 24)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(81, 24,'计划新增', '', '', 'inspection:plan:add',    2, 1, 1),
(82, 24,'计划编辑', '', '', 'inspection:plan:edit',   2, 2, 1),
(83, 24,'计划删除', '', '', 'inspection:plan:delete', 2, 3, 1);

-- 巡检记录按钮 (parent: 25)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(84, 25,'记录新增', '', '', 'inspection:record:add',    2, 1, 1),
(85, 25,'记录编辑', '', '', 'inspection:record:edit',   2, 2, 1),
(86, 25,'记录删除', '', '', 'inspection:record:delete', 2, 3, 1);

-- 公告通知按钮 (parent: 29)
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(87, 29,'公告新增', '', '', 'announcement:list:add',     2, 1, 1),
(88, 29,'公告编辑', '', '', 'announcement:list:edit',    2, 2, 1),
(89, 29,'公告删除', '', '', 'announcement:list:delete',  2, 3, 1),
(90, 29,'发布公告', '', '', 'announcement:list:publish', 2, 4, 1),
(91, 29,'撤回公告', '', '', 'announcement:list:revoke',  2, 5, 1),
(92, 29,'置顶公告', '', '', 'announcement:list:top',     2, 6, 1);

-- 4. 角色-菜单权限分配

-- 超级管理员 -> 全部菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;

-- 物业管理员 -> 全部菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, id FROM sys_menu;

-- 业主 -> 首页 + 收费 + 报修 + 投诉 + 公告 (含按钮)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 目录和菜单
(3,26),(3,27),  -- 数据统计/首页
(3,11),(3,12),(3,13),(3,14),  -- 收费管理
(3,15),(3,16),  -- 报修管理
(3,17),(3,18),  -- 投诉建议
(3,28),(3,29),  -- 公告通知
-- 按钮权限
(3,57),(3,58),  -- 收费：生成账单、缴纳费用
(3,63),(3,64),(3,67),  -- 报修：新增、编辑、评价
(3,68),(3,69);  -- 投诉：新增、编辑

-- 维修工 -> 首页 + 报修 + 设备
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 目录和菜单
(4,26),(4,27),  -- 数据统计/首页
(4,15),(4,16),  -- 报修管理
(4,19),(4,20),(4,21),(4,22),  -- 设备管理
-- 按钮权限
(4,64),(4,66),  -- 报修：编辑、处理
(4,75),(4,76),(4,78),(4,79);  -- 设备：新增、编辑；维保：新增、编辑

-- 巡检员 -> 首页 + 设备 + 巡检
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 目录和菜单
(5,26),(5,27),  -- 数据统计/首页
(5,19),(5,20),(5,21),(5,22),  -- 设备管理
(5,23),(5,24),(5,25),  -- 巡检管理
-- 按钮权限
(5,75),(5,76),(5,78),(5,79),  -- 设备：新增、编辑；维保：新增、编辑
(5,81),(5,82),(5,84),(5,85);  -- 巡检：计划新增/编辑、记录新增/编辑

SET FOREIGN_KEY_CHECKS = 1;

