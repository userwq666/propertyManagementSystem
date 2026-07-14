package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityOwner;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityParking;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityOwnerService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityOwnerServiceImpl extends ServiceImpl<CommunityOwnerMapper, CommunityOwner> implements CommunityOwnerService {

    @Autowired
    private CommunityHouseMapper houseMapper;

    @Autowired
    private CommunityParkingMapper parkingMapper;

    @Autowired
    private FeeRecordService feeRecordService;

    @Override
    public IPage<CommunityOwner> getOwnerPage(Integer pageNum, Integer pageSize, String name, String phone) {
        LambdaQueryWrapper<CommunityOwner> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CommunityOwner::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(CommunityOwner::getPhone, phone);
        }
        wrapper.orderByDesc(CommunityOwner::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<CommunityOwner> getOwnerList() {
        return this.list();
    }

    @Override
    @Transactional
    public void addOwner(OwnerRequest request) {
        CommunityOwner owner = new CommunityOwner();
        BeanUtils.copyProperties(request, owner);
        this.save(owner);
    }

    @Override
    @Transactional
    public void updateOwner(OwnerRequest request) {
        CommunityOwner existing = this.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("业主不存在");
        }
        CommunityOwner owner = new CommunityOwner();
        BeanUtils.copyProperties(request, owner);
        this.updateById(owner);
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) {
        LambdaQueryWrapper<CommunityHouse> houseWrapper = new LambdaQueryWrapper<>();
        houseWrapper.eq(CommunityHouse::getOwnerId, id);
        if (houseMapper.selectCount(houseWrapper) > 0) {
            throw new BusinessException("该业主存在关联的房屋，不允许删除");
        }

        LambdaQueryWrapper<CommunityParking> parkingWrapper = new LambdaQueryWrapper<>();
        parkingWrapper.eq(CommunityParking::getOwnerId, id);
        if (parkingMapper.selectCount(parkingWrapper) > 0) {
            throw new BusinessException("该业主存在关联的车位，不允许删除");
        }

        if (feeRecordService.countByOwnerId(id) > 0) {
            throw new BusinessException("该业主存在关联的收费记录，不允许删除");
        }

        this.removeById(id);
    }

    @Override
    public CommunityOwner getOwnerById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public void bindUser(Long ownerId, Long userId) {
        CommunityOwner owner = this.getById(ownerId);
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        owner.setUserId(userId);
        this.updateById(owner);
    }
}
