package com.lsy.propertymanagementsystem.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerDTO;
import com.lsy.propertymanagementsystem.module.community.dto.CommunityOwnerVO;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.service.CommunityOwnerService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityOwnerServiceImpl extends ServiceImpl<CommunityOwnerMapper, CommunityOwnerDomain> implements CommunityOwnerService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Page<CommunityOwnerVO> page(int pageNum, int pageSize, String name, String phone) {
        LambdaQueryWrapper<CommunityOwnerDomain> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CommunityOwnerDomain::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(CommunityOwnerDomain::getPhone, phone);
        }
        if (SecurityUtils.isOwner()) {
            wrapper.eq(CommunityOwnerDomain::getUserId, SecurityUtils.getCurrentUserId());
        }
        wrapper.orderByDesc(CommunityOwnerDomain::getCreateTime);

        Page<CommunityOwnerDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        Set<Long> userIds = domainPage.getRecords().stream()
                .map(CommunityOwnerDomain::getUserId).filter(id -> id != null).collect(Collectors.toSet());

        final Map<Long, SysUserDomain> userMap;
        if (userIds.isEmpty()) {
            userMap = new HashMap<>();
        } else {
            List<SysUserDomain> users = sysUserMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(SysUserDomain::getId, u -> u));
        }

        List<CommunityOwnerVO> voList = domainPage.getRecords().stream()
                .map(d -> convertToVO(d, userMap))
                .collect(Collectors.toList());

        Page<CommunityOwnerVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
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
            throw new BusinessException("业主不存在");
        }
        BeanUtils.copyProperties(owner, existing);
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) {
        this.removeById(id);
    }

    @Override
    public CommunityOwnerVO getOwnerById(Long id) {
        CommunityOwnerDomain domain = this.getById(id);
        if (domain == null) {
            return null;
        }
        Map<Long, SysUserDomain> userMap = new HashMap<>();
        if (domain.getUserId() != null) {
            SysUserDomain user = sysUserMapper.selectById(domain.getUserId());
            if (user != null) {
                userMap.put(user.getId(), user);
            }
        }
        return convertToVO(domain, userMap);
    }

    @Override
    public CommunityOwnerDomain getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<CommunityOwnerDomain>().eq(CommunityOwnerDomain::getUserId, userId));
    }

    private CommunityOwnerVO convertToVO(CommunityOwnerDomain domain, Map<Long, SysUserDomain> userMap) {
        CommunityOwnerVO vo = new CommunityOwnerVO();
        BeanUtils.copyProperties(domain, vo);
        if (domain.getUserId() != null) {
            SysUserDomain user = userMap.get(domain.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
        }
        return vo;
    }
}
