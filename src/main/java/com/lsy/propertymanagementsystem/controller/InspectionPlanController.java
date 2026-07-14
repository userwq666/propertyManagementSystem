package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.service.InspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inspection/plan")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @PostMapping
    public Result add(@Valid @RequestBody InspectionPlanRequest request) {
        inspectionPlanService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody InspectionPlanRequest request) {
        inspectionPlanService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionPlanService.delete(id);
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
                       @RequestParam(required = false) Integer status) {
        Page<InspectionPlan> page = inspectionPlanService.page(pageNum, pageSize, status);
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