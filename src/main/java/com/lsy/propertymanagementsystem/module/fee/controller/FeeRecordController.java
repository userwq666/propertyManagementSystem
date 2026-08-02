package com.lsy.propertymanagementsystem.module.fee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeGenerateDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeRecordVO;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/fee/record")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @PreAuthorize("hasAuthority('fee:record:add')")
    @PostMapping("/generate")
    public Result generateBills(@Valid @RequestBody FeeGenerateDTO dto) {
        feeRecordService.generateBills(dto.getItemId(), dto.getHouseIds());
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:record:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeRecordVO vo = feeRecordService.getById(id);
        return Result.success(vo);
    }

    @PreAuthorize("hasAuthority('fee:record:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer payStatus) {
        Page<FeeRecordVO> page = feeRecordService.page(pageNum, pageSize, ownerId, houseId, payStatus);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('fee:record:edit')")
    @PutMapping("/pay")
    public Result confirmPay(@RequestParam Long id, @RequestParam String payWay,
                             @RequestParam(required = false) BigDecimal paidAmount,
                             @RequestParam(required = false) String remark) {
        feeRecordService.confirmPay(id, payWay, paidAmount, remark);
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
