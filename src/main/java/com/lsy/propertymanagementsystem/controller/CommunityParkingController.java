package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;
import com.lsy.propertymanagementsystem.service.CommunityParkingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class CommunityParkingController {
    
    @Autowired
    private CommunityParkingService parkingService;
    
    @PostMapping
    public Result<Void> addParking(@RequestBody @Valid ParkingRequest request) {
        parkingService.addParking(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateParking(@RequestBody @Valid ParkingRequest request) {
        parkingService.updateParking(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteParking(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityParking>> getParkingList() {
        return Result.success(parkingService.getParkingList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityParking> getParkingById(@PathVariable Long id) {
        return Result.success(parkingService.getParkingById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityParking>> getParkingPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String parkingNo,
            @RequestParam(required = false) Integer status) {
        return Result.success(parkingService.getParkingPage(pageNum, pageSize, parkingNo, status));
    }
    
    @PutMapping("/status")
    public Result<Void> updateParkingStatus(@RequestParam Long id, @RequestParam Integer status, @RequestParam(required = false) Long ownerId) {
        parkingService.updateParkingStatus(id, status, ownerId);
        return Result.success();
    }
}
