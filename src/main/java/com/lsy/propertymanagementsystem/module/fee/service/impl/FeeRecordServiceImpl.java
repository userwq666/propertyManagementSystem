package com.lsy.propertymanagementsystem.module.fee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeRecordDTO;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.enums.PayType;
import com.lsy.propertymanagementsystem.module.fee.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.module.fee.service.FeeItemService;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.fee.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeRecordServiceImpl implements FeeRecordService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Autowired
    private FeeItemService feeItemService;

    @Override
    @Transactional
    public void generateBills(List<FeeRecordDTO> domains) {
        for (FeeRecordDTO dto : domains) {
            if (feeItemService.getById(dto.getItemId()) == null) {
                throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("收费项目不存在，itemId=" + dto.getItemId());
            }
            FeeRecordDomain domain = new FeeRecordDomain();
            BeanUtils.copyProperties(dto, domain);
            feeRecordMapper.insert(domain);
        }
    }

    @Override
    public FeeRecordDomain getById(Long id) {
        return feeRecordMapper.selectById(id);
    }

    @Override
    public Page<FeeRecordDomain> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status) {
        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(FeeRecordDomain::getOwnerId, SecurityUtils.isOwner() ? SecurityUtils.getCurrentUserId() : ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecordDomain::getHouseId, houseId);
        }
        if (status != null) {
            wrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.of(status));
        }
        wrapper.orderByDesc(FeeRecordDomain::getCreateTime);
        return feeRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void confirmPay(Long id, String payWay) {
        FeeRecordDomain domain = feeRecordMapper.selectById(id);
        if (domain == null) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("账单不存在");
        }
        if (domain.getStatus() == FeeRecordStatus.PAID) {
            throw new com.lsy.propertymanagementsystem.common.exception.BusinessException("账单已支付");
        }
        Integer payType = 1;
        if (payWay != null) {
            try { payType = Integer.parseInt(payWay); } catch (NumberFormatException ignored) {}
        }
        domain.confirmPay(PayType.of(payType));
        feeRecordMapper.updateById(domain);
    }

    @Override
    public Map<String, Object> getStatistics(Long ownerId, Long houseId) {
        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FeeRecordDomain::getStatus, FeeRecordStatus.UNPAID, FeeRecordStatus.OVERDUE);
        if (ownerId != null) {
            wrapper.eq(FeeRecordDomain::getOwnerId, SecurityUtils.isOwner() ? SecurityUtils.getCurrentUserId() : ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecordDomain::getHouseId, houseId);
        }
        List<FeeRecordDomain> arrearsList = feeRecordMapper.selectList(wrapper);

        BigDecimal totalArrears = arrearsList.stream()
                .map(FeeRecordDomain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("arrearsList", arrearsList);
        result.put("totalArrears", totalArrears);
        result.put("count", arrearsList.size());
        return result;
    }

    @Override
    public long countByOwnerId(Long ownerId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecordDomain>().eq(FeeRecordDomain::getOwnerId, ownerId));
    }

    @Override
    public long countByHouseId(Long houseId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecordDomain>().eq(FeeRecordDomain::getHouseId, houseId));
    }

    @Override
    public long countByItemId(Long itemId) {
        return feeRecordMapper.selectCount(new LambdaQueryWrapper<FeeRecordDomain>().eq(FeeRecordDomain::getItemId, itemId));
    }

    @Override
    @Transactional
    public void markOverdue() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<FeeRecordDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecordDomain::getStatus, FeeRecordStatus.UNPAID);
        wrapper.lt(FeeRecordDomain::getEndDate, today);
        List<FeeRecordDomain> overdueRecords = feeRecordMapper.selectList(wrapper);

        for (FeeRecordDomain record : overdueRecords) {
            record.markOverdue();
            feeRecordMapper.updateById(record);
        }
    }
}
