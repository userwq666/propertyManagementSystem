package com.lsy.propertymanagementsystem.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentMaintenanceDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentMaintenanceVO;

public interface EquipmentMaintenanceService {
    void add(EquipmentMaintenanceDTO dto);
    void update(EquipmentMaintenanceDTO dto);
    void delete(Long id);
    EquipmentMaintenanceVO getById(Long id);
    Page<EquipmentMaintenanceVO> page(int pageNum, int pageSize, Long equipmentId, Integer maintenanceType, Integer status);
    void startMaintenance(Long id);
    void completeMaintenance(Long id);
}
