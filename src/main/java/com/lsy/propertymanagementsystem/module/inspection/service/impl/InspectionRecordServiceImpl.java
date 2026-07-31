package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordDTO;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionRecordVO;
import com.lsy.propertymanagementsystem.module.inspection.enums.HandleStatus;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.enums.TaskStatus;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import com.lsy.propertymanagementsystem.module.repair.dto.RepairRecordDTO;
import com.lsy.propertymanagementsystem.module.repair.service.RepairRecordService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Autowired
    private InspectionPlanMapper inspectionPlanMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RepairRecordService repairRecordService;

    @Override
    public Page<InspectionRecordVO> page(int pageNum, int pageSize, Long planId, Long equipmentId) {
        LambdaQueryWrapper<InspectionRecordDomain> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(InspectionRecordDomain::getPlanId, planId);
        }
        if (equipmentId != null) {
            wrapper.eq(InspectionRecordDomain::getEquipmentId, equipmentId);
        }
        wrapper.orderByDesc(InspectionRecordDomain::getCreateTime);
        Page<InspectionRecordDomain> domainPage = inspectionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<InspectionRecordDomain> records = domainPage.getRecords();
        Page<InspectionRecordVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        if (records.isEmpty()) {
            return voPage;
        }

        Set<Long> planIds = records.stream().map(InspectionRecordDomain::getPlanId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> planNameMap = new HashMap<>();
        if (!planIds.isEmpty()) {
            List<InspectionPlanDomain> plans = inspectionPlanMapper.selectBatchIds(planIds);
            for (InspectionPlanDomain plan : plans) {
                planNameMap.put(plan.getId(), plan.getPlanName());
            }
        }

        Set<Long> eqIds = records.stream().map(InspectionRecordDomain::getEquipmentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> eqNameMap = new HashMap<>();
        if (!eqIds.isEmpty()) {
            List<EquipmentDomain> equipments = equipmentMapper.selectBatchIds(eqIds);
            for (EquipmentDomain eq : equipments) {
                eqNameMap.put(eq.getId(), eq.getEquipmentName());
            }
        }

        Set<Long> userIds = new HashSet<>();
        for (InspectionRecordDomain r : records) {
            if (r.getInspectorUserId() != null) userIds.add(r.getInspectorUserId());
            if (r.getHandlerId() != null) userIds.add(r.getHandlerId());
            if (r.getFillerUserId() != null) userIds.add(r.getFillerUserId());
        }
        Map<Long, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUserDomain> users = sysUserMapper.selectBatchIds(userIds);
            for (SysUserDomain user : users) {
                userNameMap.put(user.getId(), user.getRealName());
            }
        }

        List<InspectionRecordVO> voList = records.stream().map(record -> {
            InspectionRecordVO vo = new InspectionRecordVO();
            BeanUtils.copyProperties(record, vo);
            vo.setPlanName(planNameMap.get(record.getPlanId()));
            vo.setEquipmentName(eqNameMap.get(record.getEquipmentId()));
            vo.setInspectorName(userNameMap.get(record.getInspectorUserId()));
            vo.setFillerName(userNameMap.get(record.getFillerUserId()));
            vo.setHandlerName(userNameMap.get(record.getHandlerId()));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void addRecord(InspectionRecordDTO dto) {
        if ("inspector".equals(SecurityUtils.getRoleKey())) {
            dto.setInspectorUserId(SecurityUtils.getCurrentUserId());
        }
        InspectionRecordDomain domain = new InspectionRecordDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getStatus() != null) {
            domain.setStatus(InspectResult.of(dto.getStatus()));
        }
        if (domain.getInspectionTime() == null) {
            domain.setInspectionTime(LocalDateTime.now());
        }
        domain.setFillerUserId(SecurityUtils.getCurrentUserId());
        inspectionRecordMapper.insert(domain);
    }

    @Override
    @Transactional
    public void addRecordDomain(InspectionRecordDomain record) {
        inspectionRecordMapper.insert(record);
    }

    @Override
    public InspectionRecordVO getRecordById(Long id) {
        InspectionRecordDomain record = inspectionRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    @Transactional
    public void updateRecord(InspectionRecordDTO dto) {
        InspectionRecordDomain existing = inspectionRecordMapper.selectById(dto.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检记录不存在");
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String roleKey = SecurityUtils.getRoleKey();
        boolean isManager = "admin".equals(roleKey) || "property_admin".equals(roleKey);
        if (!isManager && !currentUserId.equals(existing.getInspectorUserId())) {
            throw new BusinessException("只能填写自己负责的巡检记录");
        }
        if (dto.getStatus() != null) {
            InspectResult result = InspectResult.of(dto.getStatus());
            existing.setStatus(result);
            existing.setAbnormalDesc(dto.getAbnormalDesc());
            existing.setAbnormalImages(dto.getAbnormalImages());
            existing.setLocationLat(dto.getLocationLat());
            existing.setLocationLng(dto.getLocationLng());
            existing.setLocationAddress(dto.getLocationAddress());
            if (result == InspectResult.ABNORMAL) {
                existing.setHandleStatus(HandleStatus.PENDING);
            }
            existing.setTaskStatus(TaskStatus.DONE);
            if (existing.getFillerUserId() == null) {
                existing.setFillerUserId(currentUserId);
            }
        }
        existing.setRemark(dto.getRemark());
        inspectionRecordMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        inspectionRecordMapper.deleteById(id);
    }

    @Override
    public long countByPlanId(Long planId) {
        return inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>().eq(InspectionRecordDomain::getPlanId, planId));
    }

    @Override
    public long countByPlanAndDate(Long planId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>()
                .eq(InspectionRecordDomain::getPlanId, planId)
                .ge(InspectionRecordDomain::getInspectionTime, start)
                .lt(InspectionRecordDomain::getInspectionTime, end));
    }

    @Override
    @Transactional
    public void acceptRecord(Long id) {
        InspectionRecordDomain record = inspectionRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("巡检记录不存在");
        }
        if (record.getTaskStatus() != TaskStatus.PENDING) {
            throw new BusinessException("该任务当前不可接单");
        }
        if (record.getInspectorUserId() != null) {
            throw new BusinessException("该任务已指定巡检员");
        }
        record.accept(SecurityUtils.getCurrentUserId());
        inspectionRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public Long createRepairForAbnormal(Long id) {
        InspectionRecordDomain record = inspectionRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("巡检记录不存在");
        }
        if (record.getStatus() != InspectResult.ABNORMAL) {
            throw new BusinessException("只有异常巡检记录才能生成报修单");
        }
        if (record.getRepairRecordId() != null) {
            throw new BusinessException("该异常已生成报修单，不能重复报修");
        }
        RepairRecordDTO dto = new RepairRecordDTO();
        dto.setEquipmentId(record.getEquipmentId());
        dto.setRepairType("公共设施");
        dto.setRepairContent(record.getAbnormalDesc() != null ? record.getAbnormalDesc() : "巡检异常");
        dto.setRemark("巡检异常-记录ID:" + id);
        Long repairId = repairRecordService.addRepair(dto);
        record.setRepairRecordId(repairId);
        inspectionRecordMapper.updateById(record);
        return repairId;
    }

    private InspectionRecordVO convertToVO(InspectionRecordDomain record) {
        InspectionRecordVO vo = new InspectionRecordVO();
        BeanUtils.copyProperties(record, vo);

        if (record.getPlanId() != null) {
            InspectionPlanDomain plan = inspectionPlanMapper.selectById(record.getPlanId());
            if (plan != null) {
                vo.setPlanName(plan.getPlanName());
            }
        }

        if (record.getEquipmentId() != null) {
            EquipmentDomain equipment = equipmentMapper.selectById(record.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getEquipmentName());
            }
        }

        if (record.getInspectorUserId() != null) {
            SysUserDomain user = sysUserMapper.selectById(record.getInspectorUserId());
            if (user != null) {
                vo.setInspectorName(user.getRealName());
            }
        }

        if (record.getFillerUserId() != null) {
            SysUserDomain user = sysUserMapper.selectById(record.getFillerUserId());
            if (user != null) {
                vo.setFillerName(user.getRealName());
            }
        }

        if (record.getHandlerId() != null) {
            SysUserDomain user = sysUserMapper.selectById(record.getHandlerId());
            if (user != null) {
                vo.setHandlerName(user.getRealName());
            }
        }

        return vo;
    }
}
