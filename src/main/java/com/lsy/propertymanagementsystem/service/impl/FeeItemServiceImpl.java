package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import com.lsy.propertymanagementsystem.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.service.FeeItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeeItemServiceImpl implements FeeItemService {

    @Autowired
    private FeeItemMapper feeItemMapper;

    @Override
    public void add(FeeItemRequest request) {
        FeeItem feeItem = new FeeItem();
        BeanUtils.copyProperties(request, feeItem);
        feeItemMapper.insert(feeItem);
    }

    @Override
    public void update(FeeItemRequest request) {
        FeeItem feeItem = feeItemMapper.selectById(request.getId());
        if (feeItem == null) {
            throw new BusinessException("收费项目不存在");
        }
        BeanUtils.copyProperties(request, feeItem);
        feeItemMapper.updateById(feeItem);
    }

    @Override
    public void delete(Long id) {
        feeItemMapper.deleteById(id);
    }

    @Override
    public FeeItem getById(Long id) {
        return feeItemMapper.selectById(id);
    }

    @Override
    public List<FeeItem> list() {
        return feeItemMapper.selectList(null);
    }

    @Override
    public Page<FeeItem> page(int pageNum, int pageSize) {
        return feeItemMapper.selectPage(new Page<>(pageNum, pageSize), null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        FeeItem feeItem = feeItemMapper.selectById(id);
        if (feeItem == null) {
            throw new BusinessException("收费项目不存在");
        }
        feeItem.setStatus(status);
        feeItemMapper.updateById(feeItem);
    }
}