package com.lsy.propertymanagementsystem.module.statistics.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/overview")
    public Result getOverview() {
        Map<String, Object> overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fee/monthly")
    public Result getMonthlyFeeStatistics(@RequestParam(defaultValue = "2026") Integer year) {
        if (year < 1900 || year > 2100) {
            return Result.error("年份范围无效，应在1900-2100之间");
        }
        List<Map<String, Object>> statistics = statisticsService.getMonthlyFeeStatistics(year);
        return Result.success(statistics);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fee/byItem")
    public Result getFeeByItem() {
        List<Map<String, Object>> statistics = statisticsService.getFeeByItem();
        return Result.success(statistics);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/repair/overview")
    public Result getRepairOverview() {
        Map<String, Object> overview = statisticsService.getRepairOverview();
        return Result.success(overview);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/repair/byType")
    public Result getRepairByType() {
        List<Map<String, Object>> statistics = statisticsService.getRepairByType();
        return Result.success(statistics);
    }
}
