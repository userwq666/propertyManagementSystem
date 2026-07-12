-- 使用数据库
USE property_management_system;

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
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '房屋表';

-- 业主信息表
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联系统登录用户id',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    check_in_time DATETIME COMMENT '入住时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '业主信息表';

-- 车位表
CREATE TABLE community_parking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parking_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    parking_type TINYINT NOT NULL DEFAULT 0 COMMENT '车位类型：0固定车位 1临时车位',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已租赁',
    owner_id BIGINT COMMENT '所属业主id',
    expire_time DATETIME COMMENT '租赁到期时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '车位表';

-- 插入测试数据
INSERT INTO community_building (building_no, floor_count, total_house, build_year) VALUES
('1栋', 18, 72, 2020),
('2栋', 18, 72, 2020),
('3栋', 24, 96, 2021);

INSERT INTO community_house (building_id, room_no, area, house_type, house_status) VALUES
(1, '1-101', 89.50, '两室一厅', 1),
(1, '1-102', 89.50, '两室一厅', 0),
(1, '2-101', 120.00, '三室两厅', 1),
(2, '1-101', 89.50, '两室一厅', 2);

INSERT INTO community_parking (parking_no, parking_type, status) VALUES
('A-001', 0, 0),
('A-002', 0, 1),
('B-001', 1, 0);
