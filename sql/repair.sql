-- 报修维修模块表结构

-- 创建报修记录表
CREATE TABLE repair_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '报修业主id',
    house_id BIGINT NOT NULL COMMENT '房屋id',
    repair_type VARCHAR(50) NOT NULL COMMENT '报修类型：水电/门窗/公共设备',
    content TEXT NOT NULL COMMENT '故障描述',
    img_url VARCHAR(500) COMMENT '故障图片',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待处理 1处理中 2已完成 3驳回',
    handle_user VARCHAR(50) COMMENT '处理物业人员',
    handle_result TEXT COMMENT '处理结果',
    finish_time DATETIME COMMENT '完成时间',
    rating TINYINT COMMENT '评分：1-5',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '报修记录表';
