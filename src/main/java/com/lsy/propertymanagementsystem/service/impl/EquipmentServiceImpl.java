package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;
import com.lsy.propertymanagementsystem.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    @Transactional
    public void add(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(request, equipment);
        equipment.setStatus(0);
        equipmentMapper.insert(equipment);
    }

    @Override
    @Transactional
    public void update(EquipmentRequest request) {
        Equipment equipment = equipmentMapper.selectById(request.getId());
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        BeanUtils.copyProperties(request, equipment);
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        equipmentMapper.deleteById(id);
    }

    @Override
    public Equipment getById(Long id) {
        return equipmentMapper.selectById(id);
    }

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
        return equipmentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
    }
}
