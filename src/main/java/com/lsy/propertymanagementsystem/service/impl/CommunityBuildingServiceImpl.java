package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;
import com.lsy.propertymanagementsystem.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.service.CommunityBuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityBuildingServiceImpl extends ServiceImpl<CommunityBuildingMapper, CommunityBuilding> implements CommunityBuildingService {
    
    @Override
    public IPage<CommunityBuilding> getBuildingPage(Integer pageNum, Integer pageSize, String buildingNo) {
        LambdaQueryWrapper<CommunityBuilding> wrapper = new LambdaQueryWrapper<>();
        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.like(CommunityBuilding::getBuildingNo, buildingNo);
        }
        wrapper.orderByAsc(CommunityBuilding::getBuildingNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityBuilding> getBuildingList() {
        LambdaQueryWrapper<CommunityBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityBuilding::getBuildingNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addBuilding(BuildingRequest request) {
        CommunityBuilding building = new CommunityBuilding();
        building.setBuildingNo(request.getBuildingNo());
        building.setFloorCount(request.getFloorCount());
        building.setTotalHouse(request.getTotalHouse());
        building.setBuildYear(request.getBuildYear());
        building.setRemark(request.getRemark());
        this.save(building);
    }
    
    @Override
    @Transactional
    public void updateBuilding(BuildingRequest request) {
        CommunityBuilding building = this.getById(request.getId());
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }
        
        building.setBuildingNo(request.getBuildingNo());
        building.setFloorCount(request.getFloorCount());
        building.setTotalHouse(request.getTotalHouse());
        building.setBuildYear(request.getBuildYear());
        building.setRemark(request.getRemark());
        this.updateById(building);
    }
    
    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityBuilding getBuildingById(Long id) {
        return this.getById(id);
    }
}