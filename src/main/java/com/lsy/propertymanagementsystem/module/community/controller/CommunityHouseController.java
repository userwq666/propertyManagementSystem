package com.lsy.propertymanagementsystem.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityHouseDTO;
import com.lsy.propertymanagementsystem.module.community.service.CommunityHouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/house")
public class CommunityHouseController {

    @Autowired
    private CommunityHouseService houseService;

    @PreAuthorize("hasAuthority('community:house:add')")
    @PostMapping
    public Result add(@Valid @RequestBody CommunityHouseDTO house) {
        houseService.addHouse(house);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:house:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody CommunityHouseDTO house) {
        houseService.updateHouse(house);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:house:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('community:house:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(houseService.getHouseById(id));
    }

    @PreAuthorize("hasAuthority('community:house:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long buildingId,
                       @RequestParam(required = false) Integer houseStatus) {
        Page<CommunityHouseDomain> page = houseService.page(pageNum, pageSize, buildingId, houseStatus);
        return Result.success(page);
    }
}
