package com.lsy.propertymanagementsystem.module.inspection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanDTO;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanVO;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inspection/plan")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @PreAuthorize("hasAuthority('inspection:plan:add')")
    @PostMapping
    public Result add(@Valid @RequestBody InspectionPlanDTO plan) {
        inspectionPlanService.addPlan(plan);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:plan:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody InspectionPlanDTO plan) {
        inspectionPlanService.updatePlan(plan);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:plan:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionPlanService.deletePlan(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:plan:list')")
    @GetMapping("/{id}")
    public Result getPlanById(@PathVariable Long id) {
        InspectionPlanVO plan = inspectionPlanService.getPlanById(id);
        return Result.success(plan);
    }

    @PreAuthorize("hasAuthority('inspection:plan:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String planName,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = false) Long equipmentId) {
        Page<InspectionPlanVO> page = inspectionPlanService.page(pageNum, pageSize, planName, status, equipmentId);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('inspection:plan:edit')")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        inspectionPlanService.updateStatus(id, status);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:plan:edit')")
    @PostMapping("/generate")
    public Result generate() {
        inspectionPlanService.generateByCycle();
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:plan:list') or hasAuthority('inspection:record:list')")
    @GetMapping("/inspectors")
    public Result<List<Map<String, Object>>> inspectors() {
        return Result.success(inspectionPlanService.listInspectors());
    }
}
