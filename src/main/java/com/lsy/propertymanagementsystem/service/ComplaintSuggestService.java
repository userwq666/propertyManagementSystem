package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;

public interface ComplaintSuggestService {
    void add(ComplaintSuggestRequest request);
    void update(ComplaintSuggestRequest request);
    void delete(Long id);
    ComplaintSuggest getById(Long id);
    Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status, String type);
    void updateStatus(Long id, Integer status, String handleUser, String handleResult);
    void updateRating(Long id, Integer rating);
}