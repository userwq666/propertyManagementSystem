package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemVO;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeCycleType;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeItemType;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
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
    public FeeItemVO getById(Long id) {
        FeeItemDomain domain = super.getById(id);
        return convertToVO(domain);
    }

    @Override
    public Page<FeeItemVO> page(int pageNum, int pageSize, String itemName, Integer status) {
        LambdaQueryWrapper<FeeItemDomain> wrapper = new LambdaQueryWrapper<>();
        if (itemName != null && !itemName.isEmpty()) {
            wrapper.like(FeeItemDomain::getItemName, itemName);
        }
        if (status != null) {
            wrapper.eq(FeeItemDomain::getStatus, EnableStatus.of(status));
        }
        wrapper.orderByDesc(FeeItemDomain::getCreateTime);
        Page<FeeItemDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        Page<FeeItemVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(domainPage.getTotal());
        voPage.setRecords(domainPage.getRecords().stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void add(FeeItemDTO domain) {
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        feeItemDomain.setItemType(FeeItemType.of(domain.getItemType()));
        feeItemDomain.setCycleType(FeeCycleType.of(domain.getCycleType()));
        feeItemDomain.setStatus(EnableStatus.of(domain.getStatus()));
        this.save(feeItemDomain);
    }

    @Override
    @Transactional
    public void update(FeeItemDTO domain) {
        FeeItemDomain existing = super.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("收费项目不存在");
        }
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        if (domain.getItemType() != null) {
            feeItemDomain.setItemType(FeeItemType.of(domain.getItemType()));
        }
        if (domain.getCycleType() != null) {
            feeItemDomain.setCycleType(FeeCycleType.of(domain.getCycleType()));
        }
        if (domain.getStatus() != null) {
            feeItemDomain.setStatus(EnableStatus.of(domain.getStatus()));
        }
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
        FeeItemDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("收费项目不存在");
        }
        domain.changeStatus(EnableStatus.of(status));
        this.updateById(domain);
    }

    private FeeItemVO convertToVO(FeeItemDomain domain) {
        if (domain == null) {
            return null;
        }
        FeeItemVO vo = new FeeItemVO();
        BeanUtils.copyProperties(domain, vo);
        return vo;
    }
}
