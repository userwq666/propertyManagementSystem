package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;
import com.lsy.propertymanagementsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    public Result add(@Valid @RequestBody AnnouncementRequest request) {
        announcementService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody AnnouncementRequest request) {
        announcementService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer status) {
        Page<Announcement> page = announcementService.page(pageNum, pageSize, type, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/top")
    public Result updateTop(@RequestParam Long id, @RequestParam Integer isTop) {
        announcementService.updateTop(id, isTop);
        return Result.success();
    }
}