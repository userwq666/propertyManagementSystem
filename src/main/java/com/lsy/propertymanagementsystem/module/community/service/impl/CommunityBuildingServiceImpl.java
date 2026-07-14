package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityBuilding;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityBuildingService;
import com.lsy.propertymanagementsystem.module.community.service.CommunityHouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityBuildingServiceImpl extends ServiceImpl<CommunityBuildingMapper, CommunityBuilding> implements CommunityBuildingService {

    @Autowired
    private CommunityHouseService houseService;

    @Override
    public IPage<CommunityBuilding> getBuildingPage(Integer pageNum, Integer pageSize, String buildingNo) {
        LambdaQueryWrapper<CommunityBuilding> wrapper = new LambdaQueryWrapper<>();
        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.like(CommunityBuilding::getBuildingNo, buildingNo);
        }
        wrapper.orderByDesc(CommunityBuilding::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<CommunityBuilding> getBuildingList() {
        return this.list();
    }

    @Override
    @Transactional
    public void addBuilding(BuildingRequest request) {
        CommunityBuilding building = new CommunityBuilding();
        BeanUtils.copyProperties(request, building);
        this.save(building);
    }

    @Override
    @Transactional
    public void updateBuilding(BuildingRequest request) {
        CommunityBuilding existing = this.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("楼栋不存在");
        }
        CommunityBuilding building = new CommunityBuilding();
        BeanUtils.copyProperties(request, building);
        this.updateById(building);
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        if (houseService.countByBuildingId(id) > 0) {
            throw new BusinessException("该楼栋下存在房屋，不允许删除");
        }
        this.removeById(id);
    }

    @Override
    public CommunityBuilding getBuildingById(Long id) {
        return this.getById(id);
    }
}
