package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.module.fee.entity.FeeRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeRecordService {
    void generateBills(List<FeeRecordRequest> requests);
    FeeRecord getById(Long id);
    Page<FeeRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer payStatus);
    void confirmPay(Long id, String payWay);
    Map<String, Object> getStatistics(Long ownerId, Long houseId);
    void markOverdue();
    long countByOwnerId(Long ownerId);
    long countByHouseId(Long houseId);
    long countByItemId(Long itemId);
}