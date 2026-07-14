package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityParking;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityParkingService;
import org.springframework.beans.BeanUtils;
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
        wrapper.orderByDesc(CommunityParking::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<CommunityParking> getParkingList() {
        return this.list();
    }

    @Override
    @Transactional
    public void addParking(ParkingRequest request) {
        CommunityParking parking = new CommunityParking();
        BeanUtils.copyProperties(request, parking);
        this.save(parking);
    }

    @Override
    @Transactional
    public void updateParking(ParkingRequest request) {
        CommunityParking existing = this.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("车位不存在");
        }
        CommunityParking parking = new CommunityParking();
        BeanUtils.copyProperties(request, parking);
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
            throw new BusinessException("车位不存在");
        }
        parking.setStatus(status);
        if (status == 1) {
            parking.setOwnerId(ownerId);
            parking.setExpireTime(LocalDateTime.now().plusYears(1));
        } else {
            parking.setExpireTime(null);
            parking.setOwnerId(null);
        }
        this.updateById(parking);
    }
}
