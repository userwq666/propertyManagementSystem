package com.lsy.propertymanagementsystem.module.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.repair.entity.RepairRecord;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repair/record")
public class RepairRecordController {

    @Autowired
    private RepairRecordService repairRecordService;

    @PostMapping
    public Result add(@RequestBody RepairRecord record) {
        repairRecordService.addRepair(record);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody RepairRecord record) {
        repairRecordService.updateRepair(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        repairRecordService.deleteRepair(id);
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
                       @RequestParam(required = false) Integer status) {
        Page<RepairRecord> page = repairRecordService.page(pageNum, pageSize, ownerId, status);
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