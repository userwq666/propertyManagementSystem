package com.lsy.propertymanagementsystem.module.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordVO;

import java.util.List;

public interface RepairRecordService {
    Page<RepairRecordVO> page(int pageNum, int pageSize, Long ownerId, Long handlerId, Integer status);
    RepairRecordVO getById(Long id);
    Long addRepair(RepairRecordDTO domain);
    void updateRepair(RepairRecordDTO domain);
    void deleteRepair(Long id);
    void updateStatus(Long id, Integer status, Long handlerId, Long equipmentId, String handleContent);
    void updateRating(Long id, Integer score, String content);
    int autoCompleteExpired();
    List<CommunityHouseDomain> listHouses(Long ownerId);
    List<EquipmentDomain> listEquipments();
}
