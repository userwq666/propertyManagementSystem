package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanDTO;

public interface InspectionPlanService {
    Page<InspectionPlanDomain> page(int pageNum, int pageSize, String planName, Integer status);
    void addPlan(InspectionPlanDTO plan);
    void updatePlan(InspectionPlanDTO plan);
    void deletePlan(Long id);
    InspectionPlanDomain getPlanById(Long id);
    void updateStatus(Long id, Integer status);
    void generateByCycle();
}