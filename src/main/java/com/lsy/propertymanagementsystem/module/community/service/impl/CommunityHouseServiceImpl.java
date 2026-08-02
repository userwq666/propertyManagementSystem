package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityBuildingDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseVO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityHouseService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouseDomain> implements CommunityHouseService {

    @Resource
    private CommunityBuildingMapper buildingMapper;

    @Resource
    private CommunityOwnerMapper ownerMapper;

    @Override
    public Page<CommunityHouseVO> page(int pageNum, int pageSize, Long buildingId, Integer houseStatus) {
        LambdaQueryWrapper<CommunityHouseDomain> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CommunityHouseDomain::getBuildingId, buildingId);
        }
        if (houseStatus != null) {
            wrapper.eq(CommunityHouseDomain::getHouseStatus, houseStatus);
        }
        if (SecurityUtils.isOwner()) {
            Long ownerId = getOwnerIdByUserId(SecurityUtils.getCurrentUserId());
            if (ownerId == null) {
                Page<CommunityHouseVO> empty = new Page<>(pageNum, pageSize, 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.eq(CommunityHouseDomain::getOwnerId, ownerId);
        }
        wrapper.orderByDesc(CommunityHouseDomain::getCreateTime);

        Page<CommunityHouseDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        Set<Long> buildingIds = domainPage.getRecords().stream()
                .map(CommunityHouseDomain::getBuildingId).filter(id -> id != null).collect(Collectors.toSet());
        Set<Long> ownerIds = domainPage.getRecords().stream()
                .map(CommunityHouseDomain::getOwnerId).filter(id -> id != null).collect(Collectors.toSet());

        final Map<Long, CommunityBuildingDomain> buildingMap;
        if (buildingIds.isEmpty()) {
            buildingMap = new HashMap<>();
        } else {
            List<CommunityBuildingDomain> buildings = buildingMapper.selectBatchIds(buildingIds);
            buildingMap = buildings.stream().collect(Collectors.toMap(CommunityBuildingDomain::getId, b -> b));
        }
        final Map<Long, CommunityOwnerDomain> ownerMap;
        if (ownerIds.isEmpty()) {
            ownerMap = new HashMap<>();
        } else {
            List<CommunityOwnerDomain> owners = ownerMapper.selectBatchIds(ownerIds);
            ownerMap = owners.stream().collect(Collectors.toMap(CommunityOwnerDomain::getId, o -> o));
        }

        List<CommunityHouseVO> voList = domainPage.getRecords().stream()
                .map(d -> convertToVO(d, buildingMap, ownerMap))
                .collect(Collectors.toList());

        Page<CommunityHouseVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void addHouse(CommunityHouseDTO house) {
        // 同楼栋下房间号唯一性校验
        LambdaQueryWrapper<CommunityHouseDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityHouseDomain::getBuildingId, house.getBuildingId());
        wrapper.eq(CommunityHouseDomain::getRoomNo, house.getRoomNo());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("同一楼栋下房间号已存在");
        }
        // 房屋状态非空置时必须指定业主
        if (house.getHouseStatus() != null && house.getHouseStatus().getValue() != 0 && house.getOwnerId() == null) {
            throw new BusinessException("非空置状态的房屋必须指定业主");
        }
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
    public CommunityHouseVO getHouseById(Long id) {
        CommunityHouseDomain domain = this.getById(id);
        if (domain == null) {
            return null;
        }
        Map<Long, CommunityBuildingDomain> buildingMap = new HashMap<>();
        Map<Long, CommunityOwnerDomain> ownerMap = new HashMap<>();
        if (domain.getBuildingId() != null) {
            CommunityBuildingDomain building = buildingMapper.selectById(domain.getBuildingId());
            if (building != null) {
                buildingMap.put(building.getId(), building);
            }
        }
        if (domain.getOwnerId() != null) {
            CommunityOwnerDomain owner = ownerMapper.selectById(domain.getOwnerId());
            if (owner != null) {
                ownerMap.put(owner.getId(), owner);
            }
        }
        return convertToVO(domain, buildingMap, ownerMap);
    }

    private CommunityHouseVO convertToVO(CommunityHouseDomain domain, Map<Long, CommunityBuildingDomain> buildingMap, Map<Long, CommunityOwnerDomain> ownerMap) {
        CommunityHouseVO vo = new CommunityHouseVO();
        BeanUtils.copyProperties(domain, vo);
        if (domain.getBuildingId() != null) {
            CommunityBuildingDomain building = buildingMap.get(domain.getBuildingId());
            if (building != null) {
                vo.setBuildingNo(building.getBuildingNo());
            }
        }
        if (domain.getOwnerId() != null) {
            CommunityOwnerDomain owner = ownerMap.get(domain.getOwnerId());
            if (owner != null) {
                vo.setOwnerName(owner.getName());
            }
        }
        return vo;
    }

    private Long getOwnerIdByUserId(Long userId) {
        CommunityOwnerDomain owner = ownerMapper.selectOne(new LambdaQueryWrapper<CommunityOwnerDomain>()
                .eq(CommunityOwnerDomain::getUserId, userId));
        return owner != null ? owner.getId() : null;
    }
}
