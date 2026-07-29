package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingVO;

public interface CommunityParkingService {
    void addParking(CommunityParkingDTO parking);
    void updateParking(CommunityParkingDTO parking);
    void deleteParking(Long id);
    CommunityParkingVO getParkingById(Long id);
    Page<CommunityParkingVO> page(int pageNum, int pageSize, String parkingNo, Integer status);
    long countByOwnerId(Long ownerId);
}