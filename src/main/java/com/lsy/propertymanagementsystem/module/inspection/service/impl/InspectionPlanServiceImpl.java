package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InspectionPlanServiceImpl extends ServiceImpl<InspectionPlanMapper, InspectionPlan> implements InspectionPlanService {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @Override
    public Page<InspectionPlan> page(int pageNum, int pageSize, String planName, Integer status) {
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(InspectionPlan::getPlanName, planName);
        }
        if (status != null) {
            wrapper.eq(InspectionPlan::getStatus, status);
        }
        wrapper.orderByDesc(InspectionPlan::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addPlan(InspectionPlan plan) {
        if (plan.getPlanType() == 0) {
            plan.setStatus(0);
        } else {
            plan.setStatus(0);
        }
        this.save(plan);
    }

    @Override
    @Transactional
    public void updatePlan(InspectionPlan plan) {
        InspectionPlan existing = this.getById(plan.getId());
        if (existing == null) {
            throw new BusinessException("巡检计划不存在");
        }
        this.updateById(plan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        if (inspectionRecordService.countByPlanId(id) > 0) {
            throw new BusinessException("该计划存在关联的巡检记录，不允许删除");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        InspectionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("巡检计划不存在");
        }
        plan.setStatus(status);
        this.updateById(plan);
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionPlan::getPlanType, 1);
        wrapper.ne(InspectionPlan::getStatus, 3);
        List<InspectionPlan> plans = this.list(wrapper);

        for (InspectionPlan plan : plans) {
            LocalDate nextDate = calculateNextDate(plan, today);
            if (nextDate != null && !nextDate.isAfter(today)) {
                createInspectionRecords(plan);
                updatePlanNextDate(plan, today);
            }
        }
    }

    private LocalDate calculateNextDate(InspectionPlan plan, LocalDate today) {
        if (plan.getStartDate() == null) {
            return null;
        }

        LocalDate nextDate = plan.getStartDate();
        int cycleValue = plan.getCycleValue() != null ? plan.getCycleValue() : 1;

        while (nextDate.isBefore(today) || nextDate.isEqual(today)) {
            switch (plan.getCycleType()) {
                case 0:
                    nextDate = nextDate.plusDays(cycleValue);
                    break;
                case 1:
                    nextDate = nextDate.plusWeeks(cycleValue);
                    break;
                case 2:
                    nextDate = nextDate.plusMonths(cycleValue);
                    break;
                default:
                    nextDate = nextDate.plusDays(cycleValue);
                    break;
            }
        }

        return nextDate;
    }

    private void createInspectionRecords(InspectionPlan plan) {
        if (plan.getEquipmentIds() == null || plan.getEquipmentIds().isEmpty()) {
            return;
        }
        if (plan.getInspectorIds() == null || plan.getInspectorIds().isEmpty()) {
            return;
        }

        List<Long> equipmentIdList = Arrays.stream(plan.getEquipmentIds().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Long> inspectorIdList = Arrays.stream(plan.getInspectorIds().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (inspectorIdList.isEmpty()) {
            return;
        }

        for (Long equipmentId : equipmentIdList) {
            Long inspectorId = inspectorIdList.get(0);

            InspectionRecord record = new InspectionRecord();
            record.setPlanId(plan.getId());
            record.setEquipmentId(equipmentId);
            record.setInspectorId(inspectorId);
            record.setResult(0);
            inspectionRecordService.addRecord(record);
        }

        plan.setStatus(1);
        this.updateById(plan);
    }

    private void updatePlanNextDate(InspectionPlan plan, LocalDate today) {
        int cycleValue = plan.getCycleValue() != null ? plan.getCycleValue() : 1;
        LocalDate newStartDate;

        switch (plan.getCycleType()) {
            case 0:
                newStartDate = today.plusDays(cycleValue);
                break;
            case 1:
                newStartDate = today.plusWeeks(cycleValue);
                break;
            case 2:
                newStartDate = today.plusMonths(cycleValue);
                break;
            default:
                newStartDate = today.plusDays(cycleValue);
                break;
        }

        plan.setStartDate(newStartDate);
        this.updateById(plan);
    }
}