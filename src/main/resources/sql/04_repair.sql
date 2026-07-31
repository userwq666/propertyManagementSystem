-- ===============================================================
-- 物业管理系统 - 报修管理模块
-- ===============================================================

-- 4. 报修管理表
-- =====================================================================

-- 报修记录表
CREATE TABLE repair_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报修单号',
    owner_id BIGINT NULL COMMENT '报修业主id，系统代报可为空',
    house_id BIGINT NULL COMMENT '报修房屋id，公共区域可为空',
    repair_type VARCHAR(50) COMMENT '报修类型：水电、门窗、家电、公共设施、其他',
    repair_content TEXT NOT NULL COMMENT '报修内容',
    repair_images VARCHAR(1000) COMMENT '报修图片，逗号分隔',
    equipment_id BIGINT NULL COMMENT '关联设备id，结单时可指定',
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
    CONSTRAINT fk_repair_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_repair_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '报修记录表';

-- 索引
CREATE INDEX idx_repair_owner ON repair_record(owner_id);
CREATE INDEX idx_repair_house ON repair_record(house_id);
CREATE INDEX idx_repair_handler ON repair_record(handler_id);
CREATE INDEX idx_repair_status ON repair_record(status);
CREATE INDEX idx_repair_equipment ON repair_record(equipment_id);
CREATE INDEX idx_repair_no ON repair_record(repair_no);
CREATE INDEX idx_repair_create_time ON repair_record(create_time);

-- =====================================================================
