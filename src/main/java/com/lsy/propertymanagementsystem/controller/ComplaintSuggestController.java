package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.service.ComplaintSuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/complaint/suggest")
public class ComplaintSuggestController {

    @Autowired
    private ComplaintSuggestService complaintSuggestService;

    @PostMapping
    public Result add(@Valid @RequestBody ComplaintSuggestRequest request) {
        complaintSuggestService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody ComplaintSuggestRequest request) {
        complaintSuggestService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        complaintSuggestService.delete(id);
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
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = false) String type) {
        Page<ComplaintSuggest> page = complaintSuggestService.page(pageNum, pageSize, ownerId, houseId, status, type);
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