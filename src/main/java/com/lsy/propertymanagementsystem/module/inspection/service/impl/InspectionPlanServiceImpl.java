package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanEquipmentDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanInspectorDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanDTO;
import com.lsy.propertymanagementsystem.module.inspection.enums.FrequencyType;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanEquipmentMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanInspectorMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InspectionPlanServiceImpl extends ServiceImpl<InspectionPlanMapper, InspectionPlanDomain> implements InspectionPlanService {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @Autowired
    private InspectionPlanEquipmentMapper planEquipmentMapper;

    @Autowired
    private InspectionPlanInspectorMapper planInspectorMapper;

    @Override
    public Page<InspectionPlanDomain> page(int pageNum, int pageSize, String planName, Integer status) {
        LambdaQueryWrapper<InspectionPlanDomain> wrapper = new LambdaQueryWrapper<>();
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(InspectionPlanDomain::getPlanName, planName);
        }
        if (status != null) {
            wrapper.eq(InspectionPlanDomain::getStatus, EnableStatus.of(status));
        }
        wrapper.orderByDesc(InspectionPlanDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addPlan(InspectionPlanDTO dto) {
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        if (domain.getStatus() == null) {
            domain.setStatus(EnableStatus.ENABLED);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void updatePlan(InspectionPlanDTO dto) {
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        InspectionPlanDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        if (inspectionRecordService.countByPlanId(id) > 0) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("该计划存在关联的巡检记录，不允许删除");
        }
        planEquipmentMapper.delete(new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, id));
        planInspectorMapper.delete(new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, id));
        this.removeById(id);
    }

    @Override
    public InspectionPlanDomain getPlanById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        InspectionPlanDomain plan = this.getById(id);
        if (plan == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        plan.changeStatus(EnableStatus.of(status));
        this.updateById(plan);
    }

    private LocalDate calculateNextDate(InspectionPlanDomain plan, LocalDate today) {
        LocalDate date = plan.getStartDate();
        if (date == null) return null;

        String freqValue = plan.getFrequencyValue();
        int cycle = freqValue != null ? Integer.parseInt(freqValue) : 1;

        while (!date.isAfter(today)) {
            FrequencyType freqType = plan.getFrequencyType();
            if (freqType == FrequencyType.DAILY) {
                date = date.plusDays(cycle);
            } else if (freqType == FrequencyType.WEEKLY) {
                date = date.plusWeeks(cycle);
            } else if (freqType == FrequencyType.MONTHLY) {
                date = date.plusMonths(cycle);
            } else {
                return date;
            }
        }
        return date;
    }

    private void createInspectionRecords(InspectionPlanDomain plan) {
        List<InspectionPlanEquipmentDomain> equipments = planEquipmentMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, plan.getId()));
        List<InspectionPlanInspectorDomain> inspectors = planInspectorMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, plan.getId()));

        if (equipments.isEmpty() || inspectors.isEmpty()) return;

        for (int i = 0; i < equipments.size(); i++) {
            InspectionRecordDomain record = new InspectionRecordDomain();
            record.setPlanId(plan.getId());
            record.setEquipmentId(equipments.get(i).getEquipmentId());
            record.setInspectorUserId(inspectors.get(i % inspectors.size()).getInspectorId());
            record.setInspectionTime(LocalDateTime.now());
            record.startInspect();
            inspectionRecordService.addRecordDomain(record);
        }
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<InspectionPlanDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionPlanDomain::getPlanType, 1);
        wrapper.ne(InspectionPlanDomain::getStatus, EnableStatus.DISABLED);
        List<InspectionPlanDomain> plans = this.list(wrapper);

        for (InspectionPlanDomain plan : plans) {
            LocalDate nextDate = calculateNextDate(plan, today);
            if (nextDate != null && nextDate.isAfter(today)) {
                plan.setStartDate(nextDate);
                this.updateById(plan);
                continue;
            }
            createInspectionRecords(plan);
            plan.setStartDate(calculateNextDate(plan, today));
            this.updateById(plan);
        }
    }
}