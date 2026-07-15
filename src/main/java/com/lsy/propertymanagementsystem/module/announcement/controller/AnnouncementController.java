package com.lsy.propertymanagementsystem.module.announcement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.announcement.domain.AnnouncementDomain;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementDTO;
import com.lsy.propertymanagementsystem.module.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    public Result add(@Valid @RequestBody AnnouncementDTO announcement) {
        announcementService.addAnnouncement(announcement);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody AnnouncementDTO announcement) {
        announcementService.updateAnnouncement(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        AnnouncementDomain announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String title,
                       @RequestParam(required = false) Integer status) {
        Page<AnnouncementDomain> page = announcementService.page(pageNum, pageSize, title, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/top")
    public Result updateTop(@RequestParam Long id, @RequestParam Integer isTop) {
        announcementService.updateIsTop(id, isTop);
        return Result.success();
    }
}