package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.service.EquipmentCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentCategoryServiceImpl implements EquipmentCategoryService {

    @Autowired
    private EquipmentCategoryMapper equipmentCategoryMapper;

    @Override
    @Transactional
    public void add(EquipmentCategoryRequest request) {
        EquipmentCategory category = new EquipmentCategory();
        BeanUtils.copyProperties(request, category);
        category.setIsDefault(0);
        equipmentCategoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void update(EquipmentCategoryRequest request) {
        EquipmentCategory category = equipmentCategoryMapper.selectById(request.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getIsDefault() == 1) {
            throw new BusinessException("预设分类不可修改");
        }
        BeanUtils.copyProperties(request, category);
        equipmentCategoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EquipmentCategory category = equipmentCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getIsDefault() == 1) {
            throw new BusinessException("预设分类不可删除");
        }
        equipmentCategoryMapper.deleteById(id);
    }

    @Override
    public EquipmentCategory getById(Long id) {
        return equipmentCategoryMapper.selectById(id);
    }

    @Override
    public List<EquipmentCategory> list() {
        LambdaQueryWrapper<EquipmentCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EquipmentCategory::getSortOrder);
        return equipmentCategoryMapper.selectList(wrapper);
    }
}
