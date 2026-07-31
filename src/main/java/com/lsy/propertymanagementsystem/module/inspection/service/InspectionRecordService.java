package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordDTO;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InspectionRecordService {
    Page<InspectionRecordVO> page(int pageNum, int pageSize, Long planId, Long equipmentId);
    void addRecord(InspectionRecordDTO dto);
    void addRecordDomain(InspectionRecordDomain record);
    InspectionRecordVO getRecordById(Long id);
    void updateRecord(InspectionRecordDTO dto);
    void deleteRecord(Long id);
    long countByPlanId(Long planId);
    long countByPlanAndDate(Long planId, LocalDate date);
    void acceptRecord(Long id);
    Long createRepairForAbnormal(Long id);
    List<Map<String, Object>> listRecordLogs(Long recordId);
}
