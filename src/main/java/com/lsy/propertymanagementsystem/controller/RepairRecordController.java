package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.RepairRecordRequest;
import com.lsy.propertymanagementsystem.entity.RepairRecord;
import com.lsy.propertymanagementsystem.service.RepairRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repair/record")
public class RepairRecordController {

    @Autowired
    private RepairRecordService repairRecordService;

    @PostMapping
    public Result add(@Valid @RequestBody RepairRecordRequest request) {
        repairRecordService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody RepairRecordRequest request) {
        repairRecordService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        repairRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        RepairRecord repairRecord = repairRecordService.getById(id);
        return Result.success(repairRecord);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer status) {
        Page<RepairRecord> page = repairRecordService.page(pageNum, pageSize, ownerId, houseId, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) String handleUser,
                               @RequestParam(required = false) String handleResult) {
        repairRecordService.updateStatus(id, status, handleUser, handleResult);
        return Result.success();
    }

    @PutMapping("/rating")
    public Result updateRating(@RequestParam Long id, @RequestParam Integer rating) {
        repairRecordService.updateRating(id, rating);
        return Result.success();
    }
}