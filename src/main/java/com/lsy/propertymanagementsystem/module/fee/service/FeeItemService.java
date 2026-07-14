package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.module.fee.entity.FeeItem;
import java.util.List;

public interface FeeItemService {
    void add(FeeItemRequest request);
    void update(FeeItemRequest request);
    void delete(Long id);
    FeeItem getById(Long id);
    List<FeeItem> list();
    Page<FeeItem> page(int pageNum, int pageSize, String itemName, Integer status);
    void updateStatus(Long id, Integer status);
}