package com.lsy.propertymanagementsystem.module.equipment.service;

import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentCategoryDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryDTO;

import java.util.List;

public interface EquipmentCategoryService {
    List<EquipmentCategoryDomain> getList();
    void add(EquipmentCategoryDTO category);
    void update(EquipmentCategoryDTO category);
    void delete(Long id);
}