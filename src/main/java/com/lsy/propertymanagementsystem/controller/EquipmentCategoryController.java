package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.service.EquipmentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentCategoryRequest request) {
        equipmentCategoryService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentCategoryRequest request) {
        equipmentCategoryService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentCategoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentCategory category = equipmentCategoryService.getById(id);
        return Result.success(category);
    }

    @GetMapping("/list")
    public Result list() {
        List<EquipmentCategory> list = equipmentCategoryService.list();
        return Result.success(list);
    }
}