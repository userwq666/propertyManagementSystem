package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeItemDomain;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;

public interface FeeItemService {
    FeeItemDomain getById(Long id);
    Page<FeeItemDomain> page(int pageNum, int pageSize, String itemName, Integer status);
    void add(FeeItemDTO domain);
    void update(FeeItemDTO domain);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}