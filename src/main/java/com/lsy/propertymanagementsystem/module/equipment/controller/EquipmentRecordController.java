package com.lsy.propertymanagementsystem.module.equipment.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.equipment.service.EquipmentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment/record")
public class EquipmentRecordController {

    @Autowired
    private EquipmentRecordService equipmentRecordService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/equipments")
    public Result<List<Map<String, Object>>> equipments() {
        return Result.success(equipmentRecordService.listEquipmentOptions());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary(@RequestParam Long equipmentId) {
        return Result.success(equipmentRecordService.getSummary(equipmentId));
    }
}
