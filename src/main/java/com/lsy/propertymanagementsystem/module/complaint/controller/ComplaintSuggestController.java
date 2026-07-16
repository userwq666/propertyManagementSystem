package com.lsy.propertymanagementsystem.module.complaint.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint/suggest")
public class ComplaintSuggestController {

    @Autowired
    private ComplaintSuggestService complaintSuggestService;

    @PreAuthorize("hasAuthority('complaint:list:add')")
    @PostMapping
    public Result add(@Valid @RequestBody ComplaintSuggestDTO domain) {
        complaintSuggestService.add(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('complaint:list:edit')")
    @PutMapping
    public Result update(@Valid @RequestBody ComplaintSuggestDTO domain) {
        complaintSuggestService.update(domain);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('complaint:list:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        complaintSuggestService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('complaint:list:list')")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer status) {
        Page<ComplaintSuggestDomain> page = complaintSuggestService.page(pageNum, pageSize, ownerId, type, status);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('complaint:list:edit')")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) Long handlerId,
                               @RequestParam(required = false) String handleContent) {
        complaintSuggestService.updateStatus(id, status, handlerId, handleContent);
        return Result.success();
    }
}
