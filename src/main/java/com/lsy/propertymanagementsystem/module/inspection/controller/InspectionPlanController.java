package com.lsy.propertymanagementsystem.module.inspection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspection/plan")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @PostMapping
    public Result add(@RequestBody InspectionPlan plan) {
        inspectionPlanService.addPlan(plan);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody InspectionPlan plan) {
        inspectionPlanService.updatePlan(plan);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionPlanService.deletePlan(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        InspectionPlan plan = inspectionPlanService.getById(id);
        return Result.success(plan);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String planName,
                       @RequestParam(required = false) Integer status) {
        Page<InspectionPlan> page = inspectionPlanService.page(pageNum, pageSize, planName, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        inspectionPlanService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/generate")
    public Result generate() {
        inspectionPlanService.generateByCycle();
        return Result.success();
    }
}