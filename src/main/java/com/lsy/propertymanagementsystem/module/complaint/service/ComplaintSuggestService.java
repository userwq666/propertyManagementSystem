package com.lsy.propertymanagementsystem.module.complaint.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.complaint.domain.ComplaintSuggestDomain;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;

public interface ComplaintSuggestService {
    void add(ComplaintSuggestDTO domain);
    void update(ComplaintSuggestDTO domain);
    void delete(Long id);
    Page<ComplaintSuggestDomain> page(int pageNum, int pageSize, Long ownerId, String type, Integer status);
    void updateStatus(Long id, Integer status, Long handlerId, String handleContent);
}