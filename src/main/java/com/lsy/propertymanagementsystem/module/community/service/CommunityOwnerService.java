package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerDTO;

public interface CommunityOwnerService {
    void addOwner(CommunityOwnerDTO owner);
    void updateOwner(CommunityOwnerDTO owner);
    void deleteOwner(Long id);
    CommunityOwnerDomain getOwnerById(Long id);
    CommunityOwnerDomain getByUserId(Long userId);
    Page<CommunityOwnerDomain> page(int pageNum, int pageSize, String name, String phone);
}