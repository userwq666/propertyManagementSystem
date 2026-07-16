package com.lsy.propertymanagementsystem.module.complaint.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint/suggest")
public class ComplaintSuggestController {
    // 投诉建议服务
    @Autowired
    private ComplaintSuggestService complaintSuggestService;

    // 新增投诉建议
    @PostMapping
    public Result add(@Valid @RequestBody ComplaintSuggestDTO domain) {
        complaintSuggestService.add(domain);
        return Result.success();
    }

    // 更新投诉建议
    @PutMapping
    public Result update(@Valid @RequestBody ComplaintSuggestDTO domain) {
        complaintSuggestService.update(domain);
        return Result.success();
    }

    // 删除投诉建议
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        complaintSuggestService.delete(id);
        return Result.success();
    }

    // 分页查询投诉建议
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer status) {
        Page<ComplaintSuggestDomain> page = complaintSuggestService.page(pageNum, pageSize, ownerId, type, status);
        return Result.success(page);
    }

    // 更新投诉建议状态
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) Long handlerId,
                               @RequestParam(required = false) String handleContent) {
        complaintSuggestService.updateStatus(id, status, handlerId, handleContent);
        return Result.success();
    }
}