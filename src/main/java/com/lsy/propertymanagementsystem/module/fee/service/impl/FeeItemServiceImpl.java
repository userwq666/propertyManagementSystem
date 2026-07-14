package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.module.fee.entity.FeeItem;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItem> implements FeeItemService {

    @Autowired
    private FeeRecordService feeRecordService;

    @Override
    public Page<FeeItem> page(int pageNum, int pageSize, String itemName, Integer status) {
        LambdaQueryWrapper<FeeItem> wrapper = new LambdaQueryWrapper<>();
        if (itemName != null && !itemName.isEmpty()) {
            wrapper.like(FeeItem::getItemName, itemName);
        }
        if (status != null) {
            wrapper.eq(FeeItem::getStatus, status);
        }
        wrapper.orderByDesc(FeeItem::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void add(FeeItemRequest request) {
        FeeItem feeItem = new FeeItem();
        BeanUtils.copyProperties(request, feeItem);
        this.save(feeItem);
    }

    @Override
    @Transactional
    public void update(FeeItemRequest request) {
        FeeItem existing = this.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("收费项目不存在");
        }
        FeeItem feeItem = new FeeItem();
        BeanUtils.copyProperties(request, feeItem);
        this.updateById(feeItem);
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
        FeeItem feeItem = this.getById(id);
        if (feeItem == null) {
            throw new BusinessException("收费项目不存在");
        }
        feeItem.setStatus(status);
        this.updateById(feeItem);
    }
}
