package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityBuildingDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentCategoryDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentVO;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, EquipmentDomain> implements EquipmentService {

    @Autowired
    private EquipmentCategoryMapper equipmentCategoryMapper;

    @Autowired
    private CommunityBuildingMapper communityBuildingMapper;

    @Override
    public EquipmentVO getById(Long id) {
        EquipmentDomain domain = super.getById(id);
        if (domain == null) {
            return null;
        }
        Map<Long, String> categoryNameMap = loadCategoryNameMap(List.of(domain));
        Map<Long, String> buildingNoMap = loadBuildingNoMap(List.of(domain));
        return convertToVO(domain, categoryNameMap, buildingNoMap);
    }

    @Override
    public Page<EquipmentVO> page(int pageNum, int pageSize, Long categoryId, Integer status, String equipmentName) {
        LambdaQueryWrapper<EquipmentDomain> wrapper = new LambdaQueryWrapper<>();
        if (equipmentName != null && !equipmentName.isBlank()) { wrapper.like(EquipmentDomain::getEquipmentName, equipmentName); }
        if (categoryId != null) {
            wrapper.eq(EquipmentDomain::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(EquipmentDomain::getStatus, EquipmentStatus.of(status));
        }
        wrapper.orderByDesc(EquipmentDomain::getCreateTime);
        Page<EquipmentDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        List<EquipmentDomain> records = domainPage.getRecords();
        Map<Long, String> categoryNameMap = loadCategoryNameMap(records);
        Map<Long, String> buildingNoMap = loadBuildingNoMap(records);

        List<EquipmentVO> voRecords = records.stream()
                .map(d -> convertToVO(d, categoryNameMap, buildingNoMap))
                .collect(Collectors.toList());

        Page<EquipmentVO> voPage = new Page<>(domainPage.getCurrent(), domainPage.getSize(), domainPage.getTotal());
        voPage.setRecords(voRecords);
        return voPage;
    }

    @Override
    @Transactional
    public void addEquipment(EquipmentDTO dto) {
        LambdaQueryWrapper<EquipmentDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentDomain::getEquipmentNo, dto.getEquipmentNo());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("设备编号已存在");
        }
        EquipmentDomain domain = new EquipmentDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.setStatus(EquipmentStatus.of(dto.getStatus()));
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateEquipment(EquipmentDTO dto) {
        EquipmentDomain existing = super.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("设备不存在");
        }
        LambdaQueryWrapper<EquipmentDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentDomain::getEquipmentNo, dto.getEquipmentNo());
        wrapper.ne(EquipmentDomain::getId, dto.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("设备编号已存在");
        }
        EquipmentDomain domain = new EquipmentDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.setStatus(EquipmentStatus.of(dto.getStatus()));
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        EquipmentDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("设备不存在");
        }
        domain.changeStatus(EquipmentStatus.of(status));
        this.updateById(domain);
    }

    @Override
    public long countByCategoryId(Long categoryId) {
        return this.count(new LambdaQueryWrapper<EquipmentDomain>().eq(EquipmentDomain::getCategoryId, categoryId));
    }

    private EquipmentVO convertToVO(EquipmentDomain domain, Map<Long, String> categoryNameMap, Map<Long, String> buildingNoMap) {
        EquipmentVO vo = new EquipmentVO();
        BeanUtils.copyProperties(domain, vo);
        vo.setCategoryName(categoryNameMap.get(domain.getCategoryId()));
        vo.setBuildingNo(buildingNoMap.get(domain.getBuildingId()));
        return vo;
    }

    private Map<Long, String> loadCategoryNameMap(List<EquipmentDomain> records) {
        List<Long> categoryIds = records.stream()
                .map(EquipmentDomain::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }
        return equipmentCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(EquipmentCategoryDomain::getId, EquipmentCategoryDomain::getCategoryName, (a, b) -> a));
    }

    private Map<Long, String> loadBuildingNoMap(List<EquipmentDomain> records) {
        List<Long> buildingIds = records.stream()
                .map(EquipmentDomain::getBuildingId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (buildingIds.isEmpty()) {
            return new HashMap<>();
        }
        return communityBuildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(CommunityBuildingDomain::getId, CommunityBuildingDomain::getBuildingNo, (a, b) -> a));
    }
}
