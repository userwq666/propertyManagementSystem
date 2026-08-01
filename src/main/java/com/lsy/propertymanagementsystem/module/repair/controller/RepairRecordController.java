package com.lsy.propertymanagementsystem.module.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordVO;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/repair/record")
public class RepairRecordController {

    @Autowired
    private RepairRecordService repairRecordService;

    @PreAuthorize("hasAuthority('repair:record:add')")
    @PostMapping
    public Result add(@Valid @RequestBody RepairRecordDTO record) {
        repairRecordService.addRepair(record);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('repair:record:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody RepairRecordDTO record) {
        repairRecordService.updateRepair(record);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('repair:record:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        repairRecordService.deleteRepair(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('repair:record:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        RepairRecordVO vo = repairRecordService.getById(id);
        return Result.success(vo);
    }

    @PreAuthorize("hasAuthority('repair:record:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long handlerId,
                       @RequestParam(required = false) Long equipmentId,
                       @RequestParam(required = false) Integer status) {
        Page<RepairRecordVO> page = repairRecordService.page(pageNum, pageSize, ownerId, handlerId, equipmentId, status);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('repair:record:process')")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) Long handlerId,
                               @RequestParam(required = false) Long equipmentId,
                               @RequestParam(required = false) String handleContent,
                               @RequestParam(required = false) BigDecimal expenseAmount,
                               @RequestParam(required = false) Integer expenseType) {
        repairRecordService.updateStatus(id, status, handlerId, equipmentId, handleContent, expenseAmount, expenseType);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('repair:record:evaluate')")
    @PutMapping("/rating")
    public Result updateRating(@RequestParam Long id, @RequestParam Integer score, @RequestParam(required = false) String content) {
        repairRecordService.updateRating(id, score, content);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('repair:record:add')")
    @GetMapping("/houses")
    public Result<List<CommunityHouseDomain>> listHouses(@RequestParam(required = false) Long ownerId) {
        return Result.success(repairRecordService.listHouses(ownerId));
    }

    @PreAuthorize("hasAuthority('repair:record:add') or hasAuthority('repair:record:process')")
    @GetMapping("/equipments")
    public Result<List<EquipmentDomain>> listEquipments() {
        return Result.success(repairRecordService.listEquipments());
    }
}
