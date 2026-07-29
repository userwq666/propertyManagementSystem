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

    @PreAuthorize("hasAuthority('statistics:overview:list')")
    @GetMapping("/overview")
    public Result getOverview() {
        Map<String, Object> overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    @PreAuthorize("hasAuthority('statistics:fee:list')")
    @GetMapping("/fee/monthly")
    public Result getMonthlyFeeStatistics(@RequestParam(defaultValue = "2026") Integer year) {
        if (year < 1900 || year > 2100) {
            return Result.error("年份范围无效，应在1900-2100之间");
        }
        List<Map<String, Object>> statistics = statisticsService.getMonthlyFeeStatistics(year);
        return Result.success(statistics);
    }

    @PreAuthorize("hasAuthority('statistics:fee:list')")
    @GetMapping("/fee/byItem")
    public Result getFeeByItem() {
        List<Map<String, Object>> statistics = statisticsService.getFeeByItem();
        return Result.success(statistics);
    }

    @PreAuthorize("hasAuthority('statistics:repair:list')")
    @GetMapping("/repair/overview")
    public Result getRepairOverview() {
        Map<String, Object> overview = statisticsService.getRepairOverview();
        return Result.success(overview);
    }

    @PreAuthorize("hasAuthority('statistics:repair:list')")
    @GetMapping("/repair/byType")
    public Result getRepairByType() {
        List<Map<String, Object>> statistics = statisticsService.getRepairByType();
        return Result.success(statistics);
    }

    @PreAuthorize("hasAuthority('statistics:fee:list')")
    @GetMapping("/fee/trend")
    public Result getFeeTrend(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getFeeTrend(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:repair:list')")
    @GetMapping("/repair/trend")
    public Result getRepairTrend(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getRepairTrend(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:repair:list')")
    @GetMapping("/repair/typeRatio")
    public Result getRepairTypeRatio(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getRepairTypeRatio(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:equipment:list')")
    @GetMapping("/equipment/status")
    public Result getDeviceStatus(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getDeviceStatus(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:equipment:list')")
    @GetMapping("/equipment/maintenanceWarning")
    public Result getMaintenanceWarning(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getMaintenanceWarning(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:complaint:list')")
    @GetMapping("/complaint/satisfactionTrend")
    public Result getSatisfactionTrend(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getSatisfactionTrend(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:complaint:list')")
    @GetMapping("/complaint/typeRatio")
    public Result getComplaintTypeRatio(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getComplaintTypeRatio(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:inspection:list')")
    @GetMapping("/inspection/completion")
    public Result getInspectionCompletion(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getInspectionCompletion(timeRange);
        return Result.success(data);
    }

    @PreAuthorize("hasAuthority('statistics:inspection:list')")
    @GetMapping("/inspection/abnormal")
    public Result getInspectionAbnormal(@RequestParam(defaultValue = "6m") String timeRange) {
        List<Map<String, Object>> data = statisticsService.getInspectionAbnormal(timeRange);
        return Result.success(data);
    }
}
