package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.module.community.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityHouseService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouse> implements CommunityHouseService {

    @Autowired
    private FeeRecordService feeRecordService;

    @Autowired
    private RepairRecordService repairRecordService;

    @Override
    public IPage<CommunityHouse> getHousePage(Integer pageNum, Integer pageSize, Long buildingId, String roomNo, Integer houseStatus) {
        LambdaQueryWrapper<CommunityHouse> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CommunityHouse::getBuildingId, buildingId);
        }
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like(CommunityHouse::getRoomNo, roomNo);
        }
        if (houseStatus != null) {
            wrapper.eq(CommunityHouse::getHouseStatus, houseStatus);
        }
        wrapper.orderByDesc(CommunityHouse::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<CommunityHouse> getHouseList() {
        return this.list();
    }

    @Override
    @Transactional
    public void addHouse(HouseRequest request) {
        CommunityHouse house = new CommunityHouse();
        BeanUtils.copyProperties(request, house);
        this.save(house);
    }

    @Override
    @Transactional
    public void updateHouse(HouseRequest request) {
        CommunityHouse existing = this.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("房屋不存在");
        }
        CommunityHouse house = new CommunityHouse();
        BeanUtils.copyProperties(request, house);
        this.updateById(house);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        if (feeRecordService.countByHouseId(id) > 0) {
            throw new BusinessException("该房屋存在关联的收费记录，不允许删除");
        }

        if (repairRecordService.countByHouseId(id) > 0) {
            throw new BusinessException("该房屋存在关联的报修记录，不允许删除");
        }

        this.removeById(id);
    }

    @Override
    public CommunityHouse getHouseById(Long id) {
        return this.getById(id);
    }

    @Override
    public long countByBuildingId(Long buildingId) {
        return this.count(new LambdaQueryWrapper<CommunityHouse>().eq(CommunityHouse::getBuildingId, buildingId));
    }

    @Override
    @Transactional
    public void updateHouseStatus(Long id, Integer houseStatus) {
        CommunityHouse house = this.getById(id);
        if (house == null) {
            throw new BusinessException("房屋不存在");
        }
        house.setHouseStatus(houseStatus);
        this.updateById(house);
    }
}
