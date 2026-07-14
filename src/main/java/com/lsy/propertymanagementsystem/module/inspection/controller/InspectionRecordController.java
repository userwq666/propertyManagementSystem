package com.lsy.propertymanagementsystem.module.inspection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.inspection.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspection/record")
public class InspectionRecordController {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @PostMapping
    public Result add(@RequestBody InspectionRecord record) {
        inspectionRecordService.addRecord(record);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody InspectionRecord record) {
        inspectionRecordService.updateRecord(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionRecordService.deleteRecord(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        InspectionRecord record = inspectionRecordService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long equipmentId,
                       @RequestParam(required = false) Integer result) {
        Page<InspectionRecord> page = inspectionRecordService.page(pageNum, pageSize, equipmentId, result);
        return Result.success(page);
    }

    @GetMapping("/equipment/{id}")
    public Result getByEquipmentId(@PathVariable Long id) {
        List<InspectionRecord> list = inspectionRecordService.getByEquipmentId(id);
        return Result.success(list);
    }
}