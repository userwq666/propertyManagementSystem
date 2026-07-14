package com.lsy.propertymanagementsystem.module.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.repair.entity.RepairRecord;

public interface RepairRecordService {
    void addRepair(RepairRecord record);
    void updateRepair(RepairRecord record);
    void deleteRepair(Long id);
    RepairRecord getById(Long id);
    Page<RepairRecord> page(int pageNum, int pageSize, Long ownerId, Integer status);
    void updateStatus(Long id, Integer status, String handleUser, String handleResult);
    void updateRating(Long id, Integer rating);
    long countByHouseId(Long houseId);
}