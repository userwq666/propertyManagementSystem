package com.lsy.propertymanagementsystem.module.fee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee/item")
public class FeeItemController {

    @Autowired
    private FeeItemService feeItemService;

    @PreAuthorize("hasAuthority('fee:item:add')")
    @PostMapping
    public Result add(@Valid @RequestBody FeeItemDTO domain) {
        feeItemService.add(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:item:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody FeeItemDTO domain) {
        feeItemService.update(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:item:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        feeItemService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:item:list')")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeItemDomain domain = feeItemService.getById(id);
        return Result.success(domain);
    }

    @PreAuthorize("hasAuthority('fee:item:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String itemName,
                       @RequestParam(required = false) Integer status) {
        Page<FeeItemDomain> page = feeItemService.page(pageNum, pageSize, itemName, status);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('fee:item:edit')")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        feeItemService.updateStatus(id, status);
        return Result.success();
    }
}
