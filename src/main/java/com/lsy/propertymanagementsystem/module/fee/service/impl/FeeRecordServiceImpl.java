package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.module.fee.entity.FeeItem;
import com.lsy.propertymanagementsystem.module.fee.entity.FeeRecord;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeRecordServiceImpl implements FeeRecordService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private FeeItemMapper feeItemMapper;

    @Override
    @Transactional
    public void generateBills(List<FeeRecordRequest> requests) {
        for (FeeRecordRequest request : requests) {
            FeeItem feeItem = feeItemMapper.selectById(request.getItemId());
            if (feeItem == null) {
                throw new BusinessException("收费项目不存在，itemId=" + request.getItemId());
            }

            FeeRecord feeRecord = new FeeRecord();
            feeRecord.setOwnerId(request.getOwnerId());
            feeRecord.setHouseId(request.getHouseId());
            feeRecord.setItemId(request.getItemId());
            feeRecord.setTotalMoney(request.getTotalMoney());
            feeRecord.setBillCycle(request.getBillCycle());
            feeRecord.setPayStatus(0);
            feeRecordMapper.insert(feeRecord);
        }
    }

    @Override
    public FeeRecord getById(Long id) {
        return feeRecordMapper.selectById(id);
    }

    @Override
    public Page<FeeRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer payStatus) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(FeeRecord::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecord::getHouseId, houseId);
        }
        if (payStatus != null) {
            wrapper.eq(FeeRecord::getPayStatus, payStatus);
        }
        wrapper.orderByDesc(FeeRecord::getCreateTime);
        return feeRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void confirmPay(Long id, String payWay) {
        FeeRecord feeRecord = feeRecordMapper.selectById(id);
        if (feeRecord == null) {
            throw new BusinessException("账单不存在");
        }
        if (feeRecord.getPayStatus() == 1) {
            throw new BusinessException("账单已缴费，请勿重复缴费");
        }
        feeRecord.setPayStatus(1);
        feeRecord.setPayTime(LocalDateTime.now());
        feeRecord.setPayWay(payWay);
        feeRecordMapper.updateById(feeRecord);
    }

    @Override
    public Map<String, Object> getStatistics(Long ownerId, Long houseId) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FeeRecord::getPayStatus, 0, 2);
        if (ownerId != null) {
            wrapper.eq(FeeRecord::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecord::getHouseId, houseId);
        }
        List<FeeRecord> arrearsList = feeRecordMapper.selectList(wrapper);

        BigDecimal totalArrears = arrearsList.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("arrearsList", arrearsList);
        result.put("totalArrears", totalArrears);
        result.put("count", arrearsList.size());
        return result;
    }

    @Override
    public long countByOwnerId(Long ownerId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecord>().eq(FeeRecord::getOwnerId, ownerId));
    }

    @Override
    public long countByHouseId(Long houseId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecord>().eq(FeeRecord::getHouseId, houseId));
    }

    @Override
    public long countByItemId(Long itemId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecord>().eq(FeeRecord::getItemId, itemId));
    }

    @Override
    @Transactional
    public void markOverdue() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String currentMonth = today.format(formatter);

        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 0);
        wrapper.lt(FeeRecord::getBillCycle, currentMonth);
        List<FeeRecord> overdueRecords = feeRecordMapper.selectList(wrapper);

        for (FeeRecord record : overdueRecords) {
            record.setPayStatus(2);
            feeRecordMapper.updateById(record);
        }
    }
}