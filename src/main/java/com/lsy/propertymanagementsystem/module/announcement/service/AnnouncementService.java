package com.lsy.propertymanagementsystem.module.announcement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementDTO;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementVO;

public interface AnnouncementService {
    void addAnnouncement(AnnouncementDTO announcement);
    void updateAnnouncement(AnnouncementDTO announcement);
    void deleteAnnouncement(Long id);
    AnnouncementVO getById(Long id);
    Page<AnnouncementVO> page(int pageNum, int pageSize, String title, Integer status);
    void updateStatus(Long id, Integer status);
    void updateIsTop(Long id, Integer isTop);
}