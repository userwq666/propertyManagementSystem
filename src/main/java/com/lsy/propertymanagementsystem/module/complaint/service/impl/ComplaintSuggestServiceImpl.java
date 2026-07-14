package com.lsy.propertymanagementsystem.module.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.complaint.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.module.complaint.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintSuggestServiceImpl extends ServiceImpl<ComplaintSuggestMapper, ComplaintSuggest> implements ComplaintSuggestService {

    @Override
    public Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, String type, Integer status) {
        LambdaQueryWrapper<ComplaintSuggest> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(ComplaintSuggest::getOwnerId, ownerId);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(ComplaintSuggest::getType, type);
        }
        if (status != null) {
            wrapper.eq(ComplaintSuggest::getStatus, status);
        }
        wrapper.orderByDesc(ComplaintSuggest::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addComplaintSuggest(ComplaintSuggest record) {
        record.setStatus(0);
        this.save(record);
    }

    @Override
    @Transactional
    public void updateComplaintSuggest(ComplaintSuggest record) {
        ComplaintSuggest existing = this.getById(record.getId());
        if (existing == null) {
            throw new BusinessException("投诉建议不存在");
        }
        existing.setType(record.getType());
        existing.setTitle(record.getTitle());
        existing.setContent(record.getContent());
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteComplaintSuggest(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, String handleUser, String handleResult) {
        ComplaintSuggest record = this.getById(id);
        if (record == null) {
            throw new BusinessException("投诉建议不存在");
        }
        record.setStatus(status);
        if (handleUser != null) {
            record.setHandleUser(handleUser);
        }
        if (handleResult != null) {
            record.setHandleResult(handleResult);
        }
        if (status == 3) {
            record.setFinishTime(LocalDateTime.now());
        }
        this.updateById(record);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        ComplaintSuggest record = this.getById(id);
        if (record == null) {
            throw new BusinessException("投诉建议不存在");
        }
        if (record.getStatus() != 3) {
            throw new BusinessException("只有已完成的投诉建议才能评价");
        }
        record.setRating(rating);
        this.updateById(record);
    }
}