package com.lsy.propertymanagementsystem.module.fee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeNoticeDomain;

public interface FeeNoticeService {
    void add(FeeNoticeDomain domain);
    void update(FeeNoticeDomain domain);
    void delete(Long id);
    FeeNoticeDomain getById(Long id);
    Page<FeeNoticeDomain> page(int pageNum, int pageSize, Integer noticeType, Integer sendStatus);
    void publish(Long id);
}
