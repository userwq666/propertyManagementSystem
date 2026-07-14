package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.entity.Equipment;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result add(@RequestBody Equipment equipment) {
        equipmentService.addEquipment(equipment);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Equipment equipment) {
        equipmentService.updateEquipment(equipment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
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