CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    house_id BIGINT NOT NULL COMMENT '房屋ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：投诉/建议/其他',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容描述',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回',
    handle_user VARCHAR(50) NULL COMMENT '处理人',
    handle_result VARCHAR(500) NULL COMMENT '处理结果',
    finish_time DATETIME NULL COMMENT '完成时间',
    rating TINYINT NULL COMMENT '评分：1-5',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
) COMMENT '投诉建议表';