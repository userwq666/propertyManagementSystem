package com.lsy.propertymanagementsystem.task;

import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.module.announcement.service.AnnouncementService;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private FeeRecordService feeRecordService;

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @Autowired
    private RepairRecordService repairRecordService;

    @Autowired
    private AnnouncementService announcementService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void markOverdueFees() {
        feeRecordService.markOverdue();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void generateInspectionByCycle() {
        inspectionPlanService.generateByCycle();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanTokenBlacklist() {
        JwtUtils.cleanExpiredBlacklist();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void autoCompleteExpiredRepairs() {
        repairRecordService.autoCompleteExpired();
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void publishScheduledAnnouncements() {
        announcementService.publishScheduled();
    }
}
