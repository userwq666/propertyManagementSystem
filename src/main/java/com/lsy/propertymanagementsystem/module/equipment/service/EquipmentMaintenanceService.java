package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;

public interface EquipmentMaintenanceService {
    void add(EquipmentMaintenanceDomain domain);
    void update(EquipmentMaintenanceDomain domain);
    void delete(Long id);
    EquipmentMaintenanceDomain getById(Long id);
    Page<EquipmentMaintenanceDomain> page(int pageNum, int pageSize, Long equipmentId, Integer maintenanceType, Integer status);
    void startMaintenance(Long id);
    void completeMaintenance(Long id);
    long countByEquipmentId(Long equipmentId);
}
