package com.lsy.propertymanagementsystem.module.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;

public interface RepairRecordService {
    Page<RepairRecordDomain> page(int pageNum, int pageSize, Long ownerId, Integer status);
    void addRepair(RepairRecordDTO domain);
    void updateRepair(RepairRecordDTO domain);
    void deleteRepair(Long id);
    void updateStatus(Long id, Integer status, Long handlerId, String handleContent);
    void updateRating(Long id, Integer score, String content);
    long countByHouseId(Long houseId);
}