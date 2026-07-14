-- 设备分类表
CREATE TABLE equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否预设分类：0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '设备分类表';

-- 插入预设分类数据
INSERT INTO equipment_category (category_name, description, sort_order, is_default) VALUES
('电梯', '电梯设备', 1, 1),
('消防设备', '消防设备', 2, 1),
('门禁系统', '门禁系统', 3, 1),
('监控设备', '监控设备', 4, 1),
('水泵', '水泵设备', 5, 1),
('配电设备', '配电设备', 6, 1);

-- 设备表
CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    equipment_code VARCHAR(50) NOT NULL COMMENT '设备编号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    location VARCHAR(200) COMMENT '安装位置',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常 1维修中 2停用',
    maintenance_user VARCHAR(50) COMMENT '维护人员',
    install_date DATE COMMENT '安装日期',
    warranty_date DATE COMMENT '保修到期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_equipment_code (equipment_code)
) COMMENT '设备表';

-- 巡检计划表
CREATE TABLE inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type TINYINT NOT NULL COMMENT '计划类型：0手动创建 1周期生成',
    cycle_type TINYINT COMMENT '周期类型：0每天 1每周 2每月 3自定义',
    cycle_value INT COMMENT '自定义周期值（每N天/周/月）',
    plan_date DATE COMMENT '计划日期（手动创建时使用）',
    start_date DATE COMMENT '周期开始日期',
    end_date DATE COMMENT '周期结束日期',
    equipment_ids VARCHAR(500) COMMENT '设备ID列表（逗号分隔）',
    inspector_ids VARCHAR(500) COMMENT '巡检人员ID列表（逗号分隔）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待执行 1执行中 2已完成 3已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '巡检计划表';

-- 巡检记录表
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT COMMENT '关联计划ID（可为空，支持临时巡检）',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    inspector_id BIGINT NOT NULL COMMENT '巡检人员ID',
    inspect_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '巡检时间',
    result TINYINT NOT NULL COMMENT '巡检结果：0正常 1一般异常 2严重异常',
    fault_desc VARCHAR(500) COMMENT '功能异常描述',
    repair_suggestion VARCHAR(500) COMMENT '维修建议',
    budget DECIMAL(10,2) COMMENT '预估费用',
    duration VARCHAR(50) COMMENT '预估工时',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '巡检记录表';