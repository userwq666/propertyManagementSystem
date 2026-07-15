package com.lsy.propertymanagementsystem.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerDTO;
import com.lsy.propertymanagementsystem.module.community.service.CommunityOwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/owner")
public class CommunityOwnerController {

    @Autowired
    private CommunityOwnerService ownerService;

    @PostMapping
    public Result add(@Valid @RequestBody CommunityOwnerDTO owner) {
        ownerService.addOwner(owner);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody CommunityOwnerDTO owner) {
        ownerService.updateOwner(owner);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(ownerService.getOwnerById(id));
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String phone) {
        Page<CommunityOwnerDomain> page = ownerService.page(pageNum, pageSize, name, phone);
        return Result.success(page);
    }
}