package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.service.ComplaintSuggestService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintSuggestServiceImpl implements ComplaintSuggestService {

    @Autowired
    private ComplaintSuggestMapper complaintSuggestMapper;

    @Override
    @Transactional
    public void add(ComplaintSuggestRequest request) {
        ComplaintSuggest complaintSuggest = new ComplaintSuggest();
        BeanUtils.copyProperties(request, complaintSuggest);
        complaintSuggest.setStatus(0); // 默认状态：待受理
        complaintSuggestMapper.insert(complaintSuggest);
    }

    @Override
    @Transactional
    public void update(ComplaintSuggestRequest request) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(request.getId());
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        BeanUtils.copyProperties(request, complaintSuggest);
        complaintSuggestMapper.updateById(complaintSuggest);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        complaintSuggestMapper.deleteById(id);
    }

    @Override
    public ComplaintSuggest getById(Long id) {
        return complaintSuggestMapper.selectById(id);
    }

    @Override
    public Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status, String type) {
        LambdaQueryWrapper<ComplaintSuggest> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(ComplaintSuggest::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(ComplaintSuggest::getHouseId, houseId);
        }
        if (status != null) {
            wrapper.eq(ComplaintSuggest::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(ComplaintSuggest::getType, type);
        }
        wrapper.orderByDesc(ComplaintSuggest::getCreateTime);
        return complaintSuggestMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, String handleUser, String handleResult) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(id);
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        complaintSuggest.setStatus(status);
        if (handleUser != null) complaintSuggest.setHandleUser(handleUser);
        if (handleResult != null) complaintSuggest.setHandleResult(handleResult);
        if (status == 3) complaintSuggest.setFinishTime(LocalDateTime.now()); // 完成时自动记录时间
        complaintSuggestMapper.updateById(complaintSuggest);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer rating) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(id);
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        if (complaintSuggest.getStatus() != 3) {
            throw new BusinessException("只能评价已完成的投诉建议");
        }
        complaintSuggest.setRating(rating);
        complaintSuggest.setStatus(4); // 更新状态为已评价
        complaintSuggestMapper.updateById(complaintSuggest);
    }
}