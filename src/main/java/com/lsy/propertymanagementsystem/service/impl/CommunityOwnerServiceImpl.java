package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;
import com.lsy.propertymanagementsystem.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.service.CommunityOwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityOwnerServiceImpl extends ServiceImpl<CommunityOwnerMapper, CommunityOwner> implements CommunityOwnerService {
    
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
        LambdaQueryWrapper<CommunityOwner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityOwner::getName);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addOwner(OwnerRequest request) {
        CommunityOwner owner = new CommunityOwner();
        owner.setUserId(request.getUserId());
        owner.setName(request.getName());
        owner.setIdCard(request.getIdCard());
        owner.setPhone(request.getPhone());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setEmergencyPhone(request.getEmergencyPhone());
        owner.setCheckInTime(request.getCheckInTime());
        this.save(owner);
    }
    
    @Override
    @Transactional
    public void updateOwner(OwnerRequest request) {
        CommunityOwner owner = this.getById(request.getId());
        if (owner == null) {
            throw new RuntimeException("业主不存在");
        }
        
        owner.setUserId(request.getUserId());
        owner.setName(request.getName());
        owner.setIdCard(request.getIdCard());
        owner.setPhone(request.getPhone());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setEmergencyPhone(request.getEmergencyPhone());
        owner.setCheckInTime(request.getCheckInTime());
        this.updateById(owner);
    }
    
    @Override
    @Transactional
    public void deleteOwner(Long id) {
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
            throw new RuntimeException("业主不存在");
        }
        
        owner.setUserId(userId);
        this.updateById(owner);
    }
}