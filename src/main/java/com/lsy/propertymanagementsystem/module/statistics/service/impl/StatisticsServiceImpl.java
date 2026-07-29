package com.lsy.propertymanagementsystem.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintType;
import com.lsy.propertymanagementsystem.module.complaint.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMaintenanceMapper;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairType;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.module.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private FeeItemMapper feeItemMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private CommunityOwnerMapper ownerMapper;

    @Autowired
    private CommunityHouseMapper houseMapper;

    @Autowired
    private CommunityParkingMapper parkingMapper;

    @Autowired
    private ComplaintSuggestMapper complaintSuggestMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentMaintenanceMapper equipmentMaintenanceMapper;

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        long ownerCount = ownerMapper.selectCount(new LambdaQueryWrapper<>());
        long houseCount = houseMapper.selectCount(new LambdaQueryWrapper<>());
        long parkingCount = parkingMapper.selectCount(new LambdaQueryWrapper<>());

        LambdaQueryWrapper<FeeRecordDomain> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.PAID);
        List<FeeRecordDomain> paidRecords = feeRecordMapper.selectList(paidWrapper);
        BigDecimal totalPaid = paidRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecordDomain> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.OVERDUE);
        List<FeeRecordDomain> overdueRecords = feeRecordMapper.selectList(overdueWrapper);
        BigDecimal totalOverdue = overdueRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecordDomain> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.UNPAID);
        List<FeeRecordDomain> unpaidRecords = feeRecordMapper.selectList(unpaidWrapper);
        BigDecimal totalUnpaid = unpaidRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long repairPending = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, RepairStatus.PENDING));
        long repairProcessing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, RepairStatus.PROCESSING));

        result.put("ownerCount", ownerCount);
        result.put("houseCount", houseCount);
        result.put("parkingCount", parkingCount);
        result.put("totalPaid", totalPaid);
        result.put("totalOverdue", totalOverdue);
        result.put("totalUnpaid", totalUnpaid);
        result.put("repairPending", repairPending);
        result.put("repairProcessing", repairProcessing);

        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyFeeStatistics(Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.PAID);
        List<FeeRecordDomain> records = feeRecordMapper.selectList(wrapper);

        Map<Integer, BigDecimal> monthlyData = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyData.put(i, BigDecimal.ZERO);
        }

        for (FeeRecordDomain record : records) {
            if (record.getPayTime() != null && record.getPayTime().getYear() == year) {
                int month = record.getPayTime().getMonthValue();
                monthlyData.merge(month, record.getAmount(), BigDecimal::add);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : monthlyData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("amount", entry.getValue());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getFeeByItem() {
        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.PAID);
        List<FeeRecordDomain> records = feeRecordMapper.selectList(wrapper);

        Map<Long, BigDecimal> itemMap = records.stream()
                .collect(Collectors.groupingBy(
                        FeeRecordDomain::getItemId,
                        Collectors.reducing(BigDecimal.ZERO, FeeRecordDomain::getAmount, BigDecimal::add)
                ));

        List<Long> itemIds = new ArrayList<>(itemMap.keySet());
        Map<Long, String> itemNameMap = new HashMap<>();
        if (!itemIds.isEmpty()) {
            List<FeeItemDomain> items = feeItemMapper.selectBatchIds(itemIds);
            itemNameMap = items.stream().collect(Collectors.toMap(FeeItemDomain::getId, FeeItemDomain::getItemName));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : itemMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("itemId", entry.getKey());
            item.put("itemName", itemNameMap.getOrDefault(entry.getKey(), "未知项目"));
            item.put("amount", entry.getValue());
            result.add(item);
        }

        return result;
    }

    @Override
    public Map<String, Object> getRepairOverview() {
        Map<String, Object> result = new HashMap<>();
        long total = repairRecordMapper.selectCount(new LambdaQueryWrapper<>());
        long pending = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, RepairStatus.PENDING));
        long processing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, RepairStatus.PROCESSING));
        long completed = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, RepairStatus.COMPLETED));
        result.put("total", total);
        result.put("pending", pending);
        result.put("processing", processing);
        result.put("completed", completed);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairByType() {
        List<RepairRecordDomain> records = repairRecordMapper.selectList(new LambdaQueryWrapper<>());

        Map<RepairType, Long> typeMap = records.stream()
                .collect(Collectors.groupingBy(RepairRecordDomain::getRepairType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<RepairType, Long> entry : typeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey() != null ? entry.getKey().getValue() : null);
            item.put("count", entry.getValue());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getFeeTrend(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getRepairTrend(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getRepairTypeRatio(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getDeviceStatus(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getMaintenanceWarning(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getSatisfactionTrend(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getComplaintTypeRatio(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getInspectionCompletion(String timeRange) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getInspectionAbnormal(String timeRange) {
        return Collections.emptyList();
    }
}
