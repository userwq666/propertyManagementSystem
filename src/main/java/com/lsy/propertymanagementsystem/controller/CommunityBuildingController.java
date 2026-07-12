package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;
import com.lsy.propertymanagementsystem.service.CommunityBuildingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building")
public class CommunityBuildingController {
    
    @Autowired
    private CommunityBuildingService buildingService;
    
    @PostMapping
    public Result<Void> addBuilding(@RequestBody @Valid BuildingRequest request) {
        buildingService.addBuilding(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateBuilding(@RequestBody @Valid BuildingRequest request) {
        buildingService.updateBuilding(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityBuilding>> getBuildingList() {
        return Result.success(buildingService.getBuildingList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityBuilding> getBuildingById(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityBuilding>> getBuildingPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buildingNo) {
        return Result.success(buildingService.getBuildingPage(pageNum, pageSize, buildingNo));
    }
}
