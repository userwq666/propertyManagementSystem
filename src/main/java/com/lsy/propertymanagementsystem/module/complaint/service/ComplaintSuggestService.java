package com.lsy.propertymanagementsystem.module.complaint.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.complaint.entity.ComplaintSuggest;

public interface ComplaintSuggestService {
    void addComplaintSuggest(ComplaintSuggest record);
    void updateComplaintSuggest(ComplaintSuggest record);
    void deleteComplaintSuggest(Long id);
    ComplaintSuggest getById(Long id);
    Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, String type, Integer status);
    void updateStatus(Long id, Integer status, String handleUser, String handleResult);
    void updateRating(Long id, Integer rating);
}