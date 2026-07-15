package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseDTO;

public interface CommunityHouseService {
    void addHouse(CommunityHouseDTO house);
    void updateHouse(CommunityHouseDTO house);
    void deleteHouse(Long id);
    CommunityHouseDomain getHouseById(Long id);
    Page<CommunityHouseDomain> page(int pageNum, int pageSize, Long buildingId, Integer houseStatus);
    long countByBuildingId(Long buildingId);
}