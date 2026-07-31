-- ===============================================================
-- 迁移脚本：报修单关联设备，实现报修-设备维保联动
-- 执行日期：2026-07-31
-- 说明：结单时可指定关联设备，自动生成故障维修维保记录并恢复设备状态
-- ===============================================================
USE property_management_system;

ALTER TABLE repair_record
    ADD COLUMN equipment_id BIGINT NULL COMMENT '关联设备id，结单时可指定' AFTER repair_images,
    ADD CONSTRAINT fk_repair_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON UPDATE CASCADE ON DELETE SET NULL,
    ADD INDEX idx_repair_equipment (equipment_id);
