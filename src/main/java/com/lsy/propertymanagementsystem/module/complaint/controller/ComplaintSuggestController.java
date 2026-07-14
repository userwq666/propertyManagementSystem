package com.lsy.propertymanagementsystem.module.complaint.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.complaint.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint/suggest")
public class ComplaintSuggestController {

    @Autowired
    private ComplaintSuggestService complaintSuggestService;

    @PostMapping
    public Result add(@RequestBody ComplaintSuggest record) {
        complaintSuggestService.addComplaintSuggest(record);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody ComplaintSuggest record) {
        complaintSuggestService.updateComplaintSuggest(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        complaintSuggestService.deleteComplaintSuggest(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ComplaintSuggest complaintSuggest = complaintSuggestService.getById(id);
        return Result.success(complaintSuggest);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer status) {
        Page<ComplaintSuggest> page = complaintSuggestService.page(pageNum, pageSize, ownerId, type, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) String handleUser,
                               @RequestParam(required = false) String handleResult) {
        complaintSuggestService.updateStatus(id, status, handleUser, handleResult);
        return Result.success();
    }

    @PutMapping("/rating")
    public Result updateRating(@RequestParam Long id, @RequestParam Integer rating) {
        complaintSuggestService.updateRating(id, rating);
        return Result.success();
    }
}