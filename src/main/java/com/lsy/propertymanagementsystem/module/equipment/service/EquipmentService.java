package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentVO;

public interface EquipmentService {
    void addEquipment(EquipmentDTO domain);
    void updateEquipment(EquipmentDTO domain);
    void deleteEquipment(Long id);
    EquipmentVO getById(Long id);
    Page<EquipmentVO> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
    long countByCategoryId(Long categoryId);
}
