package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordDTO;

public interface InspectionRecordService {
    Page<InspectionRecordDomain> page(int pageNum, int pageSize, Long planId, Long equipmentId);
    void addRecord(InspectionRecordDTO dto);
    void addRecordDomain(InspectionRecordDomain record);
    InspectionRecordDomain getRecordById(Long id);
    void updateRecord(InspectionRecordDTO dto);
    long countByPlanId(Long planId);
}