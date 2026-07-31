-- ===============================================================
-- 物业管理系统 - 设备管理模块
-- ===============================================================

-- 6. 设备管理表
-- =====================================================================

-- 设备分类表
SET FOREIGN_KEY_CHECKS = 0;
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
    CONSTRAINT fk_equip_category FOREIGN KEY (category_id) REFERENCES equipment_category(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equip_building FOREIGN KEY (building_id) REFERENCES community_building(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '设备表';

-- 索引
CREATE INDEX idx_equip_category ON equipment(category_id);
CREATE INDEX idx_equip_building ON equipment(building_id);
CREATE INDEX idx_equip_status ON equipment(status);

-- =====================================================================

-- 种子数据：设备分类
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (1, '水电', 0, 1, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (2, '消防', 0, 2, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (3, '电梯', 0, 3, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (4, '绿化', 0, 4, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (5, '基础物品', 0, 5, 1);


SET FOREIGN_KEY_CHECKS = 1;