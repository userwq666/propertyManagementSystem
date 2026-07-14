package com.lsy.propertymanagementsystem.module.statistics.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getMonthlyFeeStatistics(Integer year);
    List<Map<String, Object>> getFeeByItem();
    Map<String, Object> getRepairOverview();
    List<Map<String, Object>> getRepairByType();
}