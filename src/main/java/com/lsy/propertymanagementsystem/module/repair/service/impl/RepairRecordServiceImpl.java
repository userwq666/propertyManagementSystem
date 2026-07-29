package com.lsy.propertymanagementsystem.module.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordVO;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairPriority;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairType;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecordDomain> implements RepairRecordService {

    @Autowired
    private CommunityOwnerMapper communityOwnerMapper;

    @Autowired
    private CommunityHouseMapper communityHouseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public RepairRecordVO getById(Long id) {
        RepairRecordDomain domain = super.getById(id);
        if (domain == null) {
            return null;
        }
        return batchConvertToVO(Collections.singletonList(domain)).get(0);
    }

    @Override
    public Page<RepairRecordVO> page(int pageNum, int pageSize, Long ownerId, Integer status) {
        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(RepairRecordDomain::getOwnerId, SecurityUtils.isOwner() ? SecurityUtils.getCurrentUserId() : ownerId);
        }
        if (status != null) {
            wrapper.eq(RepairRecordDomain::getStatus, RepairStatus.of(status));
        }
        wrapper.orderByDesc(RepairRecordDomain::getCreateTime);
        Page<RepairRecordDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        List<RepairRecordVO> voList = batchConvertToVO(domainPage.getRecords());
        Page<RepairRecordVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void addRepair(RepairRecordDTO dto) {
        RepairRecordDomain domain = new RepairRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getRepairType() != null) {
            domain.setRepairType(RepairType.of(dto.getRepairType()));
        }
        domain.prepareAdd();
        this.save(domain);
    }

    @Override
    @Transactional
    public void updateRepair(RepairRecordDTO dto) {
        RepairRecordDomain domain = new RepairRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        RepairRecordDomain existing = super.getById(domain.getId());
        if (existing == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (dto.getRepairType() != null) {
            existing.setRepairType(RepairType.of(dto.getRepairType()));
        }
        existing.setRepairContent(domain.getRepairContent());
        existing.setRepairImages(domain.getRepairImages());
        this.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteRepair(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, Long handlerId, String handleContent) {
        RepairRecordDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("报修记录不存在");
        }
        RepairStatus newStatus = RepairStatus.of(status);
        if (newStatus == RepairStatus.PROCESSING) {
            domain.assignHandler(handlerId);
        } else if (newStatus == RepairStatus.COMPLETED) {
            domain.complete(handleContent, null);
        } else {
            domain.setStatus(newStatus);
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer score, String content) {
        if (score < 1 || score > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        RepairRecordDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (domain.getStatus() != RepairStatus.COMPLETED) {
            throw new BusinessException("只有已完成的报修才能评价");
        }
        domain.evaluate(score, content);
        this.updateById(domain);
    }

    @Override
    public long countByHouseId(Long houseId) {
        return this.count(new LambdaQueryWrapper<RepairRecordDomain>().eq(RepairRecordDomain::getHouseId, houseId));
    }

    private List<RepairRecordVO> batchConvertToVO(List<RepairRecordDomain> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> ownerIds = records.stream().map(RepairRecordDomain::getOwnerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> houseIds = records.stream().map(RepairRecordDomain::getHouseId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> handlerIds = records.stream().map(RepairRecordDomain::getHandlerId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> ownerNameMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            List<CommunityOwnerDomain> owners = communityOwnerMapper.selectBatchIds(ownerIds);
            ownerNameMap = owners.stream().collect(Collectors.toMap(CommunityOwnerDomain::getId, CommunityOwnerDomain::getName));
        }
        Map<Long, String> roomNoMap = new HashMap<>();
        if (!houseIds.isEmpty()) {
            List<CommunityHouseDomain> houses = communityHouseMapper.selectBatchIds(houseIds);
            roomNoMap = houses.stream().collect(Collectors.toMap(CommunityHouseDomain::getId, CommunityHouseDomain::getRoomNo));
        }
        Map<Long, String> handlerNameMap = new HashMap<>();
        if (!handlerIds.isEmpty()) {
            List<SysUserDomain> users = sysUserMapper.selectBatchIds(handlerIds);
            handlerNameMap = users.stream().collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));
        }

        return records.stream().map(domain -> {
            RepairRecordVO vo = new RepairRecordVO();
            BeanUtils.copyProperties(domain, vo);
            if (domain.getOwnerId() != null) {
                vo.setOwnerName(ownerNameMap.get(domain.getOwnerId()));
            }
            if (domain.getHouseId() != null) {
                vo.setRoomNo(roomNoMap.get(domain.getHouseId()));
            }
            if (domain.getHandlerId() != null) {
                vo.setHandlerName(handlerNameMap.get(domain.getHandlerId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
