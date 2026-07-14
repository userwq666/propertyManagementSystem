package com.lsy.propertymanagementsystem.module.announcement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.announcement.entity.Announcement;

public interface AnnouncementService {
    void addAnnouncement(Announcement announcement);
    void updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Long id);
    Announcement getById(Long id);
    Page<Announcement> page(int pageNum, int pageSize, String title, Integer status);
    void updateStatus(Long id, Integer status);
    void updateIsTop(Long id, Integer isTop);
}