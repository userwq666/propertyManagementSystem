package com.lsy.propertymanagementsystem.module.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityHouseDomain;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentMaintenanceDomain;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceStatus;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceType;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMaintenanceMapper;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecordDomain> implements RepairRecordService {

    @Autowired
    private CommunityOwnerMapper communityOwnerMapper;

    @Autowired
    private CommunityHouseMapper communityHouseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentMaintenanceMapper equipmentMaintenanceMapper;

    @Override
    public RepairRecordVO getById(Long id) {
        RepairRecordDomain domain = super.getById(id);
        if (domain == null) {
            return null;
        }
        return batchConvertToVO(Collections.singletonList(domain)).get(0);
    }

    @Override
    public Page<RepairRecordVO> page(int pageNum, int pageSize, Long ownerId, Long handlerId, Long equipmentId, Integer status) {
        LambdaQueryWrapper<RepairRecordDomain> wrapper = new LambdaQueryWrapper<>();
        String roleKey = SecurityUtils.getRoleKey();
        if (SecurityUtils.isOwner()) {
            Long currentOwnerId = getOwnerIdByUserId(SecurityUtils.getCurrentUserId());
            if (currentOwnerId == null) {
                Page<RepairRecordVO> empty = new Page<>(pageNum, pageSize, 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.eq(RepairRecordDomain::getOwnerId, currentOwnerId);
        } else if ("repair_worker".equals(roleKey)) {
            Long userId = SecurityUtils.getCurrentUserId();
            wrapper.and(w -> w.eq(RepairRecordDomain::getHandlerId, userId)
                    .or().eq(RepairRecordDomain::getStatus, RepairStatus.PENDING));
        } else {
            if (ownerId != null) {
                if (ownerId == 0L) {
                    wrapper.isNull(RepairRecordDomain::getOwnerId);
                } else {
                    wrapper.eq(RepairRecordDomain::getOwnerId, ownerId);
                }
            }
            if (handlerId != null) {
                wrapper.eq(RepairRecordDomain::getHandlerId, handlerId);
            }
            if (equipmentId != null) {
                wrapper.eq(RepairRecordDomain::getEquipmentId, equipmentId);
            }
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
    public Long addRepair(RepairRecordDTO dto) {
        RepairRecordDomain domain = new RepairRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getRepairType() != null) {
            domain.setRepairType(RepairType.of(dto.getRepairType()));
        }
        if (SecurityUtils.isOwner()) {
            Long currentOwnerId = getOwnerIdByUserId(SecurityUtils.getCurrentUserId());
            if (currentOwnerId == null) {
                throw new BusinessException("未找到业主档案，无法报修");
            }
            domain.setOwnerId(currentOwnerId);
            if (dto.getHouseId() == null) {
                throw new BusinessException("请选择报修房屋");
            }
            CommunityHouseDomain house = communityHouseMapper.selectById(dto.getHouseId());
            if (house == null || !Objects.equals(house.getOwnerId(), currentOwnerId)) {
                throw new BusinessException("只能选择自己名下房屋报修");
            }
        }
        domain.setRepairNo("BX" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        domain.prepareAdd();
        domain.setCreatorId(SecurityUtils.getCurrentUserId());
        this.save(domain);
        if (domain.getEquipmentId() != null) {
            markEquipmentFault(domain.getEquipmentId());
        }
        return domain.getId();
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
        if (existing.getStatus() != RepairStatus.PENDING && existing.getStatus() != RepairStatus.PROCESSING) {
            throw new BusinessException("当前状态不允许编辑");
        }
        if (SecurityUtils.isOwner()
                && !Objects.equals(existing.getOwnerId(), getOwnerIdByUserId(SecurityUtils.getCurrentUserId()))) {
            throw new BusinessException("无权编辑该报修");
        }
        if (dto.getRepairType() != null) {
            existing.setRepairType(RepairType.of(dto.getRepairType()));
        }
        existing.setOwnerId(dto.getOwnerId());
        existing.setHouseId(dto.getHouseId());
        existing.setRepairContent(domain.getRepairContent());
        existing.setRepairImages(domain.getRepairImages());
        this.updateById(existing);
        if (existing.getEquipmentId() != null) {
            markEquipmentFault(existing.getEquipmentId());
        }
    }

    @Override
    @Transactional
    public void deleteRepair(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, Long handlerId, Long equipmentId, String handleContent) {
        RepairRecordDomain domain = super.getById(id);
        if (domain == null) {
            throw new BusinessException("报修记录不存在");
        }
        RepairStatus newStatus = RepairStatus.of(status);
        Long userId = SecurityUtils.getCurrentUserId();
        String roleKey = SecurityUtils.getRoleKey();
        boolean isAdmin = "admin".equals(roleKey) || "property_admin".equals(roleKey);
        boolean isOwner = SecurityUtils.isOwner();
        boolean isWorker = "repair_worker".equals(roleKey);

        switch (newStatus) {
            case PROCESSING -> {
                // 管理员派单或维修工接单：仅待派单可进入，条件更新防并发抢单
                if (domain.getStatus() != RepairStatus.PENDING) {
                    throw new BusinessException("只有待派单的报修才能派单或接单");
                }
                Long targetHandler;
                if (isAdmin) {
                    if (handlerId == null) {
                        throw new BusinessException("请选择维修工");
                    }
                    targetHandler = handlerId;
                } else if (isWorker) {
                    targetHandler = userId;
                } else {
                    throw new BusinessException("无权派单或接单");
                }
                int updated = baseMapper.update(null, new LambdaUpdateWrapper<RepairRecordDomain>()
                        .eq(RepairRecordDomain::getId, id)
                        .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING)
                        .isNull(RepairRecordDomain::getHandlerId)
                        .set(RepairRecordDomain::getHandlerId, targetHandler)
                        .set(RepairRecordDomain::getStatus, RepairStatus.PROCESSING));
                if (updated != 1) {
                    throw new BusinessException("该报修已被其他人处理");
                }
                if (domain.getEquipmentId() != null) {
                    markEquipmentRepair(domain.getEquipmentId());
                }
            }
            case PENDING_EVALUATE -> {
                // 维修工结单：只能完成自己负责的单；管理员可代完成
                if (!isAdmin && (!isWorker || !Objects.equals(domain.getHandlerId(), userId))) {
                    throw new BusinessException("只能完成自己负责的报修");
                }
                if (domain.getStatus() != RepairStatus.PROCESSING) {
                    throw new BusinessException("只有处理中的报修才能结单");
                }
                if (equipmentId != null && domain.getEquipmentId() == null) {
                    throw new BusinessException("该报修未关联设备，结单时不能选择设备");
                }
                domain.complete(handleContent, null);
                Long linkedEquipmentId = equipmentId != null ? equipmentId : domain.getEquipmentId();
                if (linkedEquipmentId != null) {
                    domain.setEquipmentId(linkedEquipmentId);
                }
                this.updateById(domain);
                if (linkedEquipmentId != null) {
                    linkEquipmentAfterComplete(domain, linkedEquipmentId, userId);
                }
            }
            case CANCELLED -> {
                boolean canCancel = isAdmin
                        || (isOwner && Objects.equals(domain.getOwnerId(), getOwnerIdByUserId(userId)));
                if (!canCancel) {
                    throw new BusinessException("无权取消该报修");
                }
                if (domain.getStatus() == RepairStatus.PENDING_EVALUATE
                        || domain.getStatus() == RepairStatus.COMPLETED) {
                    throw new BusinessException("当前状态不允许取消");
                }
                domain.cancel();
                this.updateById(domain);
            }
            default -> throw new BusinessException("不支持的状态变更");
        }
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
        if (domain.getStatus() != RepairStatus.PENDING_EVALUATE) {
            throw new BusinessException("只有待确认的报修才能评价，确认后不可补评");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = "admin".equals(SecurityUtils.getRoleKey())
                || "property_admin".equals(SecurityUtils.getRoleKey());
        boolean ownerOk = SecurityUtils.isOwner()
                && Objects.equals(domain.getOwnerId(), getOwnerIdByUserId(userId));
        if (!isAdmin && !ownerOk) {
            throw new BusinessException("无权评价该报修");
        }
        domain.evaluate(score, content);
        this.updateById(domain);
    }

    @Override
    @Transactional
    public int autoCompleteExpired() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        return baseMapper.update(null, new LambdaUpdateWrapper<RepairRecordDomain>()
                .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING_EVALUATE)
                .isNull(RepairRecordDomain::getEvaluateTime)
                .lt(RepairRecordDomain::getHandleTime, deadline)
                .set(RepairRecordDomain::getStatus, RepairStatus.COMPLETED));
    }

    @Override
    public List<CommunityHouseDomain> listHouses(Long ownerId) {
        LambdaQueryWrapper<CommunityHouseDomain> wrapper = new LambdaQueryWrapper<>();
        if (SecurityUtils.isOwner()) {
            Long currentOwnerId = getOwnerIdByUserId(SecurityUtils.getCurrentUserId());
            if (currentOwnerId == null) {
                return Collections.emptyList();
            }
            wrapper.eq(CommunityHouseDomain::getOwnerId, currentOwnerId);
        } else if (ownerId != null) {
            wrapper.eq(CommunityHouseDomain::getOwnerId, ownerId);
        }
        wrapper.orderByAsc(CommunityHouseDomain::getId);
        return communityHouseMapper.selectList(wrapper);
    }

    @Override
    public List<EquipmentDomain> listEquipments() {
        return equipmentMapper.selectList(new LambdaQueryWrapper<EquipmentDomain>()
                .orderByAsc(EquipmentDomain::getId));
    }

    private void linkEquipmentAfterComplete(RepairRecordDomain domain, Long equipmentId, Long userId) {
        EquipmentDomain equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return;
        }
        EquipmentMaintenanceDomain maintenance = new EquipmentMaintenanceDomain();
        maintenance.setEquipmentId(equipmentId);
        maintenance.setMaintenanceType(MaintenanceType.FAULT_REPAIR);
        maintenance.setMaintenanceContent(domain.getRepairContent());
        maintenance.setMaintenancePersonnelId(userId);
        maintenance.setStartTime(domain.getHandleTime());
        maintenance.setEndTime(domain.getHandleTime());
        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        maintenance.setRemark("报修单号：" + domain.getRepairNo());
        equipmentMaintenanceMapper.insert(maintenance);

        if (equipment.getStatus() == EquipmentStatus.FAULT
                || equipment.getStatus() == EquipmentStatus.UNDER_REPAIR
                || equipment.getStatus() == EquipmentStatus.DISABLED) {
            equipment.changeStatus(EquipmentStatus.NORMAL);
            equipmentMapper.updateById(equipment);
        }
    }

    private void markEquipmentFault(Long equipmentId) {
        EquipmentDomain equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return;
        }
        EquipmentStatus status = equipment.getStatus();
        if (status == EquipmentStatus.NORMAL || status == EquipmentStatus.DISABLED) {
            equipment.changeStatus(EquipmentStatus.FAULT);
            equipmentMapper.updateById(equipment);
        }
    }

    private void markEquipmentRepair(Long equipmentId) {
        EquipmentDomain equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return;
        }
        EquipmentStatus status = equipment.getStatus();
        if (status == EquipmentStatus.FAULT || status == EquipmentStatus.NORMAL) {
            equipment.changeStatus(EquipmentStatus.UNDER_REPAIR);
            equipmentMapper.updateById(equipment);
        }
    }

    private List<RepairRecordVO> batchConvertToVO(List<RepairRecordDomain> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> ownerIds = records.stream().map(RepairRecordDomain::getOwnerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> houseIds = records.stream().map(RepairRecordDomain::getHouseId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> handlerIds = records.stream().map(RepairRecordDomain::getHandlerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> equipmentIds = records.stream().map(RepairRecordDomain::getEquipmentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> creatorIds = records.stream().map(RepairRecordDomain::getCreatorId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, CommunityOwnerDomain> ownerMap = !ownerIds.isEmpty()
                ? communityOwnerMapper.selectBatchIds(ownerIds).stream()
                    .collect(Collectors.toMap(CommunityOwnerDomain::getId, o -> o))
                : new HashMap<>();
        Map<Long, String> roomNoMap = !houseIds.isEmpty()
                ? communityHouseMapper.selectBatchIds(houseIds).stream()
                    .collect(Collectors.toMap(CommunityHouseDomain::getId, CommunityHouseDomain::getRoomNo))
                : new HashMap<>();
        Map<Long, String> handlerNameMap = !handlerIds.isEmpty()
                ? sysUserMapper.selectBatchIds(handlerIds).stream()
                    .collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName))
                : new HashMap<>();
        Map<Long, SysUserDomain> creatorMap = !creatorIds.isEmpty()
                ? sysUserMapper.selectBatchIds(creatorIds).stream()
                    .collect(Collectors.toMap(SysUserDomain::getId, u -> u))
                : new HashMap<>();
        Map<Long, String> equipmentNameMap = !equipmentIds.isEmpty()
                ? equipmentMapper.selectBatchIds(equipmentIds).stream()
                    .collect(Collectors.toMap(EquipmentDomain::getId, EquipmentDomain::getEquipmentName))
                : new HashMap<>();

        return records.stream().map(domain -> {
            RepairRecordVO vo = new RepairRecordVO();
            BeanUtils.copyProperties(domain, vo);
            if (domain.getOwnerId() != null) {
        CommunityOwnerDomain owner = ownerMap.get(domain.getOwnerId());
        if (owner != null) {
            vo.setOwnerName(owner.getName());
            vo.setOwnerPhone(owner.getPhone());
        }
            }
            if (domain.getHouseId() != null) {
                vo.setRoomNo(roomNoMap.get(domain.getHouseId()));
            }
            if (domain.getHandlerId() != null) {
                vo.setHandlerName(handlerNameMap.get(domain.getHandlerId()));
            }
            SysUserDomain creator = creatorMap.get(domain.getCreatorId());
            if (creator != null) {
                vo.setCreatorName(creator.getRealName());
                vo.setCreatorPhone(creator.getPhone());
            }
            if (domain.getEquipmentId() != null) {
                vo.setEquipmentName(equipmentNameMap.get(domain.getEquipmentId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private Long getOwnerIdByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return communityOwnerMapper.selectList(new LambdaQueryWrapper<CommunityOwnerDomain>()
                        .eq(CommunityOwnerDomain::getUserId, userId)
                        .last("LIMIT 1"))
                .stream()
                .map(CommunityOwnerDomain::getId)
                .findFirst()
                .orElse(null);
    }
}
