package com.lsy.propertymanagementsystem.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerVO;

public interface CommunityOwnerService {
    void addOwner(CommunityOwnerDTO owner);
    void updateOwner(CommunityOwnerDTO owner);
    void deleteOwner(Long id);
    CommunityOwnerVO getOwnerById(Long id);
    CommunityOwnerDomain getByUserId(Long userId);
    Page<CommunityOwnerVO> page(int pageNum, int pageSize, String name, String phone);
}