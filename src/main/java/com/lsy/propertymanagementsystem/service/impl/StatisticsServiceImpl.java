package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import com.lsy.propertymanagementsystem.entity.RepairRecord;
import com.lsy.propertymanagementsystem.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private FeeItemMapper feeItemMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 查询总收费金额
        LambdaQueryWrapper<FeeRecord> allFeeWrapper = new LambdaQueryWrapper<>();
        allFeeWrapper.eq(FeeRecord::getPayStatus, 1);
        List<FeeRecord> paidRecords = feeRecordMapper.selectList(allFeeWrapper);
        BigDecimal totalPaid = paidRecords.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询总欠费金额
        LambdaQueryWrapper<FeeRecord> arrearsWrapper = new LambdaQueryWrapper<>();
        arrearsWrapper.eq(FeeRecord::getPayStatus, 2);
        List<FeeRecord> arrearsRecords = feeRecordMapper.selectList(arrearsWrapper);
        BigDecimal totalArrears = arrearsRecords.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询总报修数
        long totalRepair = repairRecordMapper.selectCount(null);

        // 查询待处理报修数
        LambdaQueryWrapper<RepairRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(RepairRecord::getStatus, 0);
        long pendingRepair = repairRecordMapper.selectCount(pendingWrapper);

        result.put("totalPaid", totalPaid);
        result.put("totalArrears", totalArrears);
        result.put("totalRepair", totalRepair);
        result.put("pendingRepair", pendingRepair);

        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyFeeStatistics(Integer year) {
        if (year == null) {
            year = 2026;
        }
        if (year < 1900 || year > 2100) {
            year = 2026;
        }

        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(FeeRecord::getBillCycle, String.valueOf(year));
        List<FeeRecord> records = feeRecordMapper.selectList(wrapper);

        // 按月份分组统计
        Map<String, BigDecimal> monthlyStats = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getBillCycle().substring(5, 7),
                        Collectors.reducing(BigDecimal.ZERO, FeeRecord::getTotalMoney, BigDecimal::add)
                ));

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthData = new HashMap<>();
            String monthStr = String.format("%02d", month);
            monthData.put("month", monthStr);
            monthData.put("amount", monthlyStats.getOrDefault(monthStr, BigDecimal.ZERO));
            result.add(monthData);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getFeeByItem() {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 1);
        List<FeeRecord> records = feeRecordMapper.selectList(wrapper);

        // 获取所有收费项目
        List<FeeItem> items = feeItemMapper.selectList(null);
        Map<Long, String> itemMap = items.stream()
                .collect(Collectors.toMap(FeeItem::getId, FeeItem::getItemName));

        // 按项目分组统计
        Map<Long, BigDecimal> itemStats = records.stream()
                .collect(Collectors.groupingBy(
                        FeeRecord::getItemId,
                        Collectors.reducing(BigDecimal.ZERO, FeeRecord::getTotalMoney, BigDecimal::add)
                ));

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        itemStats.forEach((itemId, amount) -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("itemId", itemId);
            itemData.put("itemName", itemMap.getOrDefault(itemId, "未知项目"));
            itemData.put("amount", amount);
            result.add(itemData);
        });

        return result;
    }

    @Override
    public Map<String, Object> getRepairOverview() {
        Map<String, Object> result = new HashMap<>();

        // 待处理
        LambdaQueryWrapper<RepairRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(RepairRecord::getStatus, 0);
        long pending = repairRecordMapper.selectCount(pendingWrapper);

        // 处理中
        LambdaQueryWrapper<RepairRecord> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.eq(RepairRecord::getStatus, 1);
        long processing = repairRecordMapper.selectCount(processingWrapper);

        // 已完成
        LambdaQueryWrapper<RepairRecord> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(RepairRecord::getStatus, 2);
        long completed = repairRecordMapper.selectCount(completedWrapper);

        // 已驳回
        LambdaQueryWrapper<RepairRecord> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(RepairRecord::getStatus, 3);
        long rejected = repairRecordMapper.selectCount(rejectedWrapper);

        result.put("pending", pending);
        result.put("processing", processing);
        result.put("completed", completed);
        result.put("rejected", rejected);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRepairByType() {
        List<RepairRecord> records = repairRecordMapper.selectList(null);

        // 按报修类型分组统计
        Map<String, Long> typeStats = records.stream()
                .collect(Collectors.groupingBy(
                        RepairRecord::getRepairType,
                        Collectors.counting()
                ));

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        typeStats.forEach((type, count) -> {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", type);
            typeData.put("count", count);
            result.add(typeData);
        });

        return result;
    }
}