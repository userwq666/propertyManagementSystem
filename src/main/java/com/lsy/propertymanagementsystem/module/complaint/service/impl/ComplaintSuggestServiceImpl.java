package com.lsy.propertymanagementsystem.module.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestVO;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintType;
import com.lsy.propertymanagementsystem.module.complaint.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.module.complaint.service.ComplaintSuggestService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ComplaintSuggestServiceImpl extends ServiceImpl<ComplaintSuggestMapper, ComplaintSuggestDomain> implements ComplaintSuggestService {

    @Autowired
    private CommunityOwnerMapper communityOwnerMapper;

    @Autowired
    private CommunityHouseMapper communityHouseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    // 分页查询投诉建议
    @Override
    public Page<ComplaintSuggestVO> page(int pageNum, int pageSize, Long ownerId, String type, Integer status) {
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
        Page<ComplaintSuggestDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);
        List<ComplaintSuggestVO> voList = convertToVO(domainPage.getRecords());
        Page<ComplaintSuggestVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private List<ComplaintSuggestVO> convertToVO(List<ComplaintSuggestDomain> domainList) {
        if (domainList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ownerIds = domainList.stream().map(ComplaintSuggestDomain::getOwnerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> houseIds = domainList.stream().map(ComplaintSuggestDomain::getHouseId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> handlerIds = domainList.stream().map(ComplaintSuggestDomain::getHandlerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, String> ownerNameMap = ownerIds.isEmpty() ? Collections.emptyMap()
                : communityOwnerMapper.selectBatchIds(ownerIds).stream()
                        .collect(Collectors.toMap(CommunityOwnerDomain::getId, CommunityOwnerDomain::getName));

        Map<Long, String> houseRoomMap = houseIds.isEmpty() ? Collections.emptyMap()
                : communityHouseMapper.selectBatchIds(houseIds).stream()
                        .collect(Collectors.toMap(CommunityHouseDomain::getId, CommunityHouseDomain::getRoomNo));

        Map<Long, String> handlerNameMap = handlerIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(handlerIds).stream()
                        .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));

        return domainList.stream().map(domain -> {
            ComplaintSuggestVO vo = new ComplaintSuggestVO();
            BeanUtils.copyProperties(domain, vo);
            vo.setOwnerName(ownerNameMap.get(domain.getOwnerId()));
            vo.setRoomNo(houseRoomMap.get(domain.getHouseId()));
            vo.setHandlerName(handlerNameMap.get(domain.getHandlerId()));
            return vo;
        }).collect(Collectors.toList());
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