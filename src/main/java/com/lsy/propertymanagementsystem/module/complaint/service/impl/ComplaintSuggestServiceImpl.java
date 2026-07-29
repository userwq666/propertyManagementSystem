package com.lsy.propertymanagementsystem.module.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintType;
import com.lsy.propertymanagementsystem.module.complaint.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintSuggestServiceImpl extends ServiceImpl<ComplaintSuggestMapper, ComplaintSuggestDomain> implements ComplaintSuggestService {

    // 分页查询投诉建议
    @Override
    public Page<ComplaintSuggestDomain> page(int pageNum, int pageSize, Long ownerId, String type, Integer status) {
        LambdaQueryWrapper<ComplaintSuggestDomain> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(ComplaintSuggestDomain::getOwnerId, SecurityUtils.isOwner() ? SecurityUtils.getCurrentUserId() : ownerId);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(ComplaintSuggestDomain::getType, ComplaintType.of(Integer.parseInt(type)));
        }
        if (status != null) {
            wrapper.eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.of(status));
        }
        wrapper.orderByDesc(ComplaintSuggestDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    // 新增投诉建议
    @Override
    @Transactional
    public void add(ComplaintSuggestDTO dto) {
        ComplaintSuggestDomain domain = new ComplaintSuggestDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.prepareAdd();
        this.save(domain);
    }

    // 更新投诉建议
    @Override
    @Transactional
    public void update(ComplaintSuggestDTO dto) {
        ComplaintSuggestDomain domain = new ComplaintSuggestDomain();
        BeanUtils.copyProperties(dto, domain);
        ComplaintSuggestDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("投诉建议不存在");
        }
        existing.setType(domain.getType());
        existing.setCategory(domain.getCategory());
        existing.setContent(domain.getContent());
        this.updateById(existing);
    }

    // 删除投诉建议
    @Override
    @Transactional
    public void delete(Long id) {
        this.removeById(id);
    }

    // 更新投诉建议状态
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, Long handlerId, String handleContent) {
        ComplaintSuggestDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("投诉建议不存在");
        }
        ComplaintStatus newStatus = ComplaintStatus.of(status);
        if (newStatus == ComplaintStatus.ACCEPTED) {
            domain.assignHandler(handlerId);
        } else if (newStatus == ComplaintStatus.REPLIED) {
            domain.reply(handleContent);
        } else if (newStatus == ComplaintStatus.CLOSED) {
            domain.close();
        } else {
            domain.setStatus(newStatus);
        }
        this.updateById(domain);
    }
}