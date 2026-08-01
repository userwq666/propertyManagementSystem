package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeExpenseDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseVO;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeExpenseMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeExpenseService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FeeExpenseServiceImpl extends ServiceImpl<FeeExpenseMapper, FeeExpenseDomain> implements FeeExpenseService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Page<FeeExpenseVO> page(int pageNum, int pageSize, String expenseName, Integer expenseType) {
        LambdaQueryWrapper<FeeExpenseDomain> wrapper = new LambdaQueryWrapper<>();
        if (expenseName != null && !expenseName.isEmpty()) {
            wrapper.like(FeeExpenseDomain::getExpenseName, expenseName);
        }
        if (expenseType != null) {
            wrapper.eq(FeeExpenseDomain::getExpenseType, expenseType);
        }
        if (SecurityUtils.isOwner()) {
            // 业主只能查看审核通过的费用公示
            wrapper.eq(FeeExpenseDomain::getAuditStatus, 1);
        }
        wrapper.orderByDesc(FeeExpenseDomain::getExpenseDate).orderByDesc(FeeExpenseDomain::getCreateTime);
        Page<FeeExpenseDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        Page<FeeExpenseVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(convertToVO(domainPage.getRecords()));
        return voPage;
    }

    @Override
    @Transactional
    public void add(FeeExpenseDTO dto) {
        FeeExpenseDomain domain = new FeeExpenseDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.setCreatorId(SecurityUtils.getCurrentUserId());
        if (domain.getAuditStatus() == null) {
            domain.setAuditStatus(0);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void update(FeeExpenseDTO dto) {
        FeeExpenseDomain existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("消费事项不存在");
        }
        if (existing.getAuditStatus() != null && existing.getAuditStatus() != 0) {
            throw new BusinessException("已审核的消费事项不允许编辑");
        }
        String roleKey = SecurityUtils.getRoleKey();
        boolean managerLike = "admin".equals(roleKey) || "property_admin".equals(roleKey) || "finance".equals(roleKey);
        if (!managerLike && !Objects.equals(existing.getCreatorId(), SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能编辑自己申报的消费事项");
        }
        BeanUtils.copyProperties(dto, existing);
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void audit(Long id, Integer status) {
        FeeExpenseDomain existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("消费事项不存在");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException("审核状态不正确");
        }
        existing.setAuditStatus(status);
        existing.setAuditorId(SecurityUtils.getCurrentUserId());
        existing.setAuditTime(LocalDateTime.now());
        this.updateById(existing);
    }

    private List<FeeExpenseVO> convertToVO(List<FeeExpenseDomain> domains) {
        if (domains.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> creatorIds = domains.stream().map(FeeExpenseDomain::getCreatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> auditorIds = domains.stream().map(FeeExpenseDomain::getAuditorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> userIds = new java.util.ArrayList<>(creatorIds);
        userIds.addAll(auditorIds);
        Map<Long, String> creatorNameMap = creatorIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(creatorIds).stream()
                        .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));
        Map<Long, String> auditorNameMap = auditorIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(auditorIds).stream()
                        .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));
        return domains.stream().map(d -> {
            FeeExpenseVO vo = new FeeExpenseVO();
            BeanUtils.copyProperties(d, vo);
            vo.setExpenseTypeName(expenseTypeName(d.getExpenseType()));
            vo.setCreatorName(creatorNameMap.get(d.getCreatorId()));
            vo.setAuditStatusName(auditStatusName(d.getAuditStatus()));
            vo.setAuditorName(auditorNameMap.get(d.getAuditorId()));
            return vo;
        }).collect(Collectors.toList());
    }

    private String auditStatusName(Integer status) {
        if (status == null) return "待审核";
        switch (status) {
            case 1: return "已通过";
            case 2: return "已驳回";
            default: return "待审核";
        }
    }

    private String expenseTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "维修";
            case 2: return "人工";
            case 3: return "材料";
            default: return "其他";
        }
    }
}
