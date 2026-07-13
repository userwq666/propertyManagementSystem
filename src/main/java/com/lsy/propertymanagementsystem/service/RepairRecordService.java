package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.RepairRecordRequest;
import com.lsy.propertymanagementsystem.entity.RepairRecord;

public interface RepairRecordService {
    void add(RepairRecordRequest request);
    void update(RepairRecordRequest request);
    void delete(Long id);
    RepairRecord getById(Long id);
    Page<RepairRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status);
    void updateStatus(Long id, Integer status, String handleUser, String handleResult);
    void updateRating(Long id, Integer rating);
}
