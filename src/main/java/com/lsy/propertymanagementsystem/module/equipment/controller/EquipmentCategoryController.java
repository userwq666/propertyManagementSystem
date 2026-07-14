package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @PostMapping
    public Result add(@RequestBody EquipmentCategory category) {
        equipmentCategoryService.addCategory(category);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody EquipmentCategory category) {
        equipmentCategoryService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentCategoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentCategory category = equipmentCategoryService.getById(id);
        return Result.success(category);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String categoryName) {
        Page<EquipmentCategory> page = equipmentCategoryService.page(pageNum, pageSize, categoryName);
        return Result.success(page);
    }
}