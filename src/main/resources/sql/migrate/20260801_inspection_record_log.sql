-- ===============================================================
-- 迁移脚本：巡检记录修改日志表
-- 执行日期：2026-08-01
-- 说明：管理员修改已打卡巡检记录必须留痕
-- ===============================================================
USE property_management_system;

CREATE TABLE IF NOT EXISTS inspection_record_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id BIGINT NOT NULL COMMENT '巡检记录ID',
    operator_id BIGINT COMMENT '修改人ID',
    before_status TINYINT COMMENT '修改前结果',
    after_status TINYINT COMMENT '修改后结果',
    reason VARCHAR(500) COMMENT '修改原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_irlog_record FOREIGN KEY (record_id) REFERENCES inspection_record(id) ON DELETE CASCADE,
    CONSTRAINT fk_irlog_operator FOREIGN KEY (operator_id) REFERENCES sys_user(id) ON DELETE SET NULL
) COMMENT '巡检记录修改日志表';

CREATE INDEX idx_irlog_record ON inspection_record_log(record_id);
