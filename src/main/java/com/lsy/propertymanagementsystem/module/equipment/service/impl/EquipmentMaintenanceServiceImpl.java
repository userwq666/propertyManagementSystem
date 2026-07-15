package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMaintenanceMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentMaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentMaintenanceServiceImpl extends ServiceImpl<EquipmentMaintenanceMapper, EquipmentMaintenanceDomain> implements EquipmentMaintenanceService {

    @Override
    @Transactional
    public void add(EquipmentMaintenanceDomain domain) {
        if (domain.getStatus() == null) {
            domain.setStatus(0);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(EquipmentMaintenanceDomain domain) {
        EquipmentMaintenanceDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("维保记录不存在");
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    public EquipmentMaintenanceDomain getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<EquipmentMaintenanceDomain> page(int pageNum, int pageSize, Long equipmentId, Integer maintenanceType, Integer status) {
        LambdaQueryWrapper<EquipmentMaintenanceDomain> wrapper = new LambdaQueryWrapper<>();
        if (equipmentId != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getEquipmentId, equipmentId);
        }
        if (maintenanceType != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getMaintenanceType, maintenanceType);
        }
        if (status != null) {
            wrapper.eq(EquipmentMaintenanceDomain::getStatus, status);
        }
        wrapper.orderByDesc(EquipmentMaintenanceDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void startMaintenance(Long id) {
        EquipmentMaintenanceDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("维保记录不存在");
        }
        domain.start();
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void completeMaintenance(Long id) {
        EquipmentMaintenanceDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("维保记录不存在");
        }
        domain.complete();
        this.updateById(domain);
    }

    @Override
    public long countByEquipmentId(Long equipmentId) {
        return this.count(new LambdaQueryWrapper<EquipmentMaintenanceDomain>().eq(EquipmentMaintenanceDomain::getEquipmentId, equipmentId));
    }
}
