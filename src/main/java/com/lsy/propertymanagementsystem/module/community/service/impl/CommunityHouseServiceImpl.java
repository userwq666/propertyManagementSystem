package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseDTO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityHouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouseDomain> implements CommunityHouseService {

    @Override
    public Page<CommunityHouseDomain> page(int pageNum, int pageSize, Long buildingId, Integer houseStatus) {
        LambdaQueryWrapper<CommunityHouseDomain> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CommunityHouseDomain::getBuildingId, buildingId);
        }
        if (houseStatus != null) {
            wrapper.eq(CommunityHouseDomain::getHouseStatus, houseStatus);
        }
        wrapper.orderByDesc(CommunityHouseDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addHouse(CommunityHouseDTO house) {
        CommunityHouseDomain domain = new CommunityHouseDomain();
        BeanUtils.copyProperties(house, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateHouse(CommunityHouseDTO house) {
        CommunityHouseDomain existing = this.getById(house.getId());
        if (existing == null) {
            throw new BusinessException("房屋不存在");
        }
        LambdaQueryWrapper<CommunityHouseDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityHouseDomain::getRoomNo, house.getRoomNo());
        wrapper.eq(CommunityHouseDomain::getBuildingId, house.getBuildingId());
        wrapper.ne(CommunityHouseDomain::getId, house.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("同一楼栋下房间号已存在");
        }
        BeanUtils.copyProperties(house, existing);
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityHouseDomain getHouseById(Long id) {
        return this.getById(id);
    }

    @Override
    public long countByBuildingId(Long buildingId) {
        return this.count(new LambdaQueryWrapper<CommunityHouseDomain>().eq(CommunityHouseDomain::getBuildingId, buildingId));
    }
}
