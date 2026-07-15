package com.lsy.propertymanagementsystem.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityParkingDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityParkingDTO;
import com.lsy.propertymanagementsystem.module.community.service.CommunityParkingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/parking")
public class CommunityParkingController {

    @Autowired
    private CommunityParkingService parkingService;

    @PostMapping
    public Result add(@Valid @RequestBody CommunityParkingDTO parking) {
        parkingService.addParking(parking);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody CommunityParkingDTO parking) {
        parkingService.updateParking(parking);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(parkingService.getParkingById(id));
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String parkingNo,
                       @RequestParam(required = false) Integer status) {
        Page<CommunityParkingDomain> page = parkingService.page(pageNum, pageSize, parkingNo, status);
        return Result.success(page);
    }
}