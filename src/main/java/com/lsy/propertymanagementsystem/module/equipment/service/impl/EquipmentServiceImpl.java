package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.entity.Equipment;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Equipment::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Equipment::getStatus, status);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addEquipment(Equipment equipment) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getEquipmentCode, equipment.getEquipmentCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("设备编号已存在");
        }
        this.save(equipment);
    }

    @Override
    @Transactional
    public void updateEquipment(Equipment equipment) {
        Equipment existing = this.getById(equipment.getId());
        if (existing == null) {
            throw new BusinessException("设备不存在");
        }
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getEquipmentCode, equipment.getEquipmentCode());
        wrapper.ne(Equipment::getId, equipment.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("设备编号已存在");
        }
        this.updateById(equipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        this.removeById(id);
    }

    @Override
    public long countByCategoryId(Long categoryId) {
        return this.count(new LambdaQueryWrapper<Equipment>().eq(Equipment::getCategoryId, categoryId));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Equipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipment.setStatus(status);
        this.updateById(equipment);
    }
}