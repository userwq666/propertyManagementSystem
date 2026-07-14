package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionPlan;

public interface InspectionPlanService {
    void addPlan(InspectionPlan plan);
    void updatePlan(InspectionPlan plan);
    void deletePlan(Long id);
    InspectionPlan getById(Long id);
    Page<InspectionPlan> page(int pageNum, int pageSize, String planName, Integer status);
    void updateStatus(Long id, Integer status);
    void generateByCycle();
}