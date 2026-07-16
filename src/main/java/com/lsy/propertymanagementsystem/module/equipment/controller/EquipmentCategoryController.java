package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentCategoryDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentCategoryDTO;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService categoryService;

    @PreAuthorize("hasAuthority('equipment:category:add')")
    @PostMapping
    public Result add(@Valid @RequestBody EquipmentCategoryDTO domain) {
        categoryService.add(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:category:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody EquipmentCategoryDTO domain) {
        categoryService.update(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:category:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:category:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<EquipmentCategoryDomain> page = categoryService.page(pageNum, pageSize);
        return Result.success(page);
    }
}
