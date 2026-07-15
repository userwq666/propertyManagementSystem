package com.lsy.propertymanagementsystem.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityBuildingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingDTO;
import com.lsy.propertymanagementsystem.module.community.service.CommunityBuildingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/building")
public class CommunityBuildingController {

    @Autowired
    private CommunityBuildingService buildingService;

    @PostMapping
    public Result add(@Valid @RequestBody CommunityBuildingDTO building) {
        buildingService.addBuilding(building);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody CommunityBuildingDTO building) {
        buildingService.updateBuilding(building);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<CommunityBuildingDomain> page = buildingService.page(pageNum, pageSize);
        return Result.success(page);
    }
}