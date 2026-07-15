package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;
import com.lsy.propertymanagementsystem.module.equipment.dto.EquipmentMaintenanceDTO;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentMaintenanceService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment/maintenance")
public class EquipmentMaintenanceController {

    @Autowired
    private EquipmentMaintenanceService equipmentMaintenanceService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentMaintenanceDTO dto) {
        EquipmentMaintenanceDomain domain = new EquipmentMaintenanceDomain();
        BeanUtils.copyProperties(dto, domain);
        equipmentMaintenanceService.add(domain);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentMaintenanceDTO dto) {
        EquipmentMaintenanceDomain domain = new EquipmentMaintenanceDomain();
        BeanUtils.copyProperties(dto, domain);
        equipmentMaintenanceService.update(domain);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentMaintenanceService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentMaintenanceDomain domain = equipmentMaintenanceService.getById(id);
        return Result.success(domain);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long equipmentId,
                       @RequestParam(required = false) Integer maintenanceType,
                       @RequestParam(required = false) Integer status) {
        Page<EquipmentMaintenanceDomain> page = equipmentMaintenanceService.page(pageNum, pageSize, equipmentId, maintenanceType, status);
        return Result.success(page);
    }

    @PutMapping("/start/{id}")
    public Result startMaintenance(@PathVariable Long id) {
        equipmentMaintenanceService.startMaintenance(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result completeMaintenance(@PathVariable Long id) {
        equipmentMaintenanceService.completeMaintenance(id);
        return Result.success();
    }
}
