-- =====================================================================
-- 物业管理系统 - 完整数据库结构初始化脚本
-- 包含：建库、建表、外键约束、索引、视图
-- 执行顺序：第 1 步
-- =====================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS property_management_system 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE property_management_system;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
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
    user_type TINYINT NOT NULL DEFAULT 3 COMMENT '用户类型：1超级管理员 2物业管理员 3业主',
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
-- 2. 小区基础表
-- =====================================================================

-- 楼栋表
CREATE TABLE community_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_no VARCHAR(50) NOT NULL COMMENT '楼栋编号',
    floor_count INT COMMENT '总楼层',
    total_house INT COMMENT '总户数',
    build_year INT COMMENT '建成年份',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '楼栋表';

-- 房屋表
CREATE TABLE community_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id BIGINT NOT NULL COMMENT '楼栋id',
    room_no VARCHAR(50) NOT NULL COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '房屋面积',
    house_type VARCHAR(50) COMMENT '户型',
    house_status TINYINT NOT NULL DEFAULT 0 COMMENT '房屋状态：0空置 1已入住 2出租',
    owner_id BIGINT COMMENT '业主id',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_house_building FOREIGN KEY (building_id) REFERENCES community_building(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_house_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '房屋表';

-- 业主信息表
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联系统登录账号',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号',
    id_card_front VARCHAR(255) COMMENT '身份证正面',
    id_card_back VARCHAR(255) COMMENT '身份证反面',
    owner_type TINYINT NOT NULL DEFAULT 1 COMMENT '业主类型：1本人 2家属 3租客',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_owner_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '业主信息表';

-- 车位表
CREATE TABLE community_parking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parking_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    parking_type TINYINT NOT NULL DEFAULT 1 COMMENT '车位类型：1地上 2地下',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已租 2已售 3维修中',
    owner_id BIGINT COMMENT '业主id',
    rent_price DECIMAL(10,2) COMMENT '租金/月',
    sell_price DECIMAL(10,2) COMMENT '售价',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_parking_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '车位表';

-- 索引
CREATE INDEX idx_house_building ON community_house(building_id);
CREATE INDEX idx_house_owner ON community_house(owner_id);
CREATE INDEX idx_owner_user ON community_owner(user_id);
CREATE INDEX idx_parking_owner ON community_parking(owner_id);

-- =====================================================================
-- 3. 收费管理表
-- =====================================================================

-- 收费项目表
CREATE TABLE fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL COMMENT '收费项目名称',
    item_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费类型：1物业费 2车位费 3水费 4电费 5燃气费 6暖气费 9其他',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    unit VARCHAR(20) COMMENT '单位：元/㎡/月、元/月、元/吨、元/度',
    cycle_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费周期：1按月 2按季 3按半年 4按年 5一次性',
    description VARCHAR(500) COMMENT '项目描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费项目表';

-- 收费记录表
CREATE TABLE fee_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fee_no VARCHAR(50) NOT NULL UNIQUE COMMENT '缴费单号',
    owner_id BIGINT NOT NULL COMMENT '业主id',
    house_id BIGINT NOT NULL COMMENT '房屋id',
    item_id BIGINT NOT NULL COMMENT '收费项目id',
    amount DECIMAL(10,2) NOT NULL COMMENT '应收金额',
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实收金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '减免金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '缴费状态：0未缴费 1部分缴费 2已缴费 3逾期 4作废',
    pay_type TINYINT COMMENT '支付方式：1现金 2微信 3支付宝 4银行卡 5转账',
    pay_time DATETIME COMMENT '缴费时间',
    start_date DATE COMMENT '收费开始日期',
    end_date DATE COMMENT '收费结束日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_fee_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_fee_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_fee_item FOREIGN KEY (item_id) REFERENCES fee_item(id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '收费记录表';

-- 收费通知表
CREATE TABLE fee_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_title VARCHAR(100) NOT NULL COMMENT '通知标题',
    notice_content TEXT COMMENT '通知内容',
    notice_type TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型：1缴费通知 2欠费催缴 3费率调整 4其他',
    send_scope TINYINT NOT NULL DEFAULT 1 COMMENT '发送范围：1全体 2指定楼栋 3指定业主',
    building_ids VARCHAR(500) COMMENT '指定楼栋ID，逗号分隔',
    owner_ids VARCHAR(500) COMMENT '指定业主ID，逗号分隔',
    send_status TINYINT NOT NULL DEFAULT 0 COMMENT '发送状态：0草稿 1已发送 2发送失败',
    send_time DATETIME COMMENT '发送时间',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费通知表';

-- 索引
CREATE INDEX idx_fee_owner ON fee_record(owner_id);
CREATE INDEX idx_fee_house ON fee_record(house_id);
CREATE INDEX idx_fee_item ON fee_record(item_id);
CREATE INDEX idx_fee_status ON fee_record(status);
CREATE INDEX idx_fee_no ON fee_record(fee_no);
CREATE INDEX idx_notice_creator ON fee_notice(creator_id);

-- =====================================================================
-- 4. 报修管理表
-- =====================================================================

-- 报修记录表
CREATE TABLE repair_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报修单号',
    owner_id BIGINT NOT NULL COMMENT '报修业主id',
    house_id BIGINT NOT NULL COMMENT '报修房屋id',
    repair_type VARCHAR(50) COMMENT '报修类型：水电、门窗、家电、公共设施、其他',
    repair_content TEXT NOT NULL COMMENT '报修内容',
    repair_images VARCHAR(1000) COMMENT '报修图片，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待派单 1处理中 2待评价 3已完成 4已取消',
    priority TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1普通 2加急 3紧急',
    handler_id BIGINT NULL COMMENT '处理人ID',
    handle_content TEXT COMMENT '处理内容',
    handle_images VARCHAR(1000) COMMENT '处理图片，逗号分隔',
    handle_time DATETIME COMMENT '处理完成时间',
    evaluate_score TINYINT COMMENT '评价分数：1-5',
    evaluate_content VARCHAR(500) COMMENT '评价内容',
    evaluate_time DATETIME COMMENT '评价时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_repair_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_repair_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_repair_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '报修记录表';

-- 索引
CREATE INDEX idx_repair_owner ON repair_record(owner_id);
CREATE INDEX idx_repair_house ON repair_record(house_id);
CREATE INDEX idx_repair_handler ON repair_record(handler_id);
CREATE INDEX idx_repair_status ON repair_record(status);
CREATE INDEX idx_repair_no ON repair_record(repair_no);

-- =====================================================================
-- 5. 投诉建议表
-- =====================================================================

-- 投诉建议表
CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_no VARCHAR(50) NOT NULL UNIQUE COMMENT '投诉单号',
    owner_id BIGINT NOT NULL COMMENT '投诉业主id',
    house_id BIGINT NOT NULL COMMENT '投诉房屋id',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1投诉 2建议 3咨询 4表扬',
    category VARCHAR(50) COMMENT '分类：环境卫生、噪音扰民、车辆管理、服务态度、设施损坏、其他',
    content TEXT NOT NULL COMMENT '投诉建议内容',
    images VARCHAR(1000) COMMENT '图片，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1处理中 2已回复 3已关闭 4已撤销',
    priority TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1普通 2重要 3紧急',
    handler_id BIGINT NULL COMMENT '处理人ID',
    handle_content TEXT COMMENT '处理回复内容',
    handle_time DATETIME COMMENT '处理时间',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_complaint_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '投诉建议表';

-- 索引
CREATE INDEX idx_complaint_owner ON complaint_suggest(owner_id);
CREATE INDEX idx_complaint_house ON complaint_suggest(house_id);
CREATE INDEX idx_complaint_handler ON complaint_suggest(handler_id);
CREATE INDEX idx_complaint_status ON complaint_suggest(status);
CREATE INDEX idx_complaint_no ON complaint_suggest(complaint_no);

-- =====================================================================
-- 6. 设备管理表
-- =====================================================================

-- 设备分类表
CREATE TABLE equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '设备分类表';

-- 设备表
CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    category_id BIGINT NOT NULL COMMENT '设备分类id',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    spec VARCHAR(500) COMMENT '规格参数',
    location VARCHAR(200) COMMENT '安装位置',
    building_id BIGINT COMMENT '所属楼栋',
    floor VARCHAR(20) COMMENT '楼层',
    install_date DATE COMMENT '安装日期',
    warranty_end_date DATE COMMENT '保修截止日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2故障 3维修中 4停用 5报废',
    qr_code VARCHAR(255) COMMENT '二维码地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_equip_category FOREIGN KEY (category_id) REFERENCES equipment_category(id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '设备表';

-- 设备维护记录表
CREATE TABLE equipment_maintenance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL COMMENT '设备id',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1日常巡检 2定期保养 3故障维修 4更换配件 5其他',
    maintenance_content TEXT COMMENT '维护内容',
    maintenance_personnel_id BIGINT COMMENT '维护人员ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    cost DECIMAL(10,2) COMMENT '维护费用',
    parts_replaced VARCHAR(500) COMMENT '更换配件',
    next_maintenance_date DATE COMMENT '下次维护日期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待维护 1进行中 2已完成 3取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '设备维护记录表';

-- 索引
CREATE INDEX idx_equip_category ON equipment(category_id);
CREATE INDEX idx_equip_building ON equipment(building_id);
CREATE INDEX idx_equip_status ON equipment(status);
CREATE INDEX idx_maint_equip ON equipment_maintenance(equipment_id);
CREATE INDEX idx_maint_personnel ON equipment_maintenance(maintenance_personnel_id);
CREATE INDEX idx_maint_status ON equipment_maintenance(status);

-- =====================================================================
-- 7. 巡检管理表
-- =====================================================================

-- 巡检计划表
CREATE TABLE inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type TINYINT NOT NULL DEFAULT 1 COMMENT '计划类型：1日常巡检 2专项巡检 3季节性巡检 4临时巡检',
    frequency_type TINYINT COMMENT '频次类型：1每日 2每周 3每月 4每季度 5每半年 6每年 7一次性',
    frequency_value VARCHAR(100) COMMENT '频次值：如每周几、每月几号、cron表达式等',
    start_date DATE COMMENT '生效开始日期',
    end_date DATE COMMENT '生效结束日期',
    start_time TIME COMMENT '巡检开始时间',
    end_time TIME COMMENT '巡检结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_plan_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '巡检计划表';

-- 巡检计划-设备关联表
CREATE TABLE inspection_plan_equipment (
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    PRIMARY KEY (plan_id, equipment_id),
    CONSTRAINT fk_ipe_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipe_equip FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
) COMMENT '巡检计划-设备关联表';

-- 巡检计划-巡检人员关联表
CREATE TABLE inspection_plan_inspector (
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    inspector_id BIGINT NOT NULL COMMENT '巡检人员ID',
    PRIMARY KEY (plan_id, inspector_id),
    CONSTRAINT fk_ipi_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipi_inspector FOREIGN KEY (inspector_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT '巡检计划-巡检人员关联表';

-- 巡检记录表
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT COMMENT '计划id',
    equipment_id BIGINT NOT NULL COMMENT '设备id',
    inspector_user_id BIGINT COMMENT '巡检人员ID',
    inspection_time DATETIME NOT NULL COMMENT '巡检时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '巡检结果：1正常 2异常 3未巡检',
    abnormal_desc VARCHAR(500) COMMENT '异常描述',
    abnormal_images VARCHAR(1000) COMMENT '异常图片，逗号分隔',
    handle_status TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0待处理 1处理中 2已处理 3忽略',
    handle_content TEXT COMMENT '处理内容',
    handle_time DATETIME COMMENT '处理时间',
    handler_id BIGINT COMMENT '处理人ID',
    location_lat DECIMAL(10,7) COMMENT '纬度',
    location_lng DECIMAL(10,7) COMMENT '经度',
    location_address VARCHAR(200) COMMENT '定位地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_inspect_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_inspect_equip FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inspect_inspector_user FOREIGN KEY (inspector_user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_inspect_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '巡检记录表';

-- 索引
CREATE INDEX idx_inspect_plan ON inspection_record(plan_id);
CREATE INDEX idx_inspect_equip ON inspection_record(equipment_id);
CREATE INDEX idx_inspect_inspector_user ON inspection_record(inspector_user_id);
CREATE INDEX idx_inspect_handler ON inspection_record(handler_id);
CREATE INDEX idx_inspect_time ON inspection_record(inspection_time);
CREATE INDEX idx_plan_status ON inspection_plan(status);
CREATE INDEX idx_plan_creator ON inspection_plan(creator_id);

-- =====================================================================
-- 8. 公告通知表
-- =====================================================================

-- 公告表
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1通知公告 2政策法规 3便民服务 4活动通知 5紧急通知',
    cover_image VARCHAR(255) COMMENT '封面图片',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
    top_expire_time DATETIME COMMENT '置顶过期时间',
    publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态：0草稿 1发布 2下架',
    publish_time DATETIME COMMENT '发布时间',
    creator_id BIGINT COMMENT '创建人ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_announcement_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '公告表';

-- 公告阅读记录表
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    announcement_id BIGINT NOT NULL COMMENT '公告id',
    user_id BIGINT NOT NULL COMMENT '阅读用户id',
    read_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY uk_announcement_user (announcement_id, user_id)
) COMMENT '公告阅读记录表';

-- 索引
CREATE INDEX idx_announcement_creator ON announcement(creator_id);
CREATE INDEX idx_announcement_status ON announcement(publish_status);
CREATE INDEX idx_announcement_type ON announcement(type);

-- =====================================================================
-- 完成
-- =====================================================================
SET FOREIGN_KEY_CHECKS = 1;

-- 验证外键
SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'property_management_system' AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;
