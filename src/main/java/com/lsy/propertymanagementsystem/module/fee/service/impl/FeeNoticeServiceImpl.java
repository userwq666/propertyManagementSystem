package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeNoticeDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeNoticeDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeNoticeVO;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeNoticeMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeNoticeService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeeNoticeServiceImpl extends ServiceImpl<FeeNoticeMapper, FeeNoticeDomain> implements FeeNoticeService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void add(FeeNoticeDTO dto) {
        FeeNoticeDomain domain = new FeeNoticeDomain();
        BeanUtils.copyProperties(dto, domain);
        if (domain.getSendStatus() == null) {
            domain.setSendStatus(0);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(FeeNoticeDTO dto) {
        FeeNoticeDomain domain = new FeeNoticeDomain();
        BeanUtils.copyProperties(dto, domain);
        FeeNoticeDomain existing = super.getById(domain.getId());
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
        FeeNoticeDomain existing = super.getById(id);
        if (existing != null && existing.getSendStatus() == 1) {
            throw new BusinessException("已发送的通知不允许删除");
        }
        this.removeById(id);
    }

    @Override
    public FeeNoticeVO getById(Long id) {
        FeeNoticeDomain domain = super.getById(id);
        return convertToVO(domain);
    }

    @Override
    public Page<FeeNoticeVO> page(int pageNum, int pageSize, Integer noticeType, Integer sendStatus) {
        LambdaQueryWrapper<FeeNoticeDomain> wrapper = new LambdaQueryWrapper<>();
        if (noticeType != null) {
            wrapper.eq(FeeNoticeDomain::getNoticeType, noticeType);
        }
        if (sendStatus != null) {
            wrapper.eq(FeeNoticeDomain::getSendStatus, sendStatus);
        }
        wrapper.orderByDesc(FeeNoticeDomain::getCreateTime);
        Page<FeeNoticeDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        Set<Long> creatorIds = domainPage.getRecords().stream()
                .map(FeeNoticeDomain::getCreatorId)
                .collect(Collectors.toSet());

        Map<Long, String> creatorNameMap = !creatorIds.isEmpty()
                ? sysUserMapper.selectBatchIds(creatorIds).stream()
                    .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName))
                : Collections.emptyMap();

        Page<FeeNoticeVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(domainPage.getTotal());
        voPage.setRecords(domainPage.getRecords().stream()
                .map(domain -> convertToVO(domain, creatorNameMap))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void publish(Long id) {
        FeeNoticeDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("缴费通知不存在");
        }
        if (domain.getSendStatus() == 1) {
            throw new BusinessException("通知已发送，请勿重复发送");
        }
        domain.publish();
        this.updateById(domain);
    }

    private FeeNoticeVO convertToVO(FeeNoticeDomain domain) {
        return convertToVO(domain, Collections.emptyMap());
    }

    private FeeNoticeVO convertToVO(FeeNoticeDomain domain, Map<Long, String> creatorNameMap) {
        if (domain == null) {
            return null;
        }
        FeeNoticeVO vo = new FeeNoticeVO();
        BeanUtils.copyProperties(domain, vo);
        if (creatorNameMap.containsKey(domain.getCreatorId())) {
            vo.setCreatorName(creatorNameMap.get(domain.getCreatorId()));
        }
        return vo;
    }
}