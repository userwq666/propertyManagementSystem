package com.lsy.propertymanagementsystem.module.statistics.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PreAuthorize("hasAuthority('statistics:overview:list')")
    @GetMapping("/overview")
    public Result getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    @PreAuthorize("hasAuthority('statistics:repair:list')")
    @GetMapping("/repair/summary")
    public Result getRepairSummary() {
        return Result.success(statisticsService.getRepairSummary());
    }

    @PreAuthorize("hasAuthority('statistics:equipment:list')")
    @GetMapping("/equipment/summary")
    public Result getEquipmentSummary() {
        return Result.success(statisticsService.getEquipmentSummary());
    }

    @PreAuthorize("hasAuthority('statistics:user:list')")
    @GetMapping("/user/summary")
    public Result getUserSummary() {
        return Result.success(statisticsService.getUserSummary());
    }

    @PreAuthorize("hasAuthority('statistics:fee:list')")
    @GetMapping("/fee/summary")
    public Result getFeeSummary() {
        return Result.success(statisticsService.getFeeSummary());
    }

    @PreAuthorize("hasAuthority('statistics:complaint:list')")
    @GetMapping("/complaint/summary")
    public Result getComplaintSummary() {
        return Result.success(statisticsService.getComplaintSummary());
    }

    @PreAuthorize("hasAuthority('statistics:inspection:list')")
    @GetMapping("/inspection/summary")
    public Result getInspectionSummary() {
        return Result.success(statisticsService.getInspectionSummary());
    }

    @GetMapping("/todos")
    public Result getTodos() {
        return Result.success(statisticsService.getTodos());
    }
}
