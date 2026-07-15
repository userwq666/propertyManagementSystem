package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityBuildingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingDTO;

public interface CommunityBuildingService {
    void addBuilding(CommunityBuildingDTO building);
    void updateBuilding(CommunityBuildingDTO building);
    void deleteBuilding(Long id);
    CommunityBuildingDomain getBuildingById(Long id);
    Page<CommunityBuildingDomain> page(int pageNum, int pageSize);
}