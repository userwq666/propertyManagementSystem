package com.lsy.propertymanagementsystem.module.inspection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordDTO;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspection/record")
public class InspectionRecordController {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @PreAuthorize("hasAuthority('inspection:record:add')")
    @PostMapping
    public Result add(@Valid @RequestBody InspectionRecordDTO record) {
        inspectionRecordService.addRecord(record);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:record:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody InspectionRecordDTO record) {
        inspectionRecordService.updateRecord(record);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:record:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionRecordService.deleteRecord(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('inspection:record:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        InspectionRecordDomain domain = inspectionRecordService.getRecordById(id);
        return Result.success(domain);
    }

    @PreAuthorize("hasAuthority('inspection:record:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long planId,
                       @RequestParam(required = false) Long equipmentId) {
        Page<InspectionRecordDomain> page = inspectionRecordService.page(pageNum, pageSize, planId, equipmentId);
        return Result.success(page);
    }
}
