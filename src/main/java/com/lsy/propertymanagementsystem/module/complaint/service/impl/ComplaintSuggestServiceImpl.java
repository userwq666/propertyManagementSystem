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
import com.lsy.propertymanagementsystem.websocket.MessagePushService;
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

    @Autowired
    private MessagePushService messagePushService;

    // 分页查询投诉建议
    @Override
    public Page<ComplaintSuggestVO> page(int pageNum, int pageSize, Long ownerId, String type, Integer status) {
        LambdaQueryWrapper<ComplaintSuggestDomain> wrapper = new LambdaQueryWrapper<>();
        if (!SecurityUtils.hasPermission("complaint:list:edit")) {
            // 非管理员只能查看自己提交的投诉
            wrapper.eq(ComplaintSuggestDomain::getCreatorId, SecurityUtils.getCurrentUserId());
        } else if (ownerId != null) {
            wrapper.eq(ComplaintSuggestDomain::getOwnerId, ownerId);
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
        List<Long> creatorIds = domainList.stream().map(ComplaintSuggestDomain::getCreatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, String> ownerNameMap = ownerIds.isEmpty() ? Collections.emptyMap()
                : communityOwnerMapper.selectBatchIds(ownerIds).stream()
                        .collect(Collectors.toMap(CommunityOwnerDomain::getId, CommunityOwnerDomain::getName));

        Map<Long, String> houseRoomMap = houseIds.isEmpty() ? Collections.emptyMap()
                : communityHouseMapper.selectBatchIds(houseIds).stream()
                        .collect(Collectors.toMap(CommunityHouseDomain::getId, CommunityHouseDomain::getRoomNo));

        Map<Long, String> handlerNameMap = handlerIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(handlerIds).stream()
                        .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));
        Map<Long, String> creatorNameMap = creatorIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(creatorIds).stream()
                        .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));

        return domainList.stream().map(domain -> {
            ComplaintSuggestVO vo = new ComplaintSuggestVO();
            BeanUtils.copyProperties(domain, vo);
            vo.setOwnerName(ownerNameMap.get(domain.getOwnerId()));
            vo.setRoomNo(houseRoomMap.get(domain.getHouseId()));
            vo.setHandlerName(handlerNameMap.get(domain.getHandlerId()));
            vo.setCreatorName(creatorNameMap.get(domain.getCreatorId()));
            return vo;
        }).collect(Collectors.toList());
    }

    // 新增投诉建议
    @Override
    @Transactional
    public void add(ComplaintSuggestDTO dto) {
        if (SecurityUtils.isOwner()) {
            // 业主提交投诉强制归属自己
            dto.setOwnerId(getCurrentOwnerId());
        }
        ComplaintSuggestDomain domain = new ComplaintSuggestDomain();
        BeanUtils.copyProperties(dto, domain);
        domain.setCreatorId(SecurityUtils.getCurrentUserId());
        domain.setComplaintNo("TS" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        domain.prepareAdd();
        this.save(domain);
    }

    // 更新投诉建议
    @Override
    @Transactional
    public void update(ComplaintSuggestDTO dto) {
        ComplaintSuggestDomain existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("投诉建议不存在");
        }
        if (existing.getStatus() != ComplaintStatus.PENDING) {
            throw new BusinessException("仅待受理状态可编辑");
        }
        if (!Objects.equals(existing.getCreatorId(), SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能编辑自己的投诉");
        }
        ComplaintSuggestDomain domain = new ComplaintSuggestDomain();
        BeanUtils.copyProperties(dto, domain);
        existing.setType(domain.getType());
        existing.setCategory(domain.getCategory());
        existing.setContent(domain.getContent());
        existing.setImages(domain.getImages());
        existing.setIsAnonymous(domain.getIsAnonymous());
        this.updateById(existing);
    }

    @Override
    public ComplaintSuggestVO getDetail(Long id) {
        ComplaintSuggestDomain domain = this.getById(id);
        if (domain == null) {
            return null;
        }
        return convertToVO(Collections.singletonList(domain)).get(0);
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
        boolean isManager = SecurityUtils.hasPermission("complaint:list:edit");
        if (newStatus != ComplaintStatus.CANCELLED && newStatus != ComplaintStatus.COMPLETED && !isManager) {
            throw new BusinessException("无权执行该操作");
        }
        if (newStatus == ComplaintStatus.CANCELLED) {
            // 业主撤销或管理员撤销：仅待受理/已受理状态
            if (domain.getStatus() != ComplaintStatus.PENDING && domain.getStatus() != ComplaintStatus.ACCEPTED) {
                throw new BusinessException("当前状态不可撤销");
            }
            if (!Objects.equals(domain.getCreatorId(), SecurityUtils.getCurrentUserId())) {
                throw new BusinessException("只能撤销自己的投诉");
            }
            domain.setStatus(ComplaintStatus.CANCELLED);
        } else if (newStatus == ComplaintStatus.COMPLETED) {
            // 投诉人确认完成
            if (domain.getStatus() != ComplaintStatus.REPLIED) {
                throw new BusinessException("仅已回复状态可确认完成");
            }
            if (!isManager && !Objects.equals(domain.getCreatorId(), SecurityUtils.getCurrentUserId())) {
                throw new BusinessException("只能确认自己的投诉");
            }
            domain.confirm();
        } else if (newStatus == ComplaintStatus.ACCEPTED) {
            if (domain.getStatus() != ComplaintStatus.PENDING) {
                throw new BusinessException("仅待受理状态可受理");
            }
            if (handlerId == null) {
                throw new BusinessException("受理必须指定处理人");
            }
            domain.assignHandler(handlerId);
        } else if (newStatus == ComplaintStatus.PROCESSING) {
            if (domain.getStatus() != ComplaintStatus.ACCEPTED) {
                throw new BusinessException("仅已受理状态可开始处理");
            }
            domain.setStatus(ComplaintStatus.PROCESSING);
        } else if (newStatus == ComplaintStatus.REPLIED) {
            if (domain.getStatus() != ComplaintStatus.PROCESSING && domain.getStatus() != ComplaintStatus.ACCEPTED) {
                throw new BusinessException("当前状态不可回复");
            }
            if (handleContent == null || handleContent.isBlank()) {
                throw new BusinessException("回复必须填写处理内容");
            }
            domain.reply(handleContent);
            messagePushService.pushToUser(domain.getCreatorId(), "complaint", "投诉已回复",
                    "您的投诉「" + (domain.getCategory() != null ? domain.getCategory() : "投诉") + "」已回复，请确认", domain.getId());
        } else {
            throw new BusinessException("不支持的状态流转");
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void evaluate(Long id, Integer score, String content) {
        ComplaintSuggestDomain domain = this.getById(id);
        if (domain == null) {
            throw new BusinessException("投诉建议不存在");
        }
        if (domain.getStatus() != ComplaintStatus.REPLIED) {
            throw new BusinessException("仅已回复状态可评价");
        }
        if (!Objects.equals(domain.getCreatorId(), SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能评价自己的投诉");
        }
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        domain.evaluate(score, content);
        this.updateById(domain);
    }

    private Long getCurrentOwnerId() {
        CommunityOwnerDomain owner = communityOwnerMapper.selectOne(
                new LambdaQueryWrapper<CommunityOwnerDomain>()
                        .eq(CommunityOwnerDomain::getUserId, SecurityUtils.getCurrentUserId()));
        return owner != null ? owner.getId() : -1L;
    }
}
