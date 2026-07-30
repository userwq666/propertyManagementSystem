-- =====================================================================
-- 物业管理系统 - 初始数据
-- 小区：翠湖花园 | 密码统一：123456
-- =====================================================================
USE property_management_system;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1. 系统用户
-- =====================================================================
INSERT INTO sys_user (id, username, password, real_name, phone, user_type, status) VALUES
(1,  'root',      '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '系统管理员', '13800000001', 1, 1),
(2,  'admin',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '王经理',     '13800000002', 2, 1),
(3,  'zhaomin',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '赵敏',       '13800000003', 2, 1),
(4,  'zhouwei',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周伟',       '13900001101', 3, 1),
(5,  'chenli',    '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '陈丽',       '13900001102', 3, 1),
(6,  'suntao',    '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '孙涛',       '13900001103', 3, 1),
(7,  'huangfang', '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '黄芳',       '13900001104', 3, 1),
(8,  'wulei',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '吴磊',       '13900001105', 3, 1),
(9,  'xuyan',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '许燕',       '13900001106', 3, 1),
(10, 'mayun',     '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '马芸',       '13900001107', 3, 1),
(11, 'zhenghua',  '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '郑华',       '13900001108', 3, 1),
(12, 'liugong',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '刘工',       '13900001201', 4, 1),
(13, 'chenjie',   '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '陈姐',       '13900001202', 4, 1),
(14, 'zhouan',    '$2a$10$8d.4.nxvzJIPULjJMeLwquXdMVIyvUqXWyIiqmk1c7abGyNbdMWlG', '周安',       '13900001301', 5, 1);

-- =====================================================================
-- 2. 角色
-- =====================================================================
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES
(1, '超级管理员', 'admin',          '系统最高权限'),
(2, '物业管理员', 'property_admin',  '小区日常业务管理'),
(3, '业主',       'owner',           '个人线上服务'),
(4, '维修工',     'repair_worker',   '处理报修工单'),
(5, '巡检员',     'inspector',       '设备巡检与记录');

-- 用户-角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1,1),(2,2),(3,2),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3),(11,3),(12,4),(13,4),(14,5);

-- =====================================================================
-- 3. 菜单
-- =====================================================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(1,  0,  '系统管理',   '/system',             NULL,                          NULL,                      0, 1, 1),
(2,  1,  '用户管理',   '/system/user',        'system/user/index',           'system:user:list',        1, 1, 1),
(3,  1,  '角色管理',   '/system/role',        'system/role/index',           'system:role:list',        1, 2, 1),
(4,  1,  '菜单管理',   '/system/menu',        'system/menu/index',           'system:menu:list',        1, 3, 1),
(5,  1,  '操作日志',   '/system/operlog',     'system/operlog/index',        'system:operLog:list',     1, 4, 1),
(6,  0,  '小区管理',   '/community',          NULL,                          NULL,                      0, 2, 1),
(7,  6,  '楼栋管理',   '/community/building', 'community/building/index',    'community:building:list', 1, 1, 1),
(8,  6,  '房屋管理',   '/community/house',    'community/house/index',       'community:house:list',    1, 2, 1),
(9,  6,  '业主管理',   '/community/owner',    'community/owner/index',       'community:owner:list',    1, 3, 1),
(10, 6,  '车位管理',   '/community/parking',  'community/parking/index',     'community:parking:list',  1, 4, 1),
(11, 0,  '收费管理',   '/fee',                NULL,                          NULL,                      0, 3, 1),
(12, 11, '收费项目',   '/fee/item',           'fee/item/index',              'fee:item:list',           1, 1, 1),
(13, 11, '收费记录',   '/fee/record',         'fee/record/index',            'fee:record:list',         1, 2, 1),
(14, 11, '收费通知',   '/fee/notice',         'fee/notice/index',            'fee:notice:list',         1, 3, 1),
(15, 0,  '报修管理',   '/repair',             NULL,                          NULL,                      0, 4, 1),
(16, 15, '报修工单',   '/repair/record',      'repair/record/index',         'repair:record:list',      1, 1, 1),
(17, 0,  '投诉建议',   '/complaint',          NULL,                          NULL,                      0, 5, 1),
(18, 17, '投诉列表',   '/complaint/list',     'complaint/list/index',        'complaint:list:list',     1, 1, 1),
(19, 0,  '设备管理',   '/equipment',          NULL,                          NULL,                      0, 6, 1),
(20, 19, '设备分类',   '/equipment/category', 'equipment/category/index',    'equipment:category:list', 1, 1, 1),
(21, 19, '设备台账',   '/equipment/list',     'equipment/list/index',        'equipment:list:list',     1, 2, 1),
(22, 19, '维保记录',   '/equipment/maintenance','equipment/maintenance/index','equipment:maintenance:list', 1, 3, 1),
(23, 0,  '巡检管理',   '/inspection',         NULL,                          NULL,                      0, 7, 1),
(24, 23, '巡检计划',   '/inspection/plan',    'inspection/plan/index',       'inspection:plan:list',    1, 1, 1),
(25, 23, '巡检记录',   '/inspection/record',  'inspection/record/index',     'inspection:record:list',  1, 2, 1),
(26, 0,  '数据统计',   '/statistics',          NULL,                           NULL,                       0, 8, 1),
(27, 26, '统计面板',   '/statistics/dashboard', 'statistics/index',             'statistics:overview:list',  1, 1, 1),
(28, 0,  '公告通知',   '/announcement',         NULL,                           NULL,                       0, 9, 1),
(29, 28, '公告列表',   '/announcement',         'announcement/index',           'announcement:list:list',    1, 1, 1),
(30, 2, '用户新增', '', '', 'system:user:add', 2, 1, 1),
(31, 2, '用户编辑', '', '', 'system:user:edit', 2, 2, 1),
(32, 2, '用户删除', '', '', 'system:user:delete', 2, 3, 1),
(33, 2, '重置密码', '', '', 'system:user:resetPwd', 2, 4, 1),
(34, 3, '角色新增', '', '', 'system:role:add', 2, 1, 1),
(35, 3, '角色编辑', '', '', 'system:role:edit', 2, 2, 1),
(36, 3, '角色删除', '', '', 'system:role:delete', 2, 3, 1),
(37, 3, '分配菜单', '', '', 'system:role:assignMenus', 2, 4, 1),
(38, 4, '菜单新增', '', '', 'system:menu:add', 2, 1, 1),
(39, 4, '菜单编辑', '', '', 'system:menu:edit', 2, 2, 1),
(40, 4, '菜单删除', '', '', 'system:menu:delete', 2, 3, 1),
(41, 5, '日志删除', '', '', 'system:operLog:delete', 2, 1, 1),
(42, 7, '楼栋新增', '', '', 'community:building:add', 2, 1, 1),
(43, 7, '楼栋编辑', '', '', 'community:building:edit', 2, 2, 1),
(44, 7, '楼栋删除', '', '', 'community:building:delete', 2, 3, 1),
(45, 8, '房屋新增', '', '', 'community:house:add', 2, 1, 1),
(46, 8, '房屋编辑', '', '', 'community:house:edit', 2, 2, 1),
(47, 8, '房屋删除', '', '', 'community:house:delete', 2, 3, 1),
(48, 9, '业主新增', '', '', 'community:owner:add', 2, 1, 1),
(49, 9, '业主编辑', '', '', 'community:owner:edit', 2, 2, 1),
(50, 9, '业主删除', '', '', 'community:owner:delete', 2, 3, 1),
(51, 10, '车位新增', '', '', 'community:parking:add', 2, 1, 1),
(52, 10, '车位编辑', '', '', 'community:parking:edit', 2, 2, 1),
(53, 10, '车位删除', '', '', 'community:parking:delete', 2, 3, 1),
(54, 12, '项目新增', '', '', 'fee:item:add', 2, 1, 1),
(55, 12, '项目编辑', '', '', 'fee:item:edit', 2, 2, 1),
(56, 12, '项目删除', '', '', 'fee:item:delete', 2, 3, 1),
(57, 13, '生成账单', '', '', 'fee:record:add', 2, 1, 1),
(58, 13, '缴费', '', '', 'fee:record:edit', 2, 2, 1),
(59, 14, '通知新增', '', '', 'fee:notice:add', 2, 1, 1),
(60, 14, '通知编辑', '', '', 'fee:notice:edit', 2, 2, 1),
(61, 14, '通知删除', '', '', 'fee:notice:delete', 2, 3, 1),
(62, 14, '发送通知', '', '', 'fee:notice:send', 2, 4, 1),
(63, 16, '报修新增', '', '', 'repair:record:add', 2, 1, 1),
(64, 16, '报修编辑', '', '', 'repair:record:edit', 2, 2, 1),
(65, 16, '报修删除', '', '', 'repair:record:delete', 2, 3, 1),
(66, 16, '处理报修', '', '', 'repair:record:process', 2, 4, 1),
(67, 16, '评价', '', '', 'repair:record:evaluate', 2, 5, 1),
(68, 18, '投诉新增', '', '', 'complaint:list:add', 2, 1, 1),
(69, 18, '投诉编辑', '', '', 'complaint:list:edit', 2, 2, 1),
(70, 18, '投诉删除', '', '', 'complaint:list:delete', 2, 3, 1),
(71, 18, '处理投诉', '', '', 'complaint:list:process', 2, 4, 1),
(72, 20, '分类新增', '', '', 'equipment:category:add', 2, 1, 1),
(73, 20, '分类编辑', '', '', 'equipment:category:edit', 2, 2, 1),
(74, 20, '分类删除', '', '', 'equipment:category:delete', 2, 3, 1),
(75, 21, '设备新增', '', '', 'equipment:list:add', 2, 1, 1),
(76, 21, '设备编辑', '', '', 'equipment:list:edit', 2, 2, 1),
(77, 21, '设备删除', '', '', 'equipment:list:delete', 2, 3, 1),
(78, 22, '维保新增', '', '', 'equipment:maintenance:add', 2, 1, 1),
(79, 22, '维保编辑', '', '', 'equipment:maintenance:edit', 2, 2, 1),
(80, 22, '维保删除', '', '', 'equipment:maintenance:delete', 2, 3, 1),
(81, 24, '计划新增', '', '', 'inspection:plan:add', 2, 1, 1),
(82, 24, '计划编辑', '', '', 'inspection:plan:edit', 2, 2, 1),
(83, 24, '计划删除', '', '', 'inspection:plan:delete', 2, 3, 1),
(84, 25, '记录新增', '', '', 'inspection:record:add', 2, 1, 1),
(85, 25, '记录编辑', '', '', 'inspection:record:edit', 2, 2, 1),
(86, 25, '记录删除', '', '', 'inspection:record:delete', 2, 3, 1),
(87, 29, '公告新增', '', '', 'announcement:list:add', 2, 1, 1),
(88, 29, '公告编辑', '', '', 'announcement:list:edit', 2, 2, 1),
(89, 29, '公告删除', '', '', 'announcement:list:delete', 2, 3, 1),
(90, 29, '发布公告', '', '', 'announcement:list:publish', 2, 4, 1),
(91, 29, '撤回公告', '', '', 'announcement:list:revoke', 2, 5, 1),
(92, 29, '置顶公告', '', '', 'announcement:list:top', 2, 6, 1);

-- 角色-菜单
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, id FROM sys_menu;
-- 角色3（业主）：首页 + 收费 + 报修 + 投诉 + 公告
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3,26),(3,27),(3,11),(3,12),(3,13),(3,14),(3,15),(3,16),(3,17),(3,18),(3,28),(3,29);
-- 角色4（维修工）：首页 + 报修 + 设备台账
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (4,26),(4,27),(4,15),(4,16),(4,19),(4,20),(4,21);
-- 角色5（巡检员）：首页 + 设备 + 巡检
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (5,26),(5,27),(5,19),(5,20),(5,21),(5,22),(5,23),(5,24),(5,25);

-- =====================================================================
-- 4. 楼栋
-- =====================================================================
INSERT INTO community_building (id, building_no, floor_count, total_house, build_year, remark) VALUES
(1, 'A栋', 18, 72, 2021, '高层住宅，2梯4户'),
(2, 'B栋', 18, 72, 2021, '高层住宅，2梯4户'),
(3, 'C栋', 12, 48, 2022, '小高层，1梯4户'),
(4, 'D栋', 6,  24, 2022, '花园洋房，1梯2户');

-- =====================================================================
-- 5. 业主（owner_type: 1本人 2家属 3租客）
-- =====================================================================
INSERT INTO community_owner (id, user_id, name, phone, id_card, owner_type, status, remark) VALUES
(1, 4,  '周伟', '13900001101', '320102199005150011', 1, 1, 'A栋101'),
(2, 5,  '陈丽', '13900001102', '320102198812120022', 1, 1, 'A栋201'),
(3, 6,  '孙涛', '13900001103', '320102199208080033', 1, 1, 'B栋101'),
(4, 7,  '黄芳', '13900001104', '320102199511200044', 1, 1, 'B栋202'),
(5, 8,  '吴磊', '13900001105', '320102198703030055', 1, 1, 'C栋101'),
(6, 9,  '许燕', '13900001106', '320102199309090066', 3, 1, 'C栋201租客'),
(7, 10, '马芸', '13900001107', '320102199107070077', 1, 1, 'D栋101'),
(8, 11, '郑华', '13900001108', '320102198505050088', 1, 1, 'D栋201');

-- =====================================================================
-- 6. 房屋（house_status: 0空置 1已入住 2出租）
-- =====================================================================
INSERT INTO community_house (id, building_id, room_no, area, house_type, house_status, owner_id, remark) VALUES
(1,  1, '101',  89.50, '两室一厅', 1, 1,  '朝南'),
(2,  1, '102',  89.50, '两室一厅', 0, NULL,'空置'),
(3,  1, '201',  89.50, '两室一厅', 1, 2,  '朝南'),
(4,  1, '301', 120.00, '三室两厅', 1, NULL,'东边套'),
(5,  1, '501',  89.50, '两室一厅', 0, NULL,'待售'),
(6,  2, '101',  89.50, '两室一厅', 1, 3,  '朝南'),
(7,  2, '102',  89.50, '两室一厅', 0, NULL,'待租'),
(8,  2, '202',  89.50, '两室一厅', 1, 4,  '朝南'),
(9,  2, '303', 120.00, '三室两厅', 1, NULL,'西边套'),
(10, 3, '101',  95.00, '两室一厅', 1, 5,  '朝南'),
(11, 3, '201',  95.00, '两室一厅', 2, 6,  '出租给许燕'),
(12, 3, '301', 130.00, '三室两厅', 1, NULL,'顶楼复式'),
(13, 4, '101', 145.00, '三室两厅', 1, 7,  '带花园'),
(14, 4, '201', 145.00, '三室两厅', 1, 8,  ''),
(15, 4, '301', 145.00, '三室两厅', 0, NULL,'待售');

-- =====================================================================
-- 7. 车位（parking_type: 1地上 2地下; status: 0空闲 1已租 2已售 3维修中）
-- =====================================================================
INSERT INTO community_parking (id, parking_no, parking_type, rent_price, sell_price, status, owner_id, remark) VALUES
(1, 'B1-A01', 2, 300.00, 150000.00, 2, 1,  'A栋地库已售'),
(2, 'B1-A02', 2, 300.00, 150000.00, 1, 2,  'A栋地库已租'),
(3, 'B1-B01', 2, 300.00, 150000.00, 2, 3,  'B栋地库已售'),
(4, 'B1-B02', 2, 300.00, 150000.00, 2, 4,  'B栋地库已售'),
(5, 'B1-C01', 2, 300.00, 140000.00, 2, 5,  'C栋地库已售'),
(6, 'B1-C02', 2, 300.00, 140000.00, 0, NULL,'C栋地库空闲'),
(7, 'G-D01',  1, 200.00, 120000.00, 2, 7,  'D栋地面已售'),
(8, 'G-D02',  1, 200.00, 120000.00, 2, 8,  'D栋地面已售');

-- =====================================================================
-- 8. 收费项目（item_type: 1物业费 2车位费 3水费 4电费 5燃气 6暖气 9其他; cycle_type: 1月 2季 3半年 4年 5一次性）
-- =====================================================================
INSERT INTO fee_item (id, item_name, item_type, unit_price, unit, cycle_type, description, status) VALUES
(1, '物业费',       1,   2.50, '元/㎡',    1, '按建筑面积每月收取',  1),
(2, '水费',         3,   3.80, '元/吨',    1, '代收代缴',            1),
(3, '电费',         4,   0.60, '元/度',    1, '代收代缴',            1),
(4, '地下车位费',   2, 300.00, '元/月',    1, '地下车位月租',        1),
(5, '垃圾处理费',   9,  10.00, '元/户',    1, '市政统一标准',        1),
(6, '公共维修基金', 1,   1.00, '元/㎡',    2, '按季度按面积收取',    1);

-- =====================================================================
-- 9. 收费记录（status: 0未缴费 1部分缴费 2已缴费 3逾期 4作废; pay_type: 1现金 2微信 3支付宝 4银行卡 5转账）
-- =====================================================================
INSERT INTO fee_record (id, fee_no, owner_id, house_id, item_id, amount, paid_amount, discount_amount, status, pay_type, pay_time, start_date, end_date, remark) VALUES
(1,  'FR202607001', 1, 1,  1, 223.75, 223.75, 0, 2, 2, '2026-07-05 10:30:00', '2026-07-01', '2026-07-31', '微信'),
(2,  'FR202607002', 2, 3,  1, 223.75, 223.75, 0, 2, 3, '2026-07-03 14:20:00', '2026-07-01', '2026-07-31', '支付宝'),
(3,  'FR202607003', 2, 3,  2,  45.60,  45.60, 0, 2, 3, '2026-07-03 14:20:00', '2026-07-01', '2026-07-31', '用水12吨'),
(4,  'FR202607004', 5, 10, 1, 237.50, 237.50, 0, 2, 1, '2026-07-08 09:15:00', '2026-07-01', '2026-07-31', '现金'),
(5,  'FR202607005', 5, 10, 4, 300.00, 300.00, 0, 2, 1, '2026-07-08 09:15:00', '2026-07-01', '2026-07-31', 'C栋车位'),
(6,  'FR202607006', 7, 13, 1, 362.50, 362.50, 0, 2, 3, '2026-07-06 16:00:00', '2026-07-01', '2026-07-31', '支付宝'),
(7,  'FR202607007', 8, 14, 1, 362.50, 200.00, 0, 1, 2, '2026-07-10 11:00:00', '2026-07-01', '2026-07-31', '余款待补缴'),
(8,  'FR202607008', 3, 6,  1, 223.75,   0.00, 0, 0, NULL, NULL,                '2026-07-01', '2026-07-31', ''),
(9,  'FR202607009', 4, 8,  1, 223.75,   0.00, 0, 3, NULL, NULL,                '2026-07-01', '2026-07-31', '已逾期'),
(10, 'FR202607010', 1, 1,  5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(11, 'FR202607011', 2, 3,  5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(12, 'FR202607012', 3, 6,  5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(13, 'FR202607013', 4, 8,  5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(14, 'FR202607014', 5, 10, 5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(15, 'FR202607015', 7, 13, 5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', ''),
(16, 'FR202607016', 8, 14, 5,  10.00,   0.00, 0, 0, NULL, NULL, '2026-07-01', '2026-07-31', '');

-- =====================================================================
-- 10. 收费通知（notice_type: 1缴费通知 2欠费催缴 3费率调整 4其他; send_scope: 1全体 2指定楼栋 3指定业主; send_status: 0草稿 1已发送 2发送失败）
-- =====================================================================
INSERT INTO fee_notice (id, notice_title, notice_content, notice_type, send_scope, send_status, send_time, creator_id) VALUES
(1, '2026年7月物业费缴纳通知', '尊敬的业主：7月物业费已生成，请在7月15日前完成缴纳。可通过物业前台或线上缴费。', 1, 1, 1, '2026-07-01 08:00:00', 2),
(2, '水费单价调整通知',       '根据市自来水公司通知，自2026年8月1日起，居民用水价格调整为4.05元/吨。',        3, 1, 1, '2026-07-20 10:00:00', 2);

-- =====================================================================
-- 11. 设备分类（仅 category_name, parent_id, sort, status）
-- =====================================================================
INSERT INTO equipment_category (id, category_name, parent_id, sort, status) VALUES
(1, '电梯系统', 0, 1, 1),
(2, '消防系统', 0, 2, 1),
(3, '安防监控', 0, 3, 1),
(4, '给排水',   0, 4, 1),
(5, '供配电',   0, 5, 1);

-- =====================================================================
-- 12. 设备（status: 1正常 2故障 3维修中 4停用 5报废）
-- =====================================================================
INSERT INTO equipment (id, equipment_no, equipment_name, category_id, brand, model, spec, location, building_id, floor, install_date, warranty_end_date, status, remark) VALUES
(1,  'EQ-E001', 'A栋1号客梯',  1, '奥的斯', 'Gen3',     '1000kg/13人', 'A栋1单元电梯井', 1, 'B1-18F', '2021-06-01', '2026-06-01', 1, ''),
(2,  'EQ-E002', 'A栋2号客梯',  1, '奥的斯', 'Gen3',     '1000kg/13人', 'A栋1单元电梯井', 1, 'B1-18F', '2021-06-01', '2026-06-01', 1, ''),
(3,  'EQ-E003', 'B栋1号客梯',  1, '奥的斯', 'Gen3',     '1000kg/13人', 'B栋1单元电梯井', 2, 'B1-18F', '2021-06-01', '2026-06-01', 1, ''),
(4,  'EQ-E004', 'C栋客梯',     1, '通力',   'MonoSpace','800kg/10人',  'C栋1单元电梯井', 3, 'B1-12F', '2022-03-01', '2027-03-01', 1, ''),
(5,  'EQ-F001', '消防泵组',     2, '东方',   'XBD8/30',  '30L/s 80m',   '地下车库消防泵房',NULL, 'B1',   '2021-05-01', '2026-05-01', 1, ''),
(6,  'EQ-F002', '消火栓系统',   2, '天广',   'SN65',     'DN65减压稳压','各楼层走廊',      NULL, 'ALL',  '2021-05-01', '2026-05-01', 1, '共32套'),
(7,  'EQ-S001', '门禁系统',     3, '海康',   'DS-K1T342','人脸识别',    '各单元出入口',    NULL, '1F',   '2021-07-01', '2026-07-01', 2, 'B栋读卡器故障'),
(8,  'EQ-S002', '监控系统',     3, '海康',   'DS-2CD2T47','400万像素',  '全小区公共区域',  NULL, 'ALL',  '2021-07-01', '2026-07-01', 1, '共48个点位'),
(9,  'EQ-W001', '生活水泵组',   4, '南方',   'CDL32-50',  '32m³/h 50m', '地下车库水泵房',  NULL, 'B1',   '2021-04-01', '2026-04-01', 1, ''),
(10, 'EQ-P001', '低压配电柜',   5, '正泰',   'GCK',       '2000A',       '配电间',          NULL, 'B1',   '2021-04-01', '2031-04-01', 1, '');

-- =====================================================================
-- 13. 维保记录（maintenance_type: 1日常巡检 2定期保养 3故障维修 4更换配件 5其他; status: 0待维护 1进行中 2已完成 3取消）
-- =====================================================================
INSERT INTO equipment_maintenance (id, equipment_id, maintenance_type, maintenance_content, maintenance_personnel_id, start_time, end_time, cost, parts_replaced, next_maintenance_date, status, remark) VALUES
(1, 1, 2, '半年例行保养：检查曳引机、钢丝绳、门机系统，更换导靴衬板',     NULL, '2026-06-15 09:00:00', '2026-06-15 16:00:00', 3500.00, '导靴衬板×4',       '2026-12-15', 2, ''),
(2, 5, 2, '年度检测：消防泵启动测试、压力校验、管路密封检查',             NULL, '2026-05-20 10:00:00', '2026-05-20 14:00:00', 1200.00, NULL,                '2026-11-20', 2, ''),
(3, 7, 3, 'B栋门禁读卡器故障维修：更换主板',                              NULL, '2026-07-10 14:00:00', NULL,                    800.00, '门禁主板×1',       NULL,         1, '待验收');

-- =====================================================================
-- 14. 巡检计划（plan_type: 1日常 2专项 3季节性 4临时; frequency_type: 1日 2周 3月 4季 5半年 6年 7一次性）
-- =====================================================================
INSERT INTO inspection_plan (id, plan_name, plan_type, frequency_type, frequency_value, start_date, status, creator_id) VALUES
(1, '电梯日检',     1, 1, '1',      '2026-01-01', 1, 2),
(2, '消防月检',     1, 3, '1',      '2026-01-01', 1, 2),
(3, '门禁周检',     1, 2, '1',      '2026-01-01', 1, 2),
(4, '水泵季度检',   1, 4, '1',      '2026-01-01', 1, 2),
(5, '配电柜月检',   1, 3, '1',      '2026-01-01', 1, 2);

INSERT INTO inspection_plan_equipment (plan_id, equipment_id) VALUES
(1,1),(1,2),(1,3),(1,4),(2,5),(2,6),(3,7),(3,8),(4,9),(5,10);

INSERT INTO inspection_plan_inspector (plan_id, inspector_id) VALUES
(1,14),(2,14),(3,14),(4,14),(5,14);

-- =====================================================================
-- 15. 巡检记录（status: 1正常 2异常 3未巡检; handle_status: 0待处理 1处理中 2已处理 3忽略）
-- =====================================================================
INSERT INTO inspection_record (id, plan_id, equipment_id, inspector_user_id, inspection_time, status, abnormal_desc, handle_status, handle_content, handle_time, handler_id, location_lat, location_lng, location_address) VALUES
(1, 1, 1, 14, '2026-07-15 08:30:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2304, 121.4737, 'A栋1单元电梯机房'),
(2, 1, 2, 14, '2026-07-15 08:50:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2304, 121.4737, 'A栋1单元电梯机房'),
(3, 1, 3, 14, '2026-07-15 09:10:00', 2, '曳引轮有异响，疑似磨损', 2, '已联系维保单位，计划更换曳引轮', '2026-07-15 14:00:00', 14, 31.2308, 121.4740, 'B栋1单元电梯机房'),
(4, 1, 4, 14, '2026-07-15 09:30:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2312, 121.4743, 'C栋1单元电梯机房'),
(5, 2, 5, 14, '2026-07-10 10:00:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2304, 121.4737, '地下车库消防泵房'),
(6, 2, 6, 14, '2026-07-10 11:00:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2304, 121.4737, 'A栋各楼层走廊'),
(7, 3, 7, 14, '2026-07-12 14:00:00', 1, NULL,   0, NULL,   NULL, NULL, 31.2304, 121.4737, '各单元门禁'),
(8, 3, 8, 14, '2026-07-12 15:00:00', 3, NULL,   0, NULL,   NULL, NULL, NULL,    NULL,     NULL);

-- =====================================================================
-- 16. 报修记录（repair_type: String枚举"水电"/"门窗"/"家电"/"公共设施"/"其他"; status: 0待派单 1处理中 2待评价 3已完成 4已取消; priority: 1普通 2加急 3紧急）
-- =====================================================================
INSERT INTO repair_record (id, repair_no, owner_id, house_id, repair_type, repair_content, status, priority, handler_id, handle_content, handle_time, evaluate_score, evaluate_content, evaluate_time) VALUES
(1, 'RR202607001', 1, 1,  '水电',     '厨房水龙头漏水，需更换密封圈',          3, 1, 12, '已更换密封圈，测试正常',         '2026-07-06 15:30:00', 5, '刘师傅很专业，态度好',       '2026-07-07 09:00:00'),
(2, 'RR202607002', 3, 6,  '家电',     '客厅灯具不亮，可能线路问题',            3, 1, 12, '灯管烧坏，已更换新灯管',         '2026-07-08 11:00:00', 4, '效率高',                     '2026-07-08 18:00:00'),
(3, 'RR202607003', 4, 8,  '门窗',     '卧室窗户关不严，有缝隙漏风',            1, 1, 12, NULL,                            NULL,                 NULL, NULL,                         NULL),
(4, 'RR202607004', 5, 10, '公共设施', '楼道声控灯不灵敏，需要检修',            0, 2, NULL, NULL,                           NULL,                 NULL, NULL,                         NULL),
(5, 'RR202607005', 2, 3,  '家电',     '空调制冷效果差，需加氟',                0, 1, NULL, NULL,                           NULL,                 NULL, NULL,                         NULL),
(6, 'RR202607006', 7, 13, '水电',     '卫生间马桶堵塞',                        4, 1, 13, NULL,                            NULL,                 NULL, NULL,                         NULL);

-- =====================================================================
-- 17. 投诉建议（type: 1投诉 2建议 3咨询 4表扬; status: 0待受理 1已受理 2处理中 3已回复 4已关闭 5已撤销; priority: 1普通 2重要 3紧急）
-- =====================================================================
INSERT INTO complaint_suggest (id, complaint_no, owner_id, house_id, type, category, content, status, priority, handler_id, handle_content, handle_time, is_anonymous) VALUES
(1, 'CP202607001', 3, 6,  1, '噪音扰民', 'A栋602业主深夜经常聚会噪音，严重影响休息。',            1, 1, 3,  '已联系602业主沟通，对方承诺注意。',      '2026-07-08 10:00:00', 0),
(2, 'CP202607002', 5, 10, 2, '设施建议', '小区快递越来越多，建议在门口增设丰巢快递柜。',           3, 2, 2,  '已向物业经理反馈，正在与丰巢洽谈合作。',  '2026-07-10 14:00:00', 0),
(3, 'CP202607003', 1, 1,  4, '服务态度', '保洁李阿姨工作认真负责，楼道总是干干净净，特此表扬！',    2, 1, 2,  '感谢您的肯定，已转达给保洁团队。',        '2026-07-05 16:00:00', 0),
(4, 'CP202607004', 8, 14, 1, '车辆管理', '我的D-002车位被外来车辆占用，请尽快处理。',              0, 2, NULL, NULL,                                     NULL,                 0);

-- =====================================================================
-- 18. 公告通知（type: 1通知 2政策 3便民 4活动 5紧急; publish_status: 0草稿 1发布 2下架）
-- =====================================================================
INSERT INTO announcement (id, title, content, type, is_top, top_expire_time, publish_status, publish_time, creator_id, view_count) VALUES
(1, '欢迎入住翠湖花园',          '尊敬的业主，欢迎入住翠湖花园！物业服务中心位于A栋1楼，服务热线：021-68888888。',                1, 1, '2026-12-31 23:59:59', 1, '2026-01-01 09:00:00', 2, 256),
(2, '关于规范电动车停放充电的通知','严禁电动车上楼充电，请统一停放至地下车库充电区。违者将依据《消防法》相关规定处理。',          5, 1, '2026-12-31 23:59:59', 1, '2026-03-01 09:00:00', 1, 445),
(3, '2026年度物业费优惠活动',     '即日起至8月31日，一次性缴纳全年物业费可享95折优惠，欢迎广大业主积极参与。',                      2, 0, NULL,                 1, '2026-07-01 09:00:00', 2, 178),
(4, '小区夏季灭蚊虫通知',        '为改善小区环境，将于7月20日至22日进行全区域灭蚊虫喷洒作业，届时请关好门窗，注意安全。',           1, 0, NULL,                 1, '2026-07-18 10:00:00', 3,  89),
(5, '小区健身器材更新完成',      '小区健身广场器材已全部更新完毕，欢迎广大业主前往使用，请注意安全、爱护公共设施。',                 3, 0, NULL,                 1, '2026-06-15 09:00:00', 2, 134),
(6, '社区夏日亲子运动会通知',    '拟于8月10日上午9时在小区中心广场举办夏日亲子运动会，欢迎各位业主携家人踊跃报名参加！',            4, 0, NULL,                 1, '2026-07-25 09:00:00', 3,  47);

-- =====================================================================
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
