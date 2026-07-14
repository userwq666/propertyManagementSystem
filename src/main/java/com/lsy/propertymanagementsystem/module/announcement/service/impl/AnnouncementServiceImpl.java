package com.lsy.propertymanagementsystem.module.announcement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.announcement.entity.Announcement;
import com.lsy.propertymanagementsystem.module.announcement.mapper.AnnouncementMapper;
import com.lsy.propertymanagementsystem.module.announcement.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Page<Announcement> page(int pageNum, int pageSize, String title, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Announcement::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addAnnouncement(Announcement announcement) {
        announcement.setStatus(0);
        this.save(announcement);
    }

    @Override
    @Transactional
    public void updateAnnouncement(Announcement announcement) {
        Announcement existing = this.getById(announcement.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setType(announcement.getType());
        existing.setPublishTime(announcement.getPublishTime());
        existing.setExpireTime(announcement.getExpireTime());
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setStatus(status);
        if (status == 2) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        this.updateById(announcement);
    }

    @Override
    @Transactional
    public void updateIsTop(Long id, Integer isTop) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setIsTop(isTop);
        this.updateById(announcement);
    }
}