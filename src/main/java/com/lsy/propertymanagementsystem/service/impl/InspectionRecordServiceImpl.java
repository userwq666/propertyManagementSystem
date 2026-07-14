package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.service.InspectionRecordService;
import org.springframework.beans.BeanUtils;
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
    public void add(InspectionRecordRequest request) {
        InspectionRecord record = new InspectionRecord();
        BeanUtils.copyProperties(request, record);
        record.setInspectTime(LocalDateTime.now());
        inspectionRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void update(InspectionRecordRequest request) {
        InspectionRecord record = inspectionRecordMapper.selectById(request.getId());
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        BeanUtils.copyProperties(request, record);
        inspectionRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void delete(Long id) {
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
