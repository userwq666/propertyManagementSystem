package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentMaintenanceService;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentRecordService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentRecordServiceImpl implements EquipmentRecordService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @Autowired
    private RepairRecordService repairRecordService;

    @Autowired
    private EquipmentMaintenanceService equipmentMaintenanceService;

    @Override
    public List<Map<String, Object>> listEquipmentOptions() {
        return equipmentMapper.selectList(new LambdaQueryWrapper<EquipmentDomain>().orderByAsc(EquipmentDomain::getId))
                .stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", e.getId());
                    item.put("equipmentName", e.getEquipmentName());
                    item.put("equipmentNo", e.getEquipmentNo());
                    item.put("status", e.getStatus() == null ? null : e.getStatus().getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSummary(Long equipmentId) {
        Map<String, Object> summary = new HashMap<>();
        EquipmentDomain equipment = equipmentMapper.selectById(equipmentId);
        summary.put("equipment", equipment);
        summary.put("plans", inspectionPlanService.page(1, 100, null, null, equipmentId).getRecords());
        summary.put("records", inspectionRecordService.page(1, 200, null, equipmentId).getRecords());
        summary.put("repairs", repairRecordService.page(1, 200, null, null, equipmentId, null).getRecords());
        summary.put("maintenances", equipmentMaintenanceService.page(1, 200, equipmentId, null, null).getRecords());
        return summary;
    }
}
