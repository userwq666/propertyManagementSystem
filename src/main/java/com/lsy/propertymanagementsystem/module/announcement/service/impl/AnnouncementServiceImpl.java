package com.lsy.propertymanagementsystem.module.announcement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.announcement.domain.AnnouncementDomain;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementDTO;
import com.lsy.propertymanagementsystem.module.announcement.enums.PublishStatus;
import com.lsy.propertymanagementsystem.module.announcement.mapper.AnnouncementMapper;
import com.lsy.propertymanagementsystem.module.announcement.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementDomain> implements AnnouncementService {

    @Override
    public AnnouncementDomain getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<AnnouncementDomain> page(int pageNum, int pageSize, String title, Integer status) {
        LambdaQueryWrapper<AnnouncementDomain> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(AnnouncementDomain::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(AnnouncementDomain::getPublishStatus, PublishStatus.of(status));
        }
        wrapper.orderByDesc(AnnouncementDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addAnnouncement(AnnouncementDTO dto) {
        AnnouncementDomain domain = new AnnouncementDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.prepareAdd();
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateAnnouncement(AnnouncementDTO dto) {
        AnnouncementDomain domain = new AnnouncementDomain();
        BeanUtils.copyProperties(dto, domain);
        AnnouncementDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        existing.setTitle(domain.getTitle());
        existing.setContent(domain.getContent());
        existing.setType(domain.getType());
        existing.setPublishTime(domain.getPublishTime());
        existing.setTopExpireTime(domain.getTopExpireTime());
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
        AnnouncementDomain announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        PublishStatus newStatus = PublishStatus.of(status);
        if (newStatus == PublishStatus.PUBLISHED) {
            announcement.publish();
        } else if (newStatus == PublishStatus.OFFLINE) {
            announcement.offline();
        } else {
            announcement.setPublishStatus(newStatus);
        }
        this.updateById(announcement);
    }

    @Override
    @Transactional
    public void updateIsTop(Long id, Integer isTop) {
        AnnouncementDomain announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        if (isTop == 1) {
            announcement.topUntil(announcement.getTopExpireTime());
        } else {
            announcement.cancelTop();
        }
        this.updateById(announcement);
    }
}