package com.lsy.propertymanagementsystem.module.announcement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.announcement.domain.AnnouncementDomain;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementDTO;
import com.lsy.propertymanagementsystem.module.announcement.dto.AnnouncementVO;
import com.lsy.propertymanagementsystem.module.announcement.enums.PublishStatus;
import com.lsy.propertymanagementsystem.module.announcement.mapper.AnnouncementMapper;
import com.lsy.propertymanagementsystem.module.announcement.service.AnnouncementService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementDomain> implements AnnouncementService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public AnnouncementVO getById(Long id) {
        AnnouncementDomain domain = super.getById(id);
        if (domain == null) {
            return null;
        }
        return convertToVO(domain);
    }

    @Override
    public Page<AnnouncementVO> page(int pageNum, int pageSize, String title, Integer status) {
        LambdaQueryWrapper<AnnouncementDomain> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(AnnouncementDomain::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(AnnouncementDomain::getPublishStatus, PublishStatus.of(status));
        }
        wrapper.orderByDesc(AnnouncementDomain::getCreateTime);
        Page<AnnouncementDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        List<AnnouncementVO> voList = convertToVOList(domainPage.getRecords());
        Page<AnnouncementVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
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
        AnnouncementDomain existing = super.getById(domain.getId());
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
        AnnouncementDomain announcement = super.getById(id);
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
        AnnouncementDomain announcement = super.getById(id);
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

    private AnnouncementVO convertToVO(AnnouncementDomain domain) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(domain, vo);
        if (domain.getCreatorId() != null) {
            SysUserDomain user = sysUserMapper.selectById(domain.getCreatorId());
            if (user != null) {
                vo.setCreatorName(user.getRealName());
            }
        }
        return vo;
    }

    private List<AnnouncementVO> convertToVOList(List<AnnouncementDomain> domains) {
        if (domains == null || domains.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> creatorIds = domains.stream()
                .map(AnnouncementDomain::getCreatorId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, String> creatorNameMap = !creatorIds.isEmpty()
                ? sysUserMapper.selectBatchIds(creatorIds).stream()
                    .filter(u -> u.getRealName() != null)
                    .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName, (a, b) -> a))
                : Collections.emptyMap();
        return domains.stream().map(domain -> {
            AnnouncementVO vo = new AnnouncementVO();
            BeanUtils.copyProperties(domain, vo);
            if (domain.getCreatorId() != null) {
                vo.setCreatorName(creatorNameMap.get(domain.getCreatorId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }
}