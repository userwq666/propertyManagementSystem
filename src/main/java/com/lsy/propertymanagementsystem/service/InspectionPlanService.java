package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;

public interface InspectionPlanService {
    void add(InspectionPlanRequest request);
    void update(InspectionPlanRequest request);
    void delete(Long id);
    InspectionPlan getById(Long id);
    Page<InspectionPlan> page(int pageNum, int pageSize, Integer status);
    void updateStatus(Long id, Integer status);
    void generateByCycle();
}
