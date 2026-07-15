package com.lsy.propertymanagementsystem.module.announcement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.announcement.domain.AnnouncementDomain;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementDTO;

public interface AnnouncementService {
    void addAnnouncement(AnnouncementDTO announcement);
    void updateAnnouncement(AnnouncementDTO announcement);
    void deleteAnnouncement(Long id);
    AnnouncementDomain getById(Long id);
    Page<AnnouncementDomain> page(int pageNum, int pageSize, String title, Integer status);
    void updateStatus(Long id, Integer status);
    void updateIsTop(Long id, Integer isTop);
}