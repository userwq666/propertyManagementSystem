package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;
import com.lsy.propertymanagementsystem.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentRequest request) {
        equipmentService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentRequest request) {
        equipmentService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Integer status) {
        Page<Equipment> page = equipmentService.page(pageNum, pageSize, categoryId, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        equipmentService.updateStatus(id, status);
        return Result.success();
    }
}