package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Override
    @Transactional
    public void addRecord(InspectionRecord record) {
        record.setInspectTime(LocalDateTime.now());
        inspectionRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void updateRecord(InspectionRecord record) {
        InspectionRecord existing = inspectionRecordMapper.selectById(record.getId());
        if (existing == null) {
            throw new BusinessException("巡检记录不存在");
        }
        existing.setResult(record.getResult());
        existing.setFaultDesc(record.getFaultDesc());
        existing.setRepairSuggestion(record.getRepairSuggestion());
        existing.setBudget(record.getBudget());
        existing.setDuration(record.getDuration());
        inspectionRecordMapper.updateById(existing);
    }

    @Override
    public long countByPlanId(Long planId) {
        return inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecord>().eq(InspectionRecord::getPlanId, planId));
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        inspectionRecordMapper.deleteById(id);
    }

    @Override
    public InspectionRecord getById(Long id) {
        return inspectionRecordMapper.selectById(id);
    }

    @Override
    public Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        if (equipmentId != null) {
            wrapper.eq(InspectionRecord::getEquipmentId, equipmentId);
        }
        if (result != null) {
            wrapper.eq(InspectionRecord::getResult, result);
        }
        wrapper.orderByDesc(InspectionRecord::getInspectTime);
        return inspectionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<InspectionRecord> getByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionRecord::getEquipmentId, equipmentId);
        wrapper.orderByDesc(InspectionRecord::getInspectTime);
        return inspectionRecordMapper.selectList(wrapper);
    }
}