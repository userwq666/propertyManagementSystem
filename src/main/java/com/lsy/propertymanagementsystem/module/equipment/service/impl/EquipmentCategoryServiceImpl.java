package com.lsy.propertymanagementsystem.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentCategoryDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryDTO;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentCategoryService;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentCategoryServiceImpl extends ServiceImpl<EquipmentCategoryMapper, EquipmentCategoryDomain> implements EquipmentCategoryService {

    @Autowired
    private EquipmentService equipmentService;

    @Override
    public List<EquipmentCategoryDomain> getList() {
        return this.list(new LambdaQueryWrapper<EquipmentCategoryDomain>().orderByAsc(EquipmentCategoryDomain::getSort));
    }

    @Override
    @Transactional
    public void add(EquipmentCategoryDTO category) {
        EquipmentCategoryDomain domain = new EquipmentCategoryDomain();
        BeanUtils.copyProperties(category, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(EquipmentCategoryDTO category) {
        EquipmentCategoryDomain existing = this.getById(category.getId());
        if (existing == null) {
            throw new BusinessException("设备分类不存在");
        }
        EquipmentCategoryDomain domain = new EquipmentCategoryDomain();
        BeanUtils.copyProperties(category, domain);
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (equipmentService.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下存在设备，不允许删除");
        }
        this.removeById(id);
    }

    @Override
    public Page<EquipmentCategoryDomain> page(int pageNum, int pageSize) {
        return super.page(new Page<>(pageNum, pageSize));
    }
}