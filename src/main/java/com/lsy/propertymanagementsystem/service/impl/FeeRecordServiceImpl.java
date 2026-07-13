package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import com.lsy.propertymanagementsystem.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeRecordServiceImpl implements FeeRecordService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Override
    public void generateBills(List<FeeRecordRequest> requests) {
        for (FeeRecordRequest request : requests) {
            FeeRecord feeRecord = new FeeRecord();
            BeanUtils.copyProperties(request, feeRecord);
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
    public void confirmPay(Long id, String payWay) {
        FeeRecord feeRecord = feeRecordMapper.selectById(id);
        if (feeRecord == null) {
            throw new BusinessException("账单不存在");
        }
        if (feeRecord.getPayStatus() == 1) {
            throw new BusinessException("账单已缴费");
        }
        feeRecord.setPayStatus(1);
        feeRecord.setPayTime(LocalDateTime.now());
        feeRecord.setPayWay(payWay);
        feeRecordMapper.updateById(feeRecord);
    }

    @Override
    public Map<String, Object> getStatistics(Long ownerId, Long houseId) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 2);
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
}