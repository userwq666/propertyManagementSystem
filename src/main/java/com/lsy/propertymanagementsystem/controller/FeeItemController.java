package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import com.lsy.propertymanagementsystem.service.FeeItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fee/item")
public class FeeItemController {

    @Autowired
    private FeeItemService feeItemService;

    @PostMapping
    public Result add(@Valid @RequestBody FeeItemRequest request) {
        feeItemService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody FeeItemRequest request) {
        feeItemService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        feeItemService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeItem feeItem = feeItemService.getById(id);
        return Result.success(feeItem);
    }

    @GetMapping("/list")
    public Result list() {
        List<FeeItem> list = feeItemService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<FeeItem> page = feeItemService.page(pageNum, pageSize);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        feeItemService.updateStatus(id, status);
        return Result.success();
    }
}