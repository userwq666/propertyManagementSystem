package com.lsy.propertymanagementsystem.module.equipment.service;

import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface EquipmentCategoryService {
    void add(EquipmentCategoryDTO category);
    void update(EquipmentCategoryDTO category);
    void delete(Long id);
    Page<EquipmentCategoryVO> page(int pageNum, int pageSize);
}
