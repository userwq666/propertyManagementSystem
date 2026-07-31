-- ===============================================================
-- 种子数据：设备分类
-- ===============================================================

INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (1, "水电", 0, 1, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (2, "消防", 0, 2, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (3, "电梯", 0, 3, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (4, "绿化", 0, 4, 1);
INSERT IGNORE INTO equipment_category (id, category_name, parent_id, sort, status) VALUES (5, "基础物品", 0, 5, 1);
