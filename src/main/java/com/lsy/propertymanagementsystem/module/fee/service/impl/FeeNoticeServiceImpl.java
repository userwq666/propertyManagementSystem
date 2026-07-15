package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeNoticeDomain;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeNoticeMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeNoticeServiceImpl extends ServiceImpl<FeeNoticeMapper, FeeNoticeDomain> implements FeeNoticeService {

    @Override
    @Transactional
    public void add(FeeNoticeDomain domain) {
        if (domain.getSendStatus() == null) {
            domain.setSendStatus(0);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(FeeNoticeDomain domain) {
        FeeNoticeDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("缴费通知不存在");
        }
        if (existing.getSendStatus() == 1) {
            throw new BusinessException("已发送的通知不允许修改");
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FeeNoticeDomain existing = this.getById(id);
        if (existing != null && existing.getSendStatus() == 1) {
            throw new BusinessException("已发送的通知不允许删除");
        }
        this.removeById(id);
    }

    @Override
    public FeeNoticeDomain getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<FeeNoticeDomain> page(int pageNum, int pageSize, Integer noticeType, Integer sendStatus) {
        LambdaQueryWrapper<FeeNoticeDomain> wrapper = new LambdaQueryWrapper<>();
        if (noticeType != null) {
            wrapper.eq(FeeNoticeDomain::getNoticeType, noticeType);
        }
        if (sendStatus != null) {
            wrapper.eq(FeeNoticeDomain::getSendStatus, sendStatus);
        }
        wrapper.orderByDesc(FeeNoticeDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        FeeNoticeDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("缴费通知不存在");
        }
        if (domain.getSendStatus() == 1) {
            throw new BusinessException("通知已发送，请勿重复发送");
        }
        domain.publish();
        this.updateById(domain);
    }
}
