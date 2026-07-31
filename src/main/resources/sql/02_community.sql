-- ===============================================================
-- 物业管理系统 - 小区管理模块
-- ===============================================================

-- 2. 社区基础表
-- =====================================================================

-- 楼栋表
SET FOREIGN_KEY_CHECKS = 0;
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

-- 业主信息表（必须在 community_house 之前创建）
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联系统登录账号',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号（应用层需加密存储）',
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

-- 房屋表（依赖 community_building 和 community_owner）
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
CREATE INDEX idx_building_no ON community_building(building_no);
CREATE INDEX idx_house_building ON community_house(building_id);
CREATE INDEX idx_house_room ON community_house(building_id, room_no);
CREATE INDEX idx_house_owner ON community_house(owner_id);
CREATE INDEX idx_owner_user ON community_owner(user_id);
CREATE INDEX idx_owner_phone ON community_owner(phone);
CREATE INDEX idx_owner_idcard ON community_owner(id_card);
CREATE INDEX idx_parking_no ON community_parking(parking_no);
CREATE INDEX idx_parking_owner ON community_parking(owner_id);

-- =====================================================================


SET FOREIGN_KEY_CHECKS = 1;