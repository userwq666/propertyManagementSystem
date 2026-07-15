package com.lsy.propertymanagementsystem.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
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

        LambdaQueryWrapper<FeeRecordDomain> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(FeeRecordDomain::getStatus, 2);
        List<FeeRecordDomain> paidRecords = feeRecordMapper.selectList(paidWrapper);
        BigDecimal totalPaid = paidRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecordDomain> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(FeeRecordDomain::getStatus, 3);
        List<FeeRecordDomain> overdueRecords = feeRecordMapper.selectList(overdueWrapper);
        BigDecimal totalOverdue = overdueRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<FeeRecordDomain> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(FeeRecordDomain::getStatus, 0);
        List<FeeRecordDomain> unpaidRecords = feeRecordMapper.selectList(unpaidWrapper);
        BigDecimal totalUnpaid = unpaidRecords.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long repairPending = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, 0));
        long repairProcessing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, 1));

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
        wrapper.eq(FeeRecordDomain::getStatus, 2);
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
        wrapper.eq(FeeRecordDomain::getStatus, 2);
        List<FeeRecordDomain> records = feeRecordMapper.selectList(wrapper);

        Map<Long, BigDecimal> itemMap = records.stream()
                .collect(Collectors.groupingBy(
                        FeeRecordDomain::getItemId,
                        Collectors.reducing(BigDecimal.ZERO, FeeRecordDomain::getAmount, BigDecimal::add)
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
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, 0));
        long processing = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, 1));
        long completed = repairRecordMapper.selectCount(
                new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getStatus, 3));
        result.put("total", total);
        result.put("pending", pending);
        result.put("processing", processing);
        result.put("completed", completed);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairByType() {
        List<RepairRecordDomain> records = repairRecordMapper.selectList(new LambdaQueryWrapper<>());

        Map<String, Long> typeMap = records.stream()
                .collect(Collectors.groupingBy(RepairRecordDomain::getRepairType, Collectors.counting()));

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