-- ===============================================================
-- 物业管理系统 - 巡检管理模块
-- ===============================================================

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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    UNIQUE KEY uk_plan_equipment (plan_id, equipment_id),
    CONSTRAINT fk_ipe_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipe_equip FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
) COMMENT '巡检计划-设备关联表';

-- 巡检计划-巡检人员关联表
CREATE TABLE inspection_plan_inspector (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    inspector_id BIGINT NOT NULL COMMENT '巡检人员ID',
    UNIQUE KEY uk_plan_inspector (plan_id, inspector_id),
    CONSTRAINT fk_ipi_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipi_inspector FOREIGN KEY (inspector_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT '巡检计划-巡检人员关联表';

-- 巡检记录表
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT COMMENT '计划id',
    equipment_id BIGINT NOT NULL COMMENT '设备id',
    inspector_user_id BIGINT COMMENT '巡检人员ID',
    filler_user_id BIGINT COMMENT '填写人ID',
    inspection_time DATETIME NOT NULL COMMENT '巡检时间',
    task_status TINYINT NOT NULL DEFAULT 0 COMMENT '任务状态：0待接单 1已接单 2已填写',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '巡检结果：1正常 2异常 3未巡检',
    repair_record_id BIGINT COMMENT '关联报修单ID，异常转报修后记录',
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
