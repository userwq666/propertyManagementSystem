package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemScopeDomain;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeNoticeDomain;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemVO;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeCycleType;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeItemType;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeItemScopeMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeNoticeMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.websocket.MessagePushService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItemDomain> implements FeeItemService {

    @Autowired
    private FeeItemScopeMapper feeItemScopeMapper;

    @Autowired
    private CommunityHouseMapper communityHouseMapper;

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private FeeNoticeMapper feeNoticeMapper;

    @Autowired
    private MessagePushService messagePushService;

    @Override
    public FeeItemVO getById(Long id) {
        FeeItemDomain domain = super.getById(id);
        return convertToVO(domain);
    }

    @Override
    public Page<FeeItemVO> page(int pageNum, int pageSize, String itemName, Integer status) {
        LambdaQueryWrapper<FeeItemDomain> wrapper = new LambdaQueryWrapper<>();
        if (itemName != null && !itemName.isEmpty()) {
            wrapper.like(FeeItemDomain::getItemName, itemName);
        }
        if (status != null) {
            wrapper.eq(FeeItemDomain::getStatus, EnableStatus.of(status));
        }
        wrapper.orderByDesc(FeeItemDomain::getCreateTime);
        Page<FeeItemDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        Page<FeeItemVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(domainPage.getTotal());
        voPage.setRecords(domainPage.getRecords().stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void add(FeeItemDTO domain) {
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        feeItemDomain.setItemType(FeeItemType.of(domain.getItemType()));
        feeItemDomain.setCycleType(FeeCycleType.of(domain.getCycleType()));
        feeItemDomain.setStatus(EnableStatus.of(domain.getStatus()));
        if (feeItemDomain.getScopeType() == null) {
            feeItemDomain.setScopeType(1);
        }
        if (feeItemDomain.getPublished() == null) {
            feeItemDomain.setPublished(0);
        }
        this.save(feeItemDomain);
        saveScopeRelations(feeItemDomain.getId(), domain.getScopeType(), domain.getScopeIds());
    }

    @Override
    @Transactional
    public void update(FeeItemDTO domain) {
        FeeItemDomain existing = super.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("收费项目不存在");
        }
        FeeItemDomain feeItemDomain = new FeeItemDomain();
        BeanUtils.copyProperties(domain, feeItemDomain);
        if (domain.getItemType() != null) {
            feeItemDomain.setItemType(FeeItemType.of(domain.getItemType()));
        }
        if (domain.getCycleType() != null) {
            feeItemDomain.setCycleType(FeeCycleType.of(domain.getCycleType()));
        }
        if (domain.getStatus() != null) {
            feeItemDomain.setStatus(EnableStatus.of(domain.getStatus()));
        }
        if (feeItemDomain.getScopeType() == null) {
            feeItemDomain.setScopeType(existing.getScopeType());
        }
        feeItemDomain.setPublished(existing.getPublished());
        this.updateById(feeItemDomain);
        if (domain.getScopeType() != null) {
            saveScopeRelations(domain.getId(), domain.getScopeType(), domain.getScopeIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FeeItemDomain item = super.getById(id);
        if (item == null) {
            throw new BusinessException("收费项目不存在");
        }
        if (item.getStatus() != EnableStatus.DISABLED) {
            throw new BusinessException("只有停用状态的收费项目才能删除");
        }
        feeItemScopeMapper.delete(new LambdaQueryWrapper<FeeItemScopeDomain>().eq(FeeItemScopeDomain::getItemId, id));
        this.removeById(id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        FeeItemDomain item = super.getById(id);
        if (item == null) {
            throw new BusinessException("收费项目不存在");
        }
        if (item.getPublished() != null && item.getPublished() == 1) {
            throw new BusinessException("项目已发布，不能重复发布");
        }
        if (item.getUnitPrice() == null) {
            throw new BusinessException("请先设置单价");
        }
        List<Long> houseIds = resolveScopeHouses(item);
        if (houseIds.isEmpty()) {
            throw new BusinessException("收费范围内没有可生成账单的房屋");
        }
        int count = 0;
        for (Long houseId : houseIds) {
            CommunityHouseDomain house = communityHouseMapper.selectById(houseId);
            if (house == null || house.getOwnerId() == null) {
                continue;
            }
            BigDecimal amount = house.getArea() != null
                    ? item.getUnitPrice().multiply(house.getArea()).setScale(2, java.math.RoundingMode.HALF_UP)
                    : item.getUnitPrice();
            FeeRecordDomain record = new FeeRecordDomain();
            record.setFeeNo("FEE" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
            record.setOwnerId(house.getOwnerId());
            record.setHouseId(house.getId());
            record.setItemId(item.getId());
            record.setAmount(amount);
            record.setPaidAmount(BigDecimal.ZERO);
            record.setStatus(FeeRecordStatus.UNPAID);
            feeRecordMapper.insert(record);
            count++;
        }
        if (count == 0) {
            throw new BusinessException("范围内房屋均未关联业主，无法生成账单");
        }
        // 同步生成收费通知
        FeeNoticeDomain notice = new FeeNoticeDomain();
        notice.setNoticeTitle(item.getItemName() + "缴费通知");
        notice.setNoticeContent("【" + item.getItemName() + "】单价 " + item.getUnitPrice()
                + (item.getUnit() != null ? " " + item.getUnit() : "")
                + "，本次已生成 " + count + " 条待缴费账单，请相关业主及时缴纳。");
        notice.setNoticeType(1);
        notice.setSendScope(noticeScopeOf(item.getScopeType()));
        notice.setSendStatus(1);
        notice.setSendTime(java.time.LocalDateTime.now());
        notice.setItemId(item.getId());
        notice.setCreatorId(com.lsy.propertymanagementsystem.common.utils.SecurityUtils.getCurrentUserId());
        feeNoticeMapper.insert(notice);
        messagePushService.broadcast("fee", "收费通知",
                item.getItemName() + " 已发布，请相关业主及时缴纳（共 " + count + " 条待缴费账单）", item.getId());
        item.setPublished(1);
        this.updateById(item);
    }

    private Integer noticeScopeOf(Integer scopeType) {
        if (scopeType == null) {
            return 1;
        }
        switch (scopeType) {
            case 2: return 2;   // 楼栋
            case 3: return 3;   // 房屋
            case 4: return 3;   // 业主
            default: return 1;  // 全体
        }
    }

    private List<Long> resolveScopeHouses(FeeItemDomain item) {
        Integer scopeType = item.getScopeType() == null ? 1 : item.getScopeType();
        if (scopeType == 1) {
            return communityHouseMapper.selectList(new LambdaQueryWrapper<CommunityHouseDomain>()
                            .isNotNull(CommunityHouseDomain::getOwnerId))
                    .stream().map(CommunityHouseDomain::getId).collect(Collectors.toList());
        }
        List<FeeItemScopeDomain> scopes = feeItemScopeMapper.selectList(new LambdaQueryWrapper<FeeItemScopeDomain>()
                .eq(FeeItemScopeDomain::getItemId, item.getId()));
        if (scopes.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> targetIds = scopes.stream().map(FeeItemScopeDomain::getTargetId).distinct().collect(Collectors.toList());
        if (scopeType == 3) {
            return targetIds;
        }
        if (scopeType == 2) {
            return communityHouseMapper.selectList(new LambdaQueryWrapper<CommunityHouseDomain>()
                            .in(CommunityHouseDomain::getBuildingId, targetIds))
                    .stream().map(CommunityHouseDomain::getId).collect(Collectors.toList());
        }
        if (scopeType == 4) {
            return communityHouseMapper.selectList(new LambdaQueryWrapper<CommunityHouseDomain>()
                            .in(CommunityHouseDomain::getOwnerId, targetIds))
                    .stream().map(CommunityHouseDomain::getId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void saveScopeRelations(Long itemId, Integer scopeType, List<Long> scopeIds) {
        feeItemScopeMapper.delete(new LambdaQueryWrapper<FeeItemScopeDomain>().eq(FeeItemScopeDomain::getItemId, itemId));
        if (scopeType == null || scopeType == 1 || scopeIds == null) {
            return;
        }
        for (Long targetId : scopeIds) {
            FeeItemScopeDomain scope = new FeeItemScopeDomain();
            scope.setItemId(itemId);
            scope.setScopeType(scopeType);
            scope.setTargetId(targetId);
            feeItemScopeMapper.insert(scope);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        FeeItemDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("收费项目不存在");
        }
        domain.changeStatus(EnableStatus.of(status));
        this.updateById(domain);
    }

    private FeeItemVO convertToVO(FeeItemDomain domain) {
        if (domain == null) {
            return null;
        }
        FeeItemVO vo = new FeeItemVO();
        BeanUtils.copyProperties(domain, vo);
        vo.setScopeIds(feeItemScopeMapper.selectList(new LambdaQueryWrapper<FeeItemScopeDomain>()
                        .eq(FeeItemScopeDomain::getItemId, domain.getId()))
                .stream().map(FeeItemScopeDomain::getTargetId).collect(Collectors.toList()));
        return vo;
    }
}
