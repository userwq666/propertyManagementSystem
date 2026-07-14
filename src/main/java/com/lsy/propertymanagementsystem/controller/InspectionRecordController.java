package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.service.InspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inspection/record")
public class InspectionRecordController {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @PostMapping
    public Result add(@Valid @RequestBody InspectionRecordRequest request) {
        inspectionRecordService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody InspectionRecordRequest request) {
        inspectionRecordService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionRecordService.delete(id);
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