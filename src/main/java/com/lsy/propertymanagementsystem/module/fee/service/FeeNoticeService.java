package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeNoticeDTO;
import com.lsy.propertymanagementsystem.module.fee.dto.FeeNoticeVO;

public interface FeeNoticeService {
    void add(FeeNoticeDTO dto);
    void update(FeeNoticeDTO dto);
    void delete(Long id);
    FeeNoticeVO getById(Long id);
    Page<FeeNoticeVO> page(int pageNum, int pageSize, Integer noticeType, Integer sendStatus);
    void publish(Long id);
}
