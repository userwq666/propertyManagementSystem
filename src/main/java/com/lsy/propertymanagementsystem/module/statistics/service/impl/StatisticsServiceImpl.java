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
        LocalDate startDate = resolveStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.PAID)
                .ge(FeeRecordDomain::getPayTime, startDate.atStartOfDay());
        List<FeeRecordDomain> records = feeRecordMapper.selectList(wrapper);

        Map<String, BigDecimal> groupMap = new LinkedHashMap<>();
        for (FeeRecordDomain r : records) {
            if (r.getPayTime() != null) {
                String key = formatDateKey(r.getPayTime().toLocalDate(), timeRange);
                groupMap.merge(key, r.getAmount(), BigDecimal::add);
            }
        }

        List<String> keys = generateAllKeys(startDate, endDate, timeRange);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : keys) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            item.put("amount", groupMap.getOrDefault(key, BigDecimal.ZERO));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairTrend(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(RepairRecordDomain::getCreateTime, startDate.atStartOfDay());
        List<RepairRecordDomain> records = repairRecordMapper.selectList(wrapper);

        Map<String, Long> groupMap = new LinkedHashMap<>();
        for (RepairRecordDomain r : records) {
            if (r.getCreateTime() != null) {
                String key = formatDateKey(r.getCreateTime().toLocalDate(), timeRange);
                groupMap.merge(key, 1L, Long::sum);
            }
        }

        List<String> keys = generateAllKeys(startDate, endDate, timeRange);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : keys) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            item.put("count", groupMap.getOrDefault(key, 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairTypeRatio(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);

        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(RepairRecordDomain::getCreateTime, startDate.atStartOfDay());
        List<RepairRecordDomain> records = repairRecordMapper.selectList(wrapper);

        Map<RepairType, Long> typeMap = records.stream()
                .collect(Collectors.groupingBy(RepairRecordDomain::getRepairType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<RepairType, Long> entry : typeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey() != null ? entry.getKey().getValue() : null);
            item.put("typeName", entry.getKey() != null ? entry.getKey().getDesc() : null);
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDeviceStatus(String timeRange) {
        List<EquipmentDomain> equipment = equipmentMapper.selectList(new LambdaQueryWrapper<>());

        Map<EquipmentStatus, Long> statusMap = equipment.stream()
                .collect(Collectors.groupingBy(EquipmentDomain::getStatus, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<EquipmentStatus, Long> entry : statusMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", entry.getKey() != null ? entry.getKey().getValue() : null);
            item.put("statusName", entry.getKey() != null ? entry.getKey().getDesc() : null);
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMaintenanceWarning(String timeRange) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(30);

        LambdaQueryWrapper<EquipmentMaintenanceDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(EquipmentMaintenanceDomain::getNextMaintenanceDate, today)
                .le(EquipmentMaintenanceDomain::getNextMaintenanceDate, deadline);
        List<EquipmentMaintenanceDomain> maintenances = equipmentMaintenanceMapper.selectList(wrapper);

        Set<Long> equipmentIds = maintenances.stream()
                .map(EquipmentMaintenanceDomain::getEquipmentId)
                .collect(Collectors.toSet());
        Map<Long, String> equipmentNameMap = new HashMap<>();
        if (!equipmentIds.isEmpty()) {
            List<EquipmentDomain> equipmentList = equipmentMapper.selectBatchIds(equipmentIds);
            equipmentNameMap = equipmentList.stream()
                    .collect(Collectors.toMap(EquipmentDomain::getId, EquipmentDomain::getEquipmentName));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (EquipmentMaintenanceDomain m : maintenances) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("equipmentId", m.getEquipmentId());
            item.put("equipmentName", equipmentNameMap.getOrDefault(m.getEquipmentId(), "未知设备"));
            item.put("nextMaintenanceDate", m.getNextMaintenanceDate());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getSatisfactionTrend(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(RepairRecordDomain::getEvaluateScore)
                .ge(RepairRecordDomain::getEvaluateTime, startDate.atStartOfDay());
        List<RepairRecordDomain> records = repairRecordMapper.selectList(wrapper);

        Map<String, List<Integer>> groupMap = new LinkedHashMap<>();
        for (RepairRecordDomain r : records) {
            if (r.getEvaluateTime() != null) {
                String key = formatDateKey(r.getEvaluateTime().toLocalDate(), timeRange);
                groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(r.getEvaluateScore());
            }
        }

        List<String> keys = generateAllKeys(startDate, endDate, timeRange);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : keys) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            List<Integer> scores = groupMap.getOrDefault(key, Collections.emptyList());
            double avgScore = scores.isEmpty() ? 0.0
                    : scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            item.put("avgScore", Math.round(avgScore * 10.0) / 10.0);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getComplaintTypeRatio(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);

        LambdaQueryWrapper<ComplaintSuggestDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ComplaintSuggestDomain::getCreateTime, startDate.atStartOfDay());
        List<ComplaintSuggestDomain> records = complaintSuggestMapper.selectList(wrapper);

        Map<ComplaintType, Long> typeMap = records.stream()
                .collect(Collectors.groupingBy(ComplaintSuggestDomain::getType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<ComplaintType, Long> entry : typeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey() != null ? entry.getKey().getValue() : null);
            item.put("typeName", entry.getKey() != null ? entry.getKey().getDesc() : null);
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getInspectionCompletion(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        LambdaQueryWrapper<InspectionRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InspectionRecordDomain::getInspectionTime, startDate.atStartOfDay())
                .le(InspectionRecordDomain::getInspectionTime, endDate.plusDays(1).atStartOfDay());
        List<InspectionRecordDomain> records = inspectionRecordMapper.selectList(wrapper);

        Map<String, long[]> groupMap = new LinkedHashMap<>();
        for (InspectionRecordDomain r : records) {
            if (r.getInspectionTime() != null) {
                String key = formatDateKey(r.getInspectionTime().toLocalDate(), timeRange);
                long[] counts = groupMap.computeIfAbsent(key, k -> new long[2]);
                counts[0]++;
                if (r.getStatus() == InspectResult.NORMAL || r.getStatus() == InspectResult.ABNORMAL) {
                    counts[1]++;
                }
            }
        }

        List<String> keys = generateAllKeys(startDate, endDate, timeRange);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : keys) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            long[] counts = groupMap.getOrDefault(key, new long[]{0, 0});
            item.put("total", counts[0]);
            item.put("completed", counts[1]);
            double rate = counts[0] > 0 ? Math.round(counts[1] * 1000.0 / counts[0]) / 10.0 : 0.0;
            item.put("rate", rate);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getInspectionAbnormal(String timeRange) {
        LocalDate startDate = resolveStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        LambdaQueryWrapper<InspectionRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InspectionRecordDomain::getInspectionTime, startDate.atStartOfDay())
                .le(InspectionRecordDomain::getInspectionTime, endDate.plusDays(1).atStartOfDay());
        List<InspectionRecordDomain> records = inspectionRecordMapper.selectList(wrapper);

        Map<String, long[]> groupMap = new LinkedHashMap<>();
        for (InspectionRecordDomain r : records) {
            if (r.getInspectionTime() != null) {
                String key = formatDateKey(r.getInspectionTime().toLocalDate(), timeRange);
                long[] counts = groupMap.computeIfAbsent(key, k -> new long[2]);
                counts[0]++;
                if (r.getStatus() == InspectResult.ABNORMAL) {
                    counts[1]++;
                }
            }
        }

        List<String> keys = generateAllKeys(startDate, endDate, timeRange);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : keys) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            long[] counts = groupMap.getOrDefault(key, new long[]{0, 0});
            item.put("total", counts[0]);
            item.put("abnormal", counts[1]);
            double rate = counts[0] > 0 ? Math.round(counts[1] * 1000.0 / counts[0]) / 10.0 : 0.0;
            item.put("rate", rate);
            result.add(item);
        }
        return result;
    }

    private LocalDate resolveStartDate(String timeRange) {
        LocalDate today = LocalDate.now();
        switch (timeRange) {
            case "7d":
                return today.minusDays(6);
            case "30d":
                return today.minusDays(29);
            case "90d":
                return today.minusDays(89);
            case "1y":
                return today.minusYears(1);
            default:
                return today.minusDays(29);
        }
    }

    private String formatDateKey(LocalDate date, String timeRange) {
        if ("90d".equals(timeRange)) {
            int dow = date.getDayOfWeek().getValue();
            return date.minusDays(dow - 1).toString();
        }
        if ("1y".equals(timeRange)) {
            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        }
        return date.toString();
    }

    private List<String> generateAllKeys(LocalDate startDate, LocalDate endDate, String timeRange) {
        List<String> keys = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String key = formatDateKey(current, timeRange);
            if (seen.add(key)) {
                keys.add(key);
            }
            current = current.plusDays(1);
        }
        return keys;
    }
}
