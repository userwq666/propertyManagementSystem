package com.lsy.propertymanagementsystem.module.fee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseVO;
import com.lsy.propertymanagementsystem.module.fee.service.FeeExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee/expense")
public class FeeExpenseController {

    @Autowired
    private FeeExpenseService feeExpenseService;

    @PreAuthorize("hasAuthority('fee:expense:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String expenseName,
                       @RequestParam(required = false) Integer expenseType) {
        Page<FeeExpenseVO> page = feeExpenseService.page(pageNum, pageSize, expenseName, expenseType);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('fee:expense:add')")
    @PostMapping
    public Result add(@Valid @RequestBody FeeExpenseDTO dto) {
        feeExpenseService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:expense:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody FeeExpenseDTO dto) {
        feeExpenseService.update(dto);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('fee:expense:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        feeExpenseService.delete(id);
        return Result.success();
    }
}
