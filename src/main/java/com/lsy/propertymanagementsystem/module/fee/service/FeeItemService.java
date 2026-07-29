package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeItemVO;

public interface FeeItemService {
    FeeItemVO getById(Long id);
    Page<FeeItemVO> page(int pageNum, int pageSize, String itemName, Integer status);
    void add(FeeItemDTO domain);
    void update(FeeItemDTO domain);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}