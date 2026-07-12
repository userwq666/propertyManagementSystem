package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;

import java.util.List;

public interface CommunityBuildingService extends IService<CommunityBuilding> {
    IPage<CommunityBuilding> getBuildingPage(Integer pageNum, Integer pageSize, String buildingNo);
    
    List<CommunityBuilding> getBuildingList();
    
    void addBuilding(BuildingRequest request);
    
    void updateBuilding(BuildingRequest request);
    
    void deleteBuilding(Long id);
    
    CommunityBuilding getBuildingById(Long id);
}