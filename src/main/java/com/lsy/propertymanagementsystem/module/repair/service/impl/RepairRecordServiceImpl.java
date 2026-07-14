package com.lsy.propertymanagementsystem.module.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.repair.entity.RepairRecord;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecord> implements RepairRecordService {

    @Override
    public Page<RepairRecord> page(int pageNum, int pageSize, Long ownerId, Integer status) {
        LambdaQueryWrapper<RepairRecord> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(RepairRecord::getOwnerId, ownerId);
        }
        if (status != null) {
            wrapper.eq(RepairRecord::getStatus, status);
        }
        wrapper.orderByDesc(RepairRecord::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addRepair(RepairRecord record) {
        record.setStatus(0);
        this.save(record);
    }

    @Override
    @Transactional
    public void updateRepair(RepairRecord record) {
        RepairRecord existing = this.getById(record.getId());
        if (existing == null) {
            throw new BusinessException("报修记录不存在");
        }
        existing.setRepairType(record.getRepairType());
        existing.setContent(record.getContent());
        existing.setImgUrl(record.getImgUrl());
        this.updateById(existing);
    }

    @Override
    public long countByHouseId(Long houseId) {
        return this.count(new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getHouseId, houseId));
    }

    @Override
    @Transactional
    public void deleteRepair(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, String handleUser, String handleResult) {
        RepairRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException("报修记录不存在");
        }
        record.setStatus(status);
        if (handleUser != null) {
            record.setHandleUser(handleUser);
        }
        if (handleResult != null) {
            record.setHandleResult(handleResult);
        }
        if (status == 2) {
            record.setFinishTime(LocalDateTime.now());
        }
        this.updateById(record);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        RepairRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (record.getStatus() != 2) {
            throw new BusinessException("只有已完成的报修才能评价");
        }
        record.setRating(rating);
        this.updateById(record);
    }
}