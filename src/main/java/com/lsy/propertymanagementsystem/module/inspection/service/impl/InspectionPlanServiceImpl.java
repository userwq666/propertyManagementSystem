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
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanEquipmentMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanInspectorMapper;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionPlanService;
import com.lsy.propertymanagementsystem.module.inspection.service.InspectionRecordService;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    public Page<InspectionPlanVO> page(int pageNum, int pageSize, String planName, Integer status) {
        LambdaQueryWrapper<InspectionPlanDomain> wrapper = new LambdaQueryWrapper<>();
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(InspectionPlanDomain::getPlanName, planName);
        }
        if (status != null) {
            wrapper.eq(InspectionPlanDomain::getStatus, EnableStatus.of(status));
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
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        if (domain.getStatus() == null) {
            domain.setStatus(EnableStatus.ENABLED);
        }
        this.save(domain);
    }

    @Override
    @Transactional
    public void updatePlan(InspectionPlanDTO dto) {
        InspectionPlanDomain domain = new InspectionPlanDomain();
        BeanUtils.copyProperties(dto, domain);
        InspectionPlanDomain existing = this.getById(domain.getId());
        if (existing == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("巡检计划不存在");
        }
        this.updateById(domain);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        if (inspectionRecordService.countByPlanId(id) > 0) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("该计划存在关联的巡检记录，不允许删除");
        }
        planEquipmentMapper.delete(new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, id));
        planInspectorMapper.delete(new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, id));
        this.removeById(id);
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
        plan.changeStatus(EnableStatus.of(status));
        this.updateById(plan);
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

    private LocalDate calculateNextDate(InspectionPlanDomain plan, LocalDate today) {
        LocalDate date = plan.getStartDate();
        if (date == null) return null;

        String freqValue = plan.getFrequencyValue();
        int cycle = freqValue != null ? Integer.parseInt(freqValue) : 1;

        while (!date.isAfter(today)) {
            FrequencyType freqType = plan.getFrequencyType();
            if (freqType == FrequencyType.DAILY) {
                date = date.plusDays(cycle);
            } else if (freqType == FrequencyType.WEEKLY) {
                date = date.plusWeeks(cycle);
            } else if (freqType == FrequencyType.MONTHLY) {
                date = date.plusMonths(cycle);
            } else {
                return date;
            }
        }
        return date;
    }

    private void createInspectionRecords(InspectionPlanDomain plan) {
        List<InspectionPlanEquipmentDomain> equipments = planEquipmentMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanEquipmentDomain>().eq(InspectionPlanEquipmentDomain::getPlanId, plan.getId()));
        List<InspectionPlanInspectorDomain> inspectors = planInspectorMapper.selectList(
            new LambdaQueryWrapper<InspectionPlanInspectorDomain>().eq(InspectionPlanInspectorDomain::getPlanId, plan.getId()));

        if (equipments.isEmpty() || inspectors.isEmpty()) return;

        for (int i = 0; i < equipments.size(); i++) {
            InspectionRecordDomain record = new InspectionRecordDomain();
            record.setPlanId(plan.getId());
            record.setEquipmentId(equipments.get(i).getEquipmentId());
            record.setInspectorUserId(inspectors.get(i % inspectors.size()).getInspectorId());
            record.setInspectionTime(LocalDateTime.now());
            record.startInspect();
            inspectionRecordService.addRecordDomain(record);
        }
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<InspectionPlanDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionPlanDomain::getPlanType, 1);
        wrapper.ne(InspectionPlanDomain::getStatus, EnableStatus.DISABLED);
        List<InspectionPlanDomain> plans = this.list(wrapper);

        for (InspectionPlanDomain plan : plans) {
            LocalDate nextDate = calculateNextDate(plan, today);
            if (nextDate != null && nextDate.isAfter(today)) {
                plan.setStartDate(nextDate);
                this.updateById(plan);
                continue;
            }
            createInspectionRecords(plan);
            plan.setStartDate(calculateNextDate(plan, today));
            this.updateById(plan);
        }
    }
}
