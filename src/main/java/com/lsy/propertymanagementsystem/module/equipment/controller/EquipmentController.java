package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentDTO domain) {
        equipmentService.addEquipment(domain);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentDTO domain) {
        equipmentService.updateEquipment(domain);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentDomain domain = equipmentService.getById(id);
        return Result.success(domain);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Integer status) {
        Page<EquipmentDomain> page = equipmentService.page(pageNum, pageSize, categoryId, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        equipmentService.updateStatus(id, status);
        return Result.success();
    }
}