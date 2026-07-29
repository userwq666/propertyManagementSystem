package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingVO;

public interface CommunityBuildingService {
    void addBuilding(CommunityBuildingDTO building);
    void updateBuilding(CommunityBuildingDTO building);
    void deleteBuilding(Long id);
    CommunityBuildingVO getBuildingById(Long id);
    Page<CommunityBuildingVO> page(int pageNum, int pageSize, String buildingNo);
}