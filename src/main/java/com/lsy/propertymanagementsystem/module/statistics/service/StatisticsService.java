package com.lsy.propertymanagementsystem.module.statistics.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOverview();
    Map<String, Object> getRepairSummary();
    Map<String, Object> getEquipmentSummary();
    Map<String, Object> getUserSummary();
    Map<String, Object> getFeeSummary();
    Map<String, Object> getComplaintSummary();
    Map<String, Object> getInspectionSummary();
}