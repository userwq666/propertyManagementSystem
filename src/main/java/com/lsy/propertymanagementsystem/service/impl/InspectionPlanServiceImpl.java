package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.service.InspectionPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InspectionPlanServiceImpl implements InspectionPlanService {

    @Autowired
    private InspectionPlanMapper inspectionPlanMapper;

    @Override
    @Transactional
    public void add(InspectionPlanRequest request) {
        InspectionPlan plan = new InspectionPlan();
        BeanUtils.copyProperties(request, plan);
        plan.setStatus(0);
        inspectionPlanMapper.insert(plan);
    }

    @Override
    @Transactional
    public void update(InspectionPlanRequest request) {
        InspectionPlan plan = inspectionPlanMapper.selectById(request.getId());
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        BeanUtils.copyProperties(request, plan);
        inspectionPlanMapper.updateById(plan);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inspectionPlanMapper.deleteById(id);
    }

    @Override
    public InspectionPlan getById(Long id) {
        return inspectionPlanMapper.selectById(id);
    }

    @Override
    public Page<InspectionPlan> page(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(InspectionPlan::getStatus, status);
        }
        wrapper.orderByDesc(InspectionPlan::getCreateTime);
        return inspectionPlanMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        InspectionPlan plan = inspectionPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        plan.setStatus(status);
        inspectionPlanMapper.updateById(plan);
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionPlan::getPlanType, 1);
        wrapper.eq(InspectionPlan::getStatus, 0);
        wrapper.le(InspectionPlan::getStartDate, LocalDate.now());

        List<InspectionPlan> plans = inspectionPlanMapper.selectList(wrapper);
        for (InspectionPlan plan : plans) {
            LocalDate nextDate = calculateNextDate(plan);
            if (nextDate != null && !nextDate.isAfter(LocalDate.now())) {
                plan.setStartDate(nextDate);
                inspectionPlanMapper.updateById(plan);
            }
        }
    }

    private LocalDate calculateNextDate(InspectionPlan plan) {
        if (plan.getCycleType() == null || plan.getStartDate() == null) {
            return null;
        }
        switch (plan.getCycleType()) {
            case 0:
                return plan.getStartDate().plusDays(1);
            case 1:
                return plan.getStartDate().plusWeeks(1);
            case 2:
                return plan.getStartDate().plusMonths(1);
            case 3:
                return plan.getStartDate().plusDays(plan.getCycleValue() != null ? plan.getCycleValue() : 1);
            default:
                return null;
        }
    }
}
