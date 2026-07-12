package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;
import com.lsy.propertymanagementsystem.service.CommunityOwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class CommunityOwnerController {
    
    @Autowired
    private CommunityOwnerService ownerService;
    
    @PostMapping
    public Result<Void> addOwner(@RequestBody @Valid OwnerRequest request) {
        ownerService.addOwner(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateOwner(@RequestBody @Valid OwnerRequest request) {
        ownerService.updateOwner(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityOwner>> getOwnerList() {
        return Result.success(ownerService.getOwnerList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityOwner> getOwnerById(@PathVariable Long id) {
        return Result.success(ownerService.getOwnerById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityOwner>> getOwnerPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        return Result.success(ownerService.getOwnerPage(pageNum, pageSize, name, phone));
    }
    
    @PostMapping("/bindUser")
    public Result<Void> bindUser(@RequestParam Long ownerId, @RequestParam Long userId) {
        ownerService.bindUser(ownerId, userId);
        return Result.success();
    }
}
