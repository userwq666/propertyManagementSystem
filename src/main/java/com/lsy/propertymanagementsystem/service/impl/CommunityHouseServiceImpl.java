package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.service.CommunityHouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouse> implements CommunityHouseService {
    
    @Override
    public IPage<CommunityHouse> getHousePage(Integer pageNum, Integer pageSize, Long buildingId, String roomNo, Integer houseStatus) {
        LambdaQueryWrapper<CommunityHouse> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CommunityHouse::getBuildingId, buildingId);
        }
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like(CommunityHouse::getRoomNo, roomNo);
        }
        if (houseStatus != null) {
            wrapper.eq(CommunityHouse::getHouseStatus, houseStatus);
        }
        wrapper.orderByAsc(CommunityHouse::getBuildingId, CommunityHouse::getRoomNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityHouse> getHouseList() {
        LambdaQueryWrapper<CommunityHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityHouse::getBuildingId, CommunityHouse::getRoomNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addHouse(HouseRequest request) {
        CommunityHouse house = new CommunityHouse();
        house.setBuildingId(request.getBuildingId());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setHouseType(request.getHouseType());
        house.setHouseStatus(request.getHouseStatus() != null ? request.getHouseStatus() : 0);
        house.setOwnerId(request.getOwnerId());
        house.setRemark(request.getRemark());
        this.save(house);
    }
    
    @Override
    @Transactional
    public void updateHouse(HouseRequest request) {
        CommunityHouse house = this.getById(request.getId());
        if (house == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        house.setBuildingId(request.getBuildingId());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setHouseType(request.getHouseType());
        house.setHouseStatus(request.getHouseStatus());
        house.setOwnerId(request.getOwnerId());
        house.setRemark(request.getRemark());
        this.updateById(house);
    }
    
    @Override
    @Transactional
    public void deleteHouse(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityHouse getHouseById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void updateHouseStatus(Long id, Integer houseStatus) {
        CommunityHouse house = this.getById(id);
        if (house == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        house.setHouseStatus(houseStatus);
        this.updateById(house);
    }
}