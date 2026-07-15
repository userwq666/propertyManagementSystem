package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityParkingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingDTO;

public interface CommunityParkingService {
    void addParking(CommunityParkingDTO parking);
    void updateParking(CommunityParkingDTO parking);
    void deleteParking(Long id);
    CommunityParkingDomain getParkingById(Long id);
    Page<CommunityParkingDomain> page(int pageNum, int pageSize, String parkingNo, Integer status);
    long countByOwnerId(Long ownerId);
}