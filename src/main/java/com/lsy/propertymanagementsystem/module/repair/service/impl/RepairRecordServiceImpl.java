package com.lsy.propertymanagementsystem.module.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairPriority;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecordDomain> implements RepairRecordService {

    @Override
    public Page<RepairRecordDomain> page(int pageNum, int pageSize, Long ownerId, Integer status) {
        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(RepairRecordDomain::getOwnerId, ownerId);
        }
        if (status != null) {
            wrapper.eq(RepairRecordDomain::getStatus, RepairStatus.of(status));
        }
        wrapper.orderByDesc(RepairRecordDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addRepair(RepairRecordDTO dto) {
        RepairRecordDomain domain = new RepairRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.prepareAdd();
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateRepair(RepairRecordDTO dto) {
        RepairRecordDomain domain = new RepairRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        RepairRecordDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("报修记录不存在");
        }
        existing.setRepairType(domain.getRepairType());
        existing.setRepairContent(domain.getRepairContent());
        existing.setRepairImages(domain.getRepairImages());
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteRepair(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, Long handlerId, String handleContent) {
        RepairRecordDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("报修记录不存在");
        }
        RepairStatus newStatus = RepairStatus.of(status);
        if (newStatus == RepairStatus.PROCESSING) {
            domain.assignHandler(handlerId);
        } else if (newStatus == RepairStatus.COMPLETED) {
            domain.complete(handleContent, null);
        } else {
            domain.setStatus(newStatus);
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer score, String content) {
        if (score < 1 || score > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        RepairRecordDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (domain.getStatus() != RepairStatus.COMPLETED) {
            throw new BusinessException("只有已完成的报修才能评价");
        }
        domain.evaluate(score, content);
        this.updateById(domain);
    }

    @Override
    public long countByHouseId(Long houseId) {
        return this.count(new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getHouseId, houseId));
    }
}