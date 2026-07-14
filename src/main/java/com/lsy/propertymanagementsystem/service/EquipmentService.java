package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;

public interface EquipmentService {
    void add(EquipmentRequest request);
    void update(EquipmentRequest request);
    void delete(Long id);
    Equipment getById(Long id);
    Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
}
