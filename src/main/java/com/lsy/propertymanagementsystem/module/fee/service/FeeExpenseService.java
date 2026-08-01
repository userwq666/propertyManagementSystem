package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeExpenseVO;

public interface FeeExpenseService {
    Page<FeeExpenseVO> page(int pageNum, int pageSize, String expenseName, Integer expenseType);
    void add(FeeExpenseDTO dto);
    void update(FeeExpenseDTO dto);
    void delete(Long id);
    void audit(Long id, Integer status);
}
