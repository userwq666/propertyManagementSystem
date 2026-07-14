package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityOwner;

import java.util.List;

public interface CommunityOwnerService extends IService<CommunityOwner> {
    IPage<CommunityOwner> getOwnerPage(Integer pageNum, Integer pageSize, String name, String phone);
    
    List<CommunityOwner> getOwnerList();
    
    void addOwner(OwnerRequest request);
    
    void updateOwner(OwnerRequest request);
    
    void deleteOwner(Long id);
    
    CommunityOwner getOwnerById(Long id);
    
    void bindUser(Long ownerId, Long userId);
}