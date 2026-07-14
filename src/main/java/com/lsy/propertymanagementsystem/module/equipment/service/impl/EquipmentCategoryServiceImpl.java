package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentCategoryService;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentCategoryServiceImpl extends ServiceImpl<EquipmentCategoryMapper, EquipmentCategory> implements EquipmentCategoryService {

    @Autowired
    private EquipmentService equipmentService;

    @Override
    public Page<EquipmentCategory> page(int pageNum, int pageSize, String categoryName) {
        LambdaQueryWrapper<EquipmentCategory> wrapper = new LambdaQueryWrapper<>();
        if (categoryName != null && !categoryName.isEmpty()) {
            wrapper.like(EquipmentCategory::getCategoryName, categoryName);
        }
        wrapper.orderByAsc(EquipmentCategory::getSortOrder);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addCategory(EquipmentCategory category) {
        this.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(EquipmentCategory category) {
        EquipmentCategory existing = this.getById(category.getId());
        if (existing == null) {
            throw new BusinessException("设备分类不存在");
        }
        this.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (equipmentService.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下存在设备，不允许删除");
        }
        this.removeById(id);
    }

}