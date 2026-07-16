package com.lsy.propertymanagementsystem.module.fee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeRecordDTO;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee/record")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @PreAuthorize("hasAuthority('fee:record:add')")
    @PostMapping("/generate")
    public Result generateBills(@Valid @RequestBody List<FeeRecordDTO> domains) {
        feeRecordService.generateBills(domains);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:record:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeRecordDomain domain = feeRecordService.getById(id);
        return Result.success(domain);
    }

    @PreAuthorize("hasAuthority('fee:record:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer payStatus) {
        Page<FeeRecordDomain> page = feeRecordService.page(pageNum, pageSize, ownerId, houseId, payStatus);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('fee:record:edit')")
    @PutMapping("/pay")
    public Result confirmPay(@RequestParam Long id, @RequestParam String payWay) {
        feeRecordService.confirmPay(id, payWay);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:record:list')")
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam(required = false) Long ownerId,
                                @RequestParam(required = false) Long houseId) {
        Map<String, Object> statistics = feeRecordService.getStatistics(ownerId, houseId);
        return Result.success(statistics);
    }

    @PreAuthorize("hasAuthority('fee:record:edit')")
    @PutMapping("/markOverdue")
    public Result markOverdue() {
        feeRecordService.markOverdue();
        return Result.success();
    }
}
