package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, EquipmentDomain> implements EquipmentService {

    @Override
    public EquipmentDomain getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<EquipmentDomain> page(int pageNum, int pageSize, Long categoryId, Integer status) {
        LambdaQueryWrapper<EquipmentDomain> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(EquipmentDomain::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(EquipmentDomain::getStatus, EquipmentStatus.of(status));
        }
        wrapper.orderByDesc(EquipmentDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
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
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateEquipment(EquipmentDTO dto) {
        EquipmentDomain existing = this.getById(dto.getId());
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
        EquipmentDomain domain = this.getById(id);
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
}