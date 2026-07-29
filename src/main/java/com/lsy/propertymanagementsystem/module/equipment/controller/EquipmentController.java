package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentDTO;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentVO;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PreAuthorize("hasAuthority('equipment:list:add')")
    @PostMapping
    public Result add(@Valid @RequestBody EquipmentDTO domain) {
        equipmentService.addEquipment(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:list:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody EquipmentDTO domain) {
        equipmentService.updateEquipment(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:list:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('equipment:list:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentVO vo = equipmentService.getById(id);
        return Result.success(vo);
    }

    @PreAuthorize("hasAuthority('equipment:list:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Integer status) {
        Page<EquipmentVO> page = equipmentService.page(pageNum, pageSize, categoryId, status);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('equipment:list:edit')")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        equipmentService.updateStatus(id, status);
        return Result.success();
    }
}
