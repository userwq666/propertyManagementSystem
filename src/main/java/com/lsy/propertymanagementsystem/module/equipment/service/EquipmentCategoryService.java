package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.entity.EquipmentCategory;

public interface EquipmentCategoryService {
    void addCategory(EquipmentCategory category);
    void updateCategory(EquipmentCategory category);
    void deleteCategory(Long id);
    EquipmentCategory getById(Long id);
    Page<EquipmentCategory> page(int pageNum, int pageSize, String categoryName);
}