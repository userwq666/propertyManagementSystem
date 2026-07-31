package com.lsy.propertymanagementsystem.module.inspection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanDTO;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanVO;

import java.util.List;
import java.util.Map;

public interface InspectionPlanService {
    List<Map<String, Object>> listInspectors();
    Page<InspectionPlanVO> page(int pageNum, int pageSize, String planName, Integer status);
    void addPlan(InspectionPlanDTO plan);
    void updatePlan(InspectionPlanDTO plan);
    void deletePlan(Long id);
    InspectionPlanVO getPlanById(Long id);
    void updateStatus(Long id, Integer status);
    void generateByCycle();
}
