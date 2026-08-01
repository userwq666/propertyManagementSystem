-- ===============================================================
-- 物业管理系统 - 投诉建议模块
-- ===============================================================

-- 5. 投诉建议表
-- =====================================================================

-- 投诉建议表
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_no VARCHAR(50) NOT NULL UNIQUE COMMENT '投诉单号',
    owner_id BIGINT NULL COMMENT '关联业主ID（非业主投诉可为空）',
    creator_id BIGINT NOT NULL DEFAULT 0 COMMENT '投诉人用户ID',
    house_id BIGINT NULL COMMENT '关联房屋ID（可为空）',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1投诉 2建议 3咨询 4表扬',
    category VARCHAR(50) COMMENT '分类：环境卫生、噪音扰民、车辆管理、服务态度、设施损坏、其他',
    content TEXT NOT NULL COMMENT '投诉建议内容',
    images VARCHAR(1000) COMMENT '图片，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1已受理 2处理中 3已回复 4已完成 5已撤销',
    priority TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1普通 2重要 3紧急',
    handler_id BIGINT NULL COMMENT '处理人ID',
    handle_content TEXT COMMENT '处理回复内容',
    handle_time DATETIME COMMENT '处理时间',
    evaluate_score TINYINT COMMENT '评价分数1-5',
    evaluate_content VARCHAR(500) COMMENT '评价内容',
    evaluate_time DATETIME COMMENT '评价时间',
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
CREATE INDEX idx_complaint_create_time ON complaint_suggest(create_time);

-- =====================================================================


SET FOREIGN_KEY_CHECKS = 1;
