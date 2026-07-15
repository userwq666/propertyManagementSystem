package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentCategoryDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryDTO;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentCategoryDTO category) {
        equipmentCategoryService.add(category);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentCategoryDTO category) {
        equipmentCategoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentCategoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result getList() {
        List<EquipmentCategoryDomain> list = equipmentCategoryService.getList();
        return Result.success(list);
    }
}