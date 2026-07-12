package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.service.CommunityHouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house")
public class CommunityHouseController {
    
    @Autowired
    private CommunityHouseService houseService;
    
    @PostMapping
    public Result<Void> addHouse(@RequestBody @Valid HouseRequest request) {
        houseService.addHouse(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateHouse(@RequestBody @Valid HouseRequest request) {
        houseService.updateHouse(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityHouse>> getHouseList() {
        return Result.success(houseService.getHouseList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityHouse> getHouseById(@PathVariable Long id) {
        return Result.success(houseService.getHouseById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityHouse>> getHousePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNo,
            @RequestParam(required = false) Integer houseStatus) {
        return Result.success(houseService.getHousePage(pageNum, pageSize, buildingId, roomNo, houseStatus));
    }
    
    @PutMapping("/status")
    public Result<Void> updateHouseStatus(@RequestParam Long id, @RequestParam Integer houseStatus) {
        houseService.updateHouseStatus(id, houseStatus);
        return Result.success();
    }
}
