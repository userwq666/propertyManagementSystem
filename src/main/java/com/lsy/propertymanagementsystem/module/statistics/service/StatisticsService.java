package com.lsy.propertymanagementsystem.module.statistics.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getMonthlyFeeStatistics(Integer year);
    List<Map<String, Object>> getFeeByItem();
    Map<String, Object> getRepairOverview();
    List<Map<String, Object>> getRepairByType();
    // 收费趋势（按时间范围）
    List<Map<String, Object>> getFeeTrend(String timeRange);

    // 报修趋势（按时间范围）
    List<Map<String, Object>> getRepairTrend(String timeRange);

    // 报修类型占比（按时间范围）
    List<Map<String, Object>> getRepairTypeRatio(String timeRange);

    // 设备状态分布
    List<Map<String, Object>> getDeviceStatus(String timeRange);

    // 维保到期预警
    List<Map<String, Object>> getMaintenanceWarning(String timeRange);

    // 满意度评分趋势
    List<Map<String, Object>> getSatisfactionTrend(String timeRange);

    // 投诉类型分布
    List<Map<String, Object>> getComplaintTypeRatio(String timeRange);

    // 巡检完成率
    List<Map<String, Object>> getInspectionCompletion(String timeRange);

    // 巡检异常率
    List<Map<String, Object>> getInspectionAbnormal(String timeRange);
}