package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionRecord;

import java.util.List;

public interface InspectionRecordService {
    void addRecord(InspectionRecord record);
    void updateRecord(InspectionRecord record);
    void deleteRecord(Long id);
    InspectionRecord getById(Long id);
    Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result);
    List<InspectionRecord> getByEquipmentId(Long equipmentId);
    long countByPlanId(Long planId);
}