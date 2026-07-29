package com.lsy.propertymanagementsystem.module.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordVO;

public interface RepairRecordService {
    Page<RepairRecordVO> page(int pageNum, int pageSize, Long ownerId, Integer status);
    RepairRecordVO getById(Long id);
    void addRepair(RepairRecordDTO domain);
    void updateRepair(RepairRecordDTO domain);
    void deleteRepair(Long id);
    void updateStatus(Long id, Integer status, Long handlerId, String handleContent);
    void updateRating(Long id, Integer score, String content);
}
