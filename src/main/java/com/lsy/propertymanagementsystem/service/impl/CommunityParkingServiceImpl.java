package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;
import com.lsy.propertymanagementsystem.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.service.CommunityParkingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunityParkingServiceImpl extends ServiceImpl<CommunityParkingMapper, CommunityParking> implements CommunityParkingService {
    
    @Override
    public IPage<CommunityParking> getParkingPage(Integer pageNum, Integer pageSize, String parkingNo, Integer status) {
        LambdaQueryWrapper<CommunityParking> wrapper = new LambdaQueryWrapper<>();
        if (parkingNo != null && !parkingNo.isEmpty()) {
            wrapper.like(CommunityParking::getParkingNo, parkingNo);
        }
        if (status != null) {
            wrapper.eq(CommunityParking::getStatus, status);
        }
        wrapper.orderByAsc(CommunityParking::getParkingNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityParking> getParkingList() {
        LambdaQueryWrapper<CommunityParking> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityParking::getParkingNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addParking(ParkingRequest request) {
        CommunityParking parking = new CommunityParking();
        parking.setParkingNo(request.getParkingNo());
        parking.setParkingType(request.getParkingType() != null ? request.getParkingType() : 0);
        parking.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        parking.setOwnerId(request.getOwnerId());
        parking.setExpireTime(request.getExpireTime());
        parking.setRemark(request.getRemark());
        this.save(parking);
    }
    
    @Override
    @Transactional
    public void updateParking(ParkingRequest request) {
        CommunityParking parking = this.getById(request.getId());
        if (parking == null) {
            throw new RuntimeException("车位不存在");
        }
        
        parking.setParkingNo(request.getParkingNo());
        parking.setParkingType(request.getParkingType());
        parking.setStatus(request.getStatus());
        parking.setOwnerId(request.getOwnerId());
        parking.setExpireTime(request.getExpireTime());
        parking.setRemark(request.getRemark());
        this.updateById(parking);
    }
    
    @Override
    @Transactional
    public void deleteParking(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityParking getParkingById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void updateParkingStatus(Long id, Integer status, Long ownerId) {
        CommunityParking parking = this.getById(id);
        if (parking == null) {
            throw new RuntimeException("车位不存在");
        }
        
        parking.setStatus(status);
        parking.setOwnerId(status == 1 ? ownerId : null);
        parking.setExpireTime(status == 1 ? LocalDateTime.now().plusYears(1) : null);
        this.updateById(parking);
    }
}