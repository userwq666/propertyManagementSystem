package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;

import java.util.List;

public interface CommunityParkingService extends IService<CommunityParking> {
    IPage<CommunityParking> getParkingPage(Integer pageNum, Integer pageSize, String parkingNo, Integer status);
    
    List<CommunityParking> getParkingList();
    
    void addParking(ParkingRequest request);
    
    void updateParking(ParkingRequest request);
    
    void deleteParking(Long id);
    
    CommunityParking getParkingById(Long id);
    
    void updateParkingStatus(Long id, Integer status, Long ownerId);
}