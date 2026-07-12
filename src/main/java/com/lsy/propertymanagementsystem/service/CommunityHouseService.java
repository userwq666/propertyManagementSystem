package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;

import java.util.List;

public interface CommunityHouseService extends IService<CommunityHouse> {
    IPage<CommunityHouse> getHousePage(Integer pageNum, Integer pageSize, Long buildingId, String roomNo, Integer houseStatus);
    
    List<CommunityHouse> getHouseList();
    
    void addHouse(HouseRequest request);
    
    void updateHouse(HouseRequest request);
    
    void deleteHouse(Long id);
    
    CommunityHouse getHouseById(Long id);
    
    void updateHouseStatus(Long id, Integer houseStatus);
}