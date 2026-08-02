package com.lsy.propertymanagementsystem.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.module.community.domain.CommunityOwnerDomain;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.module.community.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.module.equipment.domain.EquipmentDomain;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.module.equipment.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeExpenseDomain;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeExpenseMapper;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionRecordDomain;
import com.lsy.propertymanagementsystem.module.inspection.enums.HandleStatus;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.enums.TaskStatus;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.module.inspection.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.module.repair.domain.RepairRecordDomain;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.mapper.RepairRecordMapper;
import com.lsy.propertymanagementsystem.module.statistics.service.StatisticsService;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private FeeExpenseMapper feeExpenseMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private CommunityOwnerMapper ownerMapper;

    @Autowired
    private CommunityHouseMapper houseMapper;

    @Autowired
    private CommunityParkingMapper parkingMapper;

    @Autowired
    private ComplaintSuggestMapper complaintSuggestMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentCategoryMapper equipmentCategoryMapper;

    @Autowired
    private InspectionPlanMapper inspectionPlanMapper;

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    private boolean has(String perm) {
        return SecurityUtils.hasPermission(perm);
    }

    private long countRepairStatus(RepairStatus status) {
        return repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                .eq(RepairRecordDomain::getStatus, status));
    }

    private long countEquipmentStatus(EquipmentStatus status) {
        return equipmentMapper.selectCount(new LambdaQueryWrapper<EquipmentDomain>()
                .eq(EquipmentDomain::getStatus, status));
    }

    private BigDecimal sumFeeAmount(FeeRecordStatus status) {
        return feeRecordMapper.selectList(new LambdaQueryWrapper<FeeRecordDomain>()
                        .eq(FeeRecordDomain::getStatus, status))
                .stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        if (has("statistics:repair:list")) {
            result.put("repairTotal", repairRecordMapper.selectCount(new LambdaQueryWrapper<>()));
            result.put("repairPending", countRepairStatus(RepairStatus.PENDING));
            result.put("repairDone", countRepairStatus(RepairStatus.COMPLETED));
        }
        if (has("statistics:equipment:list")) {
            result.put("equipmentTotal", equipmentMapper.selectCount(new LambdaQueryWrapper<>()));
            result.put("equipmentFault", countEquipmentStatus(EquipmentStatus.FAULT));
        }
        if (has("statistics:user:list")) {
            result.put("userTotal", sysUserMapper.selectCount(new LambdaQueryWrapper<>()));
            result.put("ownerTotal", ownerMapper.selectCount(new LambdaQueryWrapper<>()));
        }
        if (has("statistics:fee:list")) {
            result.put("feeIncome", sumFeeAmount(FeeRecordStatus.PAID));
            result.put("feeUnpaid", sumFeeAmount(FeeRecordStatus.UNPAID));
            result.put("feeExpense", sumExpense());
        }
        if (has("statistics:complaint:list")) {
            result.put("complaintTotal", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<>()));
            result.put("complaintDone", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                    .eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.COMPLETED)));
        }
        if (has("statistics:inspection:list")) {
            result.put("inspectionPlanTotal", inspectionPlanMapper.selectCount(new LambdaQueryWrapper<>()));
            result.put("inspectionRecordTotal", inspectionRecordMapper.selectCount(new LambdaQueryWrapper<>()));
        }
        return result;
    }

    @Override
    public Map<String, Object> getRepairSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", repairRecordMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("pending", countRepairStatus(RepairStatus.PENDING));
        result.put("processing", countRepairStatus(RepairStatus.PROCESSING));
        result.put("evaluate", countRepairStatus(RepairStatus.PENDING_EVALUATE));
        result.put("done", countRepairStatus(RepairStatus.COMPLETED));
        result.put("cancelled", countRepairStatus(RepairStatus.CANCELLED));
        result.put("expense", sumExpense());
        // 类型占比
        Map<String, Long> typeRatio = repairRecordMapper.selectList(new LambdaQueryWrapper<>())
                .stream()
                .filter(r -> r.getRepairType() != null)
                .collect(Collectors.groupingBy(r -> r.getRepairType().getDesc(), Collectors.counting()));
        result.put("typeRatio", typeRatio);
        return result;
    }

    @Override
    public Map<String, Object> getEquipmentSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", equipmentMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("categoryTotal", equipmentCategoryMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("normal", countEquipmentStatus(EquipmentStatus.NORMAL));
        result.put("fault", countEquipmentStatus(EquipmentStatus.FAULT));
        result.put("underRepair", countEquipmentStatus(EquipmentStatus.UNDER_REPAIR));
        result.put("disabled", countEquipmentStatus(EquipmentStatus.DISABLED));
        result.put("scrapped", countEquipmentStatus(EquipmentStatus.SCRAPPED));
        return result;
    }

    @Override
    public Map<String, Object> getUserSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", sysUserMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("owner", countUsersByRoleKey("owner"));
        result.put("propertyAdmin", countUsersByRoleKey("property_admin"));
        result.put("repairWorker", countUsersByRoleKey("repair_worker"));
        result.put("inspector", countUsersByRoleKey("inspector"));
        result.put("finance", countUsersByRoleKey("finance"));
        result.put("ownerCount", ownerMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("houseCount", houseMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("parkingCount", parkingMapper.selectCount(new LambdaQueryWrapper<>()));
        return result;
    }

    private long countUsersByRoleKey(String roleKey) {
        SysRoleDomain role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleDomain>()
                .eq(SysRoleDomain::getRoleKey, roleKey));
        if (role == null) {
            return 0;
        }
        return sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRoleDomain>()
                .eq(SysUserRoleDomain::getRoleId, role.getId()));
    }

    @Override
    public Map<String, Object> getFeeSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("receivable", sumFeeAmount(FeeRecordStatus.UNPAID)
                .add(sumFeeAmount(FeeRecordStatus.PAID))
                .add(sumFeeAmount(FeeRecordStatus.PARTIAL_PAID)));
        result.put("income", sumFeeAmount(FeeRecordStatus.PAID));
        result.put("unpaid", sumFeeAmount(FeeRecordStatus.UNPAID));
        result.put("expense", sumExpense());
        BigDecimal balance = ((BigDecimal) result.get("income")).subtract((BigDecimal) result.get("expense"));
        result.put("balance", balance);
        // 月度实收趋势（近12月）
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();
        List<FeeRecordDomain> paid = feeRecordMapper.selectList(new LambdaQueryWrapper<FeeRecordDomain>()
                .eq(FeeRecordDomain::getStatus, FeeRecordStatus.PAID));
        for (int i = 11; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            BigDecimal amount = paid.stream()
                    .filter(r -> r.getPayTime() != null && r.getPayTime().getYear() == m.getYear()
                            && r.getPayTime().getMonthValue() == m.getMonthValue())
                    .map(FeeRecordDomain::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> item = new HashMap<>();
            item.put("month", m.getYear() + "-" + m.getMonthValue());
            item.put("amount", amount);
            trend.add(item);
        }
        result.put("monthlyTrend", trend);
        return result;
    }

    private BigDecimal sumExpense() {
        return feeExpenseMapper.selectList(new LambdaQueryWrapper<FeeExpenseDomain>()
                        .eq(FeeExpenseDomain::getAuditStatus, 1))
                .stream()
                .map(FeeExpenseDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Object> getComplaintSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<>()));
        result.put("pending", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                .eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.PENDING)));
        result.put("processing", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                .in(ComplaintSuggestDomain::getStatus, ComplaintStatus.ACCEPTED, ComplaintStatus.PROCESSING)));
        result.put("done", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                .eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.COMPLETED)));
        result.put("cancelled", complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                .eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.CANCELLED)));
        List<ComplaintSuggestDomain> evaluated = complaintSuggestMapper.selectList(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                .isNotNull(ComplaintSuggestDomain::getEvaluateScore));
        double avg = evaluated.isEmpty() ? 0.0
                : evaluated.stream().mapToInt(ComplaintSuggestDomain::getEvaluateScore).average().orElse(0.0);
        result.put("avgScore", Math.round(avg * 10) / 10.0);
        result.put("evaluatedCount", evaluated.size());
        return result;
    }

    @Override
    public Map<String, Object> getInspectionSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("planTotal", inspectionPlanMapper.selectCount(new LambdaQueryWrapper<>()));
        long recordTotal = inspectionRecordMapper.selectCount(new LambdaQueryWrapper<>());
        long abnormal = inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>()
                .eq(InspectionRecordDomain::getStatus, InspectResult.ABNORMAL));
        long normal = inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>()
                .eq(InspectionRecordDomain::getStatus, InspectResult.NORMAL));
        result.put("recordTotal", recordTotal);
        result.put("normal", normal);
        result.put("abnormal", abnormal);
        result.put("completionRate", recordTotal == 0 ? 0 : Math.round((normal + abnormal) * 100.0 / recordTotal * 10) / 10.0);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTodos() {
        List<Map<String, Object>> todos = new ArrayList<>();
        String roleKey = SecurityUtils.getRoleKey();
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if ("admin".equals(roleKey) || "property_admin".equals(roleKey)) {
            todos.add(todo("repairAssign", "待派单报修",
                    repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                            .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING))));
            todos.add(todo("repairConfirm", "待确认报修",
                    repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                            .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING_EVALUATE))));
            todos.add(todo("complaint", "待处理投诉",
                    complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                            .in(ComplaintSuggestDomain::getStatus,
                                    ComplaintStatus.PENDING, ComplaintStatus.ACCEPTED, ComplaintStatus.PROCESSING))));
            todos.add(todo("inspectionAbnormal", "巡检异常待处理",
                    inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>()
                            .eq(InspectionRecordDomain::getStatus, InspectResult.ABNORMAL)
                            .eq(InspectionRecordDomain::getHandleStatus, HandleStatus.PENDING))));
            todos.add(todo("expenseAudit", "待审核消费",
                    feeExpenseMapper.selectCount(new LambdaQueryWrapper<FeeExpenseDomain>()
                            .eq(FeeExpenseDomain::getAuditStatus, 0))));
        } else if ("finance".equals(roleKey)) {
            todos.add(todo("feePending", "待收款确认",
                    feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecordDomain>()
                            .in(FeeRecordDomain::getStatus, FeeRecordStatus.UNPAID, FeeRecordStatus.OVERDUE))));
            todos.add(todo("expenseAudit", "待审核消费",
                    feeExpenseMapper.selectCount(new LambdaQueryWrapper<FeeExpenseDomain>()
                            .eq(FeeExpenseDomain::getAuditStatus, 0))));
        } else if ("repair_worker".equals(roleKey)) {
            todos.add(todo("repairAssign", "待接单报修",
                    repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                            .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING))));
            todos.add(todo("repairProcessing", "处理中报修",
                    repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                            .eq(RepairRecordDomain::getStatus, RepairStatus.PROCESSING)
                            .eq(RepairRecordDomain::getHandlerId, currentUserId))));
        } else if ("inspector".equals(roleKey)) {
            todos.add(todo("inspectionTodo", "待巡检任务",
                    inspectionRecordMapper.selectCount(new LambdaQueryWrapper<InspectionRecordDomain>()
                            .eq(InspectionRecordDomain::getStatus, InspectResult.NOT_INSPECTED)
                            .and(w -> w.eq(InspectionRecordDomain::getInspectorUserId, currentUserId)
                                    .or().eq(InspectionRecordDomain::getTaskStatus, TaskStatus.PENDING)))));
        } else if ("owner".equals(roleKey)) {
            CommunityOwnerDomain owner = ownerMapper.selectOne(new LambdaQueryWrapper<CommunityOwnerDomain>()
                    .eq(CommunityOwnerDomain::getUserId, currentUserId));
            if (owner != null) {
                todos.add(todo("feePending", "待缴物业费",
                        feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecordDomain>()
                                .eq(FeeRecordDomain::getOwnerId, owner.getId())
                                .in(FeeRecordDomain::getStatus, FeeRecordStatus.UNPAID, FeeRecordStatus.OVERDUE))));
                todos.add(todo("repairConfirm", "报修待确认",
                        repairRecordMapper.selectCount(new LambdaQueryWrapper<RepairRecordDomain>()
                                .eq(RepairRecordDomain::getOwnerId, owner.getId())
                                .eq(RepairRecordDomain::getStatus, RepairStatus.PENDING_EVALUATE))));
            }
            todos.add(todo("complaintConfirm", "投诉待确认",
                    complaintSuggestMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestDomain>()
                            .eq(ComplaintSuggestDomain::getCreatorId, currentUserId)
                            .eq(ComplaintSuggestDomain::getStatus, ComplaintStatus.REPLIED))));
        }
        return todos;
    }

    private Map<String, Object> todo(String key, String name, long count) {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("name", name);
        item.put("count", count);
        return item;
    }
}
