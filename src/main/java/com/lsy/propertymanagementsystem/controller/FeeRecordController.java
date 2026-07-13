package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import com.lsy.propertymanagementsystem.service.FeeRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee/record")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @PostMapping("/generate")
    public Result generateBills(@Valid @RequestBody List<FeeRecordRequest> requests) {
        feeRecordService.generateBills(requests);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeRecord feeRecord = feeRecordService.getById(id);
        return Result.success(feeRecord);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer payStatus) {
        Page<FeeRecord> page = feeRecordService.page(pageNum, pageSize, ownerId, houseId, payStatus);
        return Result.success(page);
    }

    @PutMapping("/pay")
    public Result confirmPay(@RequestParam Long id, @RequestParam String payWay) {
        feeRecordService.confirmPay(id, payWay);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam(required = false) Long ownerId,
                                @RequestParam(required = false) Long houseId) {
        Map<String, Object> statistics = feeRecordService.getStatistics(ownerId, houseId);
        return Result.success(statistics);
    }
}