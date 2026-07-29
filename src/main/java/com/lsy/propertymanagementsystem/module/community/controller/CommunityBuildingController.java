package com.lsy.propertymanagementsystem.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityBuildingVO;
import com.lsy.propertymanagementsystem.module.community.service.CommunityBuildingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/building")
public class CommunityBuildingController {

    @Autowired
    private CommunityBuildingService buildingService;

    @PreAuthorize("hasAuthority('community:building:add')")
    @PostMapping
    public Result add(@Valid @RequestBody CommunityBuildingDTO building) {
        buildingService.addBuilding(building);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:building:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody CommunityBuildingDTO building) {
        buildingService.updateBuilding(building);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:building:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:building:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }

    @PreAuthorize("hasAuthority('community:building:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String buildingNo) {
        Page<CommunityBuildingVO> page = buildingService.page(pageNum, pageSize, buildingNo);
        return Result.success(page);
    }
}
