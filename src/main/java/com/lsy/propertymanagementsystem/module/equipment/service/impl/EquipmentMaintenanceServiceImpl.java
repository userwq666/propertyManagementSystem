package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentMaintenanceDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentMaintenanceVO;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceStatus;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceType;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMaintenanceMapper;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentMaintenanceService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
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
public class EquipmentMaintenanceServiceImpl extends ServiceImpl<EquipmentMaintenanceMapper, EquipmentMaintenanceDomain> implements EquipmentMaintenanceService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void add(EquipmentMaintenanceDTO dto) {
        EquipmentMaintenanceDomain domain = new EquipmentMaintenanceDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getMaintenanceType() != null) {
            domain.setMaintenanceType(MaintenanceType.of(dto.getMaintenanceType()));
        }
        if (dto.getStatus() == null) {
            domain.setStatus(MaintenanceStatus.PENDING);
        } else {
            domain.setStatus(MaintenanceStatus.of(dto.getStatus()));
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(EquipmentMaintenanceDTO dto) {
        EquipmentMaintenanceDomain existing = super.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("维保记录不存在");
        }
        EquipmentMaintenanceDomain domain = new EquipmentMaintenanceDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getMaintenanceType() != null) {
            domain.setMaintenanceType(MaintenanceType.of(dto.getMaintenanceType()));
        }
        if (dto.getStatus() != null) {
            domain.setStatus(MaintenanceStatus.of(dto.getStatus()));
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    public EquipmentMaintenanceVO getById(Long id) {
        EquipmentMaintenanceDomain domain = super.getById(id);
        if (domain == null) {
            return null;
        }
        Map<Long, String> equipmentNameMap = loadEquipmentNameMap(List.of(domain));
        Map<Long, String> personnelNameMap = loadPersonnelNameMap(List.of(domain));
        return convertToVO(domain, equipmentNameMap, personnelNameMap);
    }

    @Override
    public Page<EquipmentMaintenanceVO> page(int pageNum, int pageSize, Long equipmentId, Integer maintenanceType, Integer status) {
        LambdaQueryWrapper<EquipmentMaintenanceDomain> wrapper = new LambdaQueryWrapper<>();
        if (equipmentId != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getEquipmentId, equipmentId);
        }
        if (maintenanceType != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getMaintenanceType, MaintenanceType.of(maintenanceType));
        }
        if (status != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getStatus, MaintenanceStatus.of(status));
        }
        wrapper.orderByDesc(EquipmentMaintenanceDomain::getCreateTime);
        Page<EquipmentMaintenanceDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        List<EquipmentMaintenanceDomain> records = domainPage.getRecords();
        Map<Long, String> equipmentNameMap = loadEquipmentNameMap(records);
        Map<Long, String> personnelNameMap = loadPersonnelNameMap(records);

        List<EquipmentMaintenanceVO> voRecords = records.stream()
                .map(d -> convertToVO(d, equipmentNameMap, personnelNameMap))
                .collect(Collectors.toList());

        Page<EquipmentMaintenanceVO> voPage = new Page<>(domainPage.getCurrent(), domainPage.getSize(), domainPage.getTotal());
        voPage.setRecords(voRecords);
        return voPage;
    }

    @Override
    @Transactional
    public void startMaintenance(Long id) {
        EquipmentMaintenanceDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("维保记录不存在");
        }
        domain.start();
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void completeMaintenance(Long id) {
        EquipmentMaintenanceDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("维保记录不存在");
        }
        domain.complete();
        this.updateById(domain);
    }

    private EquipmentMaintenanceVO convertToVO(EquipmentMaintenanceDomain domain, Map<Long, String> equipmentNameMap, Map<Long, String> personnelNameMap) {
        EquipmentMaintenanceVO vo = new EquipmentMaintenanceVO();
        BeanUtils.copyProperties(domain, vo);
        vo.setMaintenanceType(domain.getMaintenanceType() != null ? domain.getMaintenanceType().getValue() : null);
        vo.setStatus(domain.getStatus() != null ? domain.getStatus().getValue() : null);
        vo.setEquipmentName(equipmentNameMap.get(domain.getEquipmentId()));
        vo.setMaintenancePersonnelName(personnelNameMap.get(domain.getMaintenancePersonnelId()));
        return vo;
    }

    private Map<Long, String> loadEquipmentNameMap(List<EquipmentMaintenanceDomain> records) {
        List<Long> equipmentIds = records.stream()
                .map(EquipmentMaintenanceDomain::getEquipmentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (equipmentIds.isEmpty()) {
            return new HashMap<>();
        }
        return equipmentMapper.selectBatchIds(equipmentIds).stream()
                .collect(Collectors.toMap(EquipmentDomain::getId, EquipmentDomain::getEquipmentName, (a, b) -> a));
    }

    private Map<Long, String> loadPersonnelNameMap(List<EquipmentMaintenanceDomain> records) {
        List<Long> personnelIds = records.stream()
                .map(EquipmentMaintenanceDomain::getMaintenancePersonnelId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (personnelIds.isEmpty()) {
            return new HashMap<>();
        }
        return sysUserMapper.selectBatchIds(personnelIds).stream()
                .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName, (a, b) -> a));
    }
}
