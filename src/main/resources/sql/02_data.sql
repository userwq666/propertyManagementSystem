-- =====================================================================
-- 物业管理系统 - 初始数据插入脚本
-- 执行顺序：第 2 步（需先执行 01_schema.sql）
-- =====================================================================

USE property_management_system;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1. 系统基础数据
-- =====================================================================

-- 超级管理员（密码：123456 -> BCrypt加密）
INSERT INTO sys_user (username, password, real_name, phone, user_type, status) VALUES
('root', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800000000', 1, 1),
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '物业管理员', '13800000001', 2, 1),
('owner001', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', '13800000002', 3, 1),
('owner002', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', '13800000003', 3, 1),
('owner003', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', '13800000004', 3, 1),
('owner004', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', '13800000005', 3, 1),
('worker001', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '维修工王师傅', '13800000010', 2, 1),
('worker002', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '保洁员李阿姨', '13800000011', 2, 1),
('inspector001', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '巡检员张工', '13800000012', 2, 1);

-- 角色
INSERT INTO sys_role (role_name, role_key, remark) VALUES
('超级管理员', 'admin', '系统最高权限'),
('物业管理员', 'property_admin', '小区日常业务处理'),
('业主', 'owner', '个人线上服务'),
('维修工', 'repair_worker', '报修处理人员'),
('巡检员', 'inspector', '设备巡检人员');

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- root -> admin
(2, 2),  -- admin -> property_admin
(3, 3),  -- owner001 -> owner
(4, 3),  -- owner002 -> owner
(5, 3),  -- owner003 -> owner
(6, 3),  -- owner004 -> owner
(7, 4),  -- worker001 -> repair_worker
(8, 4),  -- worker002 -> repair_worker
(9, 5);  -- inspector001 -> inspector

-- 菜单
INSERT INTO sys_menu (parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(0, '系统管理', '/system', NULL, NULL, 0, 1, 1),
(1, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 1, 1, 1),
(1, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 1, 2, 1),
(1, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 1, 3, 1),
(1, '操作日志', '/system/operlog', 'system/operlog/index', 'system:operlog:list', 1, 4, 1),
(0, '小区管理', '/community', NULL, NULL, 0, 2, 1),
(6, '楼栋管理', '/community/building', 'community/building/index', 'community:building:list', 1, 1, 1),
(6, '房屋管理', '/community/house', 'community/house/index', 'community:house:list', 1, 2, 1),
(6, '业主管理', '/community/owner', 'community/owner/index', 'community:owner:list', 1, 3, 1),
(6, '车位管理', '/community/parking', 'community/parking/index', 'community:parking:list', 1, 4, 1),
(0, '收费管理', '/fee', NULL, NULL, 0, 3, 1),
(11, '收费项目', '/fee/item', 'fee/item/index', 'fee:item:list', 1, 1, 1),
(11, '收费记录', '/fee/record', 'fee/record/index', 'fee:record:list', 1, 2, 1),
(11, '收费通知', '/fee/notice', 'fee/notice/index', 'fee:notice:list', 1, 3, 1),
(0, '报修管理', '/repair', NULL, NULL, 0, 4, 1),
(15, '报修工单', '/repair/record', 'repair/record/index', 'repair:record:list', 1, 1, 1),
(0, '投诉建议', '/complaint', NULL, NULL, 0, 5, 1),
(17, '投诉列表', '/complaint/list', 'complaint/list/index', 'complaint:list:list', 1, 1, 1),
(0, '设备管理', '/equipment', NULL, NULL, 0, 6, 1),
(19, '设备分类', '/equipment/category', 'equipment/category/index', 'equipment:category:list', 1, 1, 1),
(19, '设备台账', '/equipment/list', 'equipment/list/index', 'equipment:list:list', 1, 2, 1),
(19, '维护记录', '/equipment/maintenance', 'equipment/maintenance/index', 'equipment:maintenance:list', 1, 3, 1),
(0, '巡检管理', '/inspection', NULL, NULL, 0, 7, 1),
(23, '巡检计划', '/inspection/plan', 'inspection/plan/index', 'inspection:plan:list', 1, 1, 1),
(23, '巡检记录', '/inspection/record', 'inspection/record/index', 'inspection:record:list', 1, 2, 1),
(0, '公告管理', '/announcement', NULL, NULL, 0, 8, 1),
(26, '公告列表', '/announcement/list', 'announcement/list/index', 'announcement:list:list', 1, 1, 1);

-- 角色菜单关联（超级管理员拥有所有权限）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 物业管理员权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
(2, 11), (2, 12), (2, 13),
(2, 15), (2, 16),
(2, 17), (2, 18),
(2, 19), (2, 20), (2, 21), (2, 22),
(2, 23), (2, 24), (2, 25),
(2, 26), (2, 27);

-- 业主权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3, 16), (3, 18);

-- =====================================================================
-- 2. 小区基础数据
-- =====================================================================

-- 楼栋
INSERT INTO community_building (building_no, floor_count, total_house, build_year, remark) VALUES
('1栋', 18, 72, 2020, '一期交付'),
('2栋', 18, 72, 2020, '一期交付'),
('3栋', 24, 96, 2021, '二期交付'),
('4栋', 24, 96, 2021, '二期交付'),
('5栋', 32, 128, 2022, '三期交付');

-- 房屋
INSERT INTO community_house (building_id, room_no, area, house_type, house_status) VALUES
(1, '1-101', 89.50, '两室一厅', 0),
(1, '1-102', 89.50, '两室一厅', 1),
(1, '2-101', 120.00, '三室两厅', 1),
(1, '2-102', 120.00, '三室两厅', 0),
(1, '3-101', 89.50, '两室一厅', 1),
(2, '1-101', 89.50, '两室一厅', 2),
(2, '1-102', 89.50, '两室一厅', 1),
(2, '2-101', 120.00, '三室两厅', 0),
(3, '1-101', 135.00, '三室两厅', 1),
(3, '1-102', 135.00, '三室两厅', 1),
(4, '1-101', 135.00, '三室两厅', 0),
(5, '1-101', 160.00, '四室两厅', 1);

-- 业主信息（关联用户）
INSERT INTO community_owner (user_id, name, phone, id_card, owner_type, status) VALUES
(3, '张三', '13800000002', '110101199001011234', 1, 1),
(4, '李四', '13800000003', '110101198502022345', 1, 1),
(5, '王五', '13800000004', '110101199203033456', 1, 1),
(6, '赵六', '13800000005', '110101198804044567', 1, 1),
-- 家属/租客
(NULL, '张小妹', '13800000006', '110101201001015678', 2, 1),
(NULL, '李小明', '13800000007', '110101201202026789', 3, 1);

-- 更新房屋业主关联
UPDATE community_house SET owner_id = 1 WHERE id = 2;  -- 1-102 -> 张三
UPDATE community_house SET owner_id = 2 WHERE id = 3;  -- 2-101 -> 李四
UPDATE community_house SET owner_id = 3 WHERE id = 5;  -- 3-101 -> 王五
UPDATE community_house SET owner_id = 4 WHERE id = 7;  -- 2栋1-102 -> 赵六
UPDATE community_house SET owner_id = 5 WHERE id = 10; -- 3栋1-102 -> 张小妹(家属)
UPDATE community_house SET owner_id = 6 WHERE id = 12; -- 5栋1-101 -> 李小明(租客)

-- 车位
INSERT INTO community_parking (parking_no, parking_type, status, owner_id, rent_price, sell_price) VALUES
('A-001', 1, 1, 1, 300.00, 120000.00),
('A-002', 1, 1, 2, 300.00, 120000.00),
('A-003', 1, 0, NULL, 300.00, 120000.00),
('B-001', 2, 1, 3, 200.00, 80000.00),
('B-002', 2, 0, NULL, 200.00, 80000.00),
('B-003', 2, 2, 4, 200.00, 80000.00);

-- =====================================================================
-- 3. 收费数据
-- =====================================================================

-- 收费项目
INSERT INTO fee_item (item_name, item_type, unit_price, unit, cycle_type, description, status) VALUES
('物业管理费', 1, 2.50, '元/㎡/月', 1, '住宅物业服务费', 1),
('地上车位费', 2, 300.00, '元/月', 1, '地上固定车位租赁费', 1),
('地下车位费', 2, 200.00, '元/月', 1, '地下固定车位租赁费', 1),
('水费', 3, 4.50, '元/吨', 1, '民用水费', 1),
('电费', 4, 0.60, '元/度', 1, '民用电费', 1),
('燃气费', 5, 3.00, '元/立方', 1, '民用燃气费', 1),
('暖气费', 6, 30.00, '元/㎡/采暖季', 4, '集中供暖费', 1);

-- 收费记录（模拟2024年1-6月数据）
INSERT INTO fee_record (fee_no, owner_id, house_id, item_id, amount, paid_amount, discount_amount, status, pay_type, pay_time, start_date, end_date) VALUES
-- 物业费
('FEE202401001', 1, 2, 1, 223.75, 223.75, 0, 2, 2, '2024-01-05 10:30:00', '2024-01-01', '2024-01-31'),
('FEE202401002', 2, 3, 1, 300.00, 300.00, 0, 2, 3, '2024-01-06 14:20:00', '2024-01-01', '2024-01-31'),
('FEE202401003', 3, 5, 1, 223.75, 0, 0, 3, NULL, NULL, '2024-01-01', '2024-01-31'),
('FEE202401004', 4, 7, 1, 223.75, 223.75, 0, 2, 2, '2024-01-08 09:15:00', '2024-01-01', '2024-01-31'),
('FEE202402001', 1, 2, 1, 223.75, 223.75, 0, 2, 2, '2024-02-05 11:00:00', '2024-02-01', '2024-02-29'),
('FEE202402002', 2, 3, 1, 300.00, 300.00, 0, 2, 2, '2024-02-06 15:30:00', '2024-02-01', '2024-02-29'),
('FEE202402003', 3, 5, 1, 223.75, 223.75, 0, 2, 3, '2024-02-10 10:00:00', '2024-02-01', '2024-02-29'),
('FEE202402004', 4, 7, 1, 223.75, 0, 0, 3, NULL, NULL, '2024-02-01', '2024-02-29'),
-- 车位费
('FEE202401010', 1, 2, 2, 300.00, 300.00, 0, 2, 2, '2024-01-05 10:30:00', '2024-01-01', '2024-01-31'),
('FEE202401011', 2, 3, 2, 300.00, 300.00, 0, 2, 3, '2024-01-06 14:20:00', '2024-01-01', '2024-01-31'),
('FEE202401012', 3, 10, 3, 200.00, 200.00, 0, 2, 2, '2024-01-08 09:15:00', '2024-01-01', '2024-01-31'),
('FEE202401013', 4, 12, 3, 200.00, 0, 0, 3, NULL, NULL, '2024-01-01', '2024-01-31'),
-- 水电费
('FEE202401020', 1, 2, 4, 45.00, 45.00, 0, 2, 2, '2024-01-10 16:00:00', '2024-01-01', '2024-01-31'),
('FEE202401021', 1, 2, 5, 120.00, 120.00, 0, 2, 2, '2024-01-10 16:05:00', '2024-01-01', '2024-01-31'),
('FEE202401022', 2, 3, 4, 58.50, 58.50, 0, 2, 3, '2024-01-11 10:00:00', '2024-01-01', '2024-01-31'),
('FEE202401023', 2, 3, 5, 180.00, 180.00, 0, 2, 3, '2024-01-11 10:05:00', '2024-01-01', '2024-01-31');

-- 收费通知
INSERT INTO fee_notice (notice_title, notice_content, notice_type, send_scope, send_status, send_time, creator_id) VALUES
('2024年1月物业费缴费通知', '尊敬的业主，2024年1月物业费已生成，请及时缴费。', 1, 1, 1, '2024-01-01 09:00:00', 2),
('2024年1月车位费缴费通知', '您的车位费已生成，请在月末前缴纳。', 1, 1, 1, '2024-01-01 09:00:00', 2),
('关于调整物业服务收费标准的通知', '经业委会协商，拟调整物业费标准，详见附件。', 3, 1, 1, '2024-02-15 10:00:00', 1);

-- =====================================================================
-- 4. 报修数据
-- =====================================================================

INSERT INTO repair_record (repair_no, owner_id, house_id, repair_type, repair_content, repair_images, status, priority, handler_id, handle_content, handle_time, evaluate_score, evaluate_content, evaluate_time) VALUES
('REP202401001', 1, 2, '水电', '厨房水龙头漏水，请尽快安排维修', 'repair/20240115/1.jpg', 3, 2, 7, '已更换水龙头密封圈，测试无漏水', '2024-01-15 16:30:00', 5, '师傅服务态度好，维修速度快', '2024-01-15 17:00:00'),
('REP202401002', 2, 3, '门窗', '卧室窗户关不严，有缝隙漏风', 'repair/20240116/1.jpg,repair/20240116/2.jpg', 3, 1, 7, '调整窗户合页，更换密封条', '2024-01-16 15:00:00', 4, '维修及时，效果不错', '2024-01-16 15:30:00'),
('REP202401003', 3, 5, '公共设施', '电梯运行异响，乘坐不安全', NULL, 2, 3, 7, '正在联系电梯维保单位处理', NULL, NULL, NULL, NULL),
('REP202401004', 4, 7, '家电', '热水器不出热水', 'repair/20240118/1.jpg', 1, 1, NULL, NULL, NULL, NULL, NULL, NULL),
('REP202401005', 1, 2, '水电', '客厅插座无电', NULL, 0, 2, NULL, NULL, NULL, NULL, NULL, NULL);

-- =====================================================================
-- 5. 投诉建议数据
-- =====================================================================

INSERT INTO complaint_suggest (complaint_no, owner_id, house_id, type, category, content, images, status, priority, handler_id, handle_content, handle_time, is_anonymous) VALUES
('COM202401001', 1, 2, 1, '噪音扰民', '楼上装修噪音太大，影响休息，希望物业协调', NULL, 2, 2, 2, '已联系楼上业主，约定装修时间为9:00-18:00', '2024-01-10 14:00:00', 0),
('COM202401002', 2, 3, 2, '环境卫生', '建议增加小区垃圾分类桶，方便业主分类投放', NULL, 2, 1, 2, '已向环卫部门申请，预计下月增设', '2024-01-12 10:00:00', 0),
('COM202401003', 3, 5, 1, '车辆管理', '地下车位经常被外来车辆占用', 'complaint/20240115/1.jpg', 1, 2, NULL, NULL, NULL, 0),
('COM202401004', 4, 7, 3, '其他', '咨询小区宽带办理流程', NULL, 2, 1, 2, '可前往物业办公室咨询，联系电话：xxx', '2024-01-18 11:00:00', 0),
('COM202401005', 1, 2, 4, '其他', '表扬保安李师傅工作负责，深夜查验细致', NULL, 2, 1, 2, '感谢表扬，已转达并奖励', '2024-01-20 09:00:00', 1);

-- =====================================================================
-- 6. 设备数据
-- =====================================================================

-- 设备分类
INSERT INTO equipment_category (category_name, parent_id, sort, status) VALUES
('电梯', 0, 1, 1),
('消防设备', 0, 2, 1),
('门禁系统', 0, 3, 1),
('监控设备', 0, 4, 1),
('水泵', 0, 5, 1),
('配电设备', 0, 6, 1),
('照明设备', 0, 7, 1),
('给排水设备', 0, 8, 1);

-- 设备
INSERT INTO equipment (equipment_no, equipment_name, category_id, brand, model, spec, location, building_id, floor, install_date, warranty_end_date, status) VALUES
('ELEV-001', '1栋1号电梯', 1, '奥的斯', 'MACHINE ROOM-LESS', '额定载重1000kg，额定速度1.75m/s', '1栋单元楼', 1, '1-18层', '2020-06-15', '2025-06-15', 1),
('ELEV-002', '1栋2号电梯', 1, '奥的斯', 'MACHINE ROOM-LESS', '额定载重1000kg，额定速度1.75m/s', '1栋单元楼', 1, '1-18层', '2020-06-15', '2025-06-15', 1),
('ELEV-003', '2栋1号电梯', 1, '通力', 'MONOSPACE', '额定载重1000kg，额定速度1.75m/s', '2栋单元楼', 2, '1-18层', '2020-06-20', '2025-06-20', 1),
('FIRE-001', '1栋消火栓系统', 2, '海天', 'SN65', '室内消火栓，公称压力1.6MPa', '各楼层消火栓箱', 1, '1-18层', '2020-06-15', '2030-06-15', 1),
('FIRE-002', '1栋喷淋系统', 2, '天管', 'ZSTZ80', '自动喷水灭火系统', '各楼层走廊', 1, '1-18层', '2020-06-15', '2030-06-15', 1),
('ACCESS-001', '1栋门禁主机', 3, '海康威视', 'DS-K2600', 'TCP/IP联网门禁控制器', '1栋单元门', 1, '1层', '2020-06-15', '2023-06-15', 1),
('CAM-001', '1栋大堂监控', 4, '大华', 'IPC-HFW2431', '400万像素红外枪机', '1栋大堂', 1, '1层', '2020-06-15', '2023-06-15', 1),
('PUMP-001', '1栋变频供水泵', 5, '南方泵业', 'CDLF32-20', '流量32m³/h，扬程200m', '1栋地下室泵房', 1, 'B1', '2020-06-15', '2023-06-15', 1),
('POWER-001', '1栋配电箱', 6, '正泰', 'DZ47-63', '三相四线，额定电流63A', '1栋各楼层电井', 1, '1-18层', '2020-06-15', '2030-06-15', 1),
('ELEV-004', '3栋1号电梯', 1, '三菱', 'NEXIEZ-MRL', '额定载重1350kg，额定速度2.5m/s', '3栋单元楼', 3, '1-24层', '2021-08-01', '2026-08-01', 1);

-- 设备维护记录
INSERT INTO equipment_maintenance (equipment_id, maintenance_type, maintenance_content, maintenance_personnel_id, start_time, end_time, cost, parts_replaced, next_maintenance_date, status) VALUES
(1, 2, '季度保养：检查曳引机、控制柜、层门装置', 9, '2024-01-10 08:00:00', '2024-01-10 12:00:00', 500.00, '润滑油', '2024-04-10', 2),
(2, 2, '季度保养：检查曳引机、控制柜、层门装置', 9, '2024-01-10 13:00:00', '2024-01-10 17:00:00', 500.00, '润滑油', '2024-04-10', 2),
(4, 1, '月度巡检：检查消火栓箱门、水带、接口完好性', 9, '2024-01-15 09:00:00', '2024-01-15 11:00:00', 0.00, NULL, '2024-02-15', 2),
(6, 1, '月度巡检：门禁刷卡、开锁功能测试', 7, '2024-01-15 14:00:00', '2024-01-15 15:30:00', 0.00, NULL, '2024-02-15', 2),
(1, 3, '故障维修：电梯层站显示异常', 9, '2024-01-20 10:00:00', '2024-01-20 14:00:00', 1200.00, '显示板', '2024-04-20', 2);

-- =====================================================================
-- 7. 巡检数据
-- =====================================================================

-- 巡检计划
INSERT INTO inspection_plan (plan_name, plan_type, frequency_type, frequency_value, start_date, end_date, start_time, end_time, status, remark, creator_id) VALUES
('电梯日常巡检', 1, 1, NULL, '2024-01-01', '2024-12-31', '08:00:00', '10:00:00', 1, '每日早高峰前巡检电梯运行状态', 9),
('消防设施月度巡检', 1, 3, NULL, '2024-01-01', '2024-12-31', '09:00:00', '12:00:00', 1, '每月15日巡检消火栓、喷淋系统', 9),
('门禁系统周巡检', 1, 2, '1', '2024-01-01', '2024-12-31', '14:00:00', '16:00:00', 1, '每周一下午巡检门禁刷卡、开锁功能', 7),
('水泵房季度巡检', 1, 4, NULL, '2024-01-01', '2024-12-31', '09:00:00', '11:00:00', 1, '每季度首月10日巡检供水设备', 9),
('监控设备半年度巡检', 1, 5, NULL, '2024-01-01', '2024-12-31', '09:00:00', '12:00:00', 1, '每半年巡检摄像头画面、存储', 7);

-- 巡检计划-设备关联
INSERT INTO inspection_plan_equipment (plan_id, equipment_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 10),  -- 电梯日常巡检
(2, 4), (2, 5),                    -- 消防月度巡检
(3, 6),                            -- 门禁周巡检
(4, 8),                            -- 水泵季度巡检
(5, 7);                            -- 监控半年度巡检

-- 巡检计划-人员关联
INSERT INTO inspection_plan_inspector (plan_id, inspector_id) VALUES
(1, 9),  -- 电梯巡检：张工
(2, 9),  -- 消防巡检：张工
(3, 7),  -- 门禁巡检：王师傅
(4, 9),  -- 水泵巡检：张工
(5, 7);  -- 监控巡检：王师傅

-- 巡检记录（模拟2024年1月执行情况）
INSERT INTO inspection_record (plan_id, equipment_id, inspector_user_id, inspection_time, status, abnormal_desc, abnormal_images, handle_status, handle_content, handle_time, handler_id, location_lat, location_lng, location_address) VALUES
(1, 1, 9, '2024-01-15 08:30:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9042, 116.4074, '1栋单元楼电梯间'),
(1, 2, 9, '2024-01-15 08:45:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9042, 116.4074, '1栋单元楼电梯间'),
(1, 3, 9, '2024-01-15 09:00:00', 2, '电梯运行有轻微异响', 'inspect/20240115/elev3_1.jpg', 2, '已联系维保单位排查，曳引机轴承磨损，计划更换', '2024-01-15 14:00:00', 9, 39.9043, 116.4075, '2栋单元楼电梯间'),
(1, 10, 9, '2024-01-15 09:15:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9045, 116.4076, '3栋单元楼电梯间'),
(2, 4, 9, '2024-01-15 10:00:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9042, 116.4074, '1栋各楼层消火栓箱'),
(2, 5, 9, '2024-01-15 11:00:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9042, 116.4074, '1栋各楼层走廊'),
(3, 6, 7, '2024-01-15 14:30:00', 1, NULL, NULL, 0, NULL, NULL, NULL, 39.9042, 116.4074, '1栋单元门');

-- =====================================================================
-- 8. 公告数据
-- =====================================================================

INSERT INTO announcement (title, content, type, cover_image, is_top, publish_status, publish_time, creator_id, view_count) VALUES
('欢迎入住绿城小区', '尊敬的业主，欢迎您入住绿城小区！物业服务热线：400-888-8888。', 1, NULL, 1, 1, '2024-01-01 09:00:00', 2, 156),
('2024年春节放假通知', '根据国务院办公厅通知，2024年春节放假安排：2月10日-2月17日放假调休，共8天。', 1, NULL, 1, 1, '2024-02-01 09:00:00', 2, 234),
('小区环境整治活动', '为改善小区环境，将于3月1日-3月7日开展环境整治周活动，请业主配合。', 4, 'announcement/20240220/1.jpg', 0, 1, '2024-02-20 10:00:00', 2, 89),
('关于规范电动车停放充电的通知', '严禁电动车上楼充电，违者将清理处理，请业主自觉遵守。', 5, NULL, 1, 1, '2024-03-01 09:00:00', 1, 445),
('小区健身器材更新完成', '小区健身广场器材已更新完毕，欢迎业主使用，注意安全。', 3, 'announcement/20240310/1.jpg', 0, 1, '2024-03-10 09:00:00', 2, 67),
('2024年物业费缴费优惠活动', '2024年3月31日前缴纳全年物业费，享95折优惠。', 2, NULL, 0, 1, '2024-03-01 09:00:00', 2, 178);

-- 公告阅读记录
INSERT INTO announcement_read (announcement_id, user_id, read_time) VALUES
(1, 2, '2024-01-01 09:30:00'),
(1, 3, '2024-01-01 10:00:00'),
(1, 4, '2024-01-01 11:00:00'),
(1, 5, '2024-01-01 14:00:00'),
(1, 6, '2024-01-02 09:00:00'),
(2, 2, '2024-02-01 09:30:00'),
(2, 3, '2024-02-01 10:00:00'),
(2, 4, '2024-02-01 11:00:00'),
(3, 2, '2024-02-20 10:30:00'),
(3, 3, '2024-02-20 11:00:00'),
(4, 2, '2024-03-01 09:30:00'),
(4, 3, '2024-03-01 10:00:00'),
(4, 4, '2024-03-01 11:00:00'),
(4, 5, '2024-03-01 14:00:00'),
(4, 6, '2024-03-01 15:00:00');

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 验证数据
-- =====================================================================
SELECT 'sys_user' AS table_name, COUNT(*) AS count FROM sys_user
UNION ALL SELECT 'sys_role', COUNT(*) FROM sys_role
UNION ALL SELECT 'sys_user_role', COUNT(*) FROM sys_user_role
UNION ALL SELECT 'sys_menu', COUNT(*) FROM sys_menu
UNION ALL SELECT 'sys_role_menu', COUNT(*) FROM sys_role_menu
UNION ALL SELECT 'community_building', COUNT(*) FROM community_building
UNION ALL SELECT 'community_house', COUNT(*) FROM community_house
UNION ALL SELECT 'community_owner', COUNT(*) FROM community_owner
UNION ALL SELECT 'community_parking', COUNT(*) FROM community_parking
UNION ALL SELECT 'fee_item', COUNT(*) FROM fee_item
UNION ALL SELECT 'fee_record', COUNT(*) FROM fee_record
UNION ALL SELECT 'fee_notice', COUNT(*) FROM fee_notice
UNION ALL SELECT 'repair_record', COUNT(*) FROM repair_record
UNION ALL SELECT 'complaint_suggest', COUNT(*) FROM complaint_suggest
UNION ALL SELECT 'equipment_category', COUNT(*) FROM equipment_category
UNION ALL SELECT 'equipment', COUNT(*) FROM equipment
UNION ALL SELECT 'equipment_maintenance', COUNT(*) FROM equipment_maintenance
UNION ALL SELECT 'inspection_plan', COUNT(*) FROM inspection_plan
UNION ALL SELECT 'inspection_plan_equipment', COUNT(*) FROM inspection_plan_equipment
UNION ALL SELECT 'inspection_plan_inspector', COUNT(*) FROM inspection_plan_inspector
UNION ALL SELECT 'inspection_record', COUNT(*) FROM inspection_record
UNION ALL SELECT 'announcement', COUNT(*) FROM announcement
UNION ALL SELECT 'announcement_read', COUNT(*) FROM announcement_read;