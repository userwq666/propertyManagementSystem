package com.lsy.propertymanagementsystem.module.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanEquipmentDomain;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanInspectorDomain;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanDTO;
import com.lsy.propertymanagementsystem.module.inspection.dto.InspectionPlanVO;
import com.lsy.propertymanagementsystem.module.inspection.enums.FrequencyType;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectionPlanType;
import com.lsy.propertymanagementsystem.module.inspection.enums.PlanStatus;
import com.lsy.propertymanagementsystem.module.inspection.enums.TaskStatus;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanEquipmentMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanInspectorMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.websocket.MessagePushService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InspectionPlanServiceImpl extends ServiceImpl<InspectionPlanMapper, InspectionPlanDomain> implements InspectionPlanService {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @Autowired
    private InspectionPlanEquipmentMapper planEquipmentMapper;

    @Autowired
    private InspectionPlanInspectorMapper planInspectorMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private MessagePushService messagePushService;

    @Override
    public List<Map<String, Object>> listInspectors() {
        SysRoleDomain inspectorRole = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRoleDomain>().eq(SysRoleDomain::getRoleKey, "inspector"));
        if (inspectorRole == null) return Collections.emptyList();
        List<Long> userIds = sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRoleDomain>().eq(SysUserRoleDomain::getRoleId, inspectorRole.getId()))
                .stream()
                .map(SysUserRoleDomain::getUserId)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) return Collections.emptyList();
        return sysUserMapper.selectBatchIds(userIds).stream().map(user -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("realName", user.getRealName());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<InspectionPlanVO> page(int pageNum, int pageSize, String planName, Integer status, Long equipmentId) {
        LambdaQueryWrapper<InspectionPlanDomain> wrapper = new LambdaQueryWrapper<>();
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(InspectionPlanDomain::getPlanName, planName);
        }
        if (status != null) {
            wrapper.eq(InspectionPlanDomain::getStatus, PlanStatus.of(status));
        }
        if (equipmentId != null) {
            List<Long> planIds = planEquipmentMapper.selectList(
                            new LambdaQueryWrapper<InspectionPlanEquipmentDomain>()
                                    .eq(InspectionPlanEquipmentDomain::getEquipmentId, equipmentId))
                    .stream()
                    .map(InspectionPlanEquipmentDomain::getPlanId)
                    .collect(Collectors.toList());
            if (planIds.isEmpty()) {
                wrapper.eq(InspectionPlanDomain::getId, -1);
            } else {
                wrapper.in(InspectionPlanDomain::getId, planIds);
            }
        }
        wrapper.orderByDesc(InspectionPlanDomain::getCreateTime);
        Page<InspectionPlanDomain> domainPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        List<InspectionPlanDomain> plans = domainPage.getRecords();
        Page<InspectionPlanVO> voPage = new Page<>(pageNum, pageSize, domainPage.getTotal());
        if (plans.isEmpty()) {
            return voPage;
        }

        List<Long> planIds = plans.stream().map(InspectionPlanDomain::getId).collect(Collectors.toList());

        Set<Long> creatorIds = plans.stream().map(InspectionPlanDomain::getCreatorId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> creatorNameMap = new HashMap<>();
        if (!creatorIds.isEmpty()) {
            List<SysUserDomain> creators = sysUserMapper.selectBatchIds(creatorIds);
            for (SysUserDomain user : creators) {
                creatorNameMap.put(user.getId(), user.getRealName());
            }
        }

        List<InspectionPlanEquipmentDomain> allEquipments = planEquipmentMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().in(InspectionPlanEquipmentDomain::getPlanId, planIds));
        Map<Long, List<Long>> equipmentIdMap = new HashMap<>();
        Map<Long, List<String>> equipmentNameMap = new HashMap<>();
        if (!allEquipments.isEmpty()) {
            Set<Long> eqIds = allEquipments.stream().map(InspectionPlanEquipmentDomain::getEquipmentId).collect(Collectors.toSet());
            Map<Long, String> eqNameMap = new HashMap<>();
            if (!eqIds.isEmpty()) {
                List<EquipmentDomain> equipments = equipmentMapper.selectBatchIds(eqIds);
                for (EquipmentDomain eq : equipments) {
                    eqNameMap.put(eq.getId(), eq.getEquipmentName());
                }
            }
            for (InspectionPlanEquipmentDomain pe : allEquipments) {
                equipmentIdMap.computeIfAbsent(pe.getPlanId(), k -> new ArrayList<>()).add(pe.getEquipmentId());
                equipmentNameMap.computeIfAbsent(pe.getPlanId(), k -> new ArrayList<>()).add(eqNameMap.get(pe.getEquipmentId()));
            }
        }

        List<InspectionPlanInspectorDomain> allInspectors = planInspectorMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanInspectorDomain>().in(InspectionPlanInspectorDomain::getPlanId, planIds));
        Map<Long, List<Long>> inspectorIdMap = new HashMap<>();
        Map<Long, List<String>> inspectorNameMap = new HashMap<>();
        if (!allInspectors.isEmpty()) {
            Set<Long> inspIds = allInspectors.stream().map(InspectionPlanInspectorDomain::getInspectorId).collect(Collectors.toSet());
            Map<Long, String> inspNameMap = new HashMap<>();
            if (!inspIds.isEmpty()) {
                List<SysUserDomain> inspectors = sysUserMapper.selectBatchIds(inspIds);
                for (SysUserDomain user : inspectors) {
                    inspNameMap.put(user.getId(), user.getRealName());
                }
            }
            for (InspectionPlanInspectorDomain pi : allInspectors) {
                inspectorIdMap.computeIfAbsent(pi.getPlanId(), k -> new ArrayList<>()).add(pi.getInspectorId());
                inspectorNameMap.computeIfAbsent(pi.getPlanId(), k -> new ArrayList<>()).add(inspNameMap.get(pi.getInspectorId()));
            }
        }

        List<InspectionPlanVO> voList = plans.stream().map(plan -> {
            InspectionPlanVO vo = new InspectionPlanVO();
            BeanUtils.copyProperties(plan, vo);
            vo.setCreatorName(creatorNameMap.get(plan.getCreatorId()));
            vo.setEquipmentIds(equipmentIdMap.getOrDefault(plan.getId(), Collections.emptyList()));
            vo.setEquipmentNames(equipmentNameMap.getOrDefault(plan.getId(), Collections.emptyList()));
            vo.setInspectorIds(inspectorIdMap.getOrDefault(plan.getId(), Collections.emptyList()));
            vo.setInspectorNames(inspectorNameMap.getOrDefault(plan.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void addPlan(InspectionPlanDTO dto) {
        if (dto.getInspectorIds() == null || dto.getInspectorIds().isEmpty()) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("请指定巡检员");
        }
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        if (dto.getPlanType() != null) {
            domain.setPlanType(InspectionPlanType.of(dto.getPlanType()));
        }
        if (dto.getFrequencyType() != null) {
            domain.setFrequencyType(FrequencyType.of(dto.getFrequencyType()));
        }
        domain.setStatus(PlanStatus.ENABLED);
        domain.setCreatorId(SecurityUtils.getCurrentUserId());
        this.save(domain);
        savePlanRelations(domain.getId(), dto.getEquipmentIds(), dto.getInspectorIds());
        for (Long inspectorId : dto.getInspectorIds()) {
            messagePushService.pushToUser(inspectorId, "inspection", "新巡检计划",
                    "巡检计划「" + domain.getPlanName() + "」已创建，请查看", domain.getId());
        }
    }

    @Override
    @Transactional
    public void updatePlan(InspectionPlanDTO dto) {
        if (dto.getInspectorIds() == null || dto.getInspectorIds().isEmpty()) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("请指定巡检员");
        }
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        InspectionPlanDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        existing.setPlanName(domain.getPlanName());
        existing.setPlanType(dto.getPlanType() != null ? InspectionPlanType.of(dto.getPlanType()) : existing.getPlanType());
        existing.setFrequencyType(dto.getFrequencyType() != null ? FrequencyType.of(dto.getFrequencyType()) : existing.getFrequencyType());
        existing.setFrequencyValue(domain.getFrequencyValue());
        existing.setStartDate(domain.getStartDate());
        existing.setEndDate(domain.getEndDate());
        existing.setStartTime(domain.getStartTime());
        existing.setEndTime(domain.getEndTime());
        existing.setRemark(domain.getRemark());
        this.updateById(existing);
        savePlanRelations(domain.getId(), dto.getEquipmentIds(), dto.getInspectorIds());
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        InspectionPlanDomain plan = this.getById(id);
        if (plan == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        if (plan.getStatus() != PlanStatus.DISABLED) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("只有停用状态的巡检计划才能删除");
        }
        this.removeById(id);
    }

    @Override
    public List<InspectionPlanVO> listByEquipmentIncludeDeleted(Long equipmentId) {
        List<Long> planIds = planEquipmentMapper.selectList(
                        new LambdaQueryWrapper<InspectionPlanEquipmentDomain>()
                                .eq(InspectionPlanEquipmentDomain::getEquipmentId, equipmentId))
                .stream()
                .map(InspectionPlanEquipmentDomain::getPlanId)
                .collect(Collectors.toList());
        if (planIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectByIdsIgnoreDeleted(planIds)
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public InspectionPlanVO getPlanById(Long id) {
        InspectionPlanDomain plan = this.getById(id);
        if (plan == null) {
            return null;
        }
        return convertToVO(plan);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        InspectionPlanDomain plan = this.getById(id);
        if (plan == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        plan.changeStatus(PlanStatus.of(status));
        this.updateById(plan);
        List<Long> inspectorIds = planInspectorMapper.selectList(
                        new LambdaQueryWrapper<InspectionPlanInspectorDomain>()
                                .eq(InspectionPlanInspectorDomain::getPlanId, id))
                .stream()
                .map(InspectionPlanInspectorDomain::getInspectorId)
                .collect(Collectors.toList());
        String statusName = PlanStatus.of(status) == PlanStatus.ENABLED ? "启用" : "停用";
        for (Long inspectorId : inspectorIds) {
            messagePushService.pushToUser(inspectorId, "inspection", "巡检计划已" + statusName,
                    "巡检计划「" + plan.getPlanName() + "」已" + statusName, plan.getId());
        }
    }

    private InspectionPlanVO convertToVO(InspectionPlanDomain plan) {
        InspectionPlanVO vo = new InspectionPlanVO();
        BeanUtils.copyProperties(plan, vo);

        if (plan.getCreatorId() != null) {
            SysUserDomain creator = sysUserMapper.selectById(plan.getCreatorId());
            if (creator != null) {
                vo.setCreatorName(creator.getRealName());
            }
        }

        List<InspectionPlanEquipmentDomain> equipments = planEquipmentMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, plan.getId()));
        if (!equipments.isEmpty()) {
            List<Long> eqIds = equipments.stream().map(InspectionPlanEquipmentDomain::getEquipmentId).collect(Collectors.toList());
            vo.setEquipmentIds(eqIds);
            if (!eqIds.isEmpty()) {
                List<EquipmentDomain> eqList = equipmentMapper.selectBatchIds(eqIds);
                Map<Long, String> eqNameMap = eqList.stream().collect(Collectors.toMap(EquipmentDomain::getId, EquipmentDomain::getEquipmentName));
                List<String> eqNames = eqIds.stream().map(eqNameMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                vo.setEquipmentNames(eqNames);
            }
        }

        List<InspectionPlanInspectorDomain> inspectors = planInspectorMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, plan.getId()));
        if (!inspectors.isEmpty()) {
            List<Long> inspIds = inspectors.stream().map(InspectionPlanInspectorDomain::getInspectorId).collect(Collectors.toList());
            vo.setInspectorIds(inspIds);
            if (!inspIds.isEmpty()) {
                List<SysUserDomain> userList = sysUserMapper.selectBatchIds(inspIds);
                Map<Long, String> userNameMap = userList.stream().collect(Collectors.toMap(SysUserDomain::getId, SysUserDomain::getRealName));
                List<String> inspNames = inspIds.stream().map(userNameMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                vo.setInspectorNames(inspNames);
            }
        }

        return vo;
    }

    private void savePlanRelations(Long planId, List<Long> equipmentIds, List<Long> inspectorIds) {
        planEquipmentMapper.delete(new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, planId));
        planInspectorMapper.delete(new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, planId));
        if (equipmentIds != null) {
            for (Long equipmentId : equipmentIds) {
                InspectionPlanEquipmentDomain pe = new InspectionPlanEquipmentDomain();
                pe.setPlanId(planId);
                pe.setEquipmentId(equipmentId);
                planEquipmentMapper.insert(pe);
            }
        }
        if (inspectorIds != null) {
            for (Long inspectorId : inspectorIds) {
                InspectionPlanInspectorDomain pi = new InspectionPlanInspectorDomain();
                pi.setPlanId(planId);
                pi.setInspectorId(inspectorId);
                planInspectorMapper.insert(pi);
            }
        }
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LocalDate today = LocalDate.now();
        List<InspectionPlanDomain> plans = this.list(new LambdaQueryWrapper<InspectionPlanDomain>()
                .eq(InspectionPlanDomain::getStatus, PlanStatus.ENABLED)
                .and(w -> w.isNull(InspectionPlanDomain::getEndDate).or().ge(InspectionPlanDomain::getEndDate, today)));

        for (InspectionPlanDomain plan : plans) {
            if (!shouldGenerateToday(plan, today)) continue;
            if (inspectionRecordService.countByPlanAndDate(plan.getId(), today) > 0) continue;
            createInspectionRecords(plan);
            if (plan.getFrequencyType() == FrequencyType.ONCE) {
                plan.changeStatus(PlanStatus.DISABLED);
                this.updateById(plan);
                List<InspectionPlanInspectorDomain> inspectors = planInspectorMapper.selectList(
                        new LambdaQueryWrapper<InspectionPlanInspectorDomain>()
                                .eq(InspectionPlanInspectorDomain::getPlanId, plan.getId()));
                for (InspectionPlanInspectorDomain inspector : inspectors) {
                    messagePushService.pushToUser(inspector.getInspectorId(), "inspection", "巡检计划已完成",
                            "巡检计划「" + plan.getPlanName() + "」已完成", plan.getId());
                }
            }
        }
    }

    private boolean shouldGenerateToday(InspectionPlanDomain plan, LocalDate today) {
        if (plan.getStartDate() != null && today.isBefore(plan.getStartDate())) return false;
        FrequencyType type = plan.getFrequencyType();
        if (type == null) return false;
        if (type == FrequencyType.DAILY) return true;
        if (type == FrequencyType.ONCE) return true;
        if (type == FrequencyType.WEEKLY) {
            String value = plan.getFrequencyValue();
            if (value == null) return false;
            return Arrays.stream(value.split(","))
                    .anyMatch(v -> String.valueOf(today.getDayOfWeek().getValue()).equals(v.trim()));
        }
        if (type == FrequencyType.MONTHLY) {
            String value = plan.getFrequencyValue();
            if (value == null) return false;
            return String.valueOf(today.getDayOfMonth()).equals(value.trim());
        }
        LocalDate start = plan.getStartDate();
        if (start == null) return false;
        int months = switch (type) {
            case QUARTERLY -> 3;
            case HALF_YEAR -> 6;
            case YEARLY -> 12;
            default -> 0;
        };
        return months > 0 && today.getDayOfMonth() == start.getDayOfMonth()
                && ChronoUnit.MONTHS.between(start, today) % months == 0;
    }

    private void createInspectionRecords(InspectionPlanDomain plan) {
        List<InspectionPlanEquipmentDomain> equipments = planEquipmentMapper.selectList(
                new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, plan.getId()));
        List<InspectionPlanInspectorDomain> inspectors = planInspectorMapper.selectList(
                new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, plan.getId()));
        if (equipments.isEmpty()) return;

        for (int i = 0; i < equipments.size(); i++) {
            InspectionRecordDomain record = new InspectionRecordDomain();
            record.setPlanId(plan.getId());
            record.setEquipmentId(equipments.get(i).getEquipmentId());
            if (!inspectors.isEmpty()) {
                record.setInspectorUserId(inspectors.get(i % inspectors.size()).getInspectorId());
                record.setTaskStatus(TaskStatus.ACCEPTED);
            } else {
                record.setTaskStatus(TaskStatus.PENDING);
            }
            record.setInspectionTime(LocalDateTime.now());
            record.setStatus(InspectResult.NOT_INSPECTED);
            inspectionRecordService.addRecordDomain(record);
        }
        for (InspectionPlanInspectorDomain inspector : inspectors) {
            messagePushService.pushToUser(inspector.getInspectorId(), "inspection", "新巡检任务",
                    "巡检计划「" + plan.getPlanName() + "」今日有巡检任务待完成", plan.getId());
        }
    }
}
