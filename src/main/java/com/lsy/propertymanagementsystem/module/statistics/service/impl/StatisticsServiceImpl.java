package com.lsy.propertymanagementsystem.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.entity.*;
import com.lsy.propertymanagementsystem.mapper.*;
import com.lsy.propertymanagementsystem.module.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private CommunityOwnerMapper ownerMapper;

    @Autowired
    private CommunityHouseMapper houseMapper;

    @Autowired
    private CommunityParkingMapper parkingMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        long ownerCount = ownerMapper.selectCount(new LambdaQueryWrapper<>());
        long houseCount = houseMapper.selectCount(new LambdaQueryWrapper<>());
        long parkingCount = parkingMapper.selectCount(new LambdaQueryWrapper<>());

        LambdaQueryWrapper<FeeRecord> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(FeeRecord::getPayStatus, 1);
        List<FeeRecord> paidRecords = feeRecordMapper.selectList(paidWrapper);
        BigDecimal totalPaid = paidRecords.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(FeeRecord::getPayStatus, 2);
        List<FeeRecord> overdueRecords = feeRecordMapper.selectList(overdueWrapper);
        BigDecimal totalOverdue = overdueRecords.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecord> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(FeeRecord::getPayStatus, 0);
        List<FeeRecord> unpaidRecords = feeRecordMapper.selectList(unpaidWrapper);
        BigDecimal totalUnpaid = unpaidRecords.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long repairPending = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getStatus, 0));
        long repairProcessing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getStatus, 1));

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
        String yearPrefix = year + "-";

        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 1);
        wrapper.likeRight(FeeRecord::getBillCycle, yearPrefix);
        List<FeeRecord> records = feeRecordMapper.selectList(wrapper);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<Integer, BigDecimal> monthlyData = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyData.put(i, BigDecimal.ZERO);
        }

        for (FeeRecord record : records) {
            try {
                String cycle = record.getBillCycle();
                if (cycle != null && cycle.length() >= 7) {
                    LocalDate date = LocalDate.parse(cycle + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (date.getYear() == year) {
                        int month = date.getMonthValue();
                        monthlyData.merge(month, record.getTotalMoney(), BigDecimal::add);
                    }
                }
            } catch (Exception ignored) {
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
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 1);
        List<FeeRecord> records = feeRecordMapper.selectList(wrapper);

        Map<Long, BigDecimal> itemMap = records.stream()
                .collect(Collectors.groupingBy(
                        FeeRecord::getItemId,
                        Collectors.reducing(BigDecimal.ZERO, FeeRecord::getTotalMoney, BigDecimal::add)
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : itemMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("itemId", entry.getKey());
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
                new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getStatus, 0));
        long processing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getStatus, 1));
        long completed = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecord>().eq(RepairRecord::getStatus, 2));
        result.put("total", total);
        result.put("pending", pending);
        result.put("processing", processing);
        result.put("completed", completed);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairByType() {
        List<RepairRecord> records = repairRecordMapper.selectList(new LambdaQueryWrapper<>());

        Map<String, Long> typeMap = records.stream()
                .collect(Collectors.groupingBy(RepairRecord::getRepairType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }

        return result;
    }
}