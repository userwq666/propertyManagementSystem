-- ===============================================================
-- 物业管理系统 - 收费管理模块
-- ===============================================================

-- 3. 收费管理表
-- =====================================================================

-- 收费项目表
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL COMMENT '收费项目名称',
    item_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费类型：1物业费 2车位费 3水费 4电费 5燃气费 6暖气费 9其他',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    unit VARCHAR(20) COMMENT '单位：元/㎡/月、元/月、元/吨、元/度',
    cycle_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费周期：1按月 2按季 3按半年 4按年 5一次性',
    due_day TINYINT NULL COMMENT '最迟收款日（每周期内截止日，1-31）',
    notice_roles VARCHAR(100) COMMENT '通知范围角色（逗号分隔角色key）',
    total_times TINYINT NOT NULL DEFAULT 0 COMMENT '收费次数（0=长期周期性，N=固定次数）',
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
    send_status TINYINT NOT NULL DEFAULT 0 COMMENT '发送状态：0草稿 1已发送 2发送失败',
    send_time DATETIME COMMENT '发送时间',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_notice_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '收费通知表';

-- 收费通知-楼栋关联表
CREATE TABLE fee_notice_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL COMMENT '通知ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    UNIQUE KEY uk_notice_building (notice_id, building_id),
    CONSTRAINT fk_fnb_notice FOREIGN KEY (notice_id) REFERENCES fee_notice(id) ON DELETE CASCADE,
    CONSTRAINT fk_fnb_building FOREIGN KEY (building_id) REFERENCES community_building(id) ON DELETE CASCADE
) COMMENT '收费通知-楼栋关联表';

-- 收费通知-业主关联表
CREATE TABLE fee_notice_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL COMMENT '通知ID',
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    UNIQUE KEY uk_notice_owner (notice_id, owner_id),
    CONSTRAINT fk_fno_notice FOREIGN KEY (notice_id) REFERENCES fee_notice(id) ON DELETE CASCADE,
    CONSTRAINT fk_fno_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON DELETE CASCADE
) COMMENT '收费通知-业主关联表';

-- 索引
CREATE INDEX idx_fee_owner ON fee_record(owner_id);
CREATE INDEX idx_fee_house ON fee_record(house_id);
CREATE INDEX idx_fee_item ON fee_record(item_id);
CREATE INDEX idx_fee_status ON fee_record(status);
CREATE INDEX idx_fee_no ON fee_record(fee_no);
CREATE INDEX idx_fee_create_time ON fee_record(create_time);
CREATE INDEX idx_notice_creator ON fee_notice(creator_id);
CREATE INDEX idx_fnb_building ON fee_notice_building(building_id);
CREATE INDEX idx_fno_owner ON fee_notice_owner(owner_id);

-- =====================================================================


SET FOREIGN_KEY_CHECKS = 1;
