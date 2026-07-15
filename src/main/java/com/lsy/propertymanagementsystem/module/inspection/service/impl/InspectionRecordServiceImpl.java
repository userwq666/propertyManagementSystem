package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordDTO;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Override
    public Page<InspectionRecordDomain> page(int pageNum, int pageSize, Long planId, Long equipmentId) {
        LambdaQueryWrapper<InspectionRecordDomain> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(InspectionRecordDomain::getPlanId, planId);
        }
        if (equipmentId != null) {
            wrapper.eq(InspectionRecordDomain::getEquipmentId, equipmentId);
        }
        wrapper.orderByDesc(InspectionRecordDomain::getCreateTime);
        return inspectionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addRecord(InspectionRecordDTO dto) {
        InspectionRecordDomain domain = new InspectionRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        inspectionRecordMapper.insert(domain);
    }

    @Override
    @Transactional
    public void addRecordDomain(InspectionRecordDomain record) {
        inspectionRecordMapper.insert(record);
    }

    @Override
    public InspectionRecordDomain getRecordById(Long id) {
        return inspectionRecordMapper.selectById(id);
    }

    @Override
    @Transactional
    public void updateRecord(InspectionRecordDTO dto) {
        InspectionRecordDomain domain = new InspectionRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        InspectionRecordDomain existing = inspectionRecordMapper.selectById(domain.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检记录不存在");
        }
        inspectionRecordMapper.updateById(domain);
    }

    @Override
    public long countByPlanId(Long planId) {
        return inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>().eq(InspectionRecordDomain::getPlanId, planId));
    }
}