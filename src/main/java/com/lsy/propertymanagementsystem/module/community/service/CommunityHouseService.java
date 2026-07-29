package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseVO;

public interface CommunityHouseService {
    void addHouse(CommunityHouseDTO house);
    void updateHouse(CommunityHouseDTO house);
    void deleteHouse(Long id);
    CommunityHouseVO getHouseById(Long id);
    Page<CommunityHouseVO> page(int pageNum, int pageSize, Long buildingId, Integer houseStatus);
}