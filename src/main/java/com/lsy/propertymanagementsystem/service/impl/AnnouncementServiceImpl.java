package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;
import com.lsy.propertymanagementsystem.mapper.AnnouncementMapper;
import com.lsy.propertymanagementsystem.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    @Transactional
    public void add(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(request, announcement);
        announcement.setStatus(0);
        announcement.setIsTop(0);
        announcementMapper.insert(announcement);
    }

    @Override
    @Transactional
    public void update(AnnouncementRequest request) {
        Announcement announcement = announcementMapper.selectById(request.getId());
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        BeanUtils.copyProperties(request, announcement);
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public Page<Announcement> page(int pageNum, int pageSize, String type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getIsTop).orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setStatus(status);
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional
    public void updateTop(Long id, Integer isTop) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setIsTop(isTop);
        announcementMapper.updateById(announcement);
    }
}
