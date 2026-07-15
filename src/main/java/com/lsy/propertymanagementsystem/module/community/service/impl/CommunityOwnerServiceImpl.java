package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerDTO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityOwnerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityOwnerServiceImpl extends ServiceImpl<CommunityOwnerMapper, CommunityOwnerDomain> implements CommunityOwnerService {

    @Override
    public Page<CommunityOwnerDomain> page(int pageNum, int pageSize, String name, String phone) {
        LambdaQueryWrapper<CommunityOwnerDomain> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CommunityOwnerDomain::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(CommunityOwnerDomain::getPhone, phone);
        }
        wrapper.orderByDesc(CommunityOwnerDomain::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void addOwner(CommunityOwnerDTO owner) {
        CommunityOwnerDomain domain = new CommunityOwnerDomain();
        BeanUtils.copyProperties(owner, domain);
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateOwner(CommunityOwnerDTO owner) {
        CommunityOwnerDomain existing = this.getById(owner.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("业主不存在");
        }
        CommunityOwnerDomain domain = new CommunityOwnerDomain();
        BeanUtils.copyProperties(owner, domain);
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityOwnerDomain getOwnerById(Long id) {
        return this.getById(id);
    }

    @Override
    public CommunityOwnerDomain getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<CommunityOwnerDomain>().eq(CommunityOwnerDomain::getUserId, userId));
    }
}