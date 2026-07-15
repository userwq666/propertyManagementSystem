package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeCycleType;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeItemType;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItemDomain> implements FeeItemService {

    @Autowired
    @Lazy
    private FeeRecordService feeRecordService;

    @Override
    public FeeItemDomain getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<FeeItemDomain> page(int pageNum, int pageSize, String itemName, Integer status) {
        LambdaQueryWrapper<FeeItemDomain> wrapper = new LambdaQueryWrapper<>();
        if (itemName != null && !itemName.isEmpty()) {
            wrapper.like(FeeItemDomain::getItemName, itemName);
        }
        if (status != null) {
            wrapper.eq(FeeItemDomain::getStatus, EnableStatus.of(status));
        }
        wrapper.orderByDesc(FeeItemDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void add(FeeItemDTO domain) {
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        this.save(feeItemDomain);
    }

    @Override
    @Transactional
    public void update(FeeItemDTO domain) {
        FeeItemDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("收费项目不存在");
        }
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        this.updateById(feeItemDomain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (feeRecordService.countByItemId(id) > 0) {
            throw new BusinessException("该收费项目存在关联的收费记录，不允许删除");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        FeeItemDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("收费项目不存在");
        }
        domain.changeStatus(EnableStatus.of(status));
        this.updateById(domain);
    }
}