package com.lsy.propertymanagementsystem.service;

import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;

import java.util.List;

public interface EquipmentCategoryService {
    void add(EquipmentCategoryRequest request);
    void update(EquipmentCategoryRequest request);
    void delete(Long id);
    EquipmentCategory getById(Long id);
    List<EquipmentCategory> list();
}
