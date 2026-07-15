package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityParkingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingDTO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityParkingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityParkingServiceImpl extends ServiceImpl<CommunityParkingMapper, CommunityParkingDomain> implements CommunityParkingService {

    @Override
    public Page<CommunityParkingDomain> page(int pageNum, int pageSize, String parkingNo, Integer status) {
        LambdaQueryWrapper<CommunityParkingDomain> wrapper = new LambdaQueryWrapper<>();
        if (parkingNo != null && !parkingNo.isEmpty()) {
            wrapper.like(CommunityParkingDomain::getParkingNo, parkingNo);
        }
        if (status != null) {
            wrapper.eq(CommunityParkingDomain::getStatus, status);
        }
        wrapper.orderByDesc(CommunityParkingDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addParking(CommunityParkingDTO parking) {
        CommunityParkingDomain domain = new CommunityParkingDomain();
        BeanUtils.copyProperties(parking, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateParking(CommunityParkingDTO parking) {
        CommunityParkingDomain existing = this.getById(parking.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("车位不存在");
        }
        CommunityParkingDomain domain = new CommunityParkingDomain();
        BeanUtils.copyProperties(parking, domain);
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void deleteParking(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityParkingDomain getParkingById(Long id) {
        return this.getById(id);
    }

    @Override
    public long countByOwnerId(Long ownerId) {
        return this.count(new LambdaQueryWrapper<CommunityParkingDomain>().eq(CommunityParkingDomain::getOwnerId, ownerId));
    }
}