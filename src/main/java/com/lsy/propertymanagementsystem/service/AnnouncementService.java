package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;

public interface AnnouncementService {
    void add(AnnouncementRequest request);
    void update(AnnouncementRequest request);
    void delete(Long id);
    Announcement getById(Long id);
    Page<Announcement> page(int pageNum, int pageSize, String type, Integer status);
    void updateStatus(Long id, Integer status);
    void updateTop(Long id, Integer isTop);
}
