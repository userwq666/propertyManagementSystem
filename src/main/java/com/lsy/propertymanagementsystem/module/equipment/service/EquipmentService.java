package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.entity.Equipment;

public interface EquipmentService {
    void addEquipment(Equipment equipment);
    void updateEquipment(Equipment equipment);
    void deleteEquipment(Long id);
    Equipment getById(Long id);
    Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
    long countByCategoryId(Long categoryId);
}