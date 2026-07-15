package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;

public interface EquipmentService {
    void addEquipment(EquipmentDTO domain);
    void updateEquipment(EquipmentDTO domain);
    void deleteEquipment(Long id);
    EquipmentDomain getById(Long id);
    Page<EquipmentDomain> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
    long countByCategoryId(Long categoryId);
}