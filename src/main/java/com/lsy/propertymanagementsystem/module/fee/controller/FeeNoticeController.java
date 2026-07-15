package com.lsy.propertymanagementsystem.module.fee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeNoticeDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeNoticeDTO;
import com.lsy.propertymanagementsystem.module.fee.service.FeeNoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee/notice")
public class FeeNoticeController {

    @Autowired
    private FeeNoticeService feeNoticeService;

    @PostMapping
    public Result add(@Valid @RequestBody FeeNoticeDTO dto) {
        FeeNoticeDomain domain = new FeeNoticeDomain();
        BeanUtils.copyProperties(dto, domain);
        feeNoticeService.add(domain);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody FeeNoticeDTO dto) {
        FeeNoticeDomain domain = new FeeNoticeDomain();
        BeanUtils.copyProperties(dto, domain);
        feeNoticeService.update(domain);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        feeNoticeService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeNoticeDomain domain = feeNoticeService.getById(id);
        return Result.success(domain);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Integer noticeType,
                       @RequestParam(required = false) Integer sendStatus) {
        Page<FeeNoticeDomain> page = feeNoticeService.page(pageNum, pageSize, noticeType, sendStatus);
        return Result.success(page);
    }

    @PutMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        feeNoticeService.publish(id);
        return Result.success();
    }
}
