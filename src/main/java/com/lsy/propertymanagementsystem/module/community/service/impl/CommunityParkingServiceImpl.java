package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityParkingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingVO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityParkingService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityParkingServiceImpl extends ServiceImpl<CommunityParkingMapper, CommunityParkingDomain> implements CommunityParkingService {

    @Resource
    private CommunityOwnerMapper ownerMapper;

    @Override
    public Page<CommunityParkingVO> page(int pageNum, int pageSize, String parkingNo, Integer status) {
        LambdaQueryWrapper<CommunityParkingDomain> wrapper = new LambdaQueryWrapper<>();
        if (parkingNo != null && !parkingNo.isEmpty()) {
            wrapper.like(CommunityParkingDomain::getParkingNo, parkingNo);
        }
        if (status != null) {
            wrapper.eq(CommunityParkingDomain::getStatus, status);
        }
        if (SecurityUtils.isOwner()) {
            wrapper.eq(CommunityParkingDomain::getOwnerId, SecurityUtils.getCurrentUserId());
        }
        wrapper.orderByDesc(CommunityParkingDomain::getCreateTime);

        Page<CommunityParkingDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        Set<Long> ownerIds = domainPage.getRecords().stream()
                .map(CommunityParkingDomain::getOwnerId).filter(id -> id != null).collect(Collectors.toSet());

        final Map<Long, CommunityOwnerDomain> ownerMap;
        if (ownerIds.isEmpty()) {
            ownerMap = new HashMap<>();
        } else {
            List<CommunityOwnerDomain> owners = ownerMapper.selectBatchIds(ownerIds);
            ownerMap = owners.stream().collect(Collectors.toMap(CommunityOwnerDomain::getId, o -> o));
        }

        List<CommunityParkingVO> voList = domainPage.getRecords().stream()
                .map(d -> convertToVO(d, ownerMap))
                .collect(Collectors.toList());

        Page<CommunityParkingVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void addParking(CommunityParkingDTO parking) {
        CommunityParkingDomain domain = new CommunityParkingDomain();
        BeanUtils.copyProperties(parking, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateParking(CommunityParkingDTO parking) {
        CommunityParkingDomain existing = this.getById(parking.getId());
        if (existing == null) {
            throw new BusinessException("车位不存在");
        }
        BeanUtils.copyProperties(parking, existing);
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteParking(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityParkingVO getParkingById(Long id) {
        CommunityParkingDomain domain = this.getById(id);
        if (domain == null) {
            return null;
        }
        Map<Long, CommunityOwnerDomain> ownerMap = new HashMap<>();
        if (domain.getOwnerId() != null) {
            CommunityOwnerDomain owner = ownerMapper.selectById(domain.getOwnerId());
            if (owner != null) {
                ownerMap.put(owner.getId(), owner);
            }
        }
        return convertToVO(domain, ownerMap);
    }

    private CommunityParkingVO convertToVO(CommunityParkingDomain domain, Map<Long, CommunityOwnerDomain> ownerMap) {
        CommunityParkingVO vo = new CommunityParkingVO();
        BeanUtils.copyProperties(domain, vo);
        if (domain.getOwnerId() != null) {
            CommunityOwnerDomain owner = ownerMap.get(domain.getOwnerId());
            if (owner != null) {
                vo.setOwnerName(owner.getName());
            }
        }
        return vo;
    }
}
