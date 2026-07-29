package com.lsy.propertymanagementsystem.module.complaint.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestDTO;
import com.lsy.propertymanagementsystem.module.complaint.dto.ComplaintSuggestVO;

public interface ComplaintSuggestService {
    // 新增投诉建议
    void add(ComplaintSuggestDTO domain);
    // 更新投诉建议
    void update(ComplaintSuggestDTO domain);
    // 删除投诉建议
    void delete(Long id);
    // 分页查询投诉建议
    Page<ComplaintSuggestVO> page(int pageNum, int pageSize, Long ownerId, String type, Integer status);
    // 更新投诉建议状态
    void updateStatus(Long id, Integer status, Long handlerId, String handleContent);
}