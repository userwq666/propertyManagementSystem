package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;

import java.util.List;

public interface InspectionRecordService {
    void add(InspectionRecordRequest request);
    void update(InspectionRecordRequest request);
    void delete(Long id);
    InspectionRecord getById(Long id);
    Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result);
    List<InspectionRecord> getByEquipmentId(Long equipmentId);
}
