package com.lsy.propertymanagementsystem.module.equipment.service;

import java.util.List;
import java.util.Map;

public interface EquipmentRecordService {
    List<Map<String, Object>> listEquipmentOptions();
    Map<String, Object> getSummary(Long equipmentId);
}
