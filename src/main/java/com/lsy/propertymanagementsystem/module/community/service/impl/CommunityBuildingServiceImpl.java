package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityBuildingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingDTO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityBuildingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityBuildingServiceImpl extends ServiceImpl<CommunityBuildingMapper, CommunityBuildingDomain> implements CommunityBuildingService {

    @Override
    public Page<CommunityBuildingDomain> page(int pageNum, int pageSize) {
        LambdaQueryWrapper<CommunityBuildingDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CommunityBuildingDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addBuilding(CommunityBuildingDTO building) {
        CommunityBuildingDomain domain = new CommunityBuildingDomain();
        BeanUtils.copyProperties(building, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateBuilding(CommunityBuildingDTO building) {
        CommunityBuildingDomain existing = this.getById(building.getId());
        if (existing == null) {
            throw new BusinessException("楼栋不存在");
        }
        BeanUtils.copyProperties(building, existing);
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityBuildingDomain getBuildingById(Long id) {
        return this.getById(id);
    }
}
