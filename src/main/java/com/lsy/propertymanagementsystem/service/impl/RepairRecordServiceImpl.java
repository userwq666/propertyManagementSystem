package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.RepairRecordRequest;
import com.lsy.propertymanagementsystem.entity.RepairRecord;
import com.lsy.propertymanagementsystem.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.service.RepairRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class RepairRecordServiceImpl implements RepairRecordService {

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Override
    public void add(RepairRecordRequest request) {
        RepairRecord repairRecord = new RepairRecord();
        BeanUtils.copyProperties(request, repairRecord);
        repairRecord.setStatus(0);
        repairRecordMapper.insert(repairRecord);
    }

    @Override
    public void update(RepairRecordRequest request) {
        RepairRecord repairRecord = repairRecordMapper.selectById(request.getId());
        if (repairRecord == null) {
            throw new BusinessException("报修记录不存在");
        }
        BeanUtils.copyProperties(request, repairRecord);
        repairRecordMapper.updateById(repairRecord);
    }

    @Override
    public void delete(Long id) {
        repairRecordMapper.deleteById(id);
    }

    @Override
    public RepairRecord getById(Long id) {
        return repairRecordMapper.selectById(id);
    }

    @Override
    public Page<RepairRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status) {
        LambdaQueryWrapper<RepairRecord> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(RepairRecord::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(RepairRecord::getHouseId, houseId);
        }
        if (status != null) {
            wrapper.eq(RepairRecord::getStatus, status);
        }
        wrapper.orderByDesc(RepairRecord::getCreateTime);
        return repairRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status, String handleUser, String handleResult) {
        RepairRecord repairRecord = repairRecordMapper.selectById(id);
        if (repairRecord == null) {
            throw new BusinessException("报修记录不存在");
        }
        repairRecord.setStatus(status);
        if (handleUser != null) {
            repairRecord.setHandleUser(handleUser);
        }
        if (handleResult != null) {
            repairRecord.setHandleResult(handleResult);
        }
        if (status == 2) {
            repairRecord.setFinishTime(LocalDateTime.now());
        }
        repairRecordMapper.updateById(repairRecord);
    }

    @Override
    public void updateRating(Long id, Integer rating) {
        RepairRecord repairRecord = repairRecordMapper.selectById(id);
        if (repairRecord == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (repairRecord.getStatus() != 2) {
            throw new BusinessException("只能评价已完成的报修");
        }
        repairRecord.setRating(rating);
        repairRecordMapper.updateById(repairRecord);
    }
}
